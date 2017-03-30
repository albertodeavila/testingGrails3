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

import es.greach.Episode
import es.greach.EpisodeController
import es.greach.EpisodeService
import es.greach.Serie
import grails.buildtestdata.mixin.Build
import grails.core.DefaultGrailsApplication
import grails.core.GrailsApplication
import grails.test.mixin.TestFor
import org.springframework.mock.web.MockMultipartFile
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.mop.ConfineMetaClassChanges

@Build([Serie, Episode])
@TestFor(EpisodeController)
class EpisodeControllerUnitSpec extends Specification {

    @Shared Serie serie
    @Shared Episode episodeToUpdate

    void setup(){
        serie = Serie.build()
        episodeToUpdate = Episode.build()
        defineBeans {
            episodeService(EpisodeService)
        }
    }

    @Unroll
    void "show the create Episode form"() {
        given: 'create a serie'
        Serie serie = Serie.build()

        and: 'send the serieId to the controller'
        controller.params.serieId = sendSerieId ? serie.id : null

        when: 'call to the create action'
        controller.create()

        then: 'check if the view is rendered or if its redirected to the "/"'
        if(sendSerieId) {
            assert view == '/episode/save'
            assert model.serie == serie
        }else{
            assert response.redirectedUrl == '/serie'
        }

        where:
        sendSerieId << [true, false]
    }

    @Unroll
    void "#action an episode sending the episodeId #sendEpisodeId"() {
        given: 'create a serie with episodes'
        Episode episode = Episode.build()

        and: 'send the serieId to the controller'
        controller.params.episodeId = sendEpisodeId ? episode.id : null

        when: 'call to the create action'
        Map result = controller."$action"()

        then: 'check if the view is rendered or if its redirected to the "/"'
        if(sendEpisodeId){
            if(action == 'update'){
                assert view == '/episode/save'
                assert model.episode == episode
            }else{
                assert result.episode == episode
            }
        }else {
            assert response.redirectedUrl == '/serie'
        }

        where:
        action   | sendEpisodeId
        'show'   | false
        'show'   | true
        'update' | false
        'update' | true
    }

    @Unroll
    void "delete an episode"(){
        given: 'create an episode'
        Episode episode = Episode.build()

        and: 'mock the service'
        controller.episodeService = Mock(EpisodeService)
        controller.episodeService.delete(_) >> (errorDeleting ? episode : null)

        and: 'send the id to the action'
        controller.params.episodeId = sendEpisodeId ? episode.id : ''

        when: 'call to the action to delete the episode'
        controller.delete()

        then: 'check the response'
        controller
        controller.response.redirectedUrl == '/serie'
        if(!sendEpisodeId) assert controller.flash.error == 'episode.delete.notFound'
        else if(errorDeleting) assert controller.flash.error == 'episode.delete.error'
        else assert controller.flash.message == 'episode.delete.ok'

        where:
        sendEpisodeId | errorDeleting
        true          | false
        false         | false
        true          | true
        false         | true
    }

    @ConfineMetaClassChanges([MockMultipartFile])
    @Unroll
    void "save a serie"() {
        given: 'send the data to the controller'
        controller.params.title = title
        controller.params.season = season
        controller.params.episodeNumber = episodeNumber
        controller.params.summary = summary
        if (sendVideo) controller.request.addFile(new MockMultipartFile('video', 'myImage.jpeg', 'image/jpeg', 123 as byte[]))
        if (sendSerieId) controller.params.serieId = serie.id
        if (sendEpisodeId) controller.params.episodeId = episodeToUpdate.id

        and: 'override the method to calculate the video duration in secs'
        controller.episodeService.metaClass.calculateVideoDurationInSecs = {
            1000
        }

        and: 'create the function available with the request but not with tue MockMultipartFile'
        MockMultipartFile.metaClass.getFilename = {
            'filename'
        }

        and: 'set the default config'
        GrailsApplication grailsApplication = new DefaultGrailsApplication()
        grailsApplication.config.path.episodes = 'episodes'
        grailsApplication.config.pathserie.cover = '/var/tmp/serie'
        grailsApplication.config.pathserie.episodes = '/episodes'
        controller.episodeService.grailsApplication = grailsApplication

        when: 'call to the save action'
        controller.save()

        then: 'check if the action is redirected to the "/" and contains in the flash the result expected'
        controller.flash == messageResult
        if (success) {
            assert controller.response.redirectedUrl == "/episode/show?episodeId=${episodeToUpdate.id}"
        } else if (completeParams){
            assert controller.response.redirectedUrl == "/serie/show?serieId=${serie.id}"
        }else{
            assert controller.response.redirectedUrl == '/serie'
        }

        where:
        title | season  | episodeNumber  | summary | sendVideo | sendSerieId | sendEpisodeId || completeParams | success     | messageResult
        null  | null    | null           | null    | false     | false       | false         || false          | false       | [error: 'episode.save.error']
        'a'   | null    | null           | null    | false     | false       | false         || false          | false       | [error: 'episode.save.error']
        'a'   | 'b'     | null           | null    | false     | false       | false         || false          | false       | [error: 'episode.save.error']
        'a'   | 'b'     | 'c'            | null    | false     | false       | false         || false          | false       | [error: 'episode.save.error']
        'a'   | 'b'     | 1              | null    | false     | false       | false         || false          | false       | [error: 'episode.save.error']
        'a'   | 'b'     | 1              | null    | false     | false       | false         || false          | false       | [error: 'episode.save.error']
        1     | 'b'     | 1              | null    | false     | false       | false         || false          | false       | [error: 'episode.save.error']
        'a'   | 2       | 1              | null    | false     | false       | false         || false          | false       | [error: 'episode.save.error']
        'a'   | 2       | 1              | 'asdf'  | false     | false       | false         || false          | false       | [error: 'episode.save.error']
        'a'   | 2       | 1              | 'asdf'  | false     | false       | false         || false          | false       | [error: 'episode.save.error']
        'a'   | 2       | 1              | 'asdf'  | true      | false       | false         || false          | false       | [error: 'episode.save.error']
        'a'   | 2       | 1              | 'asdf'  | false     | true        | false         || true           | false       | [error: 'episode.save.error']
        'a'   | 2       | 1              | 'asdf'  | true      | true        | true          || true           | true        | [message: 'episode.save.ok']
    }
}
