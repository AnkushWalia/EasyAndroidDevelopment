package com.android.retrofit;

import com.android.view.base.BaseActivity;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.SocketException;
import java.net.UnknownHostException;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.HttpException;


public final class RetrofitErrorHandle {

    public static void handleError(BaseActivity context, Throwable throwable, final ActionClickListener retryClickListener) {
        context.hideLoading();
        if (throwable instanceof HttpException) {
            HttpException response = (HttpException) throwable;
            int code = response.code();
            ResponseBody body = response.response().errorBody();
            Converter<ResponseBody, Error> errorConverter =
                    RetrofitClient.retrofit.responseBodyConverter(Error.class, new Annotation[0]);
            if (code == 403) {
                context.showSnackBar(throwable.getMessage());
                context.openActivityOnTokenExpire();
            } else if (errorConverter != null && body != null) {
                try {
                    Error error = errorConverter.convert(body);
                    context.showSnackBar(error.getMessage());
                } catch (IOException e1) {
                    context.showSnackBar(throwable.getMessage());
                }
            } else
                context.showSnackBar(throwable.getMessage());
        } else if (throwable instanceof UnknownHostException || throwable instanceof SocketException) {
            context.showSnackBar("Internet unreachable. Please try after sometime.", "Retry", new ActionClickListener() {  //connection unavailable
                @Override
                public void onActionClicked(Snackbar snackbar) {
                    snackbar.dismiss();
                    if (retryClickListener != null)
                        retryClickListener.onActionClicked(snackbar);
                }
            });
        } else {
            context.showSnackBar(throwable.getMessage());
        }
    }


}
