package climbberlin.de.mapapps.climbup.Helper;

import android.content.Context;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;

import climbberlin.de.mapapps.climbup.R;

/**
 * Created by Hagen on 06.03.2017.
 */

public class MarkerIconBuilder {

    private Context context;
    private Boolean showClimb;
    private String type, inout;


    /**
     * Constructor
     * @param context required for IconFactory.class
     * @param showClimb specifies which map will be displayed
     * @param type specifies if its a climb-, boulder-, or both type spot
     * @param inout specifies in-, out- or in- and outdoor of the spot
     **/

    // Constructor
    public MarkerIconBuilder(Context context, Boolean showClimb, String type, String inout) {
        this.context = context;
        this.showClimb = showClimb;
        this.type = type;
        this.inout = inout;
    }

    // Setter for Icon
    public Icon setIcon() {

        IconFactory iconFactory = IconFactory.getInstance(context);
        Icon icon = null;

        switch (type) {
            case "Bouldern":
                if (inout.equals("indoor/outdoor")) {
                    icon = iconFactory.fromResource(R.mipmap.ic_bouldern_in_out);
                }
                if (inout.equals("indoor")) {
                    icon = iconFactory.fromResource(R.mipmap.ic_bouldern_in);
                }
                if (inout.equals("outdoor")) {
                    icon = iconFactory.fromResource(R.mipmap.ic_bouldern_out);
                }
                return icon;
            case "Klettern":
                if (inout.equals("indoor/outdoor")) {
                    icon = iconFactory.fromResource(R.mipmap.ic_climb_in_out);
                }
                if (inout.equals("indoor")) {
                    icon = iconFactory.fromResource(R.mipmap.ic_climb_in);
                }
                if (inout.equals("outdoor")) {
                    icon = iconFactory.fromResource(R.mipmap.ic_climb_out);
                }
                return icon;
            default:
                if (showClimb) {
                    icon = iconFactory.fromResource(R.mipmap.ic_climb_in_out);
                } else {
                    icon = iconFactory.fromResource(R.mipmap.ic_bouldern_in_out);
                }
                return icon;
        }
    }
}
