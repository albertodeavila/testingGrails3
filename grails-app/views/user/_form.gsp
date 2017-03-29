<div class="form-group input-group">
    <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
    <g:textField name="username" class="form-control" value="${user?.username}" placeholder="${g.message(code: 'user.username.label')}" />
</div>

<div class="form-group input-group">
    <span class="input-group-addon"><i class="glyphicon glyphicon-envelope"></i></span>
    <g:textField name="email" class="form-control" value="${user?.email}" placeholder="${g.message(code: 'user.email.label')}"/>
</div>

<div class="form-group input-group">
    <span class="input-group-addon"><i class="glyphicon glyphicon-flash"></i></span>
    <g:passwordField name="password" class="form-control" placeholder="${g.message(code: 'user.password.label')}"/>
</div>

<div class="form-group input-group">
    <span class="input-group-addon"><i class="glyphicon glyphicon-flash"></i></span>
    <g:passwordField name="passwordConfirm" class="form-control" placeholder="${g.message(code: 'user.password.label.confirm')}"/>
</div>
