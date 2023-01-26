package com.example.criminalintent.database

import androidx.room.Dao
import androidx.room.Query
import com.example.criminalintent.Crime
import kotlinx.coroutines.flow.Flow
import java.util.UUID

//Data access object

@Dao
interface CrimeDao {
    @Query("SELECT * FROM crime")
    fun getCrimes(): Flow<List<Crime>>

    @Query("SELECT * FROM crime WHERE id=(:id)")
    suspend fun getCrime(id: UUID): Crime
}