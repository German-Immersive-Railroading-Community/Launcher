package com.troblecodings.launcher.util;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.assets.Assets;
import com.troblecodings.launcher.javafx.Footer;
import com.troblecodings.launcher.models.girjson.*;
import com.troblecodings.launcher.models.minecraft.MinecraftInfo;
import com.troblecodings.launcher.models.minecraft.MinecraftObject;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class StartupUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupUtil.class);

    private static final String RELEASE_API = "https://api.github.com/repos/German-Immersive-Railroading-Community/Launcher/releases";
    private static final String BETA_API = "https://girc.eu/Launcher/Beta/beta.json";

    private static String LIBPATHS = "";
    private static String MAINCLASS = null;

    public static final String OSSHORTNAME = getOSShortName();

    private static BetaInfo activeBeta = null;

    public static void setActiveBeta(BetaInfo info) {
        if (activeBeta == info)
            return;

        LOGGER.info("Changed active beta to {}", info);
        activeBeta = info;
    }

    // TODO: re-add
//	/**
//	 * Gets all currently available pull-request artifacts for GIRC-related mods.
//	 * @param refreshBetaData Indicates whether to re-download the beta.json.
//	 * @return An array of Strings containing the beta versions of the mod; or null in case of the mod not having any.
//	 */
//	public static BetaInfo[] getBetaVersions(boolean refreshBetaData) {
//		if (!Launcher.getBetaMode())
//			return new BetaInfo[0];
//
//		List<BetaInfo> betaInfo = new ArrayList<>();
//
//		try {
//			Path betaJsonPath = Paths.get(FileUtil.SETTINGS.baseDir + "/beta.json");
//
//			if (refreshBetaData || !Files.exists(betaJsonPath))
//				refreshBetaJson();
//
//			JSONObject root = new JSONObject(new String(Files.readAllBytes(betaJsonPath)));
//
//			root.keySet().forEach(key -> {
//				JSONObject mod = root.getJSONObject(key);
//				mod.keySet().forEach(pr -> {
//					try {
//						JSONObject prObj = mod.getJSONObject(pr);
//						BetaInfo info = new BetaInfo(key, Integer.parseInt(pr), prObj.getString("name"), prObj.getString("download"), prObj.getInt("port"));
//						betaInfo.add(info);
//					} catch (Exception ignored) {
//					}
//				});
//			});
//		} catch (Exception e) {
//			LOGGER.trace("Could not parse beta.json!", e);
//			return new BetaInfo[0];
//		}
//
//		return betaInfo.toArray(new BetaInfo[0]);
//	}

    private static String getOSShortName() {
        String longname = System.getProperty("os.name").toLowerCase();
        if (longname.startsWith("win")) {
            return "windows";
        } else if (longname.startsWith("mac")) {
            return "osx";
        } else if (longname.startsWith("linux")) {
            return "linux";
        }
        return "unknown";
    }

    public static boolean isJavaAnd8(Path pathToDictionary) {
        final Path pathtoJava;
        // TODO: MacOS requires a test here.
        if (SystemUtils.IS_OS_WINDOWS) {
            pathtoJava = pathToDictionary.resolve("java.exe");
        } else if (SystemUtils.IS_OS_UNIX) {
            pathtoJava = pathToDictionary.resolve("java");
        } else {
            throw new UnsupportedOperationException("This Operating System is not supported");
        }

        if (Files.notExists(pathtoJava))
            return false;
        try {
            final ProcessBuilder builder = new ProcessBuilder(pathtoJava.toString(), "-version");
            builder.redirectErrorStream(true);
            final Process process = builder.start();
            final Scanner scanner = new Scanner(process.getInputStream());
            if (!scanner.hasNextLine())
                return false;
            final String version = scanner.nextLine();
            scanner.close();
            final String[] versionSplit = version.split("\"");
            if (versionSplit.length < 2)
                return false;
            if (versionSplit[1].startsWith("1.8.0"))
                return true;
        } catch (Exception e) {
            Launcher.onError(e);
        }
        return false;
    }

    public static Optional<String> findJavaVersion() {
        String pathVar = System.getenv("path");
        if (pathVar == null) {
            pathVar = System.getenv("PATH");
            if (pathVar == null)
                pathVar = "";
        }
        Optional<String> opt1 = Arrays.stream(pathVar.split(";")).filter(str -> {
            Path pathto = Paths.get(str);
            if (Files.notExists(pathto))
                return false;
            return isJavaAnd8(pathto);
        }).findFirst();
        if (opt1.isPresent())
            return opt1;
        String home = System.getenv("JAVA_HOME");
        if (home != null) {
            Path pathToHome = Paths.get(home).resolve("bin");
            if (isJavaAnd8(pathToHome))
                return Optional.of(pathToHome.toString());
        }
        return Optional.empty();
    }

    private static void addServerToData() {
        Path pth = LauncherPaths.getGameDataDir().resolve("servers.dat");
        if (!Files.exists(pth)) {
            try {
                Files.copy(Assets.getResourceAsStream("servers.dat"), pth);
            } catch (IOException e) {
                Launcher.onError(e);
            }
        }
    }

