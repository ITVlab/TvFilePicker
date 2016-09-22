package news.androidtv.samples.filepicker;

import android.Manifest;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import news.androidtv.filepicker.FilePickerFragment;
import news.androidtv.filepicker.filters.FileExtensionFilter;
import news.androidtv.filepicker.model.AbstractFile;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int PERMISSION_READ = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                PermissionChecker.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PermissionChecker.PERMISSION_GRANTED) {
            startFilePickerFragment();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_READ);
        } else {
            startFilePickerFragment();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startFilePickerFragment();
    }

    private void startFilePickerFragment() {
        Log.d(TAG, "Start file picker");
        FilePickerFragment filePickerFragment = FilePickerFragment.newInstance(this,
                AbstractFile.fromLocalPath(Environment.getExternalStorageDirectory()),
                new FileExtensionFilter("png"), new FilePickerFragment.SelectionCallback() {
                    @Override
                    public boolean onFilePicked(FilePickerFragment filePickerFragment,
                            AbstractFile abstractFile) {
                        Toast.makeText(MainActivity.this, "User selected " + abstractFile.getUri(),
                                Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    @Override
                    public boolean onDirectoryPicked(FilePickerFragment filePickerFragment,
                            AbstractFile abstractDirectory) {
                        return filePickerFragment.explore(
                                AbstractFile.fromLocalAbstractFile(abstractDirectory));
                    }
        });
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(android.R.id.content, filePickerFragment, "root");
        transaction.commit();
    }
}
