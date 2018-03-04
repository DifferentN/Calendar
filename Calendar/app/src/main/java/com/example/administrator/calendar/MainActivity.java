package com.example.administrator.calendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.administrator.calendar.ImitationListIVew.ContainerAdapter;
import com.example.administrator.calendar.ImitationListIVew.ObserverDate;
import com.example.administrator.calendar.ImitationListIVew.ViewContain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements ObserverDate {
    private ArrayList<Integer> list;
    private ViewContain viewContainer;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewContainer = findViewById(R.id.viewContainer);
        viewContainer.setAdapter(new ContainerAdapter(this));
        viewContainer.setObserverDate(this);
        textView = findViewById(R.id.textView9);
    }


    @Override
    public void dateChange(Date date) {
        setCurrentDate(date);
    }
    private void setCurrentDate(Date date){
        Calendar cal =Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;

        textView.setText(year+"年"+month+"月");
    }
}
