package ml.vrushaket.attendanceapp.models;

public class attendanceInfo {
    String attendanceDate;
    String attendanceTime;
    String attendanceNote;
    String attendanceStatus;
    public attendanceInfo(){

    }

    public attendanceInfo(String attendanceDate, String attendanceTime, String attendanceNote, String attendanceStatus) {
        this.attendanceDate = attendanceDate;
        this.attendanceTime = attendanceTime;
        this.attendanceNote = attendanceNote;
        this.attendanceStatus = attendanceStatus;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getAttendanceTime() {
        return attendanceTime;
    }

    public void setAttendanceTime(String attendanceTime) {
        this.attendanceTime = attendanceTime;
    }

    public String getAttendanceNote() {
        return attendanceNote;
    }

    public void setAttendanceNote(String attendanceNote) {
        this.attendanceNote = attendanceNote;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }
}

