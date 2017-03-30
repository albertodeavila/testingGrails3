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

import es.greach.*
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.util.GrailsWebMockUtil
import org.grails.encoder.CodecLookup
import org.grails.plugins.web.taglib.ValidationTagLib
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.mock.web.MockMultipartFile
import org.springframework.mock.web.MockMultipartHttpServletRequest
import org.springframework.web.context.WebApplicationContext
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Integration
@Rollback
class SerieControllerIntegrationSpec extends Specification{

    @Autowired SerieController serieController
    @Autowired WebApplicationContext ctx
    @Autowired SerieService serieService
    @Autowired MessageSource messageSource
    @Shared String formatDate = 'MM/dd/yyyy'

    void setup() {
        GrailsWebMockUtil.bindMockWebRequest(ctx)
    }

    void "Show index view"() {
        given: 'delete the previous series'
        Serie.list()*.delete()

        and: 'create as many series as is specified'
        nSeries.times {
            Serie.build(name: UUID.randomUUID())
        }
        when: 'call to the index action and store result'
        Map result = serieController.index()

        then: 'the series list has the expected size'
        result.series.size() == seriesGruopedSize

        and: 'the number of rows is as expected'
        result.rows == rows

        where:
        nSeries || seriesGruopedSize | rows
        0       || 0                 | 0
        1       || 1                 | 1
        3       || 1                 | 1
        5       || 2                 | 2
    }

    void "show the create view"() {
        when: 'call the create action'
        serieController.create()

        then: "view's name is /serie/save"
        serieController.modelAndView.viewName == "/serie/save"
    }

    @Unroll
    void "saves a new serie"() {
        given: 'mock request with multipart to be able to add files'
        MockMultipartHttpServletRequest mockRequest = new MockMultipartHttpServletRequest()
        serieController.metaClass.request = mockRequest

        and: 'send the data to the controller'
        serieController.params.name = name
        serieController.params.channel = channel
        serieController.params.releaseDate = releaseDate
        serieController.params.actors = actors

        if(sendCover) {
            serieController.params.cover = new MockMultipartFile('image', 'myImage.jpg', 'image/jpeg', 123 as byte[])
        }
        serieController.params.serieId = sendSerieId ? Serie.build(name: UUID.randomUUID()).id : null

        when: 'call to the save action'
        serieController.save()

        and: 'set arguments if needed'
        Object[] args
        if(result.message)  args = [name]

        then: 'check the message and the redirect URL'
        serieController.flash."${result.keySet().first()}" == messageSource.getMessage(result[result.keySet().first()], args, Locale.ENGLISH)
        serieController.response.redirectedUrl == 'http://localhost/'

        where:
        name | channel | releaseDate                    | actors                     | sendCover | sendSerieId || result
        null | null    | null                           | null                       | false     | false       || [error: 'serie.save.error']
        'a'  | null    | null                           | null                       | false     | false       || [error: 'serie.save.error']
        'a'  | 'b'     | null                           | null                       | false     | false       || [error: 'serie.save.error']
        'a'  | 'b'     | 'c'                            | null                       | false     | false       || [error: 'serie.save.error']
        'a'  | 'b'     | 1                              | null                       | false     | false       || [error: 'serie.save.error']
        'a'  | 'b'     | new Date().format(formatDate)  | null                       | false     | false       || [message: 'serie.save.ok']
        1    | 'b'     | new Date().format(formatDate)  | null                       | false     | false       || [message: 'serie.save.ok']
        'a'  | 2       | new Date().format(formatDate)  | null                       | false     | false       || [message: 'serie.save.ok']
        'a'  | 2       | new Date().format(formatDate)  | 'nm0000164'                | false     | false       || [message: 'serie.save.ok']
        'a'  | 2       | new Date().format(formatDate)  | ['nm0000164', 'nm0000438'] | false     | false       || [message: 'serie.save.ok']
        'a'  | 2       | new Date().format(formatDate)  | ['nm0000164', 'nm0000438'] | true      | false       || [message: 'serie.save.ok']
        'a'  | 2       | new Date().format(formatDate)  | ['nm0000164', 'nm0000438'] | false     | true        || [message: 'serie.save.ok']
        'a'  | 2       | new Date().format(formatDate)  | ['nm0000164', 'nm0000438'] | true      | true        || [message: 'serie.save.ok']
    }

