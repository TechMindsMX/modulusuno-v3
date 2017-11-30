package com.modulus.uno.quotation

import com.modulus.uno.Company
import com.modulus.uno.Product

class QuotationRequestController {

    QuotationRequestService quotationRequestService
    def springSecurityService

    def index() {
    	Company company = Company.get(session.company)
      List<QuotationContract> quotationContractList = QuotationContract.findAllByCompany(company)
      def currentUser = springSecurityService.currentUser
      def lista = quotationContractList.collect{
        if(it.users.contains(currentUser)){
          return it
        }
      }.grep()
      println quotationContractList.users.dump()
      println "**"*25
      println currentUser
      println "**"*25
      println lista.dump()
      
      respond new QuotationContract(), model:[quotationContractList:quotationContractList,
       company:company, lista:lista
      ]
    }

    def chooseClient(QuotationContract quotationContract){
      def quotationRequestList = QuotationRequest.findAllByQuotationContract(quotationContract)
      render view:'index', model:[quotationRequestList:quotationRequestList, quotationContract:quotationContract]
    }


    def create(){
    	Company company = Company.get(session.company)
      List<QuotationContract> quotationContractList = QuotationContract.findAllByCompany(company)
      BigDecimal ivaRate = quotationRequestService.getIvaCurrent()

      [company:company,
      quotationContractList:quotationContractList,
      ivaRate:ivaRate]
    }

    def save(QuotationRequestCommand quotationRequestCommand ){
      def quotationRequest = quotationRequestCommand.getQuotationRequest()
      quotationRequestService.create(quotationRequest)
      redirect(action: 'show', id: quotationRequest.id)
    }

    def show(QuotationRequest quotationRequest){
      List<Product> products = Product.getAll()
      respond quotationRequest, model:[billers:quotationRequestService.getBillerCompanies(session.company.toLong()), products:products]
    }

    def edit(QuotationRequest quotationRequest){
      BigDecimal ivaRate = quotationRequestService.getIvaCurrent()
      [quotationRequest:quotationRequest,
      ivaRate:ivaRate]
    }

    def update(QuotationRequestCommand quotationRequestCommand){
        QuotationRequest quotationRequestUpdate = quotationRequestCommand.getQuotationRequest()
        QuotationRequest quotationRequest = QuotationRequest.get(params.id.toInteger())
        quotationRequest.description = quotationRequestUpdate.description
        quotationRequest.commission = quotationRequestCommand.getCommission(quotationRequestCommand.commission)
        quotationRequest.total = quotationRequestUpdate.total
        quotationRequest.subtotal = quotationRequestUpdate.subtotal
        quotationRequest.iva = quotationRequestUpdate.iva
        quotationRequestService.update(quotationRequest)
      redirect(action: 'show', id: quotationRequest.id)
    }

    def delete(QuotationRequest quotationRequest){
      quotationRequestService.delete(quotationRequest)
      redirect(action: 'index')
    }

    def processed(){
      List<QuotationRequest> quotationRequestList = QuotationRequest.findAllByStatus(QuotationRequestStatus.PROCESSED)

      [quotationRequestList:quotationRequestList]
    }

    def requestProcessed(QuotationRequestCommand quotationRequestCommand){
      QuotationRequest quotationRequestUpdate = quotationRequestCommand.getQuotationRequest()
      QuotationRequest quotationRequest= QuotationRequest.get(params.id.toInteger())
      quotationRequest.product = Product.get(params.productId.toLong())
      quotationRequest.commission = quotationRequestCommand.getCommission(params.commission)
      quotationRequest.biller = Company.get(quotationRequestCommand.biller.toLong())
      quotationRequestService.requestProcessed(quotationRequest)
      redirect(action: 'index')
    }

    def send(){
      List<QuotationRequest> quotationRequestList = QuotationRequest.findAllByStatus(QuotationRequestStatus.SEND)

      [quotationRequestList:quotationRequestList]
    }

    def sendQuotation(QuotationRequest quotationRequest){
      quotationRequestService.sendQuotation(quotationRequest)
      redirect(action: 'index')
    }

}
