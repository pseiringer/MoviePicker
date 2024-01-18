package com.example.canteenchecker.moviepicker.ui.poll

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.canteenchecker.moviepicker.api.MoviePickerApi
import com.example.canteenchecker.moviepicker.api.MoviePickerApiFactory
import com.example.canteenchecker.moviepicker.api.TMDBApiFactory
import com.example.canteenchecker.moviepicker.core.moviepicker.Vote
import com.example.canteenchecker.moviepicker.databinding.FragmentPollBinding
import com.google.gson.Gson
import kotlinx.coroutines.launch

class PollFragment : Fragment() {

    private var _binding: FragmentPollBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPollBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.textOutput.text = "HelloWorld From PollFrag"

        binding.btnGet.setOnClickListener{
            lifecycleScope.launch {
                MoviePickerApiFactory.createMoviePickerApi()
                    .getRoom(binding.edtRoomCode.text.toString())
                    .onSuccess {
                        binding.textOutput.text = Gson().toJson(it)
                    }
                    .onFailure {
                        binding.textOutput.text = "failed"
                    }
            }
        }
        binding.btnCreate.setOnClickListener{
            lifecycleScope.launch {
                MoviePickerApiFactory.createMoviePickerApi()
                    .createRoom()
                    .onSuccess {
                        binding.textOutput.text = Gson().toJson(it)
                    }
                    .onFailure {
                        binding.textOutput.text = "failed"
                    }
            }
        }
        binding.btnVote.setOnClickListener{
            lifecycleScope.launch {
                MoviePickerApiFactory.createMoviePickerApi()
                    .vote(binding.edtRoomCode.text.toString(), Vote(binding.edtVoteId.text.toString().toInt()))
                    .onSuccess {
                        binding.textOutput.text = "success"
                    }
                    .onFailure {
                        binding.textOutput.text = "failed"
                    }
            }
        }
        binding.btnClose.setOnClickListener{
            lifecycleScope.launch {
                MoviePickerApiFactory.createMoviePickerApi()
                    .closeRoom(binding.edtRoomCode.text.toString())
                    .onSuccess {
                        binding.textOutput.text = "success"
                    }
                    .onFailure {
                        binding.textOutput.text = "failed"
                    }
            }
        }
        binding.btnDelete.setOnClickListener{
            lifecycleScope.launch {
                MoviePickerApiFactory.createMoviePickerApi()
                    .deleteRoom(binding.edtRoomCode.text.toString())
                    .onSuccess {
                        binding.textOutput.text = "success"
                    }
                    .onFailure {
                        binding.textOutput.text = "failed"
                    }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}