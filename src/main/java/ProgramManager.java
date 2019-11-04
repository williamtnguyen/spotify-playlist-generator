import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.data.personalization.GetUsersTopArtistsAndTracksRequest;
import com.wrapper.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;

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
        code = this.getCode(redirectUrlFromUser);
        System.out.println(code);

        // Retrieves access and refresh token and sets it in the spotifyapi variable in order to access user's spotify data
        authorizationCodeRequest = spotifyapi.authorizationCode(code).build();
        AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
        spotifyapi.setAccessToken(authorizationCodeCredentials.getAccessToken());
        spotifyapi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

        System.out.println("Success! Authorization complete.");
        System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());


         //Rest of this code doesn't need to be executed right away since we just created the access token
         // Set access and refresh token for further "spotifyApi" object usage
         authorizationCodeRefreshRequest = spotifyapi.authorizationCodeRefresh().build();
         authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();
         spotifyapi.setAccessToken(authorizationCodeCredentials.getAccessToken());
         spotifyapi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
    }

    // Opens the user's default browser for authentication
    public void openBrowserForAuthentication() throws IOException {
        // Create authorization request and uri link
        authorizationCodeUriRequest = spotifyapi.authorizationCodeUri().scope("playlist-read-private,user-top-read\n").build();
        URI uri = authorizationCodeUriRequest.execute();

//        return uri.toURL();
        // Opens the redirect URI
        Desktop desktop = Desktop.getDesktop();
        desktop.browse(uri);
    }

    // Strips the code from the redirect url link that the user pasted into the textbox of the UI
    public String getCode (String url) throws MalformedURLException {
        redirectURL = new URL(url);
        return redirectURL.getQuery().split("code=")[1];
    }

    public void userTopArtistAndTrack() throws IOException, SpotifyWebApiException {
        GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyapi.getUsersTopArtists().build();
        Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();

        Artist[] artists = artistPaging.getItems();

        for (Artist a: artists)
        {
            System.out.println(a.getName());
            Track[] tracks = spotifyapi.getArtistsTopTracks(a.getId(), CountryCode.CA).build().execute();
            for (Track t: tracks)
            {
                System.out.print(t.getName() + ", ");
            }

        }

        System.out.println("Total: " + artistPaging.getTotal());
    }
}