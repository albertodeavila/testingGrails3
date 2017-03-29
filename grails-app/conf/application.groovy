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


// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'es.greach.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'es.greach.UserRole'
grails.plugin.springsecurity.authority.className = 'es.greach.Role'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	[pattern: '/',               access: ['permitAll']],
	[pattern: '/error',          access: ['permitAll']],
	[pattern: '/index',          access: ['permitAll']],
	[pattern: '/index.gsp',      access: ['permitAll']],
	[pattern: '/shutdown',       access: ['permitAll']],
	[pattern: '/assets/**',      access: ['permitAll']],
	[pattern: '/**/fonts/**',    access: ['permitAll']],
	[pattern: '/**/js/**',       access: ['permitAll']],
	[pattern: '/**/css/**',      access: ['permitAll']],
	[pattern: '/**/images/**',   access: ['permitAll']],
	[pattern: '/**/favicon.ico', access: ['permitAll']],
	[pattern: '/dbconsole/**',   access: ['permitAll']]

]
grails.resources.adhoc.includes = ['/images/**', '/css/**', '/js/**', '/plugins/**', '/fonts/**']
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*', '/fonts/*']

grails.plugin.springsecurity.filterChain.chainMap = [
	[pattern: '/assets/**',      filters: 'none'],
	[pattern: '/**/js/**',       filters: 'none'],
	[pattern: '/**/css/**',      filters: 'none'],
	[pattern: '/**/images/**',   filters: 'none'],
	[pattern: '/**/fonts/**',    filters: 'none'],
	[pattern: '/**/favicon.ico', filters: 'none'],
	[
			pattern: '/api/**',
			filters: 'JOINED_FILTERS,-anonymousAuthenticationFilter,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter'
	],
	[
		pattern: '/**',
		filters: 'JOINED_FILTERS,-restTokenValidationFilter,-restExceptionTranslationFilter'
	]

]
grails.plugin.springsecurity.logout.postOnly = false


path{
	episodes = 'episodes'
	serie{
		cover = '/var/tmp/serie'
		episodes = '/episodes'
	}
}
