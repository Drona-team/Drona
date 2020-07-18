package androidx.room.util;

import android.database.Cursor;
import android.database.MatrixCursor;
import androidx.annotation.RestrictTo;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public class CursorUtil
{
  private CursorUtil() {}
  
  public static Cursor copyAndClose(Cursor paramCursor)
  {
    try
    {
      MatrixCursor localMatrixCursor = new MatrixCursor(paramCursor.getColumnNames(), paramCursor.getCount());
      for (;;)
      {
        boolean bool = paramCursor.moveToNext();
        if (!bool) {
          break;
        }
        Object[] arrayOfObject = new Object[paramCursor.getColumnCount()];
        int i = 0;
        for (;;)
        {
          int j = paramCursor.getColumnCount();
          if (i >= j) {
            break label204;
          }
          j = paramCursor.getType(i);
          switch (j)
          {
          default: 
            break;
          case 4: 
            arrayOfObject[i] = paramCursor.getBlob(i);
            break;
          case 3: 
            arrayOfObject[i] = paramCursor.getString(i);
            break;
          case 2: 
            arrayOfObject[i] = Double.valueOf(paramCursor.getDouble(i));
            break;
          case 1: 
            arrayOfObject[i] = Long.valueOf(paramCursor.getLong(i));
            break;
          case 0: 
            arrayOfObject[i] = null;
          }
          i += 1;
        }
        throw new IllegalStateException();
        label204:
        localMatrixCursor.addRow(arrayOfObject);
      }
      paramCursor.close();
      return localMatrixCursor;
    }
    catch (Throwable localThrowable)
    {
      paramCursor.close();
      throw localThrowable;
    }
  }
  
  public static int getColumnIndex(Cursor paramCursor, String paramString)
  {
    int i = paramCursor.getColumnIndex(paramString);
    if (i >= 0) {
      return i;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("`");
    localStringBuilder.append(paramString);
    localStringBuilder.append("`");
    return paramCursor.getColumnIndex(localStringBuilder.toString());
  }
  
  public static int getColumnIndexOrThrow(Cursor paramCursor, String paramString)
  {
    int i = paramCursor.getColumnIndex(paramString);
    if (i >= 0) {
      return i;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("`");
    localStringBuilder.append(paramString);
    localStringBuilder.append("`");
    return paramCursor.getColumnIndexOrThrow(localStringBuilder.toString());
  }
}
