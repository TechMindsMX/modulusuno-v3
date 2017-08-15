package com.modulus.uno.paysheet

import com.modulus.uno.Company

class Paysheet {

  PrePaysheet prePaysheet
  PaysheetStatus status = PaysheetStatus.CREATED
  String rejectReason

  Date dateCreated
  Date lastUpdated

  static belongsTo = [company:Company]
  static hasMany = [employees:PaysheetEmployee]

  static constraints = {
    rejectReason nullable:true
  }

  BigDecimal getTotal() {
    employees*.totalToInvoice.sum() ?: 0
  }
}
