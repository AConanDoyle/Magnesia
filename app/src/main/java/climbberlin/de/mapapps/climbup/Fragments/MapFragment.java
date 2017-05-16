package climbberlin.de.mapapps.climbup.Fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.UiSettings;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import climbberlin.de.mapapps.climbup.Helper.SetMarkers;
import climbberlin.de.mapapps.climbup.Helper.XMLParser;
import climbberlin.de.mapapps.climbup.ListItemActivity;
import climbberlin.de.mapapps.climbup.OverActivity;
import climbberlin.de.mapapps.climbup.Preferences.SettingsActivity;
import climbberlin.de.mapapps.climbup.R;
import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;

import static climbberlin.de.mapapps.climbup.R.id.item_satellite_streets;
import static climbberlin.de.mapapps.climbup.R.id.map_flip;

public class MapFragment extends Fragment implements OnMapReadyCallback, MapboxMap.OnInfoWindowClickListener {

    // Map stuff
    private static final int MY_PERMISSIONS_REQUEST_GET_LOCATION = 1;
    protected LocationManager locationManager;
    protected Location myLocation;
    private int hasFineLocationPermission = 0;
    private int hasCoarseLocationPermission = 0;
    UiSettings mUiSettings;
    private MapboxMap mapboxMap;
    private MapView mapView;

    // Preference settings
    boolean map_tilt, map_rotate, filter_labels, filter_autoclose, isFirstRunLocationDialoge;
    SharedPreferences prefs;
    AlertDialog.Builder alertDialog;

    // Notes for data handaling
    String KEY_FM = "gml:featureMember"; // entering Tag for XML-File
    String KEY_NAME = "ogr:NAME";
    String KEY_LAT = "ogr:LAT";
    String KEY_LONG = "ogr:LONG";
    String KEY_USE = "ogr:NUTZEN";
    String KEY_KROUT = "ogr:KROUTEN";
    String KEY_BROUT = "ogr:BROUTEN";
    String KEY_INOUT = "ogr:INOUT";
    // object for spots data
    private String gml = null;

    // Floating Action Buttons & Menus
    FloatingActionMenu FAM_InOut;
    FloatingActionButton fabIN, fabOut, fabInOut, locationButton;

    // Filters for Map
    Boolean showclimb, showSatellite_streets;
    String inOutFilter;

    // others
    private Handler mHandler = new Handler();
    private double zoomLevelMap;
    private boolean fromMapFlip = false;
    View view;

    // LocationListener for handling location changes
    private LocationListener locationListener = new LocationListener() {
        public String TAG;

        @Override
        public void onLocationChanged(Location location) {
            String longitude = "Longitude: " + location.getLongitude();
            Log.i(TAG, longitude);
            String latitude = "Latitude: " + location.getLatitude();
            Log.i(TAG, latitude);
            myLocation = location;
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    // handling preference changes
    SharedPreferences.OnSharedPreferenceChangeListener prefChangedListener = new
            SharedPreferences.OnSharedPreferenceChangeListener() {

                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                      String key) {
                    if (key.equals("map_tilt") || key.equals("map_rotation")) {
                        map_tilt = prefs.getBoolean("map_tilt", false);
                        map_rotate = prefs.getBoolean("map_rotation", true);
                        mUiSettings.setRotateGesturesEnabled(map_rotate);
                        mUiSettings.setTiltGesturesEnabled(map_tilt);
                        Log.i("Preferences change in ", "map settings");
                    } else if (key.equals("faButton_auto_close")) {
                        filter_autoclose = prefs.getBoolean("faButton_auto_close", true);
                    } else if (key.equals("faButton_labels")) {
                        filter_labels = prefs.getBoolean("faButton_labels", true);
                        fabIN.setLabelVisibility((filter_labels) ? 1 : 0);
                        fabOut.setLabelVisibility((filter_labels) ? 1 : 0);
                        fabInOut.setLabelVisibility((filter_labels) ? 1 : 0);
                        Log.i("Preferences change in ", "filter settings");
                    }
                }
            };

    private OnFragmentInteractionListener mListener;

    public MapFragment() {
    }

    public static Fragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Preference settings & ChangeListener
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        map_tilt = prefs.getBoolean("map_tilt", false);
        map_rotate = prefs.getBoolean("map_rotation", false);
        filter_autoclose = prefs.getBoolean("faButton_auto_close", false);
        filter_labels = prefs.getBoolean("faButton_labels", false);
        isFirstRunLocationDialoge = prefs.getBoolean("isFirstRunLocationDialoge", true);
        prefs.registerOnSharedPreferenceChangeListener(prefChangedListener);

        // sets map type to climb map
        showclimb = true;
        showSatellite_streets = false;
        zoomLevelMap = 10.0;

        // parameter for own option menu entries
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // view for the map and mapbox-token
        Mapbox.getInstance(getActivity(), getString(R.string.accessToken));
        view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) view.findViewById(R.id.mapviewmapbox);

