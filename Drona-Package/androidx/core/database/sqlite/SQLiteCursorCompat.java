package androidx.core.database.sqlite;

import android.database.sqlite.SQLiteCursor;
import android.os.Build.VERSION;

public final class SQLiteCursorCompat
{
  private SQLiteCursorCompat() {}
  
  public static void setFillWindowForwardOnly(SQLiteCursor paramSQLiteCursor, boolean paramBoolean)
  {
    if (Build.VERSION.SDK_INT >= 28) {
      paramSQLiteCursor.setFillWindowForwardOnly(paramBoolean);
    }
  }
}
