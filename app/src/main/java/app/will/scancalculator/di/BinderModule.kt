package app.will.scancalculator.di

import app.will.scancalculator.gateway.DataRepository
import app.will.scancalculator.service.DataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BinderModule {

    @Binds
    @Singleton
    abstract fun bindDataRepository(dataRepositoryImpl: DataRepositoryImpl): DataRepository
}