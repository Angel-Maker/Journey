package com.angelmaker.journey;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mainMenuToolbar = findViewById(R.id.mainMenuToolbar);
        setSupportActionBar(mainMenuToolbar);
    }

    ///////////////////////////////////////////////////////
    /////////////////////Menu bar code/////////////////////
    ///////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mainMenuInflater = getMenuInflater();
        mainMenuInflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.updateActivitiesMenuItem){
            Intent updateActivities = new Intent(this, UpdateActivities.class);
            updateActivities.putExtra("test", "Hello from Main Screen!!!");
            startActivity(updateActivities);
        }
        return super.onOptionsItemSelected(item);
    }


    public void dailyActivityBtn(View view) {
        Intent newActivity = new Intent(this, NewActivity.class);
        startActivity(newActivity);
    }
}

