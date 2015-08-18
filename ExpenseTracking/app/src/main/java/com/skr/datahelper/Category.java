package com.skr.datahelper;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sreejithkr on 25/06/15.
 */
public class Category implements Parcelable {

   private static final String TABLE_NAME_CATEGORY = "category";
    protected Integer CATEGORY_ID ;
    protected String CATEGORY_NAME ;
    protected Boolean IFEXPENSE ;
protected Category(){

}
    public Category(Integer CATEGORY_ID, String CATEGORY_NAME, Boolean IFEXPENSE) {
        this.CATEGORY_ID = CATEGORY_ID;
        this.CATEGORY_NAME = CATEGORY_NAME;
        this.IFEXPENSE = IFEXPENSE;
    }



    public Integer getCATEGORY_ID() {
        return CATEGORY_ID;
    }

    public void setCATEGORY_ID(Integer CATEGORY_ID) {
        this.CATEGORY_ID = CATEGORY_ID;
    }

    public String getCATEGORY_NAME() {
        return CATEGORY_NAME;
    }

    public void setCATEGORY_NAME(String CATEGORY_NAME) {
        this.CATEGORY_NAME = CATEGORY_NAME;
    }

    public Boolean getIFEXPENSE() {
        return IFEXPENSE;
    }

    public void setIFEXPENSE(Boolean IFEXPENSE) {
        this.IFEXPENSE = IFEXPENSE;
    }

    public Category(Parcel in){

        this.CATEGORY_ID = in.readInt();
        this.CATEGORY_NAME = in.readString();
        this.IFEXPENSE = (in.readByte() != 0);
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(CATEGORY_ID);
        dest.writeString(CATEGORY_NAME);
        dest.writeByte((byte) (IFEXPENSE ? 1 : 0));


    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {

        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

}
