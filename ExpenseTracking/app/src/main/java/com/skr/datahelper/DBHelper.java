package com.skr.datahelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.util.Pair;

import com.skr.AppController;
import com.skr.expensetrack.HomeExpenceTrackFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by sreejithkr on 25/06/15.
 */
public class DBHelper  extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 2;
    // Database Name
    public static final String DATABASE_NAME = "expenseincomedb";
    public SQLiteDatabase db = null;



//    public static void setSharedInstance(DBHelper sharedInstance) {
//        DBHelper.sharedInstance = sharedInstance;
//    }

    private static DBHelper sharedInstance;
    private Context context;

    //Expence or Income details

    private static final String TABLE_NAME_EXPENCE_INCOME = "expenceincome";
    private static final String EXPENCE_INCOME_ID  = "id";
    private static final String CATEGORY_ID = "cateforyid";
    private static final String AMOUNT = "amount";
    private static final String DESCRIPTION = "description";
    private static final String IF_EXPENSE = "ifexpense";
    private static final String IS_AN_INVESTMENT = "isaninvestment";
    private static final String DAY_DATE = "daydate";
    private static final String MONTH_DATE = "monthdate";
    private static final String YEAR_DATE = "yeardate";


    //Expence or Income details
    private static final String TABLE_NAME_CATEGORY = "category";
    //private static final String CATEGORY_ID = "cateforyid";
    private static final String CATEGORY_NAME = "categoryname";
   //private static final String IFEXPENSE = "ifexpense";


    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
         this.context = context;
    }

    public static DBHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sharedInstance == null) {
            sharedInstance = new DBHelper(context.getApplicationContext());
        }
        return sharedInstance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //on create called more that one time i guess
        createAllTable(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Category
    public int deleteCategory(Category category){
        return db.delete(TABLE_NAME_CATEGORY,CATEGORY_ID+ " = "+category.getCATEGORY_ID(),null);
    }

    //Category

    public ArrayList<Category> getAllCategory() {
        ArrayList<Category> categories = new ArrayList<>();
        String query = "SELECT  * FROM " + TABLE_NAME_CATEGORY;
        // 2. get reference to writable DB
        // SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int i =  0;
        // 3. go over each row, build book and add it to list
        Category category = null;
        if (cursor.moveToFirst()) {
            do {
                Log.d("gCOUNT i", i + "");
                i++;


                category = new Category(Integer.parseInt(cursor.getString(0)),cursor.getString(1),(Integer.parseInt(cursor.getString(2))!=0));
                categories.add(category);
            } while (cursor.moveToNext());
        }
        //db.close();
        return categories;

    }

    public ArrayList<String> getExpenceOrIncomeCategoryNameList(Boolean expenceOrIncome) {
        ArrayList<String> categoryNames = new ArrayList<>();

        String query = "SELECT  * FROM " + TABLE_NAME_CATEGORY;
        // 2. get reference to writable DB
        // SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int i =  0;
        // 3. go over each row, build book and add it to list
        if (cursor.moveToFirst()) {
            do {
                Log.d("gCOUNT i", i + "");
                i++;



                if(expenceOrIncome){
                    if(Integer.parseInt(cursor.getString(2))!=0){
                        categoryNames.add(cursor.getString(1));
                    }
                }else{
                    if(Integer.parseInt(cursor.getString(2))==0){
                        categoryNames.add(cursor.getString(1));
                    }
                }


            } while (cursor.moveToNext());
        }
        //db.close();
        return categoryNames;
    }


    public Pair<ArrayList<Integer>,ArrayList<String>> getAllCategoryIDSNameListPair(Boolean expenceOrIncome) {


        ArrayList<String> categoryNames = new ArrayList<>();

        ArrayList<Integer> categoryIDS = new ArrayList<>();
        String query = "SELECT  * FROM " + TABLE_NAME_CATEGORY;
        // 2. get reference to writable DB
        // SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int i =  0;
        // 3. go over each row, build book and add it to list
        if (cursor.moveToFirst()) {
            do {
                Log.d("gCOUNT i", i + "");
                i++;


                categoryIDS.add(Integer.parseInt(cursor.getString(0)));

                if(expenceOrIncome){
                    if(Integer.parseInt(cursor.getString(2))!=0){
                        categoryNames.add(cursor.getString(1));
                    }
                }else{
                    if(Integer.parseInt(cursor.getString(2))==0){
                        categoryNames.add(cursor.getString(1));
                    }
                }


            } while (cursor.moveToNext());
        }
        //db.close();
        return new Pair<>(categoryIDS,categoryNames);

    }

    public HashMap<Integer,String> getAllCategoryHashMap() {
        HashMap<Integer,String> categories = new HashMap<>();
        String query = "SELECT  * FROM " + TABLE_NAME_CATEGORY ;
        // 2. get reference to writable DB
        // SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int i =  0;
        // 3. go over each row, build book and add it to list

        if (cursor.moveToFirst()) {
            do {




                    categories.put(Integer.parseInt(cursor.getString(0)),cursor.getString(1));


            } while (cursor.moveToNext());
        }
        //db.close();
        return categories;

    }
    public HashMap<Integer,String> getAllIncomeCategory() {
        HashMap<Integer,String> categories = new HashMap<>();
        String query = "SELECT  * FROM " + TABLE_NAME_CATEGORY + " WHERE "+IF_EXPENSE+" = 0";
        // 2. get reference to writable DB
        // SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int i =  0;
        // 3. go over each row, build book and add it to list

        if (cursor.moveToFirst()) {
            do {



                if(!(Integer.parseInt(cursor.getString(2))!=0)){
                    categories.put(Integer.parseInt(cursor.getString(0)),cursor.getString(1));
                }

            } while (cursor.moveToNext());
        }
        //db.close();
        return categories;

    }
    public HashMap<Integer,String> getAllExpenceCategory() {
        HashMap<Integer,String>categories = new HashMap<>();
        String query = "SELECT  * FROM " + TABLE_NAME_CATEGORY + " WHERE "+IF_EXPENSE+" = 1";
        // 2. get reference to writable DB
        // SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int i =  0;
        // 3. go over each row, build book and add it to list

        if (cursor.moveToFirst()) {
            do {
                Log.d("gCOUNT i", i + "");
                i++;


                if((Integer.parseInt(cursor.getString(2))!=0)){
                    categories.put(Integer.parseInt(cursor.getString(0)),cursor.getString(1));
                }
            } while (cursor.moveToNext());
        }
        //db.close();
        return categories;

    }
    public int updateCategory(Category category,Category newCategory){

        ArrayList<String> categoryNames = getExpenceOrIncomeCategoryNameList(newCategory.IFEXPENSE);
        Boolean categoryNameAlreadyExist = false;
        for (int count=0;count<categoryNames.size();count++){
            categoryNameAlreadyExist = categoryNames.get(count).equalsIgnoreCase(newCategory.CATEGORY_NAME);
        }
        if(categoryNameAlreadyExist){
            return -1;
        }

        ContentValues values = new ContentValues();

        values.put(CATEGORY_NAME,newCategory.getCATEGORY_NAME());
        values.put(IF_EXPENSE, (category.getIFEXPENSE()?1:0));


        return db.update(TABLE_NAME_CATEGORY,values,
                CATEGORY_ID+ " = "+category.getCATEGORY_ID(),
                null);
    }
    public Pair<Integer,Category> addNewCategory(Category category){
        Pair<ArrayList<Integer>,ArrayList<String>> pair =  getAllCategoryIDSNameListPair(category.IFEXPENSE);
        ArrayList<Integer> categoryIDS = pair.first;
        ArrayList<String> categoryNames = pair.second;
        Boolean categoryNameAlreadyExist = false;
        for (int count=0;count<categoryNames.size();count++){
            Log.e("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^",""+categoryNames.get(count).equalsIgnoreCase(category.CATEGORY_NAME));
            categoryNameAlreadyExist = categoryNames.get(count).equalsIgnoreCase(category.CATEGORY_NAME);
            if(categoryNameAlreadyExist){
                return new Pair<>(-1,null);

            }
        }

    Integer category_id = 0;

        Boolean continuWhileLoop = true;
        while(continuWhileLoop){
            if(categoryIDS.contains(category_id)){
                category_id = category_id + 1;
            }else {
                continuWhileLoop = false;
            }
        }

        String sql = "INSERT INTO "+ TABLE_NAME_CATEGORY +" VALUES (?,?,?);";
        SQLiteStatement statement = db.compileStatement(sql);
        db.beginTransaction();

            statement.clearBindings();
            statement.bindLong(1, category_id);
            statement.bindString(2, category.getCATEGORY_NAME());
            statement.bindLong(3, (category.getIFEXPENSE()?1:0));

            statement.execute();

        db.setTransactionSuccessful();
        db.endTransaction();
        AppController.getInstance().expenceOrIncomeOrCategoryAdded = true;
        AppController.getInstance().categoryAdded = true;
return new Pair<>(0,new Category(category_id, category.getCATEGORY_NAME(),category.getIFEXPENSE()));
    }

    public void addAllCategoryFromArrayList(ArrayList<Category> categories){


        String sql = "INSERT INTO "+ TABLE_NAME_CATEGORY +" VALUES (?,?,?);";
        SQLiteStatement statement = db.compileStatement(sql);
        db.beginTransaction();
        for (int i = 0; i<categories.size(); i++) {
            statement.clearBindings();
            statement.bindLong(1, categories.get(i).getCATEGORY_ID());
            statement.bindString(2, categories.get(i).getCATEGORY_NAME());
            statement.bindLong(3, (categories.get(i).getIFEXPENSE()?1:0));

            statement.execute();
        }
        db.setTransactionSuccessful();
        db.endTransaction();

    }

    //ExpenseIncome
    public int updateExpenceIncomeBasedToCategory(Category oldCategory,Integer newCategoryID){

        ContentValues values = new ContentValues();

        values.put(CATEGORY_ID, newCategoryID);


        return db.update(TABLE_NAME_EXPENCE_INCOME,values,
                CATEGORY_ID+ " = "+oldCategory.getCATEGORY_ID(),
                null);
    }
    public int deleteExpenceIncomeBasedOnCategory(Category category){
        int returnVal = db.delete(TABLE_NAME_EXPENCE_INCOME,CATEGORY_ID+ " = "+category.getCATEGORY_ID(),null);
        saveValuesForHome();
        return returnVal;
    }
    public long getAllExpenseIncomeWithCategory(Category category){
        String query = "SELECT  count(*) FROM " + TABLE_NAME_EXPENCE_INCOME +" WHERE "+ CATEGORY_ID +"="+category.getCATEGORY_ID();
        SQLiteStatement statement = db.compileStatement(query);
        long count = statement.simpleQueryForLong();
        return count;


    }

    public int deleteExpenseIncome(ExpenseIncome expenseIncome){
        int returnVal = db.delete(TABLE_NAME_EXPENCE_INCOME,EXPENCE_INCOME_ID+ " = "+expenseIncome.getEXPENCE_INCOME_ID(),null);
    saveValuesForHome();
        return returnVal;
    }
    public int updateExpenseIncome(ExpenseIncome oldExpenseIncome,ExpenseIncome newExpenseIncome) {
        ContentValues values = new ContentValues();

        Date date = newExpenseIncome.getDateFromDateString();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date) ;
        values.put(CATEGORY_ID, newExpenseIncome.getCATEGORY_ID());
        values.put(AMOUNT, newExpenseIncome.getAMOUNT());
        values.put(DESCRIPTION, newExpenseIncome.getDESCRIPTION());
        values.put(IF_EXPENSE, ((newExpenseIncome.getIF_EXPENSE()) ? 1 : 0));
        values.put(IS_AN_INVESTMENT,(newExpenseIncome.getIS_AN_INVESTMENT() ? 1 : 0));
        values.put(DAY_DATE,cal.get(Calendar.DATE));
        values.put(MONTH_DATE, cal.get(Calendar.MONTH)+1);


        int returnVal = db.update(TABLE_NAME_EXPENCE_INCOME,values,
                EXPENCE_INCOME_ID+ " = "+oldExpenseIncome.getEXPENCE_INCOME_ID(),
                null);
        saveValuesForHome();
        AppController.getInstance().expenceOrIncomeOrCategoryAdded = true;
        if(newExpenseIncome.getIF_EXPENSE()) {
            AppController.getInstance().expenceAdded = true;
        }else {
            AppController.getInstance().incomeAdded = true;
        }
        return returnVal;

    }
    public void addExpenseIncome(ExpenseIncome expenseIncome){
Log.d("addExpenseIncome ",expenseIncome.toString());
        ArrayList<ExpenseIncome>  expenseIncomes = getAllExpenseIncome();
        ArrayList<Integer>  expenceID = new ArrayList<>();
        for(int count = 0; count< expenseIncomes.size();count++){
            expenceID.add(expenseIncomes.get(count).getEXPENCE_INCOME_ID());
        }
        Boolean continuWhileLoop = true;
        Integer newMaxPriority = 0;
        while(continuWhileLoop){
            if(expenceID.contains(newMaxPriority)){
                newMaxPriority = newMaxPriority + 1;
            }else {
                continuWhileLoop = false;
            }
        }



        Date date = expenseIncome.getDateFromDateString();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date) ;

        ContentValues values = new ContentValues();
        values.put(EXPENCE_INCOME_ID,newMaxPriority);
        values.put(CATEGORY_ID, expenseIncome.getCATEGORY_ID());
        values.put(AMOUNT, expenseIncome.getAMOUNT());
        values.put(DESCRIPTION, expenseIncome.getDESCRIPTION());
        values.put(IF_EXPENSE, ((expenseIncome.getIF_EXPENSE()) ? 1 : 0));
        values.put(IS_AN_INVESTMENT,(expenseIncome.getIS_AN_INVESTMENT() ? 1 : 0));
        values.put(DAY_DATE,cal.get(Calendar.DATE));
        values.put(MONTH_DATE, cal.get(Calendar.MONTH)+1);

        values.put(YEAR_DATE,cal.get(Calendar.YEAR));
        // 3. insert
        db.insert(TABLE_NAME_EXPENCE_INCOME, // table
                null, //nullColumnHack
                values);
        saveValuesForHome();
        AppController.getInstance().expenceOrIncomeOrCategoryAdded = true;
        if(expenseIncome.getIF_EXPENSE()) {
            AppController.getInstance().expenceAdded = true;
        }else {
            AppController.getInstance().incomeAdded = true;
        }
        // key/value -> keys = column names/ values = column values
        // 4. close
        //db.close();

    }

    public ArrayList<ExpenseIncome> getAllExpenseIncome() {

        ArrayList<ExpenseIncome> expenseIncomeList = new ArrayList<>();
        String query = "SELECT  * FROM " + TABLE_NAME_EXPENCE_INCOME;
        // 2. get reference to writable DB
        // SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Date currentDate = new Date();
        //"MMMM d, yyyy"
        Date date = null;
        DateFormat format = new SimpleDateFormat(AppController.MonthSpaceDateSpaceYearFormat, Locale.ENGLISH);
        String formatedCurrentDate = format.format(currentDate);
        int i =  0;
        // 3. go over each row, build book and add it to list
        ExpenseIncome expenseIncome = null;
        if (cursor.moveToFirst()) {
            do {
                Log.d("gCOUNT i", i + "");
                i++;
/*
                 EXPENCE_INCOME_ID
                CATEGORY_ID
                 AMOUNT
                 DESCRIPTION
                IF_EXPENSE
           IS_AN_INVESTMENT
                DAY_DATE
                 MONTH_DATE
                YEAR_DATE */



                expenseIncome = new ExpenseIncome(Integer.parseInt(cursor.getString(0)),
                        Integer.parseInt(cursor.getString(1)),
                        Integer.parseInt(cursor.getString(2)),
                        cursor.getString(3),
                (Integer.parseInt(cursor.getString(4))!=0),
                (Integer.parseInt(cursor.getString(5))!=0),
                cursor.getString(6)+"-"+cursor.getString(7)+"-"+cursor.getString(8));
                expenseIncomeList.add(expenseIncome);
            } while (cursor.moveToNext());
        }
        //db.close();
        return expenseIncomeList;

    }

    public ArrayList<ExpenseIncome> getAllExpense() {

        ArrayList<ExpenseIncome> expenseIncomeList = new ArrayList<>();
        String query = "SELECT  * FROM " + TABLE_NAME_EXPENCE_INCOME + " WHERE "+IF_EXPENSE+" = 1";
        // 2. get reference to writable DB
        // SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Date currentDate = new Date();
        //"MMMM d, yyyy"
        Date date = null;
        DateFormat format = new SimpleDateFormat(AppController.MonthSpaceDateSpaceYearFormat, Locale.ENGLISH);
        String formatedCurrentDate = format.format(currentDate);
        int i =  0;
        // 3. go over each row, build book and add it to list
        ExpenseIncome expenseIncome = null;
        if (cursor.moveToFirst()) {
            do {
                Log.d("gCOUNT i", i + "");
                i++;
/*
                 EXPENCE_INCOME_ID
                CATEGORY_ID
                 AMOUNT
                 DESCRIPTION
                IF_EXPENSE
           IS_AN_INVESTMENT
                DAY_DATE
                 MONTH_DATE
                YEAR_DATE */



                expenseIncome = new ExpenseIncome(Integer.parseInt(cursor.getString(0)),
                        Integer.parseInt(cursor.getString(1)),
                        Integer.parseInt(cursor.getString(2)),
                        cursor.getString(3),
                        (Integer.parseInt(cursor.getString(4))!=0),
                        (Integer.parseInt(cursor.getString(5))!=0),
                        cursor.getString(6)+"-"+cursor.getString(7)+"-"+cursor.getString(8));
                expenseIncomeList.add(expenseIncome);
            } while (cursor.moveToNext());
        }
        //db.close();
        return expenseIncomeList;

    }
    public ArrayList<ExpenseIncome> getAllIncome() {

        ArrayList<ExpenseIncome> expenseIncomeList = new ArrayList<>();
        String query = "SELECT  * FROM " + TABLE_NAME_EXPENCE_INCOME + " WHERE "+IF_EXPENSE+" = 0";
        // 2. get reference to writable DB
        // SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Date currentDate = new Date();
        //"MMMM d, yyyy"
        Date date = null;
        DateFormat format = new SimpleDateFormat(AppController.MonthSpaceDateSpaceYearFormat, Locale.ENGLISH);
        String formatedCurrentDate = format.format(currentDate);
        int i =  0;
        // 3. go over each row, build book and add it to list
        ExpenseIncome expenseIncome = null;
        if (cursor.moveToFirst()) {
            do {
                Log.d("gCOUNT i", i + "");
                i++;
/*
                 EXPENCE_INCOME_ID
                CATEGORY_ID
                 AMOUNT
                 DESCRIPTION
                IF_EXPENSE
           IS_AN_INVESTMENT
                DAY_DATE
                 MONTH_DATE
                YEAR_DATE */



                expenseIncome = new ExpenseIncome(Integer.parseInt(cursor.getString(0)),
                        Integer.parseInt(cursor.getString(1)),
                        Integer.parseInt(cursor.getString(2)),
                        cursor.getString(3),
                        (Integer.parseInt(cursor.getString(4))!=0),
                        (Integer.parseInt(cursor.getString(5))!=0),
                        cursor.getString(6)+"-"+cursor.getString(7)+"-"+cursor.getString(8));
                expenseIncomeList.add(expenseIncome);
            } while (cursor.moveToNext());
        }
        //db.close();
        return expenseIncomeList;

    }

    // Get All group
    public ArrayList<ExpenseIncome> getexpenceIncomeForCategoriesArrayList(ArrayList<CheckBoxListData> checkBoxListDatas,String startDate,String endDate) {
        if(checkBoxListDatas == null || checkBoxListDatas.size()<=0){
            return new ArrayList<> ();

        }
        if(startDate == null || startDate.isEmpty()){
            startDate = null;
        }
        if(endDate == null || endDate.isEmpty()){
            endDate = null;
        }
        ArrayList<ExpenseIncome> expenseIncomeList = new ArrayList<>();
        // SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+ TABLE_NAME_EXPENCE_INCOME +" WHERE  ";
        String condition = ""+CATEGORY_ID+ "= ";
        String leftOverQuery = "";
        Log.d("mobileNumbers.size()",checkBoxListDatas.size()+"");
        int totalItems = checkBoxListDatas.size();
        for (int i = 0; i<totalItems; i++) {
            Log.d("mobileNumbers.get(i)",checkBoxListDatas.get(i).toString());
            if(i == (totalItems - 1)){
                leftOverQuery = leftOverQuery + condition + ""+checkBoxListDatas.get(i).CATEGORY_ID ;

            }else{
                leftOverQuery = leftOverQuery + condition + ""+checkBoxListDatas.get(i).CATEGORY_ID+ " OR ";
            }
        }
        query = query + leftOverQuery;

        Log.d("query string all mobile where clause",query);
        Cursor cursor = db.rawQuery(query, null);
        int i =  0;
        // 3. go over each row, build book and add it to list
        ExpenseIncome expenseIncome = null;
        if (cursor.moveToFirst()) {
            do {
                Log.d("TABLE_NAME_PERSON mobile_number_person",i+"" );
                i++;
                expenseIncome = new ExpenseIncome(Integer.parseInt(cursor.getString(0)),
                        Integer.parseInt(cursor.getString(1)),
                        Integer.parseInt(cursor.getString(2)),
                        cursor.getString(3),
                        (Integer.parseInt(cursor.getString(4))!=0),
                        (Integer.parseInt(cursor.getString(5))!=0),
                        cursor.getString(6)+"-"+cursor.getString(7)+"-"+cursor.getString(8));


                if(startDate != null && endDate != null) {
                    if (AppController.compareDatesFallsBetweenTwoGivenDate_dd_mm_yyyy(expenseIncome.dateString, startDate, endDate)) {
                        expenseIncomeList.add(expenseIncome);
                    }
                }else if(startDate != null   && endDate == null){
                    if(AppController.compareTwoDateString(expenseIncome.getDateString(),startDate)>=0){
                        expenseIncomeList.add(expenseIncome);
                    }
                }else if(startDate == null   && endDate != null){
                    if(AppController.compareTwoDateString(expenseIncome.getDateString(),startDate)<=0) {
                        expenseIncomeList.add(expenseIncome);
                    }
                }else {

                    expenseIncomeList.add(expenseIncome);

                }
                // Add book to books
            } while (cursor.moveToNext());
        }
        Log.e("expenseIncomeList count"+expenseIncomeList.size(),"expenseIncomeList expenseIncomeList");
        return expenseIncomeList;
    }

    public void dropAllTable(){
        // SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_EXPENCE_INCOME+"");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_CATEGORY+"");

        //db.close();

    }


    public TotalComponentsClass caculateTotalBasedOnInputs(ArrayList<CheckBoxListData> categoryLists,String startDate,String endDate){

//        ArrayList<ExpenseIncome> expenseList = new ArrayList<>();
//        ArrayList<ExpenseIncome> incomeList = new ArrayList<>();

        if(startDate == null || startDate.isEmpty()){
            startDate = null;
        }
        if(endDate == null || endDate.isEmpty()){
            endDate = null;
        }
Log.e(""+"startDate"+startDate+" endDate"+endDate,"");
        Integer expenceTotal = 0;
        Integer incomeTotal = 0;
        String query = "SELECT  * FROM " + TABLE_NAME_EXPENCE_INCOME;
        String condition = ""+CATEGORY_ID+ "= ";
        String leftOverQuery = "";
        Log.d("mobileNumbers.size()",categoryLists.size()+"");
        int maxCount = categoryLists.size();
        if(maxCount>0){

            query = query + " WHERE ";
        }
        for (int i = 0; i<maxCount; i++) {



            CheckBoxListData checkBoxListData = categoryLists.get(i);

            if(i == (maxCount - 1)){
                leftOverQuery = leftOverQuery + condition + ""+checkBoxListData.CATEGORY_ID ;

            }else{
                leftOverQuery = leftOverQuery + condition + ""+checkBoxListData.CATEGORY_ID+ " OR ";
            }
        }

            query = query + leftOverQuery;
            Log.d("mobileNumbers.get(i)",query);

            Cursor cursor = db.rawQuery(query, null);

            ExpenseIncome expenseIncome = null;
            if (cursor.moveToFirst()) {
                do {

                    expenseIncome = new ExpenseIncome(Integer.parseInt(cursor.getString(0)),
                            Integer.parseInt(cursor.getString(1)),
                            Integer.parseInt(cursor.getString(2)),
                            cursor.getString(3),
                            (Integer.parseInt(cursor.getString(4))!=0),
                            (Integer.parseInt(cursor.getString(5))!=0),
                            cursor.getString(6)+"-"+cursor.getString(7)+"-"+cursor.getString(8));
                    if(startDate != null && endDate != null) {
                        if (AppController.compareDatesFallsBetweenTwoGivenDate_dd_mm_yyyy(expenseIncome.dateString, startDate, endDate)) {
                            if(expenseIncome.IF_EXPENSE) {
                                expenceTotal = expenceTotal + expenseIncome.getAMOUNT();
                               // expenseList.add(expenseIncome);
                            }else{
                                incomeTotal = incomeTotal + expenseIncome.getAMOUNT();

                               // incomeList.add(expenseIncome);
                            }
                        }
                    }else if(startDate != null   && endDate == null){
                        if(AppController.compareTwoDateString(expenseIncome.getDateString(),startDate)>=0){
                            if(expenseIncome.IF_EXPENSE) {
                                expenceTotal = expenceTotal + expenseIncome.getAMOUNT();
                               // expenseList.add(expenseIncome);
                            }else{
                                incomeTotal = incomeTotal + expenseIncome.getAMOUNT();
                               // incomeList.add(expenseIncome);
                            }
                        }
                    }else if(startDate == null   && endDate != null){
                        if(AppController.compareTwoDateString(expenseIncome.getDateString(),startDate)<=0) {
                            if (expenseIncome.IF_EXPENSE) {
                                expenceTotal = expenceTotal + expenseIncome.getAMOUNT();
                              //  expenseList.add(expenseIncome);
                            } else {
                                incomeTotal = incomeTotal + expenseIncome.getAMOUNT();
                              //  incomeList.add(expenseIncome);
                            }
                        }
                    }else {

                            if (expenseIncome.IF_EXPENSE) {
                                expenceTotal = expenceTotal + expenseIncome.getAMOUNT();
                                //  expenseList.add(expenseIncome);
                            } else {
                                incomeTotal = incomeTotal + expenseIncome.getAMOUNT();
                                //  incomeList.add(expenseIncome);
                            }

                    }
                    // Add book to books
                } while (cursor.moveToNext());
            }



        return new TotalComponentsClass(expenceTotal,incomeTotal);
    }

    public void saveValuesForHome(){

        Date today = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(today) ;
        //  cal.get(Calendar.DATE);
        Integer month = cal.get(Calendar.MONTH)+1;
        Integer year = cal.get(Calendar.YEAR);
        SharedPreferences settings = context.getSharedPreferences(AppController.MY_APP_PREFERENCE, 0);
        SharedPreferences.Editor editor = settings.edit();
        for (int i=0; i<6; i++)
        {
            editor.remove( HomeExpenceTrackFragment.expenceTotalHome + i);
            editor.remove(HomeExpenceTrackFragment.incomeTotalHome + i);
        }

        for (int i=0; i<6; i++)
        {





            Integer expenceTotal = 0;
            Integer incomeTotal = 0;
            String query = "SELECT  * FROM " + TABLE_NAME_EXPENCE_INCOME +" WHERE "+MONTH_DATE+"="+month+" AND "+YEAR_DATE+" = "+year;
            Cursor cursor = db.rawQuery(query, null);
            ExpenseIncome expenseIncome = null;
            if (cursor.moveToFirst()) {
                do {
                    expenseIncome = new ExpenseIncome(Integer.parseInt(cursor.getString(0)),
                            Integer.parseInt(cursor.getString(1)),
                            Integer.parseInt(cursor.getString(2)),
                            cursor.getString(3),
                            (Integer.parseInt(cursor.getString(4))!=0),
                            (Integer.parseInt(cursor.getString(5))!=0),
                            cursor.getString(6)+"-"+cursor.getString(7)+"-"+cursor.getString(8));
                    if (expenseIncome.IF_EXPENSE) {
                        expenceTotal = expenceTotal + expenseIncome.getAMOUNT();
                        //  expenseList.add(expenseIncome);
                    } else {
                        incomeTotal = incomeTotal + expenseIncome.getAMOUNT();
                        //  incomeList.add(expenseIncome);
                    }
                } while (cursor.moveToNext());

                editor.putLong( HomeExpenceTrackFragment.expenceTotalHome + i, expenceTotal);
                editor.putLong(HomeExpenceTrackFragment.incomeTotalHome + i,incomeTotal);
            }

            editor.commit();

            month = month - 1;
            if(month == 0){
                month = 12;
                year = year-1;
            }
        }
        editor.putBoolean(HomeExpenceTrackFragment.saveValuesForHomeCalledOnceFlag,true);
        editor.commit();

    }

    public void createAllTable(SQLiteDatabase db ){

        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME_EXPENCE_INCOME+" ("+EXPENCE_INCOME_ID+" INTEGER PRIMARY KEY NOT NULL UNIQUE, "+CATEGORY_ID+" INTEGER NOT NULL, "+AMOUNT+" TEXT NOT NULL, "+DESCRIPTION+" TEXT NOT NULL, "+IF_EXPENSE+" INTEGER NOT NULL, "+IS_AN_INVESTMENT+" INTEGER NOT NULL, "+DAY_DATE+" INTEGER NOT NULL, "+MONTH_DATE+" INTEGER NOT NULL, "+YEAR_DATE+" INTEGER NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME_CATEGORY+" ("+CATEGORY_ID+" INTEGER PRIMARY KEY NOT NULL UNIQUE, "+CATEGORY_NAME+" TEXT NOT NULL, "+IF_EXPENSE+" INTEGER NOT NULL)");

    }

    public void postArchiveOpertaions(){
db.delete(TABLE_NAME_EXPENCE_INCOME,null,null);
        db.delete(TABLE_NAME_CATEGORY,null,null);

//        db.execSQL("DELETE "+TABLE_NAME_EXPENCE_INCOME);
//        db.execSQL("TRUNCATE TABLE "+TABLE_NAME_CATEGORY);
    }

}
