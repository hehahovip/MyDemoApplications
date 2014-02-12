package com.hehaho.android.demo.camerademo;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.ImageView;

import java.util.List;

import dalvik.system.PathClassLoader;

public class MainActivity extends Activity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

       // Log.i("Premisson for camera:", PackageManager.getSystemAvailableFeatures(PackageManager.FEATURE_CAMERA));

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private ImageView mImageView;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main2, container, false);

            Button takePhoto = (Button)rootView.findViewById(R.id.button);
            Button showPic = (Button)rootView.findViewById(R.id.saveBtn);

            if(!PlaceholderFragment.isIntentAvailable(container.getContext(), MediaStore.ACTION_IMAGE_CAPTURE)){
                takePhoto.setEnabled(false);
            }

            mImageView = (ImageView)rootView.findViewById(R.id.imageView);
            takePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dispatchTakePictureIntent(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            });

            return rootView;
        }

        private void dispatchTakePictureIntent(int actionCode) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, actionCode);
           // handleSmallCameraPhoto(takePictureIntent);
        }

        private void handleSmallCameraPhoto(Intent intent) {
            Bundle extras = intent.getExtras();
            Bitmap mImageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(mImageBitmap);
        }

        public static boolean isIntentAvailable(Context context, String action) {
            final PackageManager packageManager = context.getPackageManager();
            final Intent intent = new Intent(action);
            List<ResolveInfo> list =
                    packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            handleSmallCameraPhoto(data);
        }
    }

}
