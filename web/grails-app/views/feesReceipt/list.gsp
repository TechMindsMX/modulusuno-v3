<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'feesReceipt.label', default: 'FeesReceipt')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>
  </head>
  <body>
    <div class="page-title">
    <sec:ifAnyGranted roles="ROLE_ADMIN, ROLE_ADMIN_IECCE, ROLE_EJECUTOR">
    <h1>
      <i class="icon-recibo fa-3x"></i>
      Recibos de honorarios
      <small><g:message code="feesReceipt.list.label" /></small>
      </h1>
</sec:ifAnyGranted>

<sec:ifAnyGranted roles="ROLE_INTEGRADO,ROLE_LEGAL_REPRESENTATIVE, ROLE_INTEGRADO_OPERADOR">
<h1>
      <i class="icon-recibo fa-3x"></i>
      Operaciones | Recibo de honorarios
      <small><g:message code="feesReceipt.list.label" /></small>
      </h1>
</sec:ifAnyGranted>

      <ol class="breadcrumb">
        <li><i class="fa fa-caret-square-o-up"></i> Compañía</li>
        <li class="active">Órdenes de Recibo de Honorarios</li>
      </ol>
    </div>

    <div class="row">
      <div class="col-lg-12">
        <div class="portlet portlet-blue">
          <div class="portlet-heading">
            <div class="portlet-title">
              <h4><g:message code="feesReceipt.list.label"/></h4>
            </div>
            <div class="clearfix"></div>
          </div>
          <div id="bluePortlet" class="panel-collapse collapse in">
            <div class="portlet-body">
              <g:if test="${flash.message}">
                <div class="alert alert-danger" role="alert">${flash.message}</div>
              </g:if>
              <g:if test="${messageSuccess}">
                <div class="well well-sm alert-success">${messageSuccess}</div>
              </g:if>
             <g:render template="table" model="[feesReceiptList:feesReceiptList, feesReceiptCount:feesReceiptCount]"/>
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>