        // FA-Menu & Buttons + the preference settings from user
        FAM_InOut = (FloatingActionMenu) view.findViewById(R.id.floating_action_menu_inout);
        fabIN = (FloatingActionButton) view.findViewById(R.id.floating_action_menu_In);
        fabIN.setLabelVisibility((filter_labels) ? 1 : 0);
        fabOut = (FloatingActionButton) view.findViewById(R.id.floating_action_menu_Out);
        fabOut.setLabelVisibility((filter_labels) ? 1 : 0);
        fabInOut = (FloatingActionButton) view.findViewById(R.id.floating_action_menu_InandOut);
        fabInOut.setLabelVisibility((filter_labels) ? 1 : 0);

        // more map stuff
        locationButton = (FloatingActionButton) view.findViewById(R.id.location_toggle_fab);
        mapView.onCreate(savedInstanceState);
        onMapReady(mapboxMap);

        // ShowCase Views for Mapflip- and Filterbutton
        // shows the Intro just once
        FancyShowCaseView ShowCaseViewMapFlip = new FancyShowCaseView.Builder(getActivity())
                //  .focusOn(view.findViewById(R.id.....))
                .focusCircleAtPosition(585, 110, 70) // Not relative!
                .title(getString(R.string.map_flip_show_case))
                .closeOnTouch(true)
                .backgroundColor(Color.argb(150, 128, 128, 128))
                .showOnce("map_flip")
                .build();

        FancyShowCaseView FancyShowCaseViewFilterMenu = new FancyShowCaseView.Builder(getActivity())
                //      .focusOn(view.findViewById(R.id.ShowCaseFilterbutton))
                .focusCircleAtPosition(88, 1040, 90) // Not relative!
                .title(getString(R.string.filter_button_show_case))
                .closeOnTouch(true)
                .backgroundColor(Color.argb(150, 128, 128, 128))
                .showOnce("map_filterMenu")
                .build();

        new FancyShowCaseQueue()
                .add(ShowCaseViewMapFlip)
                .add(FancyShowCaseViewFilterMenu)
                .show();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuItem itemMapTyp = menu.add(Menu.NONE, map_flip, Menu.NONE,
                showclimb
                        ? R.string.map_boulder
                        : R.string.map_climb);
        itemMapTyp.setIcon(showclimb
                ? R.drawable.ic_button_black
                : R.drawable.ic_button_white);

        MenuItem itemSatelliteMap = menu.add(Menu.NONE, item_satellite_streets, Menu.NONE,
                showSatellite_streets
                        ? "Standardansicht"
                        : "Satellitenansicht");

