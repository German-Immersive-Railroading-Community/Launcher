package com.troblecodings.launcher.services

import com.google.inject.Inject
import com.troblecodings.launcher.util.FileUtil
import com.troblecodings.launcher.util.LauncherPaths
import net.lenni0451.commons.httpclient.HttpClient
import net.raphimc.minecraftauth.java.JavaAuthManager
import net.raphimc.minecraftauth.java.model.MinecraftProfile
import net.raphimc.minecraftauth.msa.model.MsaApplicationConfig
import net.raphimc.minecraftauth.msa.model.MsaDeviceCode
import net.raphimc.minecraftauth.msa.service.impl.DeviceCodeMsaAuthService
import org.apache.logging.log4j.LogManager
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer

class UserServiceKt @Inject constructor(
    private val httpClient: HttpClient,
    private val executorService: ExecutorService
) : IUserServiceKt {
    companion object {
        private val logger = LogManager.getLogger(javaClass.name)
    }

    private val authActionInProgress: AtomicBoolean = AtomicBoolean(false)

    @Volatile
    private var session: JavaAuthManager? = null

    @Volatile
    private var currentRequest: Future<*>? = null

    override val profile: MinecraftProfile?
        get() = session?.minecraftProfile?.cached

    @Throws(IOException::class)
    override fun login(deviceCodeCallback: Consumer<MsaDeviceCode?>?) {
        while (!authActionInProgress.compareAndSet(false, true)) {
            logger.debug("Waiting for entry into critical section.")
        }
        
        if (session != null && session?.minecraftToken?.upToDate != null) {
            logger.info("Session is up to date.")
            return
        }

        // Critical section (we dont want to login twice at the same time)
        if (!authActionInProgress.compareAndSet(false, true)) {
            logger.info("Login already in progress.")
            return
        }

        try {
            val builder = JavaAuthManager.create(httpClient)
            val session = builder.login({ httpClient: HttpClient?, applicationConfig: MsaApplicationConfig?, callback: Consumer<MsaDeviceCode?>? ->
                DeviceCodeMsaAuthService(
                    httpClient,
                    applicationConfig,
                    callback
                )
            }, deviceCodeCallback)

            // Get these here already so that we don't have to do any refresh operations.
            session.minecraftToken?.getUpToDate()
            session.minecraftProfile?.getUpToDate()
            session.changeListeners?.add { this.saveSession() }

            this.session = session
        } finally {
            authActionInProgress.set(false)
        }
    }

    override fun loginAsync(deviceCodeCallback: Consumer<MsaDeviceCode?>?): CompletableFuture<Void> =
        CompletableFuture
            .runAsync({ login(deviceCodeCallback) }, executorService)
            .exceptionally { error -> logger.error("Login failed", error); null }

    override fun logout() {
        TODO("Not yet implemented")
    }

    override fun logoutAsync(): CompletableFuture<Void> {
        TODO("Not yet implemented")
    }

    override fun loadSession(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Saves the current session. If there is no current session, this method returns.
     *
     *
     * Intended to be called by registering a callback with [JavaAuthManager]'s getChangeListeners.
     */
    override fun saveSession() {
        if (session == null) return

        val json = JavaAuthManager.toJson(session)

        try {
            Files.write(
                LauncherPaths.getSessionFilePath(),
                FileUtil.GSON.toJson(json).toByteArray(StandardCharsets.UTF_8)
            )
        } catch (e: IOException) {
            logger.error("Failed saving session locally!", e)
        }
    }
}