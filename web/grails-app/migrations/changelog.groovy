databaseChangeLog = {
  include file: 'initial-structure.groovy'
  include file: 'create-m1-roles.groovy'
  include file: 'adding-user-admin-production.groovy'
  include file: 'adding-list-banks.groovy'
  include file: 'adding-colunm-reference-to-payment.groovy'
  include file: 'adding-uuid-to-purchase-order.groovy'
  include file: 'initializeUuidToPurchaseOrder.groovy'
  include file: 'moving-data-from-uuid-to-folio-in-sale-order.groovy'
  include file: 'adding-uuid-to-cashout-order.groovy'
  include file: 'initialize-uuid-from-cashout-order-existing.groovy'
}