        itemSatelliteMap.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        itemMapTyp.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        getActivity().getMenuInflater().inflate(R.menu.menu_appbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // calls the item from options menu; for instance settings
        switch (id) {
            case R.id.item_settings:
                Intent intentsettings = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intentsettings);
                break;
            case R.id.item_over:
                Intent intentover = new Intent(getActivity(), OverActivity.class);
                startActivity(intentover);
                break;
            case R.id.map_flip:

                showSatellite_streets = false;
                showclimb = !showclimb;

                // Fires Snackbar Notification
                Snackbar snackbar = Snackbar
                        .make(view.findViewById(R.id.fragment_map),
                                !showclimb ? "Boulderspots" : "Kletterspots", Snackbar.LENGTH_LONG);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(!showclimb ? Color.BLACK : Color.WHITE);
                TextView textViewsnakebar = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textViewsnakebar.setTextColor(!showclimb ? Color.WHITE : Color.BLACK);
                snackbar.show();
                fromMapFlip = true;

                // reload map
                onMapReady(mapboxMap);
                // reload option menu
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().invalidateOptionsMenu();
                    }
                });
                break;
            case item_satellite_streets:
                showSatellite_streets = !showSatellite_streets;
                fromMapFlip = true;
                onMapReady(mapboxMap);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().invalidateOptionsMenu();
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onMapReady(MapboxMap mapboxMap) {

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {

                List<String> permissions = new ArrayList<String>();

                // If the Androidversion is higher than M, the app asks for runtime-permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hasFineLocationPermission = getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                    hasCoarseLocationPermission = getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
                }
                if (!permissions.isEmpty()) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_GET_LOCATION);
                }

                // two If-Checks for Runtime Permissions/ in these case fine and coarse location
                if (hasFineLocationPermission != 0) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_GET_LOCATION);
                }

                if (hasCoarseLocationPermission != 0) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_GET_LOCATION);
                }

                // Locationmanager which combines the GPS and the Network (WIFI and MobilePhoneNetwork) providers
                // plus one listener which is import for the regular updates
                locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                        && hasCoarseLocationPermission == 0) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 10, locationListener);
                }
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        && hasFineLocationPermission == 0) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 10, locationListener);
                }

                // requesting location for android smaller than marshmallow (API23)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500,
                            0, locationListener);
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }

                if (hasCoarseLocationPermission == 0) {
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500,
                                0, locationListener);
                        myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }

                if (hasFineLocationPermission == 0) {
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    } else {
                        showSettingsAlert();
                        locationManager.requestLocationUpdates("gps", 500, 0, locationListener);
                        myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }

                // some UserInterface settings
                mUiSettings = mapboxMap.getUiSettings();
                mUiSettings.setAttributionEnabled(false);
                mUiSettings.setAllGesturesEnabled(true);
                mUiSettings.setRotateGesturesEnabled(map_rotate);
                mUiSettings.setTiltGesturesEnabled(map_tilt);

                // clears map
                mapboxMap.clear();
                mapboxMap.setMyLocationEnabled(true);
                mapboxMap.setOnInfoWindowClickListener(MapFragment.this);

                // sets the base map
                if (!showSatellite_streets) {
                    if (showclimb) {
                        // map for climbing spots
                        mapboxMap.setStyleUrl("mapbox://styles/androidpiti/cioblqu8f00abbvntseh2qytp");
                    } else {
                        // map for boulder spots
                        mapboxMap.setStyleUrl(Style.DARK);
                    }
                } else {
                    mapboxMap.setStyleUrl(Style.SATELLITE_STREETS);
                }

                // set the title for map
                if (showclimb) {
                    getActivity().setTitle("Kletterspots");
                } else {
                    getActivity().setTitle("Boulderspots");
                }

                // Holds CameraPosition while MapFlip (change between Boulder and Climbing Map)
                if (fromMapFlip) {
                    // default location for map (Berlin Alexanderplatz)
                    LatLng mapPosition = mapboxMap.getCameraPosition().target;
                    zoomLevelMap = mapboxMap.getCameraPosition().zoom;
                    myLocation = new Location("l");
                    myLocation.setLatitude(mapPosition.getLatitude());
                    myLocation.setLongitude(mapPosition.getLongitude());
                    fromMapFlip = false;
                } else {
                    // if location is known move to users location, otherwise to city center
                    if (myLocation == null) {
                        // default location for map (Berlin Alexanderplatz)
                        myLocation = new Location("l");
                        myLocation.setLatitude(52.52001);
                        myLocation.setLongitude(13.40495);
                    }
                }

                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                        .zoom(zoomLevelMap)
                        .build();
                // map animation to position
                mapboxMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 6000);

                // loading data and sets the markers
                String filename = "spotsberlin5";
                try {
                    XMLParser parser = new XMLParser();
                    gml = parser.loadFile(filename, getResources(), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // default filter values
                inOutFilter = "indooroutdoor";

                // method that places all markers on the map
                setMarkers(mapboxMap, gml);

                // Button for Indoor-spots
                fabIN.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        inOutFilter = "indoor";
                        mapboxMap.clear();
                        setMarkers(mapboxMap, gml);
                        // some issues with close method
                        if (filter_autoclose)
                            FAM_InOut.close(filter_autoclose);
                    }
                });

                // Button for Outdoor-spots
                fabOut.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        inOutFilter = "outdoor";
                        mapboxMap.clear();
                        setMarkers(mapboxMap, gml);
                        // some issues with close method
                        if (filter_autoclose)
                            FAM_InOut.close(filter_autoclose);
                    }
                });

                // Button for In- & Outdoor-spots
                fabInOut.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        inOutFilter = "indooroutdoor";
                        mapboxMap.clear();
                        setMarkers(mapboxMap, gml);
                        // some issues with close method
                        if (filter_autoclose)
                            FAM_InOut.close(filter_autoclose);
                    }
                });

                // button for settting Camera to user location
                locationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        locationListener.onLocationChanged(myLocation);
                        if (myLocation != null) {
                            CameraPosition position = new CameraPosition.Builder()
                                    .target(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                                    .zoom(15)
                                    .build();
                            mapboxMap.animateCamera(CameraUpdateFactory
                                    .newCameraPosition(position), 5000);
                        }
                    }
                });
            }
        });
    }

    // method for setting the markers on the map
    public void setMarkers(MapboxMap mapboxMap, String gml) {

        XMLParser parser = new XMLParser();
        Document doc = parser.getDomElement(gml);
        NodeList nl = doc.getElementsByTagName(KEY_FM);

        // set markers on map
        for (int i = 0; i < nl.getLength(); i++) {

            Element e = (Element) nl.item(i);
            double LAT = Double.parseDouble(parser.getValue(e, KEY_LAT));
            double LONG = Double.parseDouble(parser.getValue(e, KEY_LONG));
            String name = parser.getValue(e, KEY_NAME);
            String use = parser.getValue(e, KEY_USE);
            String kRouten = parser.getValue(e, KEY_KROUT);
            String bRouten = parser.getValue(e, KEY_BROUT);
            String inout = parser.getValue(e, KEY_INOUT);

            // declares SetMarkers instance
            SetMarkers setmarkers = new SetMarkers(getActivity(), showclimb, mapboxMap, inOutFilter, LAT, LONG, name,
                    use, kRouten, bRouten, inout);
            setmarkers.setMarker();
        }
    }

    // method for infowindowbubble in map when is clicked;
    public boolean onInfoWindowClick(@NonNull final com.mapbox.mapboxsdk.annotations.Marker marker) {

        Intent intentSingleSpot = new Intent(getActivity().getApplicationContext(),
                ListItemActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("titel", marker.getTitle());
        bundle.putBoolean("fromMap", true);
        intentSingleSpot.putExtras(bundle);
        startActivity(intentSingleSpot);

        /*final Dialog dialog = new Dialog(getActivity());
        dialog.setTitle("Favoritenliste");
        dialog.setContentView(R.layout.activity_info_bubble);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        Button btnBtmLeft = (Button) dialog.findViewById(R.id.buttonNo);
        Button btnBtmRight = (Button) dialog.findViewById(R.id.buttonYes);

        btnBtmLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnBtmRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Adds a the spot to favorite's list
                try {
                    DBHandler db = new DBHandler(getActivity());
                    db.addSpot(new Spots(1, marker.getTitle(), marker.getPosition().getLatitude(), marker.getPosition().getLongitude(),
                            "dd", "dd", "dd", "dd", "dd", "dd", "dd", "dd", "dd"));
                    Log.d("database", "add Spot: " + marker.getTitle());
                } catch (Exception e) {
                    System.out.println("Error " + e.getMessage());
                }

                dialog.dismiss();
            }
        });

        TextView  text_question = (TextView) dialog.findViewById(R.id.text_question);
        text_question.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        text_question.setText("Spot: " + marker.getTitle() + "\n" + " zu Favoritenliste hinzufügen?");

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int dialogWidth = (int)(displayMetrics.widthPixels * 0.85);
        int dialogHeight = (int)(displayMetrics.heightPixels * 0.45);
        dialog.getWindow().setLayout(dialogWidth, dialogHeight);
        dialog.show();
*/
        return false;
    }

    // Asks user for location service
    public void showSettingsAlert() {

        // Fires a Dialog for Location service
        if (isFirstRunLocationDialoge) {
            alertDialog = new AlertDialog.Builder(getActivity());

            // setting dialog title 6 message
            alertDialog.setTitle("Standortbestimmung");
            alertDialog.setMessage("Die Standortbestimmung ist nicht aktiviert. Wollen Sie diese aktivieren?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(intent);
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // showe Alert message
            alertDialog.show();
        }

        isFirstRunLocationDialoge = false;
        prefs.edit().putBoolean("isFirstRunLocationDialoge", isFirstRunLocationDialoge).apply();
    }

    // lifecycle methods
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // mapView.onSaveInstanceState(outState);
        Log.d("lifecycle", "onSaveInstanceState invoked");
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Boulderspots");
        isFirstRunLocationDialoge = prefs.getBoolean("isFirstRunLocationDialoge", false);
        getActivity().setTitle("Kletterspots");
        Log.i("lifecycle", "onResume invoked");
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        prefs.edit().putBoolean("isFirstRunLocationDialoge", isFirstRunLocationDialoge).apply();
        Log.i("lifecycle", "onPause invoked");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("lifecycle", "onDestroy invoked");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        isFirstRunLocationDialoge = true;
        prefs.edit().putBoolean("isFirstRunLocationDialoge", isFirstRunLocationDialoge).apply();
        Log.d("lifecycle", "onDetach invoked");
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}