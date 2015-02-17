package gadgetbazaar.bulbana.sistem;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gadgetbazaar.bulbana.R;
import gadgetbazaar.bulbana.bulbanaapp;
import gadgetbazaar.bulbana.services.MapMarkerStyle;
import gadgetbazaar.bulbana.services.gpsFinder;

public class haritaGorunumu extends Activity {
    // Conversion from feet to meters
    private static final float METERS_PER_FEET = 0.3048f;
    // Conversion from kilometers to meters
    private static final int METERS_PER_KILOMETER = 1000;
    // Initial offset for calculating the map bounds
    private static final double OFFSET_CALCULATION_INIT_DIFF = 1.0;

    // Accuracy for calculating the map bounds
    private static final float OFFSET_CALCULATION_ACCURACY = 0.01f;

    // Maximum results returned from a Parse query
    private static final int MAX_POST_SEARCH_RESULTS = 20;

    // Maximum post search radius for map in kilometers
    private static int MAX_POST_SEARCH_DISTANCE;

    private final Map<String, Marker> mapMarkers = new HashMap<String, Marker>();
    gpsFinder gps;
    MapFragment mapFragment;
    private Location mLocation;
    private double mLat;
    private double mLng;
    private String mSecilen;
    private float radius;
    private float lastRadius;
    private ParseQueryAdapter<MapMarkerStyle> pointsQueryAdapter;
    private String selectedPostObjectId;


    private int mostRecentMapUpdate;

    private Location currentLocation;
    private Location lastLocation;
    private Circle mapCircle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harita_gorunumu);


