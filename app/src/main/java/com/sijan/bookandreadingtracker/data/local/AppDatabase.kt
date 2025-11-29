package com.sijan.bookandreadingtracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [BookEntity::class, UserEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "book_tracker_db"
    }
}

