package climbberlin.de.mapapps.climbup;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;

import androidx.annotation.RequiresApi;

import com.airbnb.lottie.LottieAnimationView;

public class SplashscreenActivity extends Activity {

    View view;
    LottieAnimationView animationView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = new View (this);
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        setContentView(R.layout.activity_splashscreen);
        int SPLASH_DISPLAY_LENGHT = 5500;                    // time howe long SplashScreen is visible

        // set up startanimation
        // To-do: implement animation listener
        animationView = findViewById(R.id.animation_view);
        animationView.setAnimation("startanimation.json");
        animationView.setImageAssetsFolder("images/");
        animationView.loop(false);
        animationView.playAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // starts MainActivity and deletes splash screen
                Intent mainIntent = new Intent(SplashscreenActivity.this, MainActivity.class);
                ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 50 ,
                        50, view.getWidth(), view.getHeight());
                startActivity(mainIntent, options.toBundle());
                SplashscreenActivity.this.finish();

            }
        }, SPLASH_DISPLAY_LENGHT);
    }
}
