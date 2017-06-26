package com.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import com.android.activity.BaseActivity;
import com.nispok.snackbar.listeners.ActionClickListener;


public class BaseFragment extends Fragment implements AdapterView.OnItemClickListener,
        View.OnClickListener, AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {

    public BaseActivity baseActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity = (BaseActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        baseActivity.hideSoftKeyboard();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onClick(View v) {

    }

    public void showToast(String msg) {
        baseActivity.showToast(msg);
    }

    public void showSnackBar(String message, String action, ActionClickListener actionClickListener) {
        baseActivity.showSnackBar(message, action, actionClickListener);
    }

    public void showSnackBar(String msg) {
        baseActivity.showSnackBar(msg);
    }


    public void log(String s) {
        baseActivity.log(s);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

}