    @Unroll
    void "show the updates form"() {
        given: 'create a serie to update and assign to params'
        Serie serie
        if (serieId == 'new') {
            serie = Serie.build(name: UUID.randomUUID())
            serieId = serie.id
        }
        serieController.params.serieId = serieId

        when: 'call to the update action'
        serieController.update()

        then: 'if the serie doesn\'t exists, a message is shown and it redirects. If the serie exists, check the view rendered and the model'
        if (code) {
            assert serieController.flash.error == messageSource.getMessage(code, null, Locale.ENGLISH)
            assert serieController.response.redirectedUrl == 'http://localhost/'
        }else {
            assert serieController.modelAndView.viewName == '/serie/save'
            assert serieController.modelAndView.model.serie.id == serie.id
            assert serieController.modelAndView.model.serie.name == serie.name
            assert serieController.modelAndView.model.serie.channel == serie.channel
            assert serieController.modelAndView.model.serie.releaseDate == serie.releaseDate
            assert serieController.modelAndView.model.serie.pathToCover == serie.pathToCover
        }

        where:
        serieId || code
        null    || 'serie.update.notFound'
        -1      || 'serie.update.notExists'
        'new'   || ''
    }

    @Unroll
    void "shows a serie"() {
        given: 'create a serie to update and assign to params'
        Serie serie
        if (serieId == 'new') {
            serie = Serie.build(name: UUID.randomUUID())
            serieId = serie.id
        }
        serieController.params.serieId = serieId

        when: 'call to the show action'
        serieController.show()

        then: 'if the serie doesn\'t exists, a message is shown and it redirects. If the serie exists, check the view rendered and the model'
        if (code) {
            assert serieController.flash.error == messageSource.getMessage(code, null, Locale.ENGLISH)
            assert serieController.response.redirectedUrl == 'http://localhost/'
        } else {
            assert serieController.modelAndView.viewName == '/serie/show'
            assert serieController.modelAndView.model.serie.id == serie.id
            assert serieController.modelAndView.model.serie.name == serie.name
            assert serieController.modelAndView.model.serie.channel == serie.channel
            assert serieController.modelAndView.model.serie.releaseDate == serie.releaseDate
            assert serieController.modelAndView.model.serie.pathToCover == serie.pathToCover
        }

        where:
        serieId || code
        null    || 'serie.show.notFound'
        -1      || 'serie.show.notExists'
        'new'   || ''
    }

    @Unroll
    void "deletes a serie"() {
        given: 'create a serie to update and assign to params'
        Serie serie
        Object[] args
        if (serieId == 'new') {
            serie = Serie.build(name: UUID.randomUUID())
            serieId = serie.id
            args = [serie.name]
        }
        serieController.params.serieId = serieId

        and: 'override the delete method'
        def originalDeleteMethod = serieService.&delete
        if(failSaving){
            serieService.metaClass.delete = { Serie serieToDelete ->
                true
            }
        }

        when: 'call the delete action'
        serieController.delete()

        then: 'check the flash returned and the redirect'
        serieController.flash."${messageType}" == messageSource.getMessage(code, args, Locale.ENGLISH)
        serieController.response.redirectedUrl == 'http://localhost/'

        cleanup: 'restore delete method'
        serieService.metaClass.delete = originalDeleteMethod

        where:
        serieId || messageType | code                    | failSaving
        null    || 'error'     | 'serie.delete.notFound' | false
        -1      || 'error'     | 'serie.delete.notFound' | false
        'new'   || 'message'   | 'serie.delete.ok'       | false
        'new'   || 'error'     | 'serie.delete.error'    | true
    }
}
