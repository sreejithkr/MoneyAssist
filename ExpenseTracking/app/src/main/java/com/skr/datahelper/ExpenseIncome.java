package com.skr.datahelper;

import android.os.Parcel;
import android.os.Parcelable;

import com.skr.AppController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sreejithkr on 25/06/15.
 */
public class ExpenseIncome implements Comparable<ExpenseIncome>, Parcelable {
    private static final String TABLE_NAME_EXPENCE_INCOME = "expenceincome";
    Integer EXPENCE_INCOME_ID  ;
    Integer CATEGORY_ID ;
    Integer AMOUNT ;
    String DESCRIPTION ;
    Boolean IF_EXPENSE ;
    Boolean IS_AN_INVESTMENT;
    String dateString;

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public ExpenseIncome(Integer EXPENCE_INCOME_ID, Integer CATEGORY_ID, Integer AMOUNT, String DESCRIPTION, Boolean IF_EXPENSE, Boolean IS_AN_INVESTMENT, String dateString) {
        this.EXPENCE_INCOME_ID = EXPENCE_INCOME_ID;
        this.CATEGORY_ID = CATEGORY_ID;
        this.AMOUNT = AMOUNT;
        this.DESCRIPTION = DESCRIPTION;
        this.IF_EXPENSE = IF_EXPENSE;
        this.IS_AN_INVESTMENT = IS_AN_INVESTMENT;
        this.dateString = dateString;
    }

    public Integer getEXPENCE_INCOME_ID() {
        return EXPENCE_INCOME_ID;
    }

    public void setEXPENCE_INCOME_ID(Integer EXPENCE_INCOME_ID) {
        this.EXPENCE_INCOME_ID = EXPENCE_INCOME_ID;
    }

    public Integer getCATEGORY_ID() {
        return CATEGORY_ID;
    }

    public void setCATEGORY_ID(Integer CATEGORY_ID) {
        this.CATEGORY_ID = CATEGORY_ID;
    }

    public Integer getAMOUNT() {
        return AMOUNT;
    }
    public String getAMOUNTWithCurrency() {
        return AMOUNT.toString() +AppController.getInstance().getCurrencyString();
    }

    public void setAMOUNT(Integer AMOUNT) {
        this.AMOUNT = AMOUNT;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public Boolean getIF_EXPENSE() {
        return IF_EXPENSE;
    }

    public void setIF_EXPENSE(Boolean IF_EXPENSE) {
        this.IF_EXPENSE = IF_EXPENSE;
    }

    public Boolean getIS_AN_INVESTMENT() {
        return IS_AN_INVESTMENT;
    }

    public void setIS_AN_INVESTMENT(Boolean IS_AN_INVESTMENT) {
        this.IS_AN_INVESTMENT = IS_AN_INVESTMENT;
    }

    public Date getDateFromDateString() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(AppController.DateDashMonthDashYearFormat);
            return  sdf.parse(dateString);

        }catch (Exception e){
            return null;
        }
    }

    @Override
    public String toString() {
        return "EXPENCE_INCOME_ID "+EXPENCE_INCOME_ID +" CATEGORY_ID " +CATEGORY_ID+" AMOUNT "+AMOUNT +" DESCRIPTION "+ DESCRIPTION + " IF_EXPENSE "+IF_EXPENSE+ " IS_AN_INVESTMENT " +IS_AN_INVESTMENT+" dateString "+dateString;
    }

    public static String getDateStringInMonthSpaceDateSpaceYearFormatFromString(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(AppController.MonthSpaceDateSpaceYearFormat);
            Date date =  sdf.parse(dateString);
            sdf = new SimpleDateFormat(AppController.DateDashMonthDashYearFormat, Locale.ENGLISH);
            return sdf.format(date);
        }catch (Exception e){
            return null;
        }
    }

    public  String getDateinMMMM_DD_YYYY() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(AppController.DateDashMonthDashYearFormat);
            Date date =  sdf.parse(this.dateString);
            sdf = new SimpleDateFormat(AppController.MonthSpaceDateSpaceYearFormat, Locale.ENGLISH);
            return sdf.format(date);
        }catch (Exception e){
            return null;
        }
    }
    public  String getDateinMMM_DD_YYYY() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(AppController.DateDashMonthDashYearFormat);
            Date date =  sdf.parse(this.dateString);
            sdf = new SimpleDateFormat(AppController.ThreeWordsMonthSpaceDateSpaceYearFormat, Locale.ENGLISH);
            return sdf.format(date);
        }catch (Exception e){
            return null;
        }
    }
    public String getMonthYearString() {



            Date date =  getDateFromDateString();
             SimpleDateFormat sdf = new SimpleDateFormat(AppController.MONTH_YEAR_Format, Locale.ENGLISH);
            return sdf.format(date);

    }
    public ExpenseIncome(Parcel in){
        this.EXPENCE_INCOME_ID = in.readInt();
        this.CATEGORY_ID = in.readInt();
        this.AMOUNT = in.readInt();
        this.DESCRIPTION = in.readString();
        this.IF_EXPENSE = (in.readByte() != 0);;
        this.IS_AN_INVESTMENT = (in.readByte() != 0);;
        this.dateString = in.readString();

    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(EXPENCE_INCOME_ID);
        dest.writeInt(CATEGORY_ID);
        dest.writeInt(AMOUNT);
        dest.writeString(DESCRIPTION);
        dest.writeByte((byte) (IF_EXPENSE ? 1 : 0));
        dest.writeByte((byte) (IS_AN_INVESTMENT ? 1 : 0));
        dest.writeString(dateString);


    }

    public static final Parcelable.Creator<ExpenseIncome> CREATOR = new Parcelable.Creator<ExpenseIncome>() {

        public ExpenseIncome createFromParcel(Parcel in) {
            return new ExpenseIncome(in);
        }

        public ExpenseIncome[] newArray(int size) {
            return new ExpenseIncome[size];
        }
    };

    @Override
    public int compareTo(ExpenseIncome another) {
        return AppController.compareTwoDateString(another.getDateString(),this.getDateString());
    }

}
