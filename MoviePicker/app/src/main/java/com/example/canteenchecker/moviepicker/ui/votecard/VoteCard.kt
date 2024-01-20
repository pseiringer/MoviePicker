package com.example.canteenchecker.moviepicker.ui.votecard

import android.graphics.Bitmap
import android.graphics.Insets
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowMetrics
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchUIUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.canteenchecker.moviepicker.R
import com.example.canteenchecker.moviepicker.util.Blur
import java.time.LocalDate


data class VoteCard(
    val id: Int,
    val title: String,
    val releaseDate: String,
    val runtime: Int,
    val adult: Boolean,
    val image: Bitmap,
    val numVotes: Int
)


class VoteCardAdapter(val addVoteClicked: (id: Int) -> Unit, val cardClicked: (id: Int) -> Unit): RecyclerView.Adapter<VoteCardAdapterViewHolder>(){

    private var votes = emptyList<VoteCard>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VoteCardAdapterViewHolder {
        return VoteCardAdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_vote_card, parent, false)
        )
    }

    override fun getItemCount(): Int = votes.size

    override fun onBindViewHolder(holder: VoteCardAdapterViewHolder, position: Int) = holder.run {
        val vote = votes[position]
        itemView.setOnClickListener{
            cardClicked(vote.id)
        }
        if (vote.image != null){
            imgPoster.setImageBitmap(vote.image)
            if (Blur.needsBlur(vote.adult, itemView.context)){
                Blur.blurBitmapInto(vote.image, imgPoster)
            }

            itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            val width = (itemView.measuredWidth * 0.3).toInt()
            imgPoster.layoutParams.width = width
            imgPoster.layoutParams.height = (width / 2) * 3
        }
        txvTitle.text = vote.title
        txvYear.text = LocalDate.parse(vote.releaseDate).year.toString()
        txvRuntime.text = itemView.context.getString(R.string.placeholder_text_runtime, vote.runtime)
        txvVotes.text = vote.numVotes.toString()
        btnAddVote.setOnClickListener {
            addVoteClicked(vote.id)
        }
    }



    fun displayVotes(movies: List<VoteCard>){
        this.votes = movies;
        notifyDataSetChanged()
    }

}

class VoteCardAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val imgPoster: ImageView = itemView.findViewById(R.id.img_votePoster)
    val txvTitle: TextView = itemView.findViewById(R.id.txv_voteTitle)
    val txvYear: TextView = itemView.findViewById(R.id.txv_voteYear)
    val txvRuntime: TextView = itemView.findViewById(R.id.txv_voteRuntime)
    val txvVotes: TextView = itemView.findViewById(R.id.txv_voteVotes)
    val btnAddVote: TextView = itemView.findViewById(R.id.btn_voteAddVote)
}