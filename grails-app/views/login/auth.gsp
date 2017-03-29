<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title></title>
  <meta name="layout" content="main"/>
</head>
<body>
    <div class="row">
        <div class="col-lg-4 col-lg-offset-4">
            <h3 class="text-center marginTop10"><g:message code="login.login.label"/></h3>
            <hr class="clean">

            <form action="${postUrl}" role="form" method='POST' id='loginForm' class="marginTop10">
                <div class="form-group input-group">
                    <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
                    <input class="form-control" placeholder="${message(code:'springSecurity.login.username.label')}"  name='username' id='username'>
                </div>

                <div class="form-group input-group">
                    <span class="input-group-addon"><i class="glyphicon glyphicon-flash"></i></span>
                    <input type="password" class="form-control" placeholder="${message(code:'springSecurity.login.password.label')}" name='password' id='password'>
                </div>

                <div class="form-group input-group center-block">
                    <p class="text-center">
                        <g:message code="auth.notSignUp.label" />
                        <g:link controller="user" action="create" absolute="true">
                            <g:message code="springSecurity.register.title"/>
                        </g:link>
                    </p>
                </div>
                <div class="form-group input-group center-block">
                    <button type="submit" class="btn btn-naranja btn-block">
                        <span class="glyphicon glyphicon-log-in"></span>
                        <g:message code="login.button.label"/>
                    </button>
                </div>

            </form>
        </div>
    </div>
</body>
</html>