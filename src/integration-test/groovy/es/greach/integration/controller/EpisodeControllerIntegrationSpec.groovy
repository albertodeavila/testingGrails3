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

import es.greach.Episode
import es.greach.EpisodeController
import es.greach.EpisodeService
import es.greach.Serie
import grails.core.DefaultGrailsApplication
import grails.core.GrailsApplication
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.util.GrailsWebMockUtil
import org.grails.encoder.CodecLookup
import org.grails.plugins.testing.GrailsMockMultipartFile
import org.grails.plugins.web.taglib.ValidationTagLib
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.mock.web.MockMultipartFile
import org.springframework.mock.web.MockMultipartHttpServletRequest
import org.springframework.web.context.WebApplicationContext
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.mop.ConfineMetaClassChanges
import static org.springframework.http.HttpStatus.OK

@Integration
@Rollback
class EpisodeControllerIntegrationSpec extends Specification {

    @Autowired EpisodeController episodeController
    @Autowired WebApplicationContext ctx
    @Autowired EpisodeService episodeService
    @Autowired MessageSource messageSource

    @Shared Episode episodeToUpdate
    @Shared Serie serie
    @Shared String videoPath
    @Shared File copiedVideo

    void setupSpec(){
        File video = new File('src/integration-test/groovy/es/greach/functional/utils/media/video.mp4')
        copiedVideo = new File('src/integration-test/groovy/es/greach/functional/utils/media/video2.mp4')
        copiedVideo << video.text
        videoPath = copiedVideo.absolutePath
    }

    void setup() {
        GrailsWebMockUtil.bindMockWebRequest(ctx)
        serie = Serie.build(name: UUID.randomUUID())
        episodeToUpdate = Episode.build(serie: serie)
    }

    @Unroll
    void "create an episode that belongs to a serie #hasSerie"(){
        given: 'create a serie'
        Serie serie
        if (hasSerie) serie = Serie.build(name: UUID.randomUUID())

        and: 'assign id to the serieId param'
        episodeController.params.serieId = serie?.id

        when: 'call to the create action'
        episodeController.create()

        then: 'If hasSerie, save view is rendered with the serie in model. Otherwise, redirect to the controller serie'
        if (hasSerie) {
            assert episodeController.modelAndView
            assert episodeController.modelAndView.viewName == '/episode/save'
            assert episodeController.modelAndView.model
            assert episodeController.modelAndView.model.serie
            assert episodeController.modelAndView.model.serie.id == serie.id
        } else {
           assert episodeController.response.redirectedUrl == 'http://localhost/'
        }

        where:
        hasSerie << [true, false]
    }

    @Unroll
    void "update a given episode that belongs to a serie #hasSerie"(){
        given: 'create a serie'
        Serie serie = Serie.build(name: UUID.randomUUID())
        Episode episode
        if (hasEpisode) episode = Episode.build(serie: serie)

        and: 'assign id to the serieId param'
        episodeController.params.episodeId = episode?.id

        when: 'call to the update action'
        episodeController.update()

        then: 'If hasEpisode, save view is rendered with the serie in model. Otherwise, redirect to the controller serie'
        if (hasEpisode) {
            assert episodeController.modelAndView
            assert episodeController.modelAndView.viewName == '/episode/save'
            assert episodeController.modelAndView.model
            assert episodeController.modelAndView.model.episode
            assert episodeController.modelAndView.model.episode.id == episode.id
        } else {
            assert episodeController.response.redirectedUrl == 'http://localhost/'
        }

        where:
        hasEpisode << [true, false]
    }

    @Unroll
    void "Delete a given episode"(){
        given: 'create a serie and the episode to delete'
        Serie serie = Serie.build(name: UUID.randomUUID())
        Episode episode
        if (hasEpisode) episode = Episode.build(serie: serie)

        and: 'assign id to the serieId param'
        episodeController.params.episodeId = episode?.id

        and: 'forceFail in service to get the error message'
        def originalDeleteMethod = episodeService.&delete
        if (hasEpisode && forceFail){
            episodeService.metaClass.delete = { Episode e ->
                true
            }
        }

        when: 'call the delete action'
        episodeController.delete()

        then: 'If hasEpisode, redirect to controller save and get message. If not or force fail message, an error message will be shown'
        if (!hasEpisode){
            assert episodeController.flash.error == messageSource.getMessage(messageCode, null, Locale.ENGLISH )
        } else if (hasEpisode && !forceFail) {
            assert episodeController.response.redirectedUrl == 'http://localhost/'
            assert episodeController.flash.message == messageSource.getMessage(messageCode, [episode.title] as Object[], Locale.ENGLISH )
            assert !Episode.get(episode?.id)
        } else {
            assert episodeController.flash.error == messageSource.getMessage(messageCode, [episode.title] as Object[], Locale.ENGLISH )
        }

        cleanup: 'restore delete method'
        episodeService.metaClass.delete = originalDeleteMethod

        where:
        hasEpisode | forceFail || messageCode
        true       | false     || 'episode.delete.ok'
        false      | false     || 'episode.delete.notFound'
        true       | true      || 'episode.delete.error'
    }

