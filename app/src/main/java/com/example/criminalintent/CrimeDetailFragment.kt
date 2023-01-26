package com.example.criminalintent

import android.icu.text.DateFormat
import android.icu.text.DateFormat.getDateInstance
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.criminalintent.databinding.FragmentCrimeDetailBinding
import java.util.*

class CrimeDetailFragment : Fragment() {

	private lateinit var crime: Crime

	private var _binding: FragmentCrimeDetailBinding? = null
	private val binding
		get() = checkNotNull(_binding) {
			"Cannot access binding because it is null. Is the view visible?"
		}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		crime = Crime(
			id = UUID.randomUUID(),
			title = "",
			date = Date(),
			isSolved = false
		)
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
				crime = crime.copy(title = text.toString())
			}

			crimeDate.apply {
				text = getDateInstance(DateFormat.FULL, Locale.US).format(crime.date).toString()
				isEnabled = false
			}

			crimeSolved.setOnCheckedChangeListener { _, isChecked ->
				crime = crime.copy(isSolved = isChecked)
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()

		_binding = null
	}
}