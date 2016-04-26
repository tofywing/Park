package googleplay.personal.yee.park.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Yee on 4/18/16.
 */
public class ImageService {

    public static final String TYPE_MAP = "Map";
    public static final String TYPE_SIGHT = "Sight";
    public static int x = -1;
    public static int y = -1;

    Context mContext;
    // public static String newPath = "";


    public ImageService(Context context) {
        mContext = context;
    }


    public void saveImage(Bitmap image, String type, int parkId) throws IOException {
        String path = mContext.getFilesDir() + type;
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        String newPath = path + String.valueOf(parkId);
        File myCaptureFile = new File(newPath);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        image.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }

    public Bitmap getBitmap(String path) {
        return BitmapFactory.decodeFile(path);
    }

    public Bitmap getCroppedBitmap(Bitmap bmp, int screenWidth) {
        Bitmap sbmp;
        if (x == -1 || y == -1) {
            x = screenWidth / 4;
            y = screenWidth / 2;
        }
        sbmp = Bitmap.createBitmap(bmp, x, y, y, y);
        int radius = sbmp.getWidth();
        Bitmap output = Bitmap.createBitmap(Math.round(sbmp.getWidth()), Math.round(sbmp.getHeight()), Bitmap.Config
                .ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(radius, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);
        return output;
    }

}
