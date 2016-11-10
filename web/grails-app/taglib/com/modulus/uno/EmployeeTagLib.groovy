package com.modulus.uno

class EmployeeTagLib {
  static namespace = "employeeLib"
  static defaultEncodeAs = "raw"

  def employeeService

  def isEmployee = { attrs ->
    out << employeeService.isEmployee(attrs.be)
  }

}
