<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'menu.label', default: 'Menu')}" />
    <title><g:message code="default.show.label" args="[entityName]" /></title>
  </head>
  <body>

    <div class="row">
      <!-- BEGIN PAGE TITLE -->
      <div class="page-title">
        <h1>
          <i class="fa fa-info-circle fa-3x"></i>
          <g:message code="menu.show"/>
        </h1>
      </div>
      <!-- END OF PAGE TITLE -->
    </div>

    <g:if test="${flash.message}">
    <div class="alert alert-info" role="alert">
        <div class="message" role="status">${flash.message}</div>
    </div>
    </g:if>

    <div class="row">
      <div class="col-md-6">
        <div class="portlet portlet-default">
          <div class="portlet-heading">
            <div class="portlet-title">
              <h4>Datos de la operación de menú</h4>
            </div>
            <div class="clearfix"></div>
          </div>
          <div id="defaultPortlet" class="panel-collapse collapse in">
            <div class="portlet-body">
              <ul class="property-list menu">
                <f:display bean="menu" property="name" wrapper="show" />
                <f:display bean="menu" property="internalUrl" wrapper="show" />
                <f:display bean="menu" property="parameters" wrapper="show" />
              </ul>
              <div class="property-value" aria-labelledby="menu-label">
                <g:form action="delete" method="DELETE" id="${menu.id}">
                  <g:link class="edit btn btn-primary" action="edit" resource="${this.menu}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                  <g:link class="edit btn btn-default" action="index" resource="${this.menu}"><g:message code="default.button.index.label" default="Todos los menues" /></g:link>
                  <input class="delete btn btn-danger" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                </g:form>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="col-md-6">
        <div class="portlet portlet-default">
          <div class="portlet-heading">
            <div class="portlet-title">
              <h4>Submenues</h4>
            </div>
            <div class="clearfix"></div>
          </div>
          <div id="defaultPortlet" class="panel-collapse collapse in">
            <div class="portlet-body">
              <table class="table table-condensed">
                <thead>
                  <tr>
                    <th>Nombre del menú</th>
                    <th>&nbsp;</th>
                  </tr>
                </thead>
                <tbody>
                <g:if test="${menu.menus}">
                  <g:each in="${menu.menus}" var="m">
                  <tr>
                    <td> <g:link action="show" id="${m.id}"> ${m} </g:link> </td>
                    <td>
                      <g:form action="removeSubmenu" method="DELETE" id="${menu.id}">
                      <g:hiddenField name="submenuId" value="${m.id}"></g:hiddenField>
                      <button class="btn btn-danger" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Seguro?')}');">
                        Quitar operación
                      </button>
                      </g:form>
                    </td>
                  </tr>
                  </g:each>
                </g:if>
                <g:else>
                  <tr>
                    <td colspan="2"><b>No hay submenues</b></td>
                  </tr>
                </g:else>
                </tbody>
              </table>
              <div class="property-value" aria-labelledby="menu-label">
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="row">
      <div class="col-md-12">
        <div class="portlet portlet-default">
          <div class="portlet-heading">
            <div class="portlet-title">
              <h4>Menus no incluidos</h4>
            </div>
            <div class="clearfix"></div>
          </div>
          <div id="defaultPortlet" class="panel-collapse collapse in">
						<g:form action="addSubmenu" id="${menu.id}">
            <div class="portlet-body">
              <table class="table table-condensed table-striped">
                <thead>
                  <tr>
                    <th>Nombre del menú</th>
                    <th>&nbsp;</th>
                  </tr>
                </thead>
                <tbody>
                <g:if test="${menusNotIncluded}">
                  <g:hiddenField name="parentMenuId" value="${menu.id}" />
                  <g:each in="${menusNotIncluded}" var="m">
                  <tr>
                    <td> <g:link action="show" id="${m.id}"> ${m} </g:link> </td>
                    <td>
                      <g:checkBox name="menuOption" value="${m.id}" class="form-check-input" checked="false"></g:checkBox>
                    </td>
                  </tr>
                  </g:each>
                </g:if>
                <g:else>
                  <tr>
                    <td colspan="2"><b>Todos los menues incluidos</b></td>
                  </tr>
                </g:else>
                </tbody>
              </table>
							<g:submitButton name="submit" value="Agregar submenues" class="btn btn-default"></g:submitButton>
            </div>
						</g:form>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>
