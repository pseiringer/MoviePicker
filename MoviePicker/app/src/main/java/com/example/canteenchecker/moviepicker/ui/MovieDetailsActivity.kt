package com.example.canteenchecker.moviepicker.ui

import android.content.Context
import android.content.Intent
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.canteenchecker.moviepicker.R
import com.example.canteenchecker.moviepicker.api.TMDBApiFactory
import com.example.canteenchecker.moviepicker.databinding.ActivityMovieDetailsBinding
import com.example.canteenchecker.moviepicker.util.Blur
import com.example.canteenchecker.moviepicker.util.Watchlist
import com.example.canteenchecker.moviepicker.util.WatchlistManager
import kotlinx.coroutines.launch
import java.time.LocalDate


class MovieDetailsActivity : AppCompatActivity() {

    companion object {

        private val MOVIE_ID = "movieId"

        fun intent(context: Context, movieId: Int): Intent {
            return Intent(context, MovieDetailsActivity::class.java)
                .apply {
                    putExtra(MOVIE_ID, movieId)
                }
        }
    }

    private lateinit var binding: ActivityMovieDetailsBinding

    private var movieId: Int = -1
    private var watchlist: Watchlist = Watchlist(ArrayList());

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMovieDetailsBinding.inflate(layoutInflater);
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.txvDetailsTitle.text = intent.getIntExtra(MOVIE_ID, -1).toString()

        movieId = intent.getIntExtra(MOVIE_ID, -1)
        loadWatchlist()
        loadMovieData()
        initToggleButton()
    }

    private fun loadWatchlist() {
        watchlist = WatchlistManager.getWatchList(this@MovieDetailsActivity)
    }

    private fun loadMovieData() = lifecycleScope.launch {
        if (movieId != -1){
            var tmdb = TMDBApiFactory.createTMDBApi();
            // load movie details
            var movie = tmdb.getMovie(movieId).getOrNull();
            if (movie != null){
                binding.txvDetailsTitle.text = movie.title
                binding.txvDetailsYear.text = LocalDate.parse(movie.release_date).year.toString()
                binding.txvDetailsRuntime.text = getString(R.string.placeholder_text_runtime, movie.runtime)
                binding.txvDetailsSummary.text = movie.overview

                // load credits
                var credits = tmdb.getCredits(movieId).getOrNull();
                binding.txvDetailsDirector.text =
                    credits?.crew
                        ?.filter { it.job == "Director" }
                        ?.map { it.name }
                        ?.joinToString(", ")
                        ?: ""

                // load config (for getting the image)
                var config = tmdb.getConfigurationDetails().getOrNull();
                if (config != null){
                    // load poster
                    var img = tmdb.getImage(config.images.secure_base_url, "w500", movie.poster_path).getOrNull();
                    if (img != null){
                        binding.imgDetailsImage.setImageBitmap(img)
                        if (Blur.needsBlur(movie.adult, this@MovieDetailsActivity)){
                            Blur.blurBitmapInto(img, binding.imgDetailsImage)
                        }
                        var width = 0
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            val windowMetrics: WindowMetrics =
                                windowManager.currentWindowMetrics
                            val insets: Insets = windowMetrics.windowInsets
                                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                            width = windowMetrics.bounds.width() - insets.left - insets.right
                        } else {
                            val displayMetrics = DisplayMetrics()
                            @Suppress("DEPRECATION")
                            windowManager.defaultDisplay.getMetrics(displayMetrics)
                            width = displayMetrics.widthPixels
                        }
                        width = (width * 0.25).toInt()
                        binding.imgDetailsImage.layoutParams.width = width
                        binding.imgDetailsImage.layoutParams.height = (width / 2) * 3
                    }
                }
            }
        }
    }

    private fun initToggleButton() {
        updateToggleButtonText()
        binding.btnToggleWatchlist.setOnClickListener {
            if (watchlist.entries.contains(movieId)){
                watchlist.entries.remove(movieId)
            }
            else {
                watchlist.entries.add(movieId)
            }
            WatchlistManager.setWatchList(this@MovieDetailsActivity, watchlist)
            updateToggleButtonText()
        }
    }

    private fun updateToggleButtonText() {
        if (watchlist.entries.contains(movieId)) {
            binding.btnToggleWatchlist.text = getString(R.string.text_remove_from_watchlist)
        } else {
            binding.btnToggleWatchlist.text = getString(R.string.text_add_to_watchlist)
        }
    }
}