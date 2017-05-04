package com.modulus.uno

import java.text.*
import grails.validation.Validateable

class SaleOrderCommand implements Validateable {

  String addressId
  String companyId
  String clientId
  String currencyUsd = "MXN"
  String changeType
  String fechaCobro
  String note
  String paymentMethod
  String externalId

  static constraints = {
    currencyUsd blank:false, size:3..3, validator: { val -> val in ["MXN","USD"] }
    changeType nullable:true
    externalId nullable:true
  }

  SaleOrder createOrUpdateSaleOrder() {
    Company company = Company.get(this.companyId)
    BusinessEntity businessEntity = BusinessEntity.get(this.clientId)
    Address address = Address.get(this.addressId)

    SaleOrder saleOrder
    if (this.externalId) {
      saleOrder = SaleOrder.findByCompanyAndExternalIdAndStatusInList(company, this.externalId, [SaleOrderStatus.CREADA, SaleOrderStatus.POR_AUTORIZAR])
    }

    PaymentMethod paymentMethod
    if (this.paymentMethod == "0" || this.paymentMethod == "1" || this.paymentMethod == "2" || this.paymentMethod == "3") {
      paymentMethod = PaymentMethod.find { it.ordinal().toString() == this.paymentMethod }
    } else {
      paymentMethod = PaymentMethod.find { it.toString() == this.paymentMethod }
    }

    if (saleOrder) {
       saleOrder.rfc = businessEntity.rfc
       saleOrder.clientName =  businessEntity.toString()
       saleOrder.note = this.note
       saleOrder.paymentMethod = paymentMethod
       saleOrder.fechaCobro = Date.parse("dd/MM/yyyy", this.fechaCobro)
       saleOrder.currency = this.currencyUsd
       saleOrder.changeType = getValueInBigDecimal(this.changeType ?: "0")
    } else {
      saleOrder = new SaleOrder(
        rfc:businessEntity.rfc,
        clientName: businessEntity.toString(),
        company:company,
        externalId:this.externalId,
        note:this.note,
        paymentMethod:paymentMethod,
        status:SaleOrderStatus.CREADA,
        fechaCobro:Date.parse("dd/MM/yyyy", this.fechaCobro),
        currency:this.currencyUsd,
        changeType:getValueInBigDecimal(this.changeType ?: "0")
      )
      saleOrder.addToAddresses(address)
    }
    saleOrder
  }

  private def getValueInBigDecimal(String value) {
    Locale.setDefault(new Locale("es","MX"));
    DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
    df.setParseBigDecimal(true);
    BigDecimal bd = (BigDecimal) df.parse(value);
    bd
  }

}
