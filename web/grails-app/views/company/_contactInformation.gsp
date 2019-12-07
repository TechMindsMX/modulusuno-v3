<div class="property-value" aria-labelledby="${label}-label">
  <div class="panel-group" id="contactInformation" role="tablist" aria-multiselectable="true">
    <g:each var="contact" in="${company.contacts.sort{ it.id }}">
      <sec:ifAnyGranted roles="ROLE_CORPORATIVE">
        <div class="panel panel-default">
          <div class="panel-heading" role="tab" id="heading_${contact.id}">
            <h4 class="panel-title">
              <a role="button" data-toggle="collapse" data-parent="#contactInformation" href="#collapse_${contact.id}" aria-expanded="true" aria-controls="collapse_${contact.id}" style="color:inherit !important;">
                ${contact.department} - ${contact.position}
              </a>
            </h4>
          </div>
          <div id="collapse_${contact.id}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading_${contact.id}">
            <div class="panel-body">
              <address>
                <strong>${contact.name}</strong><br>
                <g:each var="telephone" in="${contact.telephones.sort{ it.id }}">
                  <div>
                    ${telephone.type}: ${telephone.number} <g:if test="${telephone.extension}">,${telephone.extension}</g:if>
                  </div>
                </g:each>
              </address>
            </div>
          </div>
        </div>
      </sec:ifAnyGranted>

      <sec:ifAnyGranted roles="ROLE_LEGAL_REPRESENTATIVE_EJECUTOR,ROLE_LEGAL_REPRESENTATIVE_VISOR">
        <div class="panel panel-default">
          <div class="panel-heading" role="tab" id="heading_${contact.id}">
            <h4 class="panel-title">
              <a role="button" data-toggle="collapse" data-parent="#contactInformation" href="#collapse_${contact.id}" aria-expanded="true" aria-controls="collapse_${contact.id}" style="color:inherit !important;">
                ${contact.department} - ${contact.position}
              </a>

              <span class="pull-right">
                <g:link controller="contactInformation" action="edit" id="${contact.id}">Editar</g:link>
              <span>
            </h4>
          </div>
          <div id="collapse_${contact.id}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading_${contact.id}">
            <div class="panel-body">
              <address>
                <strong>${contact.name}</strong><br>

                <g:each var="telephone" in="${contact.telephones.sort{ it.id }}">
                  <div>
                    <i class="fa fa-phone"></i>
                    <g:link controller="telephone" action="editForContact" id="${telephone.id}" >
                      ${telephone.type}: ${telephone.number} <g:if test="${telephone.extension}">,${telephone.extension}</g:if>
                    </g:link>
                  </div>
                </g:each>

                <g:each var="email" in="${contact.emails.sort{ it.id }}">
                  <div>
                    <i class="fa fa-envelope"></i>
                    <g:link controller="email" action="editForContact" id="${email.id}" >
                      ${email.type}: ${email.address}
                    </g:link>
                  </div>
                </g:each>
              </address>

              <div class="row pull-right">
                <div class="col-md-6">
                  <g:link action="createForContact" controller="telephone" id="${contact.id}" class="btn btn-default">Agregar Teléfono</g:link>
                </div>
                <div class="col-md-6">
                  <g:link action="createForContact" controller="email" id="${contact.id}" class="btn btn-default">Agregar Email</g:link>
                </div>
              </div>
            </div>
          </div>
        </div>

      </sec:ifAnyGranted>
    </g:each>
  </div>
</div>
<sec:ifAnyGranted roles="ROLE_LEGAL_REPRESENTATIVE_EJECUTOR">
  <div class="text-right">
  	<g:link action="createForCompany" controller="contactInformation" class="btn btn-default">Agregar contacto</g:link>
  </div>
</sec:ifAnyGranted>


