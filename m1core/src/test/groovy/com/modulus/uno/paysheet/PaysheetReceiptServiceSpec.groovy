package com.modulus.uno.paysheet

import grails.test.mixin.TestFor
import grails.test.mixin.Mock
import spock.lang.Specification
import spock.lang.Unroll
import java.math.RoundingMode

import com.modulus.uno.invoice.paysheetReceipt.*
import com.modulus.uno.invoice.*

import com.modulus.uno.Company
import com.modulus.uno.BankAccount
import com.modulus.uno.Bank
import com.modulus.uno.DataImssEmployee
import com.modulus.uno.EmployeeLink
import com.modulus.uno.Address
import com.modulus.uno.AddressType
import com.modulus.uno.PaymentPeriod
import com.modulus.uno.PaysheetReceiptCommand
import com.modulus.uno.RestException

import com.modulus.uno.DataImssEmployeeService
import com.modulus.uno.RestService

@TestFor(PaysheetReceiptService)
@Mock([Paysheet, PrePaysheet, Company, PaysheetEmployee, PrePaysheetEmployee, BankAccount, Bank, PaysheetContract, PayerPaysheetProject, PaysheetProject, Address, EmployeeLink, DataImssEmployee, BreakdownPaymentEmployee, PrePaysheetEmployeeIncidence, ExtraHourIncidence])
class PaysheetReceiptServiceSpec extends Specification {

  PaysheetProjectService paysheetProjectService = Mock(PaysheetProjectService)
  DataImssEmployeeService dataImssEmployeeService = Mock(DataImssEmployeeService)
  RestService restService = Mock(RestService)
  PaysheetContract paysheetContract
  Company company

  def setup() {
    service.paysheetProjectService = paysheetProjectService
    service.dataImssEmployeeService = dataImssEmployeeService
    service.restService = restService
    paysheetContract = createPaysheetContract()
    company = createCompanyForContract()
    grailsApplication.config.modulus.paysheetReceiptCreate = "paysheetreceipt/create"
    grailsApplication.config.modulus.paysheetReceiptGeneratePdf = "paysheetreceipt/generatePdf"
  }

  private Company createCompanyForContract() {
    new Company(
      rfc:"RFC-CLIENT",
      bussinessName: "THE CLIENT CONTRACT"
    ).save(validate:false)
  }

  private PrePaysheetEmployee createPrePaysheetEmployee() {
    new PrePaysheetEmployee (
      rfc:"BUPA690824IRA",
      curp:"BUPA690824HDFRRL02",
      numberEmployee:"NUM-EMP",
      nameEmployee:"EMPLEADO MODULUS UNO",
      bank: new Bank(name:"THEBANK", bankingCode:"40072").save(validate:false),
      clabe: "012180000191120163",
      branch: "180",
      account: "00019112016",
      cardNumber: "1111222233334444",
      netPayment: new BigDecimal(16500),
      incidences: createIncidences()
    ).save(validate:false)
  }

  private List<PrePaysheetEmployeeIncidence> createIncidences() {
    [
      new PrePaysheetEmployeeIncidence(internalKey:"P010", description:"Premios por puntualidad", keyType:"010", type: IncidenceType.PERCEPTION, paymentSchema: PaymentSchema.IMSS, exemptAmount: new BigDecimal(100), taxedAmount: new BigDecimal(0)).save(validate:false),
      new PrePaysheetEmployeeIncidence(internalKey:"P019", description:"Horas extra", keyType:"019", type: IncidenceType.PERCEPTION, paymentSchema: PaymentSchema.IMSS, exemptAmount: new BigDecimal(0), taxedAmount: new BigDecimal(0), extraHourIncidence: new ExtraHourIncidence(days:1, type:"01", quantity:2, amount: new BigDecimal(43.93)).save(validate:false)).save(validate:false),
      new PrePaysheetEmployeeIncidence(internalKey:"P004", description:"Reembolso de Gastos Médicos Dentales y Hospitalarios", keyType:"004", type: IncidenceType.OTHER_PERCEPTION, paymentSchema: PaymentSchema.IMSS, exemptAmount: new BigDecimal(500), taxedAmount: new BigDecimal(0)).save(validate:false),
      new PrePaysheetEmployeeIncidence(internalKey:"P036", description:"Ayuda para transporte", keyType:"036", type: IncidenceType.OTHER_PERCEPTION, paymentSchema: PaymentSchema.ASSIMILABLE, exemptAmount: new BigDecimal(400), taxedAmount: new BigDecimal(0)).save(validate:false),
      new PrePaysheetEmployeeIncidence(internalKey:"P012", description:"Seguro de Gastos Médicos Mayores", keyType:"012", type: IncidenceType.PERCEPTION, paymentSchema: PaymentSchema.ASSIMILABLE, exemptAmount: new BigDecimal(1000), taxedAmount: new BigDecimal(0)).save(validate:false),
      new PrePaysheetEmployeeIncidence(internalKey:"D007", description:"Pensión alimenticia", keyType:"007", type: IncidenceType.DEDUCTION, paymentSchema: PaymentSchema.IMSS, exemptAmount: new BigDecimal(300), taxedAmount: new BigDecimal(0)).save(validate:false),
      new PrePaysheetEmployeeIncidence(internalKey:"D013", description:"Pagos hechos con exceso al trabajador", keyType:"013", type: IncidenceType.DEDUCTION, paymentSchema: PaymentSchema.ASSIMILABLE, exemptAmount: new BigDecimal(1000), taxedAmount: new BigDecimal(0)).save(validate:false)
    ]
  }

