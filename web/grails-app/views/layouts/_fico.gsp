<li class="panel">
  <a href="javascript:;" data-parent="#side" data-toggle="collapse" class="accordion-toggle" data-target="#ROLE_FICO">
    <g:message code="role_fico" /><i class="fa fa-caret-down"></i>
  </a>
  <ul class="collapse nav" id="ROLE_FICO">
    <li>
      <g:link controller="dashboard" action="jobs">
        <i class="fa fa-book"></i> Ejecuciones
      </g:link>
    </li>
    <li><g:link controller="payment" action="conciliation">Conciliación de Cobros</g:link></li>
    <li>
      <g:if test="${companyInfo.isAvailableForOperationInThisCompany() == 'true'}">
      <g:link controller="company" action="accountStatement">Estado de Cuenta</g:link>
      </g:if>
    </li>
  </ul>
</li>
