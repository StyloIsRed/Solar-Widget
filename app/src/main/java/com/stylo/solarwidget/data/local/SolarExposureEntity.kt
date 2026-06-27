package com.stylo.solarwidget.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "solar_exposure")
data class SolarExposureEntity(
    @PrimaryKey
    val date: String, // ISO format: YYYY-MM-DD
    val cloudCoverage: Int, // 0-100%
    val solarExposure: Int, // 0-100%
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface SolarExposureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exposure: SolarExposureEntity)
    
    @Query("SELECT * FROM solar_exposure WHERE date = :date")
    suspend fun getByDate(date: String): SolarExposureEntity?
    
    @Query("SELECT * FROM solar_exposure ORDER BY date DESC LIMIT 1")
    suspend fun getLatest(): SolarExposureEntity?
    
    @Query("SELECT * FROM solar_exposure WHERE date = :date")
    fun observeByDate(date: String): Flow<SolarExposureEntity?>
    
    @Query("SELECT * FROM solar_exposure ORDER BY date DESC")
    fun observeAll(): Flow<List<SolarExposureEntity>>
}

@Database(entities = [SolarExposureEntity::class], version = 1)
abstract class SolarExposureDatabase : RoomDatabase() {
    abstract fun solarExposureDao(): SolarExposureDao
}
