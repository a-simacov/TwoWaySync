package com.synngate.twowaysync.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.synngate.twowaysync.data.local.db.dao.ExternalServersDao
import com.synngate.twowaysync.data.local.db.dao.ProductsDao
import com.synngate.twowaysync.data.local.db.entities.ExternalServerEntity
import com.synngate.twowaysync.data.local.db.entities.ProductEntity

@Database(entities = [ExternalServerEntity::class, ProductEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun externalServersDao(): ExternalServersDao
    abstract fun productsDao(): ProductsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "twoway_sync_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
