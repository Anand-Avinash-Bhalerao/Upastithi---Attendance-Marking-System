package ml.vrushaket.attendanceapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ml.vrushaket.attendanceapp.R;
import ml.vrushaket.attendanceapp.models.instituteInfo;
import ml.vrushaket.attendanceapp.models.studentInfo;

public class LoginActivity extends AppCompatActivity {

    private TextView RegisterActivityIntent;
    private TextInputLayout userEmail,userPassword;
    private Button btnLogin;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();


        //attaching listener to button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCredentials();
            }
        });

        RegisterActivityIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });


    }
    void init(){
        //initializing views
        userEmail = findViewById(R.id.edtUserEmail);
        userPassword = findViewById(R.id.edtUserPassword);
        btnLogin = findViewById(R.id.btnLogin);
        RegisterActivityIntent = findViewById(R.id.tvRegisterIntent);
    }

    void checkCredentials(){
        int flag = 0;
        String email = userEmail.getEditText().getText().toString().trim();
        String password = userPassword.getEditText().getText().toString().trim();
        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
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

        if(!TextUtils.isEmpty(password) && !TextUtils.isEmpty(email)){
            flag = 0;
        }
        if(flag == 0){
            login(email,password);
        }


    }

    void login(String email, String password){
        LayoutInflater factory = LayoutInflater.from(this);
        final View DialogView = factory.inflate(R.layout.login_wait_dialog, null);
        final AlertDialog Dialog = new AlertDialog.Builder(this).create();
        Dialog.setView(DialogView);
        Dialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid Credentials, Please try again.", Toast.LENGTH_SHORT).show();
                        }
                        Dialog.dismiss();
                    }
                });
    }
}