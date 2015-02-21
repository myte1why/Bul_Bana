package gadgetbazaar.bulbana;


import android.app.Application;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseTwitterUtils;
import com.parse.SaveCallback;

public class bulbanaapp extends Application {


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

                } else {
                }
            }


            //TODO: parse ui makeup will be done.

            //TODO: find logo and app icon with a better name.

        });
    }
}



