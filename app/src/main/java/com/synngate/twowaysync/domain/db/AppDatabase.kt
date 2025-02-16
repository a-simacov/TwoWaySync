package com.synngate.twowaysync.domain.db // <----  ОБЯЗАТЕЛЬНО проверьте и исправьте пакет, если у вас другой!

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.synngate.twowaysync.data.source.local.dao.LogDao
import com.synngate.twowaysync.data.source.local.dao.ProductDao
import com.synngate.twowaysync.data.source.local.dao.RemoteServerDao
import com.synngate.twowaysync.data.source.local.entity.LogDetailsEntity
import com.synngate.twowaysync.data.source.local.entity.ProductDetailsEntity
import com.synngate.twowaysync.data.source.local.entity.RemoteServerEntity

@Database(entities = [LogDetailsEntity::class, RemoteServerEntity::class, ProductDetailsEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao
    abstract fun remoteServerDao(): RemoteServerDao
    abstract fun productDao(): ProductDao

    companion object { // <----  Добавляем companion object для статического метода
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase { // <----  Реализация статического метода getDatabase(Context)
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database" // <----  Имя вашей базы данных
                )
                    .fallbackToDestructiveMigration() //  Стратегия миграции (для примера - удаление и пересоздание)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}