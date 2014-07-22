package cz.destil.wearsquare.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.wearable.DataMap;
import com.mariux.teleport.lib.TeleportClient;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.destil.wearsquare.R;
import cz.destil.wearsquare.adapter.CheckInAdapter;
import cz.destil.wearsquare.core.BaseActivity;
import cz.destil.wearsquare.core.BaseAsyncTask;
import cz.destil.wearsquare.util.DebugLog;

public class CheckInActivity extends BaseActivity {

    @InjectView(R.id.list)
    WearableListView vList;
    @InjectView(R.id.progress)
    ProgressBar vProgress;

    CheckInAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        ButterKnife.inject(this);
        mAdapter = new CheckInAdapter(this);
        vList.setAdapter(mAdapter);

        teleport().setOnSyncDataItemTask(new TeleportClient.OnSyncDataItemTask() {
            @Override
            protected void onPostExecute(DataMap result) {
                DebugLog.d("receiving item");
                new LoadBitmapTask(result).start();
            }
        });
    }


    @Override
    protected void onConnected() {
        super.onConnected();
        DebugLog.d("sending start message");
        teleport().sendMessage("/start", null);
    }

    class LoadBitmapTask extends BaseAsyncTask {

        Bitmap bitmap;
        DataMap result;

        LoadBitmapTask(DataMap result) {
            this.result = result;
        }

        @Override
        public void inBackground() {
            bitmap = teleport().loadBitmapFromAsset(result.getAsset("icon"));
        }

        @Override
        public void postExecute() {
            vProgress.setVisibility(View.GONE);
            mAdapter.addVenue(new CheckInAdapter.Venue("todo", result.getString("name"), bitmap));
            mAdapter.notifyItemInserted(mAdapter.getItemCount());
        }
    }
}