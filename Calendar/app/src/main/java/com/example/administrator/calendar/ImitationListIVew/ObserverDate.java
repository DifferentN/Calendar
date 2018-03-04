package com.example.administrator.calendar.ImitationListIVew;

import java.util.Date;

/**
 * Created by Administrator on 2018/2/19 0019.
 * 若当前日历view发生移动，则更改日期
 */

public interface ObserverDate {
    public abstract void dateChange(Date date);

}
