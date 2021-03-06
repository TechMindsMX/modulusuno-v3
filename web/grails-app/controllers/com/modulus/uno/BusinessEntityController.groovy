package com.modulus.uno

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class BusinessEntityController {

  def businessEntityService
  def clientService
  def providerService
  def restService
  def springSecurityService
  def employeeService

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE", createAccountByProvider: "POST"]

  def index(Integer max) {
    params.max = 25
    def roles = springSecurityService.getPrincipal().getAuthorities()
    def company = Company.findById(session.company.toLong())
    max = Math.min(max ?: 25, 100)
    def offset = params.offset? params.offset.toInteger() : 0
    def total = company.businessEntities.size()

    def businessEntitiesIds = company.businessEntities.toList().collect { it.id }

    def allBusinessEntitiesCompany = []

    if(businessEntitiesIds) {
      def businessEntities = BusinessEntity.where {
        id in businessEntitiesIds
      }

      allBusinessEntitiesCompany = businessEntities.list(params)
    }

    boolean businessEntityToAuthorize = allBusinessEntitiesCompany.find { it.status == BusinessEntityStatus.TO_AUTHORIZE } ? true : false
    // def businessEntityList = allBusinessEntitiesCompany.subList(Math.min(offset, total), Math.min(offset+max,total))

    respond allBusinessEntitiesCompany, model:[businessEntityCount:total, businessEntityToAuthorize:businessEntityToAuthorize, clientProviderType:LeadType.CLIENTE, company:company]
  }

  def show(BusinessEntity businessEntity) {
    Company company = Company.get(session.company)
    LeadType relation = businessEntityService.getClientProviderType(businessEntity.rfc)
    Map clientData = businessEntityService.getClientData(company, businessEntity, relation)
    DataImssEmployee dataImssEmployee = businessEntityService.getDataImssEmployee(company, businessEntity, relation)
    respond businessEntity, model:[relation:relation.toString(), clientData:clientData, dataImssEmployee:dataImssEmployee]
  }

  def create() {

    def businessEntity = new BusinessEntity(params)
    respond businessEntity, model:[clientProviderType: params.clientProviderType]
  }

  def createAccountByProvider() {
    def businessEntity = BusinessEntity.findByRfc(params.rfcBank)
    redirect action:"show",id:businessEntity.id
  }

  @Transactional
  def save(BusinessEntityCommand command) {
    command.rfc = command.rfc.toUpperCase()
    command.clientProviderType = params.clientProviderType
    if (params.clientProviderType.equals("EMPLEADO")){
      command.website="http://www.employee.com"
      command.type = BusinessEntityType.FISICA
      params.persona = 'fisica'
    }

    BusinessEntity businessEntity = new BusinessEntity(command.properties)
    if (command.hasErrors()) {
      render(view:'create', model:[command:command, businessEntity:businessEntity, clientProviderType:params.clientProviderType], params:params,banks:Bank.list().sort{ it.name })
      return
    }

    Company company = Company.findById(session.company.toLong())
    businessEntityService.generatedBussinessEntityProperties(businessEntity, params, company)

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'businessEntity.created', args: [message(code: 'businessEntity.label', default: 'BusinessEntity'), businessEntity.id])
        redirect action: 'show', id:businessEntity.id
      }
      '*' { respond businessEntity, [status: CREATED] }
    }
  }

  def edit(BusinessEntity businessEntity) {
    String clientProviderType = businessEntityService.getClientProviderType(businessEntity.rfc)
    respond businessEntity, model:[clientProviderType:clientProviderType]
  }

  @Transactional
  def update(BusinessEntity businessEntity) {
    log.info "Business entity to update: ${businessEntity.dump()}"
    if (businessEntity == null) {
      transactionStatus.setRollbackOnly()
      notFound()
      return
    }

    if (businessEntity.hasErrors()) {
      transactionStatus.setRollbackOnly()
      String clientProviderType = businessEntityService.getClientProviderType(businessEntity.rfc)
      render  view:'edit', model:[businessEntity:businessEntity, clientProviderType:clientProviderType, params:params]
      return
    }

    def company = Company.get(session.company)

    businessEntityService.updateBusinessEntity(businessEntity, company, params)

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'businessEntity.updated', args: [message(code: 'businessEntity.label', default: 'BusinessEntity'), businessEntity.id])
        redirect action: 'show', id:businessEntity.id
      }
      '*'{ respond businessEntity, [status: OK] }
    }
  }

  @Transactional
  def delete(BusinessEntity businessEntity) {

    if (businessEntity == null) {
      transactionStatus.setRollbackOnly()
      notFound()
      return
    }

    businessEntity.delete flush:true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'businessEntity.label', default: 'BusinessEntity'), businessEntity.id])
        redirect action:"index", method:"GET"
      }
      '*'{ render status: NO_CONTENT }
    }
  }

  def search(){
    if (!params.rfc) {
      redirect action:"index"
      return
    }

    def company = Company.get(session.company)
    def businessEntityList = businessEntityService.findBusinessEntityByKeyword(params.rfc, null, company)
    boolean businessEntityToAuthorize = businessEntityList.find { it.status == BusinessEntityStatus.TO_AUTHORIZE } ? true : false
    if(businessEntityList.isEmpty()){
      flash.message = "No se encontr\u00F3 cliente o proveedor."
    }

    render view:'index',model:[businessEntityList:businessEntityList, businessEntityToAuthorize:businessEntityToAuthorize]
  }

  @Transactional
  def generateSubAccountStp(BusinessEntity businessEntity) {
    Company company = Company.get(session.company)
    businessEntityService.generateSubAccountStp(company, businessEntity)
    redirect action:"show", id:businessEntity.id
  }

  protected void notFound() {
    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'businessEntity.label', default: 'BusinessEntity'), params.id])
        redirect action: "index", method: "GET"
      }
      '*'{ render status: NOT_FOUND }
    }
  }

  def massiveRegistration() {
    [clientProviderType:LeadType.CLIENTE]
  }

  def downloadLayout() {
    log.info "Downloading layout for business entity of type ${params.clientProviderType}"
    def layout = businessEntityService.createLayoutForBusinessEntityType(params.clientProviderType)
    layout.with {
      setResponseHeaders(response, "layout${params.clientProviderType}.xlsx")
      save(response.outputStream)
    }
  }

  def downloadListForBusinessEntities(){
    def company = Company.findById(session.company.toLong())
    def xlsList = businessEntityService.exportXlsForBusinessRelationships(params.clientProviderType?:"All Entities", company)
    xlsList.with {
      setResponseHeaders(response, "${params.clientProviderType?:'Relaciones comerciales'}.xlsx")
      save(response.outputStream)
    }
  }

  def uploadMassiveRecords() {
    String entityType = params.entityType
    def file = request.getFile('massiveRecordsFile')
    Company company = Company.get(session.company)
    Map resultImport = businessEntityService."processXlsMassiveFor${entityType}"(file, company)
    render view:"massiveRegistrationResult", model:[resultImport:resultImport]
  }

  def showToAuthorizeEntities() {
    Company company = Company.get(session.company)
    def beToAuthorize = businessEntityService.getBusinessEntitiesToAuthorizeForCompany(company)
    [beToAuthorize:beToAuthorize]
  }

  def authorizeEntities() {
    log.info "Ids to authorize: ${params.entities}"
    if (params.entities) {
      businessEntityService.authorizeBusinessEntities(params.entities)
    }
    redirect action:"showToAuthorizeEntities"
  }

  @Transactional
  def inactive(BusinessEntity businessEntity) {
    businessEntityService.changeEnabledRelatedUserIfExists(businessEntity, false)
      businessEntity.status = BusinessEntityStatus.INACTIVE
      businessEntity.save()
      redirect action:"show", id:businessEntity.id
  }

  @Transactional
  def authorize(BusinessEntity businessEntity) {
    businessEntityService.changeEnabledRelatedUserIfExists(businessEntity, true)
      businessEntity.status = BusinessEntityStatus.ACTIVE
      businessEntity.save()
      redirect action:"show", id:businessEntity.id
  }

}
