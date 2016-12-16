package com.modulus.uno
import grails.converters.JSON

class PurchaseOrder {

  String providerName
  PurchaseOrderStatus status = PurchaseOrderStatus.CREADA

  Date dateCreated
  Date lastUpdated
  Boolean isAnticipated = false
  // TODO: No sé si esto sea la solución o tenemos que crear otra clase de dominio
  Boolean isMoneyBackOrder = false
  BankAccount bankAccount
  RejectReason rejectReason
  String rejectComment

  static belongsTo = [company:Company]
  static hasMany = [documents:S3Asset, items:PurchaseOrderItem, authorizations:Authorization]

  Date fechaPago
  String externalId
  Date originalDate

  static constraints = {
    providerName blank:false, size:1..300
    bankAccount nullable:true
    rejectReason nullable:true
    rejectComment nullable:true, blank:true
    fechaPago nullable:false
    externalId nullable:true
    originalDate nullable:true
  }

  def getSubtotal(){
    items*.amountWithoutTaxes.sum() ?: 0
  }

  def getTaxes(){
    getSubtotal() * 0.16
  }

  BigDecimal getTotalIVA(){
    items*.appliedIVA.sum() ?: 0
  }

  BigDecimal getTotalIEPS() {
    items*.appliedIEPS.sum() ?: 0
  }

  BigDecimal getTotal(){
    getSubtotal() + getTotalIVA() + getTotalIEPS()
  }

  static marshaller = {
    JSON.registerObjectMarshaller(PurchaseOrder, 1) { m ->
      return [
      id: m.id,
      providerName: m.providerName,
      isAnticipated: m.isAnticipated,
      bankAccount: m.bankAccount,
      fechaPago: m.fechaPago,
      externalId: m.externalId,
      items:m.items,
      company: m.company
      ]
    }
  }
}
