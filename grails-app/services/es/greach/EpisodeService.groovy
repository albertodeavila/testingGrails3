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
package es.greach

import com.xuggle.xuggler.IContainer
import grails.core.GrailsApplication
import grails.transaction.Transactional
import org.springframework.web.multipart.MultipartFile

import java.nio.file.FileAlreadyExistsException

@Transactional
class EpisodeService {

    GrailsApplication grailsApplication
    /**
     * Save or update an Episode with the given data
     * @param title the episode title
     * @param season the episode season
     * @param episodeNumber the number of the episode in the season
     * @param tempFile the file uploaded
     * @param serie the serie that belongs to
     * @param episode the episode to update
     * @return The episode saved or null otherwise
     */
    Episode save(String title, Integer season, Integer episodeNumber, String summary, MultipartFile video, Serie serie, Episode episode = null ) {
        if(serie) {
            if (!episode) episode = new Episode()
            episode.title = title
            episode.season = season
            episode.episodeNumber = episodeNumber
            episode.serie = serie
            episode.summary = summary

            if (episode.id && episode.pathToFile && video) {
                deleteVideoFile(episode)
            }
            if (video) episode = saveVideoFile(video, serie, episode)
            episode?.save() ? episode : null
        }
    }

    /**
     * Save to the file system the episode video
     * @param video the file
     * @param episode the entity related with the video
     * @return an episode with duration and the path to the file setted
     */
    Episode saveVideoFile(MultipartFile video, Serie serie, Episode episode){
        if(video && episode && serie){
            try{
                String separator = File.separator
                File episodesFolder = new File("${grailsApplication.config.path.serie.cover}$separator${serie.id}$separator${grailsApplication.config.path.episodes}$separator")
                if (!episodesFolder.exists()) episodesFolder.mkdirs()
                File finalVideo = new File("${grailsApplication.config.path.serie.cover}$separator${serie.id}$separator${grailsApplication.config.path.episodes}$separator${video.originalFilename}")
                video.transferTo(finalVideo)
                episode.durationInSecs = calculateVideoDurationInSecs(finalVideo)
                episode.pathToFile = finalVideo.absolutePath
                episode
            }catch (SecurityException e){
                log.error "A security exception ocurr: ${e.stackTrace}"
                episode.delete()
            } catch (FileAlreadyExistsException e){
                log.error "An FileAlreadyExist exception ocurr ${e.stackTrace}"
                episode.delete()
            } catch (IOException e){
                log.error "An IO exception ocurr: ${e.stackTrace}"
                episode.delete()
            } catch (Exception e) {
                log.error "An exception ocurr: ${e.stackTrace}"
                episode.delete()
            }
        }
    }

    /**
     * Delete the video file from the episode
     * @param episode the entity to delete its video file
     */
    void deleteVideoFile(Episode episode){
        try{
            episode.file?.delete()
            episode.pathToFile = null
        }catch (SecurityException e){
            log.error "A security exception ocurr: ${e.stackTrace}"
        } catch (FileAlreadyExistsException e){
            log.error "An FileAlreadyExist exception ocurr ${e.stackTrace}"
        } catch (IOException e){
            log.error "An IO exception ocurr: ${e.stackTrace}"
        } catch (Exception e) {
            log.error "An exception ocurr: ${e.stackTrace}"
        }
    }

    /**
     * Calculate the video duration
     * @param video the file to calculate the duration
     * @return a number with the duration in seconds
     */
    Long calculateVideoDurationInSecs(File video){
        IContainer container = IContainer.make()
        container.open(video.absolutePath, IContainer.Type.READ, null)
        Math.round(container.duration / 1000000)
    }


    /**
     * Delete the episode
     * @param episode the entity to delete
     * @return null if it's successfully deleted or the episode otherwise
     */
    Episode delete(Episode episode){
        Serie serie = episode?.serie
        serie?.removeFromEpisodes(episode)
        episode?.delete()
        Episode.get(episode?.id)
    }
}
