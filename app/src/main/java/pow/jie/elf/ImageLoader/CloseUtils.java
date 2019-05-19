package pow.jie.elf.ImageLoader;

import java.io.Closeable;
import java.io.IOException;

public class CloseUtils {
    public static void closeQuietly(Closeable closeable){
        if(closeable != null){
            try {
                closeable.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
