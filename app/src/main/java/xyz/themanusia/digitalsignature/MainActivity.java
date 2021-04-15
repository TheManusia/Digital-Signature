package xyz.themanusia.digitalsignature;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;

import xyz.themanusia.digitalsignature.ui.home.HomeActivity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String FOLDER_PATH = Environment.getExternalStorageDirectory() + File.separator + "DigitalSignature";
    private static final String[] PERMISSION = new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
    private static final int PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(PERMISSION, PERMISSION_CODE);
        else
            ActivityCompat.requestPermissions(MainActivity.this,
                    PERMISSION,
                    PERMISSION_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        File folder = new File(FOLDER_PATH);

        if (!folder.exists())
            if (folder.mkdirs())
                Log.e(TAG, "onRequestPermissionsResult: Folder Created");
            else
                Log.e(TAG, "onRequestPermissionsResult: Failed " + FOLDER_PATH);

        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}