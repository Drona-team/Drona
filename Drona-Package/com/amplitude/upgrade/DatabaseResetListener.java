package com.amplitude.upgrade;

import android.database.sqlite.SQLiteDatabase;

public abstract interface DatabaseResetListener
{
  public abstract void onDatabaseReset(SQLiteDatabase paramSQLiteDatabase);
}
