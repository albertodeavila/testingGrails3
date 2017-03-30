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

import geb.Page

class ShowSeriePage extends Page {

    static at = {
        $("h1.marginTop10").text() && $("[for='channel']").isDisplayed()
    }

    static content = {
        valueForLabel{ label-> $("[for='${label}'] + div p").text().trim() }

        serieName { $('h1').text() }
        serieChannel{ valueForLabel('channel') }
        serieReleaseDate{ valueForLabel('releaseDate') }
        actors (required: false){ $('h3')*.text()*.trim() }


        cover { $("#cover") }

        episodesTable (required: false) { $("#datatable-episodesTable") }
        episodes (required: false) { episodesTable.find('tbody > tr') }

        episodesId (required: false) { episodes.find('td:nth-child(1)')?.text() }
        episodesTitle (required: false) { episodes.find('td:nth-child(2)')?.text() }
        episodesSeason (required: false) { episodes.find('td:nth-child(3)')?.text() }
        episodesNumber (required: false) { episodes.find('td:nth-child(4)')?.text() }

        editButton (required: false) { $("#editSerieButtonsDiv a:nth-child(1)") }
        addEpisodeButton (required: false) { $("#editSerieButtonsDiv a:nth-child(2)") }
        deleteButton (required: false) { $("#editSerieButtonsDiv a:nth-child(3)") }

    }
}
