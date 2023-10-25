package eu.girc.launcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Contains some utilities used by the Launcher.
 */
public final class LauncherUtils {
    private LauncherUtils() { }

    /**
     * Unjars a file to a specific location in the file system.
     *
     * @param file         The JAR file to unpack.
     * @param baseLocation The base location where the JAR entries should be decompressed to.
     * @throws IOException If an I/O error occurs.
     */
    public static void unjar(Path file, Path baseLocation) throws IOException {
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
}
