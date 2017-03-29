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

import grails.core.GrailsApplication
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import static org.springframework.http.HttpStatus.OK

class ActorService {

    RestBuilder restBuilderBean
    GrailsApplication grailsApplication

    /**
     * Search actors and actresses from an API based on IMDB data
     * @see http://www.imdb.com/xml/find?json=1&nr=1&nm=on&q=jeniffer+garner
     * @param name the actor/actress name
     * @return a map with:
     *      - list of result
     *      - status of the api request
     */
    Map searchActorsByName(String name) {
        Map result = [ status: INTERNAL_SERVER_ERROR.value(), data: []]
        String apiBase = grailsApplication.config.api.actors.base
        try {
            RestResponse response = restBuilderBean.get("${apiBase}xml/find?json=1&nr=1&nm=on&q=${name}") {
                contentType('application/json; charset=utf-8')
            }

            result.status = response.status
            if (response.status == OK.value() && response.json) {
                List actors = []
                if(response.json.name_approx) actors += response.json.name_approx
                if(response.json.name_substring) actors += response.json.name_substring
                if(response.json.name_popular) actors += response.json.name_popular
                if(response.json.name_exact) actors += response.json.name_exact

                actors = actors.findAll{it.description.contains("Actor") || it.description.contains("Actress")}
                result.data = actors.collect { [id: it.id, name: it.name] }
            }else{
                log.error "Can not find actors by name ${name}"
            }
        }catch(Exception e){
            log.error "An exception ocurr while searching actors by name: ${name} Exeption: ${e.stackTrace}"
        }
        result
    }

    /**
     * Search in database if an actor exists and if it doesn't exists, go to the API and create a new one
     * @param imdbIds a list of imdb id's to search
     * @return a list of actors
     */
    List<Actor> findOrCreateActorByImdbId(List<String> imdbIds){
        List<Actor> actors = []
        imdbIds.each{ String imdbId->
            Actor actor = Actor.findByImdbId(imdbId) ?: findActorInAPIByImdbId(imdbId)
            if(actor) actors << actor
        }
        actors
    }

    /**
     * Search an actor in the API and save it if it's founded
     * @param imdbId the imdb id to search the actor
     * @return an actor or null if it can not be found
     */
    Actor findActorInAPIByImdbId(String imdbId){
        String apiBase = grailsApplication.config.api.actors.base
        try {
            RestResponse response = restBuilderBean.get("${apiBase}name/$imdbId") {
                contentType('application/json; charset=utf-8')
            }

            if (response.status == OK.value() && response.text) {
                String html = response.text
                Document document = Jsoup.parse(html)
                String name = document.select('[itemprop="name"]').html()
                String imageUrl = document.select('#img_primary div a img').attr('src')
                new Actor(name: name, imdbId: imdbId, imageUrl: imageUrl).save()
            }else{
                log.error "Can not find actor by imdbId ${imdbId}"
            }
        }catch (e){
            log.error "An exception ocurr trying to find an Actor: ${e.stackTrace}"
        }
    }
}
