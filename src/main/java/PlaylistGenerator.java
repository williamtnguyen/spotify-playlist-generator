import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.*;
import com.wrapper.spotify.requests.data.personalization.GetUsersTopArtistsAndTracksRequest;
import com.wrapper.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import com.wrapper.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;
import com.wrapper.spotify.requests.data.tracks.GetAudioFeaturesForSeveralTracksRequest;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/*
    RESPONSIBILITIES:
    • Generate playlist
    • Filter by mood
*/
public class PlaylistGenerator {

    // local private reference to be reused in methods
    private SpotifyApi spotifyApi;
    private String playlistID;

    // upon initialization of a PlaylistGenerator instance, pass in the spotifyApi from authentication
    public PlaylistGenerator(SpotifyApi spotifyApi) {
        this.spotifyApi = spotifyApi;
    }

    // Step 1: Returns a map of artist names mapped to an array of their top tracks
    public HashMap<String, Track[]> userTopArtistAndTrack() throws IOException, SpotifyWebApiException {

        GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyApi.getUsersTopArtists().build();
        Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();
        Artist[] artists = artistPaging.getItems();

        HashMap<String, Track[]> topTracks = new HashMap<>();
        for (Artist a: artists) {
            Track[] tracks = spotifyApi.getArtistsTopTracks(a.getId(), CountryCode.CA).build().execute();
            topTracks.put(a.getName(), tracks);
        }
        return topTracks;
    }

    public ArrayList<String> userTopListenedTracks() throws IOException, SpotifyWebApiException {

        GetUsersTopTracksRequest getUsersTopTracksRequest = spotifyApi.getUsersTopTracks().build();
        Paging<Track> tracksPaging = getUsersTopTracksRequest.execute();
        Track[] topTracksList = tracksPaging.getItems();
        ArrayList<String> usersTopTracks = new ArrayList<>();
        for (Track t : topTracksList){
            usersTopTracks.add(t.getUri());
        }
        return usersTopTracks;

    }


    /**
     * Step 2: Algorithm to create a list of Track URI's that suffice the mood-index
     * THREE METRICS USED TO MEASURE MOOD: (0.0 to 1.0 scale)
     * 1. Valence: musical positiveness conveyed by a track
     * 2. Danceability: how suitable a track is for dancing
     * 3. Energy: perceptual measure of intensity and activity
     * @return a list of the selected track URI'
     */
    public ArrayList<String> filterMood(HashMap<String, Track[]> topTracks, double mood) throws IOException, SpotifyWebApiException {

        ArrayList<String> selectedSongURIs = new ArrayList<>();
        for(String artistName : topTracks.keySet()) {
            // here we need to pass a long comma-separated-list String of Track ID's into .getAudioFeaturesForSeveralTracks
            String stringOfIDs = trackIDToString(topTracks.get(artistName)); // this makes a fat string full of the track IDs
            GetAudioFeaturesForSeveralTracksRequest getAudioFeatures = spotifyApi.getAudioFeaturesForSeveralTracks(stringOfIDs).build();
            AudioFeatures[] audioFeatures = getAudioFeatures.execute();

            // for each set of audio-features of each track
            for(AudioFeatures trackData : audioFeatures) {
               if(mood < 0.10) {
                   if(0 <= trackData.getValence() && trackData.getValence() <= (mood+0.15)) {
                       if(trackData.getDanceability() <= (mood*8) && trackData.getEnergy() <= (mood*10)) {
                           selectedSongURIs.add(trackData.getUri());
                       }
                   }
               }
               else if(0.10 <= mood && mood < 0.25) {
                   if((mood-0.075) <= trackData.getValence() && trackData.getValence() <= (mood+0.075)) {
                       if(trackData.getDanceability() <= (mood*4) && trackData.getEnergy() <= (mood*5)) {
                           selectedSongURIs.add(trackData.getUri());
                       }
                   }
               }
               else if(0.25 <= mood && mood < 0.50) {
                   if((mood-0.05) <= trackData.getValence() && trackData.getValence() <= (mood+0.05)) {
                       if(trackData.getDanceability() <= (mood*1.75) && trackData.getEnergy() <= (mood*1.75)) {
                           selectedSongURIs.add(trackData.getUri());
                       }
                   }
               }
               else if(0.50 <= mood && mood < 0.75) {
                   if((mood-0.075) <= trackData.getValence() && trackData.getValence() <= (mood+0.075)) {
                       if(trackData.getDanceability() >= (mood/2.5) && trackData.getEnergy() >= (mood/2.0)) {
                           selectedSongURIs.add(trackData.getUri());
                       }
                   }
               }
               else if(0.75 <= mood && mood < 0.90) {
                   if((mood-0.075) <= trackData.getValence() && trackData.getValence() <= (mood+0.075)) {
                       if(trackData.getDanceability() >= (mood/2.0) && trackData.getEnergy() >= (mood/1.75)) {
                           selectedSongURIs.add(trackData.getUri());
                       }
                   }
               }
               else if(mood >= 0.90) {
                   if((mood-0.15) <= trackData.getValence() && trackData.getValence() <= 1) {
                       if(trackData.getDanceability() >= (mood/1.75) && trackData.getEnergy() >= (mood/1.5)) {
                           selectedSongURIs.add(trackData.getUri());
                       }
                   }
               }
            }
        }
        // shuffle the array and then return it
        Collections.shuffle(selectedSongURIs);
        return selectedSongURIs;
    }

