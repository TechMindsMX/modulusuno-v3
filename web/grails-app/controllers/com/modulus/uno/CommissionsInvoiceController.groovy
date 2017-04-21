package com.modulus.uno

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class CommissionsInvoiceController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def commissionTransactionService
  def commissionsInvoiceService

  @Transactional
  def createCommissionsInvoice(Company company) {
    Corporate corporate = Corporate.get(params.corporateId)
    commissionsInvoiceService.createCommissionsInvoiceForCompany(company)
    redirect action:"listCommissionsInvoice", id:company.id, params:[corporateId:corporate.id]
  }

  def listCommissionsInvoice(Company company) {
    params.max = 25
    params.sort = "dateCreated"
    params.order = "desc"
    Corporate corporate = Corporate.get(params.corporateId)
    [invoices:commissionsInvoiceService.getCommissionsInvoiceForCompany(company, params), corporate:corporate, company:company]
  }

  @Transactional
  def stampInvoice(CommissionsInvoice invoice) {
    Corporate corporate = Corporate.get(params.corporateId)
    commissionsInvoiceService.stampInvoice(invoice)
    redirect action:"listCommissionsInvoice", id:invoice.receiver.id, params:[corporateId:corporate.id]
  }

  def showCommissionsInvoice(CommissionsInvoice invoice) {
    Corporate corporate = Corporate.get(params.corporateId)
    List commissionsSummary = commissionsInvoiceService.getCommissionsSummaryFromInvoice(invoice)
    [corporate:corporate, invoice:invoice, commissionsSummary:commissionsSummary]
  }

}
