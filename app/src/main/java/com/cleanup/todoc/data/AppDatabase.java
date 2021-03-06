package com.cleanup.todoc.data;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.cleanup.todoc.data.dao.ProjectDao;
import com.cleanup.todoc.data.dao.TaskDao;
import com.cleanup.todoc.data.entity.Project;
import com.cleanup.todoc.data.entity.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Project.class, Task.class}, version = 1, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {

    public static final String APP_NAME = "app_database";
    public abstract TaskDao taskDao();
    public abstract ProjectDao projectDao();
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static AppDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (AppDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, APP_NAME)
                            .addCallback(prepopulateDatabase)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback prepopulateDatabase = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {

                ProjectDao projectDao = INSTANCE.projectDao();
                projectDao.deleteAll();

                Project project = new Project("Projet Tartampion", 0xFFEADAD1);
                projectDao.insert(project);

                 project = new Project("Projet Lucidia", 0xFFB4CDBA);
                projectDao.insert(project);

                project = new Project("Projet Circus", 0xFFA3CED2);
                projectDao.insert(project);

                TaskDao taskDao = INSTANCE.taskDao();
                taskDao.deleteAll();



                Log.d(TAG, "onCreate: db is created");

            });

        }
    };


}
