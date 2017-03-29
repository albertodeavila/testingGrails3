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

import es.greach.SerieController
import grails.test.mixin.TestFor
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(SerieController)
class SerieControllerUnitSpec extends Specification {


    /*************************************
            CONTROLLER UNIT EXERCISE 2
     **************************************/
    @Ignore("Until start work on controller unit exercise 2")
    @Unroll
    void "delete a serie"(){
        given: 'create a serie'
        //TODO complete me

        and: 'mock the service'
        //TODO complete me

        and: 'send the id to the action'
        //TODO complete me

        when: 'call to the action to delete the serie'
        //TODO complete me

        then: 'check that the response redirect to the /serie/index and if there what flash message or error return'
        //TODO complete me

        where:
        sendSerieId | errorDeleting
        true        | false
        false       | false
        true        | true
        false       | true
    }

    /*************************************
            CONTROLLER UNIT EXERCISE 3
     **************************************/
    @Ignore("Until start work on controller unit exercise 3")
    @Unroll
    void "#action a serie sending the id #sendSerieId about a serie that exist #existsSerie"(){
        given: 'create a serie'
        //TODO complete me

        and: 'send the id to the action'
        //TODO complete me

        when: 'call to the action to show/update the serie'
        //TODO complete me

        then: 'check the response, the flash message, the view rendered and the model'
        //TODO complete me

        where:
        action   | viewRendered | sendSerieId | existSerie
        'show'   | 'show'       | true        | false
        'show'   | 'show'       | false       | false
        'show'   | 'show'       | true        | true
        'show'   | 'show'       | false       | true
        'update' | 'save'       | true        | false
        'update' | 'save'       | false       | false
        'update' | 'save'       | true        | true
        'update' | 'save'       | false       | true
    }

    /*************************************
             CONTROLLER UNIT EXERCISE 4
     **************************************/
    @Ignore("Until start work on controller unit exercise 4")
    void "show the create form in the serie controller"(){
        when: 'call to the create action'
        //TODO complete me

        then: 'the view is rendered'
        //TODO complete me
    }

    /*************************************
            CONTROLLER UNIT EXERCISE 5
     **************************************/
    @Ignore("Until start work on controller unit exercise 5")
    void "show index in the serie controller"(){
        given: 'create 12 series'
        //TODO complete me

        when: 'call to the index action'
        //TODO complete me

        then: 'check the returned map'
        //TODO complete me
    }

    /*************************************
            CONTROLLER UNIT EXERCISE 6
     **************************************/
    @Ignore("Until start work on controller unit exercise 6")
    @Unroll
    void "save a serie"(){
        given: 'override the message method to return '
        //TODO complete me

        and: 'mock the ActorService to return actors'
        //TODO complete me

        and: 'send the data to the controller'
        //TODO complete me

        when: 'call to the save action'
        //TODO complete me


        then: 'check the action redirect to the /serie/index and contains in the flash the result expected'
        //TODO complete me

        where:
        name | channel | releaseDate                    | actors            | sendCover | sendSerieId || result
        null | null    | null                           | null              | false     | false       || [error: 'serie.save.error']
        //TODO complete me
    }
}
