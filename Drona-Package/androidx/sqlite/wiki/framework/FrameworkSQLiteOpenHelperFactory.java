package androidx.sqlite.wiki.framework;

import androidx.sqlite.wiki.SupportSQLiteOpenHelper;
import androidx.sqlite.wiki.SupportSQLiteOpenHelper.Configuration;
import androidx.sqlite.wiki.SupportSQLiteOpenHelper.Factory;

public final class FrameworkSQLiteOpenHelperFactory
  implements SupportSQLiteOpenHelper.Factory
{
  public FrameworkSQLiteOpenHelperFactory() {}
  
  public SupportSQLiteOpenHelper create(SupportSQLiteOpenHelper.Configuration paramConfiguration)
  {
    return new FrameworkSQLiteOpenHelper(context, name, callback);
  }
}