//       radius = bulbanaapp.getSearchDistance();
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment);
        mapFragment.getMap().setMyLocationEnabled(true);
       // mapFragment.getMapAsync((OnMapReadyCallback) this);

        gps = new gpsFinder(this);
        final Intent isecilen = getIntent();
        mSecilen = isecilen.getStringExtra("selected");

        mLocation = gps.getLocation();
        mLat = gps.getLatitude();
        mLng = gps.getLongitude();
        MAX_POST_SEARCH_DISTANCE = getResources().getInteger(R.integer.distance);

        final ParseGeoPoint userPoint = new ParseGeoPoint(mLat, mLng);


        ParseQueryAdapter.QueryFactory<MapMarkerStyle> mapQuery = new ParseQueryAdapter.QueryFactory<MapMarkerStyle>() {
            @Override
            public ParseQuery<MapMarkerStyle> create() {
                ParseQuery<MapMarkerStyle> query = MapMarkerStyle.getQuery();
  //              query.whereWithinKilometers("location", userPoint, radius * METERS_PER_FEET / METERS_PER_KILOMETER);

                return query;
            }
        };
        pointsQueryAdapter = new ParseQueryAdapter<MapMarkerStyle>(this, mapQuery) {
            @Override
            public View getItemView(MapMarkerStyle markerStyle, View view, ViewGroup parent) {
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.marker_style, null);
                }
                TextView tFirmaAdi = (TextView) findViewById(R.id.tFirmaAdi);
                TextView tFirmaTelefon = (TextView) findViewById(R.id.tFiremaTelefon);
                TextView tFirmaAdres = (TextView) findViewById(R.id.tFirmaAdres);
                tFirmaAdi.setText(markerStyle.getFirmaAdi());
                tFirmaTelefon.setText(markerStyle.getFirmaTelefon());
                tFirmaAdres.setText(markerStyle.getFirmaAdres());
                return view;
            }
        };


        pointsQueryAdapter.setAutoload(false);

        // Attach the query adapter to the view
        ListView postsListView = (ListView) findViewById(R.id.list);
        postsListView.setAdapter(pointsQueryAdapter);

        // Set up the handler for an item's selection
        postsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final MapMarkerStyle item = pointsQueryAdapter.getItem(position);
                selectedPostObjectId = item.getObjectId();
                mapFragment.getMap().animateCamera(
                        CameraUpdateFactory.newLatLng(new LatLng(item.getLocation().getLatitude(), item
                                .getLocation().getLongitude())), new GoogleMap.CancelableCallback() {
                            public void onFinish() {
                                Marker marker = mapMarkers.get(item.getObjectId());
                                if (marker != null) {
                                    marker.showInfoWindow();
                                }
                            }

                            public void onCancel() {
                            }
                        }
                );
                Marker marker = mapMarkers.get(item.getObjectId());
                if (marker != null) {
                    marker.showInfoWindow();
                }
            }
        });
        mapFragment.getMap().setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            public void onCameraChange(CameraPosition position) {
                // When the camera changes, update the query
                doMapQuery();
            }
        });
        //TODO: https://www.parse.com/tutorials/anywall-android#4-view-posts

        //TODO: add markers to map.

        //TODO: create custom icon set with call ability.
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    /*
     * Set up a query to update the list view
     */
    private void doListQuery() {
        Location myLoc =gps.getLocation();
        // If location info is available, load the data
        if (myLoc != null) {
            // Refreshes the list view with new data based
            // usually on updated location data.
            pointsQueryAdapter.loadObjects();
        }
    }
    /*
  * Set up the query to update the map view
  */
    private void doMapQuery() {
        final int myUpdateNumber = ++mostRecentMapUpdate;
        Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
        // If location info isn't available, clean up any existing markers
        if (myLoc == null) {
            cleanUpMarkers(new HashSet<String>());
            return;
        }
        final ParseGeoPoint myPoint = geoPointFromLocation(myLoc);
        // Create the map Parse query
        final ParseQuery<MapMarkerStyle> mapQuery = MapMarkerStyle.getQuery();
        // Set up additional query filters
        mapQuery.whereWithinKilometers("location", myPoint, MAX_POST_SEARCH_DISTANCE);
        mapQuery.include("user");
        mapQuery.orderByDescending("createdAt");
        mapQuery.setLimit(MAX_POST_SEARCH_RESULTS);
        // Kick off the query in the background
        mapQuery.findInBackground(new FindCallback<MapMarkerStyle>() {
            @Override
            public void done(List<MapMarkerStyle> objects, ParseException e) {
                if (e != null) {
                    return;
                }
        /*
         * Make sure we're processing results from
         * the most recent update, in case there
         * may be more than one in progress.
         */
                if (myUpdateNumber != mostRecentMapUpdate) {
                    return;
                }
                // Posts to show on the map
                Set<String> toKeep = new HashSet<String>();
                // Loop through the results of the search
                for (MapMarkerStyle post : objects) {
                    // Add this post to the list of map pins to keep
                    toKeep.add(post.getObjectId());
                    // Check for an existing marker for this post
                    Marker oldMarker = mapMarkers.get(post.getObjectId());
                    // Set up the map marker's location
                    MarkerOptions markerOpts =
                            new MarkerOptions().position(new LatLng(post.getLocation().getLatitude(), post
                                    .getLocation().getLongitude()));
                    // Set up the marker properties based on if it is within the search radius
                    if (post.getLocation().distanceInKilometersTo(myPoint) > radius * METERS_PER_FEET
                            / METERS_PER_KILOMETER) {
                        // Check for an existing out of range marker
                        if (oldMarker != null) {
                            if (oldMarker.getSnippet() == null) {
                                // Out of range marker already exists, skip adding it
                                continue;
                            } else {
                                // Marker now out of range, needs to be refreshed
                                oldMarker.remove();
                            }
                        }
                        // Display a red marker with a predefined title and no snippet
                        markerOpts =
                                markerOpts.title(getResources().getString(R.string.point_out_of_range)).icon(
                                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    } else {
                        // Check for an existing in range marker
                        if (oldMarker != null) {
                            if (oldMarker.getSnippet() != null) {
                                // In range marker already exists, skip adding it
                                continue;
                            } else {
                                // Marker now in range, needs to be refreshed
                                oldMarker.remove();
                            }
                        }
                        // Display a green marker with the post information
                        markerOpts.title(post.getFirmaAdi()).snippet(post.getFirmaTelefon()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    }
                    // Add a new marker
                    Marker marker = mapFragment.getMap().addMarker(markerOpts);
                    mapMarkers.put(post.getObjectId(), marker);
                    if (post.getObjectId().equals(selectedPostObjectId)) {
                        marker.showInfoWindow();
                        selectedPostObjectId = null;
                    }
                }
                // Clean up old markers.
                cleanUpMarkers(toKeep);
            }
        });
    }
    /*
 * Helper method to clean up old markers
 */
    private void cleanUpMarkers(Set<String> markersToKeep) {
        for (String objId : new HashSet<String>(mapMarkers.keySet())) {
            if (!markersToKeep.contains(objId)) {
                Marker marker = mapMarkers.get(objId);
                marker.remove();
                mapMarkers.get(objId).remove();
                mapMarkers.remove(objId);
            }
        }
    }
    /*
 * Helper method to get the Parse GEO point representation of a location
 */
    private ParseGeoPoint geoPointFromLocation(Location loc) {
        return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
    }
    private void updateCircle(LatLng myLatLng) {
        if (mapCircle == null) {
            mapCircle =
                    mapFragment.getMap().addCircle(
                            new CircleOptions().center(myLatLng).radius(radius * METERS_PER_FEET));
            int baseColor = Color.DKGRAY;
            mapCircle.setStrokeColor(baseColor);
            mapCircle.setStrokeWidth(2);
            mapCircle.setFillColor(Color.argb(50, Color.red(baseColor), Color.green(baseColor),
                    Color.blue(baseColor)));
        }
        mapCircle.setCenter(myLatLng);
        mapCircle.setRadius(radius * METERS_PER_FEET); // Convert radius in feet to meters.
    }

    /*
     * Zooms the map to show the area of interest based on the search radius
     */
    private void updateZoom(LatLng myLatLng) {
        // Get the bounds to zoom to
        LatLngBounds bounds = calculateBoundsWithCenter(myLatLng);
        // Zoom to the given bounds
        mapFragment.getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
    }

    /*
     * Helper method to calculate the offset for the bounds used in map zooming
     */
    private double calculateLatLngOffset(LatLng myLatLng, boolean bLatOffset) {
        // The return offset, initialized to the default difference
        double latLngOffset = OFFSET_CALCULATION_INIT_DIFF;
        // Set up the desired offset distance in meters
        float desiredOffsetInMeters = radius * METERS_PER_FEET;
        // Variables for the distance calculation
        float[] distance = new float[1];
        boolean foundMax = false;
        double foundMinDiff = 0;
        // Loop through and get the offset
        do {
            // Calculate the distance between the point of interest
            // and the current offset in the latitude or longitude direction
            if (bLatOffset) {
                Location.distanceBetween(myLatLng.latitude, myLatLng.longitude, myLatLng.latitude
                        + latLngOffset, myLatLng.longitude, distance);
            } else {
                Location.distanceBetween(myLatLng.latitude, myLatLng.longitude, myLatLng.latitude,
                        myLatLng.longitude + latLngOffset, distance);
            }
            // Compare the current difference with the desired one
            float distanceDiff = distance[0] - desiredOffsetInMeters;
            if (distanceDiff < 0) {
                // Need to catch up to the desired distance
                if (!foundMax) {
                    foundMinDiff = latLngOffset;
                    // Increase the calculated offset
                    latLngOffset *= 2;
                } else {
                    double tmp = latLngOffset;
                    // Increase the calculated offset, at a slower pace
                    latLngOffset += (latLngOffset - foundMinDiff) / 2;
                    foundMinDiff = tmp;
                }
            } else {
                // Overshot the desired distance
                // Decrease the calculated offset
                latLngOffset -= (latLngOffset - foundMinDiff) / 2;
                foundMax = true;
            }
        } while (Math.abs(distance[0] - desiredOffsetInMeters) > OFFSET_CALCULATION_ACCURACY);
        return latLngOffset;
    }

    /*
     * Helper method to calculate the bounds for map zooming
     */
    LatLngBounds calculateBoundsWithCenter(LatLng myLatLng) {
        // Create a bounds
        LatLngBounds.Builder builder = LatLngBounds.builder();

        // Calculate east/west points that should to be included
        // in the bounds
        double lngDifference = calculateLatLngOffset(myLatLng, false);
        LatLng east = new LatLng(myLatLng.latitude, myLatLng.longitude + lngDifference);
        builder.include(east);
        LatLng west = new LatLng(myLatLng.latitude, myLatLng.longitude - lngDifference);
        builder.include(west);

        // Calculate north/south points that should to be included
        // in the bounds
        double latDifference = calculateLatLngOffset(myLatLng, true);
        LatLng north = new LatLng(myLatLng.latitude + latDifference, myLatLng.longitude);
        builder.include(north);
        LatLng south = new LatLng(myLatLng.latitude - latDifference, myLatLng.longitude);
        builder.include(south);

        return builder.build();
    }
}

