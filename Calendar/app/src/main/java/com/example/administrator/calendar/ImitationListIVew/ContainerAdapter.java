package com.example.administrator.calendar.ImitationListIVew;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;


import com.example.administrator.calendar.R;
import com.example.administrator.calendar.calendar.CalendarAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2018/2/2 0002.
 */

public class ContainerAdapter extends BaseAdapter {
    Context context;
    public ContainerAdapter(Context context){
        this.context=context;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public View getChildView(View convertView, ViewGroup parent, Date date){
        LayoutInflater inflater = LayoutInflater.from(context);
        View containCell = null;
        GridView gridiew = null;
        ContainerHolder holder = null;
        ArrayList<Date> dates = new ArrayList<Date>();
        CalendarAdapter calendarAdapter = new CalendarAdapter(context,-1);


        if(convertView!=null){
            holder = (ContainerHolder) convertView.getTag();
            gridiew = holder.getGridView();

            setDates(dates,date);
            calendarAdapter.setDate(dates);
            calendarAdapter.setCurrentDate(date);
            gridiew.setAdapter(calendarAdapter);

            holder.setDate(date);
            convertView.setTag(holder);
            return convertView;
        }else{
            containCell = inflater.inflate(R.layout.container_cell,parent,false);
            gridiew = containCell.findViewById(R.id.gridView);

            setDates(dates,date);
            calendarAdapter.setDate(dates);
            calendarAdapter.setCurrentDate(date);
            gridiew.setAdapter(calendarAdapter);

            holder = new ContainerHolder(date,gridiew);
            containCell.setTag(holder);
            return containCell;
        }

    }


    private void setDates(ArrayList<Date> dates ,Date tDate) {
        int sumdays = 0,pulldays = 0,predays = 0;
        Calendar cal = Calendar.getInstance();
        cal.setTime(tDate);

        Calendar firstCal = (Calendar) cal.clone();
        int temp = cal.get(Calendar.DAY_OF_MONTH);
        for(int i=1;i<temp;i++){
            firstCal.add(Calendar.DATE,-1);
        }

        //得到日历表格上的第一天
        Calendar lastCal = (Calendar) firstCal.clone();
        predays = lastCal.get(Calendar.DAY_OF_WEEK)-1;
        sumdays+=predays;
        for(int i=0;i<predays;i++){
            lastCal.add(Calendar.DATE,-1);
        }

        //得到本月的最后一天
        pulldays = firstCal.getActualMaximum(Calendar.DATE);
        sumdays+=pulldays;
        Calendar futureCal = (Calendar) firstCal.clone();
        for(int i=1;i<pulldays;i++){
            futureCal.add(Calendar.DATE,1);
        }

        //得到了当前日历表格中的所有天数
        sumdays+=7-futureCal.get(Calendar.DAY_OF_WEEK);

        for(int i=0;i<sumdays;i++){
            dates.add(lastCal.getTime());
            lastCal.add(Calendar.DATE,1);
        }
    }
}
