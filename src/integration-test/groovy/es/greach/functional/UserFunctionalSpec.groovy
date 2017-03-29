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
package es.greach.functional

import es.greach.functional.pages.login.RegisterPage
import es.greach.functional.pages.serie.IndexSeriePage
import es.greach.functional.utils.FunctionalTest
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Unroll

@Integration
@Rollback
class UserFunctionalSpec extends FunctionalTest {

    @Unroll
    void "register a new user with wrong data: username: #name, email: #mail, password: #pass, confirm password: #confirmPass"(){
        given:'go to the register page'
        to RegisterPage

        and: 'fill the data'
        username = name
        email = mail
        password = pass
        passwordConfirm = confirmPass

        when: 'click in the register button'
        registerButton.click()

        then: 'an error message appears'
        at RegisterPage
        messages.errorMessage == messageSource.getMessage(messageExpected, null, null)

        where:
        name | mail         | pass   | confirmPass || messageExpected
        ''   | ''           | ''     | ''          || 'user.save.fail.required'
        'a'  | ''           | ''     | ''          || 'user.save.fail.required'
        'a'  | 'a'          | ''     | ''          || 'user.save.fail.required'
        'a'  | 'aa@bb.com'  | ''     | ''          || 'user.save.fail.required'
        'a'  | 'aa@bb.com'  | '1234' | ''          || 'user.password.notEqual'
    }

    void "register a user and login"(){
        given:'go to the register page'
        to RegisterPage

        and: 'fill the data'
        String name = UUID.randomUUID().toString()
        String pass = '1234'

        username = name
        email = 'someMail'
        password = pass
        passwordConfirm = pass

        when: 'click in the register button'
        registerButton.click()

        then: 'we are at register page'
        at RegisterPage

        and: 'a succes message is shown'
        messages.successMessage == messageSource.getMessage('user.save.ok', [name].toArray(), null)

        when: 'fill username and password'
        loginAs(name, pass)

        then: "serie's index page is shown"
        at IndexSeriePage
    }

    void "Login application as an admin"() {
        given: 'admin credentials'
        String adminUsername = 'admin'
        String adminPassword = '1234'

        when: 'login in the app'
        loginAs(adminUsername, adminPassword)

        then: "serie's index page is shown"
        at IndexSeriePage
    }
}
