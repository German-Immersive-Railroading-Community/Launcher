package com.troblecodings.launcher.di

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.troblecodings.launcher.services.IUserServiceKt
import com.troblecodings.launcher.services.UserServiceKt
import dev.dirs.ProjectDirectories
import net.raphimc.minecraftauth.MinecraftAuth
import java.net.http.HttpClient
import java.time.Duration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class GirModule : AbstractModule() {
    override fun configure() {
        bind(ObjectMapper::class.java).`in`(Singleton::class.java)
        bind(IUserServiceKt::class.java).to(UserServiceKt::class.java).`in`(Singleton::class.java)
    }

    @Provides
    @Singleton
    fun provideProjectDirectories(): ProjectDirectories = ProjectDirectories.from("", "", "girc-launcher")

    @Provides
    @Singleton
    fun provideExecutorService(): ExecutorService = Executors.newVirtualThreadPerTaskExecutor()

    @Provides
    @Singleton
    fun provideRaphiHttpClient(): net.lenni0451.commons.httpclient.HttpClient =
        MinecraftAuth
            .createHttpClient("MinecraftAuth: GIRC-Launcher")
            .setConnectTimeout(10000)
            .setReadTimeout(10000)
            .setFollowRedirects(false)
            .setIgnoreInvalidSSL(false)

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .version(HttpClient.Version.HTTP_2)
        .followRedirects(HttpClient.Redirect.NEVER)
        .build()
}