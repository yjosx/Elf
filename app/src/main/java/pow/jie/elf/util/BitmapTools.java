package pow.jie.elf.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapTools {
    public static Bitmap changeBitmapSize(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        //计算压缩的比率
        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;

        //获取想要缩放的matrix
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        //获取新的bitmap
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        bitmap.getWidth();
        bitmap.getHeight();
        return bitmap;
    }

//    private Drawable getDiscBlackgroundDrawable() {
//        int discSize = (int) (mScreenWidth * DisplayUtil.SCALE_DISC_SIZE);
//        Bitmap bitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R
//                .drawable.ic_disc_blackground), discSize, discSize, false);
//        Bitmap src = BitmapFactory.decodeResource(getResources(), imageId);
//        Bitmap dst;
//        //将长方形图片裁剪成正方形图片
//        if (src.getWidth() >= src.getHeight()){
//            dst = Bitmap.createBitmap(src, src.getWidth()/2 - src.getHeight()/2, 0, src.getHeight(), src.getHeight()
//            );
//        }else{
//            dst = Bitmap.createBitmap(src, 0, src.getHeight()/2 - src.getWidth()/2, src.getWidth(), src.getWidth()
//            );
//        }
//        RoundedBitmapDrawable roundDiscDrawable = RoundedBitmapDrawableFactory.create
//                (getResources(), bitmapDisc);
//        return roundDiscDrawable;
//    }
//
//    private Drawable getDiscDrawable(int musicPicRes) {
//        int discSize = (int) (mScreenWidth * DisplayUtil.SCALE_DISC_SIZE);
//        int musicPicSize = (int) (mScreenWidth * DisplayUtil.SCALE_MUSIC_PIC_SIZE);
//
//        Bitmap bitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R
//                .drawable.ic_disc), discSize, discSize, false);
//        Bitmap bitmapMusicPic = getMusicPicBitmap(musicPicSize,musicPicRes);
//        BitmapDrawable discDrawable = new BitmapDrawable(bitmapDisc);
//        RoundedBitmapDrawable roundMusicDrawable = RoundedBitmapDrawableFactory.create
//                (getResources(), bitmapMusicPic);
//
//        //抗锯齿
//        discDrawable.setAntiAlias(true);
//        roundMusicDrawable.setAntiAlias(true);
//
//        Drawable[] drawables = new Drawable[2];
//        drawables[0] = roundMusicDrawable;
//        drawables[1] = discDrawable;
//
//        LayerDrawable layerDrawable = new LayerDrawable(drawables);
//        int musicPicMargin = (int) ((DisplayUtil.SCALE_DISC_SIZE - DisplayUtil
//                .SCALE_MUSIC_PIC_SIZE) * mScreenWidth / 2);
//        //调整专辑图片的四周边距
//        layerDrawable.setLayerInset(0, musicPicMargin, musicPicMargin, musicPicMargin,
//                musicPicMargin);
//
//        return layerDrawable;
//    }
//    /*设备屏幕宽度*/
//    public static int getScreenWidth(Contet contet) {
//        return contet.getResources().getDisplayMetrics().widthPiels;
//    }
//
//    /*设备屏幕高度*/
//    public static int getScreenHeight(Contet contet) {
//        return contet.getResources().getDisplayMetrics().heightPiels;
//    }
}
