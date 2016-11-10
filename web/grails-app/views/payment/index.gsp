<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'payment.label', default: 'Payment')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
      <div class="page-title">
      <h1>
      <i class="icon-bell fa-3x"></i>
      Notificaciones de Pago a Integrados
      <small><g:message code="payment.list" args="[entityName]" /></small>
      </h1>
         <ol class="breadcrumb">
         <li><i class="fa fa-caret-square-o-up"></i> Compañía</li>
          <li class="active">Notificaciones de Pago a Integrados</li>
        </ol>
      </div>
      <div id="edit-address" class="content scaffold-edit" role="main">
        <div class="portlet portlet-blue">
          <div class="portlet-heading">
            <div class="portlet-title">
              <br />
              <br />
            </div>
            <div class="clearfix"></div>
          </div>
          <div id="horizontalFormExample" class="panel-collapse collapse in">
            <div class="portlet-body">
              <g:if test="${flash.message}">
              <div class="message" role="status">${flash.message}</div>
            </g:if>
              <g:render template="list"/>
            </div>
          </div>
        </div>
      </div>
      </div>
   </body>
</html>
