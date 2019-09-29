This repo is backend part of CRM-system for administrate patients of dentist clinic.

Some info about this app:
- backend developed on kotlin
- framework, selected for backend development is JetBrains/KtorIO really small and fluent framework for reast api
- for data layer used JetBrains/Exposed, which is small and cool orm 
- for DI was used JetBrains/Koin
- backend app hosted now on heroku service (https://ptsadmapp.herokuapp.com). For now it's impossible to generate swagger ui, because Swagger is not fully supports for KtorIO now (not supported tokens completely)
- to increase server security, was used JWT token