    @Unroll
    void "show a given episode"(){
        given: 'create a serie and a episode'
        Serie serie = Serie.build(name: UUID.randomUUID())
        Episode episode = Episode.build(serie: serie)

        and: 'assign id to the serieId param'
        episodeController.params.episodeId = episodeId ? (episodeId == 'idCorrect' ? episode?.id : -1): null

        when: 'call to the show action'
        Map result = episodeController.show()

        then: 'If hasEpisode, save view is rendered with the serie in model. Otherwise, redirect to the controller serie'
        if (episodeId == 'idCorrect'){
            assert result.episode == episode
        } else {
            assert episodeController.response.redirectedUrl == 'http://localhost/'
            assert episodeController.flash.error == messageSource.getMessage(code, null, Locale.ENGLISH )
        }

        where:
        episodeId     | code
        'idCorrect'   | null
        'idInCorrect' | 'episode.show.notExists'
        null          | 'episode.show.notFound'
    }

    @ConfineMetaClassChanges([MockMultipartFile])
    @Unroll
    void "save a serie with: title: #title, season #season, episodeNumber #episodeNumber, summary #summary, sendVideo #sendVideo, sendSerieId #sendSerieId => saves #success"() {
        given: 'send the data to the controller'
        episodeController.params.title = title
        episodeController.params.season = season
        episodeController.params.episodeNumber = episodeNumber
        episodeController.params.summary = summary
        MockMultipartHttpServletRequest mockRequest = new MockMultipartHttpServletRequest()
        episodeController.metaClass.request = mockRequest
        if (sendVideo){
            episodeController.request.addFile(new MockMultipartFile('video', 'myImage.jpeg', 'image/jpeg', 123 as byte[]))
        }
        if (sendSerieId) episodeController.params.serieId = serie.id
        if (sendEpisodeId) episodeController.params.episodeId = episodeToUpdate.id

        and: 'create the function available with the request but not with tue MockMultipartFile'
        MockMultipartFile.metaClass.getFilename = {
            'filename'
        }

        when: 'call to the save action'
        episodeController.save()

        then: 'check the action redirect to the corresponding endpoint and contains in the flash the result expected'
        episodeController.flash."${messageResult.keySet().first()}" == messageSource.getMessage(messageResult[messageResult.keySet().first()], [title].toArray(), Locale.ENGLISH)
        if (success) {
            assert episodeController.response.redirectedUrl == "http://localhost/episode/show?episodeId=${episodeToUpdate.id}"
        } else if (completeParams){
            assert episodeController.response.redirectedUrl == "http://localhost/serie/show?serieId=${serie.id}"
        }else{
            assert episodeController.response.redirectedUrl == 'http://localhost/'
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
        'a'   | 2       | 1              | 'asdf'  | false     | true        | false         || true           | false       | [error: 'episode.save.error']
        'a'   | 2       | 1              | 'asdf'  | true      | false       | false         || false          | false       | [error: 'episode.save.error']
        'a'   | 2       | 1              | 'asdf'  | true      | true        | true          || true           | true        | [message: 'episode.save.ok']
    }

    @Unroll
    void "get the episode video content"(){
        given: 'create an episode'
        Episode episode = Episode.build(pathToFile: episodeWithFile ? videoPath : '')

        and: 'send or not the episode id to the controller '
        episodeController.params.episodeId = sendEpisodeId ? episode.id : null

        when: 'call to the save action'
        episodeController.episodeContent()

        then: 'check if the action returns a 200'
        episodeController.response
        episodeController.response.status == OK.value()

        if(returnFileBytes){
            assert episodeController.response
        }

        where:
        sendEpisodeId | episodeWithFile || returnFileBytes
        false         | false           || false
        true          | false           || false
        false         | true            || false
        true          | true            || true
    }

    void cleanupSpec(){
        if(copiedVideo.exists()) copiedVideo.delete()
    }
}
