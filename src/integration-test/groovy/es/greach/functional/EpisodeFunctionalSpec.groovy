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
package es.greach.functional

import es.greach.Episode
import es.greach.Serie
import es.greach.functional.pages.episode.CreateEpisodePage
import es.greach.functional.pages.episode.EditEpisodePage
import es.greach.functional.pages.episode.ShowEpisodePage
import es.greach.functional.pages.serie.IndexSeriePage
import es.greach.functional.pages.serie.ShowSeriePage
import es.greach.functional.utils.FunctionalTest
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Unroll

@Stepwise
@Integration
@Rollback
class EpisodeFunctionalSpec extends FunctionalTest{

    @Shared String videoPath
    @Shared File copiedVideo

    void setupSpec(){
        File video = new File('src/integration-test/groovy/es/greach/functional/utils/media/video.mp4')
        copiedVideo = new File('src/integration-test/groovy/es/greach/functional/utils/media/video2.mp4')
        copiedVideo << video.text
        videoPath = copiedVideo.absolutePath
    }

    void "create an episode"(){
        given: 'a serie is created'
        Serie serie
        Serie.withNewSession { session->
            serie = Serie.build(name: UUID.randomUUID().toString())
        }

        and: 'login as an admin'
        loginAs('admin', '1234')

        when: 'go to show page'
        go("/serie/show?serieId=${serie.id}")

        then: 'the show page is present'
        at ShowSeriePage

        when: 'click on the add episode button'
        addEpisodeButton.click()

        then: 'we are at create episode page'
        at CreateEpisodePage

        when: 'fill the episode form'
        String title = 'Pilot'
        String season = '1'
        String number = '1'
        String summary = 'A summary sample'

        form.episodeTitle = title
        form.episodeSeason = season
        form.episodeEpisodeNumber = number
        form.episodeSummary = summary
        form.episodeVideo = videoPath

        and: 'click on the episode submit button'
        js.exec("\$('#episodeForm').submit()")

        then: 'we are at show episode page'
        at ShowEpisodePage

        and: 'check the values '
        episodeTitle == title
        episodeSeason == season
        episodeNumber == number
        episodeSummary == summary
        episodeVideo.isDisplayed()
    }

    @Unroll
    void "try to create an episode without the required fields"(){
        given:'a serie created'
        Serie serie
        Serie.withNewSession { session->
            serie = Serie.build(name: UUID.randomUUID().toString())
        }

        when: 'go to show page'
        go("/serie/show?serieId=${serie.id}")

        then: 'the show page is present'
        at ShowSeriePage

        when: 'click on the add episode button'
        addEpisodeButton.click()

        then: 'we are at create episode page'
        at CreateEpisodePage

        when: 'fill the episode form'
        form.episodeTitle = episodeTitle
        form.episodeSeason = season
        form.episodeEpisodeNumber = number
        form.episodeSummary = summary
        form.video = video

        and: 'click on the episode submit button'
        js.exec("\$('#episodeForm').submit()")

        then: 'we are at create episode page'
        at CreateEpisodePage

        and: 'a validation error message appears'
        fieldErrors.each{ String fieldError ->
            println fieldError
            form.validationErrorForField(fieldError) == validationError
        }

        where:
        episodeTitle | season | number | summary    | video       || fieldErrors                                    | validationError
        ''           | ''     | ''     | ''         | ''          || ['video', 'title', 'season', 'episodeNumber']  | 'This field is required.'
        'a'          | ''     | ''     | ''         | ''          || ['video', 'season', 'episodeNumber']           | 'This field is required.'
        ''           | '1'    | ''     | ''         | ''          || ['video', 'title', 'episodeNumber']            | 'This field is required.'
        ''           | ''     | '1'    | ''         | ''          || ['video', 'title', 'season']                   | 'This field is required.'
        'a'          | '1'    | ''     | ''         | ''          || ['video', 'episodeNumber']                     | 'This field is required.'
        'a'          | ''     | '1'    | ''         | ''          || ['video', 'season']                            | 'This field is required.'
        ''           | '1'    | '1'    | 'a'        | ''          || ['video', 'title']                             | 'This field is required.'
        'a'          | '1'    | '1'    | 'a'        | ''          || ['video']                                      | 'This fields is too long.'
        'a'          | '1'    | '1'    | 'a' * 4001 | videoPath   || ['summary']                                    | 'This fields is too long.'
    }

