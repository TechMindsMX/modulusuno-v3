<table class="table table-striped table-condensed">
    <tr>
        <g:each in="${resultImport.headers}" var="header">
            <th>${header}</th>             
        </g:each>
        <th>RESULTADO</th>
    </tr>
    <g:if test="${resultImport.headers.size() == 19}">
        <g:each in="${resultImport.data}" var="values" status="i">
            <td>${values.PERSONA}</td>
            <td>${values.RFC}</td>
            <td>${values.SITIO_WEB}</td>
            <td>${values.RAZON_SOCIAL}</td>
            <td>${values.PATERNO}</td>
            <td>${values.MATERNO}</td>
            <td>${values.NOMBRE}</td>
            <td>${values.CLAVE_BANCO}</td>
            <td>${values."ULTIMOS_4_DIGITOS_TARJETA"}</td>
            <td>${values.CALLE}</td>
            <td>${values.NUMEXTERIOR}</td>
            <td>${values.NUMINTERIOR}</td>
            <td>${values.CODIGO_POSTAL}</td>
            <td>${values.COLONIA}</td>
            <td>${values."DELEGACION/MUNICIPIO"}</td>
            <td>${values.PAIS}</td>
            <td>${values.CIUDAD}</td>
            <td>${values.ENTIDAD_FEDERATIVA}</td>
            <td>${values.TIPO_DE_DIRECCION}</td>
            <td>${resultImport.results[i]}</td>  
            <tr> </tr>
        </g:each>
    </g:if>
    <g:elseif test="${resultImport.headers.size() == 18}">
        <g:each in="${resultImport.data}" var="values" status="i">
            <td>${values.PERSONA}</td>
            <td>${values.RFC}</td>
            <td>${values.SITIO_WEB}</td>
            <td>${values.RAZON_SOCIAL}</td>
            <td>${values.PATERNO}</td>
            <td>${values.MATERNO}</td>
            <td>${values.NOMBRE}</td>
            <td>${values.CLABE}</td>
            <td>${values.CALLE}</td>
            <td>${values.NUMEXTERIOR}</td>
            <td>${values.NUMINTERIOR}</td>
            <td>${values.CODIGO_POSTAL}</td>
            <td>${values.COLONIA}</td>
            <td>${values."DELEGACION/MUNICIPIO"}</td>
            <td>${values.PAIS}</td>
            <td>${values.CIUDAD}</td>
            <td>${values.ENTIDAD_FEDERATIVA}</td>
            <td>${values.TIPO_DE_DIRECCION}</td>
            <td>${resultImport.results[i]}</td>  
            <tr> </tr>
        </g:each>
    </g:elseif>
    <g:else>
        <g:each in="${resultImport.data}" var="values" status="i">
            <td>${values.RFC}</td>
            <td>${values.CURP}</td>
            <td>${values.PATERNO}</td>
            <td>${values.MATERNO}</td>
            <td>${values.NOMBRE}</td>
            <td>${values.NO_EMPL}</td>
            <td>${values.CLABE}</td>
            <td>${values.NUMTARJETA}</td>
            <td>${values.IMSS}</td>
            <td>${values.NSS}</td>
            <td>${values.FECHA_ALTA}</td>
            <td>${values.BASE_COTIZA}</td>
            <td>${values.NETO}</td>
            <td>${values.PRIMA_VAC}</td>
            <td>${values.DIAS_AGUINALDO}</td>
            <td>${values.PERIODO_PAGO}</td>
            <td>${resultImport.results[i]}</td>  
            <tr> </tr> 
        </g:each>
    </g:else>           
</table>