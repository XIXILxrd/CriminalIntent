package com.example.criminalintent.database

import androidx.room.*
import com.example.criminalintent.Crime
import kotlinx.coroutines.flow.Flow
import java.util.*

//Data access object

@Dao
interface CrimeDao {
    @Query("SELECT * FROM crime")
    fun getCrimes(): Flow<List<Crime>>

    @Query("SELECT * FROM crime WHERE id=(:id)")
    suspend fun getCrime(id: UUID): Crime

    @Update
    suspend fun updateCrime(crime: Crime)

    @Insert
    suspend fun addCrime(crime: Crime)

    @Delete
    suspend fun deleteCrime(crime: Crime)
}