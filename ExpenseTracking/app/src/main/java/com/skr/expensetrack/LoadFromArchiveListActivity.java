package com.skr.expensetrack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.skr.AppController;
import com.skr.customviews.CustomAlert;
import com.skr.datahelper.ArchiveDBHelper;
import com.skr.datahelper.DBHelper;

import java.io.File;
import java.util.ArrayList;

public class LoadFromArchiveListActivity extends ActionBarActivity {
    ArchiveItemsListAdapter archiveItemsListAdapter;
    int dateStringLength = AppController.ThreeWordsMonthSpaceDateSpaceYearFormatWithUndeScore.length();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_from_archive_list);
        if(getSupportActionBar() != null){getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_green)));}

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_white);
        ListView archive_list_view =  (ListView)findViewById(R.id.archive_list_view);

        archive_list_view.setEmptyView(findViewById(R.id.nodataTextNo_archive));
         archiveItemsListAdapter =  new ArchiveItemsListAdapter(archiveList());
        archive_list_view.setAdapter(archiveItemsListAdapter);

        archive_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArchiveItemsListAdapter archiveItemsListAdapter = (ArchiveItemsListAdapter)parent.getAdapter();
                onCreateDialog(archiveItemsListAdapter,position);


            }
        });

    }
    private ArrayList archiveList(){
        String parentDirectory = getDatabasePath(DBHelper.DATABASE_NAME).getParent();
        File db = new File(parentDirectory);
        ArrayList<ArchiveDetails> fileNames = new ArrayList();
        if(db.isDirectory()) {
            File[] files = db.listFiles();
            for (int count = 0;count<files.length;count++){
                String filePath = files[count].toString().replace(parentDirectory + "/", "");
                if(filePath.contains(ArchiveDBHelper.Archive)) {
                    fileNames.add(getTheArchiveDetailsFromString(filePath));
                    Log.e("", "files[count].toString()" + parentDirectory);
                }
            }
        }
        return fileNames;
    }
    public void onCreateDialog(final ArchiveItemsListAdapter archiveItemsListAdapter,final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.dialog_load_from_archive_item_click, null);

        final TextView error_message_dialog_archive = (TextView)view.findViewById(R.id.error_message_dialog_archive);
        // error_message_dialog_archive.setVisibility(View.GONE);
        builder.setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog dialog = builder.create();

        dialog.show();
        final ArchiveDetails archiveDetails = archiveItemsListAdapter.getItem(position);
        view.findViewById(R.id.load_current_archive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArchiveDBHelper.reloadTheDataBAseFromArchive(archiveDetails.getCombinedArchiveName());
                dialog.dismiss();
                new CustomAlert.CustomBuilder(v.getContext(),getLayoutInflater(),getString(R.string.info))
                        .setTitle(R.string.info)
                        .setMessage(getResources().getString(R.string.reload_archive_dialog_des_sucess_message))
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                // do nothing
                            }
                        }).setIcon(R.drawable.success_icon)
                        .show();

            }
        });
        view.findViewById(R.id.delete_current_archive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArchiveDBHelper.deletArchive(archiveDetails.getCombinedArchiveName());
                dialog.dismiss();


                archiveItemsListAdapter.reloadData(archiveList());
                new CustomAlert.CustomBuilder(v.getContext(),getLayoutInflater(),getString(R.string.info))
                        .setTitle(R.string.info)
                        .setMessage(getResources().getString(R.string.delete_dialog_des_sucess_message))
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                // do nothing
                            }
                        }).setIcon(R.drawable.success_icon)
                        .show();

            }
        });


    }

    private  ArchiveDetails getTheArchiveDetailsFromString(String stringToExtractDetails){

        stringToExtractDetails = stringToExtractDetails.replace(ArchiveDBHelper.Archive,"");
        int stringLength = stringToExtractDetails.length();

        String archive_name = stringToExtractDetails.substring(0, stringLength-dateStringLength);
        String archived_date = stringToExtractDetails.substring(stringLength-dateStringLength,stringLength-1);
        return  new ArchiveDetails(archive_name,archived_date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_load_from_archive_list, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        if (id ==  android.R.id.home) {
            finish();

        }

        return super.onOptionsItemSelected(item);
    }
    private  class ArchiveDetails{

        String archive_name;
        String archived_date;

        private ArchiveDetails(String archive_name, String archived_date) {
            this.archive_name = archive_name;
            this.archived_date = archived_date;
        }

        public String getArchive_name() {
            return archive_name;
        }

        public void setArchive_name(String archive_name) {
            this.archive_name = archive_name;
        }

        public String getArchived_date() {
            return archived_date;
        }

        public void setArchived_date(String archived_date) {
            this.archived_date = archived_date;
        }

        public String getCombinedArchiveName(){
            return ArchiveDBHelper.Archive+archive_name+archived_date;
        }



    }

    public class ArchiveItemsListAdapter extends BaseAdapter {
        ArrayList<ArchiveDetails> all;

        ArchiveItemsListAdapter(ArrayList<ArchiveDetails> all) {
            this.all = all;

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return all.size();
        }

        @Override
        public ArchiveDetails getItem(int arg0) {
            // TODO Auto-generated method stub
            return all.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            // TODO Auto-generated method stub

            View res = arg1;
            if (res == null) res = getLayoutInflater().inflate(R.layout.list_archive_item, null);

            ArchiveDetails archiveDetail = getItem(arg0);
            TextView archive_name  = (TextView)res.findViewById(R.id.archive_name);
            archive_name.setText(archiveDetail.archive_name);
            TextView archived_date  = (TextView)res.findViewById(R.id.archived_date);
            archived_date.setText(archiveDetail.archived_date);

            return res;
        }
        public void reloadData(ArrayList<ArchiveDetails> all){
            this.all.clear();
            this.all = all;
            notifyDataSetChanged();
        }


    }

    }
