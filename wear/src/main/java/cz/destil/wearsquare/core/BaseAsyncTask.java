package cz.destil.wearsquare.core;

import android.os.AsyncTask;
import android.os.Build;

public abstract class BaseAsyncTask extends AsyncTask<Void, Void, Void> {

    public void start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            this.executeOnExecutor(THREAD_POOL_EXECUTOR);
        } else {
            this.execute();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        inBackground();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        postExecute();
    }

    public abstract void inBackground();

    public abstract void postExecute();
}
