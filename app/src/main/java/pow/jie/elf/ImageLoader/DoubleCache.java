package pow.jie.elf.ImageLoader;

import android.graphics.Bitmap;

public class DoubleCache implements ImageCache{
    ImageCache mDiskCache = new DiskCache();
    ImageCache mMemoryCache = new MemoryCache();
    @Override
    public Bitmap get(String url) {
        Bitmap bitmap = mMemoryCache.get(url);
        if(bitmap == null){
            bitmap = mDiskCache.get(url);
        }
        return bitmap;
    }


    @Override
    public void put(String url, Bitmap bitmap){
        mDiskCache.put(url,bitmap);
        mMemoryCache.put(url,bitmap);
    }
}
