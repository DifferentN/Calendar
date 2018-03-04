package com.example.administrator.calendar.ImitationListIVew;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.calendar.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2018/2/1 0001.
 */

public class ViewContain extends ViewGroup {
    private Context context;
    private Rect rect;
    private int mItenCount,mPaddingTop,mPaddingLeft,mPaddingRight,mPaddingBottom;
    //保存的时onMeasure()传入的参数
    private int mWidthMeasureSpec,mHeightMeasureSpec;

    //上次手指的位置
    private int mLastY;
    private int mActiveId = -1;

    private View mChild[],pre,cur,next;
    private int mChildCount;

    private ContainerRecycleBin recycleBin;

    private boolean onInLayout = false;

    //存放的数据
    private ContainerAdapter adapter;

    private boolean isScroll = false;

    //代表一个界面中上下两个日历view
    private View upView,downView;

    //代表当前显示日历view的日期
    private Date curDate;
    private Calendar cal;


    private ObserverDate observerDate;

    public ViewContain(Context context) {
        this(context,null);
    }

    public ViewContain(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ViewContain(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewContain);

        rect = new Rect();
        mPaddingLeft = rect.left = typedArray.getInt(R.styleable.ViewContain_paddingLeft,0);
        mPaddingTop = rect.top = typedArray.getInt(R.styleable.ViewContain_paddingTop,0);
        mPaddingRight = rect.right = typedArray.getInt(R.styleable.ViewContain_paddingRight,0);
        mPaddingBottom = rect.bottom = typedArray.getInt(R.styleable.ViewContain_paddingBottom,0);

        typedArray.recycle();

        recycleBin = new ContainerRecycleBin();
        cal = Calendar.getInstance();
        curDate = cal.getTime();
    }

    public void setAdapter(ContainerAdapter adapter){
        this.adapter = adapter;
    }

    public void setObserverDate(ObserverDate observerDate){
        this.observerDate = observerDate;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int childWidth = 0;
        int childHeiht = 0;
        int childState = 0;

        //从adapter获得
        mItenCount=0;

        View child = obtainView(curDate,0);
        measureScrapChild(child,widthMeasureSpec,heightMeasureSpec);
        childWidth = child.getMeasuredWidth();
        childHeiht = child.getMeasuredHeight();
        childState = combineMeasuredStates(childState,child.getMeasuredState());

        recycleBin.addScrapView(child);

        if(widthMode == MeasureSpec.UNSPECIFIED){
            width = mPaddingLeft + childWidth + mPaddingRight;
        }else if (widthMode == MeasureSpec.EXACTLY){
            width |= (childState & MEASURED_STATE_MASK);
        }else if (widthMode == MeasureSpec.AT_MOST){
            //设置的为wrap_content,所以获得一个最小的宽度
            width = Math.min(width,childWidth + mPaddingRight + mPaddingLeft);
        }

        if(heightMode == MeasureSpec.UNSPECIFIED){
            height = mPaddingTop + childHeiht + mPaddingBottom;
        }

        if(heightMode == MeasureSpec.AT_MOST){
            //设置的为wrap_content,所以获得一个最小的高度
            height =Math.min(height,mPaddingTop + childHeiht + mPaddingBottom);
        }

        setMeasuredDimension(width,height);

        mWidthMeasureSpec = widthMeasureSpec;
        mHeightMeasureSpec = heightMeasureSpec;
    }

    /**
     * 测量子view
     * @param child 要测量的子view
     * @param widthMeasureSpec 父view的参数
     * @param heightMeasureSpect 父view的参数
     */
    private void measureScrapChild(View child, int widthMeasureSpec, int heightMeasureSpect) {

        LayoutParams lp =  child.getLayoutParams();
        if(lp==null){
            lp =  generateDefaultLayoutParams();
            child.setLayoutParams(lp);
        }

        //根据父view的widthMeasureSpec，和自身的lp.width获得子view最合适的宽度
        int childWidthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec,
                mPaddingLeft+mPaddingRight,lp.width);
        int height = lp.height;
        //根据父view的widthMeasureSpec，和自身的lp.width获得子view最合适的高度
        int childHeightSpec = getChildMeasureSpec(heightMeasureSpect,
                mPaddingTop+mPaddingBottom,lp.height);

        //测量子view
        child.measure(childWidthSpec,childHeightSpec);

//        child.forceLayout();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mChildCount = adapter.getCount();
        //若正在布局，则退出
        if(!onInLayout){
            onInLayout = true;
        }else{
            return ;
        }

        if(recycleBin.activeView==null){
            mChildCount = 0;
        }

        //将当前在ViewContain中的view加入recycleBin，并将他们从父view中移除
        recycleBin.fillActiveView(mChildCount);
        detachAllViewsFromParent();

        invalidate();

        //重新填充ViewContain
        fillChildView(mChildCount,mPaddingTop,mPaddingLeft,true,0);

        //提示更新日历
        notifyDateChanged(curDate);

        onInLayout = false;

    }

    /**
     *
     * @param mCount mCount为0表示第一次layout（因为viewContain中无view），为1表示第二次layout
     * @param y y轴方向开始的位置
     * @param childLeft x轴方向开始的位置
     * @param flowDown 为true表示向下填充view，false表示向上填充view
     */
    private void fillChildView(int mCount,int y, int childLeft,boolean flowDown,int month) {

        View child = obtainView(curDate,month);

        boolean onWindow = false;
        boolean needToMeasure = false;
        LayoutParams lp = child.getLayoutParams();
        if(lp == null){
            lp = generateDefaultLayoutParams();
        }

        //mCount>0表示我们已经显示过子view了，新的子view是重复利用被抛弃的子view的（已测量过）
        //mCount=0表示是第一次布局子view，子view为新的，要为子view测量
        if(mCount>0){
            onWindow=true;
        }
        needToMeasure = !onWindow||child.isLayoutRequested();

        if(!onWindow){
            addViewInLayout(child,flowDown?-1:0,lp);
        }else{
            attachViewToParent(child,flowDown?-1:0,lp);
        }

        //测量子view
        if(needToMeasure){
            int childWidthSpec = ViewGroup.getChildMeasureSpec(mWidthMeasureSpec,
                    mPaddingLeft+mPaddingRight,lp.width);
            int height = lp.height;
            int childHeightSpec = getChildMeasureSpec(mHeightMeasureSpec,
                    mPaddingTop + mPaddingBottom,lp.height);

            child.measure(childWidthSpec,childHeightSpec);
        }else{
            cleanupLayoutState(child);
        }

        int w = child.getMeasuredWidth();
        int h = child.getMeasuredHeight();
        //根据flowDown判断childTop
        int childTop = flowDown?y:y-h;
        if(needToMeasure){
            int childRight = childLeft + w;
            int childBottom = childTop + h;
            child.layout(childLeft,childTop,childRight,childBottom);
        }else{
            child.offsetLeftAndRight(childLeft-child.getLeft());
            child.offsetTopAndBottom(childTop-child.getTop());
        }

    }

    /**
     *
     * @param date 当前日期
     * @param month 0 获得当前日期，1(x) 获得下一个(x个)月日期，-1（x） 获得上一个(x个)月日期
     * @return
     */
    private View obtainView(Date date,int month) {

        Date rightDate = getRightDate(date,month);

        View activeChild = recycleBin.getActiveView(0);
        if(activeChild != null){
            return activeChild;
        }

        View scrapView = recycleBin.getScrapView();
        View childView = adapter.getChildView(scrapView,this,rightDate);
        if(scrapView!=null){
            if(scrapView!=childView){
                recycleBin.addScrapView(scrapView);

            }
        }
        return childView;
    }

    /**
     *
     * @param date 当前日期
     * @param month 0 获得当前日期，1(x) 获得下一个(x个)月日期，-1（x） 获得上一个(x个)月日期
     * @return
     */
    private Date getRightDate(Date date, int month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int curDay = cal.get(Calendar.DAY_OF_MONTH);
        for(int i=1;i<curDay;i++){
            cal.add(Calendar.DATE,-1);
        }
        cal.add(Calendar.MONTH,month);
        return cal.getTime();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean down = false;

        int ePointerIndex = e.findPointerIndex(mActiveId);
        if(ePointerIndex == -1){
            ePointerIndex = 0;
            mActiveId = e.getPointerId(ePointerIndex);
        }

        int y = (int) e.getY(ePointerIndex);
        int offset = 0;

        int action = e.getAction();

         switch (action){
             case MotionEvent.ACTION_DOWN:
                 mLastY = y;
                 break;
             case MotionEvent.ACTION_MOVE:
                 //计算滑动距离
                 offset = y-mLastY;
                 if(offset>0){
                     down = true;
                 }else down = false;

                 //滑动viewContain中的view
                 traceScroll(offset,down);
                 //填充新的view
                 fillViewGap(down);
                 mLastY = y;
                 break;
             case MotionEvent.ACTION_UP:
                 doResilience();
                 break;
             default: break;
         }

        return true;
    }

    /**
     *
     * @param offset 表示手指移动的偏移量
     * @param down true表示手指下滑，false为上滑
     */
    private void traceScroll(int offset, boolean down) {

        int childCount = getChildCount();

        View child;

        int start = 0;
        int count = 0;

        int month = 0;

        //回收不可见的view，加入ScrapView，并移动view
        if(down){
            int bottom = getHeight()-mPaddingBottom - offset;
            for(int i=childCount-1;i>=0;i--){
                child = getChildAt(i);
                if(child.getTop()>bottom){
                    start = i;
                    count++;
                    recycleBin.addScrapView(child);
                    child.offsetTopAndBottom(offset);

                    month--;

                }else{
                    child.offsetTopAndBottom(offset);

                    if(child.getTop()>mPaddingTop&&child.getTop()<getHeight()-mPaddingBottom){
                        downView = child;
                    }else if(child.getBottom()<getHeight()-mPaddingBottom&&child.getBottom()>mPaddingTop){
                        upView = child;
                    }
                }
            }
        }else{
            int top = mPaddingTop - offset;
            for(int i=0;i<childCount;i++){
                child= getChildAt(i);
                if(child.getBottom()<top){
                    count++;
                    recycleBin.addScrapView(child);
                    child.offsetTopAndBottom(offset);

                    month++;
                }else{
                    child.offsetTopAndBottom(offset);

                    if(child.getTop()>mPaddingTop&&child.getTop()<getHeight()-mPaddingBottom){
                        downView = child;
                    }else if(child.getBottom()<getHeight()-mPaddingBottom&&child.getBottom()>mPaddingTop){
                        upView = child;
                    }
                }

            }
        }

        //将加入ScrapView的view从Parant移除
        if(count>0){
            detachViewsFromParent(start,count);
        }

        curDate = getRightDate(curDate,month);
        notifyDateChanged(curDate);


    }

    /**
     * 根据当前屏幕中view的位置，加入新的view
     * @param down true：手指下滑，应向上填充view，false：手指上滑，应向下填充view
     */
    private void fillViewGap(boolean down) {
        int minTop = Integer.MAX_VALUE, maxBottom = Integer.MIN_VALUE;
        View child;
        for (int i=0;i<getChildCount();i++){
            child = getChildAt(i);
            minTop = Math.min(minTop,child.getTop());
            maxBottom = Math.max(maxBottom,child.getBottom());
        }

        if(down){
            if(minTop>mPaddingTop&&minTop!=Integer.MAX_VALUE){
                fillChildView(-1,minTop,mPaddingLeft,false,-1);
            }
        }else{
            if(maxBottom<getHeight()-mPaddingBottom){
                fillChildView(-1,maxBottom,mPaddingLeft,true,1);
            }
        }
    }

    /**
     * 将占有一半屏幕的view，设置为主view，即将它扩展到全屏
     */
    private void doResilience(){
        int offset = 0;
        if(upView!=null){
            if(upView.getBottom()<(getHeight()-mPaddingTop-mPaddingBottom)/2){
                offset = -(upView.getBottom()-mPaddingTop);
            }else{
                offset = getHeight()-mPaddingBottom-upView.getBottom();
            }
            upView.offsetTopAndBottom(offset);

            if(downView!=null){
                downView.offsetTopAndBottom(offset);
                //只回收遇到的第一个退出屏幕的view
                doRecycleView();
            }
        }
        //重置curDate，为当前view的日期
        View childVIew = getChildAt(0);
        ContainerHolder holder;
        if(childVIew!=null){
            holder = (ContainerHolder) childVIew.getTag();
            if(holder!=null){
                curDate = holder.getDate();
            }else{
                Log.e("LZH","error get null holder");
            }
        }else{
            Log.e("LZH","error get null view");
        }

        notifyDateChanged(curDate);
        //请求重新布局，因为日历的行数不一致，应重新设置高度
        requestLayout();
    }


    private void doRecycleView() {
        int start = 0,count = 0,childCount = 0;
        childCount = getChildCount();
        View child = null;
        if(childCount == 1){
            return ;
        }
        for(int i=0;i<childCount;i++){
            child = getChildAt(i);
            if(child.getBottom()<=mPaddingTop||child.getTop()>=getHeight()-mPaddingBottom){
                recycleBin.addScrapView(child);
                detachViewsFromParent(i,1);
                break;
            }
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();

        switch(action){
            case MotionEvent.ACTION_DOWN:
                mLastY = (int) ev.getY();
                return false;
            case MotionEvent.ACTION_MOVE:
                return true;
            case MotionEvent.ACTION_UP:
                return false;
        }
        return false;
    }

    private void notifyDateChanged(Date date){
        if(observerDate!=null){
            observerDate.dateChange(date);
        }
    }


    class ContainerRecycleBin{
        //存放当前屏幕显示的view
        public View activeView[];

        //存放已被废弃的view
        public ArrayList<View> scrapView = new ArrayList<View>();

        public void fillActiveView(int count){
            if(count==0){
                return;
            }
            if(activeView == null){
                activeView = new View[count];
            }

            View actives[] = activeView;
            View child = null;
            LayoutParams lp;
            for(int i=0;i<count;i++){
                child = getChildAt(i);
                lp = child.getLayoutParams();
                if(lp!=null){
                    actives[i] = child;
                }
            }
        }

        public View getActiveView(int pos){
            if(pos>0&&pos<activeView.length) {
                View match = activeView[0];
                activeView[0] = null;
                return match;
            }
            return null;
        }

        public  void addScrapView(View view){
            LayoutParams lp = view.getLayoutParams();
            if(lp == null){
                return;
            }
            scrapView.add(view);
        }

        public  View getScrapView(){
            View view = null;
            if(scrapView.size()>0){
                view = scrapView.remove(scrapView.size()-1);
            }
            return view;
        }
    }
}

