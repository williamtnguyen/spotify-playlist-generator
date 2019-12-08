# Spotify Playlist Generator

This is a Java application that generates a playlist on your Spotify account based off a mood-index, as well as an option to generate a playlist of tracks with top listens for the year. 
This project is built with Maven as a project management/comprehension tool, which set up the dependency on an open-source Java wrapper/client for the Spotify API.
The frontend was built with JavaFX in unison with custom a stylesheet for the components.

See the Java wrapper for Spotify API [here](https://github.com/thelinmichael/spotify-web-api-java).

## Application Walk-Through
Here's a walk-through of all functionalities of the application:
<img src='https://i.imgur.com/N3v2rJd.gif' title='Video Walkthrough' width='' alt='Video Walkthrough'/>

GIF created with [LiceCap](http://www.cockos.com/licecap/).


The following listed below are the steps that are required to running a UI-based app with java and spotify api that generates playlists based on the user's mood.
Please note that a the user must have a spotify account and must be logged into your current web browser (google in this case).

1. First run the program from UI.java. It is important to note that the program must be run at the line where the class UI extends Application.
2. Once finished running, a JPanel will open up greeting the user to the Spotify Playlist. After agreeing to authorize the app from your main browser, copy and paste the redirected URL into the textbox.
3. If you have not yet authorized your spotify account, complete the steps on creating and logging into your spotify account from the web browser. Paste in your URL into the textbox once it has been completed.
4. After inserting your URL and clicking next, the dialog box will ask you to select your mood (0 being sad; 1 being happy). Use the slider to change the mood of the playlist you want to generate. After choosing a mood, select the button to generate playlist.
5. Once you have generated your playlist, the dialog box will display a select number of song names for you to view. If you look at your playlist closely, you may notice familiar songs that you have listened on your spotify account. Please note that you can only view the song name and artist and audio files cannot be played. If you view your playlists in a spotify app or spotify's web player, a new playlist is created with the title MoodTape: 0.##, where 0.## is the mood number used to generate your playlist. 
6. If you would like to, click on the button to the right to create another playlist. The dialog box will direct you back to the mood slider page.
7. Once you are done with the application, close the dialog box to end the program.
