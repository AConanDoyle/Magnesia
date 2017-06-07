package climbberlin.de.mapapps.climbup.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import climbberlin.de.mapapps.climbup.DB.DBHandler;
import climbberlin.de.mapapps.climbup.DB.Spots;
import climbberlin.de.mapapps.climbup.ListItemActivity;
import climbberlin.de.mapapps.climbup.OverActivity;
import climbberlin.de.mapapps.climbup.Preferences.SettingsActivity;
import climbberlin.de.mapapps.climbup.R;

public class FavoritesListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FavoritesListFragment() {
    }

    // file input from raw folder
    String gml = null;

    // XML-nodes
    private static final String KEY_SPOTID = "ogr:ID";
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

    // Arrays and List for data handling
    ArrayList<Integer> imageid = new ArrayList<>();
    ArrayList<Integer> spotid = new ArrayList<>();
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

    // View objects
    View view;
    private ListView listView;
    private TextView textViewNoContant, textViewNoContant_small;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoritesListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritesListFragment newInstance(String param1, String param2) {
        FavoritesListFragment fragment = new FavoritesListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_favorites_list, container, false);
        listView = (ListView) view.findViewById(R.id.listView_favorits);
        textViewNoContant = (TextView) view.findViewById(R.id.favorites_no_contant);
        textViewNoContant_small = (TextView) view.findViewById(R.id.favorites_no_contant_small);
        handleSpots();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    public void handleSpots() {

        setFavoriteSpots();

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


        customList = new CustomList(getActivity(), spotid, head, imageid, type, inout, krouten, brouten,
                material, opening, price, adress, lat, longC, webadress);

        listView = (ListView) view.findViewById(R.id.listView_favorits);
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

                TextView tVlat = (TextView) selectedItem.findViewById(R.id.textViewLat);
                Double tvlat = Double.parseDouble(tVlat.getText().toString().replace(',', '.'));

                TextView tVlong = (TextView) selectedItem.findViewById(R.id.textViewLong);
                Double tvlong = Double.parseDouble(tVlong.getText().toString().replace(',', '.'));

                TextView tVpotid = (TextView) selectedItem.findViewById(R.id.textViewSpotID);
                Integer tvspotID = Integer.parseInt(tVpotid.getText().toString());

                // put data into bundle for next Activity
                Intent intentSingleSpot = new Intent(getActivity().getApplicationContext(),
                        ListItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("spotid", tvspotID);
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

    private void setFavoriteSpots() {

        getActivity().setTitle(getText(R.string.nav_drawer_list_favs_spots));
        DBHandler db = new DBHandler(getActivity());
        List<Spots> spots = db.getAllSpots();

        if (spots.size() == 0) {
            listView.setVisibility(View.INVISIBLE);
            textViewNoContant.setVisibility(View.VISIBLE);
            textViewNoContant_small.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            textViewNoContant.setVisibility(View.INVISIBLE);
            textViewNoContant_small.setVisibility(View.INVISIBLE);
            int j = 0;
            while (j < spots.size()) {

                spotid.add(spots.get(j).getId());
                head.add(spots.get(j).getName());
                imageid.add(0);
                if (spots.get(j).getKrouten().isEmpty()) {
                    krouten.add("n.v.");
                } else {
                    krouten.add(spots.get(j).getKrouten());
                }

                if (spots.get(j).getBrouten().isEmpty()) {
                    brouten.add("n.v.");
                } else {
                    brouten.add(spots.get(j).getBrouten());
                }

                type.add(spots.get(j).getTyp());
                inout.add(spots.get(j).getInOut());
                material.add(spots.get(j).getMaterial());
                price.add(spots.get(j).getPrice());
                opening.add(spots.get(j).getOpening());
                adress.add(spots.get(j).getAddress());
                lat.add(spots.get(j).getLat());
                longC.add(spots.get(j).getLong());

                if (spots.get(j).getWeb().isEmpty()) {
                    webadress.add("n.v.");
                } else {
                    webadress.add(spots.get(j).getWeb());
                }
                j++;
            }
        }
    }

    @Override
    public void onResume() {
        customList.clear();
        handleSpots();
        super.onResume();
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
