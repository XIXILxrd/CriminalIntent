package com.example.criminalintent

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CrimeDetailViewModel : ViewModel() {
	private val crimeRepository = CrimeRepository.get()

	private val _crime: MutableStateFlow<Crime?> = MutableStateFlow(null)
	val crime: StateFlow<Crime?> = _crime.asStateFlow()
}