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

import es.greach.Actor
import es.greach.Serie
import es.greach.SerieService
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.FileAlreadyExistsException

@Build([Serie, Actor])
@TestFor(SerieService)
class SerieServiceUnitSpec extends Specification {

    @Shared Serie serieToUpdate

    def setup(){
        serieToUpdate = Serie.build()
    }

    @Unroll
    void "Delete a serie"() {
        given: 'build a serie or not'
        Serie serie = buildSerie ? Serie.build() : null

        when: 'delete the serie'
        Serie deletedSerie = service.delete(serie)

        then: 'the serie is deleted'
        !deletedSerie

        where:
        buildSerie << [true, false]
    }

    @Unroll
    void "Delete the serie cover"(){
        given: 'a file'
        File cover = new File('sampleCover')
        cover << 'content'

        when: 'build a serie with or without a path to file'
        Serie serie = Serie.build(pathToCover: serieWithCover ? cover.absolutePath : null)

        then: 'the serie contains a cover'
        serie.cover

        when: 'deletes the serie cover'
        service.deleteCoverFile(serie)

        then: 'the serie is deleted'
        !serie.pathToCover
        serie.cover == new File('grails-app/assets/images/default-thumbnail.jpg')

        where:
        serieWithCover << [true, false]
    }

    @Unroll
    void "save the serie cover"(){
        given: 'A multipartFile'
        MultipartFile cover = new MockMultipartFile('image', 'myImage.jpg', 'image/jpeg', 123 as byte[])

        and: 'a new serie'
        Serie serie = Serie.build()

        when: 'save the cover file to the file system'
        Serie updatedSerie = service.saveCoverFile(sendCover ? cover : null, sendSerie ? serie : null)

        then: 'check if the returned serie contains the path to cover and the file to cover'
        (updatedSerie != null) == serieUpdated
        if(serieUpdated){
            assert updatedSerie.pathToCover
            assert updatedSerie.cover
        }

        cleanup: 'delete the cover file'
        if(serieUpdated) updatedSerie.cover.delete()

        where:
        sendCover | sendSerie || serieUpdated
        false     | false     || false
        false     | true      || false
        true      | false     || false
        true      | true      || true
    }

    @Unroll
    void "try to #method and throws an exception #exceptionThrown"(){
        given: 'the method to invoke overrided'
        service.metaClass.saveCoverFile = {MultipartFile cover, Serie serie ->
            throw exceptionThrown.newInstance('')
        }

        service.metaClass.deleteCoverFile = {Serie serie ->
            throw exceptionThrown.newInstance('')
        }

        when: 'save or delete the cover file to/from the file system'
        service."$method"(methodArgs)

        then: 'the exception is thrown'
        thrown(exceptionThrown)

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

    @Unroll
    void "save/update a serie"(){
        given: 'A multipartFile to simulate the cover image'
        MultipartFile cover = new MockMultipartFile('image', 'myImage.jpg', 'image/jpeg', 123 as byte[])

        and: 'create three actors'
        List<Actor> actors = []
        3.times {
            actors << Actor.build()
        }

        when: 'save or update the serie'
        Serie serieSaved = service.save(name, channel, releaseDate, sendCover ? cover : null, sendActors ? actors : null, serie)

        then: 'check if the serie is created and if its attributes fits with the data sent'
        (serieSaved != null) == serieCreated
        if(serieCreated){
            assert serieSaved.name == name
            assert serieSaved.channel == channel
            assert serieSaved.releaseDate == releaseDate
            if(sendCover) assert serieSaved.pathToCover
            if(sendActors) assert serieSaved.actors
        }

        cleanup: 'delete the actors and the created serie'
        actors*.delete()
        if(serieCreated) serieSaved.delete()

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
