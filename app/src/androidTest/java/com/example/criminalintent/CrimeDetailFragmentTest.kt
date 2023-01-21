package com.example.criminalintent

import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.After
import org.junit.Before
import org.junit.Test

class CrimeDetailFragmentTest {

	private lateinit var scenario: FragmentScenario<CrimeDetailFragment>

	@Before
	fun setUp() {
	}

	@After
	fun tearDown() {
	}

	@Test
	fun checkBoxTest(){
		onView(withId(R.id.crime_solved)).perform(click())

		onView(withId(R.id.crime_solved)).check(matches(ViewMatchers.isChecked()))
	}
}