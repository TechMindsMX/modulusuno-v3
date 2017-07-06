package com.modulus.uno

import grails.validation.Validateable

class BankAccountCommand implements Validateable {
  String accountNumber
  String branchNumber
  String clabe
  String bank
  String cardNumber
  boolean concentradora

  BankAccount createBankAccount(){
    new BankAccount(
      accountNumber:this.accountNumber,
      branchNumber:this.branchNumber,
      clabe:this.clabe,
      concentradora: this.concentradora,
      cardNumber: this.cardNumber
    )
  }
}
