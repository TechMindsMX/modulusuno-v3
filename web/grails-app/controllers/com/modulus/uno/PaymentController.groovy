package com.modulus.uno

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import java.math.RoundingMode

class PaymentController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def paymentService

  @Transactional(readOnly = true)
  def index(Integer max) {
    params.max = Math.min(max ?: 10, 100)
    def payments = paymentService.getPaymentsToList(session.company ? session.company.toLong() : session.company, params)

    [payments: payments.list, paymentsCount: payments.items]
  }

  def show(Payment payment) {
    respond payment
  }

  @Transactional(readOnly = true)
  def create() {
    respond new Payment(params), model:[companies:Company.findAllByStatus(CompanyStatus.ACCEPTED)]
  }

  @Transactional
  def save(Payment payment) {
    payment.amount = new BigDecimal(params.amount) // TODO: Wuackala
    if (payment == null) {
      transactionStatus.setRollbackOnly()
      notFound()
      return
    }

    if (payment.hasErrors()) {
      transactionStatus.setRollbackOnly()
      respond payment.errors, view:'create'
      return
    }

    payment.save flush:true

    //TODO: enviar mail al integrado de la notificación de pago

    redirect action:'index', params:[status:"PENDING"]

  }

  def edit(Payment payment) {
    respond payment
  }

  @Transactional
  def update(Payment payment) {
    if (payment == null) {
      transactionStatus.setRollbackOnly()
      notFound()
      return
    }

    if (payment.hasErrors()) {
      transactionStatus.setRollbackOnly()
      respond payment.errors, view:'edit'
      return
    }

    payment.save flush:true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.updated.message', args: [message(code: 'payment.label', default: 'Payment'), payment.id])
        redirect payment
      }
      '*'{ respond payment, [status: OK] }
    }
  }

  @Transactional
  def delete(Payment payment) {

    if (payment == null) {
      transactionStatus.setRollbackOnly()
      notFound()
      return
    }

    payment.delete flush:true

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'payment.label', default: 'Payment'), payment.id])
        redirect action:"index", method:"GET"
      }
      '*'{ render status: NO_CONTENT }
    }
  }

  protected void notFound() {
    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'payment.label', default: 'Payment'), params.id])
        redirect action: "index", method: "GET"
      }
      '*'{ render status: NOT_FOUND }
    }
  }

  def reconcile() {
    Company company = Company.get(session.company)
    def payments = Payment.findAllByCompanyAndStatus(company, PaymentStatus.PENDING)
    def saleOrders = SaleOrder.findAllByCompanyAndStatus(company, SaleOrderStatus.EJECUTADA)
    [payments:payments, saleOrders: saleOrders, company:company]
  }

  def tieOrderWithPayment(){
    try {
      def p = paymentService.concilationForSaleOrderWithPayment(params.long("saleOrder.id"),params.long('id'))
      flash.message = """\
        El pago por \$ ${p.amount} se concilió con la orden para el cliente '${p.saleOrder.clientName}' por un monto de \$ ${p.saleOrder.total.setScale(2, RoundingMode.HALF_UP)}
      """
    }catch(e){
      flash.message = e.message
    }
    redirect action:'reconcile'
  }

  def cancelPayment(Payment payment){
    payment.status = PaymentStatus.CANCELED
    payment.save()
    redirect action:'index'
  }

}
