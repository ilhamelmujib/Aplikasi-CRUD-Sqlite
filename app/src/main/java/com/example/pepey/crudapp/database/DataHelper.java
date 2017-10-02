package com.example.pepey.crudapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ilhamelmujib on 7/29/17.
 */

public class DataHelper extends SQLiteOpenHelper{

    public static final String db = "db_crud";
    public static final String tb_user = "tb_user";


    public static final List<String> create = new ArrayList<String>(){{

        add("create table " + tb_user +
                " (nama TEXT, nis INTEGER PRIMARY KEY)");


    }};

    public static final List<String> table = new ArrayList<String>(){{
        add(tb_user);
    }};

    public DataHelper(Context context) {
        super(context, db, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        for (int i = 0; i < create.size(); i++){
            sqLiteDatabase.execSQL(create.get(i));
        }

        String sql = "INSERT INTO tb_user (nama, nis) VALUES " +
                "('Muhamad Ilham', '11505018')";
        sqLiteDatabase.execSQL(sql);

        String sql1 = "INSERT INTO tb_user (nama, nis) VALUES " +
                "('Agnesia', '11506747')";
        sqLiteDatabase.execSQL(sql1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        for (int j = 0; j < table.size(); j++){
            sqLiteDatabase.execSQL(table.get(j));
        }
    }
}
