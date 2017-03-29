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
import groovy.transform.ToString

@ToString(includes='username', includeNames=true, includePackage=false)
class Serie {

    GrailsApplication grailsApplication

    String name
    String channel
    Date releaseDate
    String pathToCover

    static hasMany = [actors: Actor, episodes: Episode]

    static constraints = {
        name unique: true
        pathToCover nullable: true
    }

    static transients = ['cover', 'grailsApplication']

    File getCover(){
        File cover = this.pathToCover ? new File(this.pathToCover) : null
        cover?.exists() ? cover : new File('grails-app/assets/images/default-thumbnail.jpg')
    }
}
