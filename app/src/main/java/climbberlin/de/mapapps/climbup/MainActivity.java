package climbberlin.de.mapapps.climbup;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.webianks.easy_feedback.EasyFeedback;

import climbberlin.de.mapapps.climbup.Fragments.ListFragment;
import climbberlin.de.mapapps.climbup.Fragments.MapFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        MapFragment.OnFragmentInteractionListener, ListFragment.OnFragmentInteractionListener,
        FragmentManager.OnBackStackChangedListener {

    // objects for fragments
    Fragment toFragment = null;
    FragmentManager fragmentManager;
    Bundle bundle = new Bundle();
    // object for map typ
    // front = climb map, back = boulder map
    private boolean mShowClimb;
    private boolean appExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Debugging Tool
        //   Stetho.initializeWithDefaults(this);

        // sets toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // creates initial fragment
        if (savedInstanceState == null) {
            try {

                mShowClimb = true;
                fragmentManager = getFragmentManager();
                toFragment = new MapFragment();
                bundle.putBoolean("maptype", mShowClimb);
                toFragment.setArguments(bundle);
                // add front side (climb map)to layout
                fragmentManager.beginTransaction().replace(R.id.fragment, toFragment).commit();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        getFragmentManager().addOnBackStackChangedListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getFragmentManager().getBackStackEntryCount() < 1) {
                exitDialog();
                if (appExit) {
                    super.onBackPressed();
                }
            }else{
                super.onBackPressed();
            }
        }
    }

    // hanndaling navigation between fragments
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        FragmentManager fragmentManager = getFragmentManager();
        int id = item.getItemId();

        // shows map
        if (id == R.id.nav_map) {

            toFragment = new MapFragment();
            bundle.putBoolean("maptype", mShowClimb);
            toFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.fragment, toFragment).addToBackStack(null).commit();

            // shows Listview boulder- and climbspots
        } else if (id == R.id.nav_list_climb_boulder) {

            toFragment = new ListFragment();
            // 0 = Climbing and Boulderspots
            bundle.putInt("typ", 0);
            toFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.fragment, toFragment).addToBackStack(null).commit();

            // shows listview climbspots
        } else if (id == R.id.nav_list_climb) {

            toFragment = new ListFragment();
            // 1 = Climbingpots
            bundle.putInt("typ", 1);
            toFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.fragment, toFragment).addToBackStack(null).commit();

            // shows listview boulderspots
        } else if (id == R.id.nav_list_boulder) {

            toFragment = new ListFragment();
            // 2 = Boulderspots
            bundle.putInt("typ", 2);
            toFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.fragment, toFragment).addToBackStack(null).commit();

        }   // Coming soon
            /*else if (id == R.id.nav_list_favorites) {

            toFragment = new ListFragment();
            // 3 = Favoritenliste
            bundle.putInt("typ", 3);
            toFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.fragment, toFragment).addToBackStack(null).commit();

        //   calls Feedback dialog
        } */
        else if (id == R.id.nav_send) {

            // Feedback feature
            new EasyFeedback.Builder(this)
                    .withEmail("mapapps@posteo.de")
                    .withSystemInfo()
                    .build()
                    .start();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackStackChanged() {
        mShowClimb = (getFragmentManager().getBackStackEntryCount() > 0);
        // When the back stack changes, invalidate the options menu (action bar).
        invalidateOptionsMenu();
    }

    // method for listview
    public void callWebAdress(View view) {

        TextView textviewwebadress = (TextView) view.findViewById(R.id.textViewWebadress);
        String url = textviewwebadress.getText().toString();

        if (!url.equals("n.v.")) {
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://" + url;

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }

    // Dialog for App Exit
    public void exitDialog() {

        AlertDialog.Builder ExitDialog = new AlertDialog.Builder(this);
        ExitDialog.setTitle(getString(R.string.dialog_exit_title));
        ExitDialog.setMessage(getText(R.string.dialog_exit_question));
        ExitDialog.setCancelable(true);
        // On pressing Yes-button
        ExitDialog.setPositiveButton(getText(R.string.button_exit_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                appExit = true;
                onBackPressed();
            }
        });
        // On pressing No-button
        ExitDialog.setNegativeButton(getText(R.string.button_exit_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        ExitDialog.show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}