package gadgetbazaar.bulbana.sistem;


import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import gadgetbazaar.bulbana.R;
import gadgetbazaar.bulbana.services.FirmaObject;
import gadgetbazaar.bulbana.services.gpsFinder;

public class haritaGorunumu extends Activity {

    GoogleMap googleMap;
    gpsFinder gps;
    ParseGeoPoint mPGP;
    private double mLat;
    private double mLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harita_gorunumu);

        //location finder.
        gps = new gpsFinder(haritaGorunumu.this);
        mLat = gps.getLatitude();
        mLng = gps.getLongitude();
        mPGP = new ParseGeoPoint(mLat, mLng);

        final List<ParseObject> firmaObjects = new ArrayList<ParseObject>();


        initilizeMap();
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Firma");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjectList, com.parse.ParseException e) {
                if (e == null) {
                    for (int i = 0; i < parseObjectList.size(); i++) {
                        ParseObject firma = parseObjectList.get(i);
                        FirmaObject f = new FirmaObject(firma.get("firmaAdi").toString(), firma.get("adres").toString(), firma.get("telefon").toString(), firma.get("lat").toString(), firma.get("lng").toString());

                        firmaObjects.add(f);
                    }
                    if (firmaObjects.size() > 0)
                        for (FirmaObject firma : firmaObjects) {
                            googleMap.addMarker(new MarkerOptions().title(firma.getFirmaAdi().toString())
                                    .snippet(firma.getAdres().toString())
                                    .icon(BitmapDescriptorFactory.defaultMarker())
                                    .position((new LatLng(Double.parseDouble(firma.getLatitude()), Double.parseDouble(firma.getLongitude())))));
                        }
                }

            }
        });
    }


    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.mapFragment)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}

