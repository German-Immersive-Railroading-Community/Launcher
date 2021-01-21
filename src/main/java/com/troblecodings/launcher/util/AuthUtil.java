package com.troblecodings.launcher.util;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import net.cydhra.nidhogg.MojangClient;
import net.cydhra.nidhogg.YggdrasilAgent;
import net.cydhra.nidhogg.YggdrasilClient;
import net.cydhra.nidhogg.data.AccountCredentials;
import net.cydhra.nidhogg.data.Profile;
import net.cydhra.nidhogg.data.ProfileProperty;
import net.cydhra.nidhogg.data.Session;

public class AuthUtil {

	private static YggdrasilClient client = new YggdrasilClient();
	private static MojangClient mclient = new MojangClient();

	public static String[] START_PARAMS = null;

	public static String[] auth(String username, String passw) throws Throwable {
		Session session = FileUtil.DEFAULT;
		if (session != null) {
			client.refresh(session);
		} else {
			if (username == null || passw == null)
				return null;
			session = client.login(new AccountCredentials(username, passw), YggdrasilAgent.MINECRAFT);
		}
		FileUtil.saveSession(session);

		if (!client.validate(session))
			return null;

		Profile profile = mclient.getProfileByUUID(session.getUuid());
		return make(profile, session);
	}

	private static String unpackProperties(Profile profile) {
		JSONArray array = new JSONArray();
		for (ProfileProperty property : profile.getProperties()) {
			JSONObject object = new JSONObject();
			object.put(property.getName(), new JSONArray(Arrays.asList(property.getValue(), property.getSignature())));
		}
		return array.toString();
	}

	private static String[] make(Profile profile, Session session) {
		return START_PARAMS = new String[] { "--username", profile.getName(), "--version", "1.12.2", "--gameDir",
				FileUtil.BASE_DIR, "--assetsDir", FileUtil.ASSET_DIR, "--assetIndex", "1.12", "--uuid",
				session.getUuid().toString(), "--accessToken", session.getAccessToken(), "--userProperties",
				unpackProperties(profile), "--userType", "mojang", "--tweakClass",
				"net.minecraftforge.fml.common.launcher.FMLTweaker", "--versionType", "Forge" };
	}

}
