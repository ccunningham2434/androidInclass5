package edu.kvcc.cis298.criminalintent;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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

    public List<Crime> fetchCrimes() {
        // >This is the method that will take the original URL and allow us to add...
        // ...any parameters that might be required to it. For the URL's on my server...
        // >...there are no additional parameters needed. However many API's require...
        // >...extra parameters and this is where they add them.

        // >Make a list of crimes to be returned.
        List<Crime> crimes = new ArrayList<>();

        try {

            String url = Uri.parse("http://barnesbrothers.homeserver.com/crimeapi").buildUpon()
                    // >Add extra parameters here with the method .appendQueryParameter("param", "value")
                    .build().toString();

            // >This calls the above method to use the URL to get the JSON from the web service.
            // >After this call we will actually have the JSON that we need to parse.
            String jsonString = getUrlString(url);
            Log.i(TAG, "Fetched contents of URL: " + jsonString);
            // >This will take the jsonString that we got back an dput it into a jsonArray object.
            // >We have to use a jsonArray because our jsonString starts out with an Array.
            // >If is started with an object "{}" we would need to use JSONObject instead of JSONArray.
            // >The book uses JSONObject for their parse when they use Flickr.
            JSONArray jsonArray = new JSONArray(jsonString);

            // >Parse the crimes out from the object.
            parseCrimes(crimes, jsonArray
            );
        } catch (JSONException jse) {
            Log.e(TAG, "Failed to parse JSON", jse);
        }catch (IOException ioe) {
            Log.e(TAG, "Failed to load " + ioe);
        }

        return crimes;
    }

    private void parseCrimes(List<Crime> crimes, JSONArray jsonArray) throws IOException, JSONException {
        // >Loop through all of the elements in the JSONArray that were sent into this method.
        for (int i = 0; i < jsonArray.length(); i++) {
            // >Fetch a single JSONObject out from the JSONArray based on teh current index that we are on.
            JSONObject crimeJsonObject = jsonArray.getJSONObject(i);

            // >Pull the value from the JSONObject for the key of "uuid".
            String uuidString = crimeJsonObject.getString("uuid");
            // >Use the value to create a new UUID from that string.
            UUID uuidForNewCrime = UUID.fromString(uuidString);

            // >Get out the title from the JSONObject.
            String title = crimeJsonObject.getString("title");
            // >Get out the date and try to parse it.
            Date date;
            try {
                // >Declare a date formatter that can be used to parse the date from a string into an actual date object.
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                // >Use the format to parse the string that we get from the JSONObject.
                date = format.parse(crimeJsonObject.getString("incident_date"));
            } catch (Exception e) {
                date = new Date();
                Log.e(TAG, e.toString());
            }

            // >Evaluate the is_solved value from the JSONObject to see if it is equal to "1".
            // >If the expression is true, then the is_solved of teh crime will be true.
            boolean is_solved = (crimeJsonObject.getString("is_solved") == "1");

            // >Create a new crime object from the data we have fetched out.
            Crime newCrime = new Crime(uuidForNewCrime, title, date, is_solved);
            // >Add the finished crime to the list of crimes that was passed in to this method.
            crimes.add(newCrime);
        }
    }

}
