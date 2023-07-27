package ml.vrushaket.attendanceapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ml.vrushaket.attendanceapp.R;
import ml.vrushaket.attendanceapp.activity.AttendanceActivity;
import ml.vrushaket.attendanceapp.adapters.subjectAdapter;
import ml.vrushaket.attendanceapp.listeners.RecyclerItemClickListener;
import ml.vrushaket.attendanceapp.models.classInfo;
import ml.vrushaket.attendanceapp.models.studentInfo;


public class AttendanceRecordsFragment extends Fragment {
    View view;
    private FirebaseAuth firebaseAuth;
    private RecyclerView subjectRV;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_attendance_records, container, false);
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
    void init(){
        subjectRV = view.findViewById(R.id.subjectRV);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
    }

    void setProfile(){
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
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
                            setClasses(instituteID,studentSnap);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    void setClasses(String instituteID, studentInfo student){
        FirebaseDatabase.getInstance().getReference().child("institutes").child(instituteID).child("attendanceSystem")
                .child("attendanceDB").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<classInfo> subjectList = new ArrayList<>();
                for (DataSnapshot datasnap: snapshot.getChildren()) {
                    FirebaseDatabase.getInstance().getReference().child("institutes").child(instituteID).child("attendanceSystem")
                            .child("attendanceDB").child(datasnap.getKey()).child("classInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            classInfo classInfo = snapshot.getValue(classInfo.class);

                            if(student.getStudentClass().equalsIgnoreCase(classInfo.getClassName())){


                                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("institutes").child(instituteID).child("attendanceSystem")
                                        .child("attendanceDB").child(String.valueOf(classInfo.getClassID())).child("attendanceInfo").child("sessionCount");
                                db.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int total_lectures = Integer.parseInt(snapshot.getValue().toString());
                                        classInfo.setTotalSessions(String.valueOf(total_lectures));
                                        FirebaseDatabase.getInstance().getReference().child("institutes").child(instituteID).child("attendanceSystem")
                                                .child("studentDB").child(student.getStudentEnrollmentID()).child("attendanceInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.hasChild(String.valueOf(classInfo.getClassID()))){
                                                    int total_present = Integer.parseInt(snapshot.child(String.valueOf(classInfo.getClassID())).getValue().toString());
                                                    classInfo.setTotalPresent(String.valueOf(total_present));
                                                }

                                                    subjectList.add(classInfo);
                                                    subjectAdapter subjectAdapter = new subjectAdapter(getActivity(), subjectList);
                                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                                                    subjectRV.setLayoutManager(linearLayoutManager);
                                                    subjectRV.setAdapter(subjectAdapter);
                                                    subjectAdapter.notifyDataSetChanged();
                                                    setRVListener(instituteID,student);


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

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    void setRVListener(String instituteID, studentInfo student){
        subjectRV.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), subjectRV ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        final TextView classID = (TextView) view.findViewById(R.id.idTVCourseID);
                        final TextView className = (TextView) view.findViewById(R.id.idTVCourseName);
                        Intent intent = new Intent(getActivity(), AttendanceActivity.class);
                        intent.putExtra("className",className.getText());
                        intent.putExtra("classID",classID.getText());
                        intent.putExtra("instituteID",instituteID);
                        intent.putExtra("enrollmentID",student.getStudentEnrollmentID());
                        startActivity(intent);

                    }

                    @Override public void onLongItemClick(View view, int position) {

                    }
                })
        );
    }


    void setPresentMarkers(String instituteID, classInfo classInfo, studentInfo student){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("institutes").child(instituteID).child("attendanceSystem")
               .child("attendanceDB").child(String.valueOf(classInfo.getClassID())).child("attendanceRecords");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total_lectures = (int) snapshot.getChildrenCount();
                classInfo.setTotalSessions(String.valueOf(total_lectures));
                FirebaseDatabase.getInstance().getReference().child("institutes").child(instituteID).child("attendanceSystem")
                        .child("studentDB").child(student.getStudentEnrollmentID()).child("attendanceInfo").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(String.valueOf(classInfo.getClassID()))){
                            int total_present = Integer.parseInt(snapshot.child(String.valueOf(classInfo.getClassID())).getValue().toString());

                            classInfo.setTotalPresent(String.valueOf(total_present));
                        }
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