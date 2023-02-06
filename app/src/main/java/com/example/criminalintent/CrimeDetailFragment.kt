package com.example.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.criminalintent.databinding.FragmentCrimeDetailBinding
import kotlinx.coroutines.launch
import java.util.*

@Suppress("DEPRECATION")
class CrimeDetailFragment : Fragment() {

	private val args: CrimeDetailFragmentArgs by navArgs()

	private val crimeDetailViewModel: CrimeDetailViewModel by viewModels {
		CrimeDetailViewModelFactory(args.crimeId)
	}

	private var _binding: FragmentCrimeDetailBinding? = null
	private val binding
		get() = checkNotNull(_binding) {
			"Cannot access binding because it is null. Is the view visible?"
		}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		val callBack = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				if (crimeDetailViewModel.crime.value?.title.toString().isBlank()) {
					Toast.makeText(
						requireActivity(),
						R.string.untitled_crime_toast,
						Toast.LENGTH_LONG
					).show()
				}
				else {
					isEnabled = false
					requireActivity().onBackPressedDispatcher.onBackPressed()
				}
			}
		}

		requireActivity().onBackPressedDispatcher.addCallback(callBack)

		_binding = FragmentCrimeDetailBinding.inflate(inflater, container, false)

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {
			crimeTitle.doOnTextChanged { text, _, _, _ ->
				crimeDetailViewModel.updateCrime { oldCrime ->
					oldCrime.copy(title = text.toString())
				}
			}

			crimeSolved.setOnCheckedChangeListener { _, isChecked ->
				crimeDetailViewModel.updateCrime { oldCrime ->
					oldCrime.copy(isSolved = isChecked)
				}
			}
		}

		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
				crimeDetailViewModel.crime.collect {crime ->
					crime?.let {
						updateUi(it)
					}
				}
			}
		}

		setFragmentResultListener(
			DatePickerFragment.REQUEST_KEY_DATE
		) { _, bundle ->
			val newDate =
				bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
			val newTime =
				bundle.getSerializable(TimePickerFragment.BUNDLE_KEY_TIME) as Date

			crimeDetailViewModel.updateCrime { it.copy(date = newDate) }
			crimeDetailViewModel.updateCrime { it.copy(time = newTime) }
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()

		_binding = null
	}

	private fun updateUi(crime: Crime) {
		binding.apply {
			if (crimeTitle.text.toString() != crime.title) {
				crimeTitle.setText(crime.title)
			}
			crimeDate.text = crime.date.toString()
			crimeTime.text = crime.time.toString()

			crimeDate.setOnClickListener {
				findNavController().navigate(
					CrimeDetailFragmentDirections.selectDate(crime.date)
				)
			}



			crimeSolved.isChecked = crime.isSolved
 		}
	}

}