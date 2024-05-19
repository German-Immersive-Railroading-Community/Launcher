package eu.girc.launcher;

import com.google.gson.reflect.TypeToken;
import eu.girc.launcher.models.AdditionalObject;
import eu.girc.launcher.models.AssetIndex;
import eu.girc.launcher.models.GirJson;
import eu.girc.launcher.models.LibraryArtifact;
import eu.girc.launcher.models.LibraryAsset;
import eu.girc.launcher.models.LibraryDownload;
import eu.girc.launcher.models.MojangAsset;
import eu.girc.launcher.models.MojangAssets;
import eu.girc.launcher.models.MojangDownload;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

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

        GirJson girJson = Globals.GSON.fromJson(Files.readString(optGirJson.get()), GirJson.class);
        List<String> libraries = prepareStart(girJson);
        if (libraries.isEmpty()) {
            logger.error("No libraries to add to the classpath were found!");
            return Optional.empty();
        }

        // TODO: Add jvm args customisation to settings
        final String[] javaArgs = new String[] {
                javaw.toString(),
                "-Xmx8G",
                "-Xms8G",
                "-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump",
                "-Djava.library.path=" + LauncherPaths.getLibrariesDir(),
                "-cp",
                String.join(";", libraries),
                girJson.mainClass(),
                "-width",
                "1280",
                "-height",
                "720"
        };

        final String[] minecraftArgs = make_minecraft_args(girJson);

        for (final String arg : javaArgs) {
            logger.debug("Java arg: {}", arg);
        }

        for (final String arg : minecraftArgs) {
            logger.debug("Minecraft arg: {}", arg);
        }

        final String[] finalArgs = Stream.concat(Arrays.stream(javaArgs), Arrays.stream(minecraftArgs)).toArray(String[]::new);

        ProcessBuilder builder = new ProcessBuilder(finalArgs).directory(LauncherPaths.getConfigDir().toFile()).redirectError(ProcessBuilder.Redirect.INHERIT).redirectOutput(ProcessBuilder.Redirect.INHERIT);
        return Optional.of(builder.start());
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
        final Path clientPath = downloadMinecraft(girJson);
        downloadMinecraftAssets(girJson);
        downloadAdditionalAssets(girJson);
        final List<String> libs = downloadLibraries(girJson);
        libs.add(clientPath.toString());
        return libs;
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
        Files.createDirectories(temurinBasePath);
        logger.debug("Resolving Adoptium Temurin v8 manifest");
        TypeToken<List<AdoptiumAsset>> adoptiumAssetTypeToken = new TypeToken<>() { };
        final Optional<List<AdoptiumAsset>> optAssets = NetUtils.downloadJson(adoptiumApiJavaEndpoint, adoptiumAssetTypeToken);
        List<AdoptiumAsset> assets = optAssets.orElseThrow();

        if (assets.isEmpty()) {
            throw new RuntimeException("Adoptium API request returned no assets");
        }

        logger.debug("Starting validation and/or download of Adoptium Temurin v8");
        AdoptiumAsset asset = assets.getFirst();
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

    private static Path downloadMinecraft(GirJson girJson) throws IOException, InterruptedException {
        final Path minecraftPath = LauncherPaths.getVersionsDir().resolve(girJson.inheritsFrom()).resolve(girJson.inheritsFrom() + ".jar");
        Files.createDirectories(minecraftPath.getParent());
        final MojangDownload clientDl = girJson.downloads().client();
        NetUtils.validateOrDownloadSha1(URI.create(clientDl.url()), minecraftPath, clientDl.sha1());
        return minecraftPath;
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
        final Optional<Path> maybeAssetIndexFile = NetUtils.validateOrDownloadSha1(URI.create(assetIndex.url()), indexesBaseDir.resolve(assetIndex.id() + ".json"), assetIndex.sha1());

        if (maybeAssetIndexFile.isEmpty()) {
            throw new IOException("AssetIndex download failed.");
        }

        final Path assetIndexFile = maybeAssetIndexFile.get();
        final MojangAssets mojAssets = Globals.GSON.fromJson(Files.readString(assetIndexFile), MojangAssets.class);
        final Map<String, MojangAsset> objects = mojAssets.objects();

        for (final String key : objects.keySet()) {
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

        for (final LibraryAsset library : libraries) {
            logger.debug("Validating or downloading library {}", library.name());
            final LibraryDownload download = library.libraryDownload();
            if (download.artifact() != null) {
                final Path path = LauncherPaths.getLibrariesDir().resolve(download.artifact().path());
                Files.createDirectories(path.getParent());
                finalLibraryList.add(path.toString());
                NetUtils.validateOrDownloadSha1(URI.create(download.artifact().url()), path, download.artifact().sha1());
            } else if (library.natives() != null && download.classifiers() != null) {
                final Map<String, String> natives = library.natives();
                final String osShorthand = getOSShorthand();

                if (!natives.containsKey(osShorthand)) {
                    logger.debug("Library {} no native with OS shorthand {}, skipping", library.name(), osShorthand);
                    continue;
                }

                final String nativeKey = natives.get(osShorthand);
                final Map<String, LibraryArtifact> classifiers = download.classifiers();
                if (!classifiers.containsKey(nativeKey)) {
                    logger.debug("Classifiers do not contain key {}, skipping", nativeKey);
                    continue;
                }

                final LibraryArtifact classifier = classifiers.get(nativeKey);
                final Path path = LauncherPaths.getLibrariesDir().resolve(classifier.path());
                Files.createDirectories(path.getParent());
                finalLibraryList.add(path.toString());
                NetUtils.validateOrDownloadSha1(URI.create(classifier.url()), path, classifier.sha1());

                // Extract natives
                LauncherUtils.unzipJar(path, LauncherPaths.getLibrariesDir());
            } else {
                logger.debug("Library {} has no natives, artifacts, or classifiers inside of the library download, skipping", library.name());
            }
        }

        return finalLibraryList;
    }

    private static void downloadAdditionalAssets(GirJson girJson) throws IOException, InterruptedException {
        final Path modsDir = LauncherPaths.getModsDir();
        Files.createDirectories(modsDir);
        final List<AdditionalObject> mods = girJson.additionalAssets().mods();
        for (final AdditionalObject mod : mods) {
            logger.debug("Validating or downloading mod {}", mod.name());
            final Path path = modsDir.resolve(mod.name());
            NetUtils.validateOrDownloadSha256(URI.create(mod.url()), path, mod.sha256());
        }

        final Path modConfigsDir = LauncherPaths.getModConfigsDir();
        Files.createDirectories(modConfigsDir);
        final List<AdditionalObject> configs = girJson.additionalAssets().config();
        for (final AdditionalObject config : configs) {
            logger.debug("Validating or downloading config {}", config.name());
            final Path path = modConfigsDir.resolve(config.name());
            NetUtils.validateOrDownloadSha256(URI.create(config.url()), path, config.sha256());
        }

        final Path irConfigsDir = modConfigsDir.resolve("immersiverailroading");
        Files.createDirectories(irConfigsDir);
        final List<AdditionalObject> irConfigs = girJson.additionalAssets().irConfig();
        for (final AdditionalObject irConfig : irConfigs) {
            logger.debug("Validating or downloading IR-config {}", irConfig.name());
            final Path path = irConfigsDir.resolve(irConfig.name());
            NetUtils.validateOrDownloadSha256(URI.create(irConfig.url()), path, irConfig.sha256());
        }

        final Path resourcePacksDir = LauncherPaths.getResourcePacksDir();
        Files.createDirectories(resourcePacksDir);
        final List<AdditionalObject> resourcePacks = girJson.additionalAssets().resourcePacks();
        for (final AdditionalObject resourcePack : resourcePacks) {
            logger.debug("Validating or downloading resource pack {}", resourcePack.name());
            final Path path = resourcePacksDir.resolve(resourcePack.name());
            NetUtils.validateOrDownloadSha256(URI.create(resourcePack.url()), path, resourcePack.sha256());
        }

        final Path contentPacksDir = LauncherPaths.getContentPacksDir().resolve("opensignals");
        Files.createDirectories(contentPacksDir);
        final List<AdditionalObject> contentPacks = girJson.additionalAssets().contentPacks();
        for (final AdditionalObject contentPack : contentPacks) {
            logger.debug("Validating or downloading OpenSignals-ContentPack {}", contentPack.name());
            final Path path = contentPacksDir.resolve(contentPack.name());
            NetUtils.validateOrDownloadSha256(URI.create(contentPack.url()), path, contentPack.sha256());
        }
    }

    private static String[] make_minecraft_args(GirJson girJson) {
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
}
