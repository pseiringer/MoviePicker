package com.example.canteenchecker.moviepicker.ui.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.canteenchecker.moviepicker.ui.AddMovieActivity
import com.example.canteenchecker.moviepicker.api.TMDBApiFactory
import com.example.canteenchecker.moviepicker.databinding.FragmentWatchlistBinding
import com.example.canteenchecker.moviepicker.ui.moviecard.MovieCard
import com.example.canteenchecker.moviepicker.ui.moviecard.MovieCardAdapter
import com.example.canteenchecker.moviepicker.util.Watchlist
import com.example.canteenchecker.moviepicker.util.WatchlistManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class WatchlistFragment : Fragment() {

    private var _binding: FragmentWatchlistBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private var watchlist: Watchlist = Watchlist(ArrayList());
    private val cards: ArrayList<MovieCard> = ArrayList();
//    private val movieCards: ArrayList<Movie> = ArrayList();

    private val movieCardAdapter = MovieCardAdapter()

    private val numColumns = 4;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val layoutMan = object : GridLayoutManager(context, numColumns) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams) : Boolean {
                lp.width = (width / spanCount)
                lp.height = (lp.width / 2) * 3
                return true
            }
        }
        binding.rcvMovieCards.layoutManager = layoutMan // GridLayoutManager(context, numColumns)
        binding.rcvMovieCards.adapter = movieCardAdapter;

        binding.fabAddMovie.setOnClickListener {
            startActivity(
                AddMovieActivity.intent(requireContext())
            )
        }

        return root
    }


    override fun onResume() {
        super.onResume()

        loadWatchlist()
        loadMovieData()
    }

    private fun loadWatchlist() {
//        watchlist.entries.clear();
//        watchlist.entries.addAll(listOf(274, 792307, 62, 694, 345, 569094, 389, 278, 406, 769, 129, 7345, 531428));
        watchlist = WatchlistManager.getWatchList(requireContext())
    }
    private fun loadMovieData() = lifecycleScope.launch {
        var tmdb = TMDBApiFactory.createTMDBApi();
        cards.clear();
        cards.ensureCapacity(watchlist.entries.size);
        val loadCardJobs = ArrayList<Job>()

        // get image base url
        var config = tmdb.getConfigurationDetails().getOrNull();
        if (config != null){
            // start jobs for loading all movies
            for (idx in watchlist.entries.indices) {
                val id = watchlist.entries[idx]
                loadCardJobs.add(
                    launch {
                        // load movie details
                        var movie = tmdb.getMovie(id).getOrNull();
                        if (movie != null){
                            // load poster
                            var img = tmdb.getImage(config.images.secure_base_url, "w500", movie.poster_path).getOrNull();
                            if (img != null){
                                cards.add(MovieCard(idx, movie.id, movie.title, movie.release_date, movie.adult, img));
                                cards.sortBy { it.index }
                                movieCardAdapter.displayMovies(cards);
                            }
                        }
                    }
                )
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}