  private PaysheetEmployee createPaysheetEmployee() {
    new PaysheetEmployee (
      paysheet: createPaysheet(),
      prePaysheetEmployee: createPrePaysheetEmployee(),
      breakdownPayment: new BreakdownPaymentEmployee(baseQuotation: new BigDecimal(2670.64), integratedDailySalary: new BigDecimal(87.85)).save(false),
      salaryImss: new BigDecimal(2500),
      socialQuota: new BigDecimal(63.44),
      subsidySalary: new BigDecimal(162.44),
      incomeTax: new BigDecimal(166.57),
      crudeAssimilable: new BigDecimal(17468.60),
      incomeTaxAssimilable: new BigDecimal(3401.03),
      netAssimilable: new BigDecimal(14067.57),
      socialQuotaEmployer: new BigDecimal(633.62),
      paysheetTax: new BigDecimal(75.00),
      commission: new BigDecimal(516.26),
      ivaRate: new BigDecimal(16)
    ).save(validate:false)
  }

  private PaysheetContract createPaysheetContract() {
    new PaysheetContract (
      employerRegistration:"REGPATRONAL",
      folio: new Integer(0),
      serie: "TESTNOM",
      company: company
    ).save(validate:false)
  }

  private Paysheet createPaysheet() {
    new Paysheet(
      paysheetContract: paysheetContract,
      prePaysheet: new PrePaysheet(paysheetProject:"TESTPROJECT").save(validate:false)
    ).save(validate:false)
  }

  private PaysheetProject createPaysheetProject() {
    new PaysheetProject(
      name: "TESTPROJECT",
      paysheetContract: paysheetContract,
      federalEntity: "DIF",
      integrationFactor: new BigDecimal(1.0542),
      payers: createPayersPaysheetProject()
    ).save(validate:false)
  }

  private List<PayerPaysheetProject> createPayersPaysheetProject() {
    Address address = new Address(street:"BOSQUES DE DURAZNOS", streetNumber:"140", zipCode:"11700", colony:"BOSQUES DE LAS LOMAS", country:"MEXICO", city:"MIGUEL HIDALGO", federalEntity:"CIUDAD DE MEXICO", addressType:AddressType.FISCAL).save(validate:false)
    [
      new PayerPaysheetProject(company:new Company(bussinessName:"SA-PAYER", rfc:"AAA010101AAA", addresses:[address]).save(validate:false), paymentSchema:PaymentSchema.IMSS).save(validate:false),
      new PayerPaysheetProject(company:new Company(bussinessName:"IAS-PAYER", rfc:"AAA010101AAA", addresses:[address]).save(validate:false), paymentSchema:PaymentSchema.ASSIMILABLE).save(validate:false)
    ]
  }

  private EmployeeLink createEmployeeLink() {
    new EmployeeLink (
      employeeRef: "BUPA690824IRA",
      company: company
    ).save(validate:false)
  }

