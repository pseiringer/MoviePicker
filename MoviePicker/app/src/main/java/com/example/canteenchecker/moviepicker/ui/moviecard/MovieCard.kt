package com.example.canteenchecker.moviepicker.ui.moviecard

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.canteenchecker.moviepicker.ui.MovieDetailsActivity
import com.example.canteenchecker.moviepicker.R
import com.example.canteenchecker.moviepicker.util.Blur


data class MovieCard(
    val index: Int,
    val id: Int,
    val title: String,
    val release: String,
    val adult: Boolean,
    val image: Bitmap
)


class MovieCardAdapter: RecyclerView.Adapter<MovieCardAdapterViewHolder>(){

    private var movies = emptyList<MovieCard>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovieCardAdapterViewHolder {
        return MovieCardAdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_movie_card, parent, false)
        )
    }

    override fun getItemCount(): Int = movies.size


    override fun onBindViewHolder(holder: MovieCardAdapterViewHolder, position: Int) = holder.run {
        val movie = movies[position]
        imgPoster.setImageBitmap(movie.image)
        if (Blur.needsBlur(movie.adult, itemView.context)){
            Blur.blurBitmapInto(movie.image, imgPoster)
        }
        itemView.setOnClickListener{
            itemView.context.run{
                startActivity(
                    MovieDetailsActivity.intent(itemView.context, movie.id)
                )
            }
        }
    }



    fun displayMovies(movies: List<MovieCard>){
        this.movies = movies;
        notifyDataSetChanged()
    }

}

class MovieCardAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val imgPoster: ImageView = itemView.findViewById(R.id.img_moviePoster)
//        val txvTitle: TextView = itemView.findViewById(R.id.txv_movieTitle)
}