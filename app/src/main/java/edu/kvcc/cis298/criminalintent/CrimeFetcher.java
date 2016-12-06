package edu.kvcc.cis298.criminalintent;

import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Created by ccunn on 05-Dec-16.
 */

public class CrimeFetcher {

    // String constant for loggin
    private static final String TAG = "CrimeFetcher";

    private byte[] getUrlBytes(String urlSpec) throws IOException {
        // >Create a new URL object from the url string that was passed in.
        URL url = new URL(urlSpec);

        // >Create a new HTTP connection to the specified url.
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            // >Create an output stream to hold htat data that is read from the url source.
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // >Create an input stream from the http connection.
            InputStream in = connection.getInputStream();

            // >Check to see that the response code from the http request is 200, which is the same as http_ok.
            // >Every web request will return some sort of response code. You can google them.
            // >Typically 200's good, 300's chache, 400's error, 500's server error.
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage()
                    + ": with " + urlSpec);
            }

            // >Create an int to  hold how many bytes we are goin g to read in.
            int bytesRead = 0;

            // >Create an byte array to act as a buffer that will read in up to 1024 bytes at a time.
            byte[] buffer = new byte[1024];

            // >While we can read bytes from the input stream.
            while ((bytesRead = in.read(buffer)) > 0) {
                // >Write the bytes out to the output stream.
                out.write(buffer, 0, bytesRead);
            }

            // >Once everything has been read and written, close the output stream.
            out.close();
            in.close();

            // >Convert the stream to a byte array.
            return out.toByteArray();
        } finally {
            // >Make sure that the web connection is closed.
            connection.disconnect();
        }
    }

    private String getUrlString(String urlSpec) throws IOException {
        return new String (getUrlBytes(urlSpec));
    }

    public void fetchCrimes() {
        // >This is the method that will take the original URL and allow us to add...
        // ...any parameters that might be required to it. For the URL's on my server...
        // >...there are no additional parameters needed. However many API's require...
        // >...extra parameters and this is where they add them.
        try {

            String url = Uri.parse("http://barnesbrothers.homeserver.com/crimeapi").buildUpon()
                    // >Add extra parameters here with the method .appendQueryParameter("param", "value")
                    .build().toString();

            String jsonString = getUrlString(url);
            Log.i(TAG, "Fetched contents of URL: " + jsonString);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to load " + ioe);
        }
    }


}
