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
  include file: 'updating-type-venta-to-factura-in-commission.groovy'
  include file: 'adding-column-payment-method-to-sale-order.groovy'
  include file: 'adding-column-alias-stp-to-modulus-uno-account.groovy'
  include file: 'adding-column-discount-to-sale-order-item.groovy'
  include file: 'adding-columns-currency-and-changetype-to-saleorder.groovy'
  include file: 'removing-state-relation-with-log.groovy'
  include file: 'adding-uuid-to-machine.groovy'
  include file: 'drop-not-null-constraint-to-change-type-from-sale-order.groovy'
  include file: 'create-domain-sale-order-payment.groovy'
  include file: 'create-domain-conciliation.groovy'
  include file: 'create-transactions.groovy'
  include file: 'adding-relation-transaction.groovy'
  include file: 'droping-columns-timone-from-cashout-order.groovy'
}
