package ml.vrushaket.attendanceapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import ml.vrushaket.attendanceapp.R;
import ml.vrushaket.attendanceapp.fragments.AttendanceRecordsFragment;
import ml.vrushaket.attendanceapp.fragments.MarkAttendanceFragment;
import ml.vrushaket.attendanceapp.fragments.HomeFragment;
import ml.vrushaket.attendanceapp.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();



        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setItemIconTintList(null);
        navigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_mark_attendance:
                    fragment = new MarkAttendanceFragment();
                    break;

                case R.id.navigation_attendancercords:
                    fragment = new AttendanceRecordsFragment();
                    break;

                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    break;

                case R.id.navigation_profile:
                    fragment = new ProfileFragment();
                    break;
            }

            return loadFragment(fragment);
        });
        if (savedInstanceState == null) {
            navigation.setSelectedItemId(R.id.navigation_home); // change to whichever id should be default
        }


        //need to add email verification after sign in initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        //if getCurrentUser does not returns null
        if(firebaseAuth.getCurrentUser()== null){
            startActivity(new Intent(MainActivity.this, SplashActivity.class));
        }

    }
    void init(){

    }


    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}