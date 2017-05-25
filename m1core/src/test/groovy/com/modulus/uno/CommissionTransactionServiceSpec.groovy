package com.modulus.uno

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import grails.test.mixin.Mock

@TestFor(CommissionTransactionService)
@Mock([CommissionTransaction, Company, Transaction, SaleOrder, SaleOrderItem, Commission])
class CommissionTransactionServiceSpec extends Specification {

  GrailsApplicationMock grailsApplication = new GrailsApplicationMock()
  CollaboratorService collaboratorService = Mock(CollaboratorService)

  def setup(){
    service.grailsApplication = grailsApplication
    service.collaboratorService = collaboratorService
  }

  void "Should save a commission transaction"() {
    given:"A fee command"
      FeeCommand feeCommand = new FeeCommand(companyId:"1", amount:new BigDecimal(10), type:"PAGO", transactionId:"1")
    and:
      Company company = new Company().save(validate:false)
      Transaction transaction = new Transaction().save(validate:false)
    when:"We save the commission"
      def commission = service.saveCommissionTransaction(feeCommand)
    then:
      commission.id
  }

  void "Should obtain the commissions pending balance for company"() {
    given:"A company"
      Company company = new Company().save(validate:false)
      Commission commission1 = new Commission(fee:5, percentage:0, type:CommissionType.DEPOSITO).save(validate:false)
      Commission commission2 = new Commission(fee:10, percentage:0, type:CommissionType.PAGO).save(validate:false)
      company.addToCommissions(commission1)
      company.addToCommissions(commission2)
      company.save(validate:false)
    when:
      def balance = service.getCommissionsBalanceForCompanyAndStatus(company, CommissionTransactionStatus.PENDING)
    then:
      balance.size() == company.commissions.size()
  }

  @Unroll
  void "Should save a invoice commission transaction with amount=#amountExpected when commission fee=#fee and percentage=#percentage and sale order total=#price for a company"() {
    given:"A company"
      Company company = new Company(rfc:"XXX010101XXX").save(validate:false)
    and:"The invoice commission"
      Commission commission = new Commission(fee:fee, percentage:percentage, type:CommissionType.FACTURA).save(validate:false)
      company.addToCommissions(commission)
    and:"The sale order"
      SaleOrder saleOrder = new SaleOrder(company:company).save(validate:false)
      SaleOrderItem saleOrderItem = new SaleOrderItem()
      saleOrderItem.price = price
      saleOrderItem.quantity = 1
      saleOrderItem.save(validate:false)
      saleOrder.addToItems(saleOrderItem)
    when:
      def transaction = service.registerCommissionForSaleOrder(saleOrder)
    then:
      transaction.id
      transaction.amount == amountExpected
    where:
    fee   | percentage  | price   ||  amountExpected
    2     | 0           | 100     ||  2
    2     | 0           | 50      ||  2
    0     | 10          | 100     ||  10
    0     | 5           | 100     ||  5
    0     | 10          | 50      ||  5
  }

  void "Should throw exception when company hasn't invoice commission"() {
    given:"A company"
      Company company = new Company(rfc:"XXX010101XXX").save(validate:false)
    and:"The invoice commission"
      Commission commission = new Commission(fee:2, percentage:0, type:CommissionType.PAGO).save(validate:false)
      company.addToCommissions(commission)
    and:"The sale order"
      SaleOrder saleOrder = new SaleOrder(company:company).save(validate:false)
      SaleOrderItem saleOrderItem = new SaleOrderItem()
      saleOrderItem.price = 100
      saleOrderItem.quantity = 1
      saleOrderItem.save(validate:false)
      saleOrder.addToItems(saleOrderItem)
    when:
      def transaction = service.registerCommissionForSaleOrder(saleOrder)
    then:
      thrown BusinessException
  }

  @Unroll
  void "Should save a fixed commission transaction with amount=#amountExpected when commission fee=#fee for a company"() {
    given:"A company"
      Company company = new Company(rfc:"XXX010101XXX").save(validate:false)
    and:"The invoice commission"
      Commission commission = new Commission(fee:fee, percentage:0, type:CommissionType.FIJA).save(validate:false)
      company.addToCommissions(commission)
    when:
      def transaction = service.applyFixedCommissionToCompany(company)
    then:
      transaction.id
      transaction.amount == amountExpected
    where:
    fee   ||  amountExpected
    1000  ||  1000
    2000  ||  2000
  }

  void "Should throw exception when company hasn't fixed commission"() {
    given:"A company"
      Company company = new Company(rfc:"XXX010101XXX").save(validate:false)
    and:"The invoice commission"
      Commission commission = new Commission(fee:2, percentage:0, type:CommissionType.PAGO).save(validate:false)
      company.addToCommissions(commission)
    when:
      def transaction = service.applyFixedCommissionToCompany(company)
    then:
      thrown BusinessException
  }

  @Unroll
  void "Should return #expect when company has fixed commission transaction = #transactionFound"() {
    given:"A company"
      Company company = new Company(rfc:"XXX010101XXX").save(validate:false)
    and:"The current month period"
      Period period = new Period(init:new Date().parse("dd-MM-yyyy", "01-04-2017"), end:new Date().parse("dd-MM-yyyy", "30-04-2017"))
      collaboratorService.getCurrentMonthPeriod() >> period
    and:"Fixed Commission Transaction"
      CommissionTransaction.metaClass.static.findByTypeAndCompanyAndDateCreatedBetween = {type,comp,init,end -> transactionFound }
    when:
      Boolean result = service.companyHasFixedCommissionAppliedInCurrentMonth(company)
    then:
      result == expect
    where:
      transactionFound                                  ||  expect
      new CommissionTransaction().save(validate:false)  ||  true
      null                                              ||  false
  }

}

