package climbberlin.de.mapapps.climbup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class OverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // sets Over text
        setContentView(R.layout.activity_over);
        TextView over_1_1 = (TextView) findViewById(R.id.textover_main_1_1);
        over_1_1.setText(Html.fromHtml(getString(R.string.text_over_main_1_1)));
        TextView over_1_2 = (TextView) findViewById(R.id.textover_main_1_2);
        over_1_2.setText(Html.fromHtml(getString(R.string.text_over_main_1_2)));
        TextView contactMail = (TextView) findViewById(R.id.contact_mail);
        contactMail.setText(Html.fromHtml(getString(R.string.text_over_main_contact_mail)));
        TextView over_2 = (TextView) findViewById(R.id.textover_main_2);
        over_2.setText(Html.fromHtml(getString(R.string.text_over_main_2)));

        // sets app version name
        String versionName = BuildConfig.VERSION_NAME;
        TextView appVersion = (TextView) findViewById(R.id.textappversion);
        appVersion.setText(versionName);

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


    // call Mail intent
    public void sendMail(View v) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, null);
        emailIntent.setType("text/plain");
        emailIntent.setData(Uri.parse("mailto:" + "mapapps@posteo.de"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback: Send up-App");
        startActivity(Intent.createChooser(emailIntent, "Feedback Mail"));
    }

}