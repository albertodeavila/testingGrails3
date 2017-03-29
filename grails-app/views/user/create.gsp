<html>
<head>
    <title></title>
    <meta name="layout" content="main"/>
</head>
<body>
<div class="display-table">
    <div class="container display-table-cell">
        <div class="row">
            <div class="col-lg-6 col-lg-offset-3 marginTop10">
                <h3 class="text-center">
                    <g:message code="user.sign.up"/>
                </h3>

                <g:form name="user-form-register" action="save" class="sign-up marginTop10">
                    <g:render template="form"/>

                    <div class="form-group input-group center-block">
                        <button type="submit" class="btn btn-naranja btn-block">
                            <span class="glyphicon glyphicon-log-in"></span>
                            <g:message code="default.button.register.label"/>
                        </button>
                    </div>
                </g:form>
            </div>
        </div>
    </div>
</div>
</body>
</html>