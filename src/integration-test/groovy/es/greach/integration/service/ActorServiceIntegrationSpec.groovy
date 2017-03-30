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
import es.greach.ActorService
import grails.core.GrailsApplication
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.http.HttpStatus.OK

@Integration
@Rollback
class ActorServiceIntegrationSpec extends Specification {

    GrailsApplication grailsApplication

    @Shared ActorService actorService

    @Unroll
    void "search actor by name #name"(){
        given: 'base to look for actors'
        grailsApplication.config.api.actors.base = "http://www.imdb.com/"

        when: 'call to the method to find actors by name'
        Map resultSearch = actorService.searchActorsByName(name)

        then: 'check the API response and the data returned'
        resultSearch
        resultSearch.status == statusExpected.value()
        if(statusExpected == OK && data){
            assert resultSearch.data
            assert resultSearch.data.any {it.name.contains(name)}
        }

        where:
        name      || statusExpected         | data
        ''        || OK                     | false
        'Antonio' || OK                     | true
        'Ed Ha'   || OK                     | true
    }

    void "search an actor by the imdbId"(){
        given: 'some data'
        String name = 'Pauley Perrette'
        String imdbId = 'nm0005306'
        String url = 'images-amazon.com/images/M/MV5'
        Long id = 1

        and: 'base to look for actors'
        grailsApplication.config.api.actors.base = "http://www.imdb.com/"

        when: 'call the method to get a new actor entity'
        Actor actor = actorService.findActorInAPIByImdbId(imdbId)

        then: 'check the fixed data returned'
        actor.with {
            name == name
            imdbId == imdbId
            id == id
            imageUrl.contains(url)
        }
    }

    @Unroll
    void "find or create an Actor"(){
        given: 'integrate imdbs list'
        List<String> imdbs = (existentActorImdbs + inexistentActorsImdbs).flatten()

        and: 'create actors with the given imdb'
        List actorsToDelete = []
        imdbs.each { String imdb ->
            actorsToDelete << Actor.build(imdbId: imdb)
        }

        when: 'find or create the actors'
        List<Actor> actors = actorService.findOrCreateActorByImdbId(imdbs)

        then: 'check the result size'
        actors.size() == imdbs.size()
        if (actors) actors.every {it.imdbId in imdbs}

        cleanup: 'delete the created objects'
        actorsToDelete*.delete()

        where:
        existentActorImdbs | inexistentActorsImdbs
        []                 | []
        []                 | ['1']
        []                 | ['1', '2']
        ['3']              | ['1', '2']
        ['3', '4']         | ['1', '2']
    }
}
