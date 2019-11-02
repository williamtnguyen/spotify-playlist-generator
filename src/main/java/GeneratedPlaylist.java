import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.Playlist;

import java.util.ArrayList;

/*
    RESPONSIBILITIES:
    • Add/Remove Songs To/From Playlist 
    • Shuffle song order
    • Stores a collection of songs
*/
public class GeneratedPlaylist {

    private Authenticate user;
    private String name;
    private String playlistID;
    private ArrayList<Track> collection; // Collection of songs
    private boolean nameAlreadyExists;

    public GeneratedPlaylist(String name, Authenticate user) {
        this.name = name;
        this.user = user;
        this.collection = new ArrayList<>();
        this.nameAlreadyExists = true;
    }

    public String createPlaylist(String userID, Authenticate user) {
        if(!this.nameAlreadyExists) {
            try {
                Playlist playlist = user.getSpotifyApi()
                        .createPlaylist(userID, this.name).public_(false).build().execute();
                System.out.println("Your playlist " + this.name + " has been created.");

                this.playlistID = playlist.getId();
                return playlist.getId();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return e.getMessage();
            }
        }
        else {
            System.out.println("The playlist already exists.");
            return this.playlistID;
        }
    }

    public void add(Track s) {
        // Adding unique tracks only
        if (!collection.contains(s)) {
            collection.add(s);
        }
    }

    public void remove(Track s) {
        collection.remove(s);
    }


}