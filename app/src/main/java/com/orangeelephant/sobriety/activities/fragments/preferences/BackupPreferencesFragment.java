package com.orangeelephant.sobriety.activities.fragments.preferences;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.orangeelephant.sobriety.R;
import com.orangeelephant.sobriety.backup.BackupSecret;
import com.orangeelephant.sobriety.backup.CreateBackup;
import com.orangeelephant.sobriety.backup.ImportBackup;
import com.orangeelephant.sobriety.backup.NoSecretExistsException;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;

public class BackupPreferencesFragment extends Fragment {
    private static final String[] permissions = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.backup_fragment, container, false);
        view.findViewById(R.id.ImportBackup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                importBackup();
            }
        });

        view.findViewById(R.id.CreateBackup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createBackup();
            }
        });

        setPassphrase();
        return view;
    }

    private void setPassphrase() {
        BackupSecret backupSecret = new BackupSecret(null);
    }

    private void selectBackupLocation() {

    }

    private void createBackup() {
        CreateBackup b = new CreateBackup();
        try {
            b.setPassphrase("Bob");
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        try {
            TextView t = getView().findViewById(R.id.backup_text);
            t.setText(b.getEncryptedDataAsString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NoSecretExistsException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    private void importBackup() {
        /*requestPermissions();

        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("*//*");
        intent = Intent.createChooser(chooseFile, "Choose a file");
        startActivity(intent);*/

        //String path =  intent.getData().getPath();
        TextView textView = getView().findViewById(R.id.input);
        CharSequence data = textView.getText();
        System.out.println(data);
        try {
            ImportBackup i = new ImportBackup(data.toString());
            i.setPassphrase("Bob");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), permissions, 12345);
    }
}
