package com.modulus.uno.paysheet

import com.modulus.uno.Company

class PaysheetController {

  PaysheetService paysheetService
  PaysheetEmployeeService paysheetEmployeeService
  PaysheetContractService paysheetContractService
  SimulatorPaysheetService simulatorPaysheetService
  List<PaysheetEmployee> paysheetEmployeeList = []

  def createFromPrePaysheet(PrePaysheet prePaysheet) {
    Paysheet paysheet = paysheetService.createPaysheetFromPrePaysheet(prePaysheet)
    redirect controller:"prePaysheet", action:"list"
  }

  def show(Paysheet paysheet) {
    respond paysheet, model:[baseUrlDocuments:grailsApplication.config.grails.url.base.images]
  }

  def list() {
    Company company = Company.get(session.company)
    List<PaysheetContract> paysheetContracts = paysheetContractService.getPaysheetContractsWithProjectsOfCompany(company)
    [paysheetContracts:paysheetContracts]
  }

  def listPaysheetsForPaysheetContract() {
    params.max = 25
    PaysheetContract paysheetContract = PaysheetContract.get(params.paysheetContractId)
    List<Paysheet> paysheetList = Paysheet.findAllByPaysheetContract(paysheetContract, params)
    Integer paysheetCount = Paysheet.countByPaysheetContract(paysheetContract)
    render view:"list", model:[client:paysheetContract.client, paysheetList:paysheetList, paysheetCount:paysheetCount]
  }

  def sendToAuthorize(Paysheet paysheet) {
    paysheetService.sendToAuthorize(paysheet)
    redirect action:"list"
  }

  def exportToXls(Paysheet paysheet) {
    log.info "Exporting to Xls the paysheet: ${paysheet.dump()}"
    def xls = paysheetService.exportPaysheetToXls(paysheet)
    xls.with {
      setResponseHeaders(response, "nomina-${paysheet.paysheetContract.client}-${paysheet.prePaysheet.paysheetProject}.xlsx")
      save(response.outputStream)
    }
  }

  def authorize(Paysheet paysheet) {
    paysheetService.authorize(paysheet)
    redirect action:"list"
  }

  def reject(Paysheet paysheet) {
    paysheetService.reject(paysheet)
    redirect action:"list"
  }

  def exportToXlsImss(Paysheet paysheet) {
    log.info "Exporting to Xls only Imss the paysheet: ${paysheet.dump()}"
    def xls = paysheetService.exportPaysheetToXlsImss(paysheet)
    xls.with {
      setResponseHeaders(response, "nominaIMSS-${paysheet.paysheetContract.client}-${paysheet.prePaysheet.paysheetProject}.xlsx")
      save(response.outputStream)
    }
  }

  def exportToXlsAssimilable(Paysheet paysheet) {
    log.info "Exporting to Xls only assimilable the paysheet: ${paysheet.dump()}"
    def xls = paysheetService.exportPaysheetToXlsAssimilable(paysheet)
    xls.with {
      setResponseHeaders(response, "nominaAsimilables-${paysheet.paysheetContract.client}-${paysheet.prePaysheet.paysheetProject}.xlsx")
      save(response.outputStream)
    }
  }

	def prepareDispersion(Paysheet paysheet){
		log.info "Preparing summary for dispersion from paysheet: ${paysheet.id}"
		List dispersionSummary = paysheetService.prepareDispersionSummary(paysheet)
		render view:"show", model:[paysheet:paysheet, dispersionSummary:dispersionSummary]
	}

  def generatePaymentDispersion(Paysheet paysheet) {
    log.info "Generating txt payments dispersion ${params} from paysheet ${paysheet.id}"
    paysheetService.generateDispersionFilesFromPaysheet(paysheet, params)
		redirect action:"show", id:paysheet.id
  }

  def exportToXlsCash(Paysheet paysheet) {
    log.info "Exporting to Xls only Cash the paysheet: ${paysheet.dump()}"
    def xls = paysheetService.exportPaysheetToXlsCash(paysheet)
    xls.with {
      setResponseHeaders(response, "nominaEfectivo-${paysheet.paysheetContract.client}-${paysheet.prePaysheet.paysheetProject}.xlsx")
      save(response.outputStream)
    }
  }

	def changePaymentWayFromEmployee(PaysheetEmployee employee) {
		paysheetEmployeeService.changePaymentWayFromEmployee(employee)
		redirect action:"show", id:employee.paysheet.id
	}

  def simulatorPaysheet(){
    render view: 'simulatorPaysheet'
  }

  def downloadLayout(){
    def layout = simulatorPaysheetService.generateLayoutForSimulator()
    layout.with {
      setResponseHeaders(response, "layoutSimuladorDeNomina.xlsx")
      save(response.outputStream)
    }
  }

  def uploadLayoutForSimulator(){
    def file = request.getFile('layoutSimulator')
    List resultList = simulatorPaysheetService.processXlsSimulator(file)
    render view:'simulatorPaysheet', model:[resultList:resultList]
  }

  def exportPaysheetEmployee(){
    def xlsForSimulator = simulatorPaysheetService.generateXLSForSimulator(paysheetEmployeeList)
    xlsForSimulator.with {
      setResponseHeaders(response, "XLSWithSimulator.xlsx")
      save(response.outputStream)
    }
  }
}
