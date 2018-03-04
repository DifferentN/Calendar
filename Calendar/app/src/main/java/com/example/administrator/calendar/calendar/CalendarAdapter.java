package com.example.administrator.calendar.calendar;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.administrator.calendar.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2018/2/14 0014.
 */

public class CalendarAdapter extends ArrayAdapter implements View.OnClickListener{
    private ArrayList<Date> dates;
    private Context context;
    private Calendar cal =  Calendar.getInstance();
    private Calendar curCal = (Calendar) cal.clone();
    private Date curDate;
    private MyTextView preView;

    public CalendarAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    public CalendarAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public CalendarAdapter(@NonNull Context context, int resource, @NonNull Object[] objects) {
        super(context, resource, objects);
    }
    public  void setDate(ArrayList<Date> dates){
        this.dates=dates;
    }


    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MyTextView curView;
        CalendarViewHolder hodler;
        Date date = getDateItem(position);

        if(curDate!=null){
            curCal.setTime(curDate);
        }

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.cell,parent,false);
            curView = convertView.findViewById(R.id.textView);
            hodler = new CalendarViewHolder(curView);
            convertView.setTag(hodler);
            setCellContent(date,curView);
        }else{
            hodler = (CalendarViewHolder) convertView.getTag();
            curView = hodler.textView;
            setCellContent(date,curView);
        }
        return convertView;
    }

    //判断月份，为每个cell染色
    private void setCellContent(Date date, MyTextView curView) {
        cal.setTime(date);
        if(cal.get(Calendar.MONTH) == curCal.get(Calendar.MONTH) ){
            curView.setTextColor(Color.BLACK);
            curView.setText(cal.get(Calendar.DAY_OF_MONTH)+"");
            curView.setOnClickListener(this);
            if (cal.get(Calendar.DAY_OF_MONTH) == curCal.get(Calendar.DAY_OF_MONTH)) {
                curView.setSign(true);
            }
        }else{
            curView.setTextColor(Color.GRAY);
            curView.setText(cal.get(Calendar.DAY_OF_MONTH)+"");
        }
    }

    private Date getDateItem(int position){
        return dates.get(position);
    }

    public void setCurrentDate(Date date){
        curDate = date;
    }

    @Override
    public void onClick(View v) {
        MyTextView myView;
        myView = (MyTextView) v;
        myView.setClickSign(true);

        myView.invalidate();
        if(preView != null){
            preView.invalidate();
        }
        preView = myView;
    }
}
