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
package es.greach.integration.service

import es.greach.UserService
import grails.plugin.greenmail.GreenMail
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Integration
@Rollback
class UserServiceIntegrationSpec extends Specification {

    @Autowired UserService userService
    @Autowired MessageSource messageSource
    @Shared GreenMail greenMail

    /*************************************
        SERVICE INTEGRATION EXERCISE 1
     **************************************/
    @Ignore("Until start work on service integration exercise 1")
    @Unroll
    void "Saves a user with username: #username password: #password email: #email and saved #saved"() {
        when: 'call to the service to save the user'
        //TODO complete me

        then: 'check if the user is saved correctly and if the user receives an email'
        //TODO complete me

        where:
        username   | password   | email       || saved
        null       | null       | null        || false
        'username' | null       | null        || false
        'username' | 'password' | null        || false
        'username' | 'password' | null        || false
        'username' | 'password' | 'e@mail.me' || true
    }

    /*************************************
        SERVICE INTEGRATION EXERCISE 2
     **************************************/
    @Ignore("Until start work on service integration exercise 2")
    void "delete a user"(){
        //TODO complete me
    }

    /*************************************
        SERVICE INTEGRATION EXERCISE 3
     **************************************/
    @Ignore("Until start work on service integration exercise 3")
    void "try to delete a user"(){
        expect: 'the service return null when this value is passed as user'
        //TODO complete me
    }

    /*************************************
        SERVICE INTEGRATION EXERCISE 4
     **************************************/
    @Ignore("Until start work on service integration exercise 4")
    void "spend purchased time "(){
        //TODO complete me
    }

    void cleanup() {
        greenMail.deleteAllMessages()
    }
}
