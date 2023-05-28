package app.will.scancalculator.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import app.will.scancalculator.Const.DATA_STORE_NAME
import app.will.scancalculator.Const.LOCAL_DATABASE_NAME
import app.will.scancalculator.local.LocalDatabase
import app.will.scancalculator.local.dao.CalculationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLocalDatabase(@ApplicationContext context: Context): LocalDatabase =
        Room.databaseBuilder(
            context, LocalDatabase::class.java, LOCAL_DATABASE_NAME
        ).build()

    @Provides
    @Singleton
    fun provideCalculationDao(localDatabase: LocalDatabase): CalculationDao {
        return localDatabase.calculationDao
    }

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        preferencesDataStore(name = DATA_STORE_NAME).getValue(context, String::javaClass)
}