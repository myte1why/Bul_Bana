package gadgetbazaar.bulbana.sistem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.AppEventsLogger;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

import gadgetbazaar.bulbana.R;

public class dispatcher extends Activity {

    protected ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatcher);

        progresBar();
    }




    @Override
    protected void onResume() {
        super.onResume();
        progresBar();
        ParseUser loggedOne = ParseUser.getCurrentUser();

        if (loggedOne != null) {
            Intent iUserLogged = new Intent(dispatcher.this, secim.class);
            iUserLogged = iUserLogged.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            iUserLogged = iUserLogged.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(iUserLogged);
        } else {
            ParseLoginBuilder builder = new ParseLoginBuilder(dispatcher.this);
            startActivityForResult(builder.build(), 0);
        }
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
    protected void progresBar() {
        mProgressBar = (ProgressBar) findViewById(R.id.pBDispatcher);
        mProgressBar.setVisibility(View.VISIBLE);}
}
