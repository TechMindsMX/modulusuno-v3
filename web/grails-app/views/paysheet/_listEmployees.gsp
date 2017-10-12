<%! import com.modulus.uno.paysheet.PaysheetStatus %>
<%! import com.modulus.uno.paysheet.PaymentSchema %>

<style>
  th,td {
    white-space: nowrap;
    width: 1px;
  }
</style>

<div class="portlet portlet-default">
  <div class="portlet-heading">
    <div class="portlet-title">
      <h4>Empleados</h4>
    </div>
    <div class="clearfix"></div>
  </div>

  <div class="portlet-body">
    <div class="table-responsive">
      <div class="container-fluid">
        <table class="table table-striped table-condensed">
          <thead>
            <tr>
              <th>No. Empl</th>
              <th>Nombre</th>
              <th>RFC</th>
              <th>CURP</th>
              <th>Cód. Banco</th>
              <th>Banco</th>
              <th>Clabe</th>
              <th>Cuenta</th>
              <th>Tarjeta</th>
							<th>Forma de Pago</th>
              <th>IMSS</th>
              <th>Asimilable</th>
              <th>Subtotal</th>
              <th>Costo Nominal</th>
              <th>Comisión</th>
              <th>IVA</th>
              <th>Total</th>
            </tr>
          </thead>
          <tbody>
            <g:each in="${paysheet.employees.sort{ it.prePaysheetEmployee.nameEmployee }}" var="employee">
              <tr>
                <td>${employee.prePaysheetEmployee.numberEmployee}</td>
                <td>${employee.prePaysheetEmployee.nameEmployee}</td>
                <td>${employee.prePaysheetEmployee.rfc}</td>
                <td>${employee.prePaysheetEmployee.curp}</td>
                <td>${employee.prePaysheetEmployee.bank?.bankingCode}</td>
                <td>${employee.prePaysheetEmployee.bank ? employee.prePaysheetEmployee.bank.name : "EFECTIVO/CHEQUE"}</td>
                <td>${employee.prePaysheetEmployee.clabe}</td>
                <td>${employee.prePaysheetEmployee.account}</td>
                <td>${employee.prePaysheetEmployee.cardNumber}</td>
                <td>
									<g:if test="${employee.prePaysheetEmployee.bank}">
										<g:link action="changePaymentWayFromEmployee" id="${employee.id}" title="Cambiar forma de pago">
											<g:message code="paysheet.payment.way.${employee.paymentWay}"/>
										</g:link>
									</g:if><g:else>
										<g:message code="paysheet.payment.way.${employee.paymentWay}"/>
									</g:else>
								</td>
                <td>${modulusuno.formatPrice(number:employee.imssSalaryNet)}</td>
                <td>${modulusuno.formatPrice(number:employee.salaryAssimilable)}</td>
                <td>${modulusuno.formatPrice(number:employee.totalSalaryEmployee)}</td>
                <td>${modulusuno.formatPrice(number:employee.paysheetCost)}</td>
                <td>${modulusuno.formatPrice(number:employee.commission)}</td>
                <td>${modulusuno.formatPrice(number:employee.paysheetIva)}</td>
                <td>${modulusuno.formatPrice(number:employee.totalToInvoice)}</td>
              </tr>
            </g:each>
          </tbody>
        </table>
      </div>
    </div>
  </div>

  <div class="row">
    <g:if test="${paysheet.status == PaysheetStatus.TO_AUTHORIZE || paysheet.status == PaysheetStatus.AUTHORIZED || paysheet.status == PaysheetStatus.CREATED}">
      <div class="col-md-4">
				<sec:ifAnyGranted roles="ROLE_FICO_EJECUTOR">
        <g:if test="${paysheet.status == PaysheetStatus.AUTHORIZED && !dispersionSummary}">
					<g:link class="btn btn-primary" action="prepareDispersion" id="${paysheet.id}">Dispersar Pagos</g:link>
        </g:if>
				</sec:ifAnyGranted>
      </div>
      <div class="col-md-8 text-right">
        <g:link class="btn btn-default" action="exportToXlsCash" id="${paysheet.id}">XLS EFECTIVO/CHEQUE</g:link>
        <g:link class="btn btn-default" action="exportToXlsImss" id="${paysheet.id}">XLS IMSS</g:link>
        <g:link class="btn btn-default" action="exportToXlsAssimilable" id="${paysheet.id}">XLS Asimilables</g:link>
        <g:link class="btn btn-default" action="exportToXls" id="${paysheet.id}">XLS</g:link>
      </div>
    </g:if>
  </div>
  <div class="row">&nbsp;</div>

  <div class="row">
    <div class="col-md-12">
      <div class="collapse" id="paymentDispersion">
				<div class"=row">
					<div class="col-md-12">
						<g:if test="${paysheet.dispersionFiles}">
							<h4>Archivos de dispersión generados</h4>
							<ul>
								<g:each in="${paysheet.dispersionFiles}" var="dispersionFile">
									<li>
         						<a href="${baseUrlDocuments}/${dispersionFile.title}.${dispersionFile.mimeType}"><i class="glyphicon glyphicon-download-alt"></i> ${dispersionFile.type}</a>
									</li>
								</g:each>
							</ul>
						</g:if>
					</div>
				</div>

      </div>
    </div>
  </div>
