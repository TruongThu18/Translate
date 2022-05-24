package com.example.translater.repo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.translater.model.Translate;

import java.util.List;

@Dao
public interface TransDao {
    @Insert
    void add(Translate translate);

    @Delete
    void del(Translate translate);

    @Query("select * from translate order by translate.id desc")
    List<Translate> getAll();

    @Query("select * from translate where translate.`from` like '%'||:s||'%'" +
            "or translate.`to` like '%'||:s||'%' order by translate.id desc")
    List<Translate> getByName(String s);
 }
