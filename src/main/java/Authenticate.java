import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class Authenticate<uri> {

    // Static local vars necessary for authentication
    private static final String CLIENT_ID = "f51f4ef2d861469195f1647d33cf7331";
    private static final String CLIENT_SECRET = "0e4eeb82d1304dadb7de85073c8b4dab";
    private static final URI REDIRECT_URI = SpotifyHttpManager.makeUri("https://www.google.com/");
    private static final String code = "";

    // spotifyApi object requires the local vars to be set as its properties to grant authorization
    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(CLIENT_ID)
            .setClientSecret(CLIENT_SECRET)
            .setRedirectUri(REDIRECT_URI)
            .build();

    private static final AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
            .build();

    public Authenticate() throws IOException {
    }

    public void authorizationCode_Sync() {
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Encapsulation of private object
    public SpotifyApi getSpotifyApi() {
        return spotifyApi;
    }
}
