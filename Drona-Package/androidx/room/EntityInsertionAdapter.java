package androidx.room;

import androidx.annotation.RestrictTo;
import androidx.sqlite.wiki.SupportSQLiteStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public abstract class EntityInsertionAdapter<T>
  extends SharedSQLiteStatement
{
  public EntityInsertionAdapter(RoomDatabase paramRoomDatabase)
  {
    super(paramRoomDatabase);
  }
  
  protected abstract void bind(SupportSQLiteStatement paramSupportSQLiteStatement, Object paramObject);
  
  public final void insert(Iterable paramIterable)
  {
    SupportSQLiteStatement localSupportSQLiteStatement = acquire();
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
        localSupportSQLiteStatement.executeInsert();
      }
      release(localSupportSQLiteStatement);
      return;
    }
    catch (Throwable paramIterable)
    {
      release(localSupportSQLiteStatement);
      throw paramIterable;
    }
  }
  
  public final void insert(Object paramObject)
  {
    SupportSQLiteStatement localSupportSQLiteStatement = acquire();
    try
    {
      bind(localSupportSQLiteStatement, paramObject);
      localSupportSQLiteStatement.executeInsert();
      release(localSupportSQLiteStatement);
      return;
    }
    catch (Throwable paramObject)
    {
      release(localSupportSQLiteStatement);
      throw paramObject;
    }
  }
  
  public final void insert(Object[] paramArrayOfObject)
  {
    SupportSQLiteStatement localSupportSQLiteStatement = acquire();
    try
    {
      int j = paramArrayOfObject.length;
      int i = 0;
      while (i < j)
      {
        bind(localSupportSQLiteStatement, paramArrayOfObject[i]);
        localSupportSQLiteStatement.executeInsert();
        i += 1;
      }
      release(localSupportSQLiteStatement);
      return;
    }
    catch (Throwable paramArrayOfObject)
    {
      release(localSupportSQLiteStatement);
      throw paramArrayOfObject;
    }
  }
  
  public final long insertAndReturnId(Object paramObject)
  {
    SupportSQLiteStatement localSupportSQLiteStatement = acquire();
    try
    {
      bind(localSupportSQLiteStatement, paramObject);
      long l = localSupportSQLiteStatement.executeInsert();
      release(localSupportSQLiteStatement);
      return l;
    }
    catch (Throwable paramObject)
    {
      release(localSupportSQLiteStatement);
      throw paramObject;
    }
  }
  
  public final long[] insertAndReturnIdsArray(Collection paramCollection)
  {
    SupportSQLiteStatement localSupportSQLiteStatement = acquire();
    try
    {
      long[] arrayOfLong = new long[paramCollection.size()];
      int i = 0;
      paramCollection = paramCollection.iterator();
      for (;;)
      {
        boolean bool = paramCollection.hasNext();
        if (!bool) {
          break;
        }
        bind(localSupportSQLiteStatement, paramCollection.next());
        arrayOfLong[i] = localSupportSQLiteStatement.executeInsert();
        i += 1;
      }
      release(localSupportSQLiteStatement);
      return arrayOfLong;
    }
    catch (Throwable paramCollection)
    {
      release(localSupportSQLiteStatement);
      throw paramCollection;
    }
  }
  
  public final long[] insertAndReturnIdsArray(Object[] paramArrayOfObject)
  {
    SupportSQLiteStatement localSupportSQLiteStatement = acquire();
    try
    {
      long[] arrayOfLong = new long[paramArrayOfObject.length];
      int k = paramArrayOfObject.length;
      int i = 0;
      int j = 0;
      while (i < k)
      {
        bind(localSupportSQLiteStatement, paramArrayOfObject[i]);
        arrayOfLong[j] = localSupportSQLiteStatement.executeInsert();
        j += 1;
        i += 1;
      }
      release(localSupportSQLiteStatement);
      return arrayOfLong;
    }
    catch (Throwable paramArrayOfObject)
    {
      release(localSupportSQLiteStatement);
      throw paramArrayOfObject;
    }
  }
  
  public final Long[] insertAndReturnIdsArrayBox(Collection paramCollection)
  {
    SupportSQLiteStatement localSupportSQLiteStatement = acquire();
    try
    {
      Long[] arrayOfLong = new Long[paramCollection.size()];
      int i = 0;
      paramCollection = paramCollection.iterator();
      for (;;)
      {
        boolean bool = paramCollection.hasNext();
        if (!bool) {
          break;
        }
        bind(localSupportSQLiteStatement, paramCollection.next());
        arrayOfLong[i] = Long.valueOf(localSupportSQLiteStatement.executeInsert());
        i += 1;
      }
      release(localSupportSQLiteStatement);
      return arrayOfLong;
    }
    catch (Throwable paramCollection)
    {
      release(localSupportSQLiteStatement);
      throw paramCollection;
    }
  }
  
  public final Long[] insertAndReturnIdsArrayBox(Object[] paramArrayOfObject)
  {
    SupportSQLiteStatement localSupportSQLiteStatement = acquire();
    try
    {
      Long[] arrayOfLong = new Long[paramArrayOfObject.length];
      int k = paramArrayOfObject.length;
      int i = 0;
      int j = 0;
      while (i < k)
      {
        bind(localSupportSQLiteStatement, paramArrayOfObject[i]);
        arrayOfLong[j] = Long.valueOf(localSupportSQLiteStatement.executeInsert());
        j += 1;
        i += 1;
      }
      release(localSupportSQLiteStatement);
      return arrayOfLong;
    }
    catch (Throwable paramArrayOfObject)
    {
      release(localSupportSQLiteStatement);
      throw paramArrayOfObject;
    }
  }
  
  public final List insertAndReturnIdsList(Collection paramCollection)
  {
    SupportSQLiteStatement localSupportSQLiteStatement = acquire();
    try
    {
      ArrayList localArrayList = new ArrayList(paramCollection.size());
      int i = 0;
      paramCollection = paramCollection.iterator();
      for (;;)
      {
        boolean bool = paramCollection.hasNext();
        if (!bool) {
          break;
        }
        bind(localSupportSQLiteStatement, paramCollection.next());
        localArrayList.add(i, Long.valueOf(localSupportSQLiteStatement.executeInsert()));
        i += 1;
      }
      release(localSupportSQLiteStatement);
      return localArrayList;
    }
    catch (Throwable paramCollection)
    {
      release(localSupportSQLiteStatement);
      throw paramCollection;
    }
  }
  
  public final List insertAndReturnIdsList(Object[] paramArrayOfObject)
  {
    SupportSQLiteStatement localSupportSQLiteStatement = acquire();
    try
    {
      ArrayList localArrayList = new ArrayList(paramArrayOfObject.length);
      int k = paramArrayOfObject.length;
      int i = 0;
      int j = 0;
      while (i < k)
      {
        bind(localSupportSQLiteStatement, paramArrayOfObject[i]);
        localArrayList.add(j, Long.valueOf(localSupportSQLiteStatement.executeInsert()));
        j += 1;
        i += 1;
      }
      release(localSupportSQLiteStatement);
      return localArrayList;
    }
    catch (Throwable paramArrayOfObject)
    {
      release(localSupportSQLiteStatement);
      throw paramArrayOfObject;
    }
  }
}
