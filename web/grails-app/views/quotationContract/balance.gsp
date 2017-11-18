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
        Lista de Solicitudes de Cotizaciones
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

            <g:if test="${!balance}">
              <g:form action="balance">
                <g:render template="balance/chooseClient"/>
              </g:form>
            </g:if><g:else>



            <g:if test="${balance.quotationContract}">
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
                <g:form action="balance">
                <div class="row">
                  <div class="col-md-6">
                  <label>
                    <g:message code="Del" />
                  </label>
                  <input class="form-control" type="text" id="datepicker" name="initDate" required="required">
                  <input type="hidden" value="${balance.quotationContract.id}" name="id" />
                  </div>
                  <div class="col-md-6">
                  <label>
                    <g:message code="Al" />
                  </label>
                  <input class="form-control" type="text" id="datepicker1" name="lastDate" required="required">
                  </div>
                </div>
                <div class="row">
                  <div class="col-md-6">
                  <g:submitButton name="consultar" class="btn btn-primary marginP" value="${message(code: 'default.button.consultar.label', default: 'Consultar')}"
                  />
                  </div>
                </div>
                </g:form>

              </div>
              <div class="col-md-4 vertical-bar">
                <div class="row">
                  <h2>
                    Disponible:  <g:formatNumber number="${balance.summary.available}" type="currency" currencyCode="MXN" />
                  </h2>
                </div>
                <div class="row">
                  <h2>
                    En transito: <g:formatNumber number="${balance.summary.transit}" type="currency" currencyCode="MXN" />
                  </h2>
                </div>
                <div class="row">
                  <h2>
                    Total: <g:formatNumber number="${balance.summary.total}" type="currency" currencyCode="MXN" />
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
                      <g:each in="${balance.conceptList}" var="paymentRequest">
                        <tr>
                          <td>${paymentRequest.concept}</td>
                          <td><g:formatDate format="dd-MM-yyyy" date="${paymentRequest.date}"/></td>
                          <td> <g:formatNumber number="${paymentRequest.deposit}" type="currency" currencyCode="MXN" /></td>
                          <td> <g:formatNumber number="${paymentRequest.charge}" type="currency" currencyCode="MXN" /></td>
                          <td> <g:formatNumber number="${paymentRequest.balance}" type="currency" currencyCode="MXN" /></td>
                        </tr>
                      </g:each>
                    </table>
                  </div>

            </g:if>

            </g:else>
             </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <asset:javascript src="quotationContract/create.js"/>
  </body>
</html>