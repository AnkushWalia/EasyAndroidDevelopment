package com.android.view.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import com.nispok.snackbar.listeners.ActionClickListener;


public class BaseFragment extends Fragment implements AdapterView.OnItemClickListener,
        View.OnClickListener, AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {

    private BaseActivity baseActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity = (BaseActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        baseActivity.hideKeyboard();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onClick(View v) {

    }

    public void showToast(String msg) {
        baseActivity.showMessage(msg);
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

    public void showLoading() {
        baseActivity.showLoading();
    }

    public void hideLoading() {
        baseActivity.hideLoading();
    }

    public void openActivityOnTokenExpire() {
        baseActivity.openActivityOnTokenExpire();
    }

    public void onError(int resId) {
        baseActivity.onError(resId);
    }

    public void onError(String message) {
        baseActivity.onError(message);
    }

    public void showMessage(String message) {
        baseActivity.showMessage(message);
    }

    public void showMessage(int resId) {
        baseActivity.showMessage(resId);
    }

    public boolean isNetworkConnected() {
        return baseActivity.isNetworkConnected();
    }

    public void hideKeyboard() {
        baseActivity.hideKeyboard();
    }

    public void hideKeyboard(Dialog dialog) {
        baseActivity.hideKeyboard(dialog);
    }


    public BaseActivity getBaseActivity() {
        return baseActivity;
    }

    public interface Callback {

        void onFragmentAttached();

        void onFragmentDetached(String tag);
    }
}
