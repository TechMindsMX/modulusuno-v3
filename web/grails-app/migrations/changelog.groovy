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
  include file: 'adding-uuid-to-fees-receipt.groovy'
  include file: 'initialize-uuid-for-existing-fees-receipt.groovy'
  include file: 'adding-unique-to-variable-of-corporate.groovy'
  include file: 'adding-columns-dates-and-status-to-stp-deposit.groovy'
  include file: 'adding-column-payment-method-to-sale-order.groovy'
  include file: 'adding-group-notification.groovy'
  include file: 'adding-column-discount-to-sale-order-item.groovy'
  include file: 'add-machine-state-domain-classes.groovy'
  include file: 'updating-machinery-link-class.groovy'
  include file: 'adding-actions-to-company.groovy'
  include file: 'updating-machine-structure.groovy'
  include file: 'updating-transition-actions-relation.groovy'
  include file: 'removing-state-relation-with-log.groovy'
  include file: 'adding-uuid-to-machine.groovy'
  include file: 'adding-notification-for-state.groovy'
  include file: 'adding-mock-machine-class.groovy'
  include file: 'adding-the-linker-structure.groovy'
  include file: 'updating-the-relation-with-the-combination.groovy'
}
