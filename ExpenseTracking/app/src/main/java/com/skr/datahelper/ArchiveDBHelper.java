package com.skr.datahelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.skr.AppController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sreejithkr on 22/08/15.
 */
public class ArchiveDBHelper {

    public static final String Archive = "__Archive__";
    public static final String databasePrePath = "/data/data/com.skr.expensetracking/databases/";

    public static void exportDB(String name,File dbpath,Context activity){
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(AppController.ThreeWordsMonthSpaceDateSpaceYearFormatWithUndeScore);
        name = Archive + name + sdf.format(today);

        //File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
      //  String currentDBPath = Environment.getDataDirectory().getPath()+"/data/"+ "com.skr.expensetrack" +"/databases/"+DBHelper.DATABASE_NAME;
       // String backupDBPath = "/data/"+ "com.skr.expensetrack" +"/databases/"+name;
        File currentDB = dbpath;

        Log.e("currentDB","currentDB "+currentDB.toString()+currentDB.exists());
        File backupDB = new File(currentDB.getParentFile(), name);
      //  currentDB.renameTo(backupDB);
        Log.e("currentDB","currentDB "+currentDB.toString());

        Log.e("currentDB","backupDB "+backupDB.toString()+backupDB.exists());

//          ArrayList<File> files =  getListFiles(currentDB.getParentFile());
//        for(int i = 0;i<files.size();i++){
//            Log.e("currentDB","currentDB "+files.get(i).toString());
//
//
//        }

        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
         //   currentDB.delete();
        //    DBHelper.setSharedInstance(null);
            SharedPreferences settings = activity.getSharedPreferences(AppController.MY_APP_PREFERENCE, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(AppController.categoryprepopulated, false);
            editor.commit();
            Log.e("currentDB","currentDB "+currentDB.toString()+currentDB.exists());

            DBHelper dbHelper = DBHelper.getInstance(activity);
            dbHelper.postArchiveOpertaions();
            AppController.categoryReloadToDB(settings,false,activity);

        } catch(IOException e) {
            e.printStackTrace();
        }
    }


    public static void reloadTheDataBAseFromArchive(String name){
        // name = "testAug_22_2015";
        String filePath = databasePrePath+name;
        FileChannel source=null;
        FileChannel destination=null;
        File currentDB = new File(filePath);
        File backupDB = new File(currentDB.getParentFile(), DBHelper.DATABASE_NAME);

        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            //  currentDB.delete();
            Log.e("currentDB","currentDB "+currentDB.toString()+currentDB.exists());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    public static void deletArchive(String name){

        String filePath = databasePrePath+name;
        File currentDB = new File(filePath);
        currentDB.delete();

    }
    public static Boolean ifFileAlreadyExist(String name){
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(AppController.ThreeWordsMonthSpaceDateSpaceYearFormatWithUndeScore);
        name = Archive + name + sdf.format(today);
        String filePath = databasePrePath+name;
        File currentDB = new File(filePath);
        return currentDB.exists();

    }
    private static ArrayList<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getListFiles(file));
            } else {
               // if(file.getName()){
                    inFiles.add(file);
              //  }
            }
        }
        return inFiles;
    }

}
