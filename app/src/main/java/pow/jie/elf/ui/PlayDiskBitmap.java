package pow.jie.elf.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class PlayDiskBitmap {
    /**
     * 合成碟片图片
     *
     * @param albumBitmap 专辑封面图
     * @return
     */
    public static Bitmap mergeThumbnailBitmap(Bitmap albumBitmap) {

        //获得底图宽和高
        int w = 40;
        int h = 40;
        //根据底图的宽和高，对专辑图片进行缩放
        albumBitmap = Bitmap.createScaledBitmap(albumBitmap, w, h, true);
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //这里需要先画出一个圆
        canvas.drawCircle(w / 2, h / 2, w / 2 - 20, paint);
        //圆画好之后将画笔重置一下
        paint.reset();
        //设置图像合成模式，该模式为只在源图像和目标图像相交的地方绘制源图像
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(albumBitmap, 0, 0, paint);
        return bm;
    }

    public static void AnimatorAction(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 5000);

        animator.setInterpolator(new LinearInterpolator());//匀速旋转

        animator.setRepeatCount(ValueAnimator.INFINITE);//无限循环
        animator.start();
    }
}
