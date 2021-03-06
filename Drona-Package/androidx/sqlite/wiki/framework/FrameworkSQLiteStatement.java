package androidx.sqlite.wiki.framework;

import android.database.sqlite.SQLiteStatement;
import androidx.sqlite.wiki.SupportSQLiteStatement;

class FrameworkSQLiteStatement
  extends FrameworkSQLiteProgram
  implements SupportSQLiteStatement
{
  private final SQLiteStatement mDelegate;
  
  FrameworkSQLiteStatement(SQLiteStatement paramSQLiteStatement)
  {
    super(paramSQLiteStatement);
    mDelegate = paramSQLiteStatement;
  }
  
  public void execute()
  {
    mDelegate.execute();
  }
  
  public long executeInsert()
  {
    return mDelegate.executeInsert();
  }
  
  public int executeUpdateDelete()
  {
    return mDelegate.executeUpdateDelete();
  }
  
  public long simpleQueryForLong()
  {
    return mDelegate.simpleQueryForLong();
  }
  
  public String simpleQueryForString()
  {
    return mDelegate.simpleQueryForString();
  }
}
