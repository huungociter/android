package com.example.scanqrcode.utils

import android.content.Context
import android.widget.ImageButton
import android.widget.ImageView
import com.example.scanqrcode.R
import java.util.Locale

object LanguageUtils {

    private const val PREFS_NAME = "prefs"
    private const val FLAG_KEY = "selected_flag"
    private const val LANGUAGE_KEY = "selected_language"


    fun changeLanguage(context: Context, language: String) {
        val locale = Locale(language)
        val config = context.resources.configuration
        Locale.setDefault(locale)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        saveLanguage(context, language)
        saveFlagState(context, language)

    }

    fun saveFlagState(context: Context, flag: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(FLAG_KEY, flag)
        editor.apply()
    }

    fun saveLanguage(context: Context, language: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(LANGUAGE_KEY, language)
        editor.apply()
    }

    fun loadLanguage(context: Context, buttonFlag: ImageButton) {
        val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

        // Load selected flag and set it
        val flag = sharedPreferences.getString("selected_flag", "english") // Default flag is English
        when (flag) {
            "vi" -> buttonFlag.setImageResource(R.drawable.flag_vietnam)
            "ja" -> buttonFlag.setImageResource(R.drawable.flag_japan)
            "en" -> buttonFlag.setImageResource(R.drawable.flag_uk)
        }

        // Load selected language and set locale
        val language = sharedPreferences.getString("selected_language", "en") // Default language is English
        val locale = Locale(language!!)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

}