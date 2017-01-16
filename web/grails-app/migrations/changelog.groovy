databaseChangeLog = {
  include file: 'initial-structure.groovy'
  include file: 'create-profile-admin.groovy'
  include file: 'create-profile-integrado.groovy'
  include file: 'create-profile-legal.groovy'
  include file: 'creating-role-admin-iecce.groovy'
  include file: 'creating-role-integrado-autorizador-and-delete-autorizador.groovy'
  include file: 'creating-role-integrado-operador.groovy'
  include file: 'creating-role-operador_iecce.groovy'
  include file: 'rename-http-to-https.groovy'
  include file: 'update-protocol-http-to-https.groovy'
  include file: 'drop-protocol-from-s3asset.groovy'
  include file: 'modify-column-name-from-sale-order-item.groovy'
  include file: 'modify-column-name-from-product.groovy'
  include file: 'adding-column-stpclabe-to-clientlink.groovy'
  include file: 'adding-column-uuid-to-business-entity.groovy'
  include file: 'drop-modulusunoaccounts_and_update_status-for-all-companies.groovy'
  include file: 'adding-ids-of-artimisa.groovy'
  include file: 'adding-date-operation-in-sale-and-puchase-order.groovy'
  include file: 'creating-role-ejecutor.groovy'
  include file: 'adding-column-externalid-to-sale-order.groovy'
  include file: 'adding-column-external-id-to-purchase-order.groovy'
  include file: 'adding-new-entity-banking-transactions.groovy'
  include file: 'delete-constraint-reference-in-movimientos-bancarios.groovy'
  include file: 'change-type-colum-quatity-in-sale-order-item.groovy'
  include file: 'creating-role-financial.groovy'
  include file: 'change-type-column-quatity-in-purchase-order-item.groovy'
  include file: 'adding-notnull-constraint-to-column-quantity-in-sale-order-item.groovy'
  include file: 'adding-new-variables-to-sale-order-and-purchase-order.groovy'
  include file: 'modify-column-name-growing-to-300-from-product.groovy'
  include file: 'modify-column-name-growing-to-300-from-sale-order-item.groovy'
  include file: 'modify-column-name-growing-to-300-from-purchase-order-item.groovy'
  include file: 'adding-indicator-for-account-stp.groovy'
  include file: 'adding-variable-note-to-purchase-and-sale-order.groovy'
  include file: 'create-table-stp-deposit.groovy'
  //include file: 'adding-user-admin-production.groovy'
  include file: 'adding-column-pdf-template-to-sale-order.groovy'
  include file: 'create-new-domain-to-parcial-payments.groovy'
  include file: 'adding-multi-payments-to-purchase-order.groovy'
  include file: 'create-m1-roles.groovy'
  include file: 'adding-new-domain-corporate.groovy'
  include file: 'creating-user-role-by-company.groovy'
  include file: 'removing-actors-relationship-in-company.groovy'
  include file: 'delete-relationship-into-user-and-role.groovy'
  include file: 'delete-old-roles-in-m1.groovy'
  include file: 'adding-user-to-production.groovy'
}
