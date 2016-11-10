<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'feesReceipt.label', default: 'FeesReceipt')}" />
    <title><g:message code="default.create.label" args="[entityName]" /></title>
  </head>
  <body>
    <div class="page-title">
      <h1><g:message code="feesReceipt.create.label" args="[entityName]" /></h1>
      <ol class="breadcrumb">
        <li><i class="fa fa-caret-square-o-up"></i> Compañia</li>
        <li class="active"><g:message code="feesReceipt.active.label"/></li>
      </ol>
    </div>
    <div id="create-address" class="content scaffold-create" role="main">
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

            <div id="create-feesReceipt" class="content scaffold-create" role="main">
              <g:if test="${flash.message}">
              <div class="message" role="status">${flash.message}</div>
              </g:if>
              <g:hasErrors bean="${this.feesReceipt}">
              <ul class="alert alert-danger" role="alert">
                <g:eachError bean="${this.feesReceipt}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                  </g:eachError>
              </ul>
                </g:hasErrors>
                <g:form action="save">
                  <input type="hidden" id="company" name="company" value="${params.company}" />
                  <input type="hidden" id="businessEntity" name="businessEntity" value="${params.businessEntity}" />
                  <input type="hidden" id="bankAccount" name="bankAccount" value="${params.bankAccount}" />
                  <f:with bean="feesReceipt">
                    <f:field property="amount"  label="${message(code:"feesReceipt.amount")}" wrapper="currencyRequired"/>
                  </f:with>
                  <fieldset class="buttons">
                    <g:submitButton name="create" class="save btn btn-default" value="${message(code: 'default.button.create.label', default: 'Create')}" />
                  </fieldset>
                </g:form>
            </div>
          </div>
        </div>
      </div>
    </div>
    </body>
</html>
