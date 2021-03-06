<div class="portlet portlet-blue">
  <div id="horizontalFormExample" class="panel-collapse collapse in">
    <div class="portlet-body">

      <div class="row">
        <div class="col-md-3">
          <label><strong><g:message code="paysheetContract.label.client"/></strong></label><br/>
          <label>${paysheetContract.client}</label>
        </div>
        <div class="col-md-3">
          <label><strong><g:message code="paysheetContract.label.employerRegistration"/></strong></label><br/>
          <label>${paysheetContract.employerRegistration}</label>
        </div>
        <div class="col-md-3">
          <label><strong><g:message code="paysheetContract.label.initDate"/></strong></label><br/>
          <label><g:formatDate format="dd-MM-yyyy" date="${paysheetContract.initDate}"/></label>
        </div>
        <div class="col-md-3">
          <label><strong><g:message code="paysheetContract.label.executive"/></strong></label><br/>
          <label>${paysheetContract.executive.name}</label>
        </div>
      </div>
      <br/><br/>
      <div class="row">
        <div class="col-md-6">
          <g:link class="btn btn-primary" action="edit" id="${paysheetContract.id}">Editar</g:link>
        </div>
        <div class="col-md-6 text-right">
          <g:link class="btn btn-primary" action="list">Lista</g:link>
        </div>
      </div>

    </div>
  </div>
</div>
