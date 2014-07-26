package cz.destil.wearsquare.util;

import android.graphics.Bitmap;

import com.google.android.gms.wearable.Asset;

import java.io.ByteArrayOutputStream;

public class ImageUtils {
    private static String sScreenDimensions;

    public static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    public static void setScreenDimensions(String sScreenDimensions) {
        ImageUtils.sScreenDimensions = sScreenDimensions;
    }

    public static String getScreenDimensions() {
        return sScreenDimensions;
    }
}
