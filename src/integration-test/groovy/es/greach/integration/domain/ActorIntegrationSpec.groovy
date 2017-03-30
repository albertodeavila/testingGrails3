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
package es.greach.integration.domain

import es.greach.Actor
import es.greach.Serie
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Specification
import spock.lang.Unroll

@Integration
@Rollback
class ActorIntegrationSpec extends Specification{

    @Unroll
    void "Validates an Actor's instance that must be valid: #valid, with params: #params"() {
        given: 'an actor is built with some params'
        Actor actor = new Actor(params)

        and: 'a serie is created'
        Serie serie
        if(hasSerie){
            serie = Serie.build(name: UUID.randomUUID())
            serie.addToActors(actor)
        }

        expect: 'the actor is valid or not'
        actor.validate() == valid
        if(valid){
            assert actor.save()
        }

        where:
        params                                                  |    hasSerie || valid
        [:]                                                     |    false    || false
        [name: 'name']                                          |    false    || false
        [imdbId: 'imdbId']                                      |    false    || false
        [imageUrl: 'imageUrl']                                  |    false    || false
        [name: 'name', imdbId: 'imdbId']                        |    false    || false
        [name: 'name', imageUrl: 'imageUrl']                    |    false    || false
        [name: 'name', imdbId: 'imdbId', imageUrl: 'imageUrl']  |    false    || true
        [name: 'name', imdbId: 'imdbId', imageUrl: 'imageUrl']  |    true     || true
    }
}
