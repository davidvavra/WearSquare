package cz.destil.wearsquare.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.destil.wearsquare.R;
import cz.destil.wearsquare.core.BaseActivity;

public abstract class ProgressActivity extends BaseActivity {

    @InjectView(R.id.progress)
    ProgressBar vProgress;
    @InjectView(R.id.error)
    TextView vError;
    @InjectView(R.id.main)
    FrameLayout vMainContainer;
    @InjectView(R.id.small_progress)
    ProgressBar vSmallProgress;
    private View vMainView;

    abstract int getMainViewResourceId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        ButterKnife.inject(this);
        hideSmallProgress();
        vMainView = ((FrameLayout) LayoutInflater.from(this).inflate(getMainViewResourceId(), vMainContainer)).getChildAt(0);
    }

    @Override
    public void startDisconnected() {
        super.startDisconnected();
        showError(getString(R.string.please_connect));
    }

    public void showProgress() {
        vProgress.setVisibility(View.VISIBLE);
        vError.setVisibility(View.GONE);
    }

    public void hideProgress() {
        vProgress.setVisibility(View.GONE);
    }

    public void hideSmallProgress() {
        vSmallProgress.setVisibility(View.GONE);
    }

    public void showSmallProgress() {
        vSmallProgress.setVisibility(View.VISIBLE);
    }

    public void showError(String message) {
        vProgress.setVisibility(View.GONE);
        vError.setVisibility(View.VISIBLE);
        vError.setText(message);
    }

    public View getMainView() {
        return vMainView;
    }

}
