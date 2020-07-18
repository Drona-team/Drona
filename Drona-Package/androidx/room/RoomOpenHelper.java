package androidx.room;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.room.migration.Migration;
import androidx.sqlite.wiki.SimpleSQLiteQuery;
import androidx.sqlite.wiki.SupportSQLiteDatabase;
import androidx.sqlite.wiki.SupportSQLiteOpenHelper.Callback;
import java.util.Iterator;
import java.util.List;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public class RoomOpenHelper
  extends SupportSQLiteOpenHelper.Callback
{
  @Nullable
  private DatabaseConfiguration mConfiguration;
  @NonNull
  private final Delegate mDelegate;
  @NonNull
  private final String mIdentityHash;
  @NonNull
  private final String mLegacyHash;
  
  public RoomOpenHelper(DatabaseConfiguration paramDatabaseConfiguration, Delegate paramDelegate, String paramString)
  {
    this(paramDatabaseConfiguration, paramDelegate, "", paramString);
  }
  
  public RoomOpenHelper(DatabaseConfiguration paramDatabaseConfiguration, Delegate paramDelegate, String paramString1, String paramString2)
  {
    super(version);
    mConfiguration = paramDatabaseConfiguration;
    mDelegate = paramDelegate;
    mIdentityHash = paramString1;
    mLegacyHash = paramString2;
  }
  
  private void checkIdentity(SupportSQLiteDatabase paramSupportSQLiteDatabase)
  {
    boolean bool = hasRoomMasterTable(paramSupportSQLiteDatabase);
    Object localObject1 = null;
    Object localObject2 = null;
    if (bool)
    {
      localObject1 = paramSupportSQLiteDatabase.query(new SimpleSQLiteQuery("SELECT identity_hash FROM room_master_table WHERE id = 42 LIMIT 1"));
      try
      {
        bool = ((Cursor)localObject1).moveToFirst();
        paramSupportSQLiteDatabase = localObject2;
        if (bool) {
          paramSupportSQLiteDatabase = ((Cursor)localObject1).getString(0);
        }
        ((Cursor)localObject1).close();
        localObject1 = paramSupportSQLiteDatabase;
      }
      catch (Throwable paramSupportSQLiteDatabase)
      {
        ((Cursor)localObject1).close();
        throw paramSupportSQLiteDatabase;
      }
    }
    if (!mIdentityHash.equals(localObject1))
    {
      if (mLegacyHash.equals(localObject1)) {
        return;
      }
      throw new IllegalStateException("Room cannot verify the data integrity. Looks like you've changed schema but forgot to update the version number. You can simply fix this by increasing the version number.");
    }
  }
  
  private void createMasterTableIfNotExists(SupportSQLiteDatabase paramSupportSQLiteDatabase)
  {
    paramSupportSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
  }
  
  private static boolean hasRoomMasterTable(SupportSQLiteDatabase paramSupportSQLiteDatabase)
  {
    paramSupportSQLiteDatabase = paramSupportSQLiteDatabase.query("SELECT 1 FROM sqlite_master WHERE type = 'table' AND name='room_master_table'");
    try
    {
      boolean bool3 = paramSupportSQLiteDatabase.moveToFirst();
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (bool3)
      {
        int i = paramSupportSQLiteDatabase.getInt(0);
        bool1 = bool2;
        if (i != 0) {
          bool1 = true;
        }
      }
      paramSupportSQLiteDatabase.close();
      return bool1;
    }
    catch (Throwable localThrowable)
    {
      paramSupportSQLiteDatabase.close();
      throw localThrowable;
    }
  }
  
  private void updateIdentity(SupportSQLiteDatabase paramSupportSQLiteDatabase)
  {
    createMasterTableIfNotExists(paramSupportSQLiteDatabase);
    paramSupportSQLiteDatabase.execSQL(RoomMasterTable.createInsertQuery(mIdentityHash));
  }
  
  public void onConfigure(SupportSQLiteDatabase paramSupportSQLiteDatabase)
  {
    super.onConfigure(paramSupportSQLiteDatabase);
  }
  
  public void onCreate(SupportSQLiteDatabase paramSupportSQLiteDatabase)
  {
    updateIdentity(paramSupportSQLiteDatabase);
    mDelegate.createAllTables(paramSupportSQLiteDatabase);
    mDelegate.onCreate(paramSupportSQLiteDatabase);
  }
  
  public void onDowngrade(SupportSQLiteDatabase paramSupportSQLiteDatabase, int paramInt1, int paramInt2)
  {
    onUpgrade(paramSupportSQLiteDatabase, paramInt1, paramInt2);
  }
  
  public void onOpen(SupportSQLiteDatabase paramSupportSQLiteDatabase)
  {
    super.onOpen(paramSupportSQLiteDatabase);
    checkIdentity(paramSupportSQLiteDatabase);
    mDelegate.onOpen(paramSupportSQLiteDatabase);
    mConfiguration = null;
  }
  
  public void onUpgrade(SupportSQLiteDatabase paramSupportSQLiteDatabase, int paramInt1, int paramInt2)
  {
    if (mConfiguration != null)
    {
      Object localObject = mConfiguration.migrationContainer.findMigrationPath(paramInt1, paramInt2);
      if (localObject != null)
      {
        mDelegate.onPreMigrate(paramSupportSQLiteDatabase);
        localObject = ((List)localObject).iterator();
        while (((Iterator)localObject).hasNext()) {
          ((Migration)((Iterator)localObject).next()).migrate(paramSupportSQLiteDatabase);
        }
        mDelegate.validateMigration(paramSupportSQLiteDatabase);
        mDelegate.onPostMigrate(paramSupportSQLiteDatabase);
        updateIdentity(paramSupportSQLiteDatabase);
        i = 1;
        break label100;
      }
    }
    int i = 0;
    label100:
    if (i == 0)
    {
      if ((mConfiguration != null) && (!mConfiguration.isMigrationRequired(paramInt1, paramInt2)))
      {
        mDelegate.dropAllTables(paramSupportSQLiteDatabase);
        mDelegate.createAllTables(paramSupportSQLiteDatabase);
        return;
      }
      paramSupportSQLiteDatabase = new StringBuilder();
      paramSupportSQLiteDatabase.append("A migration from ");
      paramSupportSQLiteDatabase.append(paramInt1);
      paramSupportSQLiteDatabase.append(" to ");
      paramSupportSQLiteDatabase.append(paramInt2);
      paramSupportSQLiteDatabase.append(" was required but not found. Please provide the necessary Migration path via RoomDatabase.Builder.addMigration(Migration ...) or allow for destructive migrations via one of the RoomDatabase.Builder.fallbackToDestructiveMigration* methods.");
      throw new IllegalStateException(paramSupportSQLiteDatabase.toString());
    }
  }
  
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
  public static abstract class Delegate
  {
    public final int version;
    
    public Delegate(int paramInt)
    {
      version = paramInt;
    }
    
    protected abstract void createAllTables(SupportSQLiteDatabase paramSupportSQLiteDatabase);
    
    protected abstract void dropAllTables(SupportSQLiteDatabase paramSupportSQLiteDatabase);
    
    protected abstract void onCreate(SupportSQLiteDatabase paramSupportSQLiteDatabase);
    
    protected abstract void onOpen(SupportSQLiteDatabase paramSupportSQLiteDatabase);
    
    protected void onPostMigrate(SupportSQLiteDatabase paramSupportSQLiteDatabase) {}
    
    protected void onPreMigrate(SupportSQLiteDatabase paramSupportSQLiteDatabase) {}
    
    protected abstract void validateMigration(SupportSQLiteDatabase paramSupportSQLiteDatabase);
  }
}
