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

import es.greach.Actor
import es.greach.Serie
import es.greach.SerieService
import grails.core.GrailsApplication
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Integration
@Rollback
class SerieServiceIntegrationSpec extends Specification {

    GrailsApplication grailsApplication
    @Shared SerieService serieService

    @Unroll
    void "save/update a serie"(){
        given: 'a multipartFile'
        MultipartFile cover = new MockMultipartFile('image', 'myImage.jpg', 'image/jpeg', 123 as byte[])

        and: 'create three actors'
        List<Actor> actors = []
        3.times {
            actors << Actor.build()
        }

        and: 'create a serie to edit'
        Serie serieToUpdate
        if (hasSerie) serieToUpdate = Serie.build()

        when: 'save or update the serie'
        Serie serieSaved = serieService.save(name, channel, releaseDate, sendCover ? cover : null, sendActors ? actors : null, serieToUpdate)

        then: 'check if the serie is created and if its attributes fits with the sent data'
        (serieSaved != null) == serieCreated
        if(serieCreated){
            assert serieSaved.name == name
            assert serieSaved.channel == channel
            assert serieSaved.releaseDate == releaseDate
            if(sendCover) assert serieSaved.pathToCover
            if(sendActors) assert serieSaved.actors
        }

        cleanup: 'delete the actors and the serie created'
        serieToUpdate?.delete()
        actors*.delete()
        if(serieCreated) serieSaved.delete()

        where:
        name   | channel   | releaseDate | sendCover | sendActors | hasSerie || serieCreated
        null   | null      | null        | false     | false      | false    || false
        'name' | null      | null        | false     | false      | false    || false
        'name' | 'channel' | null        | false     | false      | false    || false
        'name' | 'channel' | new Date()  | false     | false      | false    || true
        'name' | 'channel' | new Date()  | true      | false      | false    || true
        'name' | 'channel' | new Date()  | true      | true       | false    || true
        'name' | 'channel' | new Date()  | true      | true       | true     || true
    }

    @Unroll
    void "save the serie video with cover: #hasCover , serie: #hasSerie"() {
        given: 'a multipartFile'
        MultipartFile cover = hasCover ? new MockMultipartFile('image', 'myImage.jpg', 'image/jpeg', 123 as byte[]) : null

        and: 'a serie'
        Serie serie = hasSerie ? Serie.build(name: UUID.randomUUID()) : null

        and: 'the necessary config is set'
        grailsApplication.config.path.serie.cover = '/var/tmp/serie'

        when: 'save or update the serie'
        Serie serieSavedWithFile = serieService.saveCoverFile(cover, serie)

        then: 'check if the returned episode contains the path and the file to cover'
        if (hasCover && hasSerie){
            assert serieSavedWithFile
            assert serieSavedWithFile.name
            assert serieSavedWithFile.name == serie.name
            assert serieSavedWithFile.channel
            assert serieSavedWithFile.channel == serie.channel
            assert serieSavedWithFile.releaseDate
            assert serieSavedWithFile.releaseDate == serie.releaseDate
            assert serieSavedWithFile.pathToCover
            serieSavedWithFile.pathToCover == "${grailsApplication.config.path.serie.cover}/${serie.id}/${cover.originalFilename}".toString()
        } else {
            assert !serieSavedWithFile
        }

        cleanup: 'delete the serie and the episode'
        serie?.delete()

        where:
        hasCover | hasSerie
        false    | false
        false    | true
        true     | false
        true     | true
    }

    void "Delete the cover serie file"() {
        given: 'create a serie'
        Serie serie = serieService.save(UUID.randomUUID().toString(), 'channel', new Date(), new MockMultipartFile('image', 'myImage.jpg', 'image/jpeg', 123 as byte[]), null)

        when: "delete the serie's video file"
        serieService.deleteCoverFile(serie)

        then: "Serie's cover and serie´s pathToCover have been deleted successfully. Cover will save default image instead of that"
        serie
        !serie.pathToCover
        serie.cover == new File('grails-app/assets/images/default-thumbnail.jpg')

        cleanup: 'delete created objects'
        serie?.delete()
    }

    void "delete a serie"() {
        given: 'a serie'
        Serie serie = Serie.build(name: UUID.randomUUID())

        when: 'delete the serie'
        serie = serieService.delete(serie)

        then: 'serie has been deleted successfully'
        !serie
    }
}