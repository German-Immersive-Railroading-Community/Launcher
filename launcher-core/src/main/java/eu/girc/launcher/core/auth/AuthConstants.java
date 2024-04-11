package eu.girc.launcher.core.auth;

import java.net.URI;

final class AuthConstants {
    private AuthConstants() { }

    public static final String MICROSOFT_CLIENT_ID = "d46bfb8d-d307-47a3-b6a5-295a1017c2b7";

    public static final String MICROSOFT_OAUTH_SCOPES = "XboxLive.signin XboxLive.offline_access profile openid";

    public static final URI MICROSOFT_OAUTH_DEVICECODE_URI = URI.create("https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode");
}
