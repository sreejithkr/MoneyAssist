package com.skr.datahelper;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sreejithkr on 04/07/15.
 */
public class CheckBoxListData implements Parcelable {
    public CheckBoxListData(){
        name="no value";
        CATEGORY_ID = -1;
        checkState = false;
        ifCheckBoxRequired = false;
    }
    public CheckBoxListData( String name,Boolean checkState,Integer CATEGORY_ID){
        this.name = name;
        this.CATEGORY_ID = CATEGORY_ID;
        this.checkState = checkState;
        ifCheckBoxRequired = true;
        ifExpence = false;
    }
    public String name;
    public Boolean checkState;
    public Boolean ifCheckBoxRequired;
    public Integer CATEGORY_ID;
    public Boolean ifExpence;

    @Override
    public String toString() {
        return "name "+name+" checkState "+checkState+" ifCheckRequired "+ifCheckBoxRequired;
    }



    public CheckBoxListData(Parcel in){

        this.name = in.readString();
        this.CATEGORY_ID = in.readInt();
        this.checkState = (in.readByte() != 0);
        this.ifCheckBoxRequired = (in.readByte() != 0);
       this.ifExpence = (in.readByte() != 0);
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(CATEGORY_ID);
        dest.writeByte((byte) (checkState ? 1 : 0));
        dest.writeByte((byte) (ifCheckBoxRequired ? 1 : 0));
        dest.writeByte((byte) (ifExpence ? 1 : 0));

    }

    public static final Parcelable.Creator<CheckBoxListData> CREATOR = new Parcelable.Creator<CheckBoxListData>() {

        public CheckBoxListData createFromParcel(Parcel in) {
            return new CheckBoxListData(in);
        }

        public CheckBoxListData[] newArray(int size) {
            return new CheckBoxListData[size];
        }
    };

}