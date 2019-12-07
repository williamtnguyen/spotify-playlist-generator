import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.*;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/*
    RESPONSIBILITIES:
    ✔ Authenticate user login/registration
    • Queue/play/pause next song and previous song(FIFO)
    • Get input from GUI
    • Carry out user commands
 */
public class ProgramManager {
    private String clientID;
    private String clientSecret;
    private SpotifyApi spotifyapi;
    private URI redirectUri;
    private URL redirectURL;
    private AuthorizationCodeUriRequest authorizationCodeUriRequest;
    private AuthorizationCodeRequest authorizationCodeRequest;
    private String code;
    private AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest;
    private PlaylistGenerator playlistGenerator;

    public ProgramManager()
    {
        clientID = "f51f4ef2d861469195f1647d33cf7331";
        clientSecret = "0e4eeb82d1304dadb7de85073c8b4dab";
        redirectUri = SpotifyHttpManager.makeUri("https://www.google.com/");
        spotifyapi = new SpotifyApi.Builder()
                .setClientId(clientID)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectUri)
                .build();
    }

    // Retrieves code from entered redirect url and gets access token and refresh token
    public void authenticateUser(String redirectUrlFromUser) throws SpotifyWebApiException, IOException {
        // Sets the redirect url to the url entered by the user
        redirectURL = new URL(redirectUrlFromUser);

        // Get the code from the redirect url
        code = redirectURL.getQuery().split("code=")[1];

        // Retrieves access and refresh token and sets it in the spotifyapi variable in order to access user's spotify data
        authorizationCodeRequest = spotifyapi.authorizationCode(code).build();
        AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
        spotifyapi.setAccessToken(authorizationCodeCredentials.getAccessToken());
        spotifyapi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

         // Rest of this code doesn't need to be executed right away since we just created the access token
         // Set access and refresh token for further "spotifyApi" object usage
         authorizationCodeRefreshRequest = spotifyapi.authorizationCodeRefresh().build();
         authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();
         spotifyapi.setAccessToken(authorizationCodeCredentials.getAccessToken());
         spotifyapi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
        playlistGenerator = new PlaylistGenerator(spotifyapi);
    }

    // Opens the user's default browser for authentication
    public void openBrowserForAuthentication() throws IOException {
        // Create authorization request and uri link
        authorizationCodeUriRequest = spotifyapi.authorizationCodeUri().scope("playlist-read-private,user-top-read,playlist-modify\n").build();
        URI uri = authorizationCodeUriRequest.execute();

        // Opens the redirect URI
        Desktop desktop = Desktop.getDesktop();
        desktop.browse(uri);
    }

    public PlaylistTrack[] generatePlayList(double moodValue) throws IOException, SpotifyWebApiException {
        HashMap<String, Track[]> topTracksAndArtists = playlistGenerator.userTopArtistAndTrack();
        ArrayList<String> filteredTracksAndArtitsts = playlistGenerator.filterMood(topTracksAndArtists, moodValue);
        playlistGenerator.createPlaylist(filteredTracksAndArtitsts, moodValue);
        return playlistGenerator.getPlaylist();
    }

    public PlaylistTrack[] generatePlaylistTopListens() throws IOException, SpotifyWebApiException {
        ArrayList<String> topTracks = playlistGenerator.userTopListenedTracks();
        playlistGenerator.createPlaylist(topTracks, "Top Listens");
        return playlistGenerator.getPlaylist();
    }


    public String getCode() {
        return this.code;
    }

    // Encapsulation of private object
    public SpotifyApi getSpotifyApi() {
        return spotifyapi;
    }
}