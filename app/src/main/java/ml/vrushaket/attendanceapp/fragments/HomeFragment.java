package ml.vrushaket.attendanceapp.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;

import ml.vrushaket.attendanceapp.R;
import ml.vrushaket.attendanceapp.adapters.subjectAdapter;
import ml.vrushaket.attendanceapp.models.classInfo;
import ml.vrushaket.attendanceapp.models.studentInfo;


public class HomeFragment extends Fragment {

    View view;
    private FirebaseAuth firebaseAuth;
    TextView tvPercent, tvRatio;
    CircularProgressBar circularProgressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    MaterialCardView attendaceCV;

    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_home, container, false);
        init();
        setProfile();

        // SetOnRefreshListener on SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                setProfile();
            }
        });
        return view;
    }


     void init() {
        tvPercent = view.findViewById(R.id.tvPercent);
        tvRatio = view.findViewById(R.id.tvRatio);
        circularProgressBar = view.findViewById(R.id.circularProgressBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        attendaceCV = view.findViewById(R.id.attendancealertCV);
    }

    void setProfile() {
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            FirebaseDatabase.getInstance().getReference().child("stdinsDB").child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String enrollmentID = snapshot.child("enrollmentID").getValue().toString();
                    String instituteID = snapshot.child("instituteID").getValue().toString();

                    FirebaseDatabase.getInstance().getReference().child("institutes").child(instituteID).child("attendanceSystem")
                            .child("studentDB").child(enrollmentID).child("profileInfo").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            studentInfo studentSnap = snapshot.getValue(studentInfo.class);
                            setClasses(instituteID, studentSnap);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    void setClasses(String instituteID, studentInfo student) {
        FirebaseDatabase.getInstance().getReference().child("institutes").child(instituteID).child("attendanceSystem")
                .child("attendanceDB").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long total = 0;
                long[] ptotal = {0};
                for (DataSnapshot datasnap : snapshot.getChildren()) {

                    if(datasnap.child("classInfo").child("className").getValue().toString().equalsIgnoreCase(student.getStudentClass())){
                        long count = Long.parseLong(datasnap.child("attendanceInfo").child("sessionCount").getValue().toString());
                        total+=count;
                    }
                }


                DatabaseReference db1 = FirebaseDatabase.getInstance().getReference().child("institutes").child(instituteID).child("attendanceSystem")
                        .child("studentDB").child(student.getStudentEnrollmentID()).child("attendanceInfo");
                long finalTotal = total;
                db1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int total_present =0;
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if(dataSnapshot.getValue() != null){
                                total_present +=Integer.parseInt(dataSnapshot.getValue().toString());
                            }

                        }
                        
                        tvRatio.setText(String.valueOf(total_present)+"/"+ String.valueOf(finalTotal));
                        float percent = (Float.parseFloat(String.valueOf(total_present)) / Float.parseFloat(String.valueOf(finalTotal))) * 100;
                        if(percent < 75){
                            attendaceCV.setVisibility(View.VISIBLE);
                        }else{
                            attendaceCV.setVisibility(View.GONE);
                        }
                        attendaceCV.setVisibility(View.VISIBLE);
                        tvPercent.setText((int) percent+"%");
                        // Set Progress
                        circularProgressBar.setProgress(Float.parseFloat(String.valueOf(total_present)));
                        // or with animation
                        circularProgressBar.setProgressWithAnimation(percent, Long.valueOf(2000)); // =1s

                        // Set Progress Max
                        circularProgressBar.setProgressMax(100f);

                        if(percent < 75){
                            circularProgressBar.setProgressBarColorStart(Color.parseColor("#ff4d4d"));
                            circularProgressBar.setProgressBarColorEnd(Color.parseColor("#ff4d4d"));

                        } else{
                            circularProgressBar.setProgressBarColorStart(Color.DKGRAY);
                            circularProgressBar.setProgressBarColorEnd(Color.DKGRAY);
                        }

                        circularProgressBar.setProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);
                        circularProgressBar.setProgressBarWidth(10f); // in DP
                        circularProgressBar.setBackgroundProgressBarWidth(6f); // in DP
                        circularProgressBar.setRoundBorder(false);
                        circularProgressBar.setStartAngle(0f);
                        circularProgressBar.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




}