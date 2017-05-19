package com.modulus.uno

import grails.transaction.Transactional
import java.text.SimpleDateFormat
import org.springframework.context.i18n.LocaleContextHolder as LCH
import org.springframework.transaction.annotation.Propagation

@Transactional
class CompanyService {

  def purchaseOrderService
  def cashOutOrderService
  def saleOrderService
  def modulusUnoService
  def loanOrderHelperService
  def feesReceiptService
  def collaboratorService
  def messageSource
  def restService
  def corporateService
  DirectorService directorService
  TransactionService transactionService
  def invoiceService
  CommissionTransactionService commissionTransactionService

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

  def findCompaniesByCorporateAndStatus(CompanyStatus status,Long corporateId){
    Corporate corporate = Corporate.get(corporateId)
    ArrayList<Company> companies = corporate.companies.findAll{ it.status == status }
    companies
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
    balances.balance >= amount
  }

  AccountStatement getAccountStatementOfCompany(Company company, String beginDate, String endDate){
    if (!beginDate && !endDate) {
      beginDate = collaboratorService.getBeginDateCurrentMonth()
      endDate = collaboratorService.getEndDateCurrentMonth()
    } else {
      if (!collaboratorService.periodIsValid(beginDate, endDate)) throw new BusinessException("La fecha inicial debe ser anterior a la fecha final")
    }

    AccountStatement accountStatement = new AccountStatement()
    accountStatement.company = company
    accountStatement.balance = getBalanceOfCompany(company)
    accountStatement.startDate = new SimpleDateFormat("dd-MM-yyyy").parse(beginDate)
    accountStatement.endDate = new SimpleDateFormat("dd-MM-yyyy").parse(endDate)
    accountStatement.transactions = transactionService.getTransactionsAccountForPeriod(company.accounts?.first()?.stpClabe,accountStatement.startDate,accountStatement.endDate)
    accountStatement.commissionsBalance = commissionTransactionService.getCommissionsBalanceForCompanyAndStatus(company, CommissionTransactionStatus.PENDING)
    accountStatement
  }

  ArrayList<User> getAuthorizersByCompany(Company company) {
    directorService.findUsersOfCompanyByRole(company.id,['ROLE_AUTHORIZER_VISOR','ROLE_AUTHORIZER_EJECUTOR'])
  }

  Balance getBalanceOfCompany(Company company) {
    BigDecimal balance = 0
    BigDecimal usd = 0
    if (company.status == CompanyStatus.ACCEPTED && company.accounts) {
      balance = modulusUnoService.consultBalanceOfAccount(company.accounts.first().stpClabe)
    }
    new Balance(balance:balance, usd:usd)
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
      transaction.date = mov.dateCreated
      transaction.concept = mov.paymentConcept
      transaction.id = mov.keyTransaction ?: ""
      transaction.credit = mov.transactionType == TransactionType.DEPOSIT ? mov.amount : ""
      transaction.debit = mov.transactionType == TransactionType.WITHDRAW ? mov.amount : ""
      transaction.balance = mov.balance
      formattedTransactions << transaction
    }
    formattedTransactions
  }

  def sendDocumentsPerInvoice(def params, def rfc) {
    def documents = [key:params.key,cer:params.cer,logo:params.logo,,password:params.password, rfc:rfc, certNumber:params.numCert, serie:params.serie]
    def result = restService.sendFilesForInvoiceM1(documents)
    result
  }

  def updateDocumentsToStamp(def params, def rfc) {
    def documents = [key:params.key,cer:params.cer,logo:params.logo,,password:params.password, rfc:rfc, certNumber:params.numCert, serie:params.serie]
    log.info "Updating documents to stamp: ${documents}"
    def result = restService.updateFilesForInvoice(documents)
    result
  }

  def isAvailableForGenerateInvoices(String rfc) {
    def response = restService.existEmisorForGenerateInvoice(rfc)
  }

  PendingAccounts obtainPendingAccountsOfPeriod(Date startDate, Date endDate, Company company) {
    log.info "Pending Accounts between ${startDate} and ${endDate} for ${company}"
    PendingAccounts pendingAccounts = new PendingAccounts(startDate:startDate, endDate:endDate)
    pendingAccounts.listPayments = PurchaseOrder.findAllByFechaPagoBetweenAndStatusAndCompany(startDate, endDate, PurchaseOrderStatus.AUTORIZADA, company)
    def listExpiredPayments = PurchaseOrder.findAllByFechaPagoLessThanAndStatusAndCompany(startDate, PurchaseOrderStatus.AUTORIZADA, company)
    pendingAccounts.listExpiredPayments = listExpiredPayments
    pendingAccounts.totalExpiredPayments = listExpiredPayments ? listExpiredPayments.sum {it.total} : new BigDecimal(0)

    pendingAccounts.listCharges = SaleOrder.findAllByFechaCobroBetweenAndStatusInListAndCompany(startDate, endDate, [SaleOrderStatus.EJECUTADA, SaleOrderStatus.AUTORIZADA], company)
    def listExpiredCharges = SaleOrder.findAllByFechaCobroLessThanAndStatusInListAndCompany(startDate, [SaleOrderStatus.EJECUTADA, SaleOrderStatus.AUTORIZADA], company)
    pendingAccounts.listExpiredCharges = listExpiredCharges
    pendingAccounts.totalExpiredCharges = listExpiredCharges ? listExpiredCharges.sum {it.total} : new BigDecimal(0)

    pendingAccounts.totalPayments = pendingAccounts.listPayments ? pendingAccounts.listPayments.sum { it.total } : new BigDecimal(0)
    pendingAccounts.totalCharges = pendingAccounts.listCharges ? pendingAccounts.listCharges.sum { it.total } : new BigDecimal(0)
    pendingAccounts
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  Company saveInsideAndAssingCorporate(Company company, Long corporateId){
    // TODO: Se podría revalidar que el usuario sea corporativo
    if(company.validate()){
      Corporate corporate = Corporate.get(corporateId)
      corporateService.addCompanyToCorporate(corporate, company)
      company
    }
    else {
      throw new RuntimeException(company.errors*.toString().join(","))
    }
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

  Boolean isCompanyEnabledToStamp(Company company) {
    Address fiscalAddress = company.addresses.find {it.addressType == AddressType.FISCAL}
    def documents = isAvailableForGenerateInvoices(company.rfc)
    documents.status && fiscalAddress
  }

  List<SaleOrder> getDetailPastDuePortfolio(Long idCompany, Integer days) {
    saleOrderService.obtainListPastDuePortfolio(idCompany, days)
  }

  def assignAliasStpToCompany(Company company, String alias) {
    ModulusUnoAccount m1 = company.accounts.first()
    m1.aliasStp = alias
    m1.save()
  }

  Boolean companyIsEnabledToPay(Company company) {
    company.accounts.first().aliasStp && company.commissions.find { it.type == CommissionType.PAGO }
  }

  void changeSerieForInvoicesOfCompany(Company company, String serie, String folio) {
    Map newSerie = [rfc:company.rfc, serie:serie, folio:folio]
    invoiceService.changeSerieAndInitialFolioToStampInvoiceForEmitter(newSerie)
  }
}
