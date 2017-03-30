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
package es.greach.controller

import es.greach.User
import es.greach.UserController
import es.greach.UserService
import grails.buildtestdata.mixin.Build
import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.http.HttpStatus.*

@Build([User])
@TestFor(UserController)
class UserControllerUnitSpec extends Specification {

    def setupSpec(){
        defineBeans {
            springSecurityService(SpringSecurityService)
        }
    }

    void "show the create view"() {
        when: 'go to the create action'
        Map result = controller.create()

        then: 'check that the action only returns an empty map'
        controller
        response
        !response.redirectedUrl
        !result
        !view
    }

    @Unroll
    void "spend the user's purchased time"(){
        given: 'override the springSecurityService to obtain a user'
        User user = User.build()
        controller.springSecurityService.metaClass.getCurrentUser = {
            userInSession ? user : null
        }

        and: 'mock the UserService'
        controller.userService = Mock(UserService)
        controller.userService.spendPurchasedTime(_) >> {
            errorWhileSpendTime ? null : user
        }

        when: 'call to the action'
        controller.spendPurchasedTime()

        then: 'the action returns a status and it\'s as expected'
        response.status == expectedStatus.value()
        response.text == ' '

        where:
        expectedStatus          || userInSession | errorWhileSpendTime
        OK                      || true          | false
        INTERNAL_SERVER_ERROR   || true          | true
        BAD_REQUEST             || false         | false
    }

    @Unroll
    void "save a user with the given data"(){
        given: 'mock the service'
        controller.userService = Mock(UserService)
        controller.userService.save(_, _, _, _) >> {
            (errorSaving ? null : User.build())
        }

        and: 'send the parameters to the action'
        controller.params.username = username
        controller.params.password = password
        controller.params.passwordConfirm = passwordConfirm
        controller.params.email = email
        controller.params.result = result

        when: 'call the action save'
        controller.save()

        then: 'the response is redirected to create user action and flash contains the expected result'
        response.redirectedUrl == '/user/create'
        controller.flash == result

        where:
        username | password | passwordConfirm | email | errorSaving | result
        null     | null     | null            | null  | false       | [error: 'user.save.fail.required']
        'a'      | null     | null            | null  | false       | [error: 'user.save.fail.required']
        'a'      | 'b'      | null            | null  | false       | [error: 'user.save.fail.required']
        'a'      | 'b'      | 'b'             | null  | false       | [error: 'user.save.fail.required']
        'a'      | 'b'      | 'b'             | 'a'   | false       | [message: 'user.save.ok'         ]
        'a'      | 'b'      | 'c'             | 'a'   | false       | [error: 'user.password.notEqual' ]
        'a'      | 'b'      | 'b'             | 'a'   | true        | [error: 'user.save.fail']
    }
}
