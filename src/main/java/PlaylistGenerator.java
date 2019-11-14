import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import com.wrapper.spotify.requests.data.tracks.GetAudioFeaturesForSeveralTracksRequest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/*
    RESPONSIBILITIES:
    • Generate playlist
    • Filter by mood
*/
public class PlaylistGenerator {

    // Step 1: Returns a map of artist names mapped to an array of their top tracks
    public HashMap<String, Track[]> userTopArtistAndTrack(SpotifyApi spotifyApi) throws IOException, SpotifyWebApiException {

        GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyApi.getUsersTopArtists().build();
        Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();
        Artist[] artists = artistPaging.getItems();

        HashMap<String, Track[]> artistTracks = new HashMap<>();
        for (Artist a: artists) {
            Track[] tracks = spotifyApi.getArtistsTopTracks(a.getId(), CountryCode.CA).build().execute();
            artistTracks.put(a.getName(), tracks);
        }
        return artistTracks;
    }

    /**
     * Step 2:
     * THREE METRICS USED TO MEASURE MOOD: (0.0 to 1.0 scale)
     * 1. Valence: musical positiveness conveyed by a track
     * 2. Danceability: how suitable a track is for dancing
     * 3. Energy: perceptual measure of intensity and activity
     */
    public List<String> filterMood(SpotifyApi spotifyApi, HashMap<String, Track[]> topTracks, int mood) throws IOException, SpotifyWebApiException {

        ArrayList<String> selectedSongURIs = new ArrayList<>();
        for(String artistName : topTracks.keySet()) {
            GetAudioFeaturesForSeveralTracksRequest getAudioFeatures = spotifyApi.getAudioFeaturesForSeveralTracks().build();
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
}