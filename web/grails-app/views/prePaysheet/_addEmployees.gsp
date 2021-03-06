<%! import com.modulus.uno.BusinessEntityStatus %>
<style>
	th {
	  text-align:center;
	}
	
  th, td {
    white-space: nowrap;
    width: 1px;
  }

	.fixwidth {
	  width: 300px;
	}

</style>

<div class="portlet portlet-default">
  <div class="portlet-heading">
    <div class="portlet-title">
      <h4>Empleados disponibles para agregar</h4>
    </div>
    <div class="clearfix"></div>
  </div>

  <div class="portlet-body">
    <g:hiddenField id="entities" name="entities" value=""/>
    <g:hiddenField name="prePaysheet.id" value="${prePaysheet.id}"/>
    <div class="table-responsive">
			<div class="container-fluid">
      <table class="table table-striped table-condensed">
        <tr>
          <th><g:checkBox id="selectAll" name="selectAll" title="Seleccionar Todos"/></th>
          <th>No.Empl</th>
          <th>Nombre</th>
          <th>RFC</th>
          <th>CURP</th>
          <th>NSS</th>
					<th>Cuenta</th>
					<th>Neto a Pagar</th>
					<th>Observaciones</th>
        </tr>

        <g:each in="${employeesAvailableToAdd}" var="employee" status="index">
        <tr>
          <td>
            <g:if test="${employee.status == BusinessEntityStatus.ACTIVE}">
              <g:checkBox class="entity" id="checkBe" name="checkBe" value="${employee.id}" checked="false"/>
            </g:if>
            <g:if test="${employee.status == BusinessEntityStatus.INACTIVE}">
              <span class="glyphicon glyphicon-ban-circle text-danger" aria-hidden="true" title="El empleado está dado de baja"></span>
            </g:if>
          </td>
          <td>${employee.number}</td>
          <td>${employee}</td>
          <td>${employee.rfc}</td>
          <td>${employee.curp}</td>
          <td>${dataImssEmployees[index]?.nss}</td>
          <td>
            <g:if test="${employee.status == BusinessEntityStatus.ACTIVE}">
						<g:set var="bankAccount" value="${employee.banksAccounts ? employee.banksAccounts.first().id : ""}"/> 
						<g:select class="form-control fixwidth" id="bankAccount${employee.id}" name="bankAccount${employee.id}" from="${employee.banksAccounts}" noSelection="['':'EFECTIVO/CHEQUE']" optionKey="id" value="${bankAccount}"/>
            </g:if>
            <g:if test="${employee.status == BusinessEntityStatus.INACTIVE}">
              <span class="glyphicon glyphicon-ban-circle text-danger" aria-hidden="true" title="El empleado está dado de baja"></span>
            </g:if>
          </td>
          <td>
            <g:if test="${employee.status == BusinessEntityStatus.ACTIVE}">
						<input type="text" id="netPayment${employee.id}" name="netPayment${employee.id}" class="form-control text-right" pattern="[0-9]+(\.[0-9]{1,2})?" title="Ingrese una cantidad en formato correcto (número sin decimales o hasta 2 decimales)" value="${netPaymentEmployees[index]}" placeholder="Neto a Pagar" style="width:100px"/>
            </g:if>
            <g:if test="${employee.status == BusinessEntityStatus.INACTIVE}">
              <span class="glyphicon glyphicon-ban-circle text-danger" aria-hidden="true" title="El empleado está dado de baja"></span>
            </g:if>
          </td>
          <td>
						<g:textField class="form-control fixwidth" id="note${employee.id}" name="note${employee.id}" value="" placeholder="Observaciones"/>
          </td>
        </tr>
        </g:each>
      </table>
			</div>
    </div>
  </div>

  <div class="portlet-footer">
    <button class="btn btn-primary text-right" id="add" type="button">Agregar</button>
  </div>
</div>

