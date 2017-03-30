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

import es.greach.Role
import es.greach.User
import es.greach.UserRole
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@Mock([UserRole, Role])
@TestFor(User)
class UserUnitSpec extends Specification{

    @Unroll
    void "Validates a user's instance that must be valid: #valid, with params: #params"(){
        given: 'a user built with some params'
        User user = new User(params)

        expect: 'the serie is valid or not'
        user.validate() == valid

        and: 'and if it is valid, save it and check the params '
        if(valid){
             assert user.save()
             params.each{ String key, value->
                 assert user."$key" == value
             }
        }

        where:
        valid || params
        false || [:]
        false || [username: 'username']
        false || [username: 'username', password: 'pass']
        true  || [username: 'username', password: 'pass', email: 'email@sample.']
        true  || [username: 'username', password: 'pass', email: 'email@sample.d']
        true  || [username: 'username', password: 'pass', email: 'email@sample.com']
        true  || [username: 'username', password: 'pass', email: 'email@sample.com']
        true  || [username: 'username', password: 'pass', email: 'email@sample.com', purchasedTime: 50]
        true  || [username: 'username', password: 'pass', email: 'email@sample.com', purchasedTime: 50, enabled: false]
        true  || [username: 'username', password: 'pass', email: 'email@sample.com', purchasedTime: 50, enabled: false, accountExpired: true]
        true  || [username: 'username', password: 'pass', email: 'email@sample.com', purchasedTime: 50, enabled: false, accountExpired: true]
        true  || [username: 'username', password: 'pass', email: 'email@sample.com', purchasedTime: 50, enabled: false, accountExpired: true, accountLocked: true]
        true  || [username: 'username', password: 'pass', email: 'email@sample.com', purchasedTime: 50, enabled: false, accountExpired: true, accountLocked: true, passwordExpired: true]
    }

    @Unroll
    void "check if a user is admin: #expectedIsAdmin"(){
        given: 'a new user'
        User user = new User(username: 'username', password: 'pass', email: 'email@sample.com').save()

        and: 'grant to the user the ROLE_ADMIN role'
        if(expectedIsAdmin){
            Role adminRole = new Role(authority: 'ROLE_ADMIN').save()
            UserRole.create user, adminRole
        }

        expect: 'check if the user is admin as we expect'
        user.isAdmin() == expectedIsAdmin

        where:
        expectedIsAdmin << [false, true]
    }
}
