package com.troblecodings.launcher.services

import net.raphimc.minecraftauth.java.model.MinecraftProfile
import net.raphimc.minecraftauth.msa.model.MsaDeviceCode
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

interface IUserServiceKt {
    @Throws(IOException::class)
    fun login(deviceCodeCallback: Consumer<MsaDeviceCode?>?)
    fun loginAsync(deviceCodeCallback: Consumer<MsaDeviceCode?>?): CompletableFuture<Void>

    fun logout()
    fun logoutAsync(): CompletableFuture<Void>

    fun loadSession(): Boolean
    fun saveSession()

    val profile: MinecraftProfile?
}