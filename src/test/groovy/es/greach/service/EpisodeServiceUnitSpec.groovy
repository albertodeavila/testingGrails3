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

import es.greach.Episode
import es.greach.EpisodeService
import es.greach.Serie
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.FileAlreadyExistsException

@Build([Serie, Episode])
@TestFor(EpisodeService)
class EpisodeServiceUnitSpec extends Specification {


    @Shared Episode episodeToUpdate

    def setup(){
        episodeToUpdate = Episode.build()
    }

    @Unroll
    void "Delete an episode"() {
        given: 'build an episode or not'
        Episode episode = buildEpisode ? Episode.build() : null

        when: 'delete the episode'
        Episode deletedEpisode = service.delete(episode)

        then: 'the episode is deleted'
        !deletedEpisode

        where:
        buildEpisode << [true, false]
    }

    void "Delete the serie cover"(){
        given: 'a video file'
        File video = new File('sampleVideo')
        video << 'content'

        when: 'build a episode with a path to the video file'
        Episode episode = Episode.build(pathToFile: video.absolutePath)

        then: 'the serie contains a video file'
        episode.file

        when: 'deletes the video file'
        service.deleteVideoFile(episode)

        then: 'the serie is deleted'
        !episode.pathToFile
        !episode.file

        cleanup: 'delete the video file'
        video?.delete()
    }

    @Unroll
    void "save the serie cover with: sendVideo: #sendVideo, sendSerie: #sendSerie and sendEpisode #sendEpisode"(){
        given: 'A multipartFile'
        MultipartFile video = new MockMultipartFile('image', 'myImage.jpg', 'image/jpeg', 123 as byte[])

        and: 'a new serie'
        Serie serie = Serie.build()

        and: 'a new episode'
        Episode episode = Episode.build()

        when: 'save the cover file to the file system'
        Episode updatedEpisode = service.saveVideoFile(sendVideo ? video : null, sendSerie ? serie : null, sendEpisode ? episode : null)

        then: 'check if the returned serie contains the path to cover and the file to cover'
        (updatedEpisode != null) == episodeUpdated
        if(episodeUpdated){
            assert updatedEpisode.pathToFile
            assert updatedEpisode.file
        }

        cleanup:
        if(episodeUpdated) updatedEpisode.file.delete()

        where:
        sendVideo | sendSerie | sendEpisode || episodeUpdated
        false     | false     | false       || false
        false     | true      | false       || false
        true      | false     | false       || false
        true      | true      | false       || false
        true      | true      | true        || true
    }


    @Unroll
    void "try to save a video and throws an exception #exceptionThrown"(){
        given: 'the methods to invoke overrided'
        service.metaClass.saveVideoFile = {MultipartFile cover, Serie serie, Episode episode ->
            throw exceptionThrown.newInstance('')
        }

        service.metaClass.deleteVideoFile = { Episode episode ->
            throw exceptionThrown.newInstance('')
        }

        when: 'save or delete the cover file to/from the file system'
        service."$method"(methodArgs)

        then: 'the exception is thrown'
        thrown(exceptionThrown)

        where:
        method            | methodArgs         | exceptionThrown
        'saveVideoFile'   | [null, null, null] | SecurityException
        'saveVideoFile'   | [null, null, null] | FileAlreadyExistsException
        'saveVideoFile'   | [null, null, null] | IOException
        'saveVideoFile'   | [null, null, null] | Exception

        'deleteVideoFile' | null               | SecurityException
        'deleteVideoFile' | null               | FileAlreadyExistsException
        'deleteVideoFile' | null               | IOException
        'deleteVideoFile' | null               | Exception
    }

    @Unroll
    void "save/update a episode"(){
        given: 'A multipartFile'
        MultipartFile video = new MockMultipartFile('video', 'myVideo.jpeg', 'image/jpeg', 123 as byte[])

        and: 'create a new serie'
        Serie serie = Serie.build()

        when: 'save or update the episode'
        Episode episodeSaved = service.save(title, season, episodeNumber, summary, sendVideo ? video : null, sendSerie ? serie : null, episode)

        then: 'check if the returned episode contains the path to cover and the file to cover'
        (episodeSaved != null) == episodeCreated
        if(episodeCreated){
            assert episodeSaved.title == title
            assert episodeSaved.season == season
            assert episodeSaved.episodeNumber == episodeNumber
            assert episodeSaved.summary == summary
            if(sendVideo) assert episodeSaved.pathToFile
            if(sendSerie) assert episodeSaved.serie
        }

        cleanup: 'delete the serie and the episode'
        serie.delete()
        if(episodeCreated) episodeSaved.delete()

        where:
        title  | season | episodeNumber | summary   | sendVideo  | sendSerie | episode          || episodeCreated
        null   | null   | null          | null      | false      | false     | null             || false
        'name' | null   | null          | null      | false      | false     | null             || false
        'name' | 1      | null          | null      | false      | false     | null             || false
        'name' | 1      | 1             | null      | false      | false     | null             || false
        'name' | 1      | 1             | 'summary' | false      | false     | null             || false
        'name' | 1      | 1             | 'summary' | true       | false     | null             || false
        'name' | 1      | 1             | 'summary' | true       | true      | null             || true
        'name' | 1      | 1             | 'summary' | true       | true      | episodeToUpdate  || true
    }
}
