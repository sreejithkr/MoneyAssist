package com.skr.datahelper;

import com.skr.AppController;

/**
 * Created by sreejithkr on 01/08/15.
 */
public class TotalComponentsClass {

//    ArrayList<ExpenseIncome> expenseList;
//    ArrayList<ExpenseIncome> incomeList;

    Integer expenceTotal = 0;
    Integer incomeTotal = 0;

public TotalComponentsClass(Integer expenceTotal, Integer incomeTotal) {
    this.expenceTotal = expenceTotal;
    this.incomeTotal = incomeTotal;
}

    public Integer getExpenceTotal() {
        return expenceTotal;
    }

    public void setExpenceTotal(Integer expenceTotal) {
        this.expenceTotal = expenceTotal;
    }

    public Integer getIncomeTotal() {
        return incomeTotal;
    }

    public void setIncomeTotal(Integer incomeTotal) {
        this.incomeTotal = incomeTotal;
    }

    public String getExpenceTotalWithCurrency() {
        return expenceTotal + AppController.getInstance().getCurrencyString();
    }


    public String getIncomeTotalWithCurrency() {
        return incomeTotal+ AppController.getInstance().getCurrencyString();
    }







}
