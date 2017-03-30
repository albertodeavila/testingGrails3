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
package es.greach.domain

import es.greach.Actor
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(Actor)
class ActorUnitSpec extends Specification {

    @Unroll
    void "Validates an Actor's instance that must be valid: #valid, with params: #params"() {
        given: 'an actor built with some params'
        Actor actor = new Actor(params)

        expect: 'the actor is valid or not'
        actor.validate() == valid

        and: 'and if it is valid, save it and check the params '
        if(valid) {
            assert actor.save()
            params.each { String key, String value ->
                assert actor."$key" == value
            }
        }

        where:
        params                                                 || valid
        [:]                                                    || false
        [name: 'name']                                         || false
        [imdbId: 'imdbId']                                     || false
        [imageUrl: 'imageUrl']                                 || false
        [name: 'name', imdbId: 'imdbId']                       || false
        [name: 'name', imageUrl: 'imageUrl']                   || false
        [name: 'name', imdbId: 'imdbId', imageUrl: 'imageUrl'] || true
        [name: 'name', imdbId: 'imdbId', imageUrl: 'imageUrl'] || true
    }
}
