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
package es.greach.functional.pages.serie

import es.greach.functional.pages.utils.MessagesModule
import geb.Page
import grails.util.Holders

class IndexSeriePage extends Page {
    static url = "/"

    static at = {
        $("h1").text() == Holders.applicationContext.getBean("messageSource").getMessage("serie.index.label", null, null)
    }

    static content = {
        messages (required: false) { module MessagesModule }
        createSerieButton (required: false) { $(".btn-success") }
        seriesAvailable (required: false) { $(".thumbnail") }
        serieWithName (required: false) { name -> seriesAvailable.find(text: name) }
        serieLinkWithName (required: false) { name -> $("[name='${name}']") }
    }
}
