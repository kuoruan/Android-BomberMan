package com.kuoruan.bomberman.activity;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by Liao on 2016/6/16 0016.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected String TAG = this.getClass().getSimpleName();
    private ActionBar mActionBar;
    protected LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBeforeSetContentView();
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        mInflater = getLayoutInflater();

        if (hasActionBar()) {
            initActionBar();
        }
        init(savedInstanceState);
    }

    protected void initActionBar() {
        mActionBar = getSupportActionBar();
        if(mActionBar!=null) {
            //mActionBar.setBackgroundDrawable(getResources().getDrawable(R.color.actionbar_bg));
            mActionBar.setDisplayShowTitleEnabled(true);
            mActionBar.setDisplayUseLogoEnabled(true);
            if (hasBackButton()) {
                mActionBar.setDisplayShowHomeEnabled(false);
                mActionBar.setDisplayHomeAsUpEnabled(true);
                //mActionBar.setHomeAsUpIndicator(R.drawable.ic_launcher);
            }
            if (hasActionBarCustomView()) {
                mActionBar.setDisplayShowCustomEnabled(true);
                int layoutRes = getActionBarCustomViewLayoutRescId();
                View actionBarView = inflateView(layoutRes);
                ActionBar.LayoutParams params;
                if (isAllActionBarCustom()) {
                    params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                            ActionBar.LayoutParams.MATCH_PARENT);
                } else {
                    params = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                            ActionBar.LayoutParams.MATCH_PARENT);
                    params.gravity = Gravity.RIGHT;
                }
                mActionBar.setCustomView(actionBarView, params);
                handlerActionBarCustomViewAction(actionBarView);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackAction();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onBackAction() {
        onBackPressed();
    }

    protected void handlerActionBarCustomViewAction(View actionBarView) {

    }

    private View inflateView(int layoutRes) {
        return mInflater.inflate(layoutRes, null);
    }

    protected boolean hasActionBarCustomView() {
        return false;
    }

    protected boolean hasBackButton() {
        return true;
    }

    protected boolean hasActionBar() {
        return true;
    }

    protected void onBeforeSetContentView() {
    }


    protected abstract int getLayoutId();

    protected abstract void init(Bundle savedInstanceState);

    protected int getActionBarCustomViewLayoutRescId() {
        return 0;
    }

    protected boolean isAllActionBarCustom() {
        return false;
    }

    protected void setActionBarTitle(int resId) {
        if (resId != 0) {
            setActionBarTitle(getString(resId));
        }
    }

    protected void setActionBarTitle(String title) {
        if (hasActionBar()) {
            mActionBar.setTitle(title);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            onBackAction();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
