package com.modulus.uno

import grails.transaction.Transactional
import java.math.RoundingMode

@Transactional
class ConciliationService {

  def springSecurityService
  def saleOrderService
  def paymentService
  def movimientosBancariosService

  def getTotalToApplyForPayment(Payment payment) {
    def conciliations = getConciliationsToApplyForPayment(payment)
    BigDecimal applied = conciliations ? conciliations*.amount.sum() : new BigDecimal(0)
    payment.amount - applied
  }

  def getTotalToApplyForBankingTransaction(MovimientosBancarios bankingTransaction) {
    def conciliations = getConciliationsToApplyForBankingTransaction(bankingTransaction)
    BigDecimal applied = conciliations ? conciliations*.amount.sum() : new BigDecimal(0)
    bankingTransaction.amount - applied
  }

  List<Conciliation> getConciliationsToApplyForPayment(Payment payment) {
    Conciliation.findAllByPaymentAndStatus(payment, ConciliationStatus.TO_APPLY)
  }

  List<Conciliation> getConciliationsToApplyForBankingTransaction(MovimientosBancarios bankingTransaction) {
    Conciliation.findAllByBankingTransactionAndStatus(bankingTransaction, ConciliationStatus.TO_APPLY)
  }

  void saveConciliationForCompany(Conciliation conciliation, Company company) {
    if (!validateAmountToApply(conciliation)) {
      throw new BusinessException("El monto a conciliar (${conciliation.amount.setScale(2, RoundingMode.HALF_UP)}) no puede ser mayor al monto por pagar de la factura (${conciliation.saleOrder.amountToPay.setScale(2, RoundingMode.HALF_UP)})")
    }

    conciliation.company = company
    conciliation.user = springSecurityService.currentUser
    log.info "Saving conciliation: ${conciliation.dump()}"
    conciliation.save()
  }

  private validateAmountToApply(Conciliation conciliation) {
    BigDecimal amountToConciliate = conciliation.amount
    if (conciliation.saleOrder.currency=="USD") {
      amountToConciliate = conciliation.amount/conciliation.changeType
    }
    amountToConciliate <= conciliation.saleOrder.amountToPay
  }

  void deleteConciliation(Conciliation conciliation) {
    conciliation.delete()
  }

  void cancelConciliationsForPayment(Payment payment) {
    List<Conciliation> conciliations = getConciliationsToApplyForPayment(payment)
    conciliations.each {
      deleteConciliation(it)
    }
  }

  void cancelConciliationsForBankingTransaction(MovimientosBancarios bankingTransaction) {
    List<Conciliation> conciliations = getConciliationsToApplyForBankingTransaction(bankingTransaction)
    conciliations.each {
      deleteConciliation(it)
    }
  }

  List<Conciliation> getConciliationsAppliedForPayment(Payment payment) {
    Conciliation.findAllByPaymentAndStatus(payment, ConciliationStatus.APPLIED)
  }

  void applyConciliationsForPayment(Payment payment) {
    List<Conciliation> conciliations = getConciliationsToApplyForPayment(payment)
    conciliations.each { conciliation ->
      applyConciliation(conciliation)
    }
    paymentService.conciliatePayment(payment)
  }

  void applyConciliationsForBankingTransaction(MovimientosBancarios bankingTransaction) {
    List<Conciliation> conciliations = getConciliationsToApplyForBankingTransaction(bankingTransaction)
    conciliations.each { conciliation ->
      applyConciliation(conciliation)
    }
    movimientosBancariosService.conciliateBankingTransaction(bankingTransaction)
  }

  private applyConciliation(Conciliation conciliation) {
    saleOrderService.addPaymentToSaleOrder(conciliation.saleOrder, conciliation.amount, conciliation.changeType)
    conciliation.status = ConciliationStatus.APPLIED
    conciliation.save()
  }

  void applyConciliationWithoutInvoice(Conciliation conciliation) {
    conciliation.company = conciliation.payment.company
    conciliation.user = springSecurityService.currentUser
    conciliation.status = ConciliationStatus.APPLIED
    log.info "Saving applied conciliation: ${conciliation.dump()}"
    conciliation.save()
    paymentService.conciliatePayment(conciliation.payment)
  }

  void applyConciliationWithoutPayment(Conciliation conciliation) {
    saleOrderService.addPaymentToSaleOrder(conciliation.saleOrder, conciliation.amount, conciliation.changeType)
    conciliation.company = conciliation.saleOrder.company
    conciliation.user = springSecurityService.currentUser
    conciliation.status = ConciliationStatus.APPLIED
    log.info "Saving applied conciliation: ${conciliation.dump()}"
    conciliation.save()
  }

}
