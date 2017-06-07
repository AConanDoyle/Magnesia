package climbberlin.de.mapapps.climbup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
import com.mapbox.mapboxsdk.maps.UiSettings;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;

import climbberlin.de.mapapps.climbup.DB.DBHandler;
import climbberlin.de.mapapps.climbup.DB.Spots;
import climbberlin.de.mapapps.climbup.Helper.MarkerIconBuilder;
import climbberlin.de.mapapps.climbup.Helper.XMLParser;
import climbberlin.de.mapapps.climbup.Preferences.SettingsActivity;

import static climbberlin.de.mapapps.climbup.R.id.addToFavorites;
import static climbberlin.de.mapapps.climbup.R.id.container;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class ListItemActivity extends AppCompatActivity {

    // objects for spot info´s
    private Double Lat, Long;
    private TextView textViewTitle, textViewtTyp, textViewKrouten, textViewBrouten, textViewInOut,
            textViewMaterial, textViewOpening, textViewPrice, textViewAdress, textViewWeb;
    private Integer SpotId;

    // objects for map
    MapView mapView;
    MapboxMap mapboxMap;

    // database object
    DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);

        db = new DBHandler(getBaseContext());

        textViewTitle = (TextView) findViewById(R.id.textViewTitels);
        textViewtTyp = (TextView) findViewById(R.id.textViewType);
        textViewKrouten = (TextView) findViewById(R.id.textViewKRouten);
        textViewBrouten = (TextView) findViewById(R.id.textViewBRouten);
        textViewInOut = (TextView) findViewById(R.id.textViewInOUT);
        textViewMaterial = (TextView) findViewById(R.id.textViewMaterial);
        textViewOpening = (TextView) findViewById(R.id.textViewopening);
        textViewPrice = (TextView) findViewById(R.id.textViewPrice);
        textViewAdress = (TextView) findViewById(R.id.textViewAdress);
        textViewWeb = (TextView) findViewById(R.id.textViewWebadress);

        Intent intentbundleData = getIntent();
        if (intentbundleData != null) {

            // If coming from map view
            if (intentbundleData.getBooleanExtra("fromMap", false)) {
                XMLParser parser = new XMLParser();
                String gml = null;
                try {
                    gml = parser.loadFile("spotsberlin6", getResources(), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Document doc = parser.getDomElement(gml);
                NodeList nl = doc.getElementsByTagName("gml:featureMember");

                for (int i = 0; i < nl.getLength(); i++) {
                    Element e = (Element) nl.item(i);

                    if (parser.getValue(e, "ogr:NAME")
                            .equals(intentbundleData.getStringExtra("titel"))) {

                        textViewTitle.setText(parser.getValue(e, "ogr:NAME"));
                        textViewtTyp.setText(parser.getValue(e, "ogr:NUTZEN"));
                        textViewKrouten.setText(parser.getValue(e, "ogr:KROUTEN"));
                        textViewBrouten.setText(parser.getValue(e, "ogr:BROUTEN"));
                        textViewInOut.setText(parser.getValue(e, "ogr:INOUT"));
                        textViewMaterial.setText(parser.getValue(e, "ogr:MATERIAL"));
                        textViewOpening.setText(parser.getValue(e, "ogr:OEFFZEIT"));
                        textViewPrice.setText(parser.getValue(e, "ogr:PREIS"));
                        textViewAdress.setText(parser.getValue(e, "ogr:STRASSE") + " "
                                + parser.getValue(e, "ogr:HAUSNR") + ", " + parser.getValue(e, "ogr:PLZ")
                                + " " + parser.getValue(e, "ogr:BEZIRK"));
                        textViewWeb.setText(parser.getValue(e, "ogr:HOMEPAGE"));
                        Lat = parseDouble(parser.getValue(e, "ogr:LAT"));
                        Long = parseDouble(parser.getValue(e, "ogr:LONG"));
                        SpotId = parseInt(parser.getValue(e, "ogr:ID"));
                    }
                }
            } else {

                textViewTitle.setText(intentbundleData.getStringExtra("titel"));
                textViewtTyp.setText(intentbundleData.getStringExtra("use"));
                textViewKrouten.setText(intentbundleData.getStringExtra("krouten"));
                textViewBrouten.setText(intentbundleData.getStringExtra("brouten"));
                textViewInOut.setText(intentbundleData.getStringExtra("inout"));
                textViewMaterial.setText(intentbundleData.getStringExtra("material"));
                textViewOpening.setText(intentbundleData.getStringExtra("opening"));
                textViewPrice.setText(intentbundleData.getStringExtra("price"));
                textViewAdress.setText(intentbundleData.getStringExtra("adress"));
                textViewWeb.setText(intentbundleData.getStringExtra("web"));

                Lat = intentbundleData.getDoubleExtra("lat", 52.52001);
                Long = intentbundleData.getDoubleExtra("long", 13.40495);
                SpotId = intentbundleData.getIntExtra("spotid", 999);
            }
        }

        Mapbox.getInstance(this, getString(R.string.accessToken));


        // Create supportMapFragment
        SupportMapFragment mapFragment;
        if (savedInstanceState == null) {

            // Create fragment for container
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Build mapboxMap
            MapboxMapOptions options = new MapboxMapOptions();
            if (textViewtTyp.getText().toString().equals("Klettern")) {
                options.styleUrl("mapbox://styles/androidpiti/cioblqu8f00abbvntseh2qytp");
            } else {
                options.styleUrl(Style.DARK);
            }
            options.camera(new CameraPosition.Builder()
                    .target(new LatLng(Lat, Long))
                    .zoom(9)
                    .build());

            // Create map fragment
            mapFragment = SupportMapFragment.newInstance(options);

            // Add map fragment
            transaction.add(container, mapFragment, "com.mapbox.map");
            transaction.commit();
        } else {
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentByTag("com.mapbox.map");
        }

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                // some UserInterface map settings
                UiSettings mUiSettings;
                mUiSettings = mapboxMap.getUiSettings();
                mUiSettings.setAttributionEnabled(false);
                mUiSettings.setAllGesturesEnabled(true);
                mUiSettings.setRotateGesturesEnabled(false);
                mUiSettings.setTiltGesturesEnabled(false);

                // sets View for map
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(Lat, Long))
                        .zoom(13)
                        .build();

                // Zoom in for view
                mapboxMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 6000);

                // sets the pin color
                boolean climbOrBoulder;
                climbOrBoulder = textViewtTyp.getText().toString().equals("Klettern");

                MarkerIconBuilder mIconBuilder = new MarkerIconBuilder(getApplicationContext(), climbOrBoulder,
                        textViewtTyp.getText().toString(), textViewInOut.getText().toString());

                // adds single marker on map
                mapboxMap.addMarker(new MarkerViewOptions()
                        .position(new LatLng(Lat, Long))
                        .title(textViewTitle.getText().toString())
                        .icon(mIconBuilder.setIcon())
                        .anchor(0.5f, 1.0f)
                        .visible(true));

            }
        });

        final ScrollView mainScrollView = (ScrollView) findViewById(R.id.list_item);
        ImageView transparentImageView = (ImageView) findViewById(R.id.transparent_image);

        // little workaround for scrollview / map problem
        // listener gets active, when user scrolls down or up on map
        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem itemFavoriteButton = menu.add(Menu.NONE, addToFavorites, Menu.NONE,
                db.checkIfSpotExists(SpotId)
                        ? R.string.favorite_yes
                        : R.string.favorite_no);

        itemFavoriteButton.setIcon(db.checkIfSpotExists(SpotId)
                ? R.drawable.ic_favorite
                : R.drawable.ic_favorite_border);

        itemFavoriteButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        this.getMenuInflater().inflate(R.menu.menu_appbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_settings:
                Intent intentsettings = new Intent(this, SettingsActivity.class);
                startActivity(intentsettings);
                break;
            case R.id.item_over:
                Intent intentover = new Intent(this, OverActivity.class);
                startActivity(intentover);
                break;
            case addToFavorites:
                // Adds a the spot to favorite's list
                if (!db.checkIfSpotExists(SpotId)) {
                    try {
                        db.addSpot(new Spots(
                                SpotId,
                                textViewTitle.getText().toString(),
                                Lat,
                                Long,
                                textViewInOut.getText().toString(),
                                textViewtTyp.getText().toString(),
                                textViewKrouten.getText().toString(),
                                textViewBrouten.getText().toString(),
                                textViewMaterial.getText().toString(),
                                textViewOpening.getText().toString(),
                                textViewPrice.getText().toString(),
                                textViewAdress.getText().toString(),
                                textViewWeb.getText().toString()));
                        Log.d("database", "add Spot: " + textViewTitle.getText().toString());
                        showSnackeBar(true);
                    } catch (Exception e) {
                        Log.e("Error ", e.getMessage());
                    }
                } else {
                    // delete spot
                    try {
                        db.deleteSpot(new Spots(
                                SpotId,
                                textViewTitle.getText().toString(),
                                Lat,
                                Long,
                                textViewInOut.getText().toString(),
                                textViewtTyp.getText().toString(),
                                textViewKrouten.getText().toString(),
                                textViewBrouten.getText().toString(),
                                textViewMaterial.getText().toString(),
                                textViewOpening.getText().toString(),
                                textViewPrice.getText().toString(),
                                textViewAdress.getText().toString(),
                                textViewWeb.getText().toString()));
                        Log.d("database", "update Spot: " + textViewTitle.getText().toString());
                        showSnackeBar(false);
                    } catch (Exception e) {
                        Log.e("Error ", e.getMessage());
                    }
                }
                // reload option menu
                Handler mHandler = new Handler();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        invalidateOptionsMenu();
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void callRouting(View v) {
        Uri gmmIntentUri = Uri.parse("geo:0,0" + "?q=" + Lat + "," + Long);
        Intent routIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        startActivity(routIntent);
    }

    public void callWebAdress(View v) {

        TextView textviewwebadress = (TextView) v.findViewById(R.id.textViewWebadress);
        String url = textviewwebadress.getText().toString();

        if (!url.equals("n.v.")) {
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://" + url;

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }

    public void showSnackeBar(Boolean spotAdded) {
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.activity_list_item),
                        spotAdded ? getText(R.string.spot_added) : getText(R.string.spot_deleted),
                        Snackbar.LENGTH_LONG);
                snackbar.show();
    }

    // lifecycle methods
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void onPause() {
        super.onPause();
        Log.d("lifecycle", "onPause invoked");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("lifecycle", "onResume invoked");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d("lifecycle", "onRestart invoked");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("lifecycle", "onLowMemory invoked");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("lifecycle", "onDestroy invoked");
    }

}