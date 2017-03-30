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

import es.greach.*
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import org.springframework.mock.web.MockMultipartFile
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Build([Serie, Actor])
@TestFor(SerieController)
class SerieControllerUnitSpec extends Specification {

    @Shared Serie serieToUpdate
    @Shared String formatDate = 'MM/dd/yyyy'

    def setup(){
        serieToUpdate = Serie.build()
        defineBeans {
            serieService(SerieService)
        }
    }


    @Unroll
    void "delete a serie"(){
        given: 'create a serie'
        Serie serie = Serie.build()

        and: 'mock the service'
        controller.serieService = Mock(SerieService)
        controller.serieService.delete(_) >> (errorDeleting ? serie : null)

        and: 'send the id to the action'
        controller.params.serieId = sendSerieId ? serie.id : ''

        when: 'call to the action to delete the serie'
        controller.delete()

        then: 'check if the response is redirected to "/" and if success message or error is returned'
        controller

        controller.response.redirectedUrl == '/serie/index'
        if(!sendSerieId) assert controller.flash.error == 'serie.delete.notFound'
        else if(errorDeleting) assert controller.flash.error == 'serie.delete.error'
        else assert controller.flash.message == 'serie.delete.ok'

        where:
        sendSerieId | errorDeleting
        true        | false
        false       | false
        true        | true
        false       | true
    }

    @Unroll
    void "#action a serie sending the id #sendSerieId about a serie that exist #existsSerie"(){
        given: 'create a serie'
        Serie serie = Serie.build()

        and: 'send the id to the action'
        controller.params.serieId = sendSerieId ? (existSerie ? serie.id : -1L) : ''

        when: 'call to the action to show/update the serie'
        controller."${action}"()

        then: 'check the response, the flash message, the view rendered and the model'
        controller
        if(!sendSerieId || !existSerie){
            assert controller.response.redirectedUrl == '/serie/index'
        }else{
            assert view == "/serie/${viewRendered}"
            assert model.serie == serie
        }

        if(!sendSerieId) assert controller.flash.error == "serie.${action}.notFound"
        else if(!existSerie) assert controller.flash.error == "serie.${action}.notExists"
        else assert !controller.flash

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

    void "show the create form in the serie controller"(){
        when: 'call to the create action'
        controller.create()

        then: 'the view is rendered'
        view == '/serie/save'
    }

    void "show index in the serie controller"(){
        given: 'create 12 series'
        12.times{
            Serie.build()
        }

        when: 'call to the index action'
        Map result = controller.index()

        then: 'check the returned map'
        result
        result.series == Serie.list().collate(3)
        result.rows == Math.ceil(Serie.count() / 3)
    }

    @Unroll
    void "save a serie"(){
        given: 'override the message method to return'
        controller.metaClass.message { Map args->
            args.code == 'default.date.format' ? 'MM/dd/yyyy' : args.code
        }

        and: 'mock the ActorService to return actors'
        controller.actorService = Mock(ActorService)
        controller.actorService.findOrCreateActorByImdbId(_) >> {List <String> imdbIds ->
            List<Actor> actors = []
            imdbIds.size().times {
                actors << Actor.build()
            }
            actors
        }

        and: 'send the data to the controller'
        controller.params.name = name
        controller.params.channel = channel
        controller.params.releaseDate = releaseDate
        controller.params.actors = actors
        if(sendCover) {
            controller.params.cover = new MockMultipartFile('image', 'myImage.jpg', 'image/jpeg', 123 as byte[])
        }
        controller.params.serieId = sendSerieId ? serieToUpdate.id : null

        when: 'call to the save action'
        controller.save()

        then: 'check if the action is redirected to "/" and contains in the flash the expected result'
        controller.response.redirectedUrl == '/serie/index'
        controller.flash == result

        where:
        name | channel | releaseDate                    | actors            | sendCover | sendSerieId || result
        null | null    | null                           | null              | false     | false       || [error: 'serie.save.error']
        'a'  | null    | null                           | null              | false     | false       || [error: 'serie.save.error']
        'a'  | 'b'     | null                           | null              | false     | false       || [error: 'serie.save.error']
        'a'  | 'b'     | 'c'                            | null              | false     | false       || [error: 'serie.save.error']
        'a'  | 'b'     | 1                              | null              | false     | false       || [error: 'serie.save.error']
        'a'  | 'b'     | new Date().format(formatDate)  | null              | false     | false       || [message: 'serie.save.ok']
        1    | 'b'     | new Date().format(formatDate)  | null              | false     | false       || [message: 'serie.save.ok']
        'a'  | 2       | new Date().format(formatDate)  | null              | false     | false       || [message: 'serie.save.ok']
        'a'  | 2       | new Date().format(formatDate)  | 'asdf'            | false     | false       || [message: 'serie.save.ok']
        'a'  | 2       | new Date().format(formatDate)  | ['asdf', 'asdf2'] | false     | false       || [message: 'serie.save.ok']
        'a'  | 2       | new Date().format(formatDate)  | ['asdf', 'asdf2'] | true      | false       || [message: 'serie.save.ok']
        'a'  | 2       | new Date().format(formatDate)  | ['asdf', 'asdf2'] | false     | true        || [message: 'serie.save.ok']
        'a'  | 2       | new Date().format(formatDate)  | ['asdf', 'asdf2'] | true      | true        || [message: 'serie.save.ok']
    }
}
