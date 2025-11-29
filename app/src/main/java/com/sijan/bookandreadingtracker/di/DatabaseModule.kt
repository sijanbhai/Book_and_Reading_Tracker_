package com.sijan.bookandreadingtracker.di

import android.content.Context
import androidx.room.Room
import com.sijan.bookandreadingtracker.data.local.AppDatabase
import com.sijan.bookandreadingtracker.data.local.BookDao
import com.sijan.bookandreadingtracker.data.local.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideBookDao(database: AppDatabase): BookDao {
        return database.bookDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
}

