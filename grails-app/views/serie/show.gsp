<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>
        <g:message code="grooflix.title.label"/>
    </title>


    <asset:javascript src="serie/show.js"/>

</head>
<body>
    <div class="row">
        <div class="col-md-12">
            <div class="col-md-12">
                <h1 class="marginTop10">${serie.name}</h1>
            </div>
            <div class="col-md-4 marginTop10">
                <img id="cover" src="data:image/png;base64,${serie.cover.bytes.encodeBase64()}" class="img-rounded"/>
                <div class="marginTop10" id="editSerieButtonsDiv">
                    <sec:ifAnyGranted roles="ROLE_ADMIN">
                        <g:link class="btn btn-primary" controller="serie" action="update" params="[serieId: serie.id]">
                            <g:message code="serie.edit.label"/>
                        </g:link>

                        <g:link class="btn btn-success" controller="episode" action="create" params="[serieId: serie.id]">
                            <g:message code="serie.edit.addEpisode"/>
                        </g:link>

                        <g:link class="btn btn-danger" controller="serie" action="delete" params="[serieId: serie.id]">
                            <g:message code="serie.edit.delete"/>
                        </g:link>
                    </sec:ifAnyGranted>
                </div>
            </div>
            <div class="col-md-8 marginTop10">
                <div class="form-horizontal">
                    <div class="form-group">
                        <label for="channel" class="col-md-3 control-label"><g:message code="serie.channel.label"/></label>
                        <div class="col-sm-8">
                            <p class="form-control-static">
                                ${serie.channel}
                            </p>
                        </div>
                    </div>
                </div>
                <div class="form-horizontal">
                    <div class="form-group">
                        <label for="releaseDate" class="col-md-3 control-label"><g:message code="serie.releaseDate.label"/></label>
                        <div class="col-md-8">
                            <p class="form-control-static">
                                ${serie?.releaseDate?.format(message (code:'default.date.format'))}
                            </p>
                        </div>
                    </div>
                </div>
                <g:if test="${serie.actors}">
                    <div class="form-horizontal">
                        <div class="form-group">
                            <label for="actors" class="col-md-3 control-label"><g:message code="serie.actors.label"/></label>
                            <div class="col-md-8">
                                <g:each in="${serie.actors}" var="actor">
                                    <div class="col-md-4">
                                        <div class="thumbnail">
                                            <img id="actor.${actor.id}" src="${actor.imageUrl}" class="img-rounded"/>
                                            <div class="caption">
                                                <h3>
                                                    ${actor.name}
                                                </h3>
                                            </div>
                                        </div>
                                    </div>
                                </g:each>
                            </div>
                        </div>
                    </div>
                </g:if>
            </div>
        </div>
    </div>
    <g:if test="${serie.episodes}">
        <div class="row">
            <div class="col-md-12">
                <h3 class="marginTop10"><g:message code="serie.episodes.label"/></h3>
            </div>
        </div>
        <table class="table table-hover" id="datatable-episodesTable">
            <thead>
                <td>#</td>
                <td><g:message code="serie.episodes.title.label"/></td>
                <td><g:message code="serie.episodes.season.label"/></td>
                <td><g:message code="serie.episodes.episodeNumber.label"/></td>
            </thead>
            <tbody>
                <g:each in="${serie.episodes}" var="episode">
                    <tr onclick="window.location.href='${createLink(controller: 'episode', action: 'show', params: [episodeId: episode.id], absolute: true)}'">
                        <td>
                            ${episode.id}
                        </td>
                        <td>
                            ${episode.title}
                        </td>
                        <td>
                            ${episode.season}
                        </td>
                        <td>
                            ${episode.episodeNumber}
                        </td>
                    </tr>
                </g:each>
            </tbody>
        </table>
    </g:if>
</body>
</html>