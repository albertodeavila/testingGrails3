<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>
        <g:message code="grooflix.title.label"/>
    </title>
    <asset:javascript src="episode/show.js"/>
</head>
<body>
    <g:set var="serie" value="${episode.serie}"/>
    <div class="row">
        <div class="col-md-12">
            <div class="col-md-12">
                <h1 class="marginTop10">
                    <g:link controller="serie" action="show" params="[serieId: serie.id]">${serie.name}</g:link> / <g:link controller="episode" action="show" params="[episodeId: episode.id]">${episode.title}</g:link>
                </h1>
            </div>
            <div class="col-md-4 marginTop10">
                <img id="uploader-container-img" src="data:image/png;base64,${serie.cover.bytes.encodeBase64()}" class="img-rounded"/>
                <div class="marginTop10" id="editEpisodeButtonsDiv">
                    <sec:ifAnyGranted roles="ROLE_ADMIN">
                        <g:link class="btn btn-primary" controller="episode" action="update" params="[episodeId: episode.id]">
                            <g:message code="episode.edit.label"/>
                        </g:link>

                        <g:link class="btn btn-danger" controller="episode" action="delete" params="[episodeId: episode.id]">
                            <g:message code="episode.delete.label"/>
                        </g:link>
                    </sec:ifAnyGranted>
                </div>
            </div>
            <div class="col-md-8 marginTop10">
                <div class="form-horizontal">
                    <div class="form-group">
                        <label for="title" class="col-md-3 control-label">
                            <g:message code="episode.title.label"/>
                        </label>
                        <div class="col-sm-8">
                            <p class="form-control-static">
                                ${episode.title}
                            </p>
                        </div>
                    </div>
                </div>
                <div class="form-horizontal">
                    <div class="form-group">
                        <label for="season" class="col-md-3 control-label">
                            <g:message code="episode.season.label"/>
                        </label>
                        <div class="col-md-8">
                            <p class="form-control-static">
                                ${episode.season}
                            </p>
                        </div>
                    </div>
                </div>
                <div class="form-horizontal">
                    <div class="form-group">
                        <label for="episodeNumber" class="col-md-3 control-label"><g:message code="episode.episodeNumber.label"/></label>
                        <div class="col-md-8">
                            <p class="form-control-static">
                                ${episode.episodeNumber}
                            </p>
                        </div>
                    </div>
                </div>
                <g:if test="${episode.summary}">
                    <div class="form-horizontal">
                        <div class="form-group">
                            <label for="summary" class="col-md-3 control-label">
                                <g:message code="episode.summary.label"/>
                            </label>
                            <div class="col-md-8">
                                <p class="form-control-static">
                                    ${episode.summary}
                                </p>
                            </div>
                        </div>
                    </div>
                </g:if>
            </div>
        </div>
    </div>
    <div class="row marginTop10">
        <div class="col-md-12">
            <h1><g:message code="episode.video.label"/></h1>
            <video id="video" src="${createLink(controller: 'episode', action: 'episodeContent', params: [episodeId: episode.id])}"
                   controls class="col-md-12"/>
        </div>
    </div>
    <g:hiddenField name="spendPurchasedTimeURL" value="${createLink(controller: 'user', action: 'spendPurchasedTime', absolute: true)}" />
</body>
</html>