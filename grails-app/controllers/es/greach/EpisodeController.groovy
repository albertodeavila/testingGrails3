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

import grails.plugin.springsecurity.annotation.Secured
import org.springframework.web.multipart.MultipartFile

import static org.springframework.http.HttpStatus.OK


class EpisodeController {

    EpisodeService episodeService

    /**
     *  Show the create form
     */
    @Secured(['ROLE_ADMIN'])
    def create(){
        Serie serie = Serie.get(params.serieId)
        if(serie) render view: 'save', model: [serie: serie]
        else redirect controller: 'serie'
    }

    /**
     *  Show the update form
     */
    @Secured(['ROLE_ADMIN'])
    def update(){
        Episode episode = Episode.get(params.long('episodeId'))
        if(episode) render view: 'save', model: [episode: episode]
        else redirect controller: 'serie'
    }

    /**
     * Save an Episode
     */
    @Secured(['ROLE_ADMIN'])
    def save(){
        if(params.title && params.season && params.episodeNumber && params.serieId) {
            String title = params.title
            String summary = params.summary
            Integer season = params.int('season', 0)
            Integer episodeNumber = params.int('episodeNumber', 0)

            Episode episode = params.episodeId ? Episode.get(params.long('episodeId')) : null
            Serie serie = Serie.get(params.long('serieId'))
            MultipartFile video = request.getFile('video')

            episode = episodeService.save(title, season, episodeNumber, summary, video?.filename ? video : null, serie, episode)
            if (episode) {
                flash.message = message code: 'episode.save.ok', args: [episode.title]
                redirect controller: 'episode', action: 'show', params: [episodeId: episode.id]
            } else if(serie){
                flash.error = message code: 'episode.save.error'
                redirect controller: 'serie', action: 'show', params: [serieId: serie.id]
            }else{
                flash.error = message code: 'episode.save.error'
                redirect controller: 'serie'
            }
        }else{
            flash.error = message code: 'episode.save.error'
            redirect controller: 'serie'
        }
    }

    /**
     * Show an episode
     */
    @Secured(['ROLE_USER', 'ROLE_ADMIN'])
    def show(){
        if(params.episodeId){
            Episode episode = Episode.get(params.long('episodeId'))
            if(episode) {
                return [episode: episode]
            }else {
                flash.error = message code: 'episode.show.notExists'
            }
        }else{
            flash.error = message code: 'episode.show.notFound'
        }
        redirect controller: 'serie'
    }

    /**
     * Render an output stream with the video content
     */
    @Secured(['ROLE_USER', 'ROLE_ADMIN'])
    def episodeContent(){
        Episode episode = Episode.get(params.long('episodeId'))
        if(episode) {
            try {
                File file = episode?.file

                if (episode && file) {
                    OutputStream out = response.getOutputStream()

                    response.setContentLength(file.length())
                    response.addHeader("Content-disposition", "attachment; filename=${file.name}")
                    response.addHeader("Content-type", "video/quicktime")
                    out.write(file.getBytes())
                    out.close()
                }
            }catch(Exception e){
                log.error 'Something went wrong with the video streaming action'
            }
        }
        response.status = OK.value()
    }


    /**
     * Delete the episode
     */
    @Secured(['ROLE_ADMIN'])
    def delete(){
        Episode episode = Episode.get(params.long('episodeId'))
        if(episode) {
            if (episodeService.delete(episode)) {
                flash.error = message code: 'episode.delete.error', args: [episode.title]
            } else {
                flash.message = message code: 'episode.delete.ok', args: [episode.title]
            }
        }else{
            flash.error = message code: 'episode.delete.notFound'
        }
        redirect controller: 'serie'
    }
}
