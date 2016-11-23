<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main" />
  <g:set var="entityName" value="${message(code: 'depositOrder.label', default: 'DepositOrder')}" />
  <title><g:message code="default.list.label" args="[entityName]" /></title>
</head>
<body>
  <g:if test="${isMoneyBackOrder}">
    <g:set var="messageOrder" value="Reembolso"/>
    <g:set var="messageBusinessEntityOrder" value="Empleado"/>
  </g:if><g:else>
    <g:set var="messageOrder" value="Pago a Proveedor"/>
    <g:set var="messageBusinessEntityOrder" value="Proveedor"/>
  </g:else>

  <div class="page-title">
  <sec:ifAnyGranted roles="ROLE_ADMIN, ROLE_ADMIN_IECCE">
   <h1>
           <i class="icon-proveedor fa-3x"></i>
        Pago a proveedores
    <small>Listado de Órdenes de ${messageOrder}</small>
    </h1>
</sec:ifAnyGranted>

<sec:ifAnyGranted roles="ROLE_INTEGRADO,ROLE_LEGAL_REPRESENTATIVE, ROLE_INTEGRADO_OPERADOR">
 <h1>
           <i class="icon-proveedor fa-3x"></i>
        Operaciones / Pago a proveedor
    <small>Listado de Órdenes de ${messageOrder}</small>
    </h1>
</sec:ifAnyGranted>

   <ol class="breadcrumb">
   <li><i class="fa fa-caret-square-o-up"></i> Compañía</li>
   <li class="active">Listado de Órdenes de ${messageOrder}</li>
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
          <div class="alert alert-danger" role="alert">${flash.message}</div>
        </g:if>
        <g:if test="${messageSuccess}">
          <div class="well well-sm alert-success">${messageSuccess}</div>
        </g:if>
      <div class="table-responsive">
      <table class="table">
        <tr>
          <th>No. de Orden</th>
          <th>${messageBusinessEntityOrder}</th>
          <th>Estatus</th>
          <th>Fecha de Creación</th>
          <th>Compañía</th>
          <th>Total</th>
          <g:if test="${!isMoneyBackOrder}">
            <th>Anticipada</th>
          </g:if>
          <th>Factura</th>
        </tr>
          <g:if test="${purchaseOrder.isEmpty()}">
            <div class="alert alert-danger" role="alert">
              <g:message code="purchaseOrder.list.empty"/>
            </div>
          </g:if>
        <g:each in="${purchaseOrder.sort{it.dateCreated}}" var="purch">
          <tr class="${message(code: 'purchaseOrder.style.background.'+purch.status)}">
            <td class="text-center"><g:link action="show" id="${purch.id}">${purch.id}</g:link></td>
            <td>${purch.providerName}</td>
            <td><g:message code="purchaseOrder.status.${purch.status}" default="${purch.status}"/></td>
            <td><g:formatDate format="dd-MM-yyyy hh:mm:ss" date="${purch.dateCreated}"/></td>
            <td>${purch.company}</td>
            <td class="text-right">${modulusuno.formatPrice(number: purch.total)}</td>
            <g:if test="${!purch.isMoneyBackOrder}">
              <td class="text-center"><g:if test="${purch.isAnticipated}">SÍ</g:if><g:else>NO</g:else></td>
            </g:if>
            <td class="text-center">
              <g:if test="${purch.isAnticipated}">
                <g:if test="${!purch.documents}">
                  <span class="label label-danger" title="Sin documentos de facturación">
                    <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
                  </span>
                </g:if>
                <g:if test="${purch.documents?.size()==1}">
                  <span class="label label-warning" title="Falta agregar un documento de facturación">
                    <span class="glyphicon glyphicon-warning-sign" aria-hidden="true"></span>
                  </span>
                </g:if>
                <g:if test="${purch.documents?.size()>=2}">
                  <span class="label label-success" title="Documentos de facturación completos">
                    <span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
                  </span>
                </g:if>
              </g:if>
            </td>
          </tr>
         </g:each>
       </table>
       <nav>
          <ul class="pagination">
            <g:paginate class="pagination" controller="purchaseOrder" action="list" total="${purchaseOrderCount}" />
          </ul>
        </nav>
      </div>
    </div>
  </div>
</div>
</div>
</body>
</html>
