package ml.vrushaket.attendanceapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ml.vrushaket.attendanceapp.R;
import ml.vrushaket.attendanceapp.models.instituteInfo;
import ml.vrushaket.attendanceapp.models.studentInfo;

public class RegistrationActivity extends AppCompatActivity {

    private TextView loginActivityIntent,tvinstituteID,tvinstituteName;
    private TextInputLayout userEmail,userPassword,userEnrollment;
    private AutoCompleteTextView spnInstitute;
    private Button btnRegister;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    List<String> institutesIDs= new ArrayList<String>();
    List<String> institutesNames = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        firebaseAuth = FirebaseAuth.getInstance();

        //if getCurrentUser does not returns null
        if(firebaseAuth.getCurrentUser() != null){
            //that means user is already logged in, so close this activity
            finish();
            //and open main activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }


        init();



        setSpinner();


        //attaching listener to button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmStudentInfo();
            }
        });

        spnInstitute.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvinstituteID.setText(institutesIDs.get(position));   //This will be the institute id.
                tvinstituteName.setText(institutesNames.get(position));
                //Toast.makeText(RegistrationActivity.this, tvinstituteID.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });


        loginActivityIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });


    }
    void init(){
        //initializing views
        userEmail = findViewById(R.id.edtUserEmail);
        userPassword = findViewById(R.id.edtUserPassword);
        userEnrollment = findViewById(R.id.edtUserEnrollment);
        btnRegister = findViewById(R.id.btnRegister);
        loginActivityIntent = findViewById(R.id.tvLoginIntent);
        spnInstitute = findViewById(R.id.spnInstitute);
        tvinstituteID = findViewById(R.id.tvinsitiuteID);
        tvinstituteName = findViewById(R.id.tvinsitiuteName);
    }

    void setSpinner(){
        FirebaseDatabase.getInstance().getReference("institutesDB").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                institutesIDs.add("");
                institutesNames.add("Select Institute");
                tvinstituteID.setText("");
                tvinstituteName.setText("Select Institute");
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    FirebaseDatabase.getInstance().getReference("institutesDB/"+postSnapshot.getKey()+"/instituteInfo/").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            instituteInfo instituteInfo  = snapshot.getValue(instituteInfo.class);
                            institutesNames.add(instituteInfo.getInstituteName());
                            institutesIDs.add(instituteInfo.getInstituteID());
                            //Toast.makeText(RegistrationActivity.this,instituteInfo.getInstituteID(), Toast.LENGTH_SHORT).show();
                            ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(RegistrationActivity.this, android.R.layout.simple_spinner_item, institutesNames);
                            areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnInstitute.setAdapter(areasAdapter);

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
    void registerUser(studentInfo student){
        //getting email , password and enrollment from edit texts
        String email = userEmail.getEditText().getText().toString().trim();
        String password = userPassword.getEditText().getText().toString().trim();
        String enrollment = userEnrollment.getEditText().getText().toString().trim();
        String instituteID = tvinstituteID.getText().toString();

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        //if getCurrentUser does not returns null
        if(firebaseAuth.getCurrentUser() != null){
            //that means user is already logged in, so close this activity
            finish();

            //and open main activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        //if the email and password are not empty then displaying a progress dialog
        LayoutInflater factory = LayoutInflater.from(this);
        final View DialogView = factory.inflate(R.layout.register_wait_dialog, null);
        final AlertDialog Dialog = new AlertDialog.Builder(this).create();
        Dialog.setView(DialogView);
        Dialog.show();
        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(student.getStudentName())
                                       // .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("USERPROFILEUPDATE", "User profile updated.");
                                                }
                                            }
                                        });

                            }
                            FirebaseDatabase.getInstance().getReference().child("institutes")
                                    .child(instituteID).child("attendanceSystem").child("studentDB").child(enrollment.toUpperCase()).child("profileInfo").child("studentUID").setValue(firebaseAuth.getCurrentUser().getUid());

                            FirebaseDatabase.getInstance().getReference().child("stdinsDB")
                                    .child(firebaseAuth.getCurrentUser().getUid()).child("instituteID").setValue(instituteID);
                            FirebaseDatabase.getInstance().getReference().child("stdinsDB")
                                    .child(firebaseAuth.getCurrentUser().getUid()).child("enrollmentID").setValue(enrollment.toUpperCase());
                            finish();
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }else{
                            //display some message here
                            Toast.makeText(RegistrationActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        Dialog.dismiss();
                    }
                });

    }

    void confirmStudentInfo(){
        int flag =0;
        //getting email , password and enrollment from edit texts
        String email = userEmail.getEditText().getText().toString().trim();
        String password = userPassword.getEditText().getText().toString().trim();
        String enrollment = userEnrollment.getEditText().getText().toString().trim();
        String instituteID = tvinstituteID.getText().toString();


        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show();
            flag = 1;
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show();
            flag = 1;
            return;
        }

        if(TextUtils.isEmpty(enrollment)){
            Toast.makeText(this,"Please enter enrollment",Toast.LENGTH_SHORT).show();
            flag = 1;
            return;
        }

        if(tvinstituteName.getText().toString().equalsIgnoreCase("Select Institute")){
            Toast.makeText(this,"Please select Institute",Toast.LENGTH_SHORT).show();
            flag = 1;
            return;
        }

        if(!TextUtils.isEmpty(enrollment) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(email) && !tvinstituteName.getText().toString().equalsIgnoreCase("Select Institute") ){
            flag = 0;
        }

        if(flag == 0){
            FirebaseDatabase.getInstance().getReference().
                    child("institutes")
                    .child(instituteID).child("attendanceSystem")
                    .child("studentDB").child(enrollment.toUpperCase()).child("profileInfo").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    studentInfo studentSnap = snapshot.getValue(studentInfo.class);

                    if(snapshot != null){
                        if(studentSnap.getStudentEmail().equalsIgnoreCase(email)){
                            //Toast.makeText(RegistrationActivity.this,"User name: " + studentSnap.getStudentName() + ", class " + studentSnap.getStudentClass(),Toast.LENGTH_SHORT).show();
                            displayStudentInfoDialog(studentSnap);

                        }
//                    else{
//                        Toast.makeText(RegistrationActivity.this,"Email registerd with "+enrollment+" do not match with the entered email. Please enter registered email.",Toast.LENGTH_SHORT).show();
//                    }

                    }else{
                        Toast.makeText(RegistrationActivity.this,"No Student Found with Enrollment ID : "+enrollment,Toast.LENGTH_SHORT).show();

                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(RegistrationActivity.this,"Error : "+error.toString(),Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void displayStudentInfoDialog(studentInfo student) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View studentInfoDialogView = factory.inflate(R.layout.studentverificationdialog, null);
        final AlertDialog studentDialog = new AlertDialog.Builder(this).create();
        final TextView studentBranch = studentInfoDialogView.findViewById(R.id.tvBranch);
        final TextView studentClass = studentInfoDialogView.findViewById(R.id.tvClass);
        final TextView studentEmail = studentInfoDialogView.findViewById(R.id.tvEmail);
        final TextView studentEnrollmentID = studentInfoDialogView.findViewById(R.id.tvEnrollment);
        final TextView studentName = studentInfoDialogView.findViewById(R.id.tvName);
        final TextView studentRollNo = studentInfoDialogView.findViewById(R.id.tvRollno);
        final TextView studentShift = studentInfoDialogView.findViewById(R.id.tvShift);

        studentBranch.setText(student.getStudentBranch());
        studentClass.setText(student.getStudentClass());
        studentEmail.setText(student.getStudentEmail());
        studentEnrollmentID.setText(student.getStudentEnrollmentID());
        studentName.setText(student.getStudentName());
        studentRollNo.setText(student.getStudentRollNo());
        studentShift.setText(student.getStudentShift());

        studentDialog.setView(studentInfoDialogView);
        studentInfoDialogView.findViewById(R.id.btnConfirmation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(student);
                studentDialog.dismiss();
            }
        });

        studentDialog.show();
    }
}