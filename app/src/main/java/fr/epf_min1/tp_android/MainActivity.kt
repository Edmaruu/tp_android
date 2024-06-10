package fr.epf_min1.tp_android
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private lateinit var restCountriesApi: RestCountriesApi
    private lateinit var countryCodeApi: CountryCodeApi
    private lateinit var countryAdapter: CountryAdapter
    private lateinit var recyclerView: RecyclerView
    private var allCountries: List<PaysSimplifie> = listOf()
    private var countryCodes: Map<String, String> = mapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_app)

        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://restcountries.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()

        restCountriesApi = retrofit.create(RestCountriesApi::class.java)

        val retrofitCodes = Retrofit.Builder()
            .baseUrl("https://flagcdn.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()

        countryCodeApi = retrofitCodes.create(CountryCodeApi::class.java)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        getAllCountryData()

        val searchView = findViewById<SearchView>(R.id.home_search)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    // Filter countries based on search query
                    val filteredCountries = allCountries.filter { country ->
                        country.translations.fra.common.contains(newText, ignoreCase = true) ||
                                country.capital?.contains(newText, ignoreCase = true) ?: false
                    }

                    displayCountries(filteredCountries)
                } else {
                    // Display all countries if search query is empty
                    displayCountries(allCountries)
                }
                return true
            }
        })
    }


    private fun getAllCountryData() {
        // Fetch country codes first
        countryCodeApi.getCountryCodes().enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    countryCodes = response.body() ?: mapOf()
                    // Now fetch country data
                    getAllCountries()
                } else {
                    // Handle error
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun getAllCountries() {
        restCountriesApi.getAllCountries().enqueue(object : Callback<List<CountryResponse>> {

            override fun onResponse(call: Call<List<CountryResponse>>, response: Response<List<CountryResponse>>) {
                if (response.isSuccessful) {
                    Log.d("RestCountriesApi", "Response received: ${response.body()}")
                    response.body()?.let { countries ->
                        val simplifiedCountries = countries.map { country ->
                            val countryCode = countryCodes.entries.find { it.value == country.translations.fra.common }?.key

                            PaysSimplifie(
                                name = country.name,
                                capital = country.capital?.firstOrNull(),
                                languages = country.languages ?: emptyMap(),
                                translations = country.translations,
                                countryCode = countryCode?.lowercase()                         )
                        }
                        allCountries = simplifiedCountries
                        displayCountries(simplifiedCountries)
                    }
                } else {
                    Log.e("RestCountriesApi", "Response not successful: ${response.code()}")
                    // Handle error
                }
            }

            override fun onFailure(call: Call<List<CountryResponse>>, t: Throwable) {
                Log.e("RestCountriesApi", "Failed to fetch countries", t)
                getAllCountries()  // Retry fetching countries
            }
        })
    }

    private fun displayCountries(countries: List<PaysSimplifie>) {
        countryAdapter = CountryAdapter(countries, this)
        recyclerView.adapter = countryAdapter
    }
}


