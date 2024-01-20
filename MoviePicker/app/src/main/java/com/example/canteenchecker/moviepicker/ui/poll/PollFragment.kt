package com.example.canteenchecker.moviepicker.ui.poll

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.canteenchecker.moviepicker.R
import com.example.canteenchecker.moviepicker.api.MoviePickerApi
import com.example.canteenchecker.moviepicker.api.MoviePickerApiFactory
import com.example.canteenchecker.moviepicker.api.TMDBApiFactory
import com.example.canteenchecker.moviepicker.core.moviepicker.Vote
import com.example.canteenchecker.moviepicker.core.moviepicker.VoteList
import com.example.canteenchecker.moviepicker.databinding.FragmentPollBinding
import com.example.canteenchecker.moviepicker.ui.AddMovieActivity
import com.example.canteenchecker.moviepicker.ui.RoomActivity
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


        binding.btnJoinRoom.setOnClickListener{
            lifecycleScope.launch {
                MoviePickerApiFactory.createMoviePickerApi()
                    .getRoom(binding.edtRoomCode.text.toString())
                    .onSuccess {
                        startActivity(
                            RoomActivity.intent(requireContext(), it.roomCode)
                        )
                    }
                    .onFailure {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.text_room_does_not_exist),
                            Toast.LENGTH_LONG)
                            .show()
                    }
            }
        }

        binding.edtRoomCode.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnJoinRoom.isEnabled = s.toString().trim().isNotEmpty()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun afterTextChanged(s: Editable?) { }
        })
        binding.btnJoinRoom.isEnabled = binding.edtRoomCode.text.toString().trim().isNotEmpty()

        binding.btnCreateRoom.setOnClickListener{
            lifecycleScope.launch {
                MoviePickerApiFactory.createMoviePickerApi()
                    .createRoom()
                    .onSuccess {
                        startActivity(
                            RoomActivity.intent(requireContext(), it.roomCode)
                        )
                    }
                    .onFailure {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.text_could_not_create_room),
                            Toast.LENGTH_LONG)
                            .show()
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