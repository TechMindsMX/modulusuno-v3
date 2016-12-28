package com.modulus.uno

class CompanySelectTagLib {

  def springSecurityService
  OrganizationService organizationService

  static namespace = "companyInfo"
  static defaultEncodeAs = "raw"

  def companyInfo = { attrs, body ->
    def company = Company.findById(session.company.toLong())
    out << "${company.toString()}"
  }

  def selectedCompany = { attrs,body ->
    def user = springSecurityService.currentUser
    def companies = organizationService.findAllCompaniesOfUser(user)
    out << g.select(from:companies, id:"companyNavSelect", name:"company",optionKey:"id", value:"${session.company}",required:"required")
  }

  def isAvailableForOperationInThisCompany = { attrs, body ->
    def company = Company.findById(session.company.toLong())
    out << (company.status == CompanyStatus.ACCEPTED)
  }

}
