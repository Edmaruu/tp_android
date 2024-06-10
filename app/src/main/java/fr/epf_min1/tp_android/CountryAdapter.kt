package fr.epf_min1.tp_android

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CountryAdapter(
    private val countries: List<PaysSimplifie>,
    context: Context
) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val countryName: TextView = itemView.findViewById(R.id.country_name)
        val capital: TextView = itemView.findViewById(R.id.capital)
        val starFavorite: CheckBox = itemView.findViewById(R.id.star_favorite)
        val countryFlag: ImageView = itemView.findViewById(R.id.country_flag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.country_item, parent, false)
        return CountryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val country = countries[position]
        holder.countryName.text = country.translations.fra.common
        holder.capital.text = country.capital


        val code = country.countryCode ?: "unknown" // Assurez-vous que code n'est jamais nul
        val flagUrl = "https://flagcdn.com/256x192/$code.png"
        val isFavorite = sharedPreferences.getBoolean(country.translations.fra.common, false)
        holder.starFavorite.isChecked = isFavorite

        Glide.with(holder.itemView.context)
            .load(flagUrl)
            .into(holder.countryFlag)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, CountryDetailActivity::class.java).apply {
                putExtra("name", country.translations.fra.common)
                putExtra("capital", country.capital)
                putExtra("languages", country.languages?.values?.joinToString(", "))
                putExtra("flagUrl", "https://flagcdn.com/256x192/$code.png")
            }
            context.startActivity(intent)
        }
        holder.starFavorite.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Save the country to favorites
                editor.putBoolean(country.translations.fra.common, true)
                editor.apply()
            } else {
                // Remove the country from favorites
                editor.remove(country.translations.fra.common)
                editor.apply()
            }
        }
    }

    override fun getItemCount() = countries.size
}
