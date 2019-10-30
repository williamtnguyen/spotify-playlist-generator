import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.awt.*;
import java.io.IOException;
import java.net.*;

public class TestAPI {
    private static String clientID;
    private static String clientSecret;
    private static SpotifyApi spotifyapi;
    private static ClientCredentialsRequest clientCredentialsRequest;
    private static URI redirectUri;
    private static AuthorizationCodeUriRequest authorizationCodeUriRequest;
    private static AuthorizationCodeRequest authorizationCodeRequest;
    private static String code = "";
    private static AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest;

    public static void main(String[] args) throws IOException, SpotifyWebApiException, InterruptedException {
        // Local vars necessary for authentication
        clientID = "f51f4ef2d861469195f1647d33cf7331";
        clientSecret = "0e4eeb82d1304dadb7de85073c8b4dab";
        redirectUri = SpotifyHttpManager.makeUri("https://www.google.com/");

        // SpotifyApi object requires the local vars to be set as its properties to grant authorization
        spotifyapi = new SpotifyApi.Builder().setClientId(clientID).setClientSecret(clientSecret).setRedirectUri(redirectUri).build();
        final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyapi.authorizationCodeUri().state("someExpectedStateString").scope("playlist-modify-public,playlist-modify-private").build();
        URI uri = authorizationCodeUriRequest.execute();

        // Opens the redirect URI
        Desktop desktop = Desktop.getDesktop();
        desktop.browse(uri);


        authorizationCodeRequest = spotifyapi.authorizationCode(code).build();
        AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();
        spotifyapi.setAccessToken(authorizationCodeCredentials.getAccessToken());
        spotifyapi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());


        authorizationCodeRefreshRequest = spotifyapi.authorizationCodeRefresh().build();
        // Set access and refresh token for further "spotifyApi" object usage
        spotifyapi.setAccessToken(authorizationCodeCredentials.getAccessToken());
        spotifyapi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
    }
}
