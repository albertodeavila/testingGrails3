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
import es.greach.ActorService
import grails.buildtestdata.mixin.Build
import grails.core.DefaultGrailsApplication
import grails.core.GrailsApplication
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import grails.test.mixin.TestFor
import groovy.json.JsonOutput
import org.springframework.http.ResponseEntity
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import static org.springframework.http.HttpStatus.OK

@Build(Actor)
@TestFor(ActorService)
class ActorServiceUnitSpec extends Specification {

    @Shared GrailsApplication grailsApplication

    def setupSpec(){
        grailsApplication = new DefaultGrailsApplication()
        grailsApplication.config.api.actors.base = 'http://www.imdb.com/'
    }

    @Unroll
    void "search actor by name #name"(){
        given: 'the grailsApplication to set the config values'
        service.grailsApplication = grailsApplication

        and: 'define the bean'
        defineBeans {
            restBuilderBean(RestBuilder)
        }

        when: 'call the search method to find actors by name'
        Map resultSearch = service.searchActorsByName(name)

        then: 'check the API response and the data returned'
        resultSearch
        resultSearch.status == statusExpected.value()
        if(statusExpected == OK && data){
            assert resultSearch.data
        }

        where:
        name      || statusExpected         | data
        null      || INTERNAL_SERVER_ERROR  | false
        ''        || OK                     | false
        'Antonio' || OK                     | true
        'Ed Ha'   || OK                     | true
    }


    void "find an actor and check if the restBuilder is invoked" (){
        given: 'the restBluilder mocked '
        RestBuilder restBuilderBean = Mock()
        service.restBuilderBean = restBuilderBean

        when: 'call the method to find the actor'
        service.searchActorsByName('Antonio')

        then: 'the restBuilder is invoked one time'
        1 * restBuilderBean.get(_ as String, _ as Closure)
    }

    void "find an actor overriding the API response" () {
        given: 'the restBluilder mocked '
        Map data = [name_popular: [id: 1, name: 'Antonio Banderas', description: 'Actor']]
        RestBuilder restBuilderBean = Mock(RestBuilder){
            1 * get(_ as String, _ as Closure) >> {
                new RestResponse(ResponseEntity.ok(JsonOutput.toJson(data)))
            }
        }

        and: 'use it in the service'
        service.restBuilderBean = restBuilderBean

        when: 'call the method to find the actor'
        Map result = service.searchActorsByName('Antonio')

        then: 'check the data returned'
        result
        result.status == OK.value()
        result.data == [[id: 1, name: 'Antonio Banderas']]
    }

    void "search an actor by imdb Id"(){
        given: 'some data'
        String name = 'Antonio Banderas'
        String imageUrl = 'http://someUrl.com'
        String imdbId = 'someImdbId'

        and: 'override the restBuilderBean get method'
        service.restBuilderBean = new RestBuilder()
        service.restBuilderBean.metaClass.get = {String url, Closure closure ->
            new RestResponse( ResponseEntity.ok("<html><body><span itemprop='name'>${name}</span> <div id='img_primary'><div><a><img src='${imageUrl}'/></a></div></div></body></html>]]"))
        }

        when: 'call the method to get a new actor entity'
        Actor actor = service.findActorInAPIByImdbId(imdbId)

        then: 'check the data returned'
        actor
        actor.name == name
        actor.imageUrl == imageUrl
        actor.imdbId
    }

    @Unroll
    void "find or create an Actor"(){
        given: 'create the existent Actors'
        existentActorImdbs.each{ String imdbId->
            Actor.build(imdbId: imdbId)
        }

        and: 'integrate imdbs list'
        List<String> imdbs = (existentActorImdbs + inexistentActorsImdbs).flatten()

        and: 'override the findActorInAPIByImdbId method'
        service.metaClass.findActorInAPIByImdbId{ String imdbId ->
            Actor.build(imdbId: imdbId)
        }

        when: 'find or create the actors'
        List<Actor> actors = service.findOrCreateActorByImdbId(imdbs)

        then: 'check the result size'
        actors.size() == imdbs.size()

        where:
        existentActorImdbs | inexistentActorsImdbs
        []                 | []
        []                 | ['1']
        []                 | ['1', '2']
        ['3']              | ['1', '2']
        ['3', '4']         | ['1', '2']
    }

}
