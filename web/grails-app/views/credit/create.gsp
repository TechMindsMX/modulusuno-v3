<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'credit.label', default: 'Credits')}" />
    <title><g:message code="default.create.label" args="[entityName]" /></title>
  </head>
  <body>

    <div class="page-title">
      <h1>
        <i class="fa fa-usd fa-3x"></i>
        Créditos / ${company.bussinessName}
        <small>Creación de crédito</small>
      </h1>
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

            <g:hasErrors bean="${credit}">
              <ul class="error alert alert-danger" role="alert">
                <g:eachError bean="${credit}" var="error">
                  <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>>
                    <g:message error="${error}"/></li>
                </g:eachError>
              </ul>
            </g:hasErrors>

            <g:form action="save">
              <fieldset class="form">
                <g:render template="form" bean="${credit}"/>
              </fieldset>

                <div class="form-group">
                  <div class="text-right">
                    <g:submitButton name="create" class="save btn btn-default" value="${message(code: 'default.button.create.label', default: 'Create')}" />
                  </div>
                </div>
            </g:form>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>
