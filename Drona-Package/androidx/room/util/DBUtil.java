package androidx.room.util;

import android.database.AbstractCursor;
import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.os.Build.VERSION;
import androidx.annotation.RestrictTo;
import androidx.room.RoomDatabase;
import androidx.sqlite.wiki.SupportSQLiteDatabase;
import androidx.sqlite.wiki.SupportSQLiteQuery;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public class DBUtil
{
  private DBUtil() {}
  
  public static void dropFtsSyncTriggers(SupportSQLiteDatabase paramSupportSQLiteDatabase)
  {
    Object localObject2 = new ArrayList();
    Object localObject1 = paramSupportSQLiteDatabase.query("SELECT name FROM sqlite_master WHERE type = 'trigger'");
    try
    {
      for (;;)
      {
        boolean bool = ((Cursor)localObject1).moveToNext();
        if (!bool) {
          break;
        }
        ((List)localObject2).add(((Cursor)localObject1).getString(0));
      }
      ((Cursor)localObject1).close();
      localObject1 = ((List)localObject2).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (String)((Iterator)localObject1).next();
        if (((String)localObject2).startsWith("room_fts_content_sync_"))
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("DROP TRIGGER IF EXISTS ");
          localStringBuilder.append((String)localObject2);
          paramSupportSQLiteDatabase.execSQL(localStringBuilder.toString());
        }
      }
      return;
    }
    catch (Throwable paramSupportSQLiteDatabase)
    {
      ((Cursor)localObject1).close();
      throw paramSupportSQLiteDatabase;
    }
  }
  
  public static Cursor query(RoomDatabase paramRoomDatabase, SupportSQLiteQuery paramSupportSQLiteQuery, boolean paramBoolean)
  {
    paramSupportSQLiteQuery = paramRoomDatabase.query(paramSupportSQLiteQuery);
    paramRoomDatabase = paramSupportSQLiteQuery;
    if (paramBoolean)
    {
      paramRoomDatabase = paramSupportSQLiteQuery;
      if ((paramSupportSQLiteQuery instanceof AbstractWindowedCursor))
      {
        AbstractWindowedCursor localAbstractWindowedCursor = (AbstractWindowedCursor)paramSupportSQLiteQuery;
        int j = localAbstractWindowedCursor.getCount();
        int i;
        if (localAbstractWindowedCursor.hasWindow()) {
          i = localAbstractWindowedCursor.getWindow().getNumRows();
        } else {
          i = j;
        }
        if (Build.VERSION.SDK_INT >= 23)
        {
          paramRoomDatabase = paramSupportSQLiteQuery;
          if (i >= j) {}
        }
        else
        {
          paramRoomDatabase = CursorUtil.copyAndClose(localAbstractWindowedCursor);
        }
      }
    }
    return paramRoomDatabase;
  }
}
