package cz.destil.wearsquare.event;

import android.graphics.Bitmap;
/**
 * Otto event which is fired when image is received from the phone.
 *
 * @author David Vávra (david@vavra.me)
 */
public class ImageLoadedEvent {
    private final String imageUrl;
    private final Bitmap bitmap;

    public ImageLoadedEvent(String imageUrl, Bitmap bitmap) {
        this.imageUrl = imageUrl;
        this.bitmap = bitmap;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
