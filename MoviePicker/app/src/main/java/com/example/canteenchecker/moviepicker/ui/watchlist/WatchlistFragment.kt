package com.example.canteenchecker.moviepicker.ui.watchlist

import android.app.ActionBar.LayoutParams
import android.graphics.Bitmap
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.canteenchecker.moviepicker.R
import com.example.canteenchecker.moviepicker.api.TMDBApiFactory
import com.example.canteenchecker.moviepicker.core.tmdb.Movie
import com.example.canteenchecker.moviepicker.databinding.FragmentWatchlistBinding
import kotlinx.coroutines.launch

class WatchlistFragment : Fragment() {

    private var _binding: FragmentWatchlistBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val watchlist: ArrayList<Int> = ArrayList();
    private val movieCards: ArrayList<Movie> = ArrayList();

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        binding.txvTemp.text = "tmp";

        // load watchlist
        watchlist.clear();
        watchlist.addAll(listOf(11, 274, 557, 792307, 760104, 62, 694, 345, ));

        loadData()

        return root
    }

    private fun loadData() = lifecycleScope.launch {
        binding.grdMovieCards.removeAllViews();

        var tmdb = TMDBApiFactory.createTMDBApi();
        var cards = ArrayList<MovieCard>();

        var config = tmdb.getConfigurationDetails().getOrNull();
        if (config != null){
            for (id in watchlist) {
                var movie = tmdb.getMovie(id).getOrNull();
                if (movie != null){
                    var img = tmdb.getImage(config.images.secure_base_url, "w500", movie.poster_path).getOrNull();
                    if (img != null){
                        cards.add(MovieCard(movie.id, movie.title, img));
                    }
                }
            }
        }

        for (card in cards) {
            var cardView = activity
                ?.layoutInflater
                ?.inflate(R.layout.fragment_movie_card, binding.grdMovieCards, false)
//            cardView?.findViewById<TextView>(R.id.txv_movieTitle)?.text = card.title;
            cardView?.findViewById<ImageView>(R.id.img_moviePoster)?.setImageBitmap(card.image);
            cardView?.layoutParams?.width = binding.grdMovieCards.width / 4;
            cardView?.layoutParams?.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130f, resources.displayMetrics).toInt()
            binding.grdMovieCards.addView(cardView);
        }

//        TMDBApiFactory.createTMDBApi()
//            .getImage("http://image.tmdb.org/t/p/","original","gq5Wi7i4SF3lo4HHkJasDV95xI9.jpg")
//            .onSuccess {
////                binding.txvTemp.text = it.toString();
////                binding.imgTemp.setImageBitmap(it);
//            }
//            .onFailure {
////                binding.txvTemp.text = "failed";
//            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class MovieCard(
        val id: Int,
        val title: String,
        val image: Bitmap
    )

}