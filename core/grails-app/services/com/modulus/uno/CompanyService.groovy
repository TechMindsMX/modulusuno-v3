package com.modulus.uno

import grails.transaction.Transactional
import java.text.SimpleDateFormat
import org.springframework.context.i18n.LocaleContextHolder as LCH

@Transactional
class CompanyService {

  def purchaseOrderService
  def cashOutOrderService
  def saleOrderService
  def depositOrderService
  def modulusUnoService
  def loanOrderHelperService
  def feesReceiptService
  def collaboratorService
  def messageSource
  def restService

  def addingActorToCompany(Company company, User user) {
    company.addToActors(user)
    company.save()
  }

  def addingLegalRepresentativeToCompany(Company company, User user){
    company.addToLegalRepresentatives(user)
    company.save()
  }

  def allCompaniesByUser(User user){
    Company.createCriteria().list {
      actors {
        eq 'username', user.username
      }
    }
  }

  // TODO: Se tienen que aceptar los parámetros de limit y offset para que se ejecute la consulta paginada
  def allCompaniesByStatus(status) {
    Company.createCriteria().list {
     eq 'status', status
    }
  }

  def createAddressForCompany(Address address, Long companyId){
    def company = Company.get(companyId)
    company.addToAddresses(address)
    company.save()
    company
  }

  def createLegalRepresentativeOfCompanyPhysicalRegime(Company company){
    def tks = company.bussinessName.tokenize(' ')
    String username = tks[0]
    if (tks.size()>1)
      username += "_"+tks[1]
    String mail = tks[0]+"@"+tks[0]+"mail.com"
    Profile profile = new Profile(name:tks[0], lastName:tks[1] ?: "SA", motherLastName:tks[2] ?: "SA", email:mail, rfc: company.rfc)

    User user = new User(username:username, password:"Temporal12345", enabled:false, accountExpired:false, accountLocked:false, passwordExpired:false, profile:profile)

    user.save flush:true

    company.addToLegalRepresentatives(user)
    company
  }

  def createS3AssetForCompany(S3Asset asset, def companyId) {
    def company = Company.findById(companyId)
    company.addToDocuments(asset)
    company.save()
    company
  }

  Boolean enoughBalanceCompany(Company company, BigDecimal amount) {
    Balance balances = getBalanceOfCompany(company)
    BigDecimal availableBalance = balances.balance - getBalanceTransiting(company)
    availableBalance >= amount
  }

  def findCompaniesForThisUser(User user) {
    def companiesLegalRepresentatives =  Company.withCriteria {
      legalRepresentatives {
        eq 'username', user.username
      }
    }
    def companiesActors = Company.withCriteria {
      actors {
        eq 'username',user.username
      }
    }
    def companies = []
    companies << companiesLegalRepresentatives ?: ""
    companies << companiesActors ?: ""
    companies.flatten().unique()
  }

  AccountStatement getAccountStatementOfCompany(Company company, String beginDate, String endDate){
    if (!beginDate && !endDate) {
      beginDate = collaboratorService.getBeginDateCurrentMonth()
      endDate = collaboratorService.getEndDateCurrentMonth()
    } else {
      if (!collaboratorService.periodIsValid(beginDate, endDate)) throw new BusinessException("La fecha inicial debe ser anterior a la fecha final")
    }

    AccountStatementCommand command = new AccountStatementCommand (uuid: company.accounts?.first()?.timoneUuid, begin:beginDate, end:endDate)
    AccountStatement accountStatement = new AccountStatement()
    accountStatement.company = company
    accountStatement.balance = getBalanceOfCompany(company)
    accountStatement.balanceTransiting = getBalanceTransiting(company)
    accountStatement.balanceSubjectToCollection = getBalanceSubjectToCollection(company)
    accountStatement.startDate = new SimpleDateFormat("dd-MM-yyyy").parse(beginDate)
    accountStatement.endDate = new SimpleDateFormat("dd-MM-yyyy").parse(endDate)
    accountStatement.transactions = modulusUnoService.getTransactionsInPeriodOfIntegrated(command)

    accountStatement
  }

  def getAuthorizersByCompany(Company company) {
  	company.actors.findAll { user ->
      if (user.authorities.any { it.authority == "ROLE_INTEGRADO_AUTORIZADOR" })
        return user
    }
  }

  Balance getBalanceOfCompany(Company company) {
    BigDecimal balance = 0
    BigDecimal usd = 0
    if (company.status == CompanyStatus.ACCEPTED && company.accounts) {
      (balance, usd) = modulusUnoService.consultBalanceOfAccount(company?.accounts?.first()?.timoneUuid)
    }
    new Balance(balance:balance, usd:usd)
  }

  BigDecimal getBalanceTransiting(Company company){
    BigDecimal transitingPurchaseOrder = purchaseOrderService.getTotalPurchaseOrderAuthorizedOfCompany(company) ?: 0.0f
    BigDecimal transitingCashOutOrder = cashOutOrderService.getTotalOrdersAuthorizedOfCompany(company) ?: 0.0f
    BigDecimal transitingLoanPaymentOrder = loanOrderHelperService.getTotalOrdersAuthorizedOfCompany(company) ?: 0.0f
    BigDecimal transitingFeesReceipt = feesReceiptService.getTotalFeesReceiptAuthorizedOfCompany(company) ?: 0.0f
    transitingPurchaseOrder + transitingCashOutOrder + transitingLoanPaymentOrder + transitingFeesReceipt
  }

