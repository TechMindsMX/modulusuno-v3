package com.modulus.uno

class Corporate {

  String nameCorporate
  String corporateUrl

  static  hasMany =[companies:Company,users: User]

    static constraints = {
      nameCorporate: nullable:false
      corporateUrl: nullable:false
    }
}
