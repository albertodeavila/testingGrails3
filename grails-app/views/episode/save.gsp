<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>
        <g:message code="grooflix.title.label"/>
    </title>
    <asset:javascript src="episode/save.js"/>
</head>
<body>
    <div class="row">
        <div class="col-md-12 marginTop10">
            <h1><g:message code="episode.${episode ? 'update' : 'create'}.label"/></h1>
        </div>
    </div>
    <g:form action="save" name="episodeForm" class="form-horizontal marginTop10" enctype="multipart/form-data" >
        <div class="form-group">
            <label for="title" class="col-sm-2 control-label"><g:message code="episode.title.label"/> *</label>
            <div class="col-sm-9">
                <g:textField class="form-control" value="${episode?.title}" name="title" placeholder="${message(code: 'episode.title.label')}"/>
            </div>
        </div>
        <div class="form-group">
            <label for="season" class="col-sm-2 control-label"><g:message code="episode.season.label"/> *</label>
            <div class="col-sm-9">
                <g:textField class="form-control" value="${episode?.season}" name="season" placeholder="${message(code: 'episode.season.label')}"/>
            </div>
        </div>
        <div class="form-group">
            <label for="episodeNumber" class="col-sm-2 control-label"><g:message code="episode.episodeNumber.label"/> *</label>
            <div class="col-sm-9">
                <g:textField class="form-control" value="${episode?.episodeNumber}" name="episodeNumber" placeholder="${message(code: 'episode.episodeNumber.label')}"/>
            </div>
        </div>
        <div class="form-group">
            <label for="season" class="col-sm-2 control-label"><g:message code="episode.summary.label"/></label>
            <div class="col-sm-9">
                <g:textArea class="form-control" value="${episode?.summary}" name="summary" placeholder="${message(code: 'episode.summary.label')}"/>
            </div>
        </div>

        <div class="form-group">
            <label for="video" class="col-sm-2 control-label"><g:message code="episode.video.label"/> *</label>
            <div class="col-sm-9">
                <input type="file" id="video" name="video" class="form-control"/>
            </div>
        </div>
        <g:if test="${episode?.pathToFile}">
            <div class="form-group">
                <label for="name" class="col-sm-2 control-label"><g:message code="episode.file.current.label"/></label>
                <div class="col-sm-9">
                    <video class="col-sm-12" src="${createLink(controller: 'episode', action: 'episodeContent', params: [episodeId: episode.id])}" controls />
                </div>
            </div>
        </g:if>

        <div class="col-md-12 pull-right">
            <g:hiddenField name="serieId" value="${episode ? episode.serie?.id : serie?.id}"/>
            <g:hiddenField name="episodeId" value="${episode?.id}"/>
            <g:submitButton name="episodeSubmitButton" class="btn btn-primary" value="${message(code: 'save.label')}"/>
        </div>
    </g:form>
</body>
</html>