public class TestAPI {
<<<<<<< .merge_file_WATo3J
    public static void main(String[] args) {
=======
    private static String clientID;
    private static String clientSecret;
    private static SpotifyApi spotifyapi;
    private static ClientCredentialsRequest clientCredentialsRequest;
    private static URI redirectUri;
    private static URL redirectURL;
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
        final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyapi.authorizationCodeUri().scope("playlist-modify-public,playlist-modify-private").build();
        URI uri = authorizationCodeUriRequest.execute();

        // Opens the redirect URI


//        System.out.println("URI: " + uri.toString());

        System.out.println("Open the link above and paste the redirect link below:" + "\n" +
                "Note that you need to add an extra space after pasting the URL before clicking Enter.");

        Desktop desktop = Desktop.getDesktop();
        desktop.browse(uri);
        /*Alternatively, could use Buffered reader below:*/
        try {
            Scanner in = new Scanner(System.in);
            String input = in.nextLine();
            redirectURL = new URL(input);
            code = redirectURL.getQuery().split("code=")[1];
        } catch(Exception e){
            System.out.println("Not a valid link.");
        }

        System.out.println(code);

        authorizationCodeRequest = spotifyapi.authorizationCode(code).build();
        AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
//        spotifyapi.setAccessToken(authorizationCodeCredentials.getAccessToken());
//        spotifyapi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());


        authorizationCodeRefreshRequest = spotifyapi.authorizationCodeRefresh().build();
        // Set access and refresh token for further "spotifyApi" object usage
        spotifyapi.setAccessToken(authorizationCodeCredentials.getAccessToken());
        spotifyapi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
>>>>>>> .merge_file_3IlfhL
    }
}
