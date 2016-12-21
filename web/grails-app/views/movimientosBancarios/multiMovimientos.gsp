<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'movimientosBancarios.label', default: 'MovimientosBancarios')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div id="list-movimientosBancarios" class="content scaffold-list" role="main">
            <h1>Subir Movimientos Bancarios </h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
              <g:hasErrors bean="${flash.command}">
              <ul class="error alert alert-danger" role="alert">
                  <g:eachError bean="${flash.command}" var="error">
                  <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                  </g:eachError>
              </ul>
              </g:hasErrors>

            <p>
              <center>
               <b>La Fecha del día de hoy es: ${new Date().format("dd/MMMM/yyy")}</b>
              </center>
            </p>
            <g:form action="multiMovimientosBancarios" method="POST" name="multiMovimientosBancarios" enctype="multipart/form-data">
              <p><g:select name="cuenta" from="${bankAccountsOfCompany}"  optionKey="id" class="form-control" /></p>
              <p><input type="file" name="multiMovimientos" class="form-control" required="required"/></p>
              <p><input type="submit" value="Subir" class="btn btn-info"/></p>
            </g:form>
        </div>
    </body>
</html>
