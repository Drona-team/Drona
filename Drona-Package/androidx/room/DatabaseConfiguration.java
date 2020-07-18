package androidx.room;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.sqlite.wiki.SupportSQLiteOpenHelper.Factory;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

public class DatabaseConfiguration
{
  public final boolean allowDestructiveMigrationOnDowngrade;
  public final boolean allowMainThreadQueries;
  @Nullable
  public final List<RoomDatabase.Callback> callbacks;
  @NonNull
  public final Context context;
  public final RoomDatabase.JournalMode journalMode;
  private final Set<Integer> mMigrationNotRequiredFrom;
  @NonNull
  public final RoomDatabase.MigrationContainer migrationContainer;
  public final boolean multiInstanceInvalidation;
  @Nullable
  public final String name;
  @NonNull
  public final Executor queryExecutor;
  public final boolean requireMigration;
  @NonNull
  public final SupportSQLiteOpenHelper.Factory sqliteOpenHelperFactory;
  @NonNull
  public final Executor transactionExecutor;
  
  public DatabaseConfiguration(Context paramContext, String paramString, SupportSQLiteOpenHelper.Factory paramFactory, RoomDatabase.MigrationContainer paramMigrationContainer, List paramList, boolean paramBoolean1, RoomDatabase.JournalMode paramJournalMode, Executor paramExecutor1, Executor paramExecutor2, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, Set paramSet)
  {
    sqliteOpenHelperFactory = paramFactory;
    context = paramContext;
    name = paramString;
    migrationContainer = paramMigrationContainer;
    callbacks = paramList;
    allowMainThreadQueries = paramBoolean1;
    journalMode = paramJournalMode;
    queryExecutor = paramExecutor1;
    transactionExecutor = paramExecutor2;
    multiInstanceInvalidation = paramBoolean2;
    requireMigration = paramBoolean3;
    allowDestructiveMigrationOnDowngrade = paramBoolean4;
    mMigrationNotRequiredFrom = paramSet;
  }
  
  public DatabaseConfiguration(Context paramContext, String paramString, SupportSQLiteOpenHelper.Factory paramFactory, RoomDatabase.MigrationContainer paramMigrationContainer, List paramList, boolean paramBoolean1, RoomDatabase.JournalMode paramJournalMode, Executor paramExecutor, boolean paramBoolean2, Set paramSet)
  {
    this(paramContext, paramString, paramFactory, paramMigrationContainer, paramList, paramBoolean1, paramJournalMode, paramExecutor, paramExecutor, false, paramBoolean2, false, paramSet);
  }
  
  public boolean isMigrationRequired(int paramInt1, int paramInt2)
  {
    if (paramInt1 > paramInt2) {
      paramInt2 = 1;
    } else {
      paramInt2 = 0;
    }
    if ((paramInt2 != 0) && (allowDestructiveMigrationOnDowngrade)) {
      return false;
    }
    if (requireMigration)
    {
      if (mMigrationNotRequiredFrom == null) {
        break label59;
      }
      if (!mMigrationNotRequiredFrom.contains(Integer.valueOf(paramInt1))) {
        return true;
      }
    }
    return false;
    label59:
    return true;
  }
  
  public boolean isMigrationRequiredFrom(int paramInt)
  {
    return isMigrationRequired(paramInt, paramInt + 1);
  }
}
