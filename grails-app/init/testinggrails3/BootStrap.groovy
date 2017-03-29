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
package testinggrails3

import es.greach.Role
import es.greach.Serie
import es.greach.User
import es.greach.UserRole
import grails.util.Environment

class BootStrap {

    def init = { servletContext ->
        Environment.executeForCurrentEnvironment {
            development {
                createRolesAndUsers()
                createSampleData()
            }

            test {
                createRolesAndUsers()
            }

            production{

            }
        }
    }

    def destroy = {

    }

    void createRolesAndUsers(){
        if(!Role.count()) {
            Role adminRole = new Role(authority: 'ROLE_ADMIN').save()
            Role userRole = new Role(authority: 'ROLE_USER').save()
            User admin = new User(username: 'admin', password: '1234', email: 'a@gmail.com').save()
            UserRole.create (admin, adminRole)

            User userA = new User(username: 'userA', password: 'mypassword', purchasedTime: 50, email: 'userA@gmail.com').save()
            UserRole.create (userA, userRole)
            User userB = new User(username: 'userB', password: 'mypassword', purchasedTime: 50, email: 'userB@gmail.com').save()
            UserRole.create (userB, userRole)
        }
    }

    void createSampleData(){
        3.times{ Serie.build(name: UUID.randomUUID()) }
    }
}
