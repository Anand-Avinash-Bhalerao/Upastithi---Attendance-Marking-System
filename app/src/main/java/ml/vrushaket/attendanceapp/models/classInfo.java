package ml.vrushaket.attendanceapp.models;

public class classInfo {
    String classID;
    String classCourse;
    String classDesc;
    String classImage;
    String className;
    String totalPresent;
    String totalSessions;

    public classInfo(){

    }

    public classInfo(String classID, String classCourse, String classDesc, String classImage, String className, String totalPresent, String totalSessions) {
        this.classID = classID;
        this.classCourse = classCourse;
        this.classDesc = classDesc;
        this.classImage = classImage;
        this.className = className;
        this.totalPresent = totalPresent;
        this.totalSessions = totalSessions;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getClassCourse() {
        return classCourse;
    }

    public void setClassCourse(String classCourse) {
        this.classCourse = classCourse;
    }

    public String getClassDesc() {
        return classDesc;
    }

    public void setClassDesc(String classDesc) {
        this.classDesc = classDesc;
    }

    public String getClassImage() {
        return classImage;
    }

    public void setClassImage(String classImage) {
        this.classImage = classImage;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTotalPresent() {
        return totalPresent;
    }

    public void setTotalPresent(String totalPresent) {
        this.totalPresent = totalPresent;
    }

    public String getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(String totalSessions) {
        this.totalSessions = totalSessions;
    }
}
