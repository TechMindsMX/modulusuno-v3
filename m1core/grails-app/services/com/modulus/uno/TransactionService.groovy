package com.modulus.uno

import grails.transaction.Transactional
import org.springframework.transaction.annotation.Propagation

@Transactional
class TransactionService {

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  Transaction saveTransaction(Transaction transaction) {
    transaction.save()
    transaction
  }

}
