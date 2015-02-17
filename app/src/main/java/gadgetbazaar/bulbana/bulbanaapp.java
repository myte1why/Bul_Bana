package gadgetbazaar.bulbana;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.fitness.request.m;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseTwitterUtils;
import com.parse.SaveCallback;

import gadgetbazaar.bulbana.services.ConfigHelper;


public class bulbanaapp extends Application {

    // Key for saving the search distance preference
    private static final String KEY_SEARCH_DISTANCE = "searchDistance";

    private static final float DEFAULT_SEARCH_DISTANCE = 500.0f;
    private static SharedPreferences preferences;
    private static ConfigHelper configHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        String parseApi = getString(R.string.parse_app_id);
        String parseClient = getString(R.string.parse_client_key);
        Parse.initialize(this, parseApi, parseClient);
        // Save the current Installation to Parse.
        ParseInstallation.getCurrentInstallation().saveInBackground();


        //Twitter
        String twitter_consumer_key = getString(R.string.twitter_consumer_key);
        String twitter_consumer_secret = getString(R.string.twitter_consumer_secret);
        ParseTwitterUtils.initialize(twitter_consumer_key, twitter_consumer_secret);

        //Parse Push ayarları
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });


        //TODO: Picasso, Push, analytics and maps.

        //TODO: parse ui makeup will be done.

        //TODO: find logo and app icon with a better name.

    }

 /* public static float getSearchDistance() {
        return preferences.getFloat(KEY_SEARCH_DISTANCE, DEFAULT_SEARCH_DISTANCE);
    }
    public static void setSearchDistance(float value) {
        preferences.edit().putFloat(KEY_SEARCH_DISTANCE, value).commit();
    }
    public static ConfigHelper getConfigHelper() {
        return configHelper;
    }
*/
}
