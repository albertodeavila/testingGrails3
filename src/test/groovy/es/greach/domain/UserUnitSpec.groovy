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
package es.greach.domain

import es.greach.User
import grails.test.mixin.TestFor
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(User)
class UserUnitSpec extends Specification{

    /*************************************
        DOMAIN UNIT EXERCISE EXTRA 1
     **************************************/
    @Ignore("Until start work on domain unit exercise extra 1")
    @Unroll
    void "Validates a user's instance that must be valid: #valid, with params: #params"(){
        given: 'a user built with some params'
        //TODO complete me

        expect: 'the serie is valid or not'
        //TODO complete me

        and: 'and if it is valid, save it and check the params '
        //TODO complete me

        where:
        expectedValidate || params
        null             || null
        //TODO complete me
    }

    /*************************************
        DOMAIN UNIT EXERCISE EXTRA 2
     **************************************/
    @Ignore("Until start work on domain unit exercise extra 2")
    @Unroll
    void "check if a user is admin: #expectedIsAdmin"(){
        given: 'a new user'
        //TODO complete me

        and: 'grant to the user the ROLE_ADMIN role'
        //TODO complete me

        expect: 'check if the user is admin as we expect'
        //TODO complete me

        where:
        expectedIsAdmin << [false, true]
    }
}
