package ml.vrushaket.attendanceapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ml.vrushaket.attendanceapp.R;
import ml.vrushaket.attendanceapp.activity.LoginActivity;
import ml.vrushaket.attendanceapp.models.studentInfo;


public class ProfileFragment extends Fragment {

    View view;
    Button btnLogout;
    TextView tvStudentName,tvEmail,tvBranch,tvClass,tvShift,tvEnrollment,tvRollno;
    private FirebaseAuth firebaseAuth;

    ImageView imgStudentImage;

    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_profile, container, false);
        init();
        setProfile();
        logout();

        return view;
    }

    void init(){
        tvStudentName  = view.findViewById(R.id.tvstudentName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvBranch = view.findViewById(R.id.tvBranch);
        tvClass = view.findViewById(R.id.tvClass);
        tvShift = view.findViewById(R.id.tvShift);
        tvEnrollment = view.findViewById(R.id.tvEnrollment);
        tvRollno = view.findViewById(R.id.tvRollno);
        imgStudentImage = view.findViewById(R.id.imgStudentImage);
        btnLogout = view.findViewById(R.id.btnLogout);
    }

    void setProfile(){
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){

            FirebaseDatabase.getInstance().getReference().child("stdinsDB").child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String enrollmentID = snapshot.child("enrollmentID").getValue().toString();
                    String instituteID = snapshot.child("instituteID").getValue().toString();
                    //Toast.makeText(getActivity(), enrollmentID+" "+instituteID, Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference().child("institutes").child(instituteID).child("attendanceSystem")
                            .child("studentDB").child(enrollmentID).child("profileInfo").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            studentInfo studentSnap = snapshot.getValue(studentInfo.class);
                            setData(studentSnap);
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

    void setData(studentInfo student){
        tvStudentName.setText(student.getStudentName());
        tvEmail.setText(student.getStudentEmail());
        tvBranch.setText(student.getStudentBranch());
        tvClass.setText(student.getStudentClass());
        tvShift.setText(student.getStudentShift());
        tvEnrollment.setText(student.getStudentEnrollmentID());
        tvRollno.setText(student.getStudentRollNo());

    }
    void logout(){
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                getActivity().finish();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
    }
}