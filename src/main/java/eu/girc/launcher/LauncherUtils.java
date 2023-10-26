package eu.girc.launcher;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Contains some utilities used by the Launcher.
 */
public final class LauncherUtils {
    private static final Logger logger = LogManager.getLogger();

    private LauncherUtils() { }

    /**
     * Unjars a file to a specific location in the file system.
     *
     * @param file         The JAR file to unpack.
     * @param baseLocation The base location where the JAR entries should be decompressed to.
     * @throws IOException If an I/O error occurs.
     */
    public static void unzipJar(Path file, Path baseLocation) throws IOException {
        try (final JarFile jar = new JarFile(file.toFile())) {
            Enumeration<JarEntry> enumerator = jar.entries();
            while (enumerator.hasMoreElements()) {
                JarEntry entry = enumerator.nextElement();
                if (entry.isDirectory()) {
                    Files.createDirectories(baseLocation.resolve(entry.getName()));
                    continue;
                }

                final File out = baseLocation.resolve(entry.getName()).toFile();
                try (final FileOutputStream fos = new FileOutputStream(out); final InputStream entryStream = jar.getInputStream(entry); final ReadableByteChannel channel = Channels.newChannel(entryStream)) {
                    fos.getChannel().transferFrom(channel, 0, entry.getSize());
                }
            }
        }
    }

    public static void unzipZip(Path archive, Path baseLocation) throws IOException {
        logger.debug("Starting unzipZip of {} to {}", archive, baseLocation);
        Files.createDirectories(baseLocation);
        try (final ZipFile zipFile = new ZipFile(archive)) {
            logger.debug("Zip file opened");
            final Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            final byte[] buffer = new byte[1024];
            int len;
            while (entries.hasMoreElements()) {
                final ZipArchiveEntry entry = entries.nextElement();
                final Path entryPath = baseLocation.resolve(entry.getName());

                logger.debug("Unzipping {}", entryPath);
                if (entry.isDirectory() || entryPath.toFile().isDirectory()) {
                    logger.debug("Was directory");
                    Files.createDirectories(entryPath);
                    continue;
                }

                logger.debug("Wasn't directory");
                try (final OutputStream out = Files.newOutputStream(entryPath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE); final InputStream zipIn = zipFile.getInputStream(entry)) {
                    while ((len = zipIn.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                }
            }
        }
        logger.debug("Unzip finished", archive, baseLocation);
    }

    public static void unzipGZip(Path archive, Path baseLocation) throws IOException {
        logger.debug("Starting unzipGZip of {} to {}", archive, baseLocation);
        Files.createDirectories(baseLocation);
    }
}
