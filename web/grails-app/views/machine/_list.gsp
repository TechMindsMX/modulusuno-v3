<script id="machine-list-template" type="text/x-handlebars-template">
{{#if machines}}
  <table class="table">
    <thead>
      <tr>
        <th>Uuid</th>
        <th>Fecha de Creación</th>
        <th></th>
      </tr>
    </thead>
    <tbody>
      {{#each machines}}
      <tr>
        <th>{{uuid}}</th>
        <th>{{dateCreated}}</th>
        <th>
          <a class="btn btn-primary" href="${createLink(controller:'machine',action:'show')}/{{uuid}}">Ver</a>
        </th>
      </tr>
      {{/each}}
    </tbody>
  </table>
{{/if}}
</script>
