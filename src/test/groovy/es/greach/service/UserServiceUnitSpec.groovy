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

import es.greach.*
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.springframework.context.MessageSource
import spock.lang.Specification
import spock.lang.Unroll

@Mock([Role, UserRole])
@Build([User])
@TestFor(UserService)
class UserServiceUnitSpec extends Specification {

    void setupSpec() {
        defineBeans {
            sendMailService(SendMailService)
        }
    }

    void setup() {
        new Role(authority: 'ROLE_ADMIN').save()
    }

    @Unroll
    void "Saves a user"() {
        given: 'override messageSource to return some message'
        service.messageSource = Mock(MessageSource)
        service.messageSource.getMessage(_, _, _) >> "some message"

        when: 'call the service to save the user'
        User user = service.save(username, password, email, Locale.ENGLISH)

        then: 'check if the user is saved and if it\'s, check the username and the email'
        if(resultError) {
            assert !user
        }else{
            assert user.username == username
            assert user.email == email
        }

        where:
        username   | password   | passwordConfirm | email       || resultError
        null       | null       | null            | null        || true
        'username' | null       | null            | null        || true
        'username' | 'password' | null            | null        || true
        'username' | 'password' | 'password2'     | null        || true
        'username' | 'password' | 'password'      | 'e@mail.me' || false
    }

    void "Delete a user"() {
        given: 'build a user or not'
        User user = buildUser ? User.build() : null

        when: 'delete the user'
        User deletedUser = service.delete(user)

        then: 'if the user is built, enabled is false. If not, user does not exist '
        if (buildUser) {
            assert !deletedUser.enabled
        } else {
            assert !deletedUser
        }

        where:
        buildUser << [true, false]
    }

    void "Spend user's purchased time"() {
        given: 'build a user with specified purchased time'
        User user = User.build(purchasedTime: purchasedTime)

        and: 'creates an admin role whether an admin role needs to be created or not'
        if (isAdmin) UserRole.create(user, Role.findByAuthority('ROLE_ADMIN'))

        when: 'spends purchased time for the user'
        User resultedUser = service.spendPurchasedTime(user)

        then: 'resulted purchased time is as expected'
        resultedUser.purchasedTime == resultedTime

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
