package com.modulus.uno

class CreateAccountCommand implements MessageCommand {
  String payerAccount
  String uuid
  String name
  ArrayList<String> email
}
