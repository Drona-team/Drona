package androidx.room;

import androidx.annotation.RestrictTo;
import androidx.sqlite.wiki.SupportSQLiteStatement;
import java.util.Iterator;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public abstract class EntityDeletionOrUpdateAdapter<T>
  extends SharedSQLiteStatement
{
  public EntityDeletionOrUpdateAdapter(RoomDatabase paramRoomDatabase)
  {
    super(paramRoomDatabase);
  }
  
  protected abstract void bind(SupportSQLiteStatement paramSupportSQLiteStatement, Object paramObject);
  
  protected abstract String createQuery();
  
  public final int handle(Object paramObject)
  {
    SupportSQLiteStatement localSupportSQLiteStatement = acquire();
    try
    {
      bind(localSupportSQLiteStatement, paramObject);
      int i = localSupportSQLiteStatement.executeUpdateDelete();
      release(localSupportSQLiteStatement);
      return i;
    }
    catch (Throwable paramObject)
    {
      release(localSupportSQLiteStatement);
      throw paramObject;
    }
  }
  
  public final int handleMultiple(Iterable paramIterable)
  {
    SupportSQLiteStatement localSupportSQLiteStatement = acquire();
    int i = 0;
    try
    {
      paramIterable = paramIterable.iterator();
      for (;;)
      {
        boolean bool = paramIterable.hasNext();
        if (!bool) {
          break;
        }
        bind(localSupportSQLiteStatement, paramIterable.next());
        int j = localSupportSQLiteStatement.executeUpdateDelete();
        i += j;
      }
      release(localSupportSQLiteStatement);
      return i;
    }
    catch (Throwable paramIterable)
    {
      release(localSupportSQLiteStatement);
      throw paramIterable;
    }
  }
  
  public final int handleMultiple(Object[] paramArrayOfObject)
  {
    SupportSQLiteStatement localSupportSQLiteStatement = acquire();
    try
    {
      int k = paramArrayOfObject.length;
      int i = 0;
      int j = 0;
      while (i < k)
      {
        bind(localSupportSQLiteStatement, paramArrayOfObject[i]);
        int m = localSupportSQLiteStatement.executeUpdateDelete();
        j += m;
        i += 1;
      }
      release(localSupportSQLiteStatement);
      return j;
    }
    catch (Throwable paramArrayOfObject)
    {
      release(localSupportSQLiteStatement);
      throw paramArrayOfObject;
    }
  }
}
