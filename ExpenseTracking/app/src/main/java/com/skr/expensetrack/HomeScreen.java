package com.skr.expensetrack;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.crittercism.app.Crittercism;
import com.skr.AppController;
import com.skr.customviews.AmazingAdapter;
import com.skr.customviews.AmazingListView;
import com.skr.customviews.CustomAlert;
import com.skr.datahelper.Category;
import com.skr.datahelper.CheckBoxListData;
import com.skr.datahelper.DBHelper;
import com.skr.datahelper.ExpenseIncome;
import com.skr.datahelper.TotalComponentsClass;

import java.util.ArrayList;
import java.util.HashMap;

enum CurrentPage{
 HomePage,
    AddExpenceIncome,
    Expense,Income,
    Category,
    Settings,
    GetTotal,
    Utilities,
    ContactUs,
    Currency


 }

public class HomeScreen extends ActionBarActivity {
    public Boolean onDrawerSlideFlag = false;
    private DrawerLayout mDrawerLayout;
    private AmazingListView mDrawerList;
    private String[] mTitles;
    private ActionBarDrawerToggle mDrawerToggle;
    CurrentPage state = CurrentPage.HomePage;
    private String[] defaultCategoryArray;
    ArrayList<String> subItemsArray;
    Fragment fragment;
    android.support.v4.app.Fragment homefragment;
Boolean ifExpenceOrIncomeListEmpty = false;

    private void onStarupSetup(){

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(AppController.MY_APP_PREFERENCE, 0);
        boolean isCategoryPresentInDB = settings.getBoolean(AppController.categoryprepopulated, false);
        AppController.categoryReloadToDB(settings,isCategoryPresentInDB,this);

        
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        Crittercism.initialize(getApplicationContext(), "559aa1145c69e80d008f93f7");
       
        if(getSupportActionBar() != null){getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_green)));}
        mTitles = getResources().getStringArray(R.array.side_panel_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.homeDrawerLayout);
        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener(){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                onDrawerSlideFlag = true;
                hideKeyboard(HomeScreen.this);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                onDrawerSlideFlag = false;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                onDrawerSlideFlag = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }

        });

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerList = (AmazingListView) findViewById(R.id.left_drawer);



        subItemsArray = new ArrayList<>();
