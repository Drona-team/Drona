package com.facebook.common.internal;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Throwables
{
  private Throwables() {}
  
  public static List getCausalChain(Throwable paramThrowable)
  {
    Preconditions.checkNotNull(paramThrowable);
    ArrayList localArrayList = new ArrayList(4);
    while (paramThrowable != null)
    {
      localArrayList.add(paramThrowable);
      paramThrowable = paramThrowable.getCause();
    }
    return Collections.unmodifiableList(localArrayList);
  }
  
  public static Throwable getRootCause(Throwable paramThrowable)
  {
    for (;;)
    {
      Throwable localThrowable = paramThrowable.getCause();
      if (localThrowable == null) {
        break;
      }
      paramThrowable = localThrowable;
    }
    return paramThrowable;
  }
  
  public static String getStackTraceAsString(Throwable paramThrowable)
  {
    StringWriter localStringWriter = new StringWriter();
    paramThrowable.printStackTrace(new PrintWriter(localStringWriter));
    return localStringWriter.toString();
  }
  
  public static RuntimeException propagate(Throwable paramThrowable)
  {
    propagateIfPossible((Throwable)Preconditions.checkNotNull(paramThrowable));
    throw new RuntimeException(paramThrowable);
  }
  
  public static void propagateIfInstanceOf(Throwable paramThrowable, Class paramClass)
    throws Throwable
  {
    if (paramThrowable != null)
    {
      if (!paramClass.isInstance(paramThrowable)) {
        return;
      }
      throw ((Throwable)paramClass.cast(paramThrowable));
    }
  }
  
  public static void propagateIfPossible(Throwable paramThrowable)
  {
    propagateIfInstanceOf(paramThrowable, Error.class);
    propagateIfInstanceOf(paramThrowable, RuntimeException.class);
  }
  
  public static void propagateIfPossible(Throwable paramThrowable, Class paramClass)
    throws Throwable
  {
    propagateIfInstanceOf(paramThrowable, paramClass);
    propagateIfPossible(paramThrowable);
  }
  
  public static void propagateIfPossible(Throwable paramThrowable, Class paramClass1, Class paramClass2)
    throws Throwable, Throwable
  {
    Preconditions.checkNotNull(paramClass2);
    propagateIfInstanceOf(paramThrowable, paramClass1);
    propagateIfPossible(paramThrowable, paramClass2);
  }
}