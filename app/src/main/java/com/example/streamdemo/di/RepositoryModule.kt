package com.example.streamdemo.di

import com.example.streamdemo.data.auth.AuthRepository
import com.example.streamdemo.data.auth.InMemoryAuthRepository
import com.example.streamdemo.data.repository.RemoteVideoRepository
import com.example.streamdemo.data.repository.VideoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVideoRepository(impl: RemoteVideoRepository): VideoRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: InMemoryAuthRepository): AuthRepository



}