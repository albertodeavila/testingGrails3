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

import es.greach.ActorController
import es.greach.ActorService
import grails.converters.JSON
import grails.test.mixin.TestFor
import spock.lang.Specification

import static org.springframework.http.HttpStatus.OK


@TestFor(ActorController)
class ActorControllerUnitSpec extends Specification {

    void "search actors by ajax"(){
        given: 'the data that the service will return'
        Map data = [id: 1, name: 'Antonio Banderas']

        and: 'mock the service'
        controller.actorService = Mock(ActorService)
        controller.actorService.searchActorsByName(_) >> [status: OK.value(), data: data]

        and: 'a name'
        String name = 'Antonio'

        and: 'send to the controller'
        controller.params.query = name

        when: 'call to the action'
        controller.searchActorsAjax()

        then:  'check if the response is the data as JSON'
        controller
        JSON.parse(controller.response.contentAsString) == data
    }
}
