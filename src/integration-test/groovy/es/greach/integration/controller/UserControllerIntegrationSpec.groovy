/*
 * Copyright 2017 Alberto De Ávila Hernández <alberto.deavila.hernandez@gmail.com>
 *                Alberto Barón Cuevas <alberto3363@gmail.com>
 *                Miguel Ángel García Gómez <miguel.angel.gargo@gmail.com>
 * 		  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package es.greach.integration.controller

import es.greach.*
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.util.GrailsWebMockUtil
import org.grails.encoder.CodecLookup
import org.grails.plugins.web.taglib.ValidationTagLib
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.web.context.WebApplicationContext
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNAUTHORIZED

@Integration
@Rollback
class UserControllerIntegrationSpec extends Specification{

    @Autowired UserController userController
    @Autowired WebApplicationContext ctx
    @Autowired MessageSource messageSource

    void setup() {
        GrailsWebMockUtil.bindMockWebRequest(ctx)
    }

    void "show the create view"(){
        when: 'call to the create action'
        Map createResult = userController.create()

        then: 'the action use default configuration to show the view with the same name'
        !createResult
    }

    @Unroll
    void "spend purchased time"(){
        given: 'a user is created'
        User user = User.build(username: UUID.randomUUID(), purchasedTime: purchasedTime)

        and: 'grant admin role to the user'
        Role adminRole = Role.findOrSaveByAuthority('ROLE_ADMIN')
        if(isAdminUser) UserRole.create(user, adminRole)

        when: 'call the action spendPurchasedTime with an authentication (or not) '
        SpringSecurityUtils.doWithAuth (user.username) {
            userController.spendPurchasedTime()
        }

        then: 'check the response'
        userController.response.status == resultStatus.value()

        where:
        isAdminUser | purchasedTime || resultStatus
        false       | 50            || OK
        false       | 0             || OK
        true        | 50            || OK
    }

    void "try to spend purchased time without user in session"(){
        when: 'call the action spendPurchasedTime with an authentication (or not) '
        userController.spendPurchasedTime()

        then: 'the action return BAD_REQUEST'
        userController.response.status == BAD_REQUEST.value()
    }

    @Unroll
    void "saves a user username: #username password: #password email: #email and saved #saved"(){
        given: 'send params to the controller'
        userController.params.username = username
        userController.params.password = password
        userController.params.passwordConfirm = passwordConfirm
        userController.params.email = email

        when: 'save the user'
        userController.save()

        then: 'check if the user is saved'
        userController.response
        if(saved){
            userController.flash.message == messageSource.getMessage('user.save.ok', null, Locale.ENGLISH)
        }else{
            userController.flash.error == messageSource.getMessage(errorMessage, null, Locale.ENGLISH)
        }

        where:
        username   | password   | passwordConfirm | email       || saved | errorMessage
        null       | null       | null            | null        || false | 'user.save.fail.required'
        'username' | null       | null            | null        || false | 'user.save.fail.required'
        'username' | 'password' | null            | null        || false | 'user.save.fail.required'
        'username' | 'password' | null            | null        || false | 'user.save.fail.required'
        'username' | 'password' | null            | 'e@mail.me' || false | 'user.password.notEqual'
        'username' | 'password' | 'password'      | 'e@mail.me' || true  | ''
    }
}