for(int count=0;count< mTitles.length;count++){
    subItemsArray.add(mTitles[count]);

}
        ArrayList<Pair<String, ArrayList<String>>> all = new ArrayList();
        all.add(new Pair<String, ArrayList<String>>("Track",subItemsArray));
        
        mDrawerList.setAdapter(new SidePanelAdapter(all));
        mDrawerList.setOnItemClickListener(new SidePanelItemClickListener());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);

        onStarupSetup();
        navigateToHome();

     //   selectItem(0,homefragment);

    }

    private void navigateToHome(){
        // update selected item and title, then close the drawer

        if(homefragment != null) {
            getSupportFragmentManager().beginTransaction().remove(homefragment).commit();
        }
        if(fragment != null) {
            getFragmentManager().beginTransaction().remove(fragment).commit();
        }
        homefragment = new HomeExpenceTrackFragment();
        ((HomeExpenceTrackFragment)homefragment).getTotalFlag = false;
        ((HomeExpenceTrackFragment)homefragment).setmListener(new HomeExpenceTrackFragment.OnFragmentInteractionListener() {
            @Override
            public void onFragmentInteraction(Uri uri) {

            }
            @Override
            public void onCreatedFragment(){
                state = CurrentPage.HomePage;
                invalidateOptionsMenu();
            }
            @Override
            public void onCalculateTotalButtonClick(){
                onCalculateTotalButtonClickHome();
            }
            @Override
            public void onMoreInfoButtonClick(HashMap<String,String> message){
                onMoreInfoButtonClickHome(message);


            }

            @Override
            public void onAddExpenceOrIncome() {


                //TODO: check implementation
                addExpenceIncomeActivityShow(null);
            }

        });

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, homefragment).commit();
        mDrawerList.setItemChecked(0, true);
        setTitle(mTitles[0]);
        mDrawerLayout.closeDrawer(mDrawerList);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        if(personDataNoAvailable) {
//            menu.findItem(R.id.action_delete_person_eventED).setVisible(false);
//        }else{
//            menu.findItem(R.id.action_delete_person_eventED).setVisible(true);
//        }
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_add_category).setVisible(false);
        menu.findItem(R.id.action_add_an_expense_or_income).setVisible(false);
        menu.findItem(R.id.action_delete_an_expense).setVisible(false);
        menu.findItem(R.id.action_delete_an_income).setVisible(false);
        menu.findItem(R.id.action_delete_category).setVisible(false);
        menu.findItem(R.id.action_sort_by_day).setVisible(false);
        menu.findItem(R.id.action_sort_by_category).setVisible(false);
        menu.findItem(R.id.action_sort_by_other_factors).setVisible(false);
        menu.findItem(R.id.action_reset_to_original).setVisible(false);
        menu.findItem(R.id.action_sort_by_months).setVisible(false);
        menu.findItem(R.id.dataRefresh).setVisible(false);
        menu.findItem(R.id.quickAdd).setVisible(true);
        menu.findItem(R.id.action_search_currency).setVisible(false);

        switch(state){
            case HomePage:
                break;
            case AddExpenceIncome:
                menu.findItem(R.id.quickAdd).setVisible(false);
                break;
            case Category:
                menu.findItem(R.id.quickAdd).setVisible(false);
                menu.findItem(R.id.action_add_category).setVisible(true);
                menu.findItem(R.id.action_delete_category).setVisible(true);
                break;
            case Income:
                menu.findItem(R.id.dataRefresh).setVisible(!((ListExpenceIncomeFragment)fragment).hideRefreshIcon);
                menu.findItem(R.id.action_add_an_expense_or_income).setVisible(true);
                menu.findItem(R.id.action_sort_by_months).setVisible(!ifExpenceOrIncomeListEmpty);
                menu.findItem(R.id.action_delete_an_income).setVisible(!ifExpenceOrIncomeListEmpty);
                menu.findItem(R.id.action_sort_by_day).setVisible(!ifExpenceOrIncomeListEmpty);
                menu.findItem(R.id.action_sort_by_category).setVisible(!ifExpenceOrIncomeListEmpty);
                menu.findItem(R.id.action_sort_by_other_factors).setVisible(!ifExpenceOrIncomeListEmpty);
                menu.findItem(R.id.action_reset_to_original).setVisible(false);

                break;
            case Expense:
                menu.findItem(R.id.dataRefresh).setVisible(!((ListExpenceIncomeFragment)fragment).hideRefreshIcon);
                menu.findItem(R.id.action_add_an_expense_or_income).setVisible(true);
                menu.findItem(R.id.action_sort_by_months).setVisible(!ifExpenceOrIncomeListEmpty);
                menu.findItem(R.id.action_delete_an_expense).setVisible(!ifExpenceOrIncomeListEmpty);
                menu.findItem(R.id.action_sort_by_day).setVisible(!ifExpenceOrIncomeListEmpty);
                menu.findItem(R.id.action_sort_by_category).setVisible(!ifExpenceOrIncomeListEmpty);
                menu.findItem(R.id.action_sort_by_other_factors).setVisible(!ifExpenceOrIncomeListEmpty);
                menu.findItem(R.id.action_reset_to_original).setVisible(false);

                break;
            case Settings:

                break;
            case GetTotal:
                break;
            case ContactUs:
                break;
            case Currency:
                menu.findItem(R.id.quickAdd).setVisible(false);
                menu.findItem(R.id.action_search_currency).setVisible(true);
                final MenuItem searchItem = menu.findItem(R.id.action_search_currency);
                final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

                MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        searchView.setQuery("",false);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        ((CurrencyListFragment)fragment).filter("");
                        return true;
                    }
                });

                ((CurrencyListFragment)fragment).searchItem = searchItem;
                // Configure the search info and add any event listeners
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        ((CurrencyListFragment)fragment).filter(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {

                        ((CurrencyListFragment)fragment).filter(newText);
                        return true;
                    }
                });

                break;


        }

        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public void onBackPressed() {
        if(state == CurrentPage.HomePage){
            super.onBackPressed();
        }else{
            navigateToHome();
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id ==  android.R.id.home){
            if(mDrawerLayout.isDrawerOpen(GravityCompat.START) ){
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }else{
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }else if (id ==  R.id.action_add_category){
            ((CategoryListFragment)fragment).cancelDelete();
            item.setTitle(getString(R.string.action_delete_category));
            Intent addCategoryActivity = new Intent(this, AddCategoryActivity.class);
            startActivityForResult(addCategoryActivity,ActivityResultIdentifies.CategoryAddedConfirmationKey);

        }else if(id == R.id.action_delete_category){
            ((CategoryListFragment)fragment).deleteEnabled();
            if(item.getTitle().toString().equalsIgnoreCase(getString(R.string.cancel_delete))){
                item.setTitle(getString(R.string.action_delete_category));
            }else{
                item.setTitle(getString(R.string.cancel_delete));

            }

        }else if(id == R.id.action_add_an_expense_or_income || id == R.id.quickAdd){
addExpenceIncomeActivityShow(null);




        }else if(id == R.id.action_delete_an_expense){
            Boolean deleteStatus = ((ListExpenceIncomeFragment)fragment).toggleDelete();
            if(deleteStatus){
                item.setTitle(getString(R.string.cancel_delete));
            }else{
                item.setTitle(getString(R.string.action_delete_an_expense));
            }

        }else if(id == R.id.action_delete_an_income) {
            Boolean deleteStatus = ((ListExpenceIncomeFragment)fragment).toggleDelete();
            if(deleteStatus){
                item.setTitle(getString(R.string.cancel_delete));
            }else{
                item.setTitle(getString(R.string.action_delete_an_income));
            }
        }
        else if(id == R.id.action_reset_to_original){
            ((ListExpenceIncomeFragment)fragment).resetToOriginal();
        }else if(id == R.id.action_sort_by_day){
            ((ListExpenceIncomeFragment)fragment).groupByDate();

        }else if(id == R.id.action_sort_by_category){
            ((ListExpenceIncomeFragment)fragment).groupByCategory();

        }else if(id == R.id.action_sort_by_other_factors){

            Intent filterAndViewExpenseIncomeIntent = new Intent(this,FilterAndViewExpenseIncomeActivity.class);
            filterAndViewExpenseIncomeIntent.putExtra(FilterAndViewExpenseIncomeActivity.ifExpenseToBeShownKey,(state==CurrentPage.Expense));
            startActivityForResult(filterAndViewExpenseIncomeIntent,ActivityResultIdentifies.FilterAndViewExpenseIncomeActivityResult);


        }else if(id == R.id.action_sort_by_months) {
            ((ListExpenceIncomeFragment)fragment).groupByMonths();

        }else if(id == R.id.dataRefresh) {
            ((ListExpenceIncomeFragment)fragment).resetToOriginal();
        }

        return super.onOptionsItemSelected(item);
    }
    public void onCalculateTotalButtonClickHome(){
        if( state == CurrentPage.GetTotal) {
            Intent intent = new Intent(this, CategorySettingActivity.class);
            intent.putExtra(CategorySettingActivity.CategorySettingActivityGetTotalFlag, true);

            startActivityForResult(intent, ActivityResultIdentifies.GetTotal);
        }
    }
    public  void onMoreInfoButtonClickHome(HashMap<String,String> textMessage){
      //  if( state == CurrentPage.GetTotal) {
            Intent intent = new Intent(this, ShowTotalDetailsActivity.class);
        String expenceMessage = textMessage.get(HomeExpenceTrackFragment.expenseMessageKey);
        if(expenceMessage.isEmpty()){
            expenceMessage = getResources().getString(R.string.allCategory_default_msg_show_total_details);
        }
        String incomeMessage = textMessage.get(HomeExpenceTrackFragment.incomeMessageKey);
        if(incomeMessage.isEmpty()){
            incomeMessage = getResources().getString(R.string.allCategory_default_msg_show_total_details);
        }
            intent.putExtra(HomeExpenceTrackFragment.expenseMessageKey, expenceMessage);
            intent.putExtra(HomeExpenceTrackFragment.incomeMessageKey, incomeMessage);
            intent.putExtra(HomeExpenceTrackFragment.timePeriodKey, textMessage.get(HomeExpenceTrackFragment.timePeriodKey));

            startActivityForResult(intent, 0);
      //  }
    }

    public void addExpenceIncomeActivityShow(Intent data){

        Intent addExpenceIncomeActivity = new Intent(this, AddExpenceIncomeActivity.class);
        Boolean editStatusFlag = false;
        if(data != null) {
            editStatusFlag = data.getBooleanExtra(ExpenceIncomeDetailActivity.editStatusFlag, false);
        }
        addExpenceIncomeActivity.putExtra(ExpenceIncomeDetailActivity.editStatusFlag,editStatusFlag);

        if(editStatusFlag && data != null) {
            final ExpenseIncome ei = data.getParcelableExtra(ListExpenceIncomeFragment.expenseIncome);
            addExpenceIncomeActivity.putExtra(ListExpenceIncomeFragment.expenseIncome,ei);
        }

        startActivityForResult(addExpenceIncomeActivity,ActivityResultIdentifies.AddExpenceIncomeActivityKey);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("(((((((((((((((())))))))))))))))))","ssfdsdfsdfsd"+requestCode);
        AppController.getInstance().listExpenseIncomeItemClicked = false;

        if (requestCode == ActivityResultIdentifies.ExpenceIncomeDetailFromList) {

            if (resultCode == RESULT_OK) {

                addExpenceIncomeActivityShow(data);
            }

        }else if(requestCode == ActivityResultIdentifies.FilterAndViewExpenseIncomeActivityResult){
            if (resultCode == RESULT_OK) {
                ((ListExpenceIncomeFragment)fragment).filterWithParameters(data);
            }
        }else if(requestCode == ActivityResultIdentifies.CategoryDeleteConfirmationKey){
            Log.e("(resultCode == RESULT_OK) ",""+(resultCode == RESULT_OK) );
            if (resultCode == RESULT_OK) {

                if(data.getBooleanExtra(CategoryDeleteConfirmationActivity.AddCategoryFlag,false)){

                    //this case handle when there only one catergory andr user tries to delete the last category
                    Intent addCategoryActivity = new Intent(this, AddCategoryActivity.class);
                    startActivityForResult(addCategoryActivity,ActivityResultIdentifies.CategoryAddedConfirmationKey);
                }else {
                    ((CategoryListFragment) fragment).removeCategoryFromListAfterSuccessfulDelete();
                    String confirmDilogMessage = getResources().getString(R.string.delete_dialog_des_sucess_message);
                    new CustomAlert.CustomBuilder(this,getLayoutInflater())
                            .setTitle(R.string.info)
                            .setMessage(confirmDilogMessage).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {


                            // do nothing
                        }
                    }).setIcon(R.drawable.delete_alert)
                            .show();
                }

            }
        }else if(requestCode == ActivityResultIdentifies.CategoryUpdateConfirmationKey){
            Log.e("(resultCode == RESULT_OK) ",""+(resultCode == RESULT_OK) );
            if (resultCode == RESULT_OK) {

                Category category = data.getParcelableExtra(AddCategoryActivity.UpdatedCategory);
                if(category != null ) {
                    ((CategoryListFragment) fragment).reloadCategoryFromListAfterSuccessfulUpdate(category);
                }
                String confirmDilogMessage = getResources().getString(R.string.success_msg_add_category);

                new CustomAlert.CustomBuilder(this,getLayoutInflater())
                        .setTitle(R.string.info)
                        .setMessage(confirmDilogMessage).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        // do nothing
                    }
                }).setIcon(R.drawable.success_icon)
                        .show();

            }
        }
        else if(requestCode == ActivityResultIdentifies.CategoryAddedConfirmationKey){
            Log.e("(resultCode == RESULT_OK) ",""+(resultCode == RESULT_OK) );
            if (resultCode == RESULT_OK) {

                Category category = data.getParcelableExtra(AddCategoryActivity.UpdatedCategory);
                if(category != null ) {
                    ((CategoryListFragment) fragment).reloadAddingCategorytoList(category);
                }
                String confirmDilogMessage = getResources().getString(R.string.success_msg_update_category);;

                new CustomAlert.CustomBuilder(this,getLayoutInflater())
                        .setTitle(R.string.info)
                        .setMessage(confirmDilogMessage).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        // do nothing
                    }
                }).setIcon(R.drawable.success_icon)
                        .show();

            }
        }

        else if(requestCode == ActivityResultIdentifies.GetTotal){
            Log.e("(resultCode == RESULT_OK) ",""+(resultCode == RESULT_OK) );

            if (resultCode == RESULT_OK) {


                ArrayList<CheckBoxListData> checkBoxListDataArrayList = data.getParcelableArrayListExtra(CategorySettingActivity.CategorySettingActivityCategoryList);
                String categorySettingStartDate = data.getStringExtra(CategorySettingActivity.CategorySettingStartDate);
                String categorySettingEndDate  = data.getStringExtra(CategorySettingActivity.CategorySettingEndDate);
                DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                TotalComponentsClass totalComponentsClass = dbHelper.caculateTotalBasedOnInputs(checkBoxListDataArrayList,categorySettingStartDate,categorySettingEndDate);
                ((HomeExpenceTrackFragment)homefragment).getTotalFlag = true;
                Pair<Boolean,Boolean> noIncomeExpenceSelectedPair =  new Pair<>(data.getBooleanExtra(CategorySettingActivity.CategorySettingActivityNoExpenseSelected,false), data.getBooleanExtra(CategorySettingActivity.CategorySettingActivityNoIncomeSelected,false));
                Pair<String,String> startEndDatePair =  new Pair<>(categorySettingStartDate,categorySettingEndDate);
                Pair<Boolean,Boolean> incomeExpesePair = new Pair<>(data.getBooleanExtra(CategorySettingActivity.ExpenseSelectAllFlag,false),data.getBooleanExtra(CategorySettingActivity.IncomeSelectAllFlag,false));
                ((HomeExpenceTrackFragment)homefragment).reloadValuesForTotal(totalComponentsClass.getExpenceTotal(),totalComponentsClass.getIncomeTotal(),checkBoxListDataArrayList,incomeExpesePair,startEndDatePair,noIncomeExpenceSelectedPair);


            }else{
                Pair<Boolean,Boolean> noIncomeExpenceSelectedPair =  new Pair<>(false,false);

                ArrayList<CheckBoxListData> checkBoxListDataArrayList = new ArrayList<>();
                String categorySettingStartDate = "";
                String categorySettingEndDate  = "";
                DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                TotalComponentsClass totalComponentsClass = dbHelper.caculateTotalBasedOnInputs(checkBoxListDataArrayList,categorySettingStartDate,categorySettingEndDate);
                ((HomeExpenceTrackFragment)homefragment).getTotalFlag = true;
                Pair<String,String> startEndDatePair =  new Pair<>(categorySettingStartDate,categorySettingEndDate);
                Pair<Boolean,Boolean> incomeExpesePair = new Pair<>(false,false);
                ((HomeExpenceTrackFragment)homefragment).reloadValuesForTotal(totalComponentsClass.getExpenceTotal(),totalComponentsClass.getIncomeTotal(),checkBoxListDataArrayList,incomeExpesePair,startEndDatePair,noIncomeExpenceSelectedPair);

            }

        }else if(requestCode == ActivityResultIdentifies.CurrencyActivityDismissed){
            Log.e("(resultCode == RESULT_OK) ", "" + (resultCode == RESULT_OK));
            if (resultCode == RESULT_OK) {
                new Handler().post(new Runnable() {
                    public void run() {

                        ((CurrencyListFragment)fragment).reloadData();

                    }
                });


            }
        }else if(requestCode == ActivityResultIdentifies.LoadFromArchiveListActivityPage){

        }else if(requestCode == ActivityResultIdentifies.ExportToExcelActivityPage){

        }else if(requestCode == ActivityResultIdentifies.AddExpenceIncomeActivityKey){
            if (resultCode == RESULT_OK) {

               if(data.getBooleanExtra(AddExpenceIncomeActivity.ReturnToListFlagKey,false)){
                  final  Boolean ifExpense = data.getBooleanExtra(AddExpenceIncomeActivity.ExpenceOrIncomeFlagKey,false);


                   new Handler().post(new Runnable() {
                       @Override
                       public void run() {
                           if(fragment != null) {
                               getFragmentManager().beginTransaction().remove(fragment).commit();
                           }
                           if(homefragment != null) {
                               getSupportFragmentManager().beginTransaction().remove(homefragment).commit();
                           }
                           fragment = null;
                           homefragment = null;
                           fragment = getExpenceIncomeListFragment(ifExpense);
                           int position = 0;
                           if(ifExpense){
                               position = subItemsArray.indexOf(getResources().getString(R.string.side_panel_items_array_expense));
                           }else{
                               position = subItemsArray.indexOf(getResources().getString(R.string.side_panel_items_array_income));
                           }
                           selectItem(position, fragment, R.id.content_frame);

                       }
                   });

               }else{
                   new Handler().post(new Runnable() {
                       @Override
                       public void run() {
                   reloadFragmentsOnRetrunignFromAddExpenceIncome();
                       }
                   });
               }
            }else {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        reloadFragmentsOnRetrunignFromAddExpenceIncome();
                    }
                });
            }
        }

    }


    public ListExpenceIncomeFragment getExpenceIncomeListFragment(Boolean isExpenseFlag) {

        ListExpenceIncomeFragment listExpenceIncomeFragment = new ListExpenceIncomeFragment();

        if (isExpenseFlag) {

                ((ListExpenceIncomeFragment)listExpenceIncomeFragment).setmListener(new ListExpenceIncomeFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onFragmentInteraction(Uri uri) {

                    }
                    public void onCreatedFragment(){
                        state = CurrentPage.Expense;
                        invalidateOptionsMenu();
                    }
                    @Override
                    public void onItemClick(AdapterView<?> parent, int position,HashMap<Integer,String> categoryIdName) {
                        ListExpenceIncomeFragment.ExpenceIncomeListAdapter expenceIncomeListAdapter = (ListExpenceIncomeFragment.ExpenceIncomeListAdapter)parent.getAdapter();
                        ExpenseIncome expenseIncome = expenceIncomeListAdapter.getItem(position);
                        Intent detailPage = new Intent(getApplication(),ExpenceIncomeDetailActivity.class);
                        detailPage.putExtra(ListExpenceIncomeFragment.categoryName, categoryIdName.get(expenseIncome.getCATEGORY_ID()));
                        detailPage.putExtra(ListExpenceIncomeFragment.expenseIncome,expenseIncome);
                        startActivityForResult(detailPage, ActivityResultIdentifies.ExpenceIncomeDetailFromList);
                    }
                    @Override
                    public void onItemClickDelete(AdapterView<?> parent, int position){

                    }

                    @Override
                    public void invalidateMenu(Boolean flag){
                        ifExpenceOrIncomeListEmpty = flag;
                        invalidateOptionsMenu();

                    }
                    @Override
                    public void addExpenceIncome(){

                        //TODO: CHECK IMPLEMENTATION
                        addExpenceIncomeActivityShow(null);
//                       if(fragment != null) {
//                            getFragmentManager().beginTransaction().remove(fragment).commit();
//                        }
//                        if(homefragment != null) {
//                            getSupportFragmentManager().beginTransaction().remove(homefragment).commit();
//                        }
//                        int position = 0;
//                        for(int count = 0;count<mTitles.length;count++){
//                            if(mTitles[count].equalsIgnoreCase(getString(R.string.side_panel_items_array_income_expense))){
//                                position = count;
//                            }
//                        }
//                        setupAddExpenceIncomeFragement();
//                        selectItem(position,fragment,R.id.content_frame);
                    }
                });
        }else{
            ((ListExpenceIncomeFragment) listExpenceIncomeFragment).isExpenseFlag = isExpenseFlag;
        ((ListExpenceIncomeFragment) listExpenceIncomeFragment).setmListener(new ListExpenceIncomeFragment.OnFragmentInteractionListener() {
            @Override
            public void onFragmentInteraction(Uri uri) {

            }

            @Override
            public void onCreatedFragment() {
                state = CurrentPage.Income;
                invalidateOptionsMenu();
            }

            @Override
            public void onItemClick(AdapterView<?> parent, int position, HashMap<Integer, String> categoryIdName) {
                ListExpenceIncomeFragment.ExpenceIncomeListAdapter expenceIncomeListAdapter = (ListExpenceIncomeFragment.ExpenceIncomeListAdapter) parent.getAdapter();
                ExpenseIncome expenseIncome = expenceIncomeListAdapter.getItem(position);
                Intent detailPage = new Intent(getApplication(), ExpenceIncomeDetailActivity.class);
                detailPage.putExtra(ListExpenceIncomeFragment.categoryName, categoryIdName.get(expenseIncome.getCATEGORY_ID()));
                detailPage.putExtra(ListExpenceIncomeFragment.expenseIncome, expenseIncome);
                startActivityForResult(detailPage, ActivityResultIdentifies.ExpenceIncomeDetailFromList);
            }

            @Override
            public void onItemClickDelete(AdapterView<?> parent, int position) {

            }

            @Override
            public void invalidateMenu(Boolean flag){

                ifExpenceOrIncomeListEmpty = flag;
                invalidateOptionsMenu();
            }
            @Override
            public void addExpenceIncome(){
                //TODO: CHECK IMPLEMENTATION
                addExpenceIncomeActivityShow(null);
//                if(fragment != null) {
//                    getFragmentManager().beginTransaction().remove(fragment).commit();
//                }
//                if(homefragment != null) {
//                    getSupportFragmentManager().beginTransaction().remove(homefragment).commit();
//                }
//                int position = 0;
//                for(int count = 0;count<mTitles.length;count++){
//                    if(mTitles[count].equalsIgnoreCase(getString(R.string.side_panel_items_array_income_expense))){
//                        position = count;
//                    }
//                }
//                setupAddExpenceIncomeFragement();
//                selectItem(position,fragment,R.id.content_frame);
            }
        });


    }
        return  listExpenceIncomeFragment;
    }
    /* The click listner for ListView in the navigation drawer */
    private class SidePanelItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            SidePanelAdapter sidePanelAdapter = (SidePanelAdapter)parent.getAdapter();

            hideKeyboard(HomeScreen.this);
            if(fragment != null) {
                getFragmentManager().beginTransaction().remove(fragment).commit();
            }
            if(homefragment != null) {
                getSupportFragmentManager().beginTransaction().remove(homefragment).commit();
            }
            fragment = null;
            homefragment = null;
            Log.d(sidePanelAdapter.getItem(position),"test");
            if(sidePanelAdapter.getItem(position).equalsIgnoreCase(getResources().getString(R.string.side_panel_items_array_home))){
               navigateToHome();
                return;

            }else if(sidePanelAdapter.getItem(position).equalsIgnoreCase(getResources().getString(R.string.side_panel_items_array_income_expense))){
                //setupAddExpenceIncomeFragement();

            }else if(sidePanelAdapter.getItem(position).equalsIgnoreCase(getResources().getString(R.string.side_panel_items_array_category))){
                fragment = getCategoryListFragement();
            }else if(sidePanelAdapter.getItem(position).equalsIgnoreCase(getResources().getString(R.string.side_panel_items_array_expense))){

                fragment = getExpenceIncomeListFragment(true);


            }else if(sidePanelAdapter.getItem(position).equalsIgnoreCase(getResources().getString(R.string.side_panel_items_array_income))){

                fragment = getExpenceIncomeListFragment(false);

            }else if(sidePanelAdapter.getItem(position).equalsIgnoreCase(getResources().getString(R.string.side_panel_items_array_setting))){
                fragment = new SettingListFragment();
                ((SettingListFragment)fragment).setmListener(new SettingListFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onFragmentInteraction(Uri uri) {

                    }
                    @Override
                    public void onCreatedFragment(){
                        state = CurrentPage.Settings;
                        invalidateOptionsMenu();
                    }


                });
            }else if(sidePanelAdapter.getItem(position).equalsIgnoreCase(getResources().getString(R.string.side_panel_items_array_get_total))){


                homefragment = new HomeExpenceTrackFragment();
                ((HomeExpenceTrackFragment)homefragment).getTotalFlag = true;
                ((HomeExpenceTrackFragment)homefragment).setmListener(new HomeExpenceTrackFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onFragmentInteraction(Uri uri) {

                    }
                    @Override
                    public void onCreatedFragment(){
                        state = CurrentPage.GetTotal;
                        invalidateOptionsMenu();
                    }
                    @Override
                    public void onCalculateTotalButtonClick(){
                        onCalculateTotalButtonClickHome();
                    }
                    @Override
                    public void onMoreInfoButtonClick(HashMap<String,String> message){
                        onMoreInfoButtonClickHome(message);


                    }
                    @Override
                    public void onAddExpenceOrIncome() {
                        //TODO: CHECK IMPLEMENTATION
                        addExpenceIncomeActivityShow(null);
//                        if(fragment != null) {
//                            getFragmentManager().beginTransaction().remove(fragment).commit();
//                        }
//                        if(homefragment != null) {
//                            getSupportFragmentManager().beginTransaction().remove(homefragment).commit();
//                        }
//                        int position = 0;
//                        for(int count = 0;count<mTitles.length;count++){
//                            if(mTitles[count].equalsIgnoreCase(getString(R.string.side_panel_items_array_income_expense))){
//                                position = count;
//                            }
//                        }
//                        setupAddExpenceIncomeFragement();
//                        selectItem(position,fragment,R.id.content_frame);
                    }
                });

                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, homefragment).commit();
