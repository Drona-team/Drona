package androidx.core.util;

import androidx.annotation.RestrictTo;
import java.util.Locale;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public final class Preconditions
{
  private Preconditions() {}
  
  public static void checkArgument(boolean paramBoolean)
  {
    if (paramBoolean) {
      return;
    }
    throw new IllegalArgumentException();
  }
  
  public static void checkArgument(boolean paramBoolean, Object paramObject)
  {
    if (paramBoolean) {
      return;
    }
    throw new IllegalArgumentException(String.valueOf(paramObject));
  }
  
  public static int checkArgumentInRange(int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    if (paramInt1 >= paramInt2)
    {
      if (paramInt1 <= paramInt3) {
        return paramInt1;
      }
      throw new IllegalArgumentException(String.format(Locale.US, "%s is out of range of [%d, %d] (too high)", new Object[] { paramString, Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) }));
    }
    throw new IllegalArgumentException(String.format(Locale.US, "%s is out of range of [%d, %d] (too low)", new Object[] { paramString, Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) }));
  }
  
  public static int checkArgumentNonnegative(int paramInt)
  {
    if (paramInt >= 0) {
      return paramInt;
    }
    throw new IllegalArgumentException();
  }
  
  public static int checkArgumentNonnegative(int paramInt, String paramString)
  {
    if (paramInt >= 0) {
      return paramInt;
    }
    throw new IllegalArgumentException(paramString);
  }
  
  public static Object checkNotNull(Object paramObject)
  {
    if (paramObject != null) {
      return paramObject;
    }
    throw new NullPointerException();
  }
  
  public static Object checkNotNull(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 != null) {
      return paramObject1;
    }
    throw new NullPointerException(String.valueOf(paramObject2));
  }
  
  public static void checkState(boolean paramBoolean)
  {
    checkState(paramBoolean, null);
  }
  
  public static void checkState(boolean paramBoolean, String paramString)
  {
    if (paramBoolean) {
      return;
    }
    throw new IllegalStateException(paramString);
  }
}
