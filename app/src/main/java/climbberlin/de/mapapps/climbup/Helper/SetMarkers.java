package climbberlin.de.mapapps.climbup.Helper;

import android.content.Context;

import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

/**
 * Created by Hagen on 08.03.2017.
 */

public class SetMarkers {

    private Context context;
    private boolean showClimb;
    private MapboxMap mapboxMap;
    private String inOutFilter, name, use, kRouten, bRouten, inout;
    private Double LAT, Long;

    // Constructor
    public SetMarkers(Context context, Boolean showClimb, MapboxMap mapboxMap,
                      String inOutFilter, Double LAT, Double Long, String name,
                      String use, String kRouten, String bRouten, String inout) {
        this.context = context;
        this.showClimb = showClimb;
        this.mapboxMap = mapboxMap;
        this.inOutFilter = inOutFilter;
        this.LAT = LAT;
        this.Long = Long;
        this.name = name;
        this.use = use;
        this.kRouten = kRouten;
        this.bRouten = bRouten;
        this.inout = inout;
    }

    public void setMarker() {
        climbboulderFilter();
    }

    // filters the content for climbing and boulderspots
    private void climbboulderFilter() {

        if (showClimb) {
            if (use.equals("Klettern") || use.equals("Klettern & Bouldern")) {
                inoutFilter();
            }
        } else if (!showClimb) {

            if (use.equals("Bouldern") || use.equals("Klettern & Bouldern")) {
                inoutFilter();
            }
        } else {
            inoutFilter();
        }
    }



    private void inoutFilter() {
        switch (inOutFilter) {
            case "indoor":
                if (inout.equals("indoor") || inout.equals("indoor/outdoor")) {
                    setMarkerOnMap();
                }
                break;
            case "outdoor":
                if (inout.equals("outdoor") || inout.equals("indoor/outdoor")) {
                    setMarkerOnMap();
                }
                break;
            default:
                setMarkerOnMap();
                break;
        }
    }

    // sets single Markers on map
    private void setMarkerOnMap() {

        MarkerIconBuilder mIconBuilder = new MarkerIconBuilder(context, showClimb, use, inout);

        mapboxMap.addMarker(new MarkerViewOptions()
                .position(new LatLng(LAT, Long))
                .title(name)
                .icon(mIconBuilder.setIcon())
                .anchor(0.5f,1.0f)
                .snippet("\nKletter oder Boulderspot: " + use +
                        "\nKletterrouten: " + kRouten + "\nBoulderrouten: "
                        + bRouten + "\nIn- oder Outdoor: " + inout));
    }
}