// update selected item and title, then close the drawer
                mDrawerList.setItemChecked(position, true);
                setTitle(mTitles[position]);
                mDrawerLayout.closeDrawer(mDrawerList);
                Intent intent = new Intent(getApplication(),CategorySettingActivity.class);
                intent.putExtra(CategorySettingActivity.CategorySettingActivityGetTotalFlag, true);


                startActivityForResult(intent,ActivityResultIdentifies.GetTotal);
                return;
            }else if(sidePanelAdapter.getItem(position).equalsIgnoreCase(getResources().getString(R.string.side_panel_items_array_currency))){
                 fragment = getCurrencyListFragment();
//                mDrawerList.setItemChecked(0, true);
//                setTitle(mTitles[0]);
//                mDrawerLayout.closeDrawer(mDrawerList);
//                homefragment = null;
//                Intent intent = new Intent(getApplication(),SetYourCurrencyActivity.class);
//                startActivityForResult(intent,ActivityResultIdentifies.CurrencyActivityDismissed);
               // return;
            }else if(sidePanelAdapter.getItem(position).equalsIgnoreCase(getResources().getString(R.string.side_panel_items_array_utilities))){

                fragment = createUtilityFragments();

            }else if(sidePanelAdapter.getItem(position).equalsIgnoreCase(getResources().getString(R.string.side_panel_items_array_help))){

                fragment = new ContactUsFragment();
                ((ContactUsFragment)fragment).setmListener( new ContactUsFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onFragmentInteraction(Uri uri) {

                    }

                    @Override
                    public void onCreatedFragment() {
                        state = CurrentPage.ContactUs;

                    }
                });
            }



            selectItem(position,fragment,R.id.content_frame);
        }
    }

    private UtilitiesFragment createUtilityFragments(){
        UtilitiesFragment utilitiesFragment = new UtilitiesFragment();
        utilitiesFragment.setmListener(new UtilitiesFragment.OnFragmentInteractionListener() {
            @Override
            public void onFragmentInteraction(Uri uri) {

            }
            @Override
            public void onCreatedFragment(){
                state = CurrentPage.Utilities;
                invalidateOptionsMenu();
            }
            @Override
            public void onArchiveButtonClicked(){

            }
            @Override
            public void onLoadFromArchiveClicked(){
                Intent intent = new Intent(getApplicationContext(),LoadFromArchiveListActivity.class);
                startActivityForResult(intent,ActivityResultIdentifies.LoadFromArchiveListActivityPage);
            }
            @Override
            public void onLoadExportToExcelClicked(){
                Intent intent = new Intent(getApplicationContext(),ExportToExcelActivity.class);
                startActivityForResult(intent,ActivityResultIdentifies.ExportToExcelActivityPage);
            }
        });
        return utilitiesFragment;
    }

    private void selectItem(int position,Fragment fragment,int contentFrame) {
        // update the main content by replacing fragments

//        Bundle args = new Bundle();
//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(contentFrame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }


    private CurrencyListFragment getCurrencyListFragment(){
        CurrencyListFragment currencyListFragment = new CurrencyListFragment();

        currencyListFragment.setmListener(new CurrencyListFragment.OnFragmentInteractionListener() {
            @Override
            public void onFragmentInteraction(Uri uri) {

            }

            @Override
            public void onCreatedFragment() {
                state = CurrentPage.Currency;
                invalidateOptionsMenu();
            }
            @Override

            public void editOtherCurrencyActivity(){
                Intent intent = new Intent(HomeScreen.this,SetYourCurrencyActivity.class);
                startActivityForResult(intent,ActivityResultIdentifies.CurrencyActivityDismissed);
            }
        });
               return currencyListFragment;

    }
    private CategoryListFragment getCategoryListFragement(){

        CategoryListFragment categoryListFragment = new CategoryListFragment();
        ((CategoryListFragment)categoryListFragment).setmListener(new CategoryListFragment.OnFragmentInteractionListener() {
            @Override
            public void onFragmentInteraction(Uri uri) {

            }
            public void onCreatedFragment(){
                state = CurrentPage.Category;
                invalidateOptionsMenu();
            }
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id,Boolean segmentedCheckStatus,Category category){
                CategoryListFragment.CategoryListAdapter categoryListAdapter = (CategoryListFragment.CategoryListAdapter)parent.getAdapter();
                if(categoryListAdapter.toBeDeletedFlag) {
                    Intent intent = new Intent(getApplication(),CategoryDeleteConfirmationActivity.class);


                    intent.putExtra(CategoryListFragment.CategoryToBeDeletedOrUpdated, category);

                    startActivityForResult(intent,ActivityResultIdentifies.CategoryDeleteConfirmationKey);

                }else{
                    Intent intent = new Intent(getApplication(),AddCategoryActivity.class);

                    intent.putExtra(AddCategoryActivity.CategoryToBeUpdatedStatus,true);
                    intent.putExtra(AddCategoryActivity.CategoryToBeUpdated, category);

                    startActivityForResult(intent,ActivityResultIdentifies.CategoryUpdateConfirmationKey);

                }
            }
        });
        return categoryListFragment;
    }
    public class SidePanelAdapter extends AmazingAdapter {
        private ArrayList<Pair<String, ArrayList<String>>> all ;

        public SidePanelAdapter(ArrayList<Pair<String, ArrayList<String>>> all){
            this.all = all;

        }

        @Override
        public String getItem(int position) {
            int c = 0;
            for (int i = 0; i < all.size(); i++) {
                if (position >= c && position < c + all.get(i).second.size()) {
                    return all.get(i).second.get(position - c);
                }
                c += all.get(i).second.size();
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        protected void onNextPageRequested(int page) {
        }

        @Override
        protected void bindSectionHeader(View view, int position, boolean displaySectionHeader) {

            if (displaySectionHeader) {
                view.findViewById(R.id.header).setVisibility(View.VISIBLE);
                TextView lSectionTitle = (TextView) view.findViewById(R.id.header);
                lSectionTitle.setText(getSections()[getSectionForPosition(position)]);
            } else {
                view.findViewById(R.id.header).setVisibility(View.GONE);
            }
            view.findViewById(R.id.header).setVisibility(View.GONE);

        }

        @Override
        public View getAmazingView(int position, View convertView, ViewGroup parent) {
            View res = convertView;
            if (res == null) res = getLayoutInflater().inflate(R.layout.item_composer, null);

            TextView nameText = (TextView) res.findViewById(R.id.nameText);
            ImageView homeViewSidePanelListImage = (ImageView) res.findViewById(R.id.homeViewSidePanelListImage);
            String person = getItem(position);
            homeViewSidePanelListImage.setImageDrawable(getDrawableFromStringID(person));



            nameText.setText(person);

            return res;
        }

        @Override
        public void configurePinnedHeader(View header, int position, int alpha) {
            TextView lSectionHeader = (TextView)header;
            lSectionHeader.setText(getSections()[getSectionForPosition(position)]);
            //  lSectionHeader.setBackgroundColor(alpha << 24 | (R.string.app_blue_color));
            //   lSectionHeader.setTextColor(alpha << 24 | (R.string.white_color));
        }
        @Override
        public int getCount() {
            int res = 0;
            for (int i = 0; i < all.size(); i++) {
                res += all.get(i).second.size();
            }
            return res;
        }
        @Override
        public int getPositionForSection(int section) {
            if (section < 0) section = 0;
            if (section >= all.size()) section = all.size() - 1;
            int c = 0;
            for (int i = 0; i < all.size(); i++) {
                if (section == i) {
                    return c;
                }
                c += all.get(i).second.size();
            }
            return 0;
        }

        @Override
        public int getSectionForPosition(int position) {
            int c = 0;
            for (int i = 0; i < all.size(); i++) {
                if (position >= c && position < c + all.get(i).second.size()) {
                    return i;
                }
                c += all.get(i).second.size();
            }
            return -1;
        }

        @Override
        public String[] getSections() {
            String[] res = new String[all.size()];
            for (int i = 0; i < all.size(); i++) {
                res[i] = all.get(i).first;
            }
            return res;
        }
    }


private Drawable getDrawableFromStringID(String settingString){
/*
*
*   <item>@string/side_panel_items_array_home</item>
        <item>@string/side_panel_items_array_income_expense</item>
        <item>@string/side_panel_items_array_expense</item>
        <item>@string/side_panel_items_array_income</item>
        <item>@string/side_panel_items_array_category</item>
        <item>Export Excel</item>
        <item>@string/side_panel_items_array_setting</item>
        <item>@string/side_panel_items_array_get_total</item>
* */

    if(settingString.equalsIgnoreCase(getResources().getString(R.string.side_panel_items_array_home))){
        return getResources().getDrawable(R.drawable.home_settings);
    }else if(settingString.equalsIgnoreCase(getResources().getString(R.string.side_panel_items_array_income_expense))){
        return getResources().getDrawable(R.drawable.add_expense_income_settings);
    }
    else if(settingString.equalsIgnoreCase(getResources().getString(R.string.side_panel_items_array_expense))){
        return getResources().getDrawable(R.drawable.expence_settings);
    }
    else if(settingString.equalsIgnoreCase(getResources().getString(R.string.side_panel_items_array_income))){
        return getResources().getDrawable(R.drawable.income_settings);
    }
    else if(settingString.equalsIgnoreCase(getResources().getString(R.string.side_panel_items_array_get_total))){
        return getResources().getDrawable(R.drawable.get_total_setting);
    }
    else if(settingString.equalsIgnoreCase(getResources().getString(R.string.side_panel_items_array_category))){
        return getResources().getDrawable(R.drawable.category_setting);
    }else if(settingString.equalsIgnoreCase(getResources().getString(R.string.side_panel_items_array_utilities))){
        return getResources().getDrawable(R.drawable.extras_utilities);

    }else if(settingString.equalsIgnoreCase(getResources().getString(R.string.side_panel_items_array_currency))){
        return getResources().getDrawable(R.drawable.currency_drawer);
    }else if(settingString.equalsIgnoreCase(getResources().getString(R.string.side_panel_items_array_help))){
        return getResources().getDrawable(R.drawable.contact_us);
    }

    else{
        return getResources().getDrawable(R.drawable.income_settings);

    }







}

    public static void hideKeyboard(Activity activity) {
        View v = activity.getWindow().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void reloadFragmentsOnRetrunignFromAddExpenceIncome(){
        if(AppController.getInstance().expenceOrIncomeOrCategoryAdded) {
            switch (state) {

                case HomePage:
                    // reaload any way

                    navigateToHome();
                    break;

                case Category:
                    if(AppController.getInstance().categoryAdded){
                        //reload category
                        if(fragment != null) {
                            getFragmentManager().beginTransaction().remove(fragment).commit();
                        }
                        fragment = getCategoryListFragement();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                    }
                    break;
                case Income:
                    if(AppController.getInstance().incomeAdded){
                        //reload
                        if(fragment != null) {
                            getFragmentManager().beginTransaction().remove(fragment).commit();
                        }
                        fragment = getExpenceIncomeListFragment(false);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                    }
                    break;
                case Expense:
                    if(AppController.getInstance().expenceAdded){
                        //reload
                        if(fragment != null) {
                            getFragmentManager().beginTransaction().remove(fragment).commit();
                        }
                        fragment = getExpenceIncomeListFragment(true);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                    }
                    break;
                case Settings:
                    break;
                case GetTotal:
                    //reload get total
                    navigateToHome();
                    break;
                case ContactUs:
                    break;


            }
        }

    }

}
