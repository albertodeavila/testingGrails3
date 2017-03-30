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
package es.greach.functional.pages.episode

import geb.Page

class ShowEpisodePage extends Page{

    static at = {
        $("h1.marginTop10").text() && $("[for='title']").isDisplayed()
    }

    static content = {

        valueForLabel{ label-> $("[for='${label}'] + div p").text().trim() }

        episodeTitle { valueForLabel('title') }
        episodeSeason { valueForLabel('season') }
        episodeNumber { valueForLabel('episodeNumber') }
        episodeSummary (required: false) { valueForLabel('summary') }
        episodeVideo { $('#video') }

        editButton (required: false) { $("#editEpisodeButtonsDiv a:nth-child(1)")}
        deleteButton (required: false) { $("#editEpisodeButtonsDiv a:nth-child(2)")}
    }

}