//	public static void update() {
//		try {
//			addServerToData();
//			String str = ConnectionUtil.getStringFromURL(RELEASE_API);
//			if (str == null) {
//				LOGGER.info("Couldn't read updater information!");
//				return;
//			}
//			JSONArray obj = new JSONArray(str);
//			JSONObject newversion = obj.getJSONObject(0).getJSONArray("assets").getJSONObject(0);
//			String downloadURL = newversion.getString("browser_download_url");
//			File location = new File(StartupUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI());
//			if (!location.isFile()) {
//				LOGGER.debug("Dev version no update!");
//				return;
//			}
//			long size = Files.size(Paths.get(location.toURI()));
//			long newsize = newversion.getNumber("size").longValue();
//			if (newsize == size) {
//				LOGGER.info("The new version ({}) is equal to the old ({})", newsize, size);
//				return;
//			}
//			LOGGER.info("Updating Launcher!");
//			ProgressMonitor progress = new ProgressMonitor(new JButton(), "Downloading update!", "", 0, (int) newsize);
//			Path pth = Paths.get(location.toURI());
//			Files.copy(pth, Paths.get(pth + ".tmp"), StandardCopyOption.REPLACE_EXISTING);
//			OutputStream stream = Files.newOutputStream(pth);
//			if (!ConnectionUtil.openConnection(downloadURL, stream,
//					bytesize -> progress.setProgress(bytesize.intValue()))) {
//				stream.close();
//				Files.copy(Paths.get(pth + ".tmp"), pth, StandardCopyOption.REPLACE_EXISTING);
//				return;
//			}
//			stream.close();
//			LogManager.shutdown(false, true);
//			ProcessBuilder builder = new ProcessBuilder("java", "-jar", location.toString());
//			builder.redirectError(Redirect.INHERIT);
//			builder.redirectOutput(Redirect.INHERIT);
//			System.exit(builder.start().waitFor());
//		} catch (Throwable e) {
//			Launcher.onError(e);
//		}
//	}

    @SuppressWarnings("unchecked")
    private static String[] prestart() {
        try {
            addServerToData();
            String clientJson = LauncherPaths.getGirJsonFile().toString();
            ConnectionUtil.download("https://girc.eu/Launcher/GIR.json", clientJson);

            String content = new String(Files.readAllBytes(Paths.get(clientJson)));
            final GirJson json = Launcher.GSON.fromJson(content, GirJson.class);

            MAINCLASS = json.mainClass();

            // Step 1: Download texture indices
            Path indices = LauncherPaths.getAssetsDir().resolve("indexes");
            if (!Files.exists(indices))
                Files.createDirectories(indices);

            final var assetIndex = json.assetIndex();
            String indexpath = indices + "/" + assetIndex.id() + ".json";
            ConnectionUtil.validateDownloadRetry(assetIndex.url(), indexpath, assetIndex.sha1());

            Path ogMC = LauncherPaths.getGameDataDir().resolve("versions").resolve(json.inheritsFrom()).resolve(json.inheritsFrom() + ".jar");
            Files.createDirectories(ogMC.getParent());

            final DownloadInfo clientDl = json.downloads().get("client");
            ConnectionUtil.validateDownloadRetry(clientDl.url(), ogMC.toString(), clientDl.sha1());

            LIBPATHS = ogMC + ";";

            final Map<String, List<AdditionalArtifact>> additional = json.additionalArtifacts();
            final var libraries = json.libraries();
            final var optionalMods = json.optionalMods();

            Path objectsPath = LauncherPaths.getAssetsDir().resolve("objects");
            if (!Files.exists(objectsPath))
                Files.createDirectories(objectsPath);

            final MinecraftInfo mcInfo = Launcher.GSON.fromJson(Files.newBufferedReader(Paths.get(indexpath)), MinecraftInfo.class);
            final Map<String, MinecraftObject> mcObjects = mcInfo.mcObjects();

            final double maxItems = mcObjects.size() + libraries.size();
            AtomicInteger counter = new AtomicInteger();

            // This part is to download the libs
            for (final LibraryArtifactInfo lib : libraries) {
                final LibraryArtifactDownloadInfo downloadInfo = lib.downloadInfo();

                if (downloadInfo.artifactDownload() != null) {
                    final LibraryArtifactDownload artifactDownload = downloadInfo.artifactDownload();
                    String url = artifactDownload.url();
                    String name = LauncherPaths.getLibrariesDir().resolve(artifactDownload.path()).toString();
                    String sha1 = artifactDownload.sha1();

                    LIBPATHS += name + ";";
                    ConnectionUtil.validateDownloadRetry(url, name, sha1);
                } else if (lib.natives() != null && downloadInfo.classifiers() != null) {
                    final Map<String, String> natives = lib.natives();
                    final String osShorthand = getOSShortName();

                    if (!natives.containsKey(osShorthand)) {
                        LOGGER.debug("Library {} no native with OS shorthand {}, skipping", lib.name(), osShorthand);
                        continue;
                    }

                    final String nativeKey = natives.get(osShorthand);
                    final Map<String, LibraryArtifactDownload> classifiers = downloadInfo.classifiers();
                    if (!classifiers.containsKey(nativeKey)) {
                        LOGGER.debug("Classifiers do not contain key {}, skipping", nativeKey);
                        continue;
                    }

                    final LibraryArtifactDownload artifact = classifiers.get(nativeKey);
                    String name = LauncherPaths.getLibrariesDir().resolve(artifact.path()).toString();
                    ConnectionUtil.validateDownloadRetry(artifact.url(), name, artifact.sha1());

                    // Extract the natives
                    unzip(name, LauncherPaths.getLibrariesDir().toString());
                } else {
                    LOGGER.debug("Library {} has no natives, artifacts, or classifiers inside of the library download, skipping", lib.name());
                }

                Footer.setProgress(counter.addAndGet(1) / maxItems);
            }

            // Asset lockup and download
            final String baseurl = "https://resources.download.minecraft.net/";

            mcObjects.forEach((key, obj) -> {
                try {
                    String hash = obj.hash();
                    String folder = obj.folder();
                    Path folderpath = Paths.get(objectsPath + "/" + folder);

                    if (!Files.exists(folderpath)) {
                        Files.createDirectories(folderpath);
                    }

                    ConnectionUtil.validateDownloadRetry(baseurl + folder + "/" + hash, folderpath + "/" + hash, hash);
                } catch (Throwable e) {
                    Launcher.onError(e);
                }
                Footer.setProgress(counter.addAndGet(1) / maxItems);
            });

            counter.set(0);
            final double max = json.totalSize();
            additional.forEach((key, artifacts) -> {
                artifacts.forEach(artifact -> {
                    Path path;
                    String name = artifact.name();

                    if (activeBeta != null && name.toLowerCase().contains(activeBeta.getModName())) {
                        try {
                            if (Files.deleteIfExists(LauncherPaths.getGameDataDir().resolve(key).resolve(name)))
                                LOGGER.info("Deleted {} in favour of {}!", name, activeBeta.getJarFileName());
                        } catch (IOException e) {
                            LOGGER.trace("Failed to delete normal mod file for beta mod: {}!", activeBeta.toString(), e);
                        }

                        path = LauncherPaths.getGameDataDir().resolve(key).resolve(activeBeta.getJarFileName());
                        ConnectionUtil.download(activeBeta.getPrDownload(), path.toString(), in -> Footer.setProgress((counter.get() + in) / max));
                    } else {
                        path = LauncherPaths.getGameDataDir().resolve(key).resolve(name);
                        ConnectionUtil.validateDownloadRetry(artifact.url(), path.toString(), artifact.sha1(), in -> Footer.setProgress((counter.get() + in) / max));
                    }

                    counter.getAndAdd(artifact.size());
                });
            });

            Path optionalModsPath = LauncherPaths.getGameDataDir().resolve("optional-mods");
            Files.createDirectories(optionalModsPath);

            additional.forEach((key, artifacts) -> {
                try {

                    List<String> validFiles = new ArrayList<>(artifacts.stream().map(AdditionalArtifact::name).toList());

                    if (activeBeta != null) {
                        validFiles.add(activeBeta.getJarFileName());
                    }

                    Files.list(optionalModsPath).filter(file -> file.toString().endsWith(".jar")).forEach(file -> {
                        Path filePath = LauncherPaths.getGameDataDir().resolve("mods").resolve(file.getFileName().toString());
                        validFiles.add(filePath.toString());
                    });

                    Files.list(LauncherPaths.getGameDataDir().resolve(key)).filter(incom -> {
                        String filename = incom.getFileName().toString();
                        return Files.isRegularFile(incom) && validFiles.stream().noneMatch(job -> job.toString().contains(filename));
                    }).forEach(t -> {
                        LOGGER.debug("Deleted file {}", t);
                        try { // I hate this language
                            Files.deleteIfExists(t);
                        } catch (IOException e) {
                            Launcher.onError(e);
                        }
                    });
                } catch (IOException e) {
                    Launcher.onError(e);
                }
            });

            Path additionalMods = LauncherPaths.getGameDataDir().resolve("additional-mods");
            Files.createDirectories(additionalMods);
            Files.list(additionalMods).filter(pth -> !pth.toString().endsWith(".dis")).forEach(pth -> {
                try {
                    Path path = Paths.get(pth.toString().replace("additional-mods", "mods"));

                    if (Files.exists(path))
                        return;

                    Files.copy(pth, path);
                } catch (IOException e) {
                    Launcher.onError(e);
                }
            });


            for (final OptionalMod optionalMod : optionalMods) {
                Path optionalFilesPath = optionalModsPath.resolve(optionalMod.name());
                ConnectionUtil.validateDownloadRetry(optionalMod.url(), optionalFilesPath.toString(), optionalMod.sha1());
            }

            Path serverDatPath = LauncherPaths.getGameDataDir().resolve("servers.dat");
            NamedTag rootTag = NBTUtil.read(serverDatPath.toString());
            CompoundTag rootCt = (CompoundTag) rootTag.getTag();
            ListTag<CompoundTag> serverListTag = (ListTag<CompoundTag>) rootCt.get("servers");

            if (serverListTag.size() > 1) {
                while (serverListTag.size() > 1)
                    serverListTag.remove(1);
            }

            NBTUtil.write(rootTag, serverDatPath.toString(), false);

            Footer.setProgress(0.001);
            return Launcher.getInstance().getUserService().makeArguments(json);
        } catch (Throwable e) {
            Launcher.onError(e);
            return null;
        }
    }

    private static void unzip(String name, String base) throws Throwable {
        String str = name.replace("\\", "/");
        JarFile file = new JarFile(str);
        Enumeration<JarEntry> enumerator = file.entries();
        while (enumerator.hasMoreElements()) {
            JarEntry entry = enumerator.nextElement();
            File outfile = new File(base + "/" + entry.getName());

            if (entry.isDirectory()) {
                Files.createDirectories(outfile.toPath());
                continue;
            }

            Files.createDirectories(outfile.getParentFile().toPath());

            ReadableByteChannel readchannel = Channels.newChannel(file.getInputStream(entry));
            FileOutputStream fos = new FileOutputStream(outfile);
            fos.getChannel().transferFrom(readchannel, 0, Long.MAX_VALUE);
            fos.close();
        }
        file.close();
    }

    public static Process start() {
        String javaVersionPath = Launcher.getInstance().getAppSettings().getCustomJrePath();
        if (javaVersionPath.isEmpty()) {
            Optional<String> javaVers = findJavaVersion();
            if (javaVers.isEmpty()) {
                javaVersionPath = "java";
                LOGGER.warn("Java version not found! Falling back!");
            } else {
                javaVersionPath = javaVers.get() + "/";
            }
        }

        if (!Files.exists(Paths.get(javaVersionPath)) || !javaVersionPath.endsWith("java.exe"))
            javaVersionPath = "java";

        String[] parameter = prestart();
        if (parameter == null)
            return null;

        String width = String.valueOf(Launcher.getInstance().getAppSettings().getWidth());
        String height = String.valueOf(Launcher.getInstance().getAppSettings().getHeight());
        String ram = String.valueOf(Launcher.getInstance().getAppSettings().getMemory());
        String[] preparameter = new String[]{javaVersionPath, "-Xmx" + ram + "M", "-Xms" + ram + "M",
                "-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump",
                "-Djava.library.path=" + LauncherPaths.getLibrariesDir(), "-cp", LIBPATHS, MAINCLASS, "-width", width, "-height",
                height};
        ProcessBuilder builder = new ProcessBuilder(
                Stream.concat(Arrays.stream(preparameter), Arrays.stream(parameter)).toArray(String[]::new));
        builder.directory(LauncherPaths.getGameDataDir().toFile());
        builder.redirectError(Redirect.INHERIT);
        builder.redirectOutput(Redirect.INHERIT);
        try {
            return builder.start();
        } catch (IOException e) {
            Launcher.onError(e);
        }
        return null;
    }
}
