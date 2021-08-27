package com.awn.app.mynotesapp;

import android.view.View;


public class CustomOnItemClickListener implements View.OnClickListener, View.OnLongClickListener {
    private int position;
    private OnItemClickCallback onItemClickCallback;

    public CustomOnItemClickListener(int position, OnItemClickCallback onItemClickCallback) {
        this.position = position;
        this.onItemClickCallback = onItemClickCallback;
    }
    @Override
    public void onClick(View view) {
        onItemClickCallback.onItemClicked(view, position);
    }

    @Override
    public boolean onLongClick(View view) {
        return true;

    }

    public interface OnItemClickCallback {
        void onItemClicked(View view, int position);
    }
}