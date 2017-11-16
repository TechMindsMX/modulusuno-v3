<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'quotationContract.label', default: 'QuotationContract')}" />
    <title><g:message code="default.create.label" args="[entityName]" /></title>
  </head>
    
  <body>
    <div class="page-title">
      <h1>
        <i class="fa fa-list-alt fa-3x"></i>
        Cotización
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
              <div class="col-md-12">
                <div class="table-responsive">
                  <table class="table table-striped table-condensed">
                    <tr>
                      <th class="text-center">Cliente</th>
                      <th class="text-center">Descripción</th>
                      <th class="text-center">Comisión</th>
                      <th class="text-center">Monto</th>
                      <th class="text-center">Estatus</th>
                    </tr>
                    <g:each in="${quotationRequestList.sort{it.biller.toString()}.each(){it}}" var="request">
                      <tr>
                        <td><g:link action="show" id="${request.id}">${request.quotationContract.client}</g:link></td>
                        <td>${request.description}</td>
                        <td class="text-center">${request.commission}</td>
                        <td class="text-center">${modulusuno.formatPrice(number:request.amount)}</td>
                        <td><g:message code="quotationRequest.status.${request.status}"/></td>
                      </tr>
                    </g:each>
                  </table>
                 </div>
              </div>
            </div>
          </div> 
          <nav>
            <div class="pagination">
              <g:paginate class="pagination" controller="quotationRequest" action="index" total="${quotationRequestCount ?: 0}" />
            </div>
          </nav>      
        </div>
      </div>
    </div>
  </body>
</html>