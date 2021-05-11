package com.example.doctorave.ModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Patient implements Parcelable {

    int age;
    List<PatientImages> images;
    String patientId, name, gender, emailId, mobileNumber,
            appointmentDate, comments, issueIllness;

    public Patient() {
    }


    public Patient(int age, List<PatientImages> images, String patientId, String name,
                   String gender, String emailId, String mobileNumber,
                   String appointmentDate, String comments, String issueIllness) {
        this.age = age;
        this.images = images;
        this.patientId = patientId;
        this.name = name;
        this.gender = gender;
        this.emailId = emailId;
        this.mobileNumber = mobileNumber;
        this.appointmentDate = appointmentDate;
        this.comments = comments;
        this.issueIllness = issueIllness;
    }

    protected Patient(Parcel in) {
        age = in.readInt();
        images = in.createTypedArrayList(PatientImages.CREATOR);
        patientId = in.readString();
        name = in.readString();
        gender = in.readString();
        emailId = in.readString();
        mobileNumber = in.readString();
        appointmentDate = in.readString();
        comments = in.readString();
        issueIllness = in.readString();
    }

    public static final Creator<Patient> CREATOR = new Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

    public List<PatientImages> getImages() {
        return images;
    }

    public void setImages(List<PatientImages> images) {
        this.images = images;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getIssueIllness() {
        return issueIllness;
    }

    public void setIssueIllness(String issueIllness) {
        this.issueIllness = issueIllness;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(age);
        dest.writeTypedList(images);
        dest.writeString(patientId);
        dest.writeString(name);
        dest.writeString(gender);
        dest.writeString(emailId);
        dest.writeString(mobileNumber);
        dest.writeString(appointmentDate);
        dest.writeString(comments);
        dest.writeString(issueIllness);
    }
}
