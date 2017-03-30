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

import es.greach.Episode
import es.greach.Serie
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@Build([Serie])
@TestFor(Episode)
class EpisodeUnitSpec extends Specification {

    @Unroll
    void "Validates an Episode's instance that must be valid: #valid, with params: #params"() {
        given: 'an episode built with some params'
        Episode episode = new Episode(params)

        and: 'add serie if it is specified'
        if (addSerie) {
            Serie serie = Serie.build()
            episode.serie = serie
        }

        expect: 'the episode is valid or not'
        episode.validate() == valid

        and: 'and if it is valid, save it and check the params '
        if(valid){
            assert episode.save()
            params.each{ String key, value->
                assert episode."$key" == value
            }
        }

        where:
        params                                                                                                    | addSerie  || valid
        [:]                                                                                                       | false     || false
        [title: 'name']                                                                                           | false     || false
        [season: 1]                                                                                               | false     || false
        [episodeNumber: 1]                                                                                        | false     || false
        [durationInSecs: 1]                                                                                       | false     || false
        [pathToFile: 'pathToFile']                                                                                | false     || false
        [title: 'name', season: 1]                                                                                | false     || false
        [title: 'name', episodeNumber: 1]                                                                         | false     || false
        [title: 'name', durationInSecs: 1]                                                                        | false     || false
        [title: 'name', pathToFile: 'pathToFile']                                                                 | false     || false
        [season: 1, episodeNumber: 1, durationInSecs: 1]                                                          | false     || false
        [season: 1, episodeNumber: 1, pathToFile: 'pathToFile']                                                   | false     || false
        [episodeNumber: 1, durationInSecs: 1, pathToFile: 'pathToFile']                                           | false     || false
        [season: 1, episodeNumber: 1]                                                                             | false     || false
        [season: 1, durationInSecs: 1]                                                                            | false     || false
        [season: 1, pathToFile: 'pathToFile']                                                                     | false     || false
        [episodeNumber: 1, durationInSecs: 1]                                                                     | false     || false
        [episodeNumber: 1, pathToFile: 'pathToFile']                                                              | false     || false
        [title: 'name', season: 1, episodeNumber: 1]                                                              | false     || false
        [title: 'name', season: 1, durationInSecs: 1]                                                             | false     || false
        [title: 'name', season: 1, pathToFile: 'pathToFile']                                                      | false     || false
        [title: 'name', episodeNumber: 1, durationInSecs: 1]                                                      | false     || false
        [title: 'name', episodeNumber: 1, pathToFile: 'pathToFile']                                               | false     || false
        [title: 'name', durationInSecs: 1, pathToFile: 'pathToFile']                                              | false     || false
        [title: 'name', season: 1, episodeNumber: 1, durationInSecs: 1]                                           | false     || false
        [title: 'name', season: 1, episodeNumber: 1, pathToFile: 'pathToFile']                                    | false     || false
        [season: 1, episodeNumber: 1, durationInSecs: 1, pathToFile: 'pathToFile']                                | false     || false
        [title: 'name', season: 1, episodeNumber: 1, durationInSecs: 1, pathToFile: 'pathToFile']                 | false     || false
        [title: 'name', season: 1, episodeNumber: 1, durationInSecs: 1, pathToFile: 'pathToFile', summary: 'sum'] | false     || false

        [title: 'name']                                                                                           | true      || false
        [season: 1]                                                                                               | true      || false
        [episodeNumber: 1]                                                                                        | true      || false
        [durationInSecs: 1]                                                                                       | true      || false
        [pathToFile: 'pathToFile']                                                                                | true      || false
        [title: 'name', season: 1]                                                                                | true      || false
        [title: 'name', episodeNumber: 1]                                                                         | true      || false
        [title: 'name', durationInSecs: 1]                                                                        | true      || false
        [title: 'name', pathToFile: 'pathToFile']                                                                 | true      || false
        [season: 1, episodeNumber: 1, durationInSecs: 1]                                                          | true      || false
        [season: 1, episodeNumber: 1, pathToFile: 'pathToFile']                                                   | true      || false
        [episodeNumber: 1, durationInSecs: 1, pathToFile: 'pathToFile']                                           | true      || false
        [episodeNumber: 1, durationInSecs: 1, pathToFile: 'pathToFile', summary: 'summary']                       | true      || false
        [season: 1, episodeNumber: 1]                                                                             | true      || false
        [season: 1, durationInSecs: 1]                                                                            | true      || false
        [season: 1, pathToFile: 'pathToFile']                                                                     | true      || false
        [episodeNumber: 1, durationInSecs: 1]                                                                     | true      || false
        [episodeNumber: 1, pathToFile: 'pathToFile']                                                              | true      || false
        [title: 'name', season: 1, episodeNumber: 1]                                                              | true      || false
        [title: 'name', season: 1, durationInSecs: 1]                                                             | true      || false
        [title: 'name', season: 1, pathToFile: 'pathToFile']                                                      | true      || false
        [title: 'name', episodeNumber: 1, durationInSecs: 1]                                                      | true      || false
        [title: 'name', episodeNumber: 1, pathToFile: 'pathToFile']                                               | true      || false
        [title: 'name', durationInSecs: 1, pathToFile: 'pathToFile']                                              | true      || false
        [title: 'name', season: 1, episodeNumber: 1, durationInSecs: 1]                                           | true      || false
        [title: 'name', season: 1, episodeNumber: 1, pathToFile: 'pathToFile']                                    | true      || false
        [season: 1, episodeNumber: 1, durationInSecs: 1, pathToFile: 'pathToFile']                                | true      || false
        [title: 'name', season: 1, episodeNumber: 1, durationInSecs: 1, pathToFile: 'pathToFile']                 | true      || true
        [title: 'name', season: 1, episodeNumber: 1, durationInSecs: 1, pathToFile: 'pathToFile', summary: 'sum'] | true      || true
    }
}
