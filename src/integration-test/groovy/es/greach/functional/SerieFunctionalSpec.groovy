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
package es.greach.functional

import es.greach.Serie
import es.greach.functional.pages.serie.CreateSeriePage
import es.greach.functional.pages.serie.EditSeriePage
import es.greach.functional.pages.serie.IndexSeriePage
import es.greach.functional.pages.serie.ShowSeriePage
import es.greach.functional.utils.FunctionalTest
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Stepwise
import spock.lang.Unroll

@Integration
@Rollback
@Stepwise
class SerieFunctionalSpec extends FunctionalTest {

    void 'Creates a serie'() {
        given: 'login as an admin'
        loginAs('admin', '1234')

        when: 'go to create serie page'
        to CreateSeriePage

        and: 'fill serie form'
        String name = 'Westworld'
        String channel = 'HBO'
        String date = new Date().format('MM/dd/yyyy')
        String actorName = 'Antonio'

        form.serieName = name
        form.serieChannel = channel
        form.serieActors = actorName
        waitFor {
            !form.spinner.isDisplayed() && form.actorsFound
        }
        form.actorsFound.first().click()
        form.serieReleaseDate = date
        form.serieCover = new File('src/integration-test/groovy/es/greach/functional/utils/media/westworld.jpg').absolutePath

        and: 'submit the serie form'
        js.exec("\$('#serieForm').submit()")

        then: 'the index serie page is shown'
        at IndexSeriePage

        and: 'a success message appear'
        messages.successMessage == messageSource.getMessage('serie.save.ok', [name].toArray(), null).trim()

        and: 'there is a serie with the name'
        serieWithName(name)

        when: 'click in the serie link'
        js.exec("\$(\"[name='${name}']\")[0].click()")

        then: 'the show page is shown'
        at ShowSeriePage

        and: 'check that the saved values are shown'
        serieName == name
        serieChannel == channel
        serieReleaseDate == date
        cover
        actors.find{ it.contains(actorName) }
    }

    @Unroll
    void "try to save a serie without required params"(){
        when: 'go to create serie page'
        to CreateSeriePage

        and: 'fill serie form'
        form.serieName = name
        form.serieChannel = channel
        form.serieReleaseDate = releaseDate

        and: 'click on the serie submit button'
        form.serieSubmitButton.click()

        then: 'we are in the create serie page'
        at CreateSeriePage
        fieldErrors.each { String fieldError ->
            assert form.validationErrorForField(fieldError) == 'This field is required.'
        }

        where:
        name | channel | releaseDate                     || fieldErrors
        ''   | ''      | ''                              || ['name', 'channel', 'releaseDate']
        'a'  | ''      | ''                              || ['channel', 'releaseDate']
        'a'  | 'b'     | ''                              || ['releaseDate']
        ''   | 'b'     | new Date().format('MM/dd/yyyy') || ['name']
        ''   | 'b'     | ''                              || ['name', 'releaseDate']
        'a'  | ''      | new Date().format('MM/dd/yyyy') || ['channel']
        ''   | ''      | new Date().format('MM/dd/yyyy') || ['name', 'channel']
    }

    void "try to show a serie that doesn't exists"(){
        when: 'go to show page that doesn\'t exists'
        go("/serie/show?serieId=-1")

        then: 'the index serie is shown'
        at IndexSeriePage

        and: 'an error message appears'
        messages.errorMessage == messageSource.getMessage('serie.show.notExists', null, null).trim()
    }

    void "try to show a serie without sending serieId"(){
        when:  'go to show serie page'
        go("/serie/show?serieId=")

        then: 'the index serie is shown'
        at IndexSeriePage

        and: 'an error message appear'
        messages.errorMessage == messageSource.getMessage('serie.show.notFound', null, null)
    }

    void "show a serie"(){
        given: 'a serie is created'
        Serie serie
        Serie.withNewSession { session ->
            serie = Serie.build(name: UUID.randomUUID().toString())
        }

        when: 'go to show page'
        go("/serie/show?serieId=${serie.id}")

        then: 'the show page is present'
        at ShowSeriePage

        and: 'check that the values saved are shown'
        serieName == serie.name
        serieChannel == serie.channel
        serieReleaseDate == serie.releaseDate.format('MM/dd/yyyy')
    }

    void "delete a serie"(){
        given: 'a serie is created'
        Serie serie
        Serie.withNewSession { session->
            serie = Serie.build(name: UUID.randomUUID().toString())
        }

        when: 'go to show serie page'
        go("/serie/show?serieId=${serie.id}")

        then: 'we are at show serie page'
        at ShowSeriePage

        when: 'click on the delete button'
        deleteButton.click()

        then: 'the index serie page is shown'
        at IndexSeriePage

        and: 'a success message appear'
        messages.successMessage == messageSource.getMessage('serie.delete.ok', [serie.name].toArray(), null)
    }

    void "update a serie"(){
        given: 'a serie is created'
        Serie serie
        Serie.withNewSession { session->
            serie = Serie.build(name: UUID.randomUUID().toString())
        }

        when: 'go to show serie page'
        go("/serie/show?serieId=${serie.id}")

        then: 'we are at show serie page'
        at ShowSeriePage

        when: 'click on the update button'
        editButton.click()

        then: 'the edit serie page is shown'
        at EditSeriePage

        when: 'change the form values'
        String name = 'Westworld 2'
        String channel = 'HBO'
        String date = new Date().format('MM/dd/yyyy')

        form.serieName = name
        form.serieChannel = channel
        form.serieReleaseDate = date

        and: 'click on save button'
        form.serieSubmitButton.click()

        then: 'the index serie page is shown'
        at IndexSeriePage

        and: 'a success message appears'
        messages.successMessage == messageSource.getMessage('serie.save.ok', [name].toArray(), null).trim()

        and: 'there is a serie with the name'
        serieWithName(name)

        when: 'click in the serie link'
        js.exec("\$(\"[name='${name}']\")[0].click()")

        then: 'the show page is shown'
        at ShowSeriePage

        and: 'check that the values saved are shown'
        serieName == name
        serieChannel == channel
        serieReleaseDate == date
    }
}
