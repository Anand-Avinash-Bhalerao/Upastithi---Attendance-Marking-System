package ml.vrushaket.attendanceapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ml.vrushaket.attendanceapp.R;
import ml.vrushaket.attendanceapp.adapters.attendanceAdapter;
import ml.vrushaket.attendanceapp.models.attendanceInfo;
import ml.vrushaket.attendanceapp.models.classInfo;

public class AttendanceActivity extends AppCompatActivity {

    String className,classID,instituteID,enrollmentID;
    TextView classNameTV;
    private RecyclerView attendaneListRV;
    int total =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        className = getIntent().getStringExtra("className");
        classID = getIntent().getStringExtra("classID");
        instituteID = getIntent().getStringExtra("instituteID");
        enrollmentID = getIntent().getStringExtra("enrollmentID");
        init();
        setHeader();
        xyz();
    }

    void init(){
        classNameTV = findViewById(R.id.classNameMsg);
        attendaneListRV = findViewById(R.id.attendanceListRV);

    }

    void setHeader(){
        classNameTV.setText(className+" Attendance");
    }

    void xyz(){

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("institutes").child(instituteID).child("attendanceSystem")
                .child("attendanceDB").child(classID).child("attendanceRecords");

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                total = (int) snapshot.getChildrenCount();
                ArrayList<attendanceInfo> attendanceList = new ArrayList<>();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String attendaceIDs = dataSnapshot.getKey();

                    db.child(attendaceIDs).child("attendanceSheet").child(enrollmentID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    db.child(attendaceIDs).child("attendanceInfo").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String date = snapshot.child("attendanceTime").getValue().toString().split(",")[0];
                                            String time = snapshot.child("attendanceTime").getValue().toString().split(",")[1];
                                            String note = snapshot.child("attendanceNote").getValue().toString();
                                            attendanceInfo attendanceInfo = new attendanceInfo(date,time,note,"Present");
                                            attendanceList.add(attendanceInfo);
                                            attendanceAdapter attendanceAdapter = new attendanceAdapter(AttendanceActivity.this, attendanceList);
                                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AttendanceActivity.this, LinearLayoutManager.VERTICAL, false);
                                            attendaneListRV.setLayoutManager(linearLayoutManager);
                                            attendaneListRV.setAdapter(attendanceAdapter);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }else{
                                    db.child(attendaceIDs).child("attendanceInfo").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String date = snapshot.child("attendanceTime").getValue().toString().split(",")[0];
                                            String time = snapshot.child("attendanceTime").getValue().toString().split(",")[1];
                                            String note = snapshot.child("attendanceNote").getValue().toString();
                                            attendanceInfo attendanceInfo = new attendanceInfo(date,time,note,"Absent");
                                            attendanceList.add(attendanceInfo);
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}