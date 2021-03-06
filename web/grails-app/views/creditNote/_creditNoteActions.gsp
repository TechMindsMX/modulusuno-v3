<%! import com.modulus.uno.status.CreditNoteStatus %>
<%! import com.modulus.uno.RejectReason %>
<g:if test="${[CreditNoteStatus.XML_GENERATED, CreditNoteStatus.APPLIED].contains(creditNote.status) && isEnabledToStamp}">
  <div class="text-right">
    <a href="${modulusuno.creditNoteUrl(creditNote:creditNote, format:'xml')}" class="btn btn-success" download>XML</a>
    <g:if test="${creditNote.status == CreditNoteStatus.APPLIED}">
      <a href="${modulusuno.creditNoteUrl(creditNote:creditNote, format:'pdf')}" class="btn btn-default" download>PDF</a>
    </g:if>
    <g:if test="${creditNote.status == CreditNoteStatus.XML_GENERATED}">
      <g:link class="btn btn-primary" action="generatePdf" id="${creditNote.id}">Generar PDF</g:link>
    </g:if>
    <sec:ifAnyGranted roles="ROLE_LEGAL_REPRESENTATIVE_EJECUTOR,ROLE_OPERATOR_EJECUTOR">
      <g:link class="btn btn-danger" action="requestCancelCreditNote" id="${creditNote.id}">Solicitar Cancelación</g:link>
    </sec:ifAnyGranted>
  </div>
</g:if>

<g:if test="${creditNote.status == CreditNoteStatus.CANCEL_APPLIED && isEnabledToStamp}">
  <div class="text-right">
    <a href="${modulusuno.cancelAccuseUrl(creditNote:creditNote, format:'xml')}" class="btn btn-default" download>Acuse XML</a>
    <a href="${modulusuno.cancelAccuseUrl(creditNote:creditNote, format:'pdf')}" class="btn btn-default" download>Acuse PDF</a>
  </div>
</g:if>

<sec:ifAnyGranted roles="ROLE_LEGAL_REPRESENTATIVE_EJECUTOR,ROLE_OPERATOR_EJECUTOR">
  <g:if test="${creditNote.status == CreditNoteStatus.CREATED}">
    <div class="text-right">
    <g:if test="${creditNote.items}">
      <g:link class="btn btn-primary" action="requestAuthorization" id="${creditNote.id}">Solicitar Autorización</g:link>
    </g:if>
    
    <button type="button" class="btn btn-danger" data-toggle="modal" data-target="#modalConfirm">
      <i class="fa fa-trash"></i> Borrar
    </button>
    </div>

    <div class="modal fade" id="modalConfirm" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            <h4 class="modal-title" id="myModalLabel">Confirme la acción</h4>
          </div>
          <div class="modal-body">
            ¿Está seguro de eliminar la nota de crédito?
          </div>
          <div class="modal-footer">
            <g:link class="btn btn-primary" action="deleteCreditNote" id="${creditNote.id}">Sí</g:link>
            <button type="button" class="btn btn-default" data-dismiss="modal">No</button>
          </div>
        </div>
      </div>
    </div>
  </g:if>

</sec:ifAnyGranted>

<sec:ifAnyGranted roles="ROLE_AUTHORIZER_EJECUTOR">
  <g:if test="${creditNote.status == CreditNoteStatus.TO_AUTHORIZE}">
    <div class="text-right">
      <g:link class="btn btn-primary" action="authorize" id="${creditNote.id}">Autorizar</g:link>
      <a data-toggle="collapse" role="button" href="#inputReasonCancellation" class="btn btn-danger" aria-expanded="false" aria-controls="inputReasonCancellation">Cancelar</a>
           <div class="row">
              <div class="col-md-12">
                <br/>
                <div class="collapse" id="inputReasonCancellation">
                  <div class="well">
                    <g:form action="cancelCreditNote" id="${creditNote.id}">
                    <div class="form-group">
                      <g:select name="rejectReason" from="${RejectReason.values()}" optionKey="name" optionValue="description" value="${creditNote.rejectReason}" class="form-control" />
                      <br/>
                      <g:textArea name="comments" placeholder="Comentarios opcionales" rows="3" cols="60" maxLength="255" class="form-control"/>
                      <br/>
                      <button type="submit" class="btn btn-danger">Ejecutar Cancelación</button>
                    </div>
                    </g:form>
                  </div>
                </div>
              </div>
            </div>

    </div>
  </g:if>
  <g:if test="${creditNote.status == CreditNoteStatus.CANCEL_TO_AUTHORIZE}">
    <div class="text-right">
      <g:link class="btn btn-danger" action="authorizeCancelCreditNote" id="${creditNote.id}">Autorizar Cancelación</g:link>
    </div>
  </g:if>
</sec:ifAnyGranted>

<sec:ifAnyGranted roles="ROLE_FICO_EJECUTOR">
  <g:if test="${creditNote.status == CreditNoteStatus.AUTHORIZED && isEnabledToStamp}">
    <div class="text-right">
      <g:link class="btn btn-primary" action="apply" id="${creditNote.id}">Aplicar</g:link>
      <a data-toggle="collapse" role="button" href="#inputReasonReject" class="btn btn-danger" aria-expanded="false" aria-controls="inputReasonReject">Rechazar</a>

          <div class="row">
            <div class="col-md-12">
              <br/>       
              <div class="collapse" id="inputReasonReject">
                <div class="well">
                  <g:form action="rejectCreditNote" id="${creditNote.id}">
                    <div class="form-group">
                      <g:select name="rejectReason" from="${RejectReason.values()}" optionKey="name" optionValue="description" value="${creditNote.rejectReason}" class="form-control" />
                      <br/>
                      <g:textArea name="comments" placeholder="Comentarios opcionales" rows="3" cols="60" maxLength="255" class="form-control"/>
                      <br/>
                      <button type="submit" class="btn btn-danger">Rechazar</button>
                    </div>
                  </g:form>
                </div>
              </div>
            </div>
          </div>

    </div>
  </g:if>

  <g:if test="${creditNote.status == CreditNoteStatus.CANCEL_AUTHORIZED && isEnabledToStamp}">
    <div class="text-right">
      <g:link class="btn btn-danger" action="applyCancelCreditNote" id="${creditNote.id}">Ejecutar Cancelación</g:link>
    </div>
  </g:if>

  <g:if test="${!isEnabledToStamp}">
    <div class="alert alert-warning">
      No está habilitado para timbrar facturas, debe registrar su certificado y su domicilio fiscal
    </div>
  </g:if>
</sec:ifAnyGranted>
