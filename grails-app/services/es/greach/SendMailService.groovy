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

import grails.gsp.PageRenderer
import grails.plugins.mail.MailService
import grails.transaction.Transactional

@Transactional
class SendMailService {

    PageRenderer groovyPageRenderer
    MailService mailService

    /**
     * Method to send an email based in the parameters
     * @param email
     * @param template
     * @param subjectMail
     * @param model The model to render the body's mail
     */
    void sendMail(String email, String template, String subjectMail, Map model) {
        if (template && email) {
            try {
                String bodyMail = groovyPageRenderer.render(template: "/emailTemplates/${template}", model: model)
                mailService.sendMail {
                    to email
                    subject subjectMail
                    html bodyMail
                }
            } catch (Exception e) {
                log.error "There was an exception sending the email: ${e.properties}"
            }
        }
    }
}
