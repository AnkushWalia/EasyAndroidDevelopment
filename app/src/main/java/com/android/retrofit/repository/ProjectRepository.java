package com.android.retrofit.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.android.BuildConfig;
import com.android.retrofit.RetrofitClient;
import com.android.room.DatabaseCreator;
import com.android.room.PersonDAO;
import com.android.service.model.Project;
import com.android.service.model.ProjectId;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class ProjectRepository {
    private static final ProjectRepository projectRepo = new ProjectRepository();
    public static PersonDAO personDAO;
    private static ApiService retrofitClient;
    private final Executor executor = Executors.newFixedThreadPool(2);

    public static ProjectRepository getInstance(Context context) {
        if (personDAO == null)
            personDAO = DatabaseCreator.getPersonDatabase(context).PersonDatabase();
        if (retrofitClient == null)
            retrofitClient = RetrofitClient.with(context).getClient(BuildConfig.BASE_URL).create(ApiService.class);
        return projectRepo;
    }

    public static ProjectRepository getInstance() {
        return projectRepo;
    }


    public final LiveData<List<Project>> getProjectList(String userId) {
        final MutableLiveData<List<Project>> data = new MutableLiveData<>();

        retrofitClient.getProjectList(userId).enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                executor.execute(() -> {
                    try {
                        personDAO.insertProject(response.body());
                        Log.e("----------insertProject", "onResponse: ");
                    } catch (Exception e) {
                        personDAO.updateProject(response.body());
                        Log.e("----------updateProject", "onResponse: ");

                    }
                });

            }

            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {
                // TODO better error handling in part #2 ...
                data.setValue(null);
            }
        });

        return data;
    }

    public final LiveData<ProjectId> getProjectDetails(String userID, String projectName) {
        final MutableLiveData<ProjectId> data = new MutableLiveData<>();

        retrofitClient.getProjectDetails(userID, projectName).enqueue(new Callback<ProjectId>() {
            @Override
            public void onResponse(Call<ProjectId> call, Response<ProjectId> response) {
                executor.execute(() -> {
                    try {
                        personDAO.insertProjectDetail(response.body());
                    } catch (Exception e) {
                        personDAO.updateProjectDetail(response.body());
                    }

                });

            }

            @Override
            public void onFailure(Call<ProjectId> call, Throwable t) {
                // TODO better error handling in part #2 ...
                data.setValue(null);
            }
        });

        return data;
    }

    void checkAppUpdate(Context context) {
        RetrofitClient.with(context).getClient("https://play.google.com/store/apps/").create(ApiService.class).checkAppUpdate("com.pawalert").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//                        showLoading();
                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull ResponseBody soAnswersResponse) {
//                        hideLoading();
//                        log(soAnswersResponse + "");
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
//                        log(e.getMessage() + "" + e);
//                        handleError(e, null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void simulateDelay() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
