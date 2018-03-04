package com.example.administrator.calendar.ImitationListIVew;

import android.widget.GridView;

import java.util.Date;

/**
 * Created by Administrator on 2018/2/14 0014.
 */

public class ContainerHolder {
    private Date date;
    private GridView gridView;

    public ContainerHolder(){}
    public ContainerHolder(Date date, GridView gridView) {
        this.date = date;
        this.gridView = gridView;
    }

    public Date getDate() {
        return date;
    }

    public GridView getGridView() {
        return gridView;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setGridView(GridView gridView) {
        this.gridView = gridView;
    }
}
