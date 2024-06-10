package fr.epf_min1.tp_android

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide

class CountryDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_detail)

        val countryName = findViewById<TextView>(R.id.country_name)
        val capital = findViewById<TextView>(R.id.capital)
        val languages = findViewById<TextView>(R.id.languages)
        val flag = findViewById<ImageView>(R.id.flag)

        val name = intent.getStringExtra("name")
        val capitalText = intent.getStringExtra("capital")
        val languagesText = intent.getStringExtra("languages")
        val flagUrl = intent.getStringExtra("flagUrl")

        countryName.text = name
        capital.text = "Capital: $capitalText"
        languages.text = "Languages: $languagesText"

        Glide.with(this)
            .load(flagUrl)
            .into(flag)
    }
}
