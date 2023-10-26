package eu.girc.launcher;

import com.google.gson.reflect.TypeToken;
import eu.girc.launcher.models.AssetIndex;
import eu.girc.launcher.models.GirJson;
import eu.girc.launcher.models.LibraryAsset;
import eu.girc.launcher.models.LibraryDownload;
import eu.girc.launcher.models.MojangAsset;
import eu.girc.launcher.models.MojangAssets;
import eu.girc.launcher.models.adoptium.AdoptiumArtifact;
import eu.girc.launcher.models.adoptium.AdoptiumAsset;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StartupUtil {
    private static final Logger logger = LogManager.getLogger();

    private static final String minecraft_resource_base_url = "https://resources.download.minecraft.net/";

    private StartupUtil() { }

    public static Optional<Process> startClient() throws IOException, InterruptedException {
        // Validate or download GIR.json, if it doesn't exist or is not up-to-date
        Optional<String> optGirJsonHash = NetUtils.downloadString(URI.create("https://api.girc.eu/launcher/manifest/sha256"));
        Optional<Path> optGirJson = NetUtils.validateOrDownloadSha256(URI.create("https://girc.eu/Launcher/GIR.json"), LauncherPaths.getConfigDir().resolve("GIR.json"), optGirJsonHash.orElse(""));

        if (optGirJson.isEmpty()) {
            logger.error("Couldn't download GIR.json!");
            return Optional.empty();
        }
        // downloads the latest Eclipse Temurin JDK with Java version 8
        final Path javaw = downloadJava8();

        GirJson girJson = Launcher.GSON.fromJson(Files.readString(optGirJson.get()), GirJson.class);
        List<String> libraries = prepareStart(girJson);
        if (libraries.isEmpty()) {
            logger.error("No libraries to add to the classpath were found!");
            return Optional.empty();
        }

        String[] args = make_arguments(girJson);

        for (String arg : args) {
            logger.debug("Arg: {}", arg);
        }

        return Optional.empty();
        //        ProcessBuilder builder = new ProcessBuilder(args).directory(LauncherPaths.getConfigDir().toFile()).redirectError(ProcessBuilder.Redirect.INHERIT).redirectOutput(ProcessBuilder.Redirect.INHERIT);
        //
        //        return Optional.of(builder.start());
    }

    private static String[] make_arguments(GirJson girJson) {
        return girJson.minecraftArguments()
                      .replace("${auth_player_name}", AuthManager.getUsername())
                      .replace("${version_name}", girJson.id())
                      .replace("${game_directory}", LauncherPaths.getConfigDir().toString())
                      .replace("${assets_root}", LauncherPaths.getAssetsDir().toString())
                      .replace("${assets_index_name}", girJson.assetIndex().id())
                      .replace("${auth_uuid}", AuthManager.getUuid())
                      .replace("${auth_access_token}", AuthManager.getAccessToken())
                      .replace("${user_type}", AuthManager.getUserType())
                      .split(" ");
    }

    /**
     * Prepares all assets for the start of the Minecraft process.
     * <br>
     * Each subsequent method called by this method should do automatic cleanup of the directories it affects.
     *
     * @param girJson The parsed GIR.json object.
     * @throws IOException
     */
    private static List<String> prepareStart(GirJson girJson) throws IOException, InterruptedException {
        // this ensures that we do not start with a missing servers.dat
        ensureServersDatExists();

        downloadMinecraftAssets(girJson);
        return downloadLibraries(girJson);
    }

    private static void ensureServersDatExists() throws IOException {
        logger.debug("Verifying valid servers.dat");
        Path pth = LauncherPaths.getConfigDir().resolve("servers.dat");
        if (!Files.exists(pth)) {
            logger.warn("Couldn't find an existing servers.dat file! Recreating.");
            Files.copy(Launcher.getResourceAsStream("servers.dat"), pth);
        }
    }

    // TODO: delete old files if new ones were detected.

    /**
     * Downloads the Java 8 Adoptium Temurin Hotspot JRE, if it does not exist and/or is outdated.
     *
     * @return The path to the javaw executable (/install_path/bin/javaw.exe).
     * @throws IOException          If an I/O error occurs.
     * @throws InterruptedException If the web request is interrupted.
     */
    private static Path downloadJava8() throws IOException, InterruptedException {
        final URI adoptiumApiJavaEndpoint = URI.create(String.format("https://api.adoptium.net/v3/assets/latest/8/hotspot?architecture=%s&image_type=jre&os=%s&vendor=eclipse", getArch(), getOSShorthand()));
        final Path temurinBasePath = LauncherPaths.getJava8Dir();
        logger.debug("Resolving Adoptium Temurin v8 manifest");
        TypeToken<List<AdoptiumAsset>> adoptiumAssetTypeToken = new TypeToken<>() { };
        final Optional<List<AdoptiumAsset>> optAssets = NetUtils.downloadJson(adoptiumApiJavaEndpoint, adoptiumAssetTypeToken);
        List<AdoptiumAsset> assets = optAssets.orElseThrow();

        if (assets.isEmpty()) {
            throw new RuntimeException("Adoptium API request returned no assets");
        }

        logger.debug("Starting validation and/or download of Adoptium Temurin v8");
        AdoptiumAsset asset = assets.get(0);
        AdoptiumArtifact pkg = asset.binary().rawPackage();
        // construct important paths here
        // pkg.name() is the archive with the os-corresponding file extension
        final Path temurinArchive = temurinBasePath.resolve(pkg.name());
        final Path temurinChecksum = temurinBasePath.resolve("sha256.txt");
        final Path temurinJavaw = temurinBasePath.resolve(asset.releaseName() + "-" + asset.binary().image_type()).resolve("bin").resolve("javaw.exe");

        logger.debug("Validating or downloading temurin/sha256.txt");
        NetUtils.downloadFileIfNotExist(URI.create(pkg.checksumLink()), temurinChecksum);
        final String checksum = Files.readString(temurinChecksum).substring(0, 64);
        if (!checksum.equals(pkg.checksum())) {
            logger.debug("checksum.txt mismatch with JSON checksum, redownloading checksum.txt");
            NetUtils.downloadFile(URI.create(pkg.checksumLink()), temurinChecksum);
        }

        if (NetUtils.validateSha256(pkg.checksum(), temurinArchive)) {
            logger.debug("Valid local temurin instance found, skipping JRE download.");
            return temurinJavaw;
        }

        logger.debug("Validating or downloading temurin/{}", pkg.name());
        NetUtils.validateOrDownloadSha256(URI.create(pkg.link()), temurinArchive, checksum);

        if (pkg.name().endsWith(".zip")) {
            LauncherUtils.unzipZip(temurinArchive, temurinBasePath);
        } else if (pkg.name().endsWith(".tar.gz")) {
            LauncherUtils.unzipGZip(temurinArchive, temurinBasePath);
        } else {
            throw new RuntimeException("Unsupported archive ending: " + temurinArchive.getFileName());
        }

        return temurinJavaw;
    }

    private static String getArch() {
        return switch (SystemUtils.OS_ARCH) {
            case "amd64" -> "x64";
            case "x86" -> "x86";
            default -> throw new RuntimeException("Unsupported arch: " + SystemUtils.OS_ARCH);
        };
    }

    private static String getOSShorthand() {
        final String osName = SystemUtils.OS_NAME.toLowerCase();
        if (osName.contains("windows")) {
            return "windows";
        } else if (osName.contains("linux") || osName.contains("unix")) {
            return "linux";
        } else if (osName.contains("mac") || osName.contains("osx")) {
            return "mac";
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + SystemUtils.OS_NAME);
        }
    }

    /**
     * Downloads all assets required by the Minecraft client.
     *
     * @param girJson The parsed GIR.json object.
     * @throws IOException          Thrown when the download of an asset fails.
     * @throws InterruptedException Thrown when the download of an asset is interrupted.
     */
    private static void downloadMinecraftAssets(GirJson girJson) throws IOException, InterruptedException {
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
        Optional<Path> maybeAssetIndexFile = NetUtils.validateOrDownloadSha1(URI.create(assetIndex.url()), indexesBaseDir.resolve(assetIndex.id() + ".json"), assetIndex.sha1());

        if (maybeAssetIndexFile.isEmpty()) {
            throw new IOException("AssetIndex download failed.");
        }

        final Path assetIndexFile = maybeAssetIndexFile.get();
        final MojangAssets mojAssets = Launcher.GSON.fromJson(Files.readString(assetIndexFile), MojangAssets.class);
        final Map<String, MojangAsset> objects = mojAssets.objects();

        for (String key : objects.keySet()) {
            final MojangAsset asset = objects.get(key);
            logger.debug("Validating or downloading assets/objects/{}/{}", asset.folder(), asset.hash());
            final Path assetParentFolder = objectsBaseDir.resolve(asset.folder());
            Files.createDirectories(assetParentFolder);
            final URI assetUri = URI.create(minecraft_resource_base_url + asset.folder() + "/" + asset.hash());
            final Optional<Path> assetFile = NetUtils.validateOrDownloadSha1(assetUri, assetParentFolder.resolve(asset.hash()), asset.hash());
            if (assetFile.isEmpty()) {
                throw new IOException("Asset " + asset.hash() + " download failed.");
            }
        }
    }

    private static List<String> downloadLibraries(GirJson girJson) throws IOException, InterruptedException {
        final List<String> finalLibraryList = new ArrayList<>();
        final List<LibraryAsset> libraries = girJson.libraryAssets();

        for (LibraryAsset library : libraries) {
            logger.debug("Validating or downloading library {}", library.name());
            final LibraryDownload download = library.libraryDownload();
            if (download.artifact() == null) continue;
            final Path path = LauncherPaths.getLibrariesDir().resolve(download.artifact().path());
            Files.createDirectories(path.getParent());
            finalLibraryList.add(library.name());
            NetUtils.validateOrDownloadSha1(URI.create(download.artifact().url()), path, download.artifact().sha1());
        }

        return finalLibraryList;
    }
}
