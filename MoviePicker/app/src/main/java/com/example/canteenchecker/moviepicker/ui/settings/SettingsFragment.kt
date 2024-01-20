package com.example.canteenchecker.moviepicker.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.canteenchecker.moviepicker.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}