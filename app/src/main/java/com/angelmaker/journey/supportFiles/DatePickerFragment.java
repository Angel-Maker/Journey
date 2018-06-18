package com.angelmaker.journey.supportFiles;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */


public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private TextView dateTV;

    private int year;
    private int month;
    private int day;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog = new DatePickerDialog(
                getActivity(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                this,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    public void setSpinnerDate(Calendar spinnerCalendar)
    {
        year = spinnerCalendar.get(Calendar.YEAR);
        month = spinnerCalendar.get(Calendar.MONTH);
        day = spinnerCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public void setTV(TextView newDateTV){dateTV = newDateTV;}

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if(dateTV != null){
            month++;

            String displayYear = Integer.toString(year);
            String displayMonth = Integer.toString(month);
            String displayDay = Integer.toString(day);

            if(month<10){ displayMonth = "0"+displayMonth; }
            if(day<10){ displayDay = "0"+displayDay; }

            dateTV.setText(displayYear+"/"+displayMonth+"/"+displayDay);
        }
    }
}