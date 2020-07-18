package androidx.sqlite.wiki;

public abstract interface SupportSQLiteQuery
{
  public abstract void bindTo(SupportSQLiteProgram paramSupportSQLiteProgram);
  
  public abstract int getArgCount();
  
  public abstract String getSql();
}
