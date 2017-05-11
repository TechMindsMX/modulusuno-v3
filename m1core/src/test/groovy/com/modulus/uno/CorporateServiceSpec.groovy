package com.modulus.uno

import grails.test.mixin.TestFor
import spock.lang.Specification
import java.lang.Void as Should
import grails.test.mixin.Mock
import spock.lang.Unroll

@TestFor(CorporateService)
@Mock([Corporate,Company,User, Role,UserRole,Profile])
class CorporateServiceSpec extends Specification {

  def setup() {
      grailsApplication.config.grails.plugin.awssdk.domain.base.url = "-qa.modulusuno.com"
  }

  Should "create a corporate"() {
    given: "set variables to name and url "
      Corporate corporate = new Corporate(nameCorporate:"MakingDevs",
                                          corporateUrl:"MakingDevs-Fico")
    when:
      service.saveNewCorporate(corporate)
    then:
      corporate.nameCorporate == "MakingDevs"
      corporate.companies == null
      corporate.id == 1
  }

  Should "add a company to corporate"() {
    given: "create a company"
      Company company = new Company(rfc:"RODS861224JUI",bussinessName:"prueba",employeeNumbers:2,grossAnnualBilling:1000.00 ).save()
    and: "create a corporate"
      Corporate corporate = new Corporate(nameCorporate:"test", corporateUrl:"url").save(validate:false)
    when:
      Corporate corporateWithACompany = service.addCompanyToCorporate(corporate, Company.get(1))
    then:
      corporateWithACompany.companies.size() == 1
  }

  Should "add users to corparate"() {
    given: "create a user"
      User user = new User(username:"nuevo", password:"123456789Abc")
    and: "create a corpotate"
      Corporate corporate = new Corporate(nameCorporate:"test", corporateUrl:"url").save(validate:false)
    and: "create Role corporative"
      new Role(authority:"ROLE_CORPORATIVE").save()
    when:
      def corporateWithUser = service.addUserToCorporate(corporate.id,user)
    then:
      corporateWithUser.users.size() == 1
      corporateWithUser.users.first() == user
  }

  Should "get the corporate of one user with ROLE_CORPORATIVE"(){
    given:"the user"
      User user = new User(username:"nuevo", password:"123456789Abc").save(validate:false)
    and:"the corporative"
      Corporate corporate = new Corporate(nameCorporate:"Corporate1",corporateUrl:"someUrl").save(validate:false)
    and: "create Role corporative"
      new Role(authority:"ROLE_CORPORATIVE").save()
    and:"the user is added to the corporate"
      service.addUserToCorporate(corporate.id,user)
    when:
      Corporate userCorporate = service.findCorporateOfUser(user)
    then:
      userCorporate.id == corporate.id
      userCorporate.nameCorporate == "Corporate1"
  }

  Should "get the corporate user of a company"(){
    given:"the company"
      Company company = new Company(rfc:"CIGE930831RB1",
                                    webSite:"http://www.somewebsite.com",
                                    employeeNumbers:10)
      company.save(validate:false)
    and:"the corporate"
      Corporate corporate = new Corporate(nameCorporate:"someName",
                                          corporateUrl:"someUrl")
      corporate.addToCompanies(company)
    and:"the user"
      User user = new User(username:"egjimenezg")
      user.save(validate:false)
      Role role = new Role(authority:"ROLE_CORPORATIVE")
      role.save(validate:false)
      UserRole.create(user,role)
      corporate.addToUsers(user)
      corporate.save(validate:false)
    when:
      User corporateUser = service.findCorporateUserOfCompany(company.id)
    then:
      user
      user.username == "egjimenezg"
  }

  Should "get the corporate users by role"(){
    given:"the corporate"
      Corporate corporate = new Corporate(nameCorporate:"Corporate I",
                                          corporateUrl:"http://www.someurl.com")
      corporate.save(validate:false)

    and:"the current user"
      Role role = new Role(authority:_authority)
      User user = new User(username:"egjimenezg@gmail.com",
                           password:"1234567890")

      user.save(validate:false)
      role.save(validate:false)
      UserRole.create(user,role)
    and:"the spring security service mock"
      service.springSecurityService = [currentUser:user]
    and:"the users without roles"
      ArrayList<User> users = [new User(username:"user1"),
                               new User(username:"user2")]

      users.each{ _user -> _user.save(validate:false) }
      users.each{ _user ->
        corporate.addToUsers(_user)
      }
    when:
      ArrayList<User> corporateUsers = service.findCorporateUsers(corporate.id)
    then:
      corporateUsers.size() == _size
    where:
      _authority         | _size
      "ROLE_M1"          | 0
      "ROLE_CORPORATIVE" | 2
  }

  Should "get corporate in base of company"() {
    given:
      Corporate corporate = new Corporate(nameCorporate: "makingdevs", corporateUrl:"makingdevs").save()
    and:
      Company company = new Company().save(validate:false)
    and:
      corporate.addToCompanies(company)
      corporate.save()
    when:
      def response = service.findCorporateByCompanyId(company.id)
    then:
      response == "makingdevs-qa.modulusuno.com"
      println response
  }

  Should "get the legal representatives of a company"(){
    given:"the company"
      Company company = new Company(rfc:"CIGE930831RB1",
                                    webSite:"http://www.somewebsite.com",
                                    employeeNumbers:10)
      company.save(validate:false)
    and:"the corporate"
      Corporate corporate = new Corporate(nameCorporate:"someName",
                                          corporateUrl:"someUrl")
      corporate.addToCompanies(company)
    and:"the user legal ejecutor"
      User user = new User(username:"legal01")
      user.save(validate:false)
      Role role = new Role(authority:"ROLE_LEGAL_REPRESENTATIVE_EJECUTOR")
      role.save(validate:false)
      UserRole.create(user,role)
      corporate.addToUsers(user)
      corporate.save(validate:false)
    and:"the user legal visor"
      User user2 = new User(username:"legal02")
      user2.save(validate:false)
      Role role2 = new Role(authority:"ROLE_LEGAL_REPRESENTATIVE_VISOR")
      role2.save(validate:false)
      UserRole.create(user2,role2)
      corporate.addToUsers(user2)
      corporate.save(validate:false)
    when:
      def legalReps = service.findLegalRepresentativesOfCompany(company.id)
    then:
      legalReps
      legalReps[0].username == "legal01"
      legalReps[1].username == "legal02"
  }

  void "Should get corporate url from user"() {
    given:"The user"
      User user = new User().save(validate:false)
    and:"The corporate"
      Corporate corporate = new Corporate(corporateUrl:"myCorp").save(validate:false)
      corporate.addToUsers(user)
      corporate.save(validate:false)
    when:
      String url = service.findUrlCorporateOfUser(user)
    then:
      url == "myCorp-qa.modulusuno.com"
  }

  void "Should get corporate url from user without corporate"() {
    given:"The user"
      User user = new User().save(validate:false)
    and:"The corporate"
      Corporate corporate = new Corporate(corporateUrl:"myCorp").save(validate:false)
    when:
      String url = service.findUrlCorporateOfUser(user)
    then:
      url == "web-qa.modulusuno.com"
  }

}
