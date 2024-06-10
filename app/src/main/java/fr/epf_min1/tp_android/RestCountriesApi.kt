package fr.epf_min1.tp_android

import retrofit2.Call
import retrofit2.http.GET

interface RestCountriesApi {
    @GET("v3.1/all")
    fun getAllCountries(): Call<List<CountryResponse>>
}

interface CountryCodeApi {
    @GET("fr/codes.json") // Remplacez par le bon chemin de votre API
    fun getCountryCodes(): Call<Map<String, String>> // Supposons que l'API renvoie un map de code à nom du pays
}

data class CountryCodeResponse(
    val code: String,
    val name: String
)


data class CountryResponse(
    val name: CountryName,
    val capital: List<String>?,
    val languages: Map<String, String>,
    val translations: Translations
)

data class PaysSimplifie(
    val name: CountryName,
    val capital: String?,
    val languages: Map<String, String>?,
    val translations: Translations,
    val countryCode: String?
)

data class CountryName(
    val common: String,
    val official: String
)

data class Translations(
    val fra: Translation
)

data class Translation(
    val common: String,
    val official: String
)
