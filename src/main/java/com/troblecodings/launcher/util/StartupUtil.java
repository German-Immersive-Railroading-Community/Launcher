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
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.ProgressMonitor;

import org.apache.logging.log4j.LogManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.assets.Assets;
import com.troblecodings.launcher.javafx.Footer;

import mslinks.ShellLink;
import mslinks.ShellLinkException;

public class StartupUtil {

	private static final String RELEASE_API = "https://api.github.com/repos/German-Immersive-Railroading-Community/Launcher/releases";

	private static String LIBPATHS = "";
	private static String MAINCLASS = null;

	public static final String OSSHORTNAME = getOSShortName();

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
		Path pathtoJava = pathToDictionary.resolve("java.exe");
		if (Files.notExists(pathtoJava))
			return false;
		try {
			ProcessBuilder builder = new ProcessBuilder(pathtoJava.toString(), "-version");
			builder.redirectErrorStream(true);
			Process process = builder.start();
			Scanner scanner = new Scanner(process.getInputStream());
			if (!scanner.hasNextLine())
				return false;
			String version = scanner.nextLine();
			scanner.close();
			if (version.split("\"")[1].startsWith("1.8.0"))
				return true;
		} catch (Exception e) {
			Launcher.onError(e);
		}
		return false;
	}

	public static Optional<String> findJavaVersion() {
		Optional<String> opt1 = Arrays.stream(System.getenv("path").split(";")).filter(str -> {
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
		// This is just fallback
		Path commonStartup = Paths.get("C:\\ProgramData\\Microsoft\\Windows\\Start Menu\\Programs\\");
		Path userStartup = Paths.get("%appdata%\\Microsoft\\Windows\\Start Menu\\Programs\\");
		try {
			Predicate<Path> pathPred = pth -> {
				try {
					if (!Files.isRegularFile(pth) || !pth.toString().endsWith(".lnk"))
						return false;
					Path path = Paths.get(new ShellLink(pth).resolveTarget()).getParent();
					Launcher.getLogger().info(path.toString());
					return isJavaAnd8(path);
				} catch (IOException e) {
					Launcher.onError(e);
				} catch (ShellLinkException e) {
					Launcher.onError(e);
				}
				return false;
			};
			Optional<Path> opt2 = Files.walk(commonStartup).filter(pathPred).findFirst();
			if (opt2.isPresent())
				return Optional.of(opt2.get().getParent().toString());

			Optional<Path> opt3 = Files.walk(userStartup).filter(pathPred).findFirst();
			if (opt3.isPresent())
				return Optional.of(opt3.get().getParent().toString());
		} catch (IOException e) {
			Launcher.onError(e);
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
				Launcher.getLogger().info("The new verision (%d) is equal to the old (%d)", newsize, size);
				return;
			}
			Launcher.getLogger().info("Updating Launcher!");
			ProgressMonitor progress = new ProgressMonitor(new JButton(), "Downloading update!", "", 0, (int) newsize);
			Path pth = Paths.get(location.toURI());
			Files.copy(pth, Paths.get(pth.toString() + ".tmp"), StandardCopyOption.REPLACE_EXISTING);
			OutputStream stream = Files.newOutputStream(pth);
			if (!ConnectionUtil.openConnection(downloadURL, stream,
					bytesize -> progress.setProgress(bytesize.intValue()))) {
				stream.close();
				Files.copy(Paths.get(pth.toString() + ".tmp"), pth, StandardCopyOption.REPLACE_EXISTING);
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
			String baseurl = "http://resources.download.minecraft.net/";
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
					Path path = Paths.get(FileUtil.SETTINGS.baseDir, key, jfileobj.getString("name"));
					ConnectionUtil.validateDownloadRetry(jfileobj.getString("url"), path.toString(),
							jfileobj.getString("sha1"), in -> Footer.setProgress((counter.get() + in) / max));
					counter.getAndAdd(jfileobj.getInt("size"));
				});
			});

			Path optionalMods = Paths.get(FileUtil.SETTINGS.baseDir, "optional-mods");
			Files.createDirectories(optionalMods);

			additional.keySet().forEach(key -> {
				try {
					List<Object> array = additional.getJSONArray(key).toList();

					Files.list(optionalMods).filter(file -> file.toString().endsWith(".jar")).forEach(file -> {
						Path filePath = Paths.get(FileUtil.SETTINGS.baseDir, "mods", file.getFileName().toString());
						array.add(filePath.toString());
					});

					Files.list(Paths.get(FileUtil.SETTINGS.baseDir, key)).filter(incom -> {
						String filename = incom.getFileName().toString();
						return Files.isRegularFile(incom) && array.stream().noneMatch(job -> job.toString().contains(filename));
					}).forEach(t -> {
						Launcher.getLogger().debug("Deleted file " + t);
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
					Files.copy(pth, Paths.get(pth.toString().replace("additional-mods", "mods")));
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
			JarEntry entry = (JarEntry) enumerator.nextElement();
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

	public static Process start() {
		String javaVersionPath = FileUtil.SETTINGS.javaPath;
		if (javaVersionPath.isEmpty()) {
			Optional<String> javaVers = findJavaVersion();
			if (!javaVers.isPresent()) {
				Launcher.onError(new IllegalStateException("No valid Java version found!"));
				// try to start normaly
			}
			javaVersionPath = javaVers.get();
		}

		String[] parameter = prestart();
		if (parameter == null)
			return null;

		String width = String.valueOf(FileUtil.SETTINGS.width);
		String height = String.valueOf(FileUtil.SETTINGS.height);
		String ram = String.valueOf(FileUtil.SETTINGS.ram);
		String[] preparameter = new String[] { javaVersionPath + "/java", "-Xmx" + ram + "M", "-Xms" + ram + "M",
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
