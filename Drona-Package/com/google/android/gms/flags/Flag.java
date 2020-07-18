package com.google.android.gms.flags;

import android.os.RemoteException;
import com.google.android.gms.common.annotation.KeepForSdk;

@Deprecated
@KeepForSdk
public abstract class Flag<T>
{
  private final String mKey;
  private final T mValue;
  private final int text;
  
  private Flag(int paramInt, String paramString, java.lang.Object paramObject)
  {
    text = paramInt;
    mKey = paramString;
    mValue = paramObject;
    Singletons.flagRegistry().scan(this);
  }
  
  public static BooleanFlag define(int paramInt, String paramString, Boolean paramBoolean)
  {
    return new BooleanFlag(paramInt, paramString, paramBoolean);
  }
  
  public static IntegerFlag define(int paramInt1, String paramString, int paramInt2)
  {
    return new IntegerFlag(paramInt1, paramString, Integer.valueOf(paramInt2));
  }
  
  public static LongFlag define(int paramInt, String paramString, long paramLong)
  {
    return new LongFlag(paramInt, paramString, Long.valueOf(paramLong));
  }
  
  public static StringFlag define(int paramInt, String paramString1, String paramString2)
  {
    return new StringFlag(paramInt, paramString1, paramString2);
  }
  
  public final java.lang.Object get()
  {
    return mValue;
  }
  
  protected abstract java.lang.Object get(Object paramObject);
  
  public final String getKey()
  {
    return mKey;
  }
  
  public final int getSource()
  {
    return text;
  }
  
  public java.lang.Object initialize()
  {
    return Singletons.Instance().initialize(this);
  }
  
  @Deprecated
  public static class BooleanFlag
    extends Flag<Boolean>
  {
    public BooleanFlag(int paramInt, String paramString, Boolean paramBoolean)
    {
      super(paramString, paramBoolean, null);
    }
    
    private final Boolean visit(Object paramObject)
    {
      try
      {
        String str = getKey();
        java.lang.Object localObject = get();
        localObject = (Boolean)localObject;
        boolean bool = paramObject.getBooleanFlagValue(str, ((Boolean)localObject).booleanValue(), getSource());
        return Boolean.valueOf(bool);
      }
      catch (RemoteException paramObject)
      {
        for (;;) {}
      }
      return (Boolean)get();
    }
  }
  
  @Deprecated
  @KeepForSdk
  public static class IntegerFlag
    extends Flag<Integer>
  {
    public IntegerFlag(int paramInt, String paramString, Integer paramInteger)
    {
      super(paramString, paramInteger, null);
    }
    
    private final Integer visit(Object paramObject)
    {
      try
      {
        String str = getKey();
        java.lang.Object localObject = get();
        localObject = (Integer)localObject;
        int i = paramObject.getIntFlagValue(str, ((Integer)localObject).intValue(), getSource());
        return Integer.valueOf(i);
      }
      catch (RemoteException paramObject)
      {
        for (;;) {}
      }
      return (Integer)get();
    }
  }
  
  @Deprecated
  @KeepForSdk
  public static class LongFlag
    extends Flag<Long>
  {
    public LongFlag(int paramInt, String paramString, Long paramLong)
    {
      super(paramString, paramLong, null);
    }
    
    private final Long visit(Object paramObject)
    {
      try
      {
        String str = getKey();
        java.lang.Object localObject = get();
        localObject = (Long)localObject;
        long l = paramObject.getLongFlagValue(str, ((Long)localObject).longValue(), getSource());
        return Long.valueOf(l);
      }
      catch (RemoteException paramObject)
      {
        for (;;) {}
      }
      return (Long)get();
    }
  }
  
  @Deprecated
  @KeepForSdk
  public static class StringFlag
    extends Flag<String>
  {
    public StringFlag(int paramInt, String paramString1, String paramString2)
    {
      super(paramString1, paramString2, null);
    }
    
    private final String transform(Object paramObject)
    {
      try
      {
        String str = getKey();
        java.lang.Object localObject = get();
        localObject = (String)localObject;
        paramObject = paramObject.getStringFlagValue(str, (String)localObject, getSource());
        return paramObject;
      }
      catch (RemoteException paramObject)
      {
        for (;;) {}
      }
      return (String)get();
    }
  }
}
