package com.modulus.uno

import grails.transaction.Transactional
import java.math.RoundingMode

@Transactional
class ConciliationService {

  def springSecurityService

  def getTotalToApplyForPayment(Payment payment) {
    def conciliations = getConciliationsToApplyForPayment(payment)
    BigDecimal applied = conciliations ? conciliations*.amount.sum() : new BigDecimal(0)
    payment.amount - applied
  }

  List<Conciliation> getConciliationsToApplyForPayment(Payment payment) {
    Conciliation.findAllByPaymentAndStatus(payment, ConciliationStatus.TO_APPLY)
  }

  void saveConciliationForCompany(Conciliation conciliation, Company company) {
    if (conciliation.amount > conciliation.saleOrder.amountToPay) {
      throw new BusinessException("El monto a conciliar (${conciliation.amount.setScale(2, RoundingMode.HALF_UP)}) no puede ser mayor al monto por pagar de la factura (${conciliation.saleOrder.amountToPay.setScale(2, RoundingMode.HALF_UP)})")
    }

    conciliation.company = company
    conciliation.user = springSecurityService.currentUser
    log.info "Saving conciliation: ${conciliation.dump()}"
    conciliation.save()
  }

  void deleteConciliation(Conciliation conciliation) {
    conciliation.delete()
  }

}
