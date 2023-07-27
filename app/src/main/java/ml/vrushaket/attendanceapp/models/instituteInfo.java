package ml.vrushaket.attendanceapp.models;

public class instituteInfo {
    String instituteName;
    String instituteID;
    public instituteInfo() {

    }
    public instituteInfo(String instituteName, String instituteID) {
        this.instituteName = instituteName;
        this.instituteID = instituteID;
    }

    public String getInstituteName() {
        return instituteName;
    }

    public void setInstituteName(String instituteName) {
        this.instituteName = instituteName;
    }

    public String getInstituteID() {
        return instituteID;
    }

    public void setInstituteID(String instituteID) {
        this.instituteID = instituteID;
    }
}
