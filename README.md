# Spotify Playlist Generator

This is a Java application that generates a playlist on your Spotify account based off a mood-index between 0 and 1, as well as an option to generate a playlist of tracks with top listens for the year. 

This project is built with Maven as a project management/comprehension tool, which set up the dependency on an open-source Java wrapper/client for the Spotify API.
The frontend was built with JavaFX in unison with custom a stylesheet for the components.

See the Java wrapper for Spotify API [here](https://github.com/thelinmichael/spotify-web-api-java).

## Application Walk-Through

Here's a walk-through of all functionalities of the application:

<img src='https://i.imgur.com/82rTO7f.gif' title='Video Walkthrough' width='' alt='Video Walkthrough'/>

GIF created with [LiceCap](http://www.cockos.com/licecap/).


## How to Run the Application:

Please note that you must have a Spotify premium account.

1. First run the program from UI.java how you would normally run a Java application. *File Path: (../src/main/java/UI.java)*

2. Once finished running, two windows will open: a Java window as well as a tab in your main browser prompting you to login to your Spotify account. After agreeing to authorize the app from your main browser, copy and paste the redirected URL in your browser into the text-box.

    1. Also note that after logging in once, all you need to do is copy and paste the redirected URL in your browser into the text-box.

3. After inserting your URL and clicking 'submit', the dialog box will ask you to select your mood-index between 0 and 1 (0 being sad; 1 being happy). Use the slider to change the mood of the playlist you want to generate. After choosing a mood, select the 'Generate Mood Playlist' button to generate playlist.

    1. Also note that you can also generate a playlist comprised of the tracks with top listen counts of the year.

4. Once you have generated your playlist, the dialog box will display a select number of song names for you to view. If you look at your playlist closely, you may notice familiar songs that you have listened on your spotify account. Please note that you can only view the song name and artist and audio files cannot be played. If you view your playlists in the Spotify app or Spotify's web player, a new playlist is created with the title MoodTape: 0.##, where 0.## is the mood number used to generate your playlist. 

5. If you would like to, click on the button to the right to create another playlist. The dialog box will direct you back to the mood slider page.

6. Once you are done with the application, close the dialog box to end the program.
