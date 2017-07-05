package com.modulus.uno

class DataImssEmployee {

  EmployeeLink employee
  Date registrationDate
  BigDecimal baseImssMonthlySalary
  BigDecimal netMonthlySalary
  BigDecimal holidayBonusRate
  Integer annualBonusDays
  PaymentPeriod paymentPeriod = PaymentPeriod.WEEKLY

  static constraints = {
    employee nullable:false
    imssSalary nullable:false, min:0.0
    assimilableSalary nullable:false, min:0.0
    holidayBonusRate nullable:false, min:0.0, max:100.0
    annualBonusDays nullable:false, min:15
    paymentPeriod nullable:false
  }

  /*
  //BigDecimal assimilableMonthlySalary (calculado) //diferencia del neto a pagar en el periodo de pago menos el salario imss calculado del periodo de pago
  //getIntegratedDailySalary salario base mensual / 30 * factor de integración 1.05452 (definido en los datos imss de la empresa)

  Integer getAntiquityInWeeks() {
  }

  Integer getAntiquityInDays() {
  }

  String getAntiquityInYearsMonthsDays() {
  }
  */
}
