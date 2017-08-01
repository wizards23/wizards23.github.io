package com.sql;

import java.io.File;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class SQL{
	private static SQLiteDatabase  db;
	
	
	public static SQLiteDatabase getDB(){
		String path = Environment.getExternalStorageDirectory()+"/PDA";
		if(db==null){
		   db = SQLiteDatabase.openOrCreateDatabase(path+"/pda.db", null);	
		}
		return db;
	}
	
	
	//创建数据库
	public static void creageDatabase(){
		String path = Environment.getExternalStorageDirectory()+"/PDA";
		File file = new File(path);
		if(!file.exists()){
		  file.mkdirs();
		}
		SQLiteDatabase.openOrCreateDatabase(path+"/pda.db", null);
		//如果表不存在就创建表
		createTable();
		
		try {
			Cursor cursor = select("SELECT len1,len2 FROM load2 WHERE id=1");
			
			if(!cursor.moveToNext()){
			insert("insert into load2 values (null,'0','0')");
			System.out.println("插入数据库");
			}			
		} catch (Exception e) {
		  System.out.println(e.getMessage());
		}
	}
	

	//创建表
	public static void createTable(){
		//getDB().execSQL("create table if not exists load(id INTEGER PRIMARY KEY AUTOINCREMENT, len VARCHAR(20))");
		
		try {
			getDB().execSQL("create table if not exists load2(id INTEGER PRIMARY KEY AUTOINCREMENT, len1 VARCHAR(20),len2 VARCHAR(20))");	
		} catch (Exception e) {
			e.getMessage();
		}
		
		
	
	}
	
	/**
	 * 插入  
	 * "insert into download values (null,'102456','4552342')"
	 */
	public static void insert(String sql){
		getDB().execSQL(sql);
	  
	}
	
	/**
	 *查询
	 * @return
	 */
	public static Cursor select(String sql){
	   Cursor cursor = getDB().rawQuery(sql, null);
	   return cursor;
	}
	

	/**
	 * 修改
	 * @param cv  //要更改的字段及内容,如果 cv.put("username","张三")
	 * @param whereClause  修改条件   如user=?
	 * @param whereArgs    修改条件参数 {"张三"}
	 */
	public static int update(ContentValues cv,String whereClause,String[] whereArgs){		
		return getDB().update("load2", cv, whereClause, whereArgs);
		
	}
	
	
	/**
	 * 删除
	 * @param whereClause 删除条件   如user=?
	 * @param whereArgs   删除条件参数 {"张三"}
	 */
	public static int delete(String whereClause,String[] whereArgs){
		return db.delete("load2", whereClause, whereArgs);
	}
	
	
	/**
	 * 
	 */
	public static void exupdate(int value,int index,int id){	
	 // db.execSQL("update download set size1='00000',size2='1111111' where _id=1");
		String sql="update load2 set len"+index+"="+value+" where id="+id;
		getDB().execSQL(sql);
	    
	}
	
	
	
	//关闭数据库
	public void close(){
		getDB().close();
	}
	
}
