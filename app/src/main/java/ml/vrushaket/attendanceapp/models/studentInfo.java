package ml.vrushaket.attendanceapp.models;

import java.io.Serializable;

public class studentInfo implements Serializable {
    String studentBranch;
    String studentClass;
    String studentEmail;
    String studentEnrollmentID;
    String studentName;
    String studentPhoto;
    String studentRollNo;
    String studentShift;
    String studentUID;

    public studentInfo(){
    }

    public studentInfo(String studentBranch, String studentClass, String studentEmail, String studentEnrollmentID, String studentName, String studentPhoto, String studentRollNo, String studentShift, String studentUID) {
        this.studentBranch = studentBranch;
        this.studentClass = studentClass;
        this.studentEmail = studentEmail;
        this.studentEnrollmentID = studentEnrollmentID;
        this.studentName = studentName;
        this.studentPhoto = studentPhoto;
        this.studentRollNo = studentRollNo;
        this.studentShift = studentShift;
        this.studentUID = studentUID;
    }

    public String getStudentBranch() {
        return studentBranch;
    }

    public void setStudentBranch(String studentBranch) {
        this.studentBranch = studentBranch;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentEnrollmentID() {
        return studentEnrollmentID;
    }

    public void setStudentEnrollmentID(String studentEnrollmentID) {
        this.studentEnrollmentID = studentEnrollmentID;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentPhoto() {
        return studentPhoto;
    }

    public void setStudentPhoto(String studentPhoto) {
        this.studentPhoto = studentPhoto;
    }

    public String getStudentRollNo() {
        return studentRollNo;
    }

    public void setStudentRollNo(String studentRollNo) {
        this.studentRollNo = studentRollNo;
    }

    public String getStudentShift() {
        return studentShift;
    }

    public void setStudentShift(String studentShift) {
        this.studentShift = studentShift;
    }

    public String getStudentUID() {
        return studentUID;
    }

    public void setStudentUID(String studentUID) {
        this.studentUID = studentUID;
    }
}
