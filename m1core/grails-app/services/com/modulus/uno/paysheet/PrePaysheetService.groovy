package com.modulus.uno.paysheet

import grails.transaction.Transactional
import java.math.RoundingMode
import pl.touk.excel.export.WebXlsxExporter
import java.text.SimpleDateFormat

import com.modulus.uno.BusinessEntityService
import com.modulus.uno.Company
import com.modulus.uno.BusinessEntity
import com.modulus.uno.DataImssEmployee
import com.modulus.uno.EmployeeLink
import com.modulus.uno.BankAccount

class PrePaysheetService {

  BusinessEntityService businessEntityService

  @Transactional
  PrePaysheet savePrePaysheet(PrePaysheet prePaysheet) {
    prePaysheet.save()
    log.info "Prepaysheet saved: ${prePaysheet.dump()}"
    prePaysheet
  }

  Map getListAndCountPrePaysheetsForCompany(Company company, Map params) {
    Map prePaysheets = [:]
    prePaysheets.list = PrePaysheet.findAllByCompany(company, params)
    prePaysheets.total = PrePaysheet.countByCompany(company)
    prePaysheets
  }

  List<BigDecimal> getNetPaymentForEmployees(List<BusinessEntity> beEmployees, PrePaysheet prePaysheet) {
    List<BigDecimal> netPayments = []
    int daysPeriod = prePaysheet.paymentPeriod.getDays()
    List dataImss = getDataImssForEmployees(beEmployees)
    dataImss.each { di ->
      BigDecimal netPayment = di ? (di.netMonthlySalary/30*daysPeriod).setScale(2, RoundingMode.HALF_UP) : new BigDecimal(0)
      netPayments.add(netPayment)
    }
    netPayments
  }

  List<DataImssEmployee> getDataImssForEmployees(List<BusinessEntity> beEmployees) {
    List<DataImssEmployee> dataImss = []
    beEmployees.each { be ->
      EmployeeLink employee = EmployeeLink.findByEmployeeRef(be.rfc)
      DataImssEmployee dataImssEmployee = DataImssEmployee.findByEmployee(employee)
      dataImss.add(dataImssEmployee)
    }
    dataImss
  }

  List<BusinessEntity> getEmployeesAvailableToAdd(PrePaysheet prePaysheet) {
    List<BusinessEntity> allActiveEmployeesForCompany = businessEntityService.getAllActiveEmployeesForCompany(prePaysheet.company)
    List<BusinessEntity> currentEmployees = obtainBusinessEntitiesFromEmployeesPrePaysheet(prePaysheet)
    allActiveEmployeesForCompany - currentEmployees
  }

  List<BusinessEntity> obtainBusinessEntitiesFromEmployeesPrePaysheet(prePaysheet) {
    List<BusinessEntity> beInPrePaysheet = []
    prePaysheet.employees.each { emp ->
      beInPrePaysheet.add(BusinessEntity.findByRfc(emp.rfc))
    }
    beInPrePaysheet.sort{ it.id }
  }

  @Transactional
  def addEmployeesToPrePaysheet(PrePaysheet prePaysheet, Map params) {
    List<BusinessEntity> employees = businessEntityService.getBusinessEntitiesFromIds(params.entities)
    employees.each { employee ->
      createAndSavePrePaysheetEmployee(employee, prePaysheet, params)
    }
    log.info "Employees in prePaysheet: ${prePaysheet.employees}"
  }

  PrePaysheet createAndSavePrePaysheetEmployee(BusinessEntity employee, PrePaysheet prePaysheet, Map params) {
    BankAccount bankAccount = params."bankAccount${employee.id}" ? BankAccount.get(params."bankAccount${employee.id}") : null
    PrePaysheetEmployee prePaysheetEmployee = new PrePaysheetEmployee(
      rfc:employee.rfc,
      curp:employee.curp,
      numberEmployee:employee.number?:"SN",
      nameEmployee:employee.toString(),
      netPayment:new BigDecimal(params."netPayment${employee.id}"),
      prePaysheet:prePaysheet,
      note:params."note${employee.id}",
      bank:bankAccount?.banco,
      clabe:bankAccount?.clabe,
      account:bankAccount?.accountNumber,
      cardNumber:bankAccount?.cardNumber
    )
    prePaysheetEmployee.save()
    prePaysheet.addToEmployees(prePaysheetEmployee)
    prePaysheet.save()
    log.info "PrePaysheetEmployee saved: ${prePaysheetEmployee?.dump()}"
    prePaysheet
  }

  @Transactional
  def sendPrePaysheetToProcess(PrePaysheet prePaysheet) {
    prePaysheet.status = PrePaysheetStatus.IN_PROCESS
    prePaysheet.save()
    //TODO: enviar notificación al ejecutivo de cuenta
  }

  def exportPrePaysheetToXls(PrePaysheet prePaysheet) {
    Map employees = getEmployeesToExport(prePaysheet)
    new WebXlsxExporter().with {
      fillRow(["PROYECTO:", prePaysheet.paysheetProject],0)
      fillRow(["PERIODO DE PAGO:", prePaysheet.paymentPeriod, "DEL:", new SimpleDateFormat("dd-MM-yyyy").format(prePaysheet.initPeriod), "AL:", new SimpleDateFormat("dd-MM-yyyy").format(prePaysheet.endPeriod)],1)
      fillRow(["RESIDENTE:", prePaysheet.accountExecutive],2)
      fillRow(employees.headers, 4)
      add(employees.data, employees.properties, 5)
    }
  }

  Map getEmployeesToExport(PrePaysheet prePaysheet) {
    Map employees = [:]
    employees.headers = ['RFC','CURP','NOMBRE','NO. EMPL.','CÓD. BANCO','BANCO','CLABE', 'CUENTA', 'TARJETA','TOTAL A PAGAR','OBSERVACIONES']
    employees.properties = ['rfc', 'curp', 'nameEmployee', 'numberEmployee', 'bank.bankingCode', 'bank.name', 'clabe', 'account', 'cardNumber', 'netPayment', 'note']
    employees.data = prePaysheet.employees.sort {it.nameEmployee}
    employees
  }

  @Transactional
  PrePaysheet changeStatusToProcessed(PrePaysheet prePaysheet) {
    prePaysheet.status = PrePaysheetStatus.PROCESSED
    prePaysheet.save()
    prePaysheet
  }

  @Transactional
  def deleteEmployeeFromPrePaysheet(PrePaysheetEmployee prePaysheetEmployee) {
    PrePaysheetEmployee.executeUpdate("delete PrePaysheetEmployee employee where employee.id = :id", [id: prePaysheetEmployee.id])
  }

}
