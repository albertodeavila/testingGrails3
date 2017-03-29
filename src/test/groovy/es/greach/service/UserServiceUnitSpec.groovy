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
package es.greach.service

import es.greach.UserService
import grails.test.mixin.TestFor
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(UserService)
class UserServiceUnitSpec extends Specification {

    /*************************************
        SERVICE UNIT EXERCISE EXTRA 1
     **************************************/
    @Ignore("Until start work on service unit exercise extra 1")
    @Unroll
    void "Saves a user"() {
        //TODO complete me

        where:
        username   | password   | passwordConfirm | email       || resultError
        null       | null       | null            | null        || true
        'username' | null       | null            | null        || true
        'username' | 'password' | null            | null        || true
        'username' | 'password' | 'password2'     | null        || true
        'username' | 'password' | 'password'      | 'e@mail.me' || false
    }

    /*************************************
     SERVICE UNIT EXERCISE EXTRA 2
     **************************************/
    @Ignore("Until start work on service unit exercise extra 2")
    void "Delete a user"() {
        //TODO complete me
    }

    /*************************************
     SERVICE UNIT EXERCISE EXTRA 3
     **************************************/
    @Ignore("Until start work on service unit exercise extra 3")
    @Unroll
    void "Spend user's purchased time"() {
        //TODO complete me

        where:
        isAdmin | purchasedTime || resultedTime
        false   | 0             || 0
        false   | 1             || 0
        false   | 5             || 0
        false   | 6             || 1
        false   | 10            || 5
        true    | 0             || 0
        true    | 1             || 1
        true    | 5             || 5
        true    | 6             || 6
        true    | 10            || 10
    }
}
