package com.modulus.uno

class UploadDocumentsController {

  def documentService

    def index() {
      def company = Company.findById(session.company.toLong())
      [company:company, documents: company.documents]
    }

    def save() {
      def type = params.type
      documentService.uploadDocumentCompany(params."${type}",type,session.company.toLong())
      redirect action:"index",params:[company:session.company]
    }

    def show() {
      Company company = Company.get(session.company ?: params.companyId)
      def documents = company.documents.findAll{ it -> it.status == true}
      [documents:documents, company:company.id,status:company.status,infoCompany:company, baseUrlDocuments:grailsApplication.config.grails.url.base.images]
    }

    def create() {
      def user = User.findById(params.user)
      def legalRepresentativeDocument = documentService.getDocumentsByCompanyForLegalRepresentative(user.profile.documents,session.company.toLong())
      [user:params.user,company:session.company,documents:legalRepresentativeDocument]
    }

    def showDocumentLegalRepresentative() {
      def legalRepresentative = User.findById(params.user)
      def documents = documentService.getDocumentsByCompanyForLegalRepresentative(legalRepresentative.profile.documents,session.company ?: params.companyId)
      render view:"show", model:[documents:documents, company:session.company,infoUser:legalRepresentative, baseUrlDocuments:grailsApplication.config.grails.url.base.images]
    }

    def saveLegalRepresentativeDocuments() {
      def type = params.type
      documentService.uploadDocumentForLegalRepresentative(params."${type}",type,params.userId,session.company.toLong())
      redirect action:"create",params:[company:session.company,user:params.userId]
    }

}
