<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'quotationContract.label', default: 'QuotationContract')}" />
    <title><g:message code="default.create.label" args="[entityName]" /></title>
    <asset:stylesheet src="quotationContract/balance.css" />
  </head>

  <body>
    <div class="page-title">
      <h1>
        <i class="fa fa-list-alt fa-3x"></i>
        Lista de Solicitudes
        <small>${company}</small>
      </h1>
    </div>
    <div id="edit-address" class="content scaffold-edit" role="main">
      <div class="portlet portlet-blue">
        <div class="portlet-heading">
          <div class="portlet-title"></div>
          <div class="clearfix"></div>
        </div>
        <div id="horizontalFormExample" class="panel-collapse collapse in">
          <div class="portlet-body">
            <g:if test="${flash.message}">
              <div class="message" role="status">${flash.message}</div>
            </g:if>
            <div class="row">
              <div class="col-md-8">
                <h1>
                <label>
                  <g:message code="Cliente: " />
                </label>
                <label>
                  <g:message message="${balance.quotationContract.client}" />
                </label>
                </h1>
                <div class="row">
                  <div class="col-md-6">
                  <label>
                    <g:message code="Del" />
                  </label>
                  <input class="form-control" type="text" id="datepicker" name="initDate" required="required">
                  </div>
                  <div class="col-md-6">
                  <label>
                    <g:message code="Al" />
                  </label>
                  <input class="form-control" type="text" id="datepicker1" name="initDate" required="required">
                  </div>
                </div>
              </div>
              <div class="col-md-4 vertical-bar">
                <div class="row">
                  <h2>
                    Disponible:
                  </h2>
                </div>
                <div class="row">
                  <h2>
                    En transito:
                  </h2>
                </div>
                <div class="row">
                  <h2>
                    Total:
                  </h2>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-md-12">
                  <div class="table-responsive">
                    <table class="table table-striped table-condensed">
                      <tr>
                        <th>Concepto</th>
                        <th>Fecha</th>
                        <th>Abono</th>
                        <th>Cargo</th>
                        <th>Saldo</th>
                      </tr>
                      <g:each in="${quotationPaymentRequestList}" var="paymentRequest">
                        <tr>
                          <td><g:message code="quotationPaymentRequest.paymentWay.${paymentRequest.paymentWay}"/></td>
                          <td><g:formatDate format="dd-MM-yyyy" date="${paymentRequest.dateCreated}"/></td>
                          <td>${paymentRequest.amount}</td>
                          <td>${paymentRequest.paymentWay}</td>
                          <td>total</td>
                        </tr>
                      </g:each>
                    </table>
                  </div>

                <nav>
                  <div class="pagination">
                    <g:paginate class="pagination" controller="businessEntity" action="index" total="${businessEntityCount ?: 0}" />
                  </div>
                </nav>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <asset:javascript src="quotationContract/create.js"/>
  </body>
</html>
