package com.modulus.uno

import grails.transaction.Transactional
import java.math.RoundingMode

@Transactional
class CommissionTransactionService {

  def grailsApplication
  CollaboratorService collaboratorService

  def saveCommissionTransaction (FeeCommand feeCommand) {
    CommissionTransaction commissionTransaction = feeCommand.createCommissionTransaction()
    commissionTransaction.save()
    commissionTransaction
  }

  def getCommissionsBalanceInPeriodForCompanyAndStatus(Company company, CommissionTransactionStatus status, Period period) {
    List balances = []
    BigDecimal iva = new BigDecimal(grailsApplication.config.iva)
    company.commissions.sort{it.type}.each {
      Map balance = [typeCommission:it.type, balance: getCommissionsBalanceInPeriodForTypeAndCompanyAndStatus(it.type, company, status, period) ?: 0]
      balance.iva = balance.balance * (iva/100)
      balance.total = balance.balance + balance.iva
      balances.add(balance)
    }
    balances
  }

  BigDecimal getCommissionsBalanceInPeriodForTypeAndCompanyAndStatus(CommissionType type, Company company, CommissionTransactionStatus status, Period period) {
    BigDecimal total = CommissionTransaction.createCriteria().get {
      and {
        eq("company", company)
        eq("type", type)
        eq("status", status)
        between("dateCreated", period.init, period.end)
      }
      projections {
        sum "amount"
      }
    }
    total
  }

  BigDecimal getTotalCommissionsPendingForCompany(Company company, Period period) {
    List balances = getCommissionsBalanceInPeriodForCompanyAndStatus(company, CommissionTransactionStatus.PENDING, period)
    balances.balance.sum()
  }

  BigDecimal getTotalInvoicedCommissionsForCompany(Company company, Period period) {
    List balances = getCommissionsBalanceInPeriodForCompanyAndStatus(company, CommissionTransactionStatus.INVOICED, period)
    balances.balance.sum()
  }

  CommissionTransaction registerCommissionForSaleOrder(SaleOrder order) {
    FeeCommand feeCommand = createFeeCommandForSaleOrder(order)
    def commission = saveCommissionTransaction(feeCommand)
    commission
  }

  private FeeCommand createFeeCommandForSaleOrder(SaleOrder saleOrder) {
    def command = null
    Commission commission = saleOrder.company.commissions.find { com ->
        com.type == CommissionType."FACTURA"
    }

    if (!commission) {
      throw new BusinessException("No existe comisión de facturación registrada")
    }

    BigDecimal amountFee = 0
    if (commission){
      if (commission.fee){
        amountFee = commission.fee * 1.0
      } else {
        amountFee = saleOrder.total * (commission.percentage/100)
      }
      command = new FeeCommand(companyId:saleOrder.company.id, amount:amountFee.setScale(2, RoundingMode.HALF_UP),type:commission.type)
    }
    command
  }

  Map getFixedCommissionsForCompany(Company company, def params) {
    List<CommissionTransaction> listFixedCommissions = CommissionTransaction.findAllByCompanyAndType(company, CommissionType.FIJA, params)
    [listFixedCommissions:listFixedCommissions, countFixedCommissions:CommissionTransaction.countByCompanyAndType(company, CommissionType.FIJA)]
  }

  CommissionTransaction applyFixedCommissionToCompany(Company company) {
    log.info "Applying fixed commission to company ${company}"
    FeeCommand feeCommand = createFeeCommandForFixedCommissionOfCompany(company)
    def commission = saveCommissionTransaction(feeCommand)
    commission
  }

  private FeeCommand createFeeCommandForFixedCommissionOfCompany(Company company) {
     Commission commission = company.commissions.find { com ->
        com.type == CommissionType."FIJA"
    }

    if (!commission) {
      throw new BusinessException("No existe comisión fija registrada para la empresa")
    }

    new FeeCommand(companyId:company.id, amount: commission.fee.setScale(2, RoundingMode.HALF_UP), type:commission.type)
  }

  Boolean companyHasFixedCommissionAppliedInCurrentMonth(Company company) {
    Period period = collaboratorService.getCurrentMonthPeriod()
    CommissionTransaction exists = CommissionTransaction.findByTypeAndCompanyAndDateCreatedBetween(CommissionType.FIJA, company, period.init, period.end)
    exists ? true : false
  }

}

