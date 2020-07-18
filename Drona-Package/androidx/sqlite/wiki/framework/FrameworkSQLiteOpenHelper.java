package androidx.sqlite.wiki.framework;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.sqlite.wiki.SupportSQLiteDatabase;
import androidx.sqlite.wiki.SupportSQLiteOpenHelper;
import androidx.sqlite.wiki.SupportSQLiteOpenHelper.Callback;

class FrameworkSQLiteOpenHelper
  implements SupportSQLiteOpenHelper
{
  private final OpenHelper mDelegate = createDelegate(paramContext, paramString, paramCallback);
  
  FrameworkSQLiteOpenHelper(Context paramContext, String paramString, SupportSQLiteOpenHelper.Callback paramCallback) {}
  
  private OpenHelper createDelegate(Context paramContext, String paramString, SupportSQLiteOpenHelper.Callback paramCallback)
  {
    return new OpenHelper(paramContext, paramString, new FrameworkSQLiteDatabase[1], paramCallback);
  }
  
  public void close()
  {
    mDelegate.close();
  }
  
  public String getDatabaseName()
  {
    return mDelegate.getDatabaseName();
  }
  
  public SupportSQLiteDatabase getReadableDatabase()
  {
    return mDelegate.getReadableSupportDatabase();
  }
  
  public SupportSQLiteDatabase getWritableDatabase()
  {
    return mDelegate.getWritableSupportDatabase();
  }
  
  public void setWriteAheadLoggingEnabled(boolean paramBoolean)
  {
    mDelegate.setWriteAheadLoggingEnabled(paramBoolean);
  }
  
  class OpenHelper
    extends SQLiteOpenHelper
  {
    final SupportSQLiteOpenHelper.Callback mCallback;
    final FrameworkSQLiteDatabase[] mDbRef;
    private boolean mMigrated;
    
    OpenHelper(String paramString, FrameworkSQLiteDatabase[] paramArrayOfFrameworkSQLiteDatabase, SupportSQLiteOpenHelper.Callback paramCallback)
    {
      super(paramString, null, version, new FrameworkSQLiteOpenHelper.OpenHelper.1(paramCallback, paramArrayOfFrameworkSQLiteDatabase));
      mCallback = paramCallback;
      mDbRef = paramArrayOfFrameworkSQLiteDatabase;
    }
    
    static FrameworkSQLiteDatabase getWrappedDb(FrameworkSQLiteDatabase[] paramArrayOfFrameworkSQLiteDatabase, SQLiteDatabase paramSQLiteDatabase)
    {
      FrameworkSQLiteDatabase localFrameworkSQLiteDatabase = paramArrayOfFrameworkSQLiteDatabase[0];
      if ((localFrameworkSQLiteDatabase == null) || (!localFrameworkSQLiteDatabase.isDelegate(paramSQLiteDatabase))) {
        paramArrayOfFrameworkSQLiteDatabase[0] = new FrameworkSQLiteDatabase(paramSQLiteDatabase);
      }
      return paramArrayOfFrameworkSQLiteDatabase[0];
    }
    
    public void close()
    {
      try
      {
        super.close();
        mDbRef[0] = null;
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    SupportSQLiteDatabase getReadableSupportDatabase()
    {
      try
      {
        mMigrated = false;
        Object localObject = super.getReadableDatabase();
        if (mMigrated)
        {
          close();
          localObject = getReadableSupportDatabase();
          return localObject;
        }
        localObject = getWrappedDb((SQLiteDatabase)localObject);
        return localObject;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    FrameworkSQLiteDatabase getWrappedDb(SQLiteDatabase paramSQLiteDatabase)
    {
      return getWrappedDb(mDbRef, paramSQLiteDatabase);
    }
    
    SupportSQLiteDatabase getWritableSupportDatabase()
    {
      try
      {
        mMigrated = false;
        Object localObject = super.getWritableDatabase();
        if (mMigrated)
        {
          close();
          localObject = getWritableSupportDatabase();
          return localObject;
        }
        localObject = getWrappedDb((SQLiteDatabase)localObject);
        return localObject;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    public void onConfigure(SQLiteDatabase paramSQLiteDatabase)
    {
      mCallback.onConfigure(getWrappedDb(paramSQLiteDatabase));
    }
    
    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
      mCallback.onCreate(getWrappedDb(paramSQLiteDatabase));
    }
    
    public void onDowngrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
    {
      mMigrated = true;
      mCallback.onDowngrade(getWrappedDb(paramSQLiteDatabase), paramInt1, paramInt2);
    }
    
    public void onOpen(SQLiteDatabase paramSQLiteDatabase)
    {
      if (!mMigrated) {
        mCallback.onOpen(getWrappedDb(paramSQLiteDatabase));
      }
    }
    
    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
    {
      mMigrated = true;
      mCallback.onUpgrade(getWrappedDb(paramSQLiteDatabase), paramInt1, paramInt2);
    }
  }
}
