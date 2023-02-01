package com.example.criminalintent

import android.icu.text.DateFormat
import android.icu.text.DateFormat.getDateInstance
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.criminalintent.databinding.FragmentCrimeDetailBinding
import java.util.*

private const val TAG = "CrimeDetailFragment"

class CrimeDetailFragment : Fragment() {

	private lateinit var crime: Crime

	private val args: CrimeDetailFragmentArgs by navArgs()

	private var _binding: FragmentCrimeDetailBinding? = null
	private val binding
		get() = checkNotNull(_binding) {
			"Cannot access binding because it is null. Is the view visible?"
		}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentCrimeDetailBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {
			crimeTitle.doOnTextChanged { text, _, _, _ ->
			}

			crimeDate.apply {
				isEnabled = false
			}

			crimeSolved.setOnCheckedChangeListener { _, isChecked ->
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()

		_binding = null
	}
}