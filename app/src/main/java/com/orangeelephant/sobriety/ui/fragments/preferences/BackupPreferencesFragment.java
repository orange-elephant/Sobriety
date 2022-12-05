package com.orangeelephant.sobriety.ui.fragments.preferences;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
        view.findViewById(R.id.ImportBackup).setOnClickListener(importer -> importBackup());

        /* view.findViewById(R.id.CreateBackup).setOnClickListener(backup -> {
            Dialogs.PasswordSetListener listener = (password) -> {
                setPassphrase(new String(password));
            };
            Dialogs.showSetPasswordDialog(getContext(), listener);
        }); */

        return view;
    }

    private void setPassphrase(String passphrase) {
        try {
            new BackupSecret(null).setPassphrase(passphrase);

            Toast t = new Toast(getContext());
            t.setText("Password set successfully");
            t.show();

            createBackup();
        } catch (GeneralSecurityException e) {
            Toast t = new Toast(getContext());
            t.setText("Failed to set password");
            t.show();
        }
    }

    private void selectBackupLocation() {

    }

    private void createBackup() {
        CreateBackup b = new CreateBackup();

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
            i.setPassphrase("test");
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
