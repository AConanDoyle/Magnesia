package climbberlin.de.mapapps.climbup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
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

import climbberlin.de.mapapps.climbup.Helper.MarkerIconBuilder;

public class ListItemActivity extends AppCompatActivity  {

    private Intent intentbundleData;

    // objects for spot info´s
    private Double Lat, Long;

    // objects for map
    MapView mapView;
    MapboxMap mapboxMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);

        TextView textViewTitle = (TextView) findViewById(R.id.textViewTitels);
        TextView textViewtTyp = (TextView) findViewById(R.id.textViewType);
        TextView textViewKrouten = (TextView) findViewById(R.id.textViewKRouten);
        TextView textViewBrouten = (TextView) findViewById(R.id.textViewBRouten);
        TextView textViewInOut = (TextView) findViewById(R.id.textViewInOUT);
        TextView textViewMaterial = (TextView) findViewById(R.id.textViewMaterial);
        TextView textViewOpening = (TextView) findViewById(R.id.textViewopening);
        TextView textViewPrice = (TextView) findViewById(R.id.textViewPrice);
        TextView textViewAdress = (TextView) findViewById(R.id.textViewAdress);
        TextView textViewWeb = (TextView) findViewById(R.id.textViewWebadress);

        intentbundleData = getIntent();
        if (intentbundleData != null) {

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

            Lat = intentbundleData.getDoubleExtra("lat", 52.52001 );
            Long = intentbundleData.getDoubleExtra("long", 13.40495);
        }

        Mapbox.getInstance(this, getString(R.string.accessToken));


        // Create supportMapFragment
        SupportMapFragment mapFragment;
        if (savedInstanceState == null) {

            // Create fragment for container
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Build mapboxMap
            MapboxMapOptions options = new MapboxMapOptions();
            if(intentbundleData.getStringExtra("use").equals("Klettern")){
                options.styleUrl("mapbox://styles/androidpiti/cioblqu8f00abbvntseh2qytp");
            }else{
                options.styleUrl(Style.DARK);
            }
            options.camera(new CameraPosition.Builder()
                    .target(new LatLng(Lat, Long))
                    .zoom(9)
                    .build());

            // Create map fragment
            mapFragment = SupportMapFragment.newInstance(options);

            // Add map fragment
            transaction.add(R.id.container, mapFragment, "com.mapbox.map");
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
                climbOrBoulder = intentbundleData.getStringExtra("use").equals("Klettern");

                MarkerIconBuilder mIconBuilder = new MarkerIconBuilder(getApplicationContext(),climbOrBoulder,
                        intentbundleData.getStringExtra("use"), intentbundleData.getStringExtra("inout"));

                // adds single marker on map
                mapboxMap.addMarker(new MarkerOptions()
                        .title(intentbundleData.getStringExtra("titel"))
                        .icon(mIconBuilder.setIcon())
                        .position(new LatLng(Lat, Long)));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
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