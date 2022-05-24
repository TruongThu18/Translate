package com.example.translater.repo;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.translater.model.Translate;

@Database(entities = {Translate.class}, version = 1)
public abstract class TranslateDataBase extends RoomDatabase {
    public abstract TransDao transDao();
    private static final String DB_NAME ="translate.db";
    private static TranslateDataBase instance;

    public static synchronized TranslateDataBase getInstance(Context context){
        if(instance==null){
            instance= Room.databaseBuilder(context.getApplicationContext(), TranslateDataBase.class,
                    DB_NAME).allowMainThreadQueries().build();
        }
        return instance;
    }
}
