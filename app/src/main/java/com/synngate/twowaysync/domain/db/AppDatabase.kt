package com.synngate.twowaysync.domain.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.synngate.twowaysync.data.source.local.dao.LogDao
import com.synngate.twowaysync.data.source.local.dao.ProductDao
import com.synngate.twowaysync.data.source.local.dao.ExternalServerDao
import com.synngate.twowaysync.data.source.local.entity.LogDetailsEntity
import com.synngate.twowaysync.data.source.local.entity.ProductDetailsEntity
import com.synngate.twowaysync.domain.model.ExternalServer

@Database(entities = [LogDetailsEntity::class, ExternalServer::class, ProductDetailsEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao
    abstract fun externalServerDao(): ExternalServerDao
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}