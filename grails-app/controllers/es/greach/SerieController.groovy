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

class SerieController {

    SerieService serieService
    ActorService actorService

    /**
     *  List series
     */
    @Secured(['ROLE_USER', 'ROLE_ADMIN'])
    def index() {
        List<Serie> series =  Serie.list()
        List<List<Serie>> seriesGrouped = series? series.collate(3) : []
        [series: seriesGrouped, rows: Math.ceil(Serie.count() / 3) ]
    }

    /**
     *  Show the create form
     */
    @Secured('ROLE_ADMIN')
    def create(){
        render view: 'save'
    }

    /**
     *  Show the update form
     */
    @Secured('ROLE_ADMIN')
    def update(){
        if(params.serieId){
            Serie serie = Serie.get(params.long('serieId'))
            if(serie) {
                return render (view: 'save', model: [serie: Serie.get(params.long('serieId'))])
            }else {
                flash.error = message code: 'serie.update.notExists'
            }
        }else{
            flash.error = message code: 'serie.update.notFound'
        }
        redirect controller: 'serie', action: 'index'
    }

    /**
     *  Save or update a serie
     */
    @Secured('ROLE_ADMIN')
    def save(){
        if(params.name && params.channel && params.releaseDate){
            String name = params.name
            String channel = params.channel

            Date releaseDate
            try {
                releaseDate = Date.parse(message(code: 'default.date.format'), params.releaseDate)
            }catch(e){
                log.error 'An error ocurr trying to parse a date'
                flash.error = message code: 'serie.save.error'
            }
            if(releaseDate){
                List<String> actorsIds = []
                if(params.actors && params.actors instanceof String[]){
                    actorsIds = params.actors.toList()
                }else if(params.actors && params.actors instanceof String){
                    actorsIds = [params.actors]
                }
                List<Actor> actors = actorsIds ? actorService.findOrCreateActorByImdbId(actorsIds) : []

                Serie serie = params.serieId ? Serie.get(params.long('serieId')) : null
                MultipartFile cover = request.getFile('cover')
                serie = serieService.save(name, channel, releaseDate, cover?.filename ? cover : null, actors, serie)
                if(serie){
                    flash.message = message code: 'serie.save.ok', args: [serie.name]
                }else{
                    flash.error = message code: 'serie.save.error'
                }
            }else{
                flash.error = message code: 'serie.save.error'
            }
        }else{
            flash.error = message code: 'serie.save.error'
        }
        redirect controller: 'serie', action: 'index'
    }

    /**
     *  Show a serie by the given id
     */
    @Secured(['ROLE_USER', 'ROLE_ADMIN'])
    def show(){
        if(params.serieId){
            Serie serie = Serie.get(params.long('serieId'))
            if(serie) {
                return render(view: 'show', model: [serie: serie])
            }else {
                flash.error = message code: 'serie.show.notExists'
            }
        }else{
            flash.error = message code: 'serie.show.notFound'
        }
        redirect controller: 'serie', action: 'index'
    }

    /**
     * Delete the serie
     */
    @Secured(['ROLE_ADMIN'])
    def delete(){
        Serie serie = Serie.get(params.serieId)
        if(serie) {
            if (serieService.delete(serie)) {
                flash.error = message code: 'serie.delete.error', args: [serie.name]
            } else {
                flash.message = message code: 'serie.delete.ok', args: [serie.name]
            }
        }else{
            flash.error = message code: 'serie.delete.notFound'
        }
        redirect controller: 'serie', action: 'index'
    }
}
