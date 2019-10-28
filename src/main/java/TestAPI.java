import com.wrapper.spotify.SpotifyApi;

import java.net.URI;

public class TestAPI {
    public static void main(String[] args) {
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId("f7f38d8d47df468f987f4f15f9862d66")
                .setClientSecret("e1385b3a4c694b6cbaec587ffc10f681")
                .setRedirectUri(URI.create("spotify-mood-gen://callback"))
                .build();
        spotifyApi.setAccessToken("yeehaw");
        System.out.println(spotifyApi.getAccessToken());
    }

}
