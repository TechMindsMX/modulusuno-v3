package com.modulus.uno

import grails.transaction.Transactional

@Transactional
class AddressService {

  def getAddresTypesForOrganization(Long companyId){
    def customAddressTypes = []
    if(!companyId){
      customAddressTypes << [key:AddressType.FISCAL.name(),value:AddressType.FISCAL.name().toLowerCase().capitalize()]
      return customAddressTypes
    }
    def addressTypes = findAddressTypeForOrganization(companyId)

    addressTypes.each{ addressType ->
      customAddressTypes << [key:addressType.name(),value:addressType.name().toLowerCase().capitalize()]
    }

    customAddressTypes
  }

  def findAddressTypeForOrganization(Long companyId){
    def addresses = Company.get(companyId).addresses
    if(addresses.find{ it.addressType == AddressType.FISCAL }) {
      AddressType.values().findAll{ it.name() != AddressType.FISCAL.name() }
    } else {
      AddressType.values()
    }
  }

  def getAllAddressTypes() {
    def customAddressTypes = []
    AddressType.values().each{ addressType ->
      customAddressTypes << [key:addressType.name(),value:addressType.name().toLowerCase().capitalize()]
    }
    customAddressTypes
  }

}