  private DataImssEmployee createDataImssEmployeeForEmployee(EmployeeLink employee) {
    new DataImssEmployee (
      employee:employee,
      nss:"NSS-EMPLOYEE",
      registrationDate: Date.parse("dd-MM-yyyy", "01-01-2018"),
      dischargeDate: Date.parse("dd-MM-yyyy", "01-02-2018"),
      baseImssMonthlySalary: new BigDecimal(5000),
      totalMonthlySalary: new BigDecimal(33000),
      holidayBonusRate: new BigDecimal(25),
      annualBonusDays: new Integer(15),
      paymentPeriod: PaymentPeriod.BIWEEKLY,
      contractType: ContractType.UNDEFINED,
      regimeType: RegimeType.SALARIES,
      workDayType: WorkDayType.DIURNAL,
      jobRisk: JobRisk.CLASS_01,
      department: "ADMINISTRACION",
      job: "AUXILIAR ADMINISTRATIVO"
    ).save(validate:false)
  }

  void "Should create the paysheet receipt invoice data from paysheet employee"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
    when:
      def invoiceData = service.createInvoiceDataFromPaysheetEmployee(paysheetEmployee)
    then:
      invoiceData.folio == "1"
      invoiceData.serie == "TESTNOM"
  }

  @Unroll
  void "Should create the paysheet receipt emitter from paysheet employee and schema = #theSchema"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
    and:"The schema"
      PaymentSchema schema = theSchema
    and:
      paysheetProjectService.getPaysheetProjectByPaysheetContractAndName(_, _) >> createPaysheetProject()
    when:
      def emitter = service.createEmitterFromPaysheetEmployeeAndSchema(paysheetEmployee, schema)
    then:
      emitter.registroPatronal == "REGPATRONAL"
      emitter.datosFiscales.razonSocial == theBusinessName
      emitter.datosFiscales.rfc == theRfc
      emitter.datosFiscales.codigoPostal == theZipCode
    where:
      theSchema     || theBusinessName  | theRfc  | theZipCode
      PaymentSchema.IMSS  || "SA-PAYER" | "AAA010101AAA" | "11700"
      PaymentSchema.ASSIMILABLE  || "IAS-PAYER" | "AAA010101AAA" | "11700"
  }

  @Unroll
  void "Should create the paysheet receipt receiver from paysheet employee and schema = #theSchema"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
    and:"The schema"
      PaymentSchema schema = theSchema
    and:
      paysheetProjectService.getPaysheetProjectByPaysheetContractAndName(_, _) >> createPaysheetProject()
      dataImssEmployeeService.calculateLaborOldInSATFormat(_) >> "P1M"
    and:
      EmployeeLink employeeLink = createEmployeeLink()
      EmployeeLink.metaClass.static.findByCompanyAndEmployeeRef = { employeeLink }
      DataImssEmployee.metaClass.static.findByEmployee = { createDataImssEmployeeForEmployee(employeeLink) }
    when:
      def receiver = service.createReceiverFromPaysheetEmployeeAndSchema(paysheetEmployee, schema)
    then:
      receiver.rfc == "BUPA690824IRA"
      receiver.nombre == "EMPLEADO MODULUS UNO"
      receiver.curp == "BUPA690824HDFRRL02"
      receiver.datosBancarios.banco == "072"
      receiver.datosBancarios.cuenta == "00019112016"
      receiver.datosLaborales.tipoContrato == theContractType
      receiver.datosLaborales.tipoRegimen == theRegimeType
    where:
      theSchema                   ||    theContractType           |   theRegimeType
      PaymentSchema.IMSS          || ContractType.UNDEFINED.key   |   RegimeType.SALARIES.key
      PaymentSchema.ASSIMILABLE   || ContractType.WORK_WITHOUT_RELATION.key  | RegimeType.FEES_ASSIMILATED.key
  }

  @Unroll
  void "Should create perceptions from paysheet employee for schema = #theSchema"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
    and:"The schema"
      PaymentSchema schema = theSchema
    when:
      def perceptions = service.createPerceptionsFromPaysheetEmployeeAndSchema(paysheetEmployee, schema)
    then:
      perceptions.detalles.size() == totalPerceptions
      perceptions.detalles.tipo.sort() == listKeys.sort()
    where:
      theSchema                   ||    totalPerceptions    |  listKeys
      PaymentSchema.IMSS          ||    3                   |   ["001","010", "019"]
      PaymentSchema.ASSIMILABLE   ||    1                   |   ["046"] 
  }

  @Unroll
  void "Should create deductions from paysheet employee for schema = #theSchema"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
    and:"The schema"
      PaymentSchema schema = theSchema
    when:
      def deductions = service.createDeductionsFromPaysheetEmployeeAndSchema(paysheetEmployee, schema)
    then:
      deductions.detalles.size() == totalDeductions
      deductions.detalles.tipo.sort() == listKeys.sort()
    where:
      theSchema                   ||    totalDeductions    |  listKeys
      PaymentSchema.IMSS          ||    3                   |   ["001","002","007"]
      PaymentSchema.ASSIMILABLE   ||    1                   |   ["002"] 
  }

  @Unroll
  void "Should create other perceptions from paysheet employee for schema = #theSchema"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
    and:"The schema"
      PaymentSchema schema = theSchema
    when:
      def otherPerceptions = service.createOtherPerceptionsFromPaysheetEmployeeAndSchema(paysheetEmployee, schema)
    then:
      otherPerceptions.size() == totalPerceptions
      otherPerceptions.tipo.sort() == listKeys.sort()
    where:
      theSchema                   ||    totalPerceptions    |  listKeys
      PaymentSchema.IMSS          ||    1                   |   ["004"]
      PaymentSchema.ASSIMILABLE   ||    0                   |   [] 
  }

  @Unroll
  void "Should create perceptions from paysheet employee without incidences for schema = #theSchema"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
      paysheetEmployee.prePaysheetEmployee.incidences = []
    and:"The schema"
      PaymentSchema schema = theSchema
    when:
      def perceptions = service.createPerceptionsFromPaysheetEmployeeAndSchema(paysheetEmployee, schema)
    then:
      perceptions.detalles.size() == totalPerceptions
      perceptions.detalles.tipo.sort() == listKeys.sort()
    where:
      theSchema                   ||    totalPerceptions    |  listKeys
      PaymentSchema.IMSS          ||    1                   |   ["001"]
      PaymentSchema.ASSIMILABLE   ||    1                   |   ["046"] 
  }

  @Unroll
  void "Should create deductions from paysheet employee without incidences for schema = #theSchema"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
      paysheetEmployee.prePaysheetEmployee.incidences = []
    and:"The schema"
      PaymentSchema schema = theSchema
    when:
      def deductions = service.createDeductionsFromPaysheetEmployeeAndSchema(paysheetEmployee, schema)
    then:
      deductions.detalles.size() == totalDeductions
      deductions.detalles.tipo.sort() == listKeys.sort()
    where:
      theSchema                   ||    totalDeductions    |  listKeys
      PaymentSchema.IMSS          ||    2                   |   ["001","002"]
      PaymentSchema.ASSIMILABLE   ||    1                   |   ["002"] 
  }

  @Unroll
  void "Should create other perceptions from paysheet employee without incidences for schema = #theSchema"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
      paysheetEmployee.prePaysheetEmployee.incidences = []
    and:"The schema"
      PaymentSchema schema = theSchema
    when:
      def otherPerceptions = service.createOtherPerceptionsFromPaysheetEmployeeAndSchema(paysheetEmployee, schema)
    then:
      otherPerceptions.size() == totalPerceptions
    where:
      theSchema                   ||    totalPerceptions
      PaymentSchema.IMSS          ||    0               
      PaymentSchema.ASSIMILABLE   ||    0               
  }

  void "Should stamp a paysheet receipt" () {
    given:"The paysheet receipt"
      PaysheetReceiptCommand paysheetReceipt = new PaysheetReceiptCommand()
    and:
      restService.sendFacturaCommandWithAuth(_, _) >> [text:"UUID_PAYSHEET_RECEIPT"]
    when:
      def result = service.stampPaysheetReceipt(paysheetReceipt)
    then:
      result == "UUID_PAYSHEET_RECEIPT"
  }

  void "Should thrown an exception when try stamp a paysheet receipt when result starts with 'Error'" () {
    given:"The paysheet receipt"
      PaysheetReceiptCommand paysheetReceipt = new PaysheetReceiptCommand()
    and:
      restService.sendFacturaCommandWithAuth(_, _) >> [text:"Error:falló el timbrado del recibo"]
    when:
      def result = service.stampPaysheetReceipt(paysheetReceipt)
    then:
      thrown RestException
  }

  void "Should thrown an exception when try stamp a paysheet receipt when result is null" () {
    given:"The paysheet receipt"
      PaysheetReceiptCommand paysheetReceipt = new PaysheetReceiptCommand()
    and:
      restService.sendFacturaCommandWithAuth(_, _) >> null
    when:
      def result = service.stampPaysheetReceipt(paysheetReceipt)
    then:
      thrown RestException
  }

  void "Should build data bank with bank and account when paysheet employee has account"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
    when:
      def result = service.buildDataBankForEmployee(paysheetEmployee)
    then:
      result.banco && result.cuenta
      result.cuenta == "00019112016"
  }

  void "Should build data bank with bank and card number  when paysheet employee hasn't account"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
      paysheetEmployee.prePaysheetEmployee.account = ""
      paysheetEmployee.save(validate:false)
    when:
      def result = service.buildDataBankForEmployee(paysheetEmployee)
    then:
      result.banco && result.cuenta
      result.cuenta == "1111222233334444"
  }

  void "Should get the pdf file when send to generate it for a paysheet receipt" () {
    given:"The paysheet receipt"
      PaysheetReceiptCommand paysheetReceipt = new PaysheetReceiptCommand()
    and:
      restService.sendFacturaCommandWithAuth(_, _) >> "Ok"
    when:
      def result = service.sendToGeneratePdfFromPaysheetReceipt(paysheetReceipt)
    then:
      result == "Ok"
  }

  void "Should throw exception when send to generate pdf file for a paysheet receipt" () {
    given:"The paysheet receipt"
      PaysheetReceiptCommand paysheetReceipt = new PaysheetReceiptCommand()
    and:
      restService.sendFacturaCommandWithAuth(_, _) >> null
    when:
      def result = service.sendToGeneratePdfFromPaysheetReceipt(paysheetReceipt)
    then:
      thrown RestException
  }

  void "Should create salary detail for schema IMSS without incidences"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
      paysheetEmployee.prePaysheetEmployee.incidences = []
    when:
      def salaryDetail = service.createSalaryDetailForSchemaIMSS(paysheetEmployee)
    then:
      salaryDetail.importeGravado == new BigDecimal(2500)
      salaryDetail.clave == "P001"
  }

  void "Should create salary detail for schema IMSS wit incidences but not include type P001 incidence"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
    when:
      def salaryDetail = service.createSalaryDetailForSchemaIMSS(paysheetEmployee)
    then:
      salaryDetail.importeGravado == new BigDecimal(2500)
      salaryDetail.clave == "P001"
  }

  void "Should create salary detail for schema IMSS wit incidences and include type P001 incidence"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
      paysheetEmployee.prePaysheetEmployee.incidences.add(new PrePaysheetEmployeeIncidence(internalKey:"P001", description:"Sueldos, Salarios  Rayas y Jornales", keyType:"001", type: IncidenceType.PERCEPTION, paymentSchema: PaymentSchema.IMSS, exemptAmount: new BigDecimal(100), taxedAmount: new BigDecimal(0)).save(validate:false))
    when:
      def salaryDetail = service.createSalaryDetailForSchemaIMSS(paysheetEmployee)
    then:
      salaryDetail.importeGravado == new BigDecimal(2600)
      salaryDetail.clave == "P001"
  }

  void "Should create salary detail for schema ASSIMILABLE without incidences"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
      paysheetEmployee.prePaysheetEmployee.incidences = []
    when:
      def salaryDetail = service.createSalaryDetailForSchemaASSIMILABLE(paysheetEmployee)
    then:
      salaryDetail.importeGravado == new BigDecimal(17468.60)
      salaryDetail.clave == "P046"
  }

  void "Should create salary detail for schema IMSS wit incidences but not include type P046 incidence"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
    when:
      def salaryDetail = service.createSalaryDetailForSchemaASSIMILABLE(paysheetEmployee)
    then:
      salaryDetail.importeGravado == new BigDecimal(17468.60)
      salaryDetail.clave == "P046"
  }

  void "Should create salary detail for schema IMSS wit incidences and include type P046 incidence"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
      paysheetEmployee.prePaysheetEmployee.incidences.add(new PrePaysheetEmployeeIncidence(internalKey:"P046", description:"Ingresos asimilados a salarios", keyType:"046", type: IncidenceType.PERCEPTION, paymentSchema: PaymentSchema.ASSIMILABLE, exemptAmount: new BigDecimal(100), taxedAmount: new BigDecimal(0)).save(validate:false))
    when:
      def salaryDetail = service.createSalaryDetailForSchemaASSIMILABLE(paysheetEmployee)
    then:
      salaryDetail.importeGravado == new BigDecimal(17568.60)
      salaryDetail.clave == "P046"
  }

  void "Should create deductions detail for schema IMSS without incidences"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
      paysheetEmployee.prePaysheetEmployee.incidences = []
    when:
      def deductionsDetail = service.createDeductionDetailForSchemaIMSS(paysheetEmployee)
    then:
      deductionsDetail.first().importeGravado.setScale(2, RoundingMode.HALF_UP) == new BigDecimal(63.44).setScale(2, RoundingMode.HALF_UP)
      deductionsDetail.first().clave == "D001"
      deductionsDetail.last().importeGravado.setScale(2, RoundingMode.HALF_UP) == new BigDecimal(4.13).setScale(2, RoundingMode.HALF_UP)
      deductionsDetail.last().clave == "D002"
  }

  void "Should create deductions detail for schema IMSS wit incidences but not include types D001 and D002 incidences"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
    when:
      def deductionsDetail = service.createDeductionDetailForSchemaIMSS(paysheetEmployee)
    then:
      deductionsDetail.first().importeGravado.setScale(2, RoundingMode.HALF_UP) == new BigDecimal(63.44).setScale(2, RoundingMode.HALF_UP)
      deductionsDetail.first().clave == "D001"
      deductionsDetail.last().importeGravado.setScale(2, RoundingMode.HALF_UP) == new BigDecimal(4.13).setScale(2, RoundingMode.HALF_UP)
      deductionsDetail.last().clave == "D002"
  }

  void "Should create deductions detail for schema IMSS wit incidences and include types D001 and D002 incidences"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
      paysheetEmployee.prePaysheetEmployee.incidences.add(new PrePaysheetEmployeeIncidence(internalKey:"D001", description:"Seguridad social", keyType:"001", type: IncidenceType.DEDUCTION, paymentSchema: PaymentSchema.IMSS, exemptAmount: new BigDecimal(100), taxedAmount: new BigDecimal(0)).save(validate:false))
      paysheetEmployee.prePaysheetEmployee.incidences.add(new PrePaysheetEmployeeIncidence(internalKey:"D002", description:"ISR", keyType:"002", type: IncidenceType.DEDUCTION, paymentSchema: PaymentSchema.IMSS, exemptAmount: new BigDecimal(100), taxedAmount: new BigDecimal(0)).save(validate:false))
    when:
      def deductionsDetail = service.createDeductionDetailForSchemaIMSS(paysheetEmployee)
    then:
      deductionsDetail.first().importeGravado.setScale(2, RoundingMode.HALF_UP) == new BigDecimal(163.44).setScale(2, RoundingMode.HALF_UP)
      deductionsDetail.first().clave == "D001"
      deductionsDetail.last().importeGravado.setScale(2, RoundingMode.HALF_UP) == new BigDecimal(104.13).setScale(2, RoundingMode.HALF_UP)
      deductionsDetail.last().clave == "D002"
  }

  void "Should create deductions detail for schema ASSIMILABLE without incidences"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
      paysheetEmployee.prePaysheetEmployee.incidences = []
    when:
      def deductionsDetail = service.createDeductionDetailForSchemaASSIMILABLE(paysheetEmployee)
    then:
      deductionsDetail.first().importeGravado.setScale(2, RoundingMode.HALF_UP) == new BigDecimal(3401.03).setScale(2, RoundingMode.HALF_UP)
      deductionsDetail.first().clave == "D002"
  }

  void "Should create deductions detail for schema ASSIMILABLE wit incidences but not include type D002 incidence"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
    when:
      def deductionsDetail = service.createDeductionDetailForSchemaASSIMILABLE(paysheetEmployee)
    then:
      deductionsDetail.first().importeGravado.setScale(2, RoundingMode.HALF_UP) == new BigDecimal(3401.03).setScale(2, RoundingMode.HALF_UP)
      deductionsDetail.first().clave == "D002"
  }

  void "Should create deductions detail for schema ASSIMILABLE wit incidences and include type D002 incidence"() {
    given:"The paysheet employee"
      PaysheetEmployee paysheetEmployee = createPaysheetEmployee()
      paysheetEmployee.prePaysheetEmployee.incidences.add(new PrePaysheetEmployeeIncidence(internalKey:"D002", description:"ISR", keyType:"002", type: IncidenceType.DEDUCTION, paymentSchema: PaymentSchema.ASSIMILABLE, exemptAmount: new BigDecimal(100), taxedAmount: new BigDecimal(0)).save(validate:false))
    when:
      def deductionsDetail = service.createDeductionDetailForSchemaASSIMILABLE(paysheetEmployee)
    then:
      deductionsDetail.first().importeGravado.setScale(2, RoundingMode.HALF_UP) == new BigDecimal(3501.03).setScale(2, RoundingMode.HALF_UP)
      deductionsDetail.first().clave == "D002"
  }


}
