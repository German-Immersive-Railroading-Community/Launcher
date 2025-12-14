package com.troblecodings.launcher.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.ProgressMonitor;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.assets.Assets;
import com.troblecodings.launcher.javafx.Footer;

public class StartupUtil {

	private static final String RELEASE_API = "https://api.github.com/repos/German-Immersive-Railroading-Community/Launcher/releases";
	private static final String BETA_API = "https://girc.eu/Launcher/Beta/beta.json";

	private static String LIBPATHS = "";
	private static String MAINCLASS = null;

	public static final String OSSHORTNAME = getOSShortName();

	private static BetaInfo activeBeta = null;
	public static void setActiveBeta(BetaInfo info) {
		if(activeBeta == info)
			return;

        Launcher.getLogger().info("Changed active beta to {}", info);
		activeBeta = info;
	}

	/**
	 * Gets all currently available pull-request artifacts for GIRC-related mods.
	 * @param refreshBetaData Indicates whether to re-download the beta.json.
	 * @return An array of Strings containing the beta versions of the mod; or null in case of the mod not having any.
	 */
	public static BetaInfo[] getBetaVersions(boolean refreshBetaData) {
		if (!Launcher.getBetaMode())
			return new BetaInfo[0];

		List<BetaInfo> betaInfo = new ArrayList<>();

		try {
			Path betaJsonPath = Paths.get(FileUtil.SETTINGS.baseDir + "/beta.json");

			if (refreshBetaData || !Files.exists(betaJsonPath))
				refreshBetaJson();

			JSONObject root = new JSONObject(new String(Files.readAllBytes(betaJsonPath)));

			root.keySet().forEach(key -> {
				JSONObject mod = root.getJSONObject(key);
				mod.keySet().forEach(pr -> {
					try {
						JSONObject prObj = mod.getJSONObject(pr);
						BetaInfo info = new BetaInfo(key, Integer.parseInt(pr), prObj.getString("name"), prObj.getString("download"), prObj.getInt("port"));
						betaInfo.add(info);
					} catch (Exception ignored) {
					}
				});
			});
		} catch (Exception e) {
			Launcher.getLogger().trace("Could not parse beta.json!", e);
			return new BetaInfo[0];
		}

		return betaInfo.toArray(new BetaInfo[0]);
	}

	public static void refreshBetaJson() {
		if (!Launcher.getBetaMode())
			return;

		if (!ConnectionUtil.download(BETA_API, FileUtil.SETTINGS.baseDir + "/beta.json")) {
			Launcher.getLogger().warn("Could not download beta.json!");
		} else {
			Launcher.getLogger().info("Refreshed beta.json!");
		}
	}

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
			if(versionSplit.length < 2)
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
		if(pathVar == null) {
			pathVar = System.getenv("PATH");
			if(pathVar == null)
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
		Path pth = Paths.get(FileUtil.SETTINGS.baseDir, "servers.dat");
		if(!Files.exists(pth)) {
			try {
				Files.copy(Assets.getResourceAsStream("servers.dat"), pth);
			} catch (IOException e) {
				Launcher.onError(e);
			}
		}
	}

