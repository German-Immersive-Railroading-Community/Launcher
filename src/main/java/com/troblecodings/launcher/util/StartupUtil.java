package com.troblecodings.launcher.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.ProgressMonitor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.troblecodings.launcher.ErrorDialog;
import com.troblecodings.launcher.ErrorPart;
import com.troblecodings.launcher.Launcher;

public class StartupUtil {

	private static final String RELEASE_API = "https://api.github.com/repos/German-Immersive-Railroading-Community/Launcher/releases";

	private static String LIBPATHS = "";
	private static String MAINCLASS = null;

	public static final String OSSHORTNAME = getOSShortName();

	public static String LWIDTH = "1280", LHEIGHT = "720";
	public static int RAM = 2048;

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

	public static void update() {
		try {
			String str = ConnectionUtil.getStringFromURL(RELEASE_API);
			if (str == null)
				return;
			JSONArray obj = new JSONArray(str);
			JSONObject newversion = obj.getJSONObject(0).getJSONArray("assets").getJSONObject(0);
			String downloadURL = newversion.getString("browser_download_url");
			File location = new File(StartupUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			long size = Files.size(Paths.get(location.toURI()));
			long newsize = newversion.getNumber("size").longValue();
			if (newsize == size || !location.isFile())
				return;
			Launcher.LOGGER.info("Updating Launcher!");
			ProgressMonitor progress = new ProgressMonitor(new JButton(), "Downloading update!", "", 0, (int) newsize);
			ConnectionUtil.download(downloadURL, location.toString(),
					bytesize -> progress.setProgress(bytesize.intValue()));
			new ProcessBuilder("java", "-jar", location.toString()).start().waitFor();
			System.exit(0);
		} catch (Throwable e) {
			e.printStackTrace();
			Launcher.INSTANCEL.setPart(new ErrorPart(Launcher.INSTANCEL.getPart(), "Update error!", "General error!"));
		}
	}

	public static boolean prestart() throws Throwable {
		String clientJson = FileUtil.BASE_DIR + "/GIR.json";
		ConnectionUtil.download("https://girc.eu/Launcher/GIR.json", clientJson);
		if (Files.notExists(Paths.get(clientJson))) {
			Launcher.INSTANCEL.setPart(new ErrorPart(Launcher.INSTANCEL.getPart(), "Missing version information!",
					"The GIR version json could not be found."));
			return false;
		}

		String content = new String(Files.readAllBytes(Paths.get(clientJson)));
		JSONObject object;
		try {
			object = new JSONObject(content);
		} catch (JSONException e) {
			Launcher.INSTANCEL.setPart(new ErrorPart(Launcher.INSTANCEL.getPart(), "Corrupted version information!",
					"The GIR version json could not be read."));
			return false;
		}

		MAINCLASS = object.getString("mainClass");

		// This part downloads the texture index and so on
		JSONObject assetIndex = object.getJSONObject("assetIndex");

		Path indexes = Paths.get(FileUtil.ASSET_DIR + "/indexes");
		if (!Files.exists(indexes))
			Files.createDirectories(indexes);

		String indexpath = indexes.toString() + "/" + assetIndex.getString("id") + ".json";
		String indexurl = assetIndex.getString("url");
		String indexsha1 = assetIndex.getString("sha1");
		long sizeAsset = assetIndex.getLong("size");
		ConnectionUtil.validateDownloadRetry(indexurl, indexpath, indexsha1, l -> Launcher.bar.update(l / sizeAsset));

		Path ogMC = Paths.get(FileUtil.BASE_DIR + "/versions/" + object.getString("inheritsFrom") + "/"
				+ object.getString("inheritsFrom") + ".jar");
		Files.createDirectories(ogMC.getParent());
		JSONObject clientDL = object.getJSONObject("downloads").getJSONObject("client");
		long sizeClient = clientDL.getLong("size");
		ConnectionUtil.validateDownloadRetry(clientDL.getString("url"), ogMC.toString(), clientDL.getString("sha1"), 
				l -> Launcher.bar.update(l / sizeClient));
		LIBPATHS = ogMC.toString() + ";";

		JSONObject additional = object.getJSONObject("additional");
		JSONArray arr = object.getJSONArray("libraries");
		Path ojectspath = Paths.get(FileUtil.ASSET_DIR + "/objects");
		if (!Files.exists(ojectspath))
			Files.createDirectories(ojectspath);

		JSONTokener tokener = new JSONTokener(Files.newInputStream(Paths.get(indexpath)));
		JSONObject index = new JSONObject(tokener);
		JSONObject objects = index.getJSONObject("objects");
		
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
				long size = artifact.getLong("size");
				ConnectionUtil.validateDownloadRetry(url, name, sha1, l -> Launcher.bar.update(l / size));
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
					long size = artifact.getLong("size");
					ConnectionUtil.validateDownloadRetry(url, name, sha1, l -> Launcher.bar.update(l / size));

					// Extract the natives
					unzip(name, FileUtil.LIB_DIR);
				}
			}
		}

		// Asset lockup and download
		String baseurl = "http://resources.download.minecraft.net/";
		objects.keySet().forEach((key) -> {
			JSONObject asset = objects.getJSONObject(key);
			String hash = asset.getString("hash");
			String folder = hash.substring(0, 2);
			Path folderpath = Paths.get(ojectspath.toString() + "/" + folder);
			if (!Files.exists(folderpath)) {
				try {
					Files.createDirectories(folderpath);
				} catch (IOException e) {
					ErrorDialog.createDialog(e);
				}
			}
			try {
				long size = asset.getLong("size");
				ConnectionUtil.validateDownloadRetry(baseurl + folder + "/" + hash, folderpath.toString() + "/" + hash,
						hash, l -> Launcher.bar.update(l / size));
			} catch (Throwable e) {
				ErrorDialog.createDialog(e);
			}
		});

		additional.keySet().forEach(key -> {
			additional.getJSONArray(key).forEach(fileobj -> {
				try {
					JSONObject jfileobj = (JSONObject) fileobj;
					long size = jfileobj.getLong("size");
					Path path = Paths.get(FileUtil.BASE_DIR, key, jfileobj.getString("name"));
					ConnectionUtil.validateDownloadRetry(jfileobj.getString("url"), path.toString(),
							jfileobj.getString("sha1"), l -> Launcher.bar.update(l / size));
				} catch (Throwable e) {
					ErrorDialog.createDialog(e);
				}
			});
		});
		Launcher.bar.update(0);
		return true;
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

	public static Process start(String[] parameter) throws Throwable {
		String[] preparameter = new String[] { "java", "-Xmx" + RAM + "M", "-Xms" + RAM + "M",
				"-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump",
				"-Djava.library.path=" + FileUtil.LIB_DIR, "-cp", LIBPATHS, MAINCLASS, "-width", LWIDTH, "-height",
				LHEIGHT };
		ProcessBuilder builder = new ProcessBuilder(
				Stream.concat(Arrays.stream(preparameter), Arrays.stream(parameter)).toArray(String[]::new));
		builder.directory(new File(FileUtil.BASE_DIR));
		builder.redirectError(Redirect.INHERIT);
		builder.redirectOutput(Redirect.INHERIT);
		Process process = builder.start();
		return process;
	}
}
