package com.ver2point0.android.roygvegetables.ui;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.ver2point0.android.roygvegetables.R;

public class EditorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // ADD CODE to prepare options menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                // ADD CODE to saveVegetable();
                // exit activity
                finish();
                return true;
            case R.id.action_delete:
                // ADD CODE to showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // ADD CODE to get to parent activity
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
