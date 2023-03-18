package com.example.healthy.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.healthy.model.Healthy;
import com.example.healthy.model.Meal;
import com.example.healthy.model.Protein;
import com.example.healthy.model.StepModel;
import com.example.healthy.model.Water;
import com.example.healthy.untils.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    private static String TAG = "DataBaseHelper";
    private static String DB_PATH = "";
    private static String DB_NAME = "Health.db";
    private SQLiteDatabase mDataBase;
    private final Context mContext;


    public DbHelper(Context context) {
        super(context, DB_NAME, null, 1);
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.mContext = context;
    }

    public void createDataBase() {
        boolean mDataBaseExist = checkDataBase();
        if (!mDataBaseExist) {
            this.getReadableDatabase();
            try {
                copyDataBase();
                Log.e(TAG, "createDatabase database created");
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
            this.close();
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public boolean openDataBase() throws SQLException {
        String mPath = DB_PATH + DB_NAME;
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }


    public boolean delete(String rowId, String tbName, String rowIdKey) {
        openDataBase();
        return mDataBase.delete(tbName, rowIdKey + "=" + rowId, null) > 0;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void addInformation(Infomation information) {
        openDataBase();
        ContentValues addRow = new ContentValues();
        addRow.put(Constants.NAME, information.name);
        addRow.put(Constants.BIRTHDAY, information.birthday);
        addRow.put(Constants.GENDER, information.gender);
        addRow.put(Constants.HEIGHT, information.height);
        addRow.put(Constants.WEIGHT, information.weight);
        addRow.put(Constants.OCCU, information.occu);
        mDataBase.insert(Constants.TB_INFORMATION, null, addRow);
        close();
    }

    public void updateInformation(String height, String weight, int id) {
        openDataBase();
        ContentValues addRow = new ContentValues();
        addRow.put(Constants.HEIGHT, height);
        addRow.put(Constants.WEIGHT, weight);
        mDataBase.update(Constants.TB_INFORMATION, addRow, Constants.ID_HEALTHY + "=?", new String[]{id + ""});


    }


    public void addHealthy(Healthy healthy) {
        openDataBase();
        ContentValues addRow = new ContentValues();
        addRow.put(Constants.STEPS, healthy.step);
        addRow.put(Constants.WATER, healthy.water);
        addRow.put(Constants.SLEEP, healthy.sleep);
        addRow.put(Constants.HEART, healthy.heart);
        addRow.put(Constants.BOOL, healthy.bool);
        addRow.put(Constants.BMI, healthy.BMI);
        addRow.put(Constants.CALORIES, healthy.Calorie);
        addRow.put(Constants.DISTANCE, healthy.distance_step);
        addRow.put(Constants.DATE, healthy.date);
        addRow.put(Constants.ENERGY, healthy.energy);
        mDataBase.insert(Constants.TB_HEALTHY, null, addRow);
        close();
    }


    public void addWater(String time) {
        openDataBase();
        ContentValues addRow = new ContentValues();
        addRow.put("time", time);
        mDataBase.insert("water", null, addRow);
    }

    public void addMeal(Meal meal) {
        openDataBase();
        ContentValues addRow = new ContentValues();
        addRow.put(Constants.FOOD, meal.name);
        addRow.put(Constants.TYPE, meal.type);
        addRow.put(Constants.KCAL, meal.kcal);
        mDataBase.insert(Constants.TB_MEAL, null, addRow);
    }

    public void addProtein(Protein meal) {
        openDataBase();
        ContentValues addRow = new ContentValues();
        addRow.put(Constants.PROTEIN, meal.protein);
        addRow.put(Constants.FAT, meal.fat);
        addRow.put(Constants.KCAL, meal.kcal);
        addRow.put(Constants.DATE, meal.date);
        mDataBase.insert(Constants.TB_PROTEIN, null, addRow);
    }

    public void addStep(StepModel stepModel) {
        openDataBase();
        ContentValues addRow = new ContentValues();
        addRow.put(Constants.STEPS, stepModel.getSteps());
        addRow.put(Constants.CALORIES, stepModel.getKcal());
        addRow.put(Constants.DISTANCE, stepModel.getDistance());
        addRow.put(Constants.DATE, stepModel.getDate());
        mDataBase.insert(Constants.TB_STEPS, null, addRow);
    }

    public List<Meal> getMeal() {
        Meal stepModel = new Meal();
        openDataBase();

        List<Meal> list = new ArrayList<>();
        openDataBase();
        Cursor cursor = mDataBase.query(Constants.TB_MEAL, null, null, null, null, null, null);
        while (cursor.moveToNext()) {

            stepModel = new Meal(cursor.getInt(0)
                    , cursor.getString(1)
                    , cursor.getString(2)
                    , cursor.getString(3)
            );
            list.add(stepModel);
        }

        close();
        return list;


    }

    public Protein getProtein() {
        Protein stepModel = new Protein();
        openDataBase();

        Cursor cursor = mDataBase.query(Constants.TB_PROTEIN, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            stepModel = new Protein(cursor.getInt(0)
                    , cursor.getString(1)
                    , cursor.getString(2)
                    , cursor.getString(3)
                    , cursor.getString(4)
            );
        }

        close();
        return stepModel;
    }

    public StepModel getSteps() {
        StepModel stepModel = new StepModel();
        openDataBase();

        Cursor cursor = mDataBase.query(Constants.TB_INFORMATION, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            stepModel = new StepModel(cursor.getInt(0)
                    , cursor.getString(1)
                    , cursor.getString(2)
                    , cursor.getString(3)
                    , cursor.getString(4)
            );
        }

        close();
        return stepModel;
    }


    public List<Water> getWater() {
        List<Water> list = new ArrayList<>();
        openDataBase();
        Cursor cursor = mDataBase.query("water", null, null, null, null, null, null);
        while (cursor.moveToNext()) {

            list.add(new Water(cursor.getInt(0)
                    , cursor.getString(1)));
        }

        close();
        return list;
    }

    public void deleteWater() {

        openDataBase();
        mDataBase.execSQL("DELETE FROM water");

        close();

    }

    public List<Healthy> getHealthy() {
        List<Healthy> list = new ArrayList<>();
        openDataBase();
        Cursor cursor = mDataBase.query(Constants.TB_HEALTHY, null, null, null, null, null, null);
        while (cursor.moveToNext()) {

            list.add(new Healthy(cursor.getString(0)
                            , cursor.getString(1)
                            , cursor.getString(2)
                            , cursor.getString(3)
                            , cursor.getString(4)
                            , cursor.getString(5)
                            , cursor.getString(6)
                            , cursor.getString(7)
                            , cursor.getString(8)
                            , cursor.getInt(9)
                            , cursor.getString(10)
                    )
            );
        }

        close();
        return list;
    }

    public Infomation getInformation() {
        Infomation infomation = new Infomation();
        openDataBase();

        Cursor cursor = mDataBase.query(Constants.TB_INFORMATION, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            infomation = new Infomation(cursor.getString(0)
                    , cursor.getString(1)
                    , cursor.getString(2)
                    , cursor.getString(3)
                    , cursor.getString(4)
                    , cursor.getString(5)
                    , cursor.getInt(6)
            );
        }

        close();
        return infomation;
    }

    public void updateHealthy(String key, String heart, int id) {
        openDataBase();
        ContentValues addRow = new ContentValues();
        addRow.put(key, heart);

        mDataBase.update(Constants.TB_HEALTHY, addRow, Constants.ID_HEALTHY + "=?", new String[]{id + ""});

    }

    public void updateProtein(String protein, String fat, String kcal, int id) {
        openDataBase();
        ContentValues addRow = new ContentValues();
        addRow.put(Constants.PROTEIN, protein);
        addRow.put(Constants.FAT, fat);
        addRow.put(Constants.KCAL, kcal);

        mDataBase.update(Constants.TB_PROTEIN, addRow, Constants.ID_HEALTHY + "=?", new String[]{id + ""});

    }

    public void updateSteps(StepModel key, int id) {
        openDataBase();
        ContentValues addRow = new ContentValues();
        addRow.put(Constants.STEPS, key.getSteps());
        addRow.put(Constants.CALORIES, key.getKcal());
        addRow.put(Constants.DISTANCE, key.getDistance());
        mDataBase.update(Constants.TB_STEPS, addRow, Constants.ID_HEALTHY + "=?", new String[]{id + ""});

    }


}

