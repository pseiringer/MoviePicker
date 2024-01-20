package com.example.canteenchecker.moviepicker.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.canteenchecker.moviepicker.R
import com.example.canteenchecker.moviepicker.api.MoviePickerApiFactory
import com.example.canteenchecker.moviepicker.api.TMDBApiFactory
import com.example.canteenchecker.moviepicker.core.moviepicker.Room
import com.example.canteenchecker.moviepicker.core.moviepicker.Vote
import com.example.canteenchecker.moviepicker.core.moviepicker.VoteList
import com.example.canteenchecker.moviepicker.core.moviepicker.VoteSum
import com.example.canteenchecker.moviepicker.databinding.ActivityRoomBinding
import com.example.canteenchecker.moviepicker.ui.votecard.VoteCard
import com.example.canteenchecker.moviepicker.ui.votecard.VoteCardAdapter
import com.example.canteenchecker.moviepicker.util.WatchlistManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

class RoomActivity : AppCompatActivity() {

    companion object {

        private val ROOM_CODE = "roomCode"

        fun intent(context: Context, roomCode: String): Intent {
            return Intent(context, RoomActivity::class.java)
                .apply {
                    putExtra(ROOM_CODE, roomCode)
                }
        }
    }

    private lateinit var binding: ActivityRoomBinding
    private val voteCardAdapter = VoteCardAdapter(::addVoteClicked, ::cardClicked)

    private var roomCode: String? = null

    private val cards: ArrayList<VoteCard> = ArrayList();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoomBinding.inflate(layoutInflater);
        setContentView(binding.root)

        roomCode = intent.getStringExtra(ROOM_CODE)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.rcvVotes.adapter = voteCardAdapter

        binding.txvRoomCode.text =
            getString(
                R.string.placeholder_text_room_code,
                roomCode ?: getString(R.string.text_invalid)
            )

        binding.srlVotes.setOnRefreshListener {
            loadRoomData()
        }

        binding.btnVoteForWatchlist.setOnClickListener {
            lifecycleScope.launch {
                var moviePickerApi = MoviePickerApiFactory.createMoviePickerApi()

                moviePickerApi.voteList(
                    roomCode ?: "",
                    VoteList(WatchlistManager.getWatchList(this@RoomActivity).entries))
                    .onSuccess {
                        loadRoomData()
                    }
                    .onFailure {
                        Toast.makeText(
                            this@RoomActivity,
                            getString(R.string.text_vote_failed),
                            Toast.LENGTH_LONG).show()
                    }
            }
        }
        binding.btnLockRoom.setOnClickListener {
            lifecycleScope.launch {
                var moviePickerApi = MoviePickerApiFactory.createMoviePickerApi()

                moviePickerApi.closeRoom(roomCode ?: "")
                    .onSuccess {
                        loadRoomData()
                    }
                    .onFailure {
                        Toast.makeText(
                            this@RoomActivity,
                            getString(R.string.text_lock_failed),
                            Toast.LENGTH_LONG).show()
                    }
            }
        }

        if (roomCode != null)
            loadRoomData()
        else
            binding.btnVoteForWatchlist.isEnabled = false
    }


    private fun loadRoomData() = lifecycleScope.launch {
        binding.srlVotes.isRefreshing = true
        cards.clear();
        var moviePickerApi = MoviePickerApiFactory.createMoviePickerApi()
        var room = moviePickerApi.getRoom(roomCode ?: "").getOrNull()
        if (room != null) {
            setButtonsEnabled(!room.isClosed);

            var tmdb = TMDBApiFactory.createTMDBApi();
            cards.ensureCapacity(room.votes.size);
            val loadCardJobs = ArrayList<Job>()

            // get image base url
            var config = tmdb.getConfigurationDetails().getOrNull();
            if (config != null) {
                // start jobs for loading all movies
                for (idx in room.votes.indices) {
                    val vote = room.votes[idx]
                    loadCardJobs.add(
                        launch {
                            // load movie details
                            var movie = tmdb.getMovie(vote.movieId).getOrNull();
                            if (movie != null) {
                                // load poster
                                var img = tmdb.getImage(
                                    config.images.secure_base_url,
                                    "w500",
                                    movie.poster_path
                                ).getOrNull();
                                if (img != null) {
                                    cards.add(
                                        VoteCard(
                                            vote.movieId,
                                            movie.title,
                                            movie.release_date,
                                            movie.runtime,
                                            movie.adult,
                                            img,
                                            vote.numVotes,
                                            room.isClosed
                                        )
                                    );
                                    cards.sortByDescending { it.numVotes }
                                    voteCardAdapter.displayVotes(cards);
                                }
                            }
                        }
                    )
                }
            }
            else {
                setButtonsEnabled(false);
            }

            loadCardJobs.forEach {
                it.join()
            }
            voteCardAdapter.displayVotes(cards);
            binding.srlVotes.isRefreshing = false
        }
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        binding.btnLockRoom.isEnabled = enabled
        binding.btnVoteForWatchlist.isEnabled = enabled
    }

    private fun cardClicked(movieId: Int) {
        startActivity(
            MovieDetailsActivity.intent(this@RoomActivity, movieId)
        )
    }

    private fun addVoteClicked(movieId: Int) {
        lifecycleScope.launch {
            var moviePickerApi = MoviePickerApiFactory.createMoviePickerApi()

            moviePickerApi.vote(
                roomCode ?: "",
                Vote(movieId))
                .onSuccess {
                    loadRoomData()
                }
                .onFailure {
                    Toast.makeText(
                        this@RoomActivity,
                        getString(R.string.text_vote_failed),
                        Toast.LENGTH_LONG).show()
                }
        }
    }
}