package com.example.doctorave.ModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

public class PatientImages implements Parcelable {

    String image;
    long dateCreated;

    PatientImages(){

    }

    public PatientImages(String image, long dateCreated) {
        this.image = image;
        this.dateCreated = dateCreated;
    }

    protected PatientImages(Parcel in) {
        image = in.readString();
        dateCreated = in.readLong();
    }

    public static final Creator<PatientImages> CREATOR = new Creator<PatientImages>() {
        @Override
        public PatientImages createFromParcel(Parcel in) {
            return new PatientImages(in);
        }

        @Override
        public PatientImages[] newArray(int size) {
            return new PatientImages[size];
        }
    };

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeLong(dateCreated);
    }
}
