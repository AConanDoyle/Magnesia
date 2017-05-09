package climbberlin.de.mapapps.climbup.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import climbberlin.de.mapapps.climbup.Helper.XMLParser;
import climbberlin.de.mapapps.climbup.ListItemActivity;
import climbberlin.de.mapapps.climbup.OverActivity;
import climbberlin.de.mapapps.climbup.Preferences.SettingsActivity;
import climbberlin.de.mapapps.climbup.R;

public class ListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public ListFragment() {
    }

    // file input from raw folder
    String gml = null;

    // XML-nodes
    private static final String KEY_LAT = "ogr:LAT";
    private static final String KEY_LONG = "ogr:LONG";
    private static final String KEY_FM = "gml:featureMember";
    private static final String KEY_HEAD = "ogr:NAME";
    private static final String KEY_DISTRICT = "ogr:BEZIRK";
    private static final String KEY_STREET = "ogr:STRASSE";
    private static final String KEY_POSTALCODE = "ogr:PLZ";
    private static final String KEY_HOUSENR = "ogr:HAUSNR";
    private static final String KEY_TYPE = "ogr:NUTZEN";
    private static final String KEY_INOUT = "ogr:INOUT";
    private static final String KEY_KROUTEN = "ogr:KROUTEN";
    private static final String KEY_BROUTEN = "ogr:BROUTEN";
    private static final String KEY_MATERIAL = "ogr:MATERIAL";
    private static final String KEY_OPENING = "ogr:OEFFZEIT";
    private static final String KEY_PRICE = "ogr:PREIS";
    private static final String KEY_WEBADRESS = "ogr:HOMEPAGE";
    private static final String KEY_IMAGEID = "ogr:IMAID";

    // Arrays and List for Datahandaling
    ArrayList<Integer> imageid = new ArrayList<>();
    ArrayList<Double> lat = new ArrayList<>();
    ArrayList<Double> longC = new ArrayList<>();
    ArrayList<String> head = new ArrayList<>();
    ArrayList<String> type = new ArrayList<>();
    ArrayList<String> inout = new ArrayList<>();
    ArrayList<String> krouten = new ArrayList<>();
    ArrayList<String> brouten = new ArrayList<>();
    ArrayList<String> adress = new ArrayList<>();
    ArrayList<String> material = new ArrayList<>();
    ArrayList<String> opening = new ArrayList<>();
    ArrayList<String> webadress = new ArrayList<>();
    ArrayList<String> price = new ArrayList<>();
    private CustomList customList;
    private ListView listView;

    // View objects
    View view;
    Resources resources;

    public static ListFragment newInstance() {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resources = getActivity().getResources();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_list, container, false);
        getActivity().setTitle("Magnesia");

        // Choosing the Spottyps, for instanca climbing oder boulderspots
        Bundle bundle = getArguments();
        int typ = bundle.getInt("typ");
        if (bundle != null) {
            handleSpots(typ);
        } else {
            // default value 0; 0 = cimbing and Boulderspots
            handleSpots(0);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_appbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_settings:
                Intent intentsettings = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intentsettings);
                break;
            case R.id.item_over:
                Intent intentover = new Intent(getActivity(), OverActivity.class);
                startActivity(intentover);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void handleSpots(int typ) {

        try {
            XMLParser parser = new XMLParser();
            String filename = "spotsberlin5";
            gml = parser.loadFile(filename, resources, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        XMLParser parser = new XMLParser();
        Document doc = parser.getDomElement(gml);
        NodeList nl = doc.getElementsByTagName(KEY_FM);

        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);

            switch (typ) {
                case 0:
                    // Climbing- & Boulderpots
                    getActivity().setTitle("Kletter- & Boulderspots");
                    settToList(parser, e);
                    break;
                case 1:
                    // Climbing
                    if (parser.getValue(e, KEY_TYPE).equals("Klettern")) {
                        getActivity().setTitle("Kletterspots");
                        settToList(parser, e);
                    }
                    break;
                case 2:
                    // Boulderpots
                    if (parser.getValue(e, KEY_TYPE).equals("Bouldern")) {
                        getActivity().setTitle("Boulderspots");
                        settToList(parser, e);
                    }
                    break;
                case 3:
                    // Favoriteslist
                    // Coming soon
                    /*DBHandler db = new DBHandler(getActivity());
                    List<Spots> spots = db.getAllSpots();

                    int j = 0;
                    while (j < spots.size()) {

                        String name = spots.getClass().getName();

                        if (parser.getValue(e, KEY_HEAD).equals(name)) {
                            settToList(parser, e);
                        }

                        j++;
                    }*/
                    break;
                default:
                    settToList(parser, e);
                    break;
            }
        }

        head.removeAll(Collections.singleton(""));
        imageid.remove(Collections.singleton(""));
        Arrays.asList(type).removeAll(Collections.singleton(""));
        Arrays.asList(inout).removeAll(Collections.singleton(""));
        Arrays.asList(krouten).removeAll(Collections.singleton(""));
        Arrays.asList(brouten).removeAll(Collections.singleton(""));
        Arrays.asList(material).removeAll(Collections.singleton(""));
        Arrays.asList(opening).removeAll(Collections.singleton(""));
        Arrays.asList(price).removeAll(Collections.singleton(""));
        //    Arrays.asList(price).removeAll(Collections.singleton(""));
        Arrays.asList(adress).removeAll(Collections.singleton(""));
        //      Arrays.asList(Arrays.toString(lat)).removeAll(Collections.singleton(""));
        longC.removeAll(Collections.singleton(""));
        //       Arrays.asList(webadress).removeAll(Collections.singleton(""));


        customList = new CustomList(getActivity(), head, imageid, type, inout, krouten, brouten,
                material, opening, price, adress, lat, longC, webadress);

        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(customList);

        // ClickListener handle if a list item will be clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                View selectedItem = customList.getViewByPosition(i, listView);

                ImageView imageview = (ImageView) selectedItem.findViewById(R.id.imageView);
                //        ImageView imageid = imageview.findViewById(R.id.imageView1);

                TextView textViewheads = (TextView) selectedItem.findViewById(R.id.textViewTitels);
                String tvheads = textViewheads.getText().toString();

                TextView textkrouten = (TextView) selectedItem.findViewById(R.id.textViewKRouten);
                String tvkrouten = textkrouten.getText().toString();

                TextView textbrouten = (TextView) selectedItem.findViewById(R.id.textViewBRouten);
                String tvbrouten = textbrouten.getText().toString();

                TextView texttype = (TextView) selectedItem.findViewById(R.id.textViewType);
                String tvtype = texttype.getText().toString();

                TextView textViewinout = (TextView) selectedItem.findViewById(R.id.textViewInOUT);
                String tvinout = textViewinout.getText().toString();

                TextView textViewmaterial = (TextView) selectedItem.findViewById(R.id.textViewMaterial);
                String tvmaterial = textViewmaterial.getText().toString();

                TextView textViewopening = (TextView) selectedItem.findViewById(R.id.textViewopening);
                String tvopening = textViewopening.getText().toString();

                TextView textViewprice = (TextView) selectedItem.findViewById(R.id.textViewPrice);
                String tvprice = textViewprice.getText().toString();

                TextView textViewadress = (TextView) selectedItem.findViewById(R.id.textViewAdress);
                String tvadress = textViewadress.getText().toString();

                TextView textViewweb = (TextView) selectedItem.findViewById(R.id.textViewWebadress);
                String tvweb = textViewweb.getText().toString();

                // Change decimal seperator
                NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);

                TextView textViewlat = (TextView) selectedItem.findViewById(R.id.textViewLat);
                TextView textViewlong = (TextView) selectedItem.findViewById(R.id.textViewLong);

                Number numberlat = null;
                Number numberlong = null;

                try {
                    numberlat = format.parse(textViewlat.getText().toString());
                    numberlong = format.parse(textViewlong.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Double tvlat = numberlat.doubleValue();
                Double tvlong = numberlong.doubleValue();

                // String tvlong = textViewlong.getText().toString();

                Intent intentSingleSpot = new Intent(getActivity().getApplicationContext(),
                        ListItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("titel", tvheads);
                bundle.putString("krouten", tvkrouten);
                bundle.putString("brouten", tvbrouten);
                bundle.putString("use", tvtype);
                bundle.putString("inout", tvinout);
                bundle.putString("material", tvmaterial);
                bundle.putString("opening", tvopening);
                bundle.putString("price", tvprice);
                bundle.putString("adress", tvadress);
                bundle.putString("web", tvweb);
                bundle.putDouble("lat", tvlat);
                bundle.putDouble("long", tvlong);
                intentSingleSpot.putExtras(bundle);
                startActivity(intentSingleSpot);

            }
        });
    }

    // method for setting a single spot to a ArrayList
    public void settToList(XMLParser parser, Element e) {

        head.add(parser.getValue(e, KEY_HEAD));
        imageid.add(getResources().getIdentifier(parser.getValue(e, KEY_IMAGEID),
                "drawable", getActivity().getPackageName()));

        if (parser.getValue(e, KEY_KROUTEN).isEmpty()) {
            krouten.add("n.v.");
        } else {
            krouten.add(parser.getValue(e, KEY_KROUTEN));
        }

        if (parser.getValue(e, KEY_BROUTEN).isEmpty()) {
            brouten.add("n.v.");
        } else {
            brouten.add(parser.getValue(e, KEY_BROUTEN));
        }

        type.add(parser.getValue(e, KEY_TYPE));
        inout.add(parser.getValue(e, KEY_INOUT));
        material.add(parser.getValue(e, KEY_MATERIAL));
        price.add(parser.getValue(e, KEY_PRICE));
        opening.add(parser.getValue(e, KEY_OPENING));

        adress.add(parser.getValue(e, KEY_STREET) + " " + parser.getValue(e, KEY_HOUSENR) + ", "
                + parser.getValue(e, KEY_POSTALCODE) + " " + parser.getValue(e, KEY_DISTRICT));

        lat.add(Double.parseDouble(parser.getValue(e, KEY_LAT)));
        longC.add(Double.parseDouble(parser.getValue(e, KEY_LONG)));

        if (parser.getValue(e, KEY_WEBADRESS).isEmpty()) {
            webadress.add("n.v.");
        } else {
            webadress.add(parser.getValue(e, KEY_WEBADRESS));
        }
    }

    // deprecated
    public int[] remov_empty_elements (int[] arrayInput) {
        int[] arrayOutput = new int[arrayInput.length];
        for (int i = 0; i < arrayInput.length; i++) {
            if (arrayInput[i] != 0) {
                arrayOutput[i] = arrayInput[i];
            }
        }
        return arrayOutput;
    }

    // lifecycle methods
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    //    listView.onSaveInstanceState();
    }

    public void onPause() {
        super.onPause();
        Log.d("lifecycle", "onPause invoked");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
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
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
