package androidx.sqlite.wiki;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build.VERSION;
import android.util.Log;
import android.util.Pair;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public abstract interface SupportSQLiteOpenHelper
{
  public abstract void close();
  
  public abstract String getDatabaseName();
  
  public abstract SupportSQLiteDatabase getReadableDatabase();
  
  public abstract SupportSQLiteDatabase getWritableDatabase();
  
  public abstract void setWriteAheadLoggingEnabled(boolean paramBoolean);
  
  public abstract class Callback
  {
    private static final String PAGE_KEY = "SupportSQLite";
    public final int version;
    
    public Callback()
    {
      version = this$1;
    }
    
    private void deleteDatabaseFile(String paramString)
    {
      if (!paramString.equalsIgnoreCase(":memory:"))
      {
        if (paramString.trim().length() == 0) {
          return;
        }
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("deleting the database file: ");
        localStringBuilder.append(paramString);
        Log.w("SupportSQLite", localStringBuilder.toString());
        if (Build.VERSION.SDK_INT >= 16) {}
        try
        {
          SQLiteDatabase.deleteDatabase(new File(paramString));
          return;
        }
        catch (Exception paramString)
        {
          boolean bool;
          Log.w("SupportSQLite", "delete failed: ", paramString);
        }
        try
        {
          bool = new File(paramString).delete();
          if (!bool)
          {
            localStringBuilder = new StringBuilder();
            localStringBuilder.append("Could not delete the database file ");
            localStringBuilder.append(paramString);
            Log.e("SupportSQLite", localStringBuilder.toString());
            return;
          }
        }
        catch (Exception paramString)
        {
          Log.e("SupportSQLite", "error while deleting corrupted database file", paramString);
          return;
        }
      }
    }
    
    public void onConfigure(SupportSQLiteDatabase paramSupportSQLiteDatabase) {}
    
    public void onCorruption(SupportSQLiteDatabase paramSupportSQLiteDatabase)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Corruption reported by sqlite on database: ");
      ((StringBuilder)localObject).append(paramSupportSQLiteDatabase.getPath());
      Log.e("SupportSQLite", ((StringBuilder)localObject).toString());
      if (!paramSupportSQLiteDatabase.isOpen())
      {
        deleteDatabaseFile(paramSupportSQLiteDatabase.getPath());
        return;
      }
      localObject = null;
      Throwable localThrowable2 = null;
      try
      {
        try
        {
          List localList = paramSupportSQLiteDatabase.getAttachedDbs();
          localObject = localList;
        }
        catch (Throwable localThrowable1)
        {
          break label89;
        }
      }
      catch (SQLiteException localSQLiteException)
      {
        for (;;) {}
      }
      localThrowable2 = localThrowable1;
      try
      {
        paramSupportSQLiteDatabase.close();
      }
      catch (IOException localIOException)
      {
        label89:
        for (;;) {}
      }
      if (localThrowable2 != null)
      {
        paramSupportSQLiteDatabase = localThrowable2.iterator();
        while (paramSupportSQLiteDatabase.hasNext()) {
          deleteDatabaseFile((String)nextsecond);
        }
      }
      deleteDatabaseFile(paramSupportSQLiteDatabase.getPath());
      throw localThrowable1;
      if (localThrowable1 != null)
      {
        paramSupportSQLiteDatabase = localThrowable1.iterator();
        while (paramSupportSQLiteDatabase.hasNext()) {
          deleteDatabaseFile((String)nextsecond);
        }
      }
      deleteDatabaseFile(paramSupportSQLiteDatabase.getPath());
      return;
    }
    
    public abstract void onCreate(SupportSQLiteDatabase paramSupportSQLiteDatabase);
    
    public void onDowngrade(SupportSQLiteDatabase paramSupportSQLiteDatabase, int paramInt1, int paramInt2)
    {
      paramSupportSQLiteDatabase = new StringBuilder();
      paramSupportSQLiteDatabase.append("Can't downgrade database from version ");
      paramSupportSQLiteDatabase.append(paramInt1);
      paramSupportSQLiteDatabase.append(" to ");
      paramSupportSQLiteDatabase.append(paramInt2);
      throw new SQLiteException(paramSupportSQLiteDatabase.toString());
    }
    
    public void onOpen(SupportSQLiteDatabase paramSupportSQLiteDatabase) {}
    
    public abstract void onUpgrade(SupportSQLiteDatabase paramSupportSQLiteDatabase, int paramInt1, int paramInt2);
  }
  
  public class Configuration
  {
    @NonNull
    public final SupportSQLiteOpenHelper.Callback callback;
    @Nullable
    public final String name;
    
    Configuration(String paramString, SupportSQLiteOpenHelper.Callback paramCallback)
    {
      name = paramString;
      callback = paramCallback;
    }
    
    public static Builder builder(Context paramContext)
    {
      return new Builder();
    }
    
    public class Builder
    {
      SupportSQLiteOpenHelper.Callback mCallback;
      String mName;
      
      Builder() {}
      
      public SupportSQLiteOpenHelper.Configuration build()
      {
        if (mCallback != null)
        {
          if (SupportSQLiteOpenHelper.Configuration.this != null) {
            return new SupportSQLiteOpenHelper.Configuration(SupportSQLiteOpenHelper.Configuration.this, mName, mCallback);
          }
          throw new IllegalArgumentException("Must set a non-null context to create the configuration.");
        }
        throw new IllegalArgumentException("Must set a callback to create the configuration.");
      }
      
      public Builder callback(SupportSQLiteOpenHelper.Callback paramCallback)
      {
        mCallback = paramCallback;
        return this;
      }
      
      public Builder name(String paramString)
      {
        mName = paramString;
        return this;
      }
    }
  }
  
  public abstract interface Factory
  {
    public abstract SupportSQLiteOpenHelper create(SupportSQLiteOpenHelper.Configuration paramConfiguration);
  }
}
