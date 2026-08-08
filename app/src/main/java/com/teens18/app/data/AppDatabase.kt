package com.teens18.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.teens18.app.model.PostEntity

@Database(entities = [PostEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            val db = Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java, "teens18.db").fallbackToDestructiveMigration().build()
            INSTANCE = db; db
        }
    }
}