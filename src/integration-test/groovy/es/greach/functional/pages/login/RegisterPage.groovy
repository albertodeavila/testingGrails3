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
package es.greach.functional.pages.login

import es.greach.functional.pages.utils.MessagesModule
import geb.Page
import grails.util.Holders

class RegisterPage extends Page {
    static url = "/user/create"

    static at = {
        $('h3.text-center').text() == Holders.applicationContext.getBean("messageSource").getMessage("user.sign.up", null, null)
    }

    static content = {
        messages(required: false) { module MessagesModule }
        username { $("input", name:"username") }
        email { $("input", name:"email") }
        password { $("input", type:"password", name:"password") }
        passwordConfirm { $("input", name:"passwordConfirm") }
        registerButton{ $("button", type:"submit") }
    }
}
