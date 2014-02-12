package com.hehaho.android.demo.filewriter.service;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.hehaho.android.demo.filewriter.MainActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by leejun on 14-2-7.
 */
public class WriteFileTask extends AsyncTask<String,Integer,Boolean> {

    @Override
    protected Boolean doInBackground(String... strings) {
        boolean flag = true;

        Log.i("Asyc Task:", "doing in background");
        File file = new File(strings[1] + "/textfile.txt");
        try{
            FileOutputStream outfile = new FileOutputStream(file, true);
            outfile.write(String.valueOf(strings[0]).getBytes());
            outfile.write(String.valueOf("\n").getBytes());
            outfile.close();
        }  catch (FileNotFoundException e) {
            flag = false;
            e.printStackTrace();
            Log.e("Write file error!", "File textfile.txt not found", e);
        } catch (IOException e) {
            e.printStackTrace();
        }


        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
            Log.i("SDCard state:", "External Storage is can be  read & write!");
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
            Log.i("SDCard state:", "External Storage is only can be read!");
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
            Log.i("SDCard state:", "External Storage is not available!");
        }

        File externalPath = Environment.getExternalStorageDirectory();

        File externalFolder = new File(externalPath.getPath() + "/" + MainActivity.APP_PACKAGE_NAME);
        if(!externalFolder.exists()){
            externalFolder.mkdir();
            Log.i("File: ", "Create external folder!");
        }

        if(mExternalStorageWriteable){
            File externalFile = new File(externalPath.getPath() + "/" + MainActivity.APP_PACKAGE_NAME + "/textfile.txt");
            try{
                FileOutputStream outfile = new FileOutputStream(externalFile, true);
                outfile.write(strings[0].getBytes());
                outfile.write(String.valueOf("\n").getBytes());
                outfile.close();
            }  catch (FileNotFoundException e) {
                flag = false;
                e.printStackTrace();
                Log.e("Write file error!", "File textfile.txt not found", e);
            } catch (IOException e) {
                flag = false;
                e.printStackTrace();
                Log.e("Write file error!", "File wirte error!", e);
            }
        }
        return flag;
    }
}
