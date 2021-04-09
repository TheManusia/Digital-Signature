package xyz.themanusia.signaturepdf;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import xyz.themanusia.signaturepdf.ui.Home.HomeActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (ContextCompat.checkSelfPermission(MainActivity.this,
//                Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);
//            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
//        }

        startActivity(new Intent(this, HomeActivity.class));
    }
}