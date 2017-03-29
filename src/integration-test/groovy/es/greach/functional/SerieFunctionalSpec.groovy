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
package es.greach.functional

import es.greach.functional.utils.FunctionalTest
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Ignore
import spock.lang.Stepwise
import spock.lang.Unroll

@Integration
@Rollback
@Stepwise
class SerieFunctionalSpec extends FunctionalTest {

    /*************************************
            FUNCTIONAL EXERCISE 1
     **************************************/
    @Ignore("Until start work on functional exercise 1")
    void 'Creates a serie'() {
        given: 'login as an admin'
        //TODO complete me

        when: 'go to create serie page'
        //TODO complete me

        and: 'fill serie form'
        //TODO complete me

        and: 'submit the serie form'
        //TODO complete me

        then: 'the index serie page is shown'
        //TODO complete me

        and: 'a success message appear'
        //TODO complete me

        and: 'there is a serie with the name'
        //TODO complete me

        when: 'click in the serie link'
        //TODO complete me

        then: 'the show page is shown'
        //TODO complete me

        and: 'check the values saved are shown'
        //TODO complete me
    }

    /*************************************
            FUNCTIONAL EXERCISE 2
     **************************************/
    @Ignore("Until start work on functional exercise 2")
    @Unroll
    void "try to save a serie without required params"(){
        when: 'go to create serie page'
        //TODO complete me

        and: 'fill serie form'
        //TODO complete me

        and: 'click on the serie submit button'
        //TODO complete me

        then: 'we are in the create serie page'
        //TODO complete me

        where:
        name | channel | releaseDate                     || fieldErrors
        ''   | ''      | ''                              || ['name', 'channel', 'releaseDate']
        'a'  | ''      | ''                              || ['channel', 'releaseDate']
        'a'  | 'b'     | ''                              || ['releaseDate']
        ''   | 'b'     | new Date().format('MM/dd/yyyy') || ['name']
        ''   | 'b'     | ''                              || ['name', 'releaseDate']
        'a'  | ''      | new Date().format('MM/dd/yyyy') || ['channel']
        ''   | ''      | new Date().format('MM/dd/yyyy') || ['name', 'channel']
    }

    /*************************************
        FUNCTIONAL EXERCISE 3
     **************************************/
    @Ignore("Until start work on functional exercise 3")
    void "try to show a serie that doesn't exists"(){
        when: 'go to show page that doesn\'t exists'
        //TODO complete me

        then: 'the index serie is shown'
        //TODO complete me

        and: 'an error message appears'
        //TODO complete me
    }

    /*************************************
     FUNCTIONAL EXERCISE 4
     **************************************/
    @Ignore("Until start work on functional exercise 4")
    void "try to show a serie without sending serieId"(){
        when: 'go to show serie page'
        //TODO complete me

        then: 'the index serie is shown'
        //TODO complete me

        and: 'an error message appear'
        //TODO complete me
    }

    /*************************************
            FUNCTIONAL EXERCISE 4
     **************************************/
    @Ignore("Until start work on functional exercise 4")
    void "show a serie"(){
        given: 'a new serie'
        //TODO complete me

        when: 'go to show page'
        //TODO complete me

        then: 'the show page is present'
        //TODO complete me

        and: 'check that the values saved are shown'
        //TODO complete me
    }

    /*************************************
            FUNCTIONAL EXERCISE 5
     **************************************/
    @Ignore("Until start work on functional exercise 5")
    void "delete a serie"(){
        given: 'a new serie'
        //TODO complete me

        when: 'go to show serie page'
        //TODO complete me

        then: 'we are at show serie page'
        //TODO complete me

        when: 'click on the delete button'
        //TODO complete me

        then: 'the index serie page is shown'
        //TODO complete me

        and: 'a success message appear'
        //TODO complete me
    }

    /*************************************
            FUNCTIONAL EXERCISE 6
     **************************************/
    @Ignore("Until start work on functional exercise 6")
    void "update a serie"(){
        given: 'a new serie'
        //TODO complete me

        when: 'go to show serie page'
        //TODO complete me

        then: 'we are at show serie page'
        //TODO complete me

        when: 'click on the update button'
        //TODO complete me

        then: 'the edit serie page is shown'
        //TODO complete me

        when: 'change the form values'
        //TODO complete me

        and: 'click on save button'
        //TODO complete me

        then: 'the index serie page is shown'
        //TODO complete me

        and: 'a success message appear'
        //TODO complete me

        and: 'there is a serie with the name'
        //TODO complete me

        when: 'click in the serie link'
        //TODO complete me

        then: 'the show page is shown'
        //TODO complete me

        and: 'check that the values saved are shown'
        //TODO complete me
    }
}
