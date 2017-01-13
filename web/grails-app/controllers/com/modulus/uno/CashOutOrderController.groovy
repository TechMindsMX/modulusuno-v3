package com.modulus.uno

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class CashOutOrderController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def springSecurityService
    def cashOutOrderService
    def companyService

    @Transactional
    def authorizeCashOutOrder(CashOutOrder cashOutOrder) {
      def user = springSecurityService.currentUser
      cashOutOrderService.addAutorizationToCashoutOrder(cashOutOrder,user)
      def isAvailableThisCashoutOrderForAuthorize = cashOutOrderService.isAvailableForAuthorize(cashOutOrder)
      if (isAvailableThisCashoutOrderForAuthorize) {
        cashOutOrder.status = CashOutOrderStatus.AUTHORIZED
        cashOutOrder.save()
      } else {
        flash.message = message(code: 'cashOutOrder.unauthorize')
      }
      redirect action:'list', params:[status:'TO_AUTHORIZED']
    }

    @Transactional
    def cancelCashOutOrder(CashOutOrder cashOutOrder) {
      cashOutOrder.status = CashOutOrderStatus.CANCELED
      cashOutOrder.save()
      redirect action:'list', params:[status:'TO_AUTHORIZED']
    }

    @Transactional
    def rejectCashOutOrder(CashOutOrder cashOutOrder) {
      cashOutOrder.status = CashOutOrderStatus.REJECTED
      cashOutOrder.save()
      redirect action:'list', params:[status:'AUTHORIZED']
    }

    @Transactional
    def executeCashOutOrder(CashOutOrder cashOutOrder) {
      String messageSuccess = message(code:"cashOutOrder.already.executed")
      if (companyService.enoughBalanceCompany(cashOutOrder.company, cashOutOrder.amount)){
        if (cashOutOrder.status == CashOutOrderStatus.AUTHORIZED) {
          cashOutOrderService.authorizeAndDoCashOutOrder(cashOutOrder)
          cashOutOrder.status = CashOutOrderStatus.EXECUTED
          cashOutOrder.save()
          messageSuccess = message(code:"cashOutOrder.executed.message")
        }
      } else {
        flash.message = message(code: 'company.insufficient.balance')
        redirect action:'show', id: cashOutOrder.id, params: [company:cashOutOrder.company.id]
        return
      }

      redirect action:'list', params:[status:'AUTHORIZED', messageSuccess:messageSuccess]
    }


    def create() {
      def company = Company.findById(session.company.toLong())
      respond new CashOutOrder(params), model:[company:company]
    }

    @Transactional
    def delete(CashOutOrder cashOutOrder) {

      if (cashOutOrder == null) {
        transactionStatus.setRollbackOnly()
        notFound()
        return
      }

      cashOutOrder.delete flush:true

      request.withFormat {
        form multipartForm {
          flash.message = message(code: 'default.deleted.message', args: [message(code: 'cashOutOrder.label', default: 'CashOutOrder'), cashOutOrder.id])
          redirect action:"index", method:"GET"
        }
        '*'{ render status: NO_CONTENT }
      }
    }

    @Transactional
    def deleteOrder(CashOutOrder cashOutOrder) {
      cashOutOrder.delete flush:true
      redirect action:"list"
    }

    def edit(CashOutOrder cashOutOrder) {
      def accounts = cashOutOrder.company.banksAccounts
      respond cashOutOrder, model:[banksAccounts:accounts]
    }

    def index(Integer max) {
      params.max = Math.min(max ?: 10, 100)
      def company = Company.findById(session.company.toLong())
      def cashOutOrders = CashOutOrder.findByCompany(company)
      respond cashOutOrders, model:[cashOutOrderList:cashOutOrders, cashOutOrderCount: CashOutOrder.count()]
    }

    def list() {
      params.max = params.max ?: 10
      def cashoutOrders = [:]
      cashoutOrders = cashOutOrderService.getCashoutOrdersToList(session.company as Long, params)

      [cashOutOrderList: cashoutOrders.list, cashOutOrderCount: cashoutOrders.items, messageSuccess:params.messageSuccess]
    }

    protected void notFound() {
      request.withFormat {
        form multipartForm {
          flash.message = message(code: 'default.not.found.message', args: [message(code: 'cashOutOrder.label', default: 'CashOutOrder'), params.id])
          redirect action: "index", method: "GET"
        }
        '*'{ render status: NOT_FOUND }
      }
    }

    @Transactional
    def save(CashOutOrderCommand command) {
      if (command == null) {
        transactionStatus.setRollbackOnly()
        notFound()
        return
      }
      def cashOutOrder = command.createCashOutOrder()
      def company = Company.findById(session.company.toLong())
      cashOutOrder.company = company
      if (cashOutOrder.hasErrors()) {
        transactionStatus.setRollbackOnly()
        respond cashOutOrder.errors, view:'create',model:[company:company]
        return
      }

      cashOutOrder.save flush:true

      request.withFormat {
        form multipartForm {
          flash.message = message(code: 'cashoutOrder.created.message')
          redirect cashOutOrder
        }
        '*' { respond cashOutOrder, [status: CREATED] }
      }
    }

    @Transactional
    def sendToAuthorize(CashOutOrder cashOutOrder) {
      Company company = Company.get(session.company)
      def missingAuthorizers = companyService.getNumberOfAuthorizersMissingForThisCompany(company ?: cashOutOrder.company)
      if (missingAuthorizers) {
        flash.error = "No tienes suficientes Autorizadores te hacen falta ${missingAuthorizers}"
      } else {
        cashOutOrderService.notificationToAuthorizersAndLegalRepresentatives(cashOutOrder)
        cashOutOrder.status = CashOutOrderStatus.TO_AUTHORIZED
        cashOutOrder.save(flush:true)
      }

      redirect action:"list"
    }

    def show(CashOutOrder cashOutOrder) {
      def company = Company.findById(session.company.toLong())
      respond cashOutOrder , model:[banksAccounts:company.banksAccounts, user:springSecurityService.currentUser]
    }


    @Transactional
    def update(CashOutOrderCommand command) {
      if (command == null) {
        transactionStatus.setRollbackOnly()
        notFound()
        return
      }

      CashOutOrder cashOutOrder = command.editCashOutOrder(params.id.toLong())

      if (cashOutOrder.hasErrors()) {
        transactionStatus.setRollbackOnly()
        respond cashOutOrder.errors, view:'edit', model:[banksAccounts:company.banksAccounts]
        return
      }

      cashOutOrder.save flush:true
      request.withFormat {
        form multipartForm {
          flash.message = message(code: 'cashoutOrder.updated.message')
          redirect cashOutOrder
        }
        '*'{ respond cashOutOrder, [status: OK] }
      }
    }

}
