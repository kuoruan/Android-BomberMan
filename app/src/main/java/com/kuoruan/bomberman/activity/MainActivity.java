package com.kuoruan.bomberman.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kuoruan.bomberman.R;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private TextView tv_create_game;
    private TextView tv_join_game;
    private TextView tv_exit_game;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initView();
    }

    private void initView() {
        tv_create_game = (TextView) findViewById(R.id.bt_single_play);
        tv_join_game = (TextView) findViewById(R.id.bt_multi_game);
        tv_exit_game = (TextView) findViewById(R.id.bt_exit_game);
        tv_create_game.setOnClickListener(this);
        tv_join_game.setOnClickListener(this);
        tv_exit_game.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_single_play:
                startActivity(new Intent(this, GameActivity.class));
                break;
            case R.id.bt_multi_game:
                startActivity(new Intent(this, ConnectionActivity.class));
                break;
        }
    }

    @Override
    protected boolean hasBackButton() {
        return false;
    }
}
