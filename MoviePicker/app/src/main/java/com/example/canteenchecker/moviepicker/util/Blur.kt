package com.example.canteenchecker.moviepicker.util

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.preference.PreferenceManager
import jp.wasabeef.blurry.Blurry

class Blur {
    companion object {
        public fun needsBlur(adult: Boolean, context: Context): Boolean {
            return adult &&
                    !(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("showAdult", false))
        }

        public fun blurBitmapInto(
            image: Bitmap, view: ImageView
        ) {
            Blurry.with(view.context)
                .radius(10)
                .sampling(8)
                .from(image)
                .into(view)
        }
    }
}