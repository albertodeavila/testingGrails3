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

import es.greach.Serie
import grails.test.mixin.TestFor
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@TestFor(Serie)
class SerieUnitSpec extends Specification {

    @Shared String utilFolder = "src${File.separator}test${File.separator}groovy${File.separator}es${File.separator}greach${File.separator}util${File.separator}"
    @Shared String imagesFolder = "grails-app${File.separator}assets${File.separator}images${File.separator}"

    /*************************************
            DOMAIN UNIT EXERCISE 1
     **************************************/
    @Ignore("Until start work on domain unit exercise 1")
    @Unroll
    void "Validates a Series's instance that must be valid: #valid, with params: #params"() {
        given: 'a serie built with some params'
        //TODO complete me

        expect: 'the serie is valid or not'
        //TODO complete me

        and: 'and if it is valid, save it and check the params '
        //TODO complete me

        where:
        params                                                                                  || valid
        [:]                                                                                     || false
        [name: 'name']                                                                          || false
        [channel: 'channel']                                                                    || false
        [releaseDate: new Date()]                                                               || false
        [pathToCover: 'pathToCover']                                                            || false
        [name: 'name', channel: 'channel']                                                      || false
        [name: 'name', releaseDate: new Date()]                                                 || false
        [name: 'name', pathToCover: 'pathToCover']                                              || false
        [name: 'name', channel: 'channel', releaseDate: new Date()]                             || true
        [name: 'name', channel: 'channel', pathToCover: 'pathToCover']                          || false
        [name: 'name', releaseDate: new Date(), pathToCover: 'pathToCover']                     || false
        [channel: 'channel', releaseDate: new Date()]                                           || false
        [channel: 'channel', pathToCover: 'pathToCover']                                        || false
        [channel: 'channel', releaseDate: new Date(), pathToCover: 'pathToCover']               || false
        [releaseDate: new Date(), pathToCover: 'pathToCover']                                   || false
        [name: 'name', channel: 'channel', releaseDate: new Date(), pathToCover: 'pathToCover'] || true
    }

    /*************************************
            DOMAIN UNIT EXERCISE 2
     **************************************/
    @Ignore("Until start work on domain unit exercise 2")
    @Unroll
    void "Gets serie's cover"() {
        given: 'defines path cover'
        //TODO complete me

        when: 'gets cover'
        //TODO complete me

        then: 'resulted cover is as expected'
        //TODO complete me

        where:
        coverPath                   || resultPath
        null                        || "${imagesFolder}default-thumbnail.jpg"
        "${utilFolder}test.jpg"     || "${utilFolder}test.jpg"
        "${utilFolder}noExists.jpg" || "${imagesFolder}default-thumbnail.jpg"
    }
}
