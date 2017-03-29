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
package es.greach.functional.utils

import es.greach.functional.pages.login.LoginPage
import geb.spock.GebSpec
import grails.core.GrailsApplication
import grails.test.mixin.integration.Integration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import spock.lang.Stepwise

@Integration
@Stepwise
abstract class FunctionalTest extends GebSpec {

    @Autowired GrailsApplication grailsApplication

    @Autowired MessageSource messageSource

    /**
     * Logs in the application as a user given the credentials
     * @param username the user's username
     * @param password the user's password
     * @param navigateToLoginPage if true, navigates to the LoginPage. Otherwise it is supposed that we are already at the LoginPage
     */
    void loginAs(String username, String password, boolean navigateToLoginPage = true) {
        if (navigateToLoginPage) {
            to LoginPage
        } else {
            at LoginPage
        }

        loginForm.username = username
        loginForm.password = password
        loginButton.click()
    }
}