    void "show an episode"(){
        given: 'an episode is created'
        Episode episode
        Episode.withNewSession { session->
            episode = Episode.build(serie: Serie.build(name: UUID.randomUUID().toString()), summary: 'foo text', pathToFile: videoPath)
        }

        when: 'go to show episode page'
        go "/episode/show?episodeId=${episode.id}"

        then: 'we are at show episode page'
        at ShowEpisodePage

        and: 'the fileds has the expected values'
        episodeTitle == episode.title
        episodeSeason == "${episode.season}"
        episodeNumber == "${episode.episodeNumber}"
        episodeSummary == episode.summary
        episodeVideo.isDisplayed()
    }

    void "try to show an episode that doesn't exists"(){
        when: 'go to show page that doesn\'t exists'
        go("/episode/show?episodeId=-1")

        then: 'the index serie is shown'
        at IndexSeriePage

        and: 'an error message appears'
        messages.errorMessage == messageSource.getMessage('episode.show.notExists', null, null).trim()
    }

    void "try to show an episode without sending episodeId"(){
        when: 'go to show episode page'
        go("/episode/show?episodeId=")

        then: 'the index serie is shown'
        at IndexSeriePage

        and: 'an error message appear'
        messages.errorMessage == messageSource.getMessage('episode.show.notFound', null, null)
    }


    void "delete an episode"(){
        given: 'an episode'
        Episode episode
        Episode.withNewSession { session->
            episode = Episode.build(serie: Serie.build(name: UUID.randomUUID().toString()),
                    summary: 'foo text', pathToFile: videoPath)
        }

        when: 'go to show episode page'
        go "/episode/show?episodeId=${episode.id}"

        then: 'we are at show episode page'
        at ShowEpisodePage

        when: 'click on the delete button'
        deleteButton.click()

        then: 'the index serie page is shown'
        at IndexSeriePage

        and: 'a success message appears'
        messages.successMessage == messageSource.getMessage('episode.delete.ok', [episode.title].toArray(), null)
    }

    void "edit an episode"(){
        given: 'an episode is created'
        Episode episode
        Episode.withNewSession { session->
            episode = Episode.build(serie: Serie.build(name: UUID.randomUUID().toString()), summary: 'foo text', pathToFile: videoPath)
        }

        when: 'go to show episode page'
        go "/episode/show?episodeId=${episode.id}"

        then: 'we are at shoe episode page'
        at ShowEpisodePage

        when: 'click on edit button'
        editButton.click()

        then: 'we are at edit episode page'
        at EditEpisodePage

        when: 'fill the episode form'
        String title = 'Pilot'
        String season = '1'
        String number = '1'
        String summary = 'A summary sample'

        form.episodeTitle = title
        form.episodeSeason = season
        form.episodeEpisodeNumber = number
        form.episodeSummary = summary
        form.episodeVideo = videoPath

        and: 'click on the episode submit button'
        js.exec("\$('#episodeForm').submit()")

        then: 'we are at show episode page'
        at ShowEpisodePage

        and: 'check the values'
        episodeTitle == title
        episodeSeason == "${season}"
        episodeNumber == "${number}"
        episodeSummary == summary
        episodeVideo.isDisplayed()

    }

    void "list episodes"(){
        given: 'an episode is created'
        Episode episode
        Episode.withNewSession { session->
            episode = Episode.build(serie: Serie.build(name: UUID.randomUUID().toString()), summary: 'foo text', pathToFile: videoPath)
        }

        when: 'go to show serie page'
        go "/serie/show?serieId=${episode.serieId}"

        then: 'we are at shoe episode page'
        at ShowSeriePage

        and:'check if the episode is available and visible'
        episodesId.contains("${episode.id}")
        episodesTitle.contains(episode.title)
        episodesSeason.contains("${episode.season}")
        episodesNumber.contains("${episode.episodeNumber}")
    }

    void cleanupSpec(){
        if(copiedVideo.exists()) copiedVideo.delete()
    }
}
