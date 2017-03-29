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
package es.greach

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured

import static org.springframework.http.HttpStatus.*

class UserController {
    SpringSecurityService springSecurityService
    UserService userService

    /**
     *  Show the create user view
     */
    @Secured(['ROLE_ANONYMOUS'])
    def create() {}


    /**
     * Save a new user
     */
    @Secured(['ROLE_ANONYMOUS'])
    def save() {
        if (!(params.username && params.password && params.email)) {
            flash.error = message code: 'user.save.fail.required'
        }else if(params.password != params.passwordConfirm) {
            flash.error = message code: 'user.password.notEqual'
        }else{
            User user = userService.save(params.username, params.password, params.email, request.locale)
            if (user) {
                flash.message = message code: 'user.save.ok', args: [user.username]
            } else {
                flash.error = message code: 'user.save.fail'
            }
        }
        redirect controller: 'user', action: 'create'
    }

    /**
     * API endpoint to spend user's purchased time while he sees a video.
     * A request to this endpoint is made every 5 secs.
     */
    @Secured(['ROLE_USER', 'ROLE_ADMIN'])
    def spendPurchasedTime(){
        User user = springSecurityService.currentUser
        if(user) {
            if(userService.spendPurchasedTime(user)){
                render status: OK.value(), text: ' '
            }else{
                render status: INTERNAL_SERVER_ERROR.value(), text: ' '
            }
        }else{
            render status: BAD_REQUEST.value(), text: ' '
        }
    }
}
