package com.skr.datahelper;

import com.skr.AppController;

/**
 * Created by sreejithkr on 01/08/15.
 */
public class TotalComponentsClass {

//    ArrayList<ExpenseIncome> expenseList;
//    ArrayList<ExpenseIncome> incomeList;

    Long expenceTotal ;
    Long incomeTotal ;

public TotalComponentsClass(Long expenceTotal, Long incomeTotal) {
    this.expenceTotal = expenceTotal;
    this.incomeTotal = incomeTotal;
}

    public Long getExpenceTotal() {
        return expenceTotal;
    }

    public void setExpenceTotal(Long expenceTotal) {
        this.expenceTotal = expenceTotal;
    }

    public Long getIncomeTotal() {
        return incomeTotal;
    }

    public void setIncomeTotal(Long incomeTotal) {
        this.incomeTotal = incomeTotal;
    }

    public String getExpenceTotalWithCurrency() {
        return expenceTotal + AppController.getInstance().getCurrencyString();
    }


    public String getIncomeTotalWithCurrency() {
        return incomeTotal+ AppController.getInstance().getCurrencyString();
    }







}
