<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>
        <g:message code="grooflix.title.label"/>
    </title>
    <asset:javascript src="serie/save.js"/>
</head>
<body>
    <div class="row">
        <div class="col-md-12 marginTop10">
            <h1><g:message code="serie.${serie ? 'update' : 'create'}.label"/></h1>
        </div>
    </div>
    <g:form action="save" name="serieForm" enctype="multipart/form-data" class="form-horizontal marginTop10">
        <div class="form-group">
            <label for="name" class="col-sm-2 control-label"><g:message code="serie.name.label"/> *</label>
            <div class="col-sm-9">
                <g:textField class="form-control" value="${serie?.name}" name="name" placeholder="${message(code: 'serie.name.label')}"/>
            </div>
        </div>
        <div class="form-group">
            <label for="name" class="col-sm-2 control-label"><g:message code="serie.channel.label"/> *</label>
            <div class="col-sm-9">
                <g:textField class="form-control" value="${serie?.channel}" name="channel" placeholder="${message(code: 'serie.channel.label')}"/>
            </div>
        </div>
        <div class="form-group">
            <label for="name" class="col-sm-2 control-label"><g:message code="serie.releaseDate.label"/> *</label>
            <div class="col-sm-9">
                <g:textField class="form-control datepicker" value="${serie?.releaseDate?.format(message (code:'default.date.format'))}" name="releaseDate" placeholder="${message(code: 'serie.releaseDate.label')}"/>
            </div>
        </div>
        <div class="form-group">
            <label for="name" class="col-sm-2 control-label"><g:message code="serie.actors.label"/></label>
            <div class="col-sm-9">
                <g:textField name="actorsSearch" class="form-control" placeholder="${message(code:'serie.actors.search')}" autocomplete="off" />
                <div id="spinner" class="spinner" style="display:none;">
                    <g:message code="spinner.alt" default="Loading&hellip;"/>
                </div>
                <g:hiddenField name="actorsSearchURL" value="${createLink(controller: 'actor', action: 'searchActorsAjax', absolute: true)}"/>
                <ul class="list-group" id="actorsDiv">
                    <g:each in="${serie?.actors}" var="actor">
                        <li class="list-group-item"><span class="glyphicon glyphicon-user"></span>
                            ${actor.name}
                            <a href="#" class="btn btn-danger pull-right deleteActor" onclick="deleteActorDiv('${actor.imdbId}');">
                                <span class="glyphicon glyphicon-trash"/>
                            </a>
                            <input type="hidden" id="actors" name="actors" value="${actor.imdbId}"/>
                        </li>
                    </g:each>
                </ul>
            </div>
        </div>
        <div class="form-group">
            <label for="name" class="col-sm-2 control-label"><g:message code="serie.cover.label"/></label>
            <div class="col-sm-9">
                <input type="file" id="cover" name="cover" class="form-control"/>
            </div>
        </div>
        <g:if test="${serie?.pathToCover}">
            <div class="form-group">
                <label for="name" class="col-sm-2 control-label"><g:message code="serie.cover.current.label"/></label>
                <div class="col-sm-9">
                    <img id="uploader-container-img" src="data:image/png;base64,${serie.cover.bytes.encodeBase64()}" class="img-rounded"/>
                </div>
            </div>
        </g:if>

        <div class="col-md-12 pull-right">
            <g:hiddenField name="serieId" value="${serie?.id}"/>
            <g:submitButton name="serieSubmitButton" class="btn btn-primary" value="${message(code: 'save.label')}"/>
        </div>
    </g:form>
</body>
</html>