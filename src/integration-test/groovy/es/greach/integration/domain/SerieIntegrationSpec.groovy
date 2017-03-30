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
import es.greach.Episode
import es.greach.Serie
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Integration
@Rollback
class SerieIntegrationSpec extends Specification {

    @Shared String utilFolder = "src${File.separator}test${File.separator}groovy${File.separator}es${File.separator}greach${File.separator}util${File.separator}"
    @Shared String imagesFolder = "grails-app${File.separator}assets${File.separator}images${File.separator}"

    @Unroll
    void "Validates a Series's instance that must be valid: #valid, with params: #params"() {
        given: 'a serie is built with some params'
        Serie serie = new Serie(params)
        Actor actor
        Episode episode

        and: 'create episodes and actors'
        if (hasActors) {
            actor = Actor.build()
            actor.addToSeries(serie)
        }

        if (hasEpisodes) {
            episode = Episode.build(serie: serie)
            serie.addToEpisodes(episode)
        }

        expect: 'the serie is valid or not'
        serie.validate() == valid
        if(valid) {
            assert serie.save()
            params.each{ String key, value ->
                assert serie."$key" == value
            }
        }

        where:
        params                                                                                                        | hasActors | hasEpisodes || valid
        [:]                                                                                                           | false     | false       || false
        [name: UUID.randomUUID().toString()]                                                                          | false     | false       || false
        [channel: 'channel']                                                                                          | false     | false       || false
        [releaseDate: new Date()]                                                                                     | false     | false       || false
        [pathToCover: 'pathToCover']                                                                                  | false     | false       || false
        [name: UUID.randomUUID().toString(), channel: 'channel']                                                      | false     | false       || false
        [name: UUID.randomUUID().toString(), releaseDate: new Date()]                                                 | false     | false       || false
        [name: UUID.randomUUID().toString(), pathToCover: 'pathToCover']                                              | false     | false       || false
        [name: UUID.randomUUID().toString(), channel: 'channel', releaseDate: new Date()]                             | false     | false       || true
        [name: UUID.randomUUID().toString(), channel: 'channel', releaseDate: new Date()]                             | false     | true        || true
        [name: UUID.randomUUID().toString(), channel: 'channel', releaseDate: new Date()]                             | true      | false       || true
        [name: UUID.randomUUID().toString(), channel: 'channel', releaseDate: new Date()]                             | true      | true        || true

        [name: UUID.randomUUID().toString(), channel: 'channel', pathToCover: 'pathToCover']                          | false     | false       || false
        [name: UUID.randomUUID().toString(), releaseDate: new Date(), pathToCover: 'pathToCover']                     | false     | false       || false
        [channel: 'channel', releaseDate: new Date()]                                                                 | false     | false       || false
        [channel: 'channel', pathToCover: 'pathToCover']                                                              | false     | false       || false
        [channel: 'channel', releaseDate: new Date(), pathToCover: 'pathToCover']                                     | false     | false       || false
        [releaseDate: new Date(), pathToCover: 'pathToCover']                                                         | false     | false       || false
        [name: UUID.randomUUID().toString(), channel: 'channel', releaseDate: new Date(), pathToCover: 'pathToCover'] | false     | false       || true
        [name: UUID.randomUUID().toString(), channel: 'channel', releaseDate: new Date(), pathToCover: 'pathToCover'] | false     | true        || true
        [name: UUID.randomUUID().toString(), channel: 'channel', releaseDate: new Date(), pathToCover: 'pathToCover'] | true      | false       || true
        [name: UUID.randomUUID().toString(), channel: 'channel', releaseDate: new Date(), pathToCover: 'pathToCover'] | true      | true        || true
    }


    @Unroll
    void "Gets serie's cover"() {
        given: 'defines a serie'
        Serie serie = new Serie (pathToCover: coverPath)

        when: 'gets the cover'
        File cover = serie.getCover()

        then: 'resulted cover is as expected'
        cover.path == resultPath

        where:
        coverPath                                                                                                                                     || resultPath
        null                        || "${imagesFolder}default-thumbnail.jpg"
        "${utilFolder}test.jpg"     || "${utilFolder}test.jpg"
        "${utilFolder}noExists.jpg" || "${imagesFolder}default-thumbnail.jpg"
    }
}
