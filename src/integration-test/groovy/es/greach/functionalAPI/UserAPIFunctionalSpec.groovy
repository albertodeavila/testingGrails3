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
package es.greach.functionalAPI

import es.greach.User
import es.greach.functional.utils.FunctionalTest
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import spock.lang.Shared

import static org.springframework.http.HttpStatus.OK


class UserAPIFunctionalSpec extends FunctionalTest{

    @Shared RestBuilder restBuilder = new RestBuilder()

    void "spend time viewing a video"(){
        given: 'a user is created'
        User user
        User.withNewSession { session ->
            user = User.findByUsername('userA')
        }
        Long purchasedTime = user.purchasedTime


        when: 'login in the app'
        RestResponse loginResponse = restBuilder.post("${baseUrl}/api/login") {
            header("Accept", "application/json")
            header("Content-Type", "application/json")
            json {
                username = user.username
                password = 'mypassword'
            }
        }

        and: 'save the access token'
        String accessToken = loginResponse.json?.access_token

        then: 'the user is logged in and the API returns a 200 code'
        loginResponse.status == 200
        accessToken

        when: 'make a post on spendPurchasedTime'
        RestResponse responsePost = restBuilder.get("${baseUrl}api/user/spendPurchasedTime") {
            header("Authorization", "Bearer ${accessToken}")
        }

        then: 'the response is OK'
        responsePost
        responsePost.status == OK.value()

        when: 'refresh the user object'
        User.withNewSession { session ->
            user.refresh()
        }

        then: 'the purchased time has been decreased by 5'
        user.purchasedTime == purchasedTime - 5
    }
}
