package com.google.android.gms.common.data;

import android.content.ContentValues;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteClosable;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.Log;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.common.internal.Asserts;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.VersionField;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

@KeepForSdk
@KeepName
@SafeParcelable.Class(creator="DataHolderCreator", validate=true)
public final class DataHolder
  extends AbstractSafeParcelable
  implements Closeable
{
  @KeepForSdk
  public static final Parcelable.Creator<DataHolder> CREATOR = new VerticalProgressBar.SavedState.1();
  private static final Builder zaly = new Image(new String[0], null);
  private boolean mClosed = false;
  @SafeParcelable.VersionField(id=1000)
  private final int zalf;
  @SafeParcelable.Field(getter="getColumns", id=1)
  private final String[] zalq;
  private Bundle zalr;
  @SafeParcelable.Field(getter="getWindows", id=2)
  private final CursorWindow[] zals;
  @SafeParcelable.Field(getter="getStatusCode", id=3)
  private final int zalt;
  @SafeParcelable.Field(getter="getMetadata", id=4)
  private final Bundle zalu;
  private int[] zalv;
  private int zalw;
  private boolean zalx = true;
  
  DataHolder(int paramInt1, String[] paramArrayOfString, CursorWindow[] paramArrayOfCursorWindow, int paramInt2, Bundle paramBundle)
  {
    zalf = paramInt1;
    zalq = paramArrayOfString;
    zals = paramArrayOfCursorWindow;
    zalt = paramInt2;
    zalu = paramBundle;
  }
  
  public DataHolder(Cursor paramCursor, int paramInt, Bundle paramBundle)
  {
    this(new com.google.android.gms.common.sqlite.CursorWrapper(paramCursor), paramInt, paramBundle);
  }
  
  private DataHolder(Builder paramBuilder, int paramInt, Bundle paramBundle)
  {
    this(Builder.access$getTaskService(paramBuilder), doInBackground(paramBuilder, -1), paramInt, null);
  }
  
  private DataHolder(Builder paramBuilder, int paramInt1, Bundle paramBundle, int paramInt2)
  {
    this(Builder.access$getTaskService(paramBuilder), doInBackground(paramBuilder, -1), paramInt1, paramBundle);
  }
  
  private DataHolder(com.google.android.gms.common.sqlite.CursorWrapper paramCursorWrapper, int paramInt, Bundle paramBundle)
  {
    this(paramCursorWrapper.getColumnNames(), convert(paramCursorWrapper), paramInt, paramBundle);
  }
  
  public DataHolder(String[] paramArrayOfString, CursorWindow[] paramArrayOfCursorWindow, int paramInt, Bundle paramBundle)
  {
    zalf = 1;
    zalq = ((String[])Preconditions.checkNotNull(paramArrayOfString));
    zals = ((CursorWindow[])Preconditions.checkNotNull(paramArrayOfCursorWindow));
    zalt = paramInt;
    zalu = paramBundle;
    zaca();
  }
  
  public static Builder builder(String[] paramArrayOfString)
  {
    return new Builder(paramArrayOfString, null, null);
  }
  
  private static CursorWindow[] convert(com.google.android.gms.common.sqlite.CursorWrapper paramCursorWrapper)
  {
    ArrayList localArrayList = new ArrayList();
    try
    {
      int j = paramCursorWrapper.getCount();
      Object localObject = paramCursorWrapper.getWindow();
      if (localObject != null)
      {
        i = ((CursorWindow)localObject).getStartPosition();
        if (i == 0)
        {
          ((SQLiteClosable)localObject).acquireReference();
          paramCursorWrapper.setWindow(null);
          localArrayList.add(localObject);
          i = ((CursorWindow)localObject).getNumRows();
          break label64;
        }
      }
      int i = 0;
      label64:
      while (i < j)
      {
        boolean bool = paramCursorWrapper.moveToPosition(i);
        if (!bool) {
          break;
        }
        CursorWindow localCursorWindow = paramCursorWrapper.getWindow();
        localObject = localCursorWindow;
        if (localCursorWindow != null)
        {
          localCursorWindow.acquireReference();
          paramCursorWrapper.setWindow(null);
        }
        else
        {
          localObject = new CursorWindow(false);
          ((CursorWindow)localObject).setStartPosition(i);
          paramCursorWrapper.fillWindow(i, (CursorWindow)localObject);
        }
        i = ((CursorWindow)localObject).getNumRows();
        if (i == 0) {
          break;
        }
        localArrayList.add(localObject);
        i = ((CursorWindow)localObject).getStartPosition();
        int k = ((CursorWindow)localObject).getNumRows();
        i += k;
      }
      paramCursorWrapper.close();
      return (CursorWindow[])localArrayList.toArray(new CursorWindow[localArrayList.size()]);
    }
    catch (Throwable localThrowable)
    {
      paramCursorWrapper.close();
      throw localThrowable;
    }
  }
  
  private static CursorWindow[] doInBackground(Builder paramBuilder, int paramInt)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a9 = a8\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  public static DataHolder empty(int paramInt)
  {
    return new DataHolder(zaly, paramInt, null);
  }
  
  private final void get(String paramString, int paramInt)
  {
    if ((zalr != null) && (zalr.containsKey(paramString)))
    {
      if (!isClosed())
      {
        if ((paramInt >= 0) && (paramInt < zalw)) {
          return;
        }
        throw new CursorIndexOutOfBoundsException(paramInt, zalw);
      }
      throw new IllegalArgumentException("Buffer is closed.");
    }
    paramString = String.valueOf(paramString);
    if (paramString.length() != 0) {
      paramString = "No such column: ".concat(paramString);
    } else {
      paramString = new String("No such column: ");
    }
    throw new IllegalArgumentException(paramString);
  }
  
  public final void close()
  {
    try
    {
      if (!mClosed)
      {
        mClosed = true;
        int i = 0;
        while (i < zals.length)
        {
          zals[i].close();
          i += 1;
        }
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  protected final void finalize()
    throws Throwable
  {
    try
    {
      boolean bool = zalx;
      if (bool)
      {
        int i = zals.length;
        if (i > 0)
        {
          bool = isClosed();
          if (!bool)
          {
            close();
            String str = toString();
            i = String.valueOf(str).length();
            StringBuilder localStringBuilder = new StringBuilder(i + 178);
            localStringBuilder.append("Internal data leak within a DataBuffer object detected!  Be sure to explicitly call release() on all DataBuffer extending objects when you are done with them. (internal object: ");
            localStringBuilder.append(str);
            localStringBuilder.append(")");
            Log.e("DataBuffer", localStringBuilder.toString());
          }
        }
      }
      super.finalize();
      return;
    }
    catch (Throwable localThrowable)
    {
      super.finalize();
      throw localThrowable;
    }
  }
  
  public final boolean getBoolean(String paramString, int paramInt1, int paramInt2)
  {
    get(paramString, paramInt1);
    return Long.valueOf(zals[paramInt2].getLong(paramInt1, zalr.getInt(paramString))).longValue() == 1L;
  }
  
  public final byte[] getByteArray(String paramString, int paramInt1, int paramInt2)
  {
    get(paramString, paramInt1);
    return zals[paramInt2].getBlob(paramInt1, zalr.getInt(paramString));
  }
  
  public final int getCount()
  {
    return zalw;
  }
  
  public final double getDouble(String paramString, int paramInt1, int paramInt2)
  {
    get(paramString, paramInt1);
    return zals[paramInt2].getDouble(paramInt1, zalr.getInt(paramString));
  }
  
  public final float getFloat(String paramString, int paramInt1, int paramInt2)
  {
    get(paramString, paramInt1);
    return zals[paramInt2].getFloat(paramInt1, zalr.getInt(paramString));
  }
  
  public final int getInteger(String paramString, int paramInt1, int paramInt2)
  {
    get(paramString, paramInt1);
    return zals[paramInt2].getInt(paramInt1, zalr.getInt(paramString));
  }
  
  public final long getLong(String paramString, int paramInt1, int paramInt2)
  {
    get(paramString, paramInt1);
    return zals[paramInt2].getLong(paramInt1, zalr.getInt(paramString));
  }
  
  public final Bundle getMetadata()
  {
    return zalu;
  }
  
  public final int getStatusCode()
  {
    return zalt;
  }
  
  public final String getString(String paramString, int paramInt1, int paramInt2)
  {
    get(paramString, paramInt1);
    return zals[paramInt2].getString(paramInt1, zalr.getInt(paramString));
  }
  
  public final int getWindowIndex(int paramInt)
  {
    int i = 0;
    boolean bool;
    if ((paramInt >= 0) && (paramInt < zalw)) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkState(bool);
    int j;
    for (;;)
    {
      j = i;
      if (i >= zalv.length) {
        break;
      }
      if (paramInt < zalv[i])
      {
        j = i - 1;
        break;
      }
      i += 1;
    }
    if (j == zalv.length) {
      return j - 1;
    }
    return j;
  }
  
  public final boolean hasColumn(String paramString)
  {
    return zalr.containsKey(paramString);
  }
  
  public final boolean hasNull(String paramString, int paramInt1, int paramInt2)
  {
    get(paramString, paramInt1);
    return zals[paramInt2].isNull(paramInt1, zalr.getInt(paramString));
  }
  
  public final boolean isClosed()
  {
    try
    {
      boolean bool = mClosed;
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final void setData(String paramString, int paramInt1, int paramInt2, CharArrayBuffer paramCharArrayBuffer)
  {
    get(paramString, paramInt1);
    zals[paramInt2].copyStringToBuffer(paramInt1, zalr.getInt(paramString), paramCharArrayBuffer);
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeStringArray(paramParcel, 1, zalq, false);
    SafeParcelWriter.writeTypedArray(paramParcel, 2, zals, paramInt, false);
    SafeParcelWriter.writeInt(paramParcel, 3, getStatusCode());
    SafeParcelWriter.writeBundle(paramParcel, 4, getMetadata(), false);
    SafeParcelWriter.writeInt(paramParcel, 1000, zalf);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
    if ((paramInt & 0x1) != 0) {
      close();
    }
  }
  
  public final void zaca()
  {
    zalr = new Bundle();
    int k = 0;
    int i = 0;
    while (i < zalq.length)
    {
      zalr.putInt(zalq[i], i);
      i += 1;
    }
    zalv = new int[zals.length];
    int j = 0;
    i = k;
    while (i < zals.length)
    {
      zalv[i] = j;
      k = zals[i].getStartPosition();
      j += zals[i].getNumRows() - (j - k);
      i += 1;
    }
    zalw = j;
  }
  
  @KeepForSdk
  public static class Builder
  {
    private final String[] zalq;
    private final ArrayList<HashMap<String, Object>> zalz;
    private final String zama;
    private final HashMap<Object, Integer> zamb;
    private boolean zamc;
    private String zamd;
    
    private Builder(String[] paramArrayOfString, String paramString)
    {
      zalq = ((String[])Preconditions.checkNotNull(paramArrayOfString));
      zalz = new ArrayList();
      zama = paramString;
      zamb = new HashMap();
      zamc = false;
      zamd = null;
    }
    
    public DataHolder build(int paramInt)
    {
      return new DataHolder(this, paramInt, null, null);
    }
    
    public DataHolder build(int paramInt, Bundle paramBundle)
    {
      return new DataHolder(this, paramInt, paramBundle, -1, null);
    }
    
    public Builder intersect(HashMap paramHashMap)
    {
      Asserts.checkNotNull(paramHashMap);
      if (zama == null) {}
      Integer localInteger;
      for (;;)
      {
        i = -1;
        break label78;
        Object localObject = paramHashMap.get(zama);
        if (localObject != null)
        {
          localInteger = (Integer)zamb.get(localObject);
          if (localInteger != null) {
            break;
          }
          zamb.put(localObject, Integer.valueOf(zalz.size()));
        }
      }
      int i = localInteger.intValue();
      label78:
      if (i == -1)
      {
        zalz.add(paramHashMap);
      }
      else
      {
        zalz.remove(i);
        zalz.add(i, paramHashMap);
      }
      zamc = false;
      return this;
    }
    
    public Builder withRow(ContentValues paramContentValues)
    {
      Asserts.checkNotNull(paramContentValues);
      HashMap localHashMap = new HashMap(paramContentValues.size());
      paramContentValues = paramContentValues.valueSet().iterator();
      while (paramContentValues.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)paramContentValues.next();
        localHashMap.put((String)localEntry.getKey(), localEntry.getValue());
      }
      return intersect(localHashMap);
    }
  }
  
  public static final class zaa
    extends RuntimeException
  {
    public zaa(String paramString)
    {
      super();
    }
  }
}
