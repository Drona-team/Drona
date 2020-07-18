package androidx.room.migration;

import androidx.sqlite.wiki.SupportSQLiteDatabase;

public abstract class Migration
{
  public final int endVersion;
  public final int startVersion;
  
  public Migration(int paramInt1, int paramInt2)
  {
    startVersion = paramInt1;
    endVersion = paramInt2;
  }
  
  public abstract void migrate(SupportSQLiteDatabase paramSupportSQLiteDatabase);
}
