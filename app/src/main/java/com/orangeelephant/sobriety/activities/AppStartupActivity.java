package com.orangeelephant.sobriety.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.orangeelephant.sobriety.database.SqlCipherMigration;

import net.sqlcipher.database.SQLiteDatabase;

public class AppStartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load libraries necessary for sqlcipher library to function
        SQLiteDatabase.loadLibs(this);
        new SqlCipherMigration(this);

        Intent intent = new Intent(AppStartupActivity.this, HomeScreenActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}