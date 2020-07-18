package androidx.room.util;

import android.database.Cursor;
import androidx.annotation.RestrictTo;
import androidx.sqlite.wiki.SupportSQLiteDatabase;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public class ViewInfo
{
  public final String name;
  public final String text;
  
  public ViewInfo(String paramString1, String paramString2)
  {
    name = paramString1;
    text = paramString2;
  }
  
  public static ViewInfo read(SupportSQLiteDatabase paramSupportSQLiteDatabase, String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("SELECT name, sql FROM sqlite_master WHERE type = 'view' AND name = '");
    localStringBuilder.append(paramString);
    localStringBuilder.append("'");
    paramSupportSQLiteDatabase = paramSupportSQLiteDatabase.query(localStringBuilder.toString());
    try
    {
      boolean bool = paramSupportSQLiteDatabase.moveToFirst();
      if (bool)
      {
        paramString = new ViewInfo(paramSupportSQLiteDatabase.getString(0), paramSupportSQLiteDatabase.getString(1));
        paramSupportSQLiteDatabase.close();
        return paramString;
      }
      paramString = new ViewInfo(paramString, null);
      paramSupportSQLiteDatabase.close();
      return paramString;
    }
    catch (Throwable paramString)
    {
      paramSupportSQLiteDatabase.close();
      throw paramString;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject != null)
    {
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (ViewInfo)paramObject;
      if (name != null ? name.equals(name) : name == null) {
        if (text != null)
        {
          if (text.equals(text)) {
            return true;
          }
        }
        else if (text == null) {
          return true;
        }
      }
      return false;
    }
    return false;
  }
  
  public int hashCode()
  {
    String str = name;
    int j = 0;
    int i;
    if (str != null) {
      i = name.hashCode();
    } else {
      i = 0;
    }
    if (text != null) {
      j = text.hashCode();
    }
    return i * 31 + j;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("ViewInfo{name='");
    localStringBuilder.append(name);
    localStringBuilder.append('\'');
    localStringBuilder.append(", sql='");
    localStringBuilder.append(text);
    localStringBuilder.append('\'');
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}
