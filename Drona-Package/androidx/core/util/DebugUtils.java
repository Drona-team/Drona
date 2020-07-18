package androidx.core.util;

import androidx.annotation.RestrictTo;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public class DebugUtils
{
  private DebugUtils() {}
  
  public static void buildShortClassTag(Object paramObject, StringBuilder paramStringBuilder)
  {
    if (paramObject == null)
    {
      paramStringBuilder.append("null");
      return;
    }
    String str2 = paramObject.getClass().getSimpleName();
    String str1 = str2;
    if ((str2 == null) || (str2.length() <= 0))
    {
      str2 = paramObject.getClass().getName();
      str1 = str2;
      int i = str2.lastIndexOf('.');
      if (i > 0) {
        str1 = str2.substring(i + 1);
      }
    }
    paramStringBuilder.append(str1);
    paramStringBuilder.append('{');
    paramStringBuilder.append(Integer.toHexString(System.identityHashCode(paramObject)));
  }
}
