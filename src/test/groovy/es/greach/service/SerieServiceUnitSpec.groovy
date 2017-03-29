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
package es.greach.service

import es.greach.Serie
import es.greach.SerieService
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.FileAlreadyExistsException

@Build([Serie])
@TestFor(SerieService)
class SerieServiceUnitSpec extends Specification {

    @Shared Serie serieToUpdate

    def setup(){
        serieToUpdate = Serie.build()
    }

    /*************************************
            SERVICE UNIT EXERCISE 1
     **************************************/
    @Ignore("Until start work on service unit exercise 1")
    @Unroll
    void "Delete a serie"() {
        given: 'build a serie or not'
        //TODO complete me

        when: 'delete the serie'
        //TODO complete me

        then: 'the serie is deleted'
        //TODO complete me

        where:
        buildSerie << [true, false]
    }

    /*************************************
            SERVICE UNIT EXERCISE 2
     **************************************/
    @Ignore("Until start work on service unit exercise 2")
    @Unroll
    void "Delete the serie cover"(){
        given: 'a file'
        //TODO complete me

        when: 'build a serie with or without a path to file'
        //TODO complete me

        then: 'the serie contains a cover'
        //TODO complete me

        when: 'deletes the serie cover'
        //TODO complete me

        then: 'the serie is deleted'
        //TODO complete me

        where:
        serieWithCover << [true, false]
    }

    /*************************************
            SERVICE UNIT EXERCISE 2
     **************************************/
    @Ignore("Until start work on service unit exercise 2")
    @Unroll
    void "save the serie cover"(){
        given: 'A multipartFile'
        //TODO complete me

        and: 'a new serie'
        //TODO complete me

        when: 'save the cover file to the file system'
        //TODO complete me

        then: 'check if the returned serie contains the path to cover and the file to cover'
        //TODO complete me

        cleanup: 'delete the cover file'
        //TODO complete me

        where:
        sendCover | sendSerie || serieUpdated
        false     | false     || false
        false     | true      || false
        true      | false     || false
        true      | true      || true
    }

    /*************************************
            SERVICE UNIT EXERCISE 3
     **************************************/
    @Ignore("Until start work on service unit exercise 3")
    @Unroll
    void "try to #method and throws an exception #exceptionThrown"(){
        given: 'the method to invoke overrided'
        //TODO complete me

        when: 'save or delete the cover file to/from the file system'
        //TODO complete me

        service.metaClass.deleteCoverFile = {Serie serie ->
            throw exceptionThrown.newInstance('')
        }

        then: 'the exception is thrown'
        //TODO complete me

        where:
        method            | methodArgs   | exceptionThrown
        'saveCoverFile'   | [null, null] | SecurityException
        'saveCoverFile'   | [null, null] | FileAlreadyExistsException
        'saveCoverFile'   | [null, null] | IOException
        'saveCoverFile'   | [null, null] | Exception

        'deleteCoverFile' | null         | SecurityException
        'deleteCoverFile' | null         | FileAlreadyExistsException
        'deleteCoverFile' | null         | IOException
        'deleteCoverFile' | null         | Exception
    }

    /*************************************
            SERVICE UNIT EXERCISE 4
     **************************************/
    @Ignore("Until start work on service unit exercise 4")
    @Unroll
    void "save/update a serie"(){
        given: 'A multipartFile to simulate the cover image'
        //TODO complete me

        and: 'create three actors'
        //TODO complete me

        when: 'save or update the serie'
        //TODO complete me

        then: 'check if the serie is created and if its attributes fits with the data sent'
        //TODO complete me

        cleanup: 'delete the actors and the created serie'
        //TODO complete me

        where:
        name   | channel   | releaseDate | sendCover | sendActors | serie          || serieCreated
        null   | null      | null        | false     | false      | null           || false
        'name' | null      | null        | false     | false      | null           || false
        'name' | 'channel' | null        | false     | false      | null           || false
        'name' | 'channel' | new Date()  | false     | false      | null           || true
        'name' | 'channel' | new Date()  | true      | false      | null           || true
        'name' | 'channel' | new Date()  | true      | true       | null           || true
        'name' | 'channel' | new Date()  | true      | true       | serieToUpdate  || true
    }
}