  def getBalanceSubjectToCollection(Company company){
    def balanceToCollectionSaleOrders = saleOrderService.getTotalSaleOrderAuthorizedOfCompany(company)
    def balanceToCollectionDepositOrders = depositOrderService.getTotalDepositOrderAuthorizedOfCompany(company)
    (balanceToCollectionSaleOrders ?: 0.0f) + (balanceToCollectionDepositOrders ?: 0.0f)
  }

  def getNumberOfAuthorizersMissingForThisCompany(Company company) {
    def authorizers = getAuthorizersByCompany(company)
    def authorizerMissing = company.numberOfAuthorizations - authorizers.size()
    if (authorizerMissing < 0) {
      authorizerMissing = 0
    }
    authorizerMissing
  }

  boolean isEnableToSendNotificationIntegrated(Company company) {
    /*int docsMin = 4
    if (company.taxRegime == CompanyTaxRegime.FISICA_EMPRESARIAL)
      docsMin = 5*/
    if (company.banksAccounts && company.addresses && (company.status == CompanyStatus.CREATED || company.status == CompanyStatus.REJECTED ))
      return true
    false
  }

  def listCompanyByFilters(def queryFilters) {
    def companies = Company.where {
        status != CompanyStatus.CREATED
    }.list()

    if (queryFilters.status)
        companies = companies.findAll { it.status == CompanyStatus."${queryFilters.status}" }
    if (queryFilters.bussinessName)
        companies = companies.findAll { it.bussinessName == queryFilters.bussinessName }
    if (queryFilters.rfc)
        companies = companies.findAll { it.rfc == queryFilters.rfc }

    companies
  }

  def formattingTransactionsForXls(def transactions) {
    List formattedTransactions = []
    transactions.each { mov ->
      Map transaction = [:]
      transaction.date = new Date(mov.timestamp)
      String typeTransaction =
      transaction.type = messageSource.getMessage("company.accountStatement.TransactionType.${mov.transactionType}", null, "${mov.transactionType}", LCH.getLocale())
      transaction.id = mov.reference ?: ""
      transaction.credit = mov.type=="CREDIT"?mov.amount:""
      transaction.debit = mov.type=="DEBIT"?mov.amount:""
      transaction.balance = mov.balance
      formattedTransactions << transaction
    }
    formattedTransactions
  }

  def sendDocumentsPerInvoice(def params, def rfc) {
    def documents = [key:params.key,cer:params.cer,logo:params.logo,,password:params.password, rfc:rfc, certNumber:params.numCert]
    String token = restService.obtainingTokenFromModulusUno()
    def result = restService.sendFilesForInvoiceM1(documents,token)
    result
  }

  def isAvailableForGenerateInvoces(String rfc) {
    def response = restService.existEmisorForGenerateInvoice(rfc)
    isAvailableForInvoices(response)
  }

  PendingAccounts obtainPendingAccountsOfPeriod(Date startDate, Date endDate, Company company) {
    Date begin = startDate ?: new Date()
    Date end = endDate ?: new Date()

    PendingAccounts pendingAccounts = new PendingAccounts(startDate:begin, endDate:end)
    pendingAccounts.listPayments = PurchaseOrder.findAllByFechaPagoBetweenAndStatusAndCompany(begin, end, PurchaseOrderStatus.AUTORIZADA, company)
    def listExpiredPayments = PurchaseOrder.findAllByFechaPagoLessThanAndStatusAndCompany(begin, PurchaseOrderStatus.AUTORIZADA, company)
    pendingAccounts.totalExpiredPayments = listExpiredPayments ? listExpiredPayments.sum {it.total} : new BigDecimal(0)

    pendingAccounts.listCharges = SaleOrder.findAllByFechaCobroBetweenAndStatusInListAndCompany(begin, end, [SaleOrderStatus.EJECUTADA, SaleOrderStatus.AUTORIZADA], company)
    def listExpiredCharges = SaleOrder.findAllByFechaCobroLessThanAndStatusInListAndCompany(begin, [SaleOrderStatus.EJECUTADA, SaleOrderStatus.AUTORIZADA], company)
    pendingAccounts.totalExpiredCharges = listExpiredCharges ? listExpiredCharges.sum {it.total} : new BigDecimal(0)

    pendingAccounts.totalPayments = pendingAccounts.listPayments ? pendingAccounts.listPayments.sum { it.total } : new BigDecimal(0)
    pendingAccounts.totalCharges = pendingAccounts.listCharges ? pendingAccounts.listCharges.sum { it.total } : new BigDecimal(0)
    pendingAccounts
  }

  private def isAvailableForInvoices(def response) {
    response.find { it.value == false}
  }

  def updateDateChargeForSaleOrder(String orderId, String chargeDate) {
    Date newChargeDate = new SimpleDateFormat("dd/MM/yyyy").parse(chargeDate)
    saleOrderService.updateDateChargeForOrder(orderId.toLong(), newChargeDate)
  }

  def updateDatePaymentForPurchaseOrder(String orderId, String paymentDate) {
    Date newPaymentDate = new SimpleDateFormat("dd/MM/yyyy").parse(paymentDate)
    purchaseOrderService.updateDatePaymentForOrder(orderId.toLong(), newPaymentDate)
  }

}
