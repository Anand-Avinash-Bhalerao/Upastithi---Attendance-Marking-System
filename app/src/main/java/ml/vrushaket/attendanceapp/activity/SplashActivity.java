package ml.vrushaket.attendanceapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

import ml.vrushaket.attendanceapp.R;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        firebaseAuth = FirebaseAuth.getInstance();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(firebaseAuth.getCurrentUser() != null){
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }else{
                    finish();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            }
        }, 3000);


    }
}