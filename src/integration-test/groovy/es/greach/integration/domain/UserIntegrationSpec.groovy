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
package es.greach.integration.domain

import es.greach.Role
import es.greach.User
import es.greach.UserRole
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Specification
import spock.lang.Unroll

@Integration
@Rollback
class UserIntegrationSpec extends Specification {

    @Unroll
    void "Validate the user creation"() {
        given: 'create a user with the given params'
        User user = new User(params)

        expect: 'the validation is as expected'
        user.validate() == valid

        and: 'if is valid, save it and check the params '
        if (valid) {
            assert user.save()
            params.each { String key, value ->
                if(key != 'password') assert user."$key" == value
            }

        }

        where:
        valid || params
        false || [:]
        false || [username: UUID.randomUUID().toString()]
        false || [username: UUID.randomUUID().toString(), password: 'pass']
        true  || [username: UUID.randomUUID().toString(), password: 'pass', email: 'email@sample.']
        true  || [username: UUID.randomUUID().toString(), password: 'pass', email: 'email@sample.d']
        true  || [username: UUID.randomUUID().toString(), password: 'pass', email: 'email@sample.com']
        true  || [username: UUID.randomUUID().toString(), password: 'pass', email: 'email@sample.com']
        true  || [username: UUID.randomUUID().toString(), password: 'pass', email: 'email@sample.com', purchasedTime: 50]
        true  || [username: UUID.randomUUID().toString(), password: 'pass', email: 'email@sample.com', purchasedTime: 50, enabled: false]
        true  || [username: UUID.randomUUID().toString(), password: 'pass', email: 'email@sample.com', purchasedTime: 50, enabled: false, accountExpired: true]
        true  || [username: UUID.randomUUID().toString(), password: 'pass', email: 'email@sample.com', purchasedTime: 50, enabled: false, accountExpired: true]
        true  || [username: UUID.randomUUID().toString(), password: 'pass', email: 'email@sample.com', purchasedTime: 50, enabled: false, accountExpired: true, accountLocked: true]
        true  || [username: UUID.randomUUID().toString(), password: 'pass', email: 'email@sample.com', purchasedTime: 50, enabled: false, accountExpired: true, accountLocked: true, passwordExpired: true]
    }

    @Unroll
    void "check if a user is admin: #expectedIsAdmin"() {
        given: 'create an user'
        User user = new User(username: UUID.randomUUID().toString(), password: 'pass', email: 'email@sample.com').save()

        and: 'grant to the user the ROLE_ADMIN role'
        if (expectedIsAdmin) {
            Role adminRole = Role.findOrCreateByAuthority('ROLE_ADMIN').save()
            UserRole.create user, adminRole
        }

        expect: 'check if the user is admin as expected'
        user.isAdmin() == expectedIsAdmin

        where:
        expectedIsAdmin << [false, true]
    }
}
