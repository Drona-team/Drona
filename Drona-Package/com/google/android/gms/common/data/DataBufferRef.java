package com.google.android.gms.common.data;

import android.database.CharArrayBuffer;
import android.net.Uri;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Preconditions;

@KeepForSdk
public class DataBufferRef
{
  @KeepForSdk
  protected final DataHolder mDataHolder;
  @KeepForSdk
  protected int mDataRow;
  private int zaln;
  
  public DataBufferRef(DataHolder paramDataHolder, int paramInt)
  {
    mDataHolder = ((DataHolder)Preconditions.checkNotNull(paramDataHolder));
    register(paramInt);
  }
  
  protected void copyToBuffer(String paramString, CharArrayBuffer paramCharArrayBuffer)
  {
    mDataHolder.setData(paramString, mDataRow, zaln, paramCharArrayBuffer);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof DataBufferRef))
    {
      paramObject = (DataBufferRef)paramObject;
      if ((Objects.equal(Integer.valueOf(mDataRow), Integer.valueOf(mDataRow))) && (Objects.equal(Integer.valueOf(zaln), Integer.valueOf(zaln))) && (mDataHolder == mDataHolder)) {
        return true;
      }
    }
    return false;
  }
  
  protected boolean getBoolean(String paramString)
  {
    return mDataHolder.getBoolean(paramString, mDataRow, zaln);
  }
  
  protected byte[] getByteArray(String paramString)
  {
    return mDataHolder.getByteArray(paramString, mDataRow, zaln);
  }
  
  protected int getDataRow()
  {
    return mDataRow;
  }
  
  protected double getDouble(String paramString)
  {
    return mDataHolder.getDouble(paramString, mDataRow, zaln);
  }
  
  protected float getFloat(String paramString)
  {
    return mDataHolder.getFloat(paramString, mDataRow, zaln);
  }
  
  protected int getInteger(String paramString)
  {
    return mDataHolder.getInteger(paramString, mDataRow, zaln);
  }
  
  protected long getLong(String paramString)
  {
    return mDataHolder.getLong(paramString, mDataRow, zaln);
  }
  
  protected String getString(String paramString)
  {
    return mDataHolder.getString(paramString, mDataRow, zaln);
  }
  
  public boolean hasColumn(String paramString)
  {
    return mDataHolder.hasColumn(paramString);
  }
  
  protected boolean hasNull(String paramString)
  {
    return mDataHolder.hasNull(paramString, mDataRow, zaln);
  }
  
  public int hashCode()
  {
    return Objects.hashCode(new Object[] { Integer.valueOf(mDataRow), Integer.valueOf(zaln), mDataHolder });
  }
  
  public boolean isDataValid()
  {
    return !mDataHolder.isClosed();
  }
  
  protected Uri parseUri(String paramString)
  {
    paramString = mDataHolder.getString(paramString, mDataRow, zaln);
    if (paramString == null) {
      return null;
    }
    return Uri.parse(paramString);
  }
  
  protected final void register(int paramInt)
  {
    boolean bool;
    if ((paramInt >= 0) && (paramInt < mDataHolder.getCount())) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkState(bool);
    mDataRow = paramInt;
    zaln = mDataHolder.getWindowIndex(mDataRow);
  }
}
