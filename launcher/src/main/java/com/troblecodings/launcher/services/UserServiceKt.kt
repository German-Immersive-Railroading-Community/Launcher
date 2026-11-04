package com.troblecodings.launcher.services

import com.google.gson.JsonObject
import com.google.inject.Inject
import com.troblecodings.launcher.util.FileUtil
import com.troblecodings.launcher.util.LauncherPaths
import net.lenni0451.commons.httpclient.HttpClient
import net.raphimc.minecraftauth.java.JavaAuthManager
import net.raphimc.minecraftauth.java.model.MinecraftProfile
import net.raphimc.minecraftauth.msa.model.MsaDeviceCode
import net.raphimc.minecraftauth.msa.service.impl.DeviceCodeMsaAuthService
import net.raphimc.minecraftauth.msa.service.util.MsaAuthServiceSupplier
import org.apache.logging.log4j.LogManager
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer

class UserServiceKt @Inject constructor(
    private val httpClient: HttpClient,
    private val executorService: ExecutorService
) : IUserServiceKt {
    companion object {
        private val logger = LogManager.getLogger(UserServiceKt::class.java)
    }

    private val authOperationInProgress = AtomicBoolean(false)
    private val authSession = AtomicReference<JavaAuthManager?>(null)

    override fun loginAsync(deviceCodeCallback: Consumer<MsaDeviceCode?>?): CompletableFuture<Unit> = handleAuthAction {
        if (authSession.get() != null) throw AlreadyLoggedIn()

        if (Files.exists(LauncherPaths.getSessionFilePath()) && loadSession()) {
            logger.info("Loaded local session")
            return@handleAuthAction
        }

        val supplier = MsaAuthServiceSupplier { client, config ->
            DeviceCodeMsaAuthService(client, config, deviceCodeCallback)
        }

        val builder = JavaAuthManager.create(httpClient)
        val authManager = builder.login(supplier)

        authSession.set(authManager)
        logger.info("Logged in")
    }

    override fun logoutAsync(): CompletableFuture<Unit> = handleAuthAction {
        if (authSession.get() == null) throw NotLoggedIn()
        authSession.set(null)
        Files.deleteIfExists(LauncherPaths.getSessionFilePath())
        logger.info("Logged out")
    }
    override fun loadSession(): Boolean = try {
        Files.newBufferedReader(LauncherPaths.getSessionFilePath())
            .use { reader ->
                val json = FileUtil.GSON.fromJson(reader, JsonObject::class.java)
                authSession.set(JavaAuthManager.fromJson(httpClient, json))
                logger.info("Loaded session from file.")
            }
        true
    } catch (e: IOException) {
        logger.error("Failed loading local session!", e)
        false
    }

    /**
     * Saves the current session. If there is no current session, this method returns.
     *
     *
     * Intended to be called by registering a callback with [JavaAuthManager]'s getChangeListeners.
     */
    override fun saveSession() {
        val session = authSession.get() ?: throw SessionNotFound()

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

    override val profile: MinecraftProfile?
        get() = authSession.get()?.minecraftProfile?.cached

    override fun isAuthenticated(): Boolean = authSession.get() != null
    override fun isAuthOperationInProgress(): Boolean = authOperationInProgress.get()

    private fun <T> handleAuthAction(operation: () -> T): CompletableFuture<T> {
        if (!authOperationInProgress.compareAndSet(false, true)) {
            return CompletableFuture.failedFuture(AuthenticationInProgress())
        }

        return try {
            CompletableFuture.supplyAsync(operation, executorService).whenComplete { _, _ ->
                authOperationInProgress.set(false)
            }
        } finally {
            authOperationInProgress.set(false)
        }
    }
}

class AuthenticationInProgress : IllegalStateException(
    "A login or logout operation is already in progress."
)

class AlreadyLoggedIn : IllegalStateException("You are already logged in.")
class NotLoggedIn : IllegalStateException("You are not logged in.")

class SessionAlreadyLoaded : IllegalStateException("A session is already loaded. Log out first.")

class SessionNotFound : IllegalStateException("No active session found.")