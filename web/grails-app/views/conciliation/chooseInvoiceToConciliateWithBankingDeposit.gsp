<%! import com.modulus.uno.status.ConciliationStatus %>
<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="main" />
    <title></title>
  </head>
  <body>
    <div class="page-title">
      <h1>
        <i class="fa fa-usd fa-3x"></i>
        Conciliación de Depósitos Bancarios
        <small>Elegir factura(s)</small>
      </h1>
    </div>

    <div class="row">
      <div class="col-lg-12">
        <div class="panel panel-primary">
          <div class="panel-body">
            <div class="container-fluid">

              <g:render template="dataBankingTransaction"/>
              <hr>
              <g:if test="${toApply > 0}">
                <g:if test="${flash.message}">
                  <div class="alert alert-danger" role="alert">${flash.message}</div>
                </g:if>

                <g:form action="addSaleOrderToConciliate">
                  <g:render template="addInvoiceToConciliateBankingTransaction"/>
                </g:form>
                <hr>
              </g:if>

              <g:if test="${conciliations}">
              <label>Facturas seleccionadas:</label>
              <div class="table-responsive">
                <table class="table">
                  <tr>
                    <th class="col-md-4">Factura</th>
                    <th>Total</th>
                    <th>Por pagar</th>
                    <th>Monto a aplicar (MXN)</th>
                    <th>Nuevo Saldo</th>
                    <th>Moneda</th>
                    <th>Tipo Cambio</th>
                    <th></th>
                  </tr>
                  <g:each in="${conciliations}" var="conciliation">
                  <tr>
                    <td>${conciliation.saleOrder.id} / ${conciliation.saleOrder.clientName}</td>
                    <td class="text-right">${modulusuno.formatPrice(number: conciliation.saleOrder.total)}</td>
                    <td class="text-right">
                      <g:if test="${bankingTransaction.conciliationStatus == ConciliationStatus.TO_APPLY}">
                        ${modulusuno.formatPrice(number: conciliation.saleOrder.amountToPay)}
                      </g:if><g:else>
                        ${modulusuno.formatPrice(number: conciliation.saleOrder.currency == "MXN" ? conciliation.saleOrder.amountToPay + conciliation.amount : conciliation.saleOrder.amountToPay + (conciliation.amount/conciliation.changeType)) }
                      </g:else>
                    </td>
                    <td class="text-right">
                        ${modulusuno.formatPrice(number: conciliation.amount)}
                    </td>
                    <td class="text-right">
                      <g:if test="${bankingTransaction.conciliationStatus == ConciliationStatus.TO_APPLY}">
                        ${modulusuno.formatPrice(number: conciliation.saleOrder.currency == "MXN" ? conciliation.saleOrder.amountToPay - conciliation.amount : conciliation.saleOrder.amountToPay - (conciliation.amount/conciliation.changeType)) }
                      </g:if><g:else>
                        ${modulusuno.formatPrice(number: conciliation.saleOrder.amountToPay)}
                      </g:else>
                    </td>
                    <td>${conciliation.saleOrder.currency}</td>
                    <td>${conciliation.changeType ?: "NA"}</td>
                    <td class="text-center">
                      <g:if test="${bankingTransaction.conciliationStatus == ConciliationStatus.TO_APPLY}">
                        <g:form action="deleteConciliation" id="${conciliation.id}">
                          <button class="btn btn-danger">Quitar</button>
                        </g:form>
                      </g:if>
                    </td>
                  </tr>
                  </g:each>
                </table>
              </div>
              <hr>
              </g:if>

              <g:render template="actionsToConciliateBankingTransaction"/>

            </div>
          </div>
        </div>
      </div>
    </div>
    <asset:javascript src="conciliation/chooseInvoice.js"/>
  </body>
</html>
