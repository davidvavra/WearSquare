package cz.destil.wearsquare.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.destil.wearsquare.R;
import cz.destil.wearsquare.core.BaseActivity;
import cz.destil.wearsquare.event.ErrorEvent;

public abstract class ProgressActivity extends BaseActivity {

    @InjectView(R.id.progress)
    ProgressBar vProgress;
    @InjectView(R.id.error)
    TextView vError;
    @InjectView(R.id.container)
    RelativeLayout vContainer;
    private View mMainView;

    abstract int getMainViewResourceId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        ButterKnife.inject(this);
        mMainView = ((ViewGroup)LayoutInflater.from(this).inflate(getMainViewResourceId(), vContainer)).getChildAt(2);
    }

    @Override
    public void startDisconnected() {
        super.startDisconnected();
        showError(getString(R.string.please_connect));
    }

    @Subscribe
    public void onError(ErrorEvent event) {
        showError(event.getMessage());
    }

    public void showProgress() {
        vProgress.setVisibility(View.VISIBLE);
        vError.setVisibility(View.GONE);
    }

    public void hideProgress() {
        vProgress.setVisibility(View.GONE);
    }

    private void showError(String message) {
        vProgress.setVisibility(View.GONE);
        vError.setVisibility(View.VISIBLE);
        vError.setText(message);
    }

    public View getMainView() {
        return mMainView;
    }

}
