package com.android.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.android.room.joinmodel.ProjectJoinModel;
import com.android.service.model.Project;
import com.android.service.model.ProjectId;

import java.util.List;


@Dao
public interface PersonDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertProject(Project... project);

    @Insert
    public void insertProject(List<Project> project);

    @Update
    public void updateProject(List<Project> project);

    @Delete
    public void deletePerson(Project person);

    @Query("SELECT * FROM Project")
    public LiveData<List<Project>> getAllPersons();


    @Insert
    public void insertProjectDetail(ProjectId project);

    @Update
    public void updateProjectDetail(ProjectId project);

    @Query("SELECT * FROM Project WHERE name = :name")
    public LiveData<ProjectJoinModel> findProjectDetail(String name);

//
//    @Query("SELECT * FROM person where mobile = :mobileIn")
//    public LiveData<Person> getPersonByMobile(String mobileIn);
//
//    @Query("SELECT * FROM person where city In (:cityIn)")
//    public List<Person> getPersonByCities(List<String> cityIn);

}
