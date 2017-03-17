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
        Conciliación de Pago
        <small>Elegir factura(s)</small>
      </h1>
    </div>

    <div class="row">
      <div class="col-lg-12">
        <div class="panel panel-primary">
          <div class="panel-body">
            <div class="container-fluid">

              <div class="row">
                <div class="col-md-4 text-center"><strong>Fecha del Pago</strong></div>
                <div class="col-md-4 text-center"><strong>Total</strong></div>
                <div class="col-md-4 text-center"><strong>Por aplicar</strong></div>
              </div>
              <div class="row">
                <div class="col-md-4 text-center"><g:formatDate format="dd-MM-yyyy" date="${payment.dateCreated}"/></div>
                <div class="col-md-4 text-center">${modulusuno.formatPrice(number: payment.amount)}</div>
                <div class="col-md-4 text-center">${modulusuno.formatPrice(number: toApply)}</div>
              </div>
              <hr>
              <g:if test="${toApply > 0}">
              <div class="row">
                <g:form action="addSaleOrderToConciliate">
                <g:hiddenField name="paymentId" value="${payment.id}"/>
                <div class="col-md-6">
                  <label>Facturas disponibles:</label>
                  <g:select class="form-control" name="saleOrderId" from="${saleOrders}" noSelection="['':' Elegir factura...']" required="true" optionKey="id"/>
                </div>
                <div class="col-md-2">
                  <label>Tipo de Cambio:</label>
                  <input class="form-control" type="number" min="0.01" step="0.01" name="changeType" readOnly="true" value="0.00"/>
                </div>
                <div class="col-md-2">
                  <label>Monto a aplicar (MXN):</label>
                  <input class="form-control" type="number" min="0.01" step="0.01" name="amountToApply" required="true"/>
                </div>
                <div class="col-md-2 text-right">
                  <br/>
                  <button class="btn btn-primary">Agregar</button>
                </div>
                </g:form>
              </div>
              <hr>
              </g:if>
              <label>Facturas seleccionadas:</label>
              <div class="table-responsive">
                <table class="table">
                  <tr>
                    <th>Folio</th>
                    <th>Fecha</th>
                    <th>Cliente</th>
                    <th>Total</th>
                    <th>Por pagar</th>
                    <th>Monto a aplicar (MXN)</th>
                    <th>Nuevo Saldo</th>
                    <th>Moneda</th>
                    <th>Tipo Cambio</th>
                    <th></th>
                  </tr>
                  <tr>
                    <td>100</td>
                    <td>15/01/2017</td>
                    <td>Cliente Equis</td>
                    <td>${modulusuno.formatPrice(number: 8000)}</td>
                    <td>${modulusuno.formatPrice(number: 8000)}</td>
                    <td>${modulusuno.formatPrice(number: 8000)}</td>
                    <td>${modulusuno.formatPrice(number: 0)}</td>
                    <td>MXN</td>
                    <td>NA</td>
                    <td class="text-center">
                      <button class="btn btn-danger">Quitar</button>
                    </td>
                  </tr>
                </table>
              </div>

              <div class="row">
                <div class="col-md-6">
                  <button class="btn btn-danger">Cancelar</button>
                </div>
                <div class="col-md-6 text-right">
                  <button class="btn btn-success">Aplicar</button>
                </div>
              </div>

            </div>
          </div>
        </div>
      </div>
    </div>

  </body>
</html>
