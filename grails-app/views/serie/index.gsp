<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>
        <g:message code="grooflix.title.label"/>
    </title>
</head>
<body>
    <div class="row">
        <div class="col-md-12">
            <h1 class="marginTop10"><g:message code="serie.index.label"/></h1>
        </div>
    </div>
    <g:if test="${series}">
        <g:each in="${series}" var="seriesRow">
            <div class="row">
                <g:each in="${seriesRow}" var="serie">
                    <div class="col-md-4">
                        <div class="thumbnail">
                            <img id="uploader-container-img" src="data:image/png;base64,${serie.cover.bytes.encodeBase64()}" class="img-rounded"/>
                            <div class="caption">
                                <h3>
                                    <g:link controller="serie" action="show" params="[serieId: serie.id]" name="${serie.name}">
                                        ${serie.name}
                                    </g:link>
                                </h3>
                            </div>
                        </div>
                    </div>
                </g:each>
            </div>
        </g:each>
    </g:if>
    <g:else>
        <div class="row">
            <div class="col-md-12">
                <div class="alert alert-warning" role="alert">
                    <sec:ifAnyGranted roles="ROLE_ADMIN">
                        <g:message code="serie.index.empty.admin"/>
                    </sec:ifAnyGranted>
                    <sec:ifNotGranted roles="ROLE_ADMIN">
                        <g:message code="serie.index.empty"/>
                    </sec:ifNotGranted>
                </div>
            </div>
        </div>
    </g:else>
    <sec:ifAnyGranted roles="ROLE_ADMIN">
        <div class="row">
            <div class="col-md-12">
                <g:link action="create" class="btn btn-success">
                    <g:message code="serie.index.create"/>
                </g:link>
            </div>
        </div>
    </sec:ifAnyGranted>
</body>
</html>