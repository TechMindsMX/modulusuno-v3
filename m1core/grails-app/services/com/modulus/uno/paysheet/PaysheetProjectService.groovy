package com.modulus.uno.paysheet

import grails.transaction.Transactional
import com.modulus.uno.CompanyService
import com.modulus.uno.CorporateService
import com.modulus.uno.BusinessEntityService
import com.modulus.uno.UserService
import com.modulus.uno.Company
import com.modulus.uno.Corporate
import com.modulus.uno.BusinessEntity
import com.modulus.uno.User
import com.modulus.uno.Profile
import com.modulus.uno.Role
import com.modulus.uno.CompanyStatus
import com.modulus.uno.NameType

class PaysheetProjectService {

  CompanyService companyService
  CorporateService corporateService
  BusinessEntityService businessEntityService
  UserService userService

  @Transactional
  PaysheetProject savePaysheetProject(PaysheetProject paysheetProject) {
    paysheetProject.save()
    paysheetProject
  }

  @Transactional
  void deletePaysheetProject(PaysheetProject paysheetProject) {
    paysheetProject.delete()
  }

  PaysheetProject getPaysheetProjectByPaysheetContractAndName(PaysheetContract paysheetContract, String name) {
    PaysheetProject.findByPaysheetContractAndName(paysheetContract, name)
  }

  List<Company> getCompaniesInCorporate(Long idCompany) {
    Corporate corporate = corporateService.getCorporateFromCompany(idCompany)
    companyService.findCompaniesByCorporateAndStatus(CompanyStatus.ACCEPTED, corporate.id)
  }

  @Transactional
  PayerPaysheetProject savePayerPaysheetProject(PayerPaysheetProject payerPaysheetProject) {
    payerPaysheetProject.save()
    log.info "Payer Paysheet project saved: ${payerPaysheetProject.dump()}"
    payerPaysheetProject  
  }

  @Transactional
  def deletePayer(PayerPaysheetProject payerPaysheetProject) {
    payerPaysheetProject.delete()
  }

  List<BusinessEntity> getAvailableEmployeesToAdd(PaysheetProject paysheetProject) {
    (paysheetProject.paysheetContract.employees - paysheetProject.employees).toList().sort { it.toString() }
  }

  @Transactional
  def addEmployeesToPaysheetProject(PaysheetProject paysheetProject, def params) {
    log.info "Adding selected employees: ${params.entities}"
    List<BusinessEntity> employees = businessEntityService.getBusinessEntitiesFromIds(params.entities)
    paysheetProject.employees.addAll(employees)
    paysheetProject.save()
    log.info "Employees in paysheetProject: ${paysheetProject.employees}"
    paysheetProject
  }
  
  @Transactional
  def deleteEmployeeFromPaysheetProject(PaysheetProject paysheetProject, Long idEmployee){
    BusinessEntity employee = BusinessEntity.get(idEmployee)
    paysheetProject.removeFromEmployees(employee)
    paysheetProject.save()
    paysheetProject
  }

  @Transactional
  BillerPaysheetProject saveBillerPaysheetProject(BillerPaysheetProject billerPaysheetProject) {
    billerPaysheetProject.save()
    log.info "Biller Paysheet project saved: ${billerPaysheetProject.dump()}"
    billerPaysheetProject  
  }

  @Transactional
  def deleteBiller(BillerPaysheetProject billerPaysheetProject) {
    billerPaysheetProject.delete()
  }

  @Transactional
  UserEmployee createUserForPaysheetProjectEmployee(PaysheetProject paysheetProject, BusinessEntity businessEntity) {
    User user = createUserFromEmployee(businessEntity)
    UserEmployee userEmployee = new UserEmployee (
      user: user,
      businessEntity: businessEntity,
      paysheetProject: paysheetProject
    )
    userEmployee.save()
    userEmployee
  }

  User createUserFromEmployee(BusinessEntity businessEntity) {
    User user = new User (
      username:businessEntity.rfc,
      password:businessEntity.curp,
      enabled:true,
      accountExpired:false,
      accountLocked:false,
      passwordExpired:false
    )

    Profile profile = new Profile (
      name: businessEntity.names.find { it.type == NameType.NOMBRE },
      lastName: businessEntity.names.find { it.type == NameType.APELLIDO_PATERNO },
      motherLastName: businessEntity.names.find { it.type == NameType.APELLIDO_MATERNO },
      email: "fakemail@mail.com"
    )

    userService.createUserWithoutRole(user, profile)
    userService.setAuthorityToUser(user, "ROLE_EMPLOYEE")
  }

}
