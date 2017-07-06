<div class="form-group">
  <label class="col-sm-5">Clabe <span class="required-indicator">*</span></label>
  <div class="col-sm-4">
    <input class="form-control" type="number" id="clabe" name="clabe" max="999999999999999999" required="" value="${bankAccount?.clabe}"/>
  </div>
</div>
<div class="form-group">
  <label class="col-sm-5">Plaza <span class="required-indicator">*</span></label>
  <div class="col-sm-4">
    <input class="form-control" type="number" id="branchNumber" name="branchNumber" max="999" required="" value="${bankAccount?.branchNumber}"/>
  </div>
</div>
<div class="form-group">
  <label class="col-sm-5">Número de Cuenta</label>
  <div class="col-sm-4">
    <input class="form-control" type="number" id="accountNumber" name="accountNumber" readonly="" value="${bankAccount?.accountNumber}"/>
  </div>
</div>
<input id="bank" name="bank" type="hidden" value="${bankAccount?.banco?.bankingCode}"/>
<div class="form-group">
  <label class="col-sm-5">Banco <span class="required-indicator">*</span></label>
  <div class="col-sm-4">
    <g:select name="banco" from="${banks}" optionValue="name" optionKey="bankingCode" class="form-control" aria-controls="example-table" readonly="true" noSelection="['':'']" value ="${bankAccount?.banco?.bankingCode}"/>
  </div>
</div>
<div class="form-group">
  <label class="col-sm-5">Número de Tarjeta</label>
  <div class="col-sm-4">
    <input class="form-control" type="number" id="cardNumber" name="cardNumber" max="9999999999999999" value="${bankAccount?.cardNumber}"/>
  </div>
</div>

<g:if test="${params.companyBankAccount}" >
  <g:if test="${bankLib.checkAccountForSTPAvailable() == '0' || bankAccount?.concentradora }" >
    <div class="center">
      <g:if test="${bankAccount?.concentradora}">
      <input type="checkbox" name="concentradora" value="true" checked="">&nbsp;<label>Cuenta Concentradora</label>
      </g:if><g:else>
      <input type="checkbox" name="concentradora" value="true">&nbsp;<label>Cuenta Concentradora</label>
      </g:else>
    </div>
  </g:if>
  <input type="hidden" name="company" value="${session.company}" />
  <input type="hidden" name="companyBankAccount" value="${params.companyBankAccount}" />
</g:if>
<g:else>
  <input type="hidden" name="businessEntity" value="${params.businessEntity}" />
</g:else>


