package com.vunke.chinaunicom.advertisement.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GroupStrategySql extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "group_strategy.db";
	private static int DATABASE_VERSION = 1;
	private String CreateTable = "create table group_strategy (_id integer not null primary key autoincrement ,create_time varchar,userName varchar,epg_code varchar,epg_package varchar,area_id varchar,group_id varhcar,apk_path varchar,epg_domain varchar)";
	private String DropTable =  "drop table if exists group_strategy";;
	public GroupStrategySql(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CreateTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DropTable);
		db.execSQL(CreateTable);
	}

}