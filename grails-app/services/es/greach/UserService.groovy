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

import grails.transaction.Transactional
import org.springframework.context.MessageSource

class UserService {

    SendMailService sendMailService
    MessageSource messageSource

    /**
     * Save or update a user with the given data
     * @param username user´s username
     * @param password password
     * @param email user´s email
     * @return the user if it's saved or null otherwise
     */
    @Transactional
    User save(String username, String password, String email, Locale locale) {
        User user = new User()
        user.username = username
        user.email = email
        user.password = password

        if (user.validate() && user.save()){
            UserRole.create(user, Role.findByAuthority('ROLE_USER'))
            sendMailService.sendMail(user.email, 'createUser', messageSource.getMessage('email.createUser.subject', null , locale), [username: user.username])
            user
        }else{
            user.discard()
        }
    }

    /**
     * Performs a logical deletion of a user (the instance remains on the database but is marked as deleted). All the
     * related data is also removed
     * @param user the user to delete
     * @return A map containing the deleted instance if it was successfully removed, null otherwise, and the messages.
     */
    @Transactional
    User delete(User user) {
        if (user) {
            user.enabled = false
            user = user.save()
        }
        user
    }

    /**
     * Spend 5 secs about the purchased time of a user
     * @param User the user to substrac 5 secs
     * @return the user if it's saved or null otherwise
     */
    @Transactional
    User spendPurchasedTime(User user){
        if(!user?.isAdmin() && user?.purchasedTime > 0) {
            if (user.purchasedTime < 5 ) {
                user.purchasedTime = 0
            } else {
                user.purchasedTime -= 5
            }
            user.save()
        }
        user
    }

}
