package com.modulus.uno

import com.github.rahulsom.swaggydoc.*
import com.wordnik.swagger.annotations.*
import static org.springframework.http.HttpStatus.*

@Api
class PurchaseOrderController {

  static allowedMethods = [save: "POST", purchaseOrderByInvoice: "POST", delete: "DELETE",savePurchaseOrderItem :"POsT"]

  BusinessEntityService businessEntityService
  PurchaseOrderService purchaseOrderService

  def index() { }

  @SwaggySave(extraParams = [
      @ApiImplicitParam(name = 'companyId', value = '', required = true, dataType = 'number',paramType = 'form'),
      @ApiImplicitParam(name = 'providerId', value = '', required = true, dataType = 'string',paramType = 'form'),
      @ApiImplicitParam(name = 'bankAccountId', value = '', required = true, dataType = 'integer',paramType = 'form'),
      @ApiImplicitParam(name = 'fechaPago', value = 'dd/MM/yyyy', required = true, dataType = 'date',paramType = 'form'),
      @ApiImplicitParam(name = 'isAnticipated', value = '', required = false, dataType = 'string',paramType = 'form'),
      @ApiImplicitParam(name = 'externalId', value = '', required = true, dataType = 'string',paramType = 'form')
      ])
  def save() {
    params.isMoneyBackOrder = false
    if (params.isAnticipated == "true")
      params.orderType = "0"
    def purchaseOrder = purchaseOrderService.createPurchaseOrder(params.long("companyId"), params)

    if (params.isAnticipated == "true")
      purchaseOrderService.requestAuthorizationForTheOrder(purchaseOrder)

    if (purchaseOrder.id)
      respond purchaseOrder, status: 201, formats: ['json']
    else
      response.sendError(404,"The purchase order are not created, or need send more information")
  }

  @SwaggySave(extraParams = [
    @ApiImplicitParam(name = 'name', value = '', dataType = 'string',paramType = 'form'),
    @ApiImplicitParam(name = 'quantity', value = '', dataType = 'number',paramType = 'form'),
    @ApiImplicitParam(name = 'price', value = '', dataType = 'number',paramType = 'form'),
    @ApiImplicitParam(name = 'ieps', value = '', dataType = 'number',paramType = 'form'),
    @ApiImplicitParam(name = 'iva', value = '', dataType = 'number',paramType = 'form'),
    @ApiImplicitParam(name = 'unitType', value = 'UNIDADES,KILOGRAMOS,METROS,LITROS,HORAS,SERVICIO,PAQUETES,CAJA, PIEZA,TONELADAS,TAMBOS', dataType = 'string',paramType = 'form'),
    @ApiImplicitParam(name = 'purchaseOrderId', value = '', dataType = 'number',paramType = 'form'),
      ])
  def savePurchaseOrderItem(PurchaseOrderItemCommand command) {
    def purchaseOrder = PurchaseOrder.findById(params.purchaseOrderId)
    def purchaseOrderItem = command.createPurchaseOrderItem()
    purchaseOrderItem.purchaseOrder = purchaseOrder

    purchaseOrderItem.save flush:true

    if(purchaseOrderItem)
      respond purchaseOrder, status:201, formats: ['json']
    else
      response.senErrod(404, "Missing fields for item")

  }

  @SwaggySave(extraParams = [
      @ApiImplicitParam(name = 'companyId', value = '', dataType = 'number',paramType = 'form'),
      @ApiImplicitParam(name = 'providerId', value = '', dataType = 'string',paramType = 'form'),
      @ApiImplicitParam(name = 'bankAccountId', value = '', dataType = 'integer',paramType = 'form'),
      @ApiImplicitParam(name = 'file', value = '', dataType = 'file',paramType = 'form')
      ])
  def purchaseOrderByInvoice() {
    def invoice = params.file
    def responsePOST = purchaseOrderService.callExternalServiceForInvoice(invoice)
    def isAExistentProvirder = businessEntityService.findBusinessEntityAndProviderLinkByRFC(responsePOST.emisor.rfc)
    if (!isAExistentProvirder) {
      response.sendError(422, "Provider not exist in M1")
      return
    }
    def providerBankAccountAndAddress = validateBankAccountsAndAddressOfProvider(responsePOST.emisor.rfc)
    if (providerBankAccountAndAddress) {
       response.sendError(422, "Provider have not a Bank Account or Address")
       return
    }
    def purchaseOrderInvoice = purchaseOrderService.createPurchaseOrderByInvoice(responsePOST,params.companyId)
    def purchaseOrder = purchaseOrderService.createPurchaseOrderItemsByInvoice(responsePOST, purchaseOrderInvoice)
    purchaseOrderService.addInvoiceToPurchaseOrder(invoice, purchaseOrder.id, "invoiceForPO")
    respond purchaseOrder, status: 201, formats: ['json']
  }

  private def validateBankAccountsAndAddressOfProvider(String rfc) {
    def thisBusinessEntityHaveAAccount = businessEntityService.knowIfBusinessEntityHaveABankAccountOrAddress(rfc)
      if (thisBusinessEntityHaveAAccount.flatten().size() < 2)
        return true
  }

}
