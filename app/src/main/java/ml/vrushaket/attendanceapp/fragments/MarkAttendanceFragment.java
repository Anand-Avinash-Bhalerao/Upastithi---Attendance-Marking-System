package ml.vrushaket.attendanceapp.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ml.vrushaket.attendanceapp.R;

public class MarkAttendanceFragment extends Fragment {

    View view;
    DecoratedBarcodeView dbvScanner;
    String scanCode;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mark_attendance, container, false);

        init();
        requestPermission();
        setScanner();

        return view;
    }

    void init(){
        dbvScanner = view.findViewById(R.id.dbv_scanner);
    }
    void requestPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 0);
            }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0 && grantResults.length < 1) {
            requestPermission();
        } else {
            dbvScanner.resume();
        }
    }
    void setScanner(){

        dbvScanner.setStatusText("Please Scan the QR Code");
        dbvScanner.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                pauseScanner();
                updateText(result.getText());


            }
            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {

            }
        });


    }



    protected void resumeScanner() {
        boolean isScanDone = false;
        if (!dbvScanner.isActivated())
            dbvScanner.resume();
        Log.d("peeyush-pause", "paused: false");
    }

    protected void pauseScanner() {
        dbvScanner.pause();
    }


    private void updateText(String scanCode) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy , hh:mm:ss a");
        Date date = new Date();
        String code = scanCode;
        //1632737811377_1632738481325_hd5hw6vsvhh    classID   attendanceID   uniqueCode
        int count = code.length() - code.replace("_", "").length();
        Log.d("_count",String.valueOf(count));
        if(count == 2){

            String QRclassID = scanCode.split("_")[0];
            String QRattendanceID = scanCode.split("_")[1];
            String QRuniqueCode = scanCode.split("_")[2];
            Toast.makeText(getContext(), scanCode, Toast.LENGTH_SHORT).show();
            firebaseAuth = FirebaseAuth.getInstance();
            if(firebaseAuth.getCurrentUser() != null){

                FirebaseDatabase.getInstance().getReference().child("stdinsDB").child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String enrollmentID = snapshot.child("enrollmentID").getValue().toString();
                        String instituteID = snapshot.child("instituteID").getValue().toString();

                        FirebaseDatabase.getInstance().getReference().child("institutes").child(instituteID).child("attendanceSystem")
                                .child("attendanceDB").child(QRclassID).child("enrolledStudents")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(enrollmentID)) {          // if student enrolled for class

                                    FirebaseDatabase.getInstance().getReference().child("institutes").child(instituteID).child("attendanceSystem")
                                            .child("attendanceDB").child(QRclassID).child("attendanceRecords").child(QRattendanceID).child("attendanceSheet").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                if(!snapshot.exists()){ //if record not found add student to present list
                                                    //match scan QR with QR on DB
                                                    FirebaseDatabase.getInstance().getReference().child("institutes").child(instituteID).child("attendanceSystem")
                                                            .child("attendanceDB")
                                                            .child(QRclassID)
                                                            .child("attendanceRecords")
                                                            .child(QRattendanceID)
                                                            .child("attendanceQR").child("attendanceQRC").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if(snapshot.exists()){
                                                                if(scanCode.equalsIgnoreCase(snapshot.getValue().toString())){
                                                                    FirebaseDatabase.getInstance().getReference().child("institutes").child(instituteID).child("attendanceSystem")
                                                                            .child("attendanceDB").child(QRclassID).child("attendanceRecords").child(QRattendanceID)
                                                                            .child("attendanceSheet").child(enrollmentID).setValue(formatter.format(date));

                                                                    //adding attendance count in studentDB
                                                                    FirebaseDatabase.getInstance().getReference().child("institutes").child(instituteID).child("attendanceSystem")
                                                                            .child("studentDB").child(enrollmentID).child("attendanceInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            if(snapshot.child(QRclassID).exists()){
                                                                                Toast.makeText(getContext(), String.valueOf(snapshot.child(QRclassID).getKey()+" "+snapshot.child(QRclassID).getValue()), Toast.LENGTH_LONG).show();
                                                                                FirebaseDatabase.getInstance().getReference().child("institutes").child(instituteID).child("attendanceSystem")
                                                                                        .child("studentDB").child(enrollmentID).child("attendanceInfo").child(QRclassID).setValue(Integer.parseInt(snapshot.child(QRclassID).getValue().toString())+1);

                                                                            }else{
                                                                                FirebaseDatabase.getInstance().getReference().child("institutes").child(instituteID).child("attendanceSystem")
                                                                                        .child("studentDB").child(enrollmentID).child("attendanceInfo").child(QRclassID).setValue(1);
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                        }
                                                                    });
//
                                                                    displaySuccessDialog();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });



                                                }else{      //else display attendance marked
                                                    displayErrorDialog();
                                                }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {}
                                    });



                                } else {

                                    // Don't exist! Do something.
                                    Toast.makeText(getActivity(),"Sorry !!! We are unable to identify you.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

        }else{
            display_error();

        }

    }
    private void display_error() {

        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View errorDialogView = factory.inflate(R.layout.attendance_error_gen, null);
        final AlertDialog errorDialog = new AlertDialog.Builder(getActivity()).create();
        errorDialog.setView(errorDialogView);
        errorDialog.show();
        resumeScanner();
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeScanner();
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseScanner();
    }

    void displaySuccessDialog(){
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View successDialogView = factory.inflate(R.layout.attendance_success, null);
        final AlertDialog successDialog = new AlertDialog.Builder(getActivity()).create();
        successDialog.setView(successDialogView);
        successDialog.show();
        resumeScanner();
    }
    void displayErrorDialog(){
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View errorDialogView = factory.inflate(R.layout.attendance_error, null);
        final AlertDialog errorDialog = new AlertDialog.Builder(getActivity()).create();
        errorDialog.setView(errorDialogView);
        errorDialog.show();
        resumeScanner();
    }
}