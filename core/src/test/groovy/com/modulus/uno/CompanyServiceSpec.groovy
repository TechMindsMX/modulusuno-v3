package com.modulus.uno

import grails.test.mixin.TestFor
import spock.lang.Specification
import java.lang.Void as Should
import grails.test.mixin.Mock
import java.text.SimpleDateFormat
import java.util.Calendar
import static java.util.Calendar.*
import spock.lang.Unroll

@TestFor(CompanyService)
@Mock([Company,Address,S3Asset,User,UserRole,Role,Profile])
class CompanyServiceSpec extends Specification {

  ModulusUnoService modulusUnoService = Mock(ModulusUnoService)
  PurchaseOrderService purchaseOrderService = Mock(PurchaseOrderService)
  CashOutOrderService cashOutOrderService = Mock(CashOutOrderService)
  LoanOrderHelperService loanOrderHelperService = Mock(LoanOrderHelperService)
  FeesReceiptService feesReceiptService = Mock(FeesReceiptService)
  SaleOrderService saleOrderService = Mock(SaleOrderService)
  DepositOrderService depositOrderService = Mock(DepositOrderService)
  CollaboratorService collaboratorService = Mock(CollaboratorService)

  def setup(){
    service.modulusUnoService = modulusUnoService
    service.purchaseOrderService = purchaseOrderService
    service.cashOutOrderService = cashOutOrderService
    service.loanOrderHelperService = loanOrderHelperService
    service.feesReceiptService = feesReceiptService
    service.saleOrderService = saleOrderService
    service.depositOrderService = depositOrderService
    service.collaboratorService = collaboratorService
  }

  Should "create a direction for a Company"(){
    given:"a company"
      def company = new Company(rfc:"JIGE930831NZ1",
                                bussinessName:"Apple Computers",
                                webSite:"http://www.apple.com",
                                employeeNumbers:40,
                                grossAnnualBilling:4000).save(validate:false)
    and:"the address"
      def address = new Address(street:"Poniente 3",
                                streetNumber:"266",
                                suite:"S/N",
                                zipCode:"57840",
                                colony:"Reforma",
                                town:"Gustavo A.Madero",
                                city:"México",
                                country:"México",
                                federalEntity:"México",
                                company:company)
    when:
      company = service.createAddressForCompany(address, company.id)
    then:
      company.addresses.first().street == address.street
  }

  def "create a s3Asset and create relatiuon with company"() {
    given:
      def asset = new S3Asset().save(validate:false)
    and:
      def company = createCompany()
    when:
      def companyWithDocuments = service.createS3AssetForCompany(asset,company.id)
    then:
      companyWithDocuments.documents.first() instanceof S3Asset
  }

  void "find all companies with status validate"() {
    given:
      def company = createCompany()
      company.status = CompanyStatus.VALIDATE
      company.save()
    when:
      def companyResult = service.allCompaniesByStatus(CompanyStatus.VALIDATE)
    then:
      companyResult.first().status == CompanyStatus.VALIDATE
  }

  void "search company by filters"() {
    given:
      createSevenCompanies()
      def params = mapDetails
    when:
      def companies = service.listCompanyByFilters(params)
    then:
      companies.size() == sizeResult
    where:
            mapDetails                                    | sizeResult
      [status:CompanyStatus.VALIDATE]                     | 2
      [rfc:"RED861224KJD"]                                | 1
      [bussinessName:"apple3"]                            | 1
      [status:CompanyStatus.ACCEPTED,rfc:"ROW861224LDD"]   | 1
  }

  void "find company in base to actor"() {
    given:
      def user = new User(username:"says33",password:"123456789QWE").save(validate:false)
    and:
      createSevenCompanies()
      def company = Company.get(1)
      company.addToActors(user)
      company.save(validate:false)
    when:
      def companyResult = service.findCompaniesForThisUser(user)
    then:
      companyResult.first().id == 1
  }

  void "find company in base to legal representatives"() {
    given:
      def user = new User(username:"says33",password:"123456789QWE").save(validate:false)
    and:
      createSevenCompanies()
      def company = Company.get(1)
      company.addToLegalRepresentatives(user)
      company.save(validate:false)
    when:
      def companyResult = service.findCompaniesForThisUser(user)
    then:
      companyResult.first().id == 1
  }

  void "find company in base to legal representatives and actor "() {
    given:
      def user = new User(username:"says33",password:"123456789QWE").save(validate:false)
    and:
      createSevenCompanies()
      def company = Company.get(1)
      company.addToLegalRepresentatives(user)
      company.save(validate:false)
      def company2 = Company.get(3)
      company2.addToActors(user)
      company2.addToLegalRepresentatives(user)
      company2.save(validate:false)
    when:
      def companyResult = service.findCompaniesForThisUser(user)
    then:
      companyResult.size() == 2
      companyResult.first().id == 1
  }

  def "verify that not missing autorizers for this company"() {
    given:
      def userRole = new Role(authority:'ROLE_INTEGRADO_AUTORIZADOR').save(validate:false)
      def userRole1 = new Role(authority:'ROLE_LEGAL_REPRESENTATIVE').save(validate:false)
    and:
      createUserWithRole('autorizador1', 'autorizador1', 'autorizador1@email.com', userRole)
      createUserWithRole('autorizador2', 'autorizador2', 'autorizador2@email.com', userRole)
      createUserWithRole('representante', 'representante', 'representante@email.com', userRole1)
    and:
      def company = new Company()
      company.numberOfAuthorizations = 2
      company.addToActors(User.get(1))
      company.addToActors(User.get(2))
      company.save(validate:false)
    when:
      def result = service.getAuthorizersByCompany(company)
    then:
      result.size() == 2
  }

  def "verify that lack an autorizer for this company"() {
    given:
      def userRole = new Role(authority:'ROLE_INTEGRADO_AUTORIZADOR').save(validate:false)
      def userRole1 = new Role(authority:'ROLE_LEGAL_REPRESENTATIVE').save(validate:false)
    and:
      createUserWithRole('autorizador1', 'autorizador1', 'autorizador1@email.com', userRole)
      createUserWithRole('representante', 'representante', 'representante@email.com', userRole1)
    and:
      def company = new Company()
      company.numberOfAuthorizations = 2
      company.addToActors(User.get(1))
      company.addToActors(User.get(2))
      company.save(validate:false)
    when:
      def result = service.getAuthorizersByCompany(company)
    then:
      result.size() == 1
  }

  def "verify that lack autorizers for this company,when exist 2 authorizers and one legal Representavice"() {
    given:
      def userRole = new Role(authority:'ROLE_INTEGRADO_AUTORIZADOR').save(validate:false)

      def userRole1 = new Role(authority:'ROLE_LEGAL_REPRESENTATIVE').save(validate:false)
    and:
      createUserWithRole('autorizador1', 'autorizador1', 'autorizador1@email.com', userRole)
      createUserWithRole('autorizador2', 'autorizador2', 'autorizador2@email.com', userRole)
      createUserWithRole('representante', 'representante', 'representante@email.com', userRole1)
    and:
      def company = new Company()
      company.numberOfAuthorizations = 1
      company.addToActors(User.get(1))
      company.addToActors(User.get(2))
      company.save(validate:false)
    when:
      def result = service.getAuthorizersByCompany(company)
    then:
      result.size() == 2
  }

  def "verify that lack an autorizer for this company,when only exist legal a representative"() {
    given:
      def userRole1 = new Role(authority:'ROLE_LEGAL_REPRESENTATIVE').save(validate:false)
    and:
      createUserWithRole('representante', 'representante', 'representante@email.com', userRole1)
    and:
      def company = new Company()
      company.numberOfAuthorizations = 1
      company.addToActors(User.get(1))
      company.save(validate:false)
    when:
      def result = service.getAuthorizersByCompany(company)
    then:
      result.size() == 0
  }

  def "get all actors with role INTEGRADO_AUTORIZADOR for this company"() {
    given:
      def userRole = new Role(authority:'ROLE_INTEGRADO_AUTORIZADOR').save(validate:false)
      def userRole1 = new Role(authority:'ROLE_LEGAL_REPRESENTATIVE').save(validate:false)
    and:
      createUserWithRole('autorizador1', 'autorizador1', 'autorizador1@email.com', userRole)
      createUserWithRole('autorizador2', 'autorizador2', 'autorizador2@email.com', userRole)
      createUserWithRole('representante', 'representante', 'representante@email.com', userRole1)
    and:
      def company = new Company()
      company.addToActors(User.get(1))
      company.addToActors(User.get(2))
      company.addToActors(User.get(3))
      company.save(validate:false)
    when:
      def count = service.getAuthorizersByCompany(company)
    then:
      count.size() == 2

  }

  def "get the number of authorizers missing from is Company"() {
    given:
      def userRole = new Role(authority:'ROLE_INTEGRADO_AUTORIZADOR').save(validate:false)
      def userRole1 = new Role(authority:'ROLE_LEGAL_REPRESENTATIVE').save(validate:false)
    and:
      createUserWithRole('autorizador1', 'autorizador1', 'autorizador1@email.com', userRole)
      createUserWithRole('autorizador2', 'autorizador2', 'autorizador2@email.com', userRole)
      createUserWithRole('representante', 'representante', 'representante@email.com', userRole1)
    and:
      def company = new Company()
      company.numberOfAuthorizations = count
      company.addToActors(User.get(1))
      company.addToActors(User.get(2))
      company.addToActors(User.get(3))
      company.save(validate:false)
    when:
      def numberOfAuthorizers = service.getNumberOfAuthorizersMissingForThisCompany(company)
    then:
      (numberOfAuthorizers == result) == comparation
    where:
      count || result | comparation
       3    ||  2     | false
       1    ||  0     | true
  }

  void "Should get balances of company"() {
    given:"A company"
      Company company = createCompany()
      company.status = CompanyStatus.ACCEPTED
    and:"An account"
      ModulusUnoAccount account = Mock(ModulusUnoAccount)
      account.timoneUuid >> "1234567890"
      account.save(validate:false)
      company.accounts = [account]
      company.save(validate:false)
    and:
      modulusUnoService.consultBalanceOfAccount(company.accounts.first().timoneUuid) >> [100.00,0.00]
    when:"Get Balance of company"
      Balance balances = service.getBalanceOfCompany(company)
    then:"Expect a balance and usd amount"
      balances.balance == 100
      balances.usd == 0
  }

  void "Should get transiting balance of company"(){
    given:"A company"
      Company company = createCompany()
    and:
      purchaseOrderService.getTotalPurchaseOrderAuthorizedOfCompany(company) >> 100
      cashOutOrderService.getTotalOrdersAuthorizedOfCompany(company) >> 100
      loanOrderHelperService.getTotalOrdersAuthorizedOfCompany(company) >> 100
      feesReceiptService.getTotalFeesReceiptAuthorizedOfCompany(company) >> 100
    when:
      def result = service.getBalanceTransiting(company)
    then:
      result == 400
  }

  void "Should get subject to collection balance of company"(){
    given:"A company"
      Company company = createCompany()
    and:
      saleOrderService.getTotalSaleOrderAuthorizedOfCompany(company) >> 100
      depositOrderService.getTotalDepositOrderAuthorizedOfCompany(company) >> 100
    when:
      def result = service.getBalanceSubjectToCollection(company)
    then:
      result == 200
  }

  void "Should throw a BusinessException when account statement period is not valid"(){
    given:"A company"
      Company company = createCompany()
    and:"A not valid period"
      String beginDate = "30-04-2016"
      String endDate = "01-03-2016"
    when:"get the account statement"
      service.getAccountStatementOfCompany(company, beginDate, endDate)
    then:"expect a business exception"
      thrown BusinessException
  }

  void "Should get the account statement of a company"(){
    given:"A company"
      Company company = createCompany()
    and:"An account"
      ModulusUnoAccount account = Mock(ModulusUnoAccount)
      account.timoneUuid >> "1234567890"
      account.save(validate:false)
      company.accounts = [account]
      company.save(validate:false)
    and:"A valid period"
      String beginDate = "01-04-2016"
      String endDate = "30-04-2016"
      collaboratorService.periodIsValid(beginDate, endDate) >> true
    when:"get the account statement"
      AccountStatement accountStatement = service.getAccountStatementOfCompany(company, beginDate, endDate)
    then:
      accountStatement.balance.balance == 0
      accountStatement.balanceTransiting == 0
      1 * modulusUnoService.getTransactionsInPeriodOfIntegrated(_ as AccountStatementCommand)
  }

  @Unroll
  void "Should get #expectResult when a company has total balance #totalBalance to solve amount #amount"() {
    given:"A company"
      Company company = createCompany()
    and:"An account"
      ModulusUnoAccount account = Mock(ModulusUnoAccount)
      account.timoneUuid = "1234567890"
      account.save(validate:false)
      company.accounts = [account]
      company.status = CompanyStatus.ACCEPTED
      company.save(validate:false)
    and:
      modulusUnoService.consultBalanceOfAccount(company.accounts.first().timoneUuid) >> [totalBalance,0]
    when:"we check balance to solve amount"
      Boolean result = service.enoughBalanceCompany(company, amount)
    then:
      result == expectResult
    where:
      amount  | totalBalance  | expectResult
      500     | 1000          | true
      1000    | 1000          | true
      1001    | 1000          | false
      1001    | 500           | false
      1001    | 1000          | false
      1001    | 1001          | true
  }

  private def createUserWithRole(String username, String password, String email, def userRole) {
      def user = User.findByUsername(username) ?: new User(username:username,
      password:password,
      enabled:true,
      profile:new Profile(name:username,
      lastName:'lastName',
      motherLastName:'motherLastName',
      email:email)).save(validate:false)

      if(!UserRole.get(user.id,userRole.id))
        UserRole.create user, userRole, true
  }

  private createSevenCompanies() {
    new Company(rfc:"ROD861224KJD",bussinessName:"apple1",status:CompanyStatus.CREATED).save(validate:false)
    new Company(rfc:"RED861224KJD",bussinessName:"apple2",status:CompanyStatus.VALIDATE).save(validate:false)
    new Company(rfc:"ROW861224LDD",bussinessName:"apple3",status:CompanyStatus.ACCEPTED).save(validate:false)
    new Company(rfc:"ROQ861224AJD",bussinessName:"apple4",status:CompanyStatus.REJECTED).save(validate:false)
    new Company(rfc:"ROV861224HJD",bussinessName:"apple5",status:CompanyStatus.CREATED).save(validate:false)
    new Company(rfc:"RIY861224GJD",bussinessName:"apple6",status:CompanyStatus.ACCEPTED).save(validate:false)
    new Company(rfc:"RCS861224FJD",bussinessName:"apple7",status:CompanyStatus.VALIDATE).save(validate:false)
  }

  private Company createCompany() {
    def company = new Company(rfc:"JIG930831NZ1",
                                bussinessName:"Apple Computers",
                                webSite:"http://www.apple.com",
                                employeeNumbers:40,
                                grossAnnualBilling:4000)
    def address = new Address(street:"Poniente 3",
                                streetNumber:"266",
                                suite:"S/N",
                                zipCode:"57840",
                                colony:"Reforma",
                                town:"Gustavo A.Madero",
                                city:"México",
                                country:"México",
                                federalEntity:"México",
                                addressType:AddressType.FISCAL,
                                company:company)

    company.addToAddresses(address)
    company.save()
    company
  }



}
