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

import es.greach.Role
import es.greach.User
import es.greach.UserRole
import es.greach.UserService
import grails.plugin.greenmail.GreenMail
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.mail.internet.MimeMessage

@Integration
@Rollback
class UserServiceIntegrationSpec extends Specification {

    @Autowired UserService userService
    @Autowired MessageSource messageSource
    @Shared GreenMail greenMail

    @Unroll
    void "Saves a user with username: #username password: #password email: #email and saved #saved"() {
        when: 'call to the service to save the user'
        User user = userService.save(username, password, email, Locale.ENGLISH)

        then: 'check if the user is saved correctly and if the user receives an email'
        if (saved) {
            assert user.username == username
            assert user.email == email
            assert user.password != password

            MimeMessage message = greenMail.latestMessage
            assert 1 == greenMail.getReceivedMessages().length
            assert message.to == email
            assert message.subject == messageSource.getMessage('email.createUser.subject', null , Locale.ENGLISH)
            assert message.content.contains(username)
        } else {
            assert !user
        }

        where:
        username   | password   | email       || saved
        null       | null       | null        || false
        'username' | null       | null        || false
        'username' | 'password' | null        || false
        'username' | 'password' | null        || false
        'username' | 'password' | 'e@mail.me' || true
    }

    void "delete a user"(){
        given: 'a user'
        User user = User.build(username: UUID.randomUUID())

        when: 'delete the user'
        User deletedUser = userService.delete(user)

        then: 'the service returns a user and its attribute enable is false'
        deletedUser
        !deletedUser.enabled
    }

    void "try to delete a user"(){
        expect: 'the service return null when this value is passed as user'
        !userService.delete(null)
    }

    void "spend purchased time "(){
        given: 'a purchased time'
        Long purchasedTime = 50

        and: 'a user'
        User user = User.build(username: UUID.randomUUID(), purchasedTime: purchasedTime)

        and: 'grant admin role to the user'
        Role adminRole = Role.findOrSaveByAuthority('ROLE_ADMIN')
        if(isAdminUser) UserRole.create(user, adminRole)

        when: 'spend 5 seconds on the user purchased time'
        User updatedUser = userService.spendPurchasedTime(sendUser ? user : null)

        then: 'check the returned user'
        if(sendUser){
            assert updatedUser
            if(isAdminUser) assert user.purchasedTime == updatedUser.purchasedTime
            else assert updatedUser.purchasedTime == purchasedTime - 5
        }else{
            assert !updatedUser
        }

        where:
        sendUser | isAdminUser
        false    | false
        true     | false
        false    | true
        true     | true
    }

    void cleanup() {
        greenMail.deleteAllMessages()
    }
}
