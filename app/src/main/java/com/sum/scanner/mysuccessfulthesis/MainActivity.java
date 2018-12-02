package com.sum.scanner.mysuccessfulthesis;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.sum.scanner.mysuccessfulthesis.fragments.CameraFragment;
import com.sum.scanner.mysuccessfulthesis.fragments.HistoryFragment;
import com.sum.scanner.mysuccessfulthesis.fragments.ResultFragment;
import com.sum.scanner.mysuccessfulthesis.fragments.SettingsFragment;

import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private static final Logger logger = Logger.getLogger(MainActivity.class.getName());
    private Toolbar toolbar;
    private byte[] imageBytes;
    private String result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        changeFragments(new CameraFragment(), getString(R.string.code_scanner));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera:
                changeFragments(new CameraFragment(), getString(R.string.code_scanner));
                return true;
            case R.id.history:
                changeFragments(new HistoryFragment(), getString(R.string.history));
                return true;
            case R.id.settings:
                changeFragments(new SettingsFragment(), getString(R.string.settings));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void changeFragments(Fragment fragment, String toolbarTitle) {
        logger.info("Switching to " + fragment.getClass().getName());
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
        toolbar.setTitle(toolbarTitle);
    }

    public void changeToResultFragments(String result) {
        setResult(result);
        changeFragments(new ResultFragment(), getString(R.string.code_scanner));
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