    // Step 3: create the playlist
    public void createPlaylist(List<String> selectedSongURIs, double mood) throws IOException, SpotifyWebApiException {
        GetCurrentUsersProfileRequest getCurrentUsersProfile = spotifyApi.getCurrentUsersProfile().build();
        com.wrapper.spotify.model_objects.specification.User user = getCurrentUsersProfile.execute();
        String userID = user.getId();

        if (selectedSongURIs.size() > 0) {
            Playlist newPlaylist = spotifyApi.createPlaylist(userID, "MoodTape: ".concat(String.format("%.2f", mood))).build().execute();
            playlistID = newPlaylist.getId();
            Collections.shuffle(selectedSongURIs);
            // here we need to convert the arraylist to an array bc
            // this method below vvv requires an array of URI Strings (ie, String[])
            spotifyApi.addTracksToPlaylist(playlistID, selectedSongURIs.toArray(new String[selectedSongURIs.size()]))
                    .build()
                    .execute();
        }
    }

    public void createPlaylist(List<String> selectedSongURIs, String playlistName) throws IOException, SpotifyWebApiException {
        GetCurrentUsersProfileRequest getCurrentUsersProfile = spotifyApi.getCurrentUsersProfile().build();
        com.wrapper.spotify.model_objects.specification.User user = getCurrentUsersProfile.execute();
        String userID = user.getId();

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String date = dateFormat.format(new Date());

        if (selectedSongURIs.size() > 0) {
            Playlist newPlaylist = spotifyApi.createPlaylist(userID, playlistName + ": " + date).build().execute();
            playlistID = newPlaylist.getId();
            // here we need to convert the arraylist to an array bc
            // this method below vvv requires an array of URI Strings (ie, String[])
            spotifyApi.addTracksToPlaylist(playlistID, selectedSongURIs.toArray(new String[selectedSongURIs.size()]))
                    .build()
                    .execute();
        }
    }

    /* Supplementary Methods */

    // turns an array of Track objects to a comma-separated-string of the respective track ID's
    private String trackIDToString(Track[] tracks) {
        String result = "";
        if(tracks.length != 0) {
            // building a comma-separated-string of track ID's
            StringBuilder ids = new StringBuilder();
            for(Track track : tracks) {
                ids.append(track.getId()).append(",");
            }
            result = ids.deleteCharAt(ids.length() - 1).toString();
        }
        return result;
    }

    public PlaylistTrack[] getPlaylist() throws IOException, SpotifyWebApiException {
        return spotifyApi.getPlaylistsTracks(playlistID).build().execute().getItems();
    }
}