package pow.jie.elf.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pow.jie.elf.R;
import pow.jie.elf.util.BitmapTools;

public class MoodChangerView extends View {
    private MenuListener menuListener;
    private boolean isShow = false;
    private int rect = 0;
    private int color;
    private int realHeight = 0;
    private int width = 90;
    private int height = 90;
    Paint mPaint;
    Path path;
    List<Integer> moodsInt = new ArrayList<>();
    List<Bitmap> moods = new ArrayList<>();

    private static int MOOD_CALM = 0;
    private static int MOOD_EXCITING = 1;
    private static int MOOD_HAPPY = 2;
    private static int MOOD_UNHAPPY = 3;

    public MoodChangerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initData();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        height = width = width > height ? height : width;//取较小的

        moods.set(0, BitmapTools.changeBitmapSize(moods.get(0), width, height));
        moods.set(1, BitmapTools.changeBitmapSize(moods.get(1), width, height));
        moods.set(2, BitmapTools.changeBitmapSize(moods.get(2), width, height));
        moods.set(3, BitmapTools.changeBitmapSize(moods.get(3), width, height));

        height += moods.size() * width;     //根据加入图标个数累加view高度
        realHeight = height - width;//加入图标的总高度
        setMeasuredDimension(width, height);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP: {
                result = TouchMethod((int) event.getX(), (int) event.getY(), false);
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                result = TouchMethod((int) event.getX(), (int) event.getY(), true);
                break;
            }
        }
        return result;
    }

    private boolean TouchMethod(int x, int y, boolean isDown) {
        if (y > getMeasuredHeight() - getMeasuredWidth() && y < getMeasuredHeight()) { //如果点在底部按钮上
            if (!isDown)
                startAnimation();
            return true;
        } else if (y > 0 && y < getMeasuredHeight() - getMeasuredWidth() && isShow) {//如果点在选项上，并且按钮在展开状态
            if (!isDown) {
                for (int i = moodsInt.size(); i > 0; i--) { //计算并判断点在了哪个位置（view的宽度为Width，高度为icon.size*Width,相当于每个图标所占的区域都是正方形）
                    if (y > (i - 1) * getMeasuredWidth() && y < i * getMeasuredWidth()) {
                        if (menuListener != null) {
                            menuListener.click(moodsInt.get(moodsInt.size() - i));//调用接口
                            Collections.swap(moods, moods.size() - i, 0);
                            Collections.swap(moodsInt, moodsInt.size() - i, 0);
                            invalidate();
                            startAnimation();
                        }
                    }
                }
            }
            return true;
        }
        startAnimation();
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public void onDraw(Canvas canvas) {
        int px = getMeasuredWidth() / 2;

        mPaint.setColor(color);       //设置画笔颜色
        mPaint.setStyle(Paint.Style.FILL);  //设置画笔模式为画
        mPaint.setStrokeWidth(10f);//设置画笔宽度为10px
        mPaint.setAntiAlias(true);

        path.setFillType(Path.FillType.EVEN_ODD);
        canvas.translate(px, getMeasuredHeight() - px);//移动坐标中心

        canvas.drawRect(-px, -rect, px, 0, mPaint);//画出两个半圆中间的矩形

        canvas.drawBitmap(moods.get(0), -moods.get(0).getWidth() / 2, -moods.get(0).getHeight() / 2, mPaint); //获取并绘制按钮没有展开时的图标

        for (int i = 1; i < moods.size(); i++) {
            if (rect >= 2 * px * (i + 1)) {//2*px=getMeasuredWidth(),i+1=当前的图标个数(-y)，如果上升高度足够显示下一个图标，就绘制
                Bitmap bitmap = moods.get(i);
                canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2, -bitmap.getHeight() / 2 - (i + 1) * 2 * px, mPaint);//在相应位置绘制图标
            }
        }

        if (rect == realHeight) { //完全展开
            isShow = true;
        } else if (rect == 0) { //完全闭合
            isShow = false;
        }
    }

    public void initData() {
        Bitmap moodCalm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_mood_clam);
        Bitmap moodExciting = BitmapFactory.decodeResource(getResources(), R.drawable.ic_mood_exciting);
        Bitmap moodHappy = BitmapFactory.decodeResource(getResources(), R.drawable.ic_mood_happy);
        Bitmap moodUnhappy = BitmapFactory.decodeResource(getResources(), R.drawable.ic_mood_unhappy);
        moodCalm = BitmapTools.changeBitmapSize(moodCalm, width, height);
        moodExciting = BitmapTools.changeBitmapSize(moodExciting, width, height);
        moodHappy = BitmapTools.changeBitmapSize(moodHappy, width, height);
        moodUnhappy = BitmapTools.changeBitmapSize(moodUnhappy, width, height);

        moods.add(moodCalm);
        moodsInt.add(MOOD_CALM);
        moods.add(moodExciting);
        moodsInt.add(MOOD_EXCITING);
        moods.add(moodHappy);
        moodsInt.add(MOOD_HAPPY);
        moods.add(moodUnhappy);
        moodsInt.add(MOOD_UNHAPPY);

        mPaint = new Paint();
        path = new Path();
        color = getResources().getColor(R.color.colorPrimary);
    }

    private class expand extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            rect = (int) (interpolatedTime * realHeight);
            invalidate();
        }
    }

    private class fold extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            rect = (int) ((1 - interpolatedTime) * realHeight);
            invalidate();
        }
    }

    public void startAnimation() {
        if (!isShow) {
            expand move = new expand();
            move.setDuration(300);
            move.setInterpolator(new AccelerateDecelerateInterpolator());
            startAnimation(move);
        } else {
            fold move = new fold();
            move.setDuration(300);
            move.setInterpolator(new AccelerateDecelerateInterpolator());
            startAnimation(move);
        }
    }

    public void setMenuListener(MenuListener menuListener) {
        this.menuListener = menuListener;
    }

    public interface MenuListener { //需实现此接口以便接受点击事件
        void click(int i);
    }
}