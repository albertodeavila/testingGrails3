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

import es.greach.Episode
import es.greach.EpisodeService
import es.greach.Serie
import grails.core.GrailsApplication
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Integration
@Rollback
class EpisodeServiceIntegrationSpec extends Specification {

    GrailsApplication grailsApplication
    @Shared EpisodeService episodeService

    @Unroll
    void "save/update an episode with title: #title , season: #season , episode: #episodeNumber , summary: #summary , video: #sendVideo , serie: #sendSerie , episodeToUpdate: #episode"() {
        given: 'a multipartFile'
        MultipartFile video = new MockMultipartFile('video', 'myVideo.jpeg', 'image/jpeg', 123 as byte[])

        and: 'a serie'
        Serie serie = Serie.build(name: UUID.randomUUID())

        and: 'create an episode to edit'
        Episode episodeToUpdate
        if (episode) episodeToUpdate = Episode.build(serie: serie)

        when: 'save or update the episode'
        Episode episodeSaved = episodeService.save(title, season, episodeNumber, summary, sendVideo ? video : null, sendSerie ? serie : null, episodeToUpdate)

        then: 'check if the returned episode contains the path and the file to cover'
        (episodeSaved != null) == episodeCreated
        if (episodeCreated) {
            assert episodeSaved.title == title
            assert episodeSaved.season == season
            assert episodeSaved.episodeNumber == episodeNumber
            assert episodeSaved.summary == summary
            if (sendVideo) assert episodeSaved.pathToFile
            if (sendSerie) assert episodeSaved.serie
        }

        where:
        title  | season | episodeNumber | summary   | sendVideo | sendSerie | episode || episodeCreated
        null   | null   | null          | null      | false     | false     | false   || false
        'name' | null   | null          | null      | false     | false     | false   || false
        'name' | 1      | null          | null      | false     | false     | false   || false
        'name' | 1      | 1             | null      | false     | false     | false   || false
        'name' | 1      | 1             | 'summary' | false     | false     | false   || false
        'name' | 1      | 1             | 'summary' | true      | false     | false   || false
        'name' | 1      | 1             | 'summary' | true      | true      | false   || true
        'name' | 1      | 1             | 'summary' | true      | true      | true    || true
    }

    @Unroll
    void "save the episode video with video: #hasVideo , episode: #hasEpisode , serie: #hasSerie"() {
        given: 'a multipartFile'
        MultipartFile video = hasVideo ? new MockMultipartFile('video', 'myVideo.jpeg', 'image/jpeg', 123 as byte[]) : null

        and: 'a serie'
        Serie serie = hasSerie ? Serie.build(name: UUID.randomUUID()) : null

        and: 'create an episode to edit'
        Episode episode = hasEpisode ? Episode.build(season: 2, episodeNumber:3, durationInSecs: 4, serie: serie, summary: 'summary') : null

        and: 'the necessary config is set'
        grailsApplication.config.path.episodes = 'episodes'
        grailsApplication.config.path.serie.cover = '/var/tmp/serie'

        when: 'save or update the episode'
        Episode episodeSavedWithFile = episodeService.saveVideoFile(video, serie, episode)

        then: 'check if the returned episode contains the path and the file to cover'
        if (hasVideo && hasSerie && hasEpisode){
            assert episodeSavedWithFile
            assert episodeSavedWithFile.pathToFile == "${grailsApplication.config.path.serie.cover}/${serie.id}/episodes/${video.originalFilename}".toString()
            assert episodeSavedWithFile.title
            assert episodeSavedWithFile.title == episode.title
            assert episodeSavedWithFile.season
            assert episodeSavedWithFile.season == episode.season
            assert episodeSavedWithFile.episodeNumber
            assert episodeSavedWithFile.episodeNumber == episode.episodeNumber
            assert episodeSavedWithFile.summary
            assert episodeSavedWithFile.summary == episode.summary
            assert episodeSavedWithFile.serie
            assert episodeSavedWithFile.serie == episode.serie
            assert episodeSavedWithFile.durationInSecs == episode.durationInSecs
        } else {
            assert !episodeSavedWithFile
        }

        where:
        hasVideo | hasSerie | hasEpisode
        false    | false    | false
        false    | true     | false
        true     | false    | false
        true     | true     | true
    }

    void "delete an episode"() {
        given: 'a serie to build the episode'
        Serie serie = Serie.build(name: UUID.randomUUID())

        and: 'an episode to delete'
        Episode episode = Episode.build(serie: serie)

        when: 'delete the episode'
        episode = episodeService.delete(episode)

        then: 'episode has been deleted successfully'
        !episode
    }

    void "delete the episode's file"() {
        given: 'a serie to build the episode'
        Serie serie = Serie.build(name: UUID.randomUUID())

        and: 'an episode to delete'
        Episode episode = Episode.build(serie: serie, pathToFile: 'https://www.youtube.com/watch?v=kJQP7kiw5Fk')

        when: "delete the episode's video file"
        episodeService.deleteVideoFile(episode)

        then: "episode's file and episode's pathToFile have been deleted successfully"
        episode
        !episode.file
        !episode.pathToFile

        cleanup: 'delete created objects'
        serie?.delete()
        episode?.delete()
    }

    void "calculate the episode's file in seconds"() {
        given: 'a video file'
        File video = new File('src/integration-test/groovy/es/greach/functional/utils/media/video.mp4')

        when: 'delete the episode'
        Long seconds = episodeService.calculateVideoDurationInSecs(video)

        then: "episode's file and episode's pathToFile have been deleted successfully"
        seconds
        seconds instanceof Long
    }
}