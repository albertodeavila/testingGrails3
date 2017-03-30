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
package es.greach.integration.controller

import es.greach.ActorController
import es.greach.ActorService
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.util.GrailsWebMockUtil
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.context.WebApplicationContext
import spock.lang.Shared
import spock.lang.Specification

@Integration
@Rollback
class ActorControllerIntegrationSpec extends Specification {

    @Autowired ActorController actorController
    @Autowired WebApplicationContext ctx

    void setup() {
        GrailsWebMockUtil.bindMockWebRequest(ctx)
    }

    void "search actors by ajax"(){
        given: 'a name'
        String name = 'Antonio'

        and: 'give params to the controller'
        actorController.params.query = 'Antonio'

        when: 'send to the controller and call the action'
        actorController.searchActorsAjax()
        List result = new JsonSlurper().parseText(actorController.response.content.toString())

        then:  'check that the response is the data as JSON'
        result
        result.any{ it.name.contains(name) }
    }
}
