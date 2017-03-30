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

import es.greach.SendMailService
import grails.plugin.greenmail.GreenMail
import grails.plugins.mail.MailService
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Integration
@Rollback
class SendMailServiceIntegrationSpec extends Specification{

    @Autowired SendMailService sendMailService
    @Autowired MailService mailService
    @Shared GreenMail greenMail

    void "send a basic mail"(){
        given: 'generate the "to" email, the subject and the body'
        String toMail = 'sample@mail.com'
        String subjectText = 'hello!'
        String bodyText = 'Hello from the testing in Grails 3 workshop!'

        when: 'send the mail'
        mailService.sendMail {
            to toMail
            subject subjectText
            body bodyText
        }

        then: 'check the content in the email sent'
        greenMail
        greenMail.latestMessage
        greenMail.latestMessage.to == toMail
        greenMail.latestMessage.subject == subjectText
        greenMail.latestMessage.content.contains(bodyText)
    }

    @Unroll
    void "send an application mail using: #email #subject #template #model"(){
        when: 'send a mail'
        sendMailService.sendMail(email, template, subject, model)

        then: 'if emailSent is true, check the received email content'
        greenMail
        if(emailSent){
            assert greenMail.latestMessage
            assert greenMail.latestMessage.to == email
            assert greenMail.latestMessage.subject == subject
            if(model) assert greenMail.latestMessage.content.contains(model.username)
        }else{
            assert !greenMail.latestMessage
        }

        where:
        email     | subject   | template     | model                    || emailSent
        null      | null      | null         | [:]                      || false
        'a@b.com' | null      | null         | [:]                      || false
        'a@b.com' | 'subject' | null         | [:]                      || false
        'a@b.com' | 'subject' | 'template'   | [:]                      || false
        'a@b.com' | 'subject' | 'createUser' | [:]                      || true
        'a@b.com' | 'subject' | 'createUser' | [username: 'myUsername'] || true
    }

    void cleanup() {
        greenMail.deleteAllMessages()
    }
}
