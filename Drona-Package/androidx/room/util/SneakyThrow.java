package androidx.room.util;

import androidx.annotation.RestrictTo;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public class SneakyThrow
{
  private SneakyThrow() {}
  
  public static void reThrow(Exception paramException)
  {
    sneakyThrow(paramException);
  }
  
  private static void sneakyThrow(Throwable paramThrowable)
    throws Throwable
  {
    throw paramThrowable;
  }
}
