package com.modulus.uno.paysheet

import grails.validation.Validateable
import com.modulus.uno.BusinessEntity
import com.modulus.uno.User
import com.modulus.uno.Company

class PaysheetContractCommand implements Validateable {
  
  String clientId
  String initDate
  String executiveId
  String companyId

  static constraints = {
    clientId nullable:false
    initDate nullable:false
    executiveId nullable:false
    companyId nullable:false
  }

  PaysheetContract createPaysheetContract() {
    new PaysheetContract(
      client:BusinessEntity.get(this.clientId),
      initDate:Date.parse("dd/MM/yyyy", this.initDate),
      executive:User.get(this.executiveId),
      company:Company.get(this.companyId)
    )
  }
}