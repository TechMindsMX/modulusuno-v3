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
        Conciliación de Cobro de Comisiones
        <small>Elegir factura(s) de comisiones de la empresa <strong>${company.bussinessName}</strong></small>
      </h1>
    </div>

    <div class="row">
      <div class="col-lg-12">
        <div class="panel panel-primary">
          <div class="panel-body">
            <div class="container-fluid">

              <div class="row">
                <div class="col-md-4 text-center">
                  <strong>Fecha del Pago</strong><br>
                  <g:formatDate format="dd-MM-yyyy" date="${payment.dateCreated}"/>
                </div>
                <div class="col-md-4 text-center">
                  <strong>Total</strong>
                  <br/>${modulusuno.formatPrice(number: payment.amount)}
                </div>
                <g:if test="${toApply == 0}">
                  <div class="col-md-4 text-center alert alert-success">
                </g:if>
                <g:else>
                  <div class="col-md-4 text-center alert alert-warning">
                </g:else>
                    <strong>Por aplicar</strong><br/>
                    ${modulusuno.formatPrice(number: toApply)}
                </div>
              </div>

              <hr>
              <g:if test="${toApply > 0}">
                <g:if test="${flash.message}">
                  <div class="alert alert-danger" role="alert">${flash.message}</div>
                </g:if>

                <g:form action="addInvoiceToConciliate">
                  <div class="row">
                    <g:hiddenField name="paymentId" value="${payment.id}"/>
                    <g:hiddenField name="corporateId" value="${corporate.id}"/>
                    <g:hiddenField name="companyId" value="${company.id}"/>
                    <div class="col-md-12">
                      <label>Facturas disponibles:</label>
                      <g:select class="form-control" name="invoiceId" from="${invoices}" noSelection="['':' Elegir factura...']" required="true" optionKey="id"/>
                    </div>
                  </div><br/>
                  <div class="row">
                    <div class="col-md-3">
                      <label>Monto a aplicar (MXN):</label>
                      <input class="form-control" type="number" min="0.01" max="${toApply}" step="0.01" name="amountToApply" required="true"/>
                    </div>
                    <div class="col-md-2 text-right">
                      <br/>
                      <button class="btn btn-primary">Agregar</button>
                    </div>
                    <div class="col-md-4"></div>
                    <div class="col-md-3"></div>
                  </div>
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
                    <th></th>
                  </tr>
                  <g:each in="${conciliations}" var="conciliation">
                  <tr>
                    <td>${conciliation.invoice.id} / ${conciliation.invoice.receiver.bussinessName}</td>
                    <td class="text-right">${modulusuno.formatPrice(number:conciliation.invoice.total)}</td>
                    <td class="text-right">${modulusuno.formatPrice(number:conciliation.invoice.amountToPay)}</td>
                    <td class="text-right">${modulusuno.formatPrice(number:conciliation.amount)}</td>
                    <td class="text-right">${modulusuno.formatPrice(number:(conciliation.invoice.amountToPay - conciliation.amount))}</td>
                    <td class="text-center">
                      <g:form action="deleteConciliation" id="${conciliation.id}" params="[corporateId:corporate.id, companyId:company.id]">
                        <button class="btn btn-danger">Quitar</button>
                      </g:form>
                    </td>
                  </tr>
                  </g:each>
                </table>
              </div>
              <hr>
              </g:if>

              <div class="row">
                <div class="col-md-3">
                  <g:link class="btn btn-info" controller="commissionsInvoice" action="conciliate" id="${company.id}" params="[corporateId:corporate.id]">Regresar</g:link>
                </div>
                <div class="col-md-3"></div>
                <div class="col-md-3"></div>
                <div class="col-md-3 text-right">
                  <g:if test="${toApply == 0}">
                    <g:link class="btn btn-success" action="applyConciliation" id="${payment.id}" params="[companyId:company.id, corporateId:corporate.id]">Aplicar</g:link>
                  </g:if>
                  <g:else>
                    <div class="alert alert-warning" role="alert">Aún dispone de monto por aplicar</div>
                  </g:else>
                </div>
              </div>

            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>
