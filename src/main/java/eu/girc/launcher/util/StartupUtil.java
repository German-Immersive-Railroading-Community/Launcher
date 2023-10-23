package eu.girc.launcher.util;

import eu.girc.launcher.Launcher;
import eu.girc.launcher.LauncherPaths;
import eu.girc.launcher.models.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class StartupUtil {
    private static final Logger logger = LogManager.getLogger();
    private static final String minecraft_resource_base_url = "https://resources.download.minecraft.net/";

    private static void unzip(String name, String base) throws Throwable {
        String str = name.replace("\\", "/");
        JarFile file = new JarFile(str);
        Enumeration<JarEntry> enumerator = file.entries();
        while (enumerator.hasMoreElements()) {
            JarEntry entry = enumerator.nextElement();
            File outfile = new File(base + "/" + entry.getName());

            if (entry.isDirectory()) {
                outfile.mkdirs();
                continue;
            }

            outfile.getParentFile().mkdirs();

            ReadableByteChannel readchannel = Channels.newChannel(file.getInputStream(entry));
            FileOutputStream fos = new FileOutputStream(outfile);
            fos.getChannel().transferFrom(readchannel, 0, Long.MAX_VALUE);
            fos.close();
        }
        file.close();
    }

    private static void ensureServersDatExists() throws IOException {
        logger.debug("Verifying valid servers.dat");
        Path pth = Paths.get(FileUtil.SETTINGS.baseDir, "servers.dat");
        if (!Files.exists(pth)) {
            logger.warn("Couldn't find an existing servers.dat file! Recreating.");
            Files.copy(Launcher.getResourceAsStream("servers.dat"), pth);
        }
    }

    /**
     * Downloads all assets required by the Minecraft client.
     * 
     * @param girJson      The parsed GIR.json object.
     * @param currentAsset
     * @param items
     * @throws IOException
     */
    private static void downloadMinecraftAssets(GirJson girJson)
            throws IOException, InterruptedException {
        logger.debug("Starting Mojang asset download");
        final AssetIndex assetIndex = girJson.assetIndex();

        logger.debug("Ensuring assets/ directory is created");
        final Path assetBaseDir = LauncherPaths.getAssetsDir();
        Files.createDirectories(assetBaseDir);

        logger.debug("Ensuring assets/objects/ directory is created");
        final Path objectsBaseDir = LauncherPaths.getObjectsDir();
        Files.createDirectories(LauncherPaths.getObjectsDir());

        logger.debug("Ensuring assets/indexes/ directory is created");
        final Path indexesBaseDir = LauncherPaths.getIndexesDir();
        Files.createDirectories(LauncherPaths.getIndexesDir());

        // assets/indexes/1.12.json
        logger.debug("Downloading assets/indexes/{}.json", assetIndex.id());
        Optional<Path> maybeAssetIndexFile = NetUtils.validateOrDownloadSha1(
                URI.create(assetIndex.url()),
                indexesBaseDir.resolve(assetIndex.id() + ".json"),
                assetIndex.sha1());

        if (maybeAssetIndexFile.isEmpty()) {
            throw new IOException("AssetIndex download failed.");
        }

        final Path assetIndexFile = maybeAssetIndexFile.get();
        final MojangAssets mojAssets = Launcher.GSON.fromJson(Files.readString(assetIndexFile), MojangAssets.class);
        final Map<String, MojangAsset> objects = mojAssets.objects();

        for (String key : objects.keySet()) {
            final MojangAsset asset = objects.get(key);
            logger.debug("Downloading assets/objects/{}/{}", asset.folder(), asset.hash());
            final Path assetParentFolder = objectsBaseDir.resolve(asset.folder());
            Files.createDirectories(assetParentFolder);
            final URI assetUri = URI.create(minecraft_resource_base_url + asset.folder() + "/" + asset.hash());
            final Optional<Path> assetFile = NetUtils.validateOrDownloadSha1(assetUri,
                    assetParentFolder.resolve(asset.hash()), asset.hash());
            if (assetFile.isEmpty()) {
                throw new IOException("Asset " + asset.hash() + " download failed.");
            }
        }
    }

    /**
     * Prepares all assets for the start of the Minecraft process.
     * 
     * @param girJson The parsed GIR.json object.
     * @throws IOException
     */
    private static List<String> prepareStart(GirJson girJson)
            throws IOException, InterruptedException {
        ensureServersDatExists();
        final List<String> libraries = new ArrayList<>();
        downloadMinecraftAssets(girJson);

        return libraries;
    }

    public static Optional<Process> startClient()
            throws IOException, InterruptedException {
        // TODO: Add GIR.json SHA-256 validation after API endpoint has been created
        Optional<GirJson> optGirJson = NetUtils.downloadJson(
                URI.create("https://girc.eu/Launcher/GIR.json"),
                GirJson.class);

        if (optGirJson.isEmpty()) {
            logger.error("Couldn't download GIR.json!");
            return Optional.empty();
        }

        GirJson girJson = optGirJson.get();
        List<String> libraries = prepareStart(girJson);
        if (libraries.isEmpty()) {
            logger.error("No libraries to add to the classpath were found!");
            return Optional.empty();
        }

        return Optional.ofNullable(null);
    }
}
