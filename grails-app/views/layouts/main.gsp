<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>
        <g:layoutTitle default="Grooflix"/>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>

    <asset:link rel="shortcut icon" href="netflix-icon.png" type="image/x-icon"/>


    <asset:stylesheet src="bootstrap.css"/>
    <asset:stylesheet src="bootstrap-datepicker3.css"/>
    <asset:stylesheet src="fileinput.css"/>
    <asset:stylesheet src="application.css"/>

    <asset:javascript src="application.js"/>
    <asset:javascript src="bootstrap.js"/>
    <asset:javascript src="bootstrap-datepicker.js"/>
    <asset:javascript src="bootstrap-typeahead.js"/>
    <asset:javascript src="fileinput.js"/>

    <asset:javascript src="plugins/datatables/jquery.dataTables.js"/>
    <asset:javascript src="plugins/datatables/DT_bootstrap.js"/>
    <asset:javascript src="plugins/datatables/dt-translations.js"/>

    <g:layoutHead/>
</head>
<body>
    <div class="navbar navbar-static-top" role="navigation">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="/#">
                    <i class="fa grails-icon">
                        <asset:image src="netflix-icon.png"/>
                    </i> <g:message code="grooflix.title.label"/>
                </a>
            </div>
            <div class="navbar-collapse collapse" aria-expanded="false" style="height: 0.8px;">
                <ul class="nav navbar-nav navbar-right">
                    <g:pageProperty name="page.nav" />
                    <sec:ifLoggedIn>
                        <li>
                            <g:link controller="logout">
                                <span class="glyphicon glyphicon-log-out"></span>
                                <g:message code="logout.button.label"/>
                            </g:link>
                        </li>
                    </sec:ifLoggedIn>
                </ul>
            </div>
        </div>
    </div>

    <div class="container-fluid">
        <g:if test="${flash.message || flash.error}">
            <div class="alert alert-${flash.error ? 'danger' : 'success'}" role="alert">
                ${flash.error ?: flash.message}
            </div>

        </g:if>
        <g:layoutBody/>
    </div>

    <div class="footer" role="contentinfo"></div>

</body>
</html>
