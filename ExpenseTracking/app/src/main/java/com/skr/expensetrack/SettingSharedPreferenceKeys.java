package com.skr.expensetrack;

/**
 * Created by sreejithkr on 07/07/15.
 */


enum  DurationPeriod{
    Days(0),Months(1),Years(2);

    private final int value;
    private DurationPeriod(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

public class SettingSharedPreferenceKeys {

    public static final String CurrencySettingOnOROffKey = "CurrencySettingOnOROffKey";
    public static final String CategorySettingOnOROffKey = "CategorySettingOnOROffKey";
    public static final String DurationSettingOnOROffKey = "DurationSettingOnOROffKey";
    public static final String AmountSettingOnOROffKey = "AmountSettingOnOROffKey";
    public static final String ExpenseCategorySettingOnOROffKey = "ExpenseCategorySettingOnOROffKey";
    public static final String IncomeCategorySettingOnOROffKey = "IncomeCategorySettingOnOROffKey";
    public static final String FirstOptionDurationSettingOnOROffKey = "FirstOptionDurationSettingOnOROffKey";
    public static final String ValueFirstOptionDurationSettingOnOROffKey = "ValueFirstOptionDurationSettingOnOROffKey";
    public static final String PeriodValueFirstOptionDurationSettingOnOROffKey = "PeriodValueFirstOptionDurationSettingOnOROffKey";
    public static final String StartDateSelectedSecondOptionDurationSettingOnOROffKey = "StartDateSelectedSecondOptionDurationSettingOnOROffKey";
    public static final String ValueStartDateSelectedSecondOptionDurationSettingOnOROffKey = "ValueStartDateSelectedSecondOptionDurationSettingOnOROffKey";
    public static final String EndDateSelectedSecondOptionDurationSettingOnOROffKey = "EndDateSelectedSecondOptionDurationSettingOnOROffKey";
    public static final String ValueEndDateSelectedSecondOptionDurationSettingOnOROffKey = "ValueEndDateSelectedSecondOptionDurationSettingOnOROffKey";
    public static final String MinSelectedAmountSettingOnOROffKey = "MinSelectedAmountSettingOnOROffKey";
    public static final String MaxSelectedAmountSettingOnOROffKey = "MaxSelectedAmountSettingOnOROffKey";
    public static final String ValueMinSelectedAmountSettingOnOROffKey = "ValueMinSelectedAmountSettingOnOROffKey";
    public static final String ValueMaxSelectedAmountSettingOnOROffKey = "ValueMaxSelectedAmountSettingOnOROffKey";

}
