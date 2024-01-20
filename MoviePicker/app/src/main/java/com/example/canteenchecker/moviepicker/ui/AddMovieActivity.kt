package com.example.canteenchecker.moviepicker.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.canteenchecker.moviepicker.R
import com.example.canteenchecker.moviepicker.api.TMDBApiFactory
import com.example.canteenchecker.moviepicker.databinding.ActivityAddMovieBinding
import com.example.canteenchecker.moviepicker.ui.moviecard.MovieCard
import com.example.canteenchecker.moviepicker.ui.moviecard.MovieCardAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddMovieActivity : AppCompatActivity() {

    private val numColumns = 4;

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, AddMovieActivity::class.java)
        }
    }

    private lateinit var binding: ActivityAddMovieBinding
    private val movieCardAdapter = MovieCardAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddMovieBinding.inflate(layoutInflater);
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val layoutMan = object : GridLayoutManager(this@AddMovieActivity, numColumns) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams) : Boolean {
                lp.width = (width / spanCount)
                lp.height = (lp.width / 2) * 3
                return true
            }
        }
        binding.rcvSearchedMovies.layoutManager = layoutMan // GridLayoutManager(context, numColumns)
        binding.rcvSearchedMovies.adapter = movieCardAdapter

        binding.srlSearchedMovies.setOnRefreshListener {
            searchMovies()
        }
        binding.btnSearch.setOnClickListener {
            searchMovies()
        }

        searchMovies()
    }

    private fun searchMovies() = lifecycleScope.launch {
        binding.srlSearchedMovies.isRefreshing = true

        val tmbd = TMDBApiFactory.createTMDBApi()
        var result = tmbd
            .searchMovie(binding.edtSearch.text.toString(), PreferenceManager.getDefaultSharedPreferences(this@AddMovieActivity).getBoolean("showAdult", false))
            .getOrElse {
                Toast.makeText(this@AddMovieActivity,
                    getString(R.string.message_search_failed),
                    Toast.LENGTH_SHORT)
                    .show()
                null
            }

        if (result == null){
            movieCardAdapter.displayMovies(emptyList())
        }
        else {
            var cards = ArrayList<MovieCard>(result.results.size);
            val loadCardJobs = ArrayList<Job>()

            // get image base url
            var config = tmbd.getConfigurationDetails().getOrNull();
            if (config != null){
                // start jobs for loading all movies
                for (idx in result.results.indices) {
                    val movie = result.results[idx]
                    loadCardJobs.add(
                        launch {
                            var img = tmbd.getImage(config.images.secure_base_url, "w500", movie.poster_path).getOrNull();
                            if (img != null) {
                                cards.add(MovieCard(idx, movie.id, movie.title, movie.release_date, movie.adult, img));
                                cards.sortBy { it.index }
                                movieCardAdapter.displayMovies(cards);
                            }
                        }
                    )
                }
            }

            // wait until everything has loaded
            for (job in loadCardJobs) job.join()
            movieCardAdapter.displayMovies(cards)
        }

        binding.srlSearchedMovies.isRefreshing = false
    }
}