import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class TestAPI2 {

    public static void main(String[] args) throws IOException, SpotifyWebApiException {

        Authenticate spotifyUser = new Authenticate();
        spotifyUser.authorizationCode_Sync();

        final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyUser.getSpotifyApi().authorizationCodeUri().scope("playlist-modify-public, playlist-modify-private").build();
        URI uri = authorizationCodeUriRequest.execute();

        Desktop desktop = Desktop.getDesktop();
        desktop.browse(uri);

        // SpotifyApi object calls for user profile info, returns a .Builder class which returns a
        // custom GetCurrentUserRequest, and .execute returns a User instance, and we ask for the ID.
        String userID = spotifyUser.getSpotifyApi().getCurrentUsersProfile().build().execute().getId();

        // "When a collection of objects is returned and the number of objects is variable,
        // the collection is wrapped in a paging object to simplify retrieval of further objects."
        Paging<PlaylistSimplified> playlists = spotifyUser.getSpotifyApi().getListOfUsersPlaylists(userID).build().execute();

        // Get the items contained in Paging object "playlists"
        PlaylistSimplified[] listOfPlaylists = playlists.getItems();

        // Testing printing names of playlists
        for(PlaylistSimplified ps : listOfPlaylists) {
            System.out.println("Name of playlist: " + ps.getName());
        }
    }
}
