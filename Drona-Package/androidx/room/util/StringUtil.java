package androidx.room.util;

import android.util.Log;
import androidx.annotation.RestrictTo;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public class StringUtil
{
  public static final String[] EMPTY_STRING_ARRAY = new String[0];
  
  private StringUtil() {}
  
  public static void appendPlaceholders(StringBuilder paramStringBuilder, int paramInt)
  {
    int i = 0;
    while (i < paramInt)
    {
      paramStringBuilder.append("?");
      if (i < paramInt - 1) {
        paramStringBuilder.append(",");
      }
      i += 1;
    }
  }
  
  public static String joinIntoString(List paramList)
  {
    if (paramList == null) {
      return null;
    }
    int j = paramList.size();
    if (j == 0) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    while (i < j)
    {
      localStringBuilder.append(Integer.toString(((Integer)paramList.get(i)).intValue()));
      if (i < j - 1) {
        localStringBuilder.append(",");
      }
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  public static StringBuilder newStringBuilder()
  {
    return new StringBuilder();
  }
  
  public static List splitToIntList(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    paramString = new StringTokenizer(paramString, ",");
    while (paramString.hasMoreElements())
    {
      String str = paramString.nextToken();
      try
      {
        localArrayList.add(Integer.valueOf(Integer.parseInt(str)));
      }
      catch (NumberFormatException localNumberFormatException)
      {
        Log.e("ROOM", "Malformed integer list", localNumberFormatException);
      }
    }
    return localArrayList;
  }
}