	public static void update() {
		try {
			addServerToData();
			String str = ConnectionUtil.getStringFromURL(RELEASE_API);
			if (str == null) {
				Launcher.getLogger().info("Couldn't read updater information!");
				return;
			}
			JSONArray obj = new JSONArray(str);
			JSONObject newversion = obj.getJSONObject(0).getJSONArray("assets").getJSONObject(0);
			String downloadURL = newversion.getString("browser_download_url");
			File location = new File(StartupUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			if (!location.isFile()) {
				Launcher.getLogger().debug("Dev version no update!");
				return;
			}
			long size = Files.size(Paths.get(location.toURI()));
			long newsize = newversion.getNumber("size").longValue();
			if (newsize == size) {
				Launcher.getLogger().info("The new version ({}) is equal to the old ({})", newsize, size);
				return;
			}
			Launcher.getLogger().info("Updating Launcher!");
			ProgressMonitor progress = new ProgressMonitor(new JButton(), "Downloading update!", "", 0, (int) newsize);
			Path pth = Paths.get(location.toURI());
			Files.copy(pth, Paths.get(pth + ".tmp"), StandardCopyOption.REPLACE_EXISTING);
			OutputStream stream = Files.newOutputStream(pth);
			if (!ConnectionUtil.openConnection(downloadURL, stream,
					bytesize -> progress.setProgress(bytesize.intValue()))) {
				stream.close();
				Files.copy(Paths.get(pth + ".tmp"), pth, StandardCopyOption.REPLACE_EXISTING);
				return;
			}
			stream.close();
			LogManager.shutdown(false, true);
			ProcessBuilder builder = new ProcessBuilder("java", "-jar", location.toString());
			builder.redirectError(Redirect.INHERIT);
			builder.redirectOutput(Redirect.INHERIT);
			System.exit(builder.start().waitFor());
		} catch (Throwable e) {
			Launcher.onError(e);
		}
	}

	@SuppressWarnings("unchecked")
	private static String[] prestart() {
		try {
			addServerToData();
			String clientJson = FileUtil.SETTINGS.baseDir + "/GIR.json";
			ConnectionUtil.download("https://girc.eu/Launcher/GIR.json", clientJson);

			String content = new String(Files.readAllBytes(Paths.get(clientJson)));
			JSONObject object;
			object = new JSONObject(content);

			MAINCLASS = object.getString("mainClass");

			// This part downloads the texture index and so on
			JSONObject assetIndex = object.getJSONObject("assetIndex");

			Path indexes = Paths.get(FileUtil.ASSET_DIR + "/indexes");
			if (!Files.exists(indexes))
				Files.createDirectories(indexes);

			String indexpath = indexes.toString() + "/" + assetIndex.getString("id") + ".json";
			String indexurl = assetIndex.getString("url");
			String indexsha1 = assetIndex.getString("sha1");
			ConnectionUtil.validateDownloadRetry(indexurl, indexpath, indexsha1);

			Path ogMC = Paths.get(FileUtil.SETTINGS.baseDir + "/versions/" + object.getString("inheritsFrom") + "/"
					+ object.getString("inheritsFrom") + ".jar");
			Files.createDirectories(ogMC.getParent());
			JSONObject clientDL = object.getJSONObject("downloads").getJSONObject("client");
			ConnectionUtil.validateDownloadRetry(clientDL.getString("url"), ogMC.toString(),
					clientDL.getString("sha1"));
			LIBPATHS = ogMC.toString() + ";";

			JSONObject additional = object.getJSONObject("additional");
			JSONArray arr = object.getJSONArray("libraries");
			JSONArray optional = object.getJSONArray("optionalMods");
			Path ojectspath = Paths.get(FileUtil.ASSET_DIR + "/objects");
			if (!Files.exists(ojectspath))
				Files.createDirectories(ojectspath);

			JSONTokener tokener = new JSONTokener(Files.newInputStream(Paths.get(indexpath)));
			JSONObject index = new JSONObject(tokener);
			JSONObject objects = index.getJSONObject("objects");

			final double maxItems = objects.keySet().size() + arr.length();
			AtomicInteger counter = new AtomicInteger();

			// This part is to download the libs
			for (Object libentry : arr) {
				JSONObject libobj = (JSONObject) libentry;
				JSONObject downloadobj = libobj.getJSONObject("downloads");
				if (downloadobj.has("artifact")) {
					JSONObject artifact = downloadobj.getJSONObject("artifact");
					String url = artifact.getString("url");
					String name = FileUtil.LIB_DIR + "/" + artifact.getString("path");
					String sha1 = artifact.getString("sha1");

					LIBPATHS += name + ";";
					ConnectionUtil.validateDownloadRetry(url, name, sha1);
				} else {
					JSONObject natives = libobj.getJSONObject("natives");
					if (natives.has(OSSHORTNAME)) {
						String nativekey = natives.getString(OSSHORTNAME).replace("${arch}",
								System.getProperty("sun.arch.data.model"));
						JSONObject classifier = downloadobj.getJSONObject("classifiers");
						JSONObject artifact = classifier.getJSONObject(nativekey);
						String url = artifact.getString("url");
						String name = FileUtil.LIB_DIR + "/" + artifact.getString("path");
						String sha1 = artifact.getString("sha1");
						ConnectionUtil.validateDownloadRetry(url, name, sha1);

						// Extract the natives
						unzip(name, FileUtil.LIB_DIR);
					}
				}
				Footer.setProgress(counter.addAndGet(1) / maxItems);
			}

			// Asset lockup and download
			String baseurl = "https://resources.download.minecraft.net/";
			objects.keySet().forEach((key) -> {
				try {
					JSONObject asset = objects.getJSONObject(key);
					String hash = asset.getString("hash");
					String folder = hash.substring(0, 2);
					Path folderpath = Paths.get(ojectspath.toString() + "/" + folder);
					if (!Files.exists(folderpath)) {
						Files.createDirectories(folderpath);
					}
					ConnectionUtil.validateDownloadRetry(baseurl + folder + "/" + hash,
							folderpath.toString() + "/" + hash, hash);
				} catch (Throwable e) {
					Launcher.onError(e);
				}
				Footer.setProgress(counter.addAndGet(1) / maxItems);
			});

			counter.set(0);
			final double max = object.getLong("wholeSize");
			additional.keySet().forEach(key -> {
				additional.getJSONArray(key).forEach(fileobj -> {
					JSONObject jfileobj = (JSONObject) fileobj;
					String name = jfileobj.getString("name");

					Path path;

					if (activeBeta != null && name.toLowerCase().contains(activeBeta.getModName())) {
						try {
							if(Files.deleteIfExists(Paths.get(FileUtil.SETTINGS.baseDir, key, name)))
								Launcher.getLogger().info("Deleted {} in favour of {}!", name, activeBeta.getJarFileName());
						} catch (IOException e) {
							Launcher.getLogger().trace("Failed to delete normal mod file for beta mod: " + activeBeta.toString() + "!", e);
						}

						path = Paths.get(FileUtil.SETTINGS.baseDir, key, activeBeta.getJarFileName());
						ConnectionUtil.download(activeBeta.getPrDownload(), path.toString(), in -> Footer.setProgress((counter.get() + in) / max));
					} else {
						path = Paths.get(FileUtil.SETTINGS.baseDir, key, name);
						ConnectionUtil.validateDownloadRetry(jfileobj.getString("url"), path.toString(),
								jfileobj.getString("sha1"), in -> Footer.setProgress((counter.get() + in) / max));
					}

					counter.getAndAdd(jfileobj.getInt("size"));
				});
			});

			Path optionalMods = Paths.get(FileUtil.SETTINGS.baseDir, "optional-mods");
			Files.createDirectories(optionalMods);

			additional.keySet().forEach(key -> {
				try {
					List<Object> array = additional.getJSONArray(key).toList();

					if(activeBeta != null) {
						array.add(activeBeta.getJarFileName());
					}

					Files.list(optionalMods).filter(file -> file.toString().endsWith(".jar")).forEach(file -> {
						Path filePath = Paths.get(FileUtil.SETTINGS.baseDir, "mods", file.getFileName().toString());
						array.add(filePath.toString());
					});

					Files.list(Paths.get(FileUtil.SETTINGS.baseDir, key)).filter(incom -> {
						String filename = incom.getFileName().toString();
						return Files.isRegularFile(incom) && array.stream().noneMatch(job -> job.toString().contains(filename));
					}).forEach(t -> {
                        Launcher.getLogger().debug("Deleted file {}", t);
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

			Path additionalMods = Paths.get(FileUtil.SETTINGS.baseDir, "additional-mods");
			Files.createDirectories(additionalMods);
			Files.list(additionalMods).filter(pth -> !pth.toString().endsWith(".dis")).forEach(pth -> {
				try {
					Path path = Paths.get(pth.toString().replace("additional-mods", "mods"));

					if(Files.exists(path))
						return;

					Files.copy(pth, path);
				} catch (IOException e) {
					Launcher.onError(e);
				}
			});


			for(Object optionalObj : optional) {
				JSONObject optionalJsonObj = (JSONObject) optionalObj;
				Path optionalFilesPath = Paths.get(optionalMods.toString(), optionalJsonObj.getString("name"));
				ConnectionUtil.validateDownloadRetry(optionalJsonObj.getString("url"), optionalFilesPath.toString(), optionalJsonObj.getString("sha1"));
			}

			Footer.setProgress(0.001);
			return AuthUtil.make(object);
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
		String javaVersionPath = FileUtil.SETTINGS.javaPath;
		if (javaVersionPath.isEmpty()) {
			Optional<String> javaVers = findJavaVersion();
			if (!javaVers.isPresent()) {
				javaVersionPath = "java";
				Launcher.getLogger().warn("Java version not found! Falling back!");
			} else {
				javaVersionPath = javaVers.get() + "/";
			}
		}

        if(!Files.exists(Paths.get(javaVersionPath)) || !javaVersionPath.endsWith("java.exe"))
            javaVersionPath = "java";

		String[] parameter = prestart();
		if (parameter == null)
			return null;

		String width = String.valueOf(FileUtil.SETTINGS.width);
		String height = String.valueOf(FileUtil.SETTINGS.height);
		String ram = String.valueOf(FileUtil.SETTINGS.ram);
		String[] preparameter = new String[] { javaVersionPath, "-Xmx" + ram + "M", "-Xms" + ram + "M",
				"-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump",
				"-Djava.library.path=" + FileUtil.LIB_DIR, "-cp", LIBPATHS, MAINCLASS, "-width", width, "-height",
				height };
		ProcessBuilder builder = new ProcessBuilder(
				Stream.concat(Arrays.stream(preparameter), Arrays.stream(parameter)).toArray(String[]::new));
		builder.directory(new File(FileUtil.SETTINGS.baseDir));
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
