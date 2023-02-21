package com.example.criminalintent

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.icu.text.DateFormat
import android.net.Uri
import android.text.format.DateFormat.format
import android.os.Bundle
import android.provider.ContactsContract
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
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
import java.io.File
import java.util.*

private const val DATE_FORMAT = "EEE, dd, MMM, yyyy"
private const val TIME_FORMAT = "HH:mm"

class CrimeDetailFragment : Fragment() {

	private val args: CrimeDetailFragmentArgs by navArgs()

	private val crimeDetailViewModel: CrimeDetailViewModel by viewModels {
		CrimeDetailViewModelFactory(args.crimeId)
	}

	private val selectSuspect = registerForActivityResult(
		ActivityResultContracts.PickContact()
	) { uri: Uri? ->
		uri?.let {
			parseContactSelection(it)
		}
	}

	private var takePhoto = registerForActivityResult(
		ActivityResultContracts.TakePicture()
	) { didTakePhoto: Boolean ->
		if (didTakePhoto && photoName != null) {
			crimeDetailViewModel.updateCrime { oldCrime ->
				oldCrime.copy(photoFileName = photoName)
			}
		}
	}

	private var photoName: String? = null

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

		createActionBar()

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

			crimeSuspect.setOnClickListener {
				selectSuspect.launch(null)
			}

			val selectSuspectIntent = selectSuspect.contract.createIntent(
				requireContext(),
				null
			)

			crimeSuspect.isEnabled = canResolveIntent(selectSuspectIntent)

			crimeCamera.setOnClickListener {
				photoName = "IMG_${Date()}.JPG"
				val photoFile = File(requireActivity().applicationContext.filesDir,
									photoName)
				val photoUri = FileProvider.getUriForFile(
					requireContext(),
					"com.example.criminalintent.fileprovider",
					photoFile
				)

				takePhoto.launch(photoUri)
			}

			val captureImageIntent = takePhoto.contract.createIntent(
				requireContext(),
				null
			)

			crimeCamera.isEnabled = canResolveIntent(captureImageIntent)
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

			crimeDetailViewModel.updateCrime { it.copy(date = newDate) }
		}

		setFragmentResultListener(
			TimePickerFragment.REQUEST_KEY_TIME
		) { _, bundle ->
			val newTime =
				bundle.getSerializable(TimePickerFragment.BUNDLE_KEY_TIME) as Date

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

			crimeDate.setOnClickListener {
				findNavController().navigate(
					CrimeDetailFragmentDirections.selectDate(crime.date)
				)
			}

			crimeTime.setOnClickListener {
				findNavController().navigate(
					CrimeDetailFragmentDirections.selectTime(crime.time)
				)
			}

			crimeDate.text = DateFormat.getDateInstance(DateFormat.FULL)
				.format(crime.date).toString()

			crimeTime.text = DateFormat.getTimeInstance(DateFormat.SHORT)
				.format(crime.time).toString()

			crimeSolved.isChecked = crime.isSolved

			crimeReport.setOnClickListener {
				val reportIntent = Intent(Intent.ACTION_SEND).apply {
					type = "text/plain"

					putExtra(Intent.EXTRA_TEXT, getCrimeReport(crime))
					putExtra(
						Intent.EXTRA_SUBJECT,
						getString(R.string.crime_report_suspect)
					)
				}

				val chooserIntent = Intent.createChooser(
					reportIntent,
					getString(R.string.send_report)
				)

				startActivity(chooserIntent)
			}

			crimeSuspect.text = crime.suspect.ifEmpty {
				getString(R.string.crime_suspect_text)
			}
		}
	}

	private fun getCrimeReport(crime: Crime) : String {
		val solvedString = if (crime.isSolved) {
			getString(R.string.crime_report_solved)
		} else {
			getString(R.string.crime_report_unsolved)
		}

		val dateString = format(DATE_FORMAT, crime.date).toString()
		val timeString = format(TIME_FORMAT, crime.time).toString()

		val suspectText = if (crime.suspect.isBlank()) {
			getString(R.string.crime_report_no_suspect)
		} else {
			getString(R.string.crime_report_suspect, crime.suspect)
		}

		return getString(
			R.string.crime_report,
			crime.title, dateString, timeString, solvedString, suspectText
		)

	}

	private fun deleteAndExit() {
		viewLifecycleOwner.lifecycleScope.launch {
			crimeDetailViewModel.crime.value?.let {
				crimeDetailViewModel.deleteCrime(it)
			}
			findNavController().popBackStack()
		}
	}

	private fun createActionBar() {
		val menuHost: MenuHost = requireActivity()

		menuHost.addMenuProvider(object : MenuProvider {
			override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
				menuInflater.inflate(R.menu.fragment_crime_detail, menu)
			}

			override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
				return when(menuItem.itemId) {
					R.id.delete_crime -> {
						deleteAndExit()
						true
					}
					else -> onMenuItemSelected(menuItem)
				}
			}
		}, viewLifecycleOwner, Lifecycle.State.RESUMED)
	}

	private fun parseContactSelection(contactUri: Uri) {
		val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

		val queryCursor = requireActivity().contentResolver
			.query(contactUri, queryFields, null, null, null)

		queryCursor?.use { cursor ->
			if (cursor.moveToFirst()) {
				val suspect = cursor.getString(0)

				crimeDetailViewModel.updateCrime { oldCrime ->
					oldCrime.copy(suspect = suspect)
				}
			}
		}
	}

	private fun canResolveIntent(intent: Intent) : Boolean {
		val packageManager: PackageManager = requireActivity().packageManager
		val resolvedActivity: ResolveInfo? =
			packageManager.resolveActivity(
				intent,
				PackageManager.MATCH_DEFAULT_ONLY
			)

		return resolvedActivity != null
	}


}