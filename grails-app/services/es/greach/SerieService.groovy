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
import grails.transaction.Transactional
import org.springframework.web.multipart.MultipartFile

import java.nio.file.FileAlreadyExistsException

@Transactional
class SerieService {

    GrailsApplication grailsApplication

    /**
     * Save or update a Serie with the given data
     * @param name the serie's name
     * @param channel the channel owner of the serie
     * @param releaseDate the release date
     * @param cover the serie's cover
     * @param actors a lis tof actors
     * @return the serie if it's saved or null otherwise
     */
    Serie save(String name, String channel, Date releaseDate, MultipartFile cover, List<Actor> actors, Serie serie = null){
        if(!serie) serie = new Serie()
        serie.name = name
        serie.channel = channel
        serie.releaseDate = releaseDate
        if(serie.actors){
            List<Actor> actorsToRemove = serie.actors.toList()
            actorsToRemove.each{ Actor actor ->
                serie.removeFromActors(actor)
            }
        }
        actors.each{ Actor actor ->
            serie.addToActors(actor)
        }
        if(serie.id && cover){
            deleteCoverFile(serie)
        }
        if(serie.save()){
            if(cover) serie = saveCoverFile(cover, serie)
            serie
        }
    }

    /**
     * Save the serie's cover
     * @param cover the cover
     * @param serie the serie to save the cover path
     * @return the serie saved or null otherwise
     */
    Serie saveCoverFile(MultipartFile cover, Serie serie){
        if(serie && cover) {
            try {
                String separator = File.separator
                File seriesFolder = new File("${grailsApplication.config.path.serie.cover}$separator${serie.id}")
                if (!seriesFolder.exists()) seriesFolder.mkdirs()
                File finalCover = new File("${grailsApplication.config.path.serie.cover}$separator${serie.id}$separator${cover.originalFilename}")
                cover.transferTo(finalCover)
                serie.pathToCover = finalCover.absolutePath
                serie.save()
            }catch (SecurityException e){
                log.error "A security exception ocurr: ${e.stackTrace}"
                serie.delete()
            } catch (FileAlreadyExistsException e){
                log.error "An FileAlreadyExist exception ocurr ${e.stackTrace}"
                serie.delete()
            } catch (IOException e){
                log.error "An IO exception ocurr: ${e.stackTrace}"
                serie.delete()
            } catch (Exception e) {
                log.error "An exception ocurr: ${e.stackTrace}"
                serie.delete()
            }
        }
    }

    /**
     * Delete the cover serie file
     * @param serie
     */
    void deleteCoverFile(Serie serie){
        try{
            if(serie.pathToCover) {
                serie?.cover?.delete()
                serie.pathToCover = ''
            }
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
     * Delete the serie
     * @param serie the entity to delete
     * @return null if it's successfully deleted or the serie otherwise
     */
    Serie delete(Serie serie){
        serie?.delete()
        Serie.get(serie?.id)
    }
}
