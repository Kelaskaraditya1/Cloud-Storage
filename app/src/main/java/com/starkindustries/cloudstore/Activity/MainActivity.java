package com.starkindustries.cloudstore.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.starkindustries.cloudstore.Keys.Keys;
import com.starkindustries.cloudstore.R;
import com.starkindustries.cloudstore.databinding.ActivityMainBinding;
public class MainActivity extends AppCompatActivity {
    public ActivityMainBinding binding;
    public SharedPreferences preferences;
    public SharedPreferences.Editor edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(MainActivity.this,R.layout.activity_main);
        preferences=getSharedPreferences(Keys.SHARED_PREFRENCES_NAME,MODE_PRIVATE);
        edit=preferences.edit();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(preferences.getBoolean(Keys.FLAG,false))
                    {
                        Intent inext = new Intent(MainActivity.this, DashBoard.class);
                        startActivity(inext);
                    }
                    else
                    {
                        Pair pairs[] = new Pair[2];
                        pairs[0]=new Pair<View,String>(binding.appLogo,"logo");
                        pairs[1]=new Pair<View,String>(binding.appName,"name");
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                        Intent inext = new Intent(MainActivity.this, LoginScreen.class);
                        startActivity(inext,options.toBundle());
                        finish();
                    }
                }
            },1000);
            return insets;
        });
    }
}