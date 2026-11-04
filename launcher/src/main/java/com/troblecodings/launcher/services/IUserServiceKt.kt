package com.troblecodings.launcher.services

import net.raphimc.minecraftauth.java.model.MinecraftProfile
import net.raphimc.minecraftauth.msa.model.MsaDeviceCode
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

interface IUserServiceKt {
    fun loginAsync(deviceCodeCallback: Consumer<MsaDeviceCode?>?): CompletableFuture<Unit>
    fun logoutAsync(): CompletableFuture<Unit>

    fun loadSession(): Boolean
    fun saveSession()

    fun isAuthenticated(): Boolean
    fun isAuthOperationInProgress(): Boolean

    val profile: MinecraftProfile?
}