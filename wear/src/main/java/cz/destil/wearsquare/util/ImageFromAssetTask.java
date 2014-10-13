package cz.destil.wearsquare.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;

/**
 * Workaround for: https://github.com/Mariuxtheone/Teleport/issues/14
 */
public abstract class ImageFromAssetTask extends AsyncTask<Object, Void, Bitmap> {

    @Override
    protected Bitmap doInBackground(Object... params) {
        Asset asset = (Asset) params[0];
        GoogleApiClient apiClient = (GoogleApiClient) params[1];
        if (asset == null || apiClient == null || !apiClient.isConnected()) {
            return null;
        }
        try {
            InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                    apiClient, asset).await().getInputStream();
            if (assetInputStream == null) {
                return null;
            }
            return BitmapFactory.decodeStream(assetInputStream);
        } catch (NullPointerException e) {
            // workaround
            return null;
        }
    }

    @Override
    protected abstract void onPostExecute(Bitmap bitmap);

};