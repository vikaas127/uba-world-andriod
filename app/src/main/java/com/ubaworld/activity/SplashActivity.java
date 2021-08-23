package com.ubaworld.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.ubaworld.R;

import static com.ubaworld.utils.Constants.IS_LOGIN;
import static com.ubaworld.utils.Constants.NOTIFICATION_TYPE;
import static com.ubaworld.utils.Constants.TYPE;
import static com.ubaworld.utils.Utils.getBooleanFromUserDefaults;

public class SplashActivity extends AppCompatActivity {
    Bundle b;
    boolean isFromMessage;
    String commentId;
    String commentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            if (getBooleanFromUserDefaults(SplashActivity.this, IS_LOGIN)) {
                if (getIntent().getExtras() != null && (getIntent().getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0) {
                    b = getIntent().getExtras();
                    Log.e("test", "splash");
                }
                if (b != null) {
                    isFromMessage = true;
                    for (String key : b.keySet()) {
                        Object value = b.get(key);
                        Log.d("SplashActivity: ", "Key: " + key + " Value: " + value);
                        commentId = String.valueOf(b.get("comment_id"));
                        commentType = String.valueOf(b.get("comment_type"));

                    }
                }
                Intent homeIntent = new Intent(SplashActivity.this, Dashboard.class);
                homeIntent.putExtra(TYPE, getIntent().getStringExtra(NOTIFICATION_TYPE));
                if (commentId != null && commentType != null) {
                    homeIntent.putExtra("comment_id", Integer.parseInt(commentId));
                    homeIntent.putExtra("comment_type", Integer.parseInt(commentType));
                }
                homeIntent.putExtra("isFromMessage", isFromMessage);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(homeIntent);
                finish();
            } else {
                Intent homeIntent = new Intent(SplashActivity.this, WelcomeActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(homeIntent);
            }
        }, 2000);
    }

}
