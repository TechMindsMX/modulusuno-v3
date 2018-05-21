package com.modulus.uno.saleorder

import grails.transaction.Transactional
import com.modulus.uno.Authorization
import com.modulus.uno.EmailSenderService
import com.modulus.uno.status.CreditNoteStatus
import com.modulus.uno.invoice.Concepto
import com.modulus.uno.invoice.MetodoDePago
import com.modulus.uno.invoice.FormaDePago
import com.modulus.uno.RestService
import com.modulus.uno.RestException

class CreditNoteService {
  
  def springSecurityService
  def grailsApplication
  EmailSenderService emailSenderService
  InvoiceService invoiceService
  RestService restService

  @Transactional
  CreditNote saveCreditNote(CreditNote creditNote) {
    creditNote.save()
    creditNote
  }

  @Transactional
  CreditNote sendToAuthorize(CreditNote creditNote) {
    creditNote.status = CreditNoteStatus.TO_AUTHORIZE
    creditNote.save()
    emailSenderService.notifyCreditNoteChangeStatus(creditNote)
    creditNote
  }

  @Transactional
  CreditNote processAuthorization(CreditNote creditNote) {
    addAuthorizationToCreditNote(creditNote)
    if (isFullAuthorized(creditNote)) {
      authorizeCreditNote(creditNote)
    }
    creditNote
  }

  @Transactional
  def addAuthorizationToCreditNote(CreditNote creditNote) {
    Authorization authorization = new Authorization(user:springSecurityService.currentUser).save()
    creditNote.addToAuthorizations(authorization)
    creditNote.save()
  }

  Boolean isFullAuthorized (CreditNote creditNote) {
    def alreadyAuthorizations = creditNote.authorizations ? creditNote.authorizations.size() : 0
    alreadyAuthorizations >= creditNote.saleOrder.company.numberOfAuthorizations
  }

  @Transactional
  CreditNote authorizeCreditNote(CreditNote creditNote) {
    creditNote.status = CreditNoteStatus.AUTHORIZED
    creditNote.save()
    emailSenderService.notifyCreditNoteChangeStatus(creditNote)
    creditNote
  }

  CreditNote processApplyCreditNote(CreditNote creditNote) {
    FacturaCommand creditNoteCommand = invoiceService.createInvoiceFromSaleOrder(creditNote.saleOrder)
    creditNoteCommand = defineDataFromCreditNote(creditNote, creditNoteCommand)
    String folioStamp = sendToStampCreditNote(creditNoteCommand)
    creditNote = applyCreditNoteWithFolio(creditNote, folioStamp)
    creditNote
  }

  FacturaCommand defineDataFromCreditNote(CreditNote creditNote, FacturaCommand creditNoteCommand) {
    creditNoteCommand.id = creditNote.saleOrder.company.id
    creditNoteCommand.observaciones = ""
    creditNoteCommand.datosDeFacturacion.tipoDeComprobante = "E"
    creditNoteCommand.datosDeFacturacion.metodoDePago = new MetodoDePago(clave:creditNote.paymentMethod.name(), descripcion:creditNote.paymentMethod.description)
    creditNoteCommand.datosDeFacturacion.formaDePago = new FormaDePago(clave:creditNote.paymentWay.key, descripcion:creditNote.paymentWay.description)
    creditNoteCommand.datosDeFacturacion.tipoRelacion = "01"
    creditNoteCommand.datosDeFacturacion.cfdiRelacionado = creditNote.saleOrder.folio.substring(0,36)
    creditNoteCommand.receptor.datosFiscales.usoCFDI = creditNote.invoicePurpose.name()
    creditNoteCommand.conceptos = buildConceptsFromCreditNote(creditNote)
    creditNoteCommand.totalesImpuestos = invoiceService.buildSummaryTaxes(creditNoteCommand)
    creditNoteCommand
  }

  private List<Concepto> buildConceptsFromCreditNote(CreditNote creditNote) {
    def conceptos = []
    creditNote.items.toList().sort{it.name}.each { item ->
      Concepto concepto = new Concepto(
        cantidad:item.quantity, 
        valorUnitario:item.price, 
        descuento:item.appliedDiscount, 
        claveProd:item.satKey,
        descripcion:item.name, 
        unidad:item.unitType,
        claveUnidad:invoiceService.getUnitKeyFromItem(item.creditNote.saleOrder.company, item),
        impuestos:invoiceService.buildTaxesFromItem(item),
        retenciones:invoiceService.buildTaxWithholdingsFromItem(item)
      )
      conceptos.add(concepto)
    }
    conceptos
  }

  def sendToStampCreditNote(FacturaCommand creditNoteCommand) {
    def result = restService.sendFacturaCommandWithAuth(creditNoteCommand, grailsApplication.config.modulus.facturaCreate)
    if (!result) {
      throw new RestException("No se pudo generar la Nota de Crédito") 
    }
    if (result.text.startsWith("Error")) {
      throw new RestException(result.text) 
    }
    result.text 
  }

  @Transactional
  CreditNote applyCreditNoteWithFolio(CreditNote creditNote, String folioStamp) {
    creditNote.status = CreditNoteStatus.APPLIED
    creditNote.folio = folioStamp
    creditNote.save()
    emailSenderService.notifyCreditNoteChangeStatus(creditNote)
    creditNote
  }

}
