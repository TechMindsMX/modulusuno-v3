package com.modulus.uno

class UserService {

  def recoveryService
  def documentService
  def companyService

  def addInformationToLegalRepresentative(def user, def params) {
    def profile = user.profile
    profile.name = params.profile.name
    profile.lastName = params.profile.lastName
    profile.motherLastName = params.profile.motherLastName
    profile.email = params.profile.email
    profile.rfc = params.profile.rfc
    profile.curp = params.profile.curp
    profile.trademark = params.profile.trademark
    profile.fullName = params.profile.fullName
    profile.birthDate = params.profile.birthDate
    if (params.telephone?.id) {
      updateTelephoneOfUser(params)
    } else {
      profile.addToTelephones(createTelephone(params))
    }
    profile.save()
  }

  def containsUsersWithDocumentsByCompany(def listOfUsers,Company company) {
    def mapUserWithDocs = []
    listOfUsers.each{ user ->
      def listOfDocuments = documentService.getDocumentsByCompanyForLegalRepresentative(user.profile.documents,company.id)
      def documentsAvilable = false
      if (listOfDocuments.size() == 4)
        documentsAvilable = true
      mapUserWithDocs.add(documentsAvilable)
    }
    if (mapUserWithDocs.contains(false)) {
      return false
    } else {
      return true
    }
  }

  def createRelationshipIntoLegalRepresentativeAndAsset(S3Asset asset, def userId) {
    def user = User.findById(userId)
    user.profile.addtoDocuments = asset
    user.save()
    user
  }

  User createUserWithoutRole(User user,Profile profile){
    profile.save()
    user.profile = profile
    user.save()
    recoveryService.sendConfirmationAccountToken(user.profile?.email)
    user
  }

  User setAuthorityToUser(User user,String authority){
    Role role = Role.findByAuthority(authority)
    UserRole.create user,role, true
    user.save()
    user
  }

  def getUsersByRole(String srol){
    def users = User.list().findAll { user ->
      if (user.authorities.any { it.authority == srol })
        return user
    }
    users
  }

  def save(User user, Role role){
    user.save()
    UserRole.create user, role, true
    recoveryService.sendConfirmationAccountToken(user.profile?.email)
  }

  private def createTelephone(params) {
    def telephone = new Telephone()
    telephone.number = params.number
    telephone.extension = params.extension
    telephone.type = params.type
    telephone.save(failOnError:true)
    telephone
  }

  private def updateTelephoneOfUser(params) {
    def telephone = Telephone.findById(params.telephone.id)
    telephone.number = params.telephone.number
    telephone.extension = params.telephone.extension
    telephone.type = params.telephone.type
    telephone.save()
  }

}
