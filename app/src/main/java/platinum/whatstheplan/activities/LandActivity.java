package platinum.whatstheplan.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LandActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_TIME = 2000;

    // ^this activity would be used as splash screen whenever the HomeActivity would take time to start

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        delayedJumpToHomeActivity();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        delayedJumpToHomeActivity();
    }

        private void delayedJumpToHomeActivity() {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent homeIntent = new Intent(LandActivity.this, HomeActivity.class);
                    finish();
                    startActivity(homeIntent);
                }
            }, SPLASH_DISPLAY_TIME);

        }
}
