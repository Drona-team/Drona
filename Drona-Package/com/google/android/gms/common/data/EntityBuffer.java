package com.google.android.gms.common.data;

import com.google.android.gms.common.annotation.KeepForSdk;
import java.util.ArrayList;

@KeepForSdk
public abstract class EntityBuffer<T>
  extends AbstractDataBuffer<T>
{
  private boolean zame = false;
  private ArrayList<Integer> zamf;
  
  protected EntityBuffer(DataHolder paramDataHolder)
  {
    super(paramDataHolder);
  }
  
  private final int getIndex(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < zamf.size())) {
      return ((Integer)zamf.get(paramInt)).intValue();
    }
    StringBuilder localStringBuilder = new StringBuilder(53);
    localStringBuilder.append("Position ");
    localStringBuilder.append(paramInt);
    localStringBuilder.append(" is out of bounds for this buffer");
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  private final void zacb()
  {
    for (;;)
    {
      int i;
      Object localObject3;
      try
      {
        if (!zame)
        {
          int j = mDataHolder.getCount();
          zamf = new ArrayList();
          if (j > 0)
          {
            zamf.add(Integer.valueOf(0));
            String str2 = getPrimaryDataMarkerColumn();
            i = mDataHolder.getWindowIndex(0);
            Object localObject1 = mDataHolder.getString(str2, 0, i);
            i = 1;
            if (i < j)
            {
              int k = mDataHolder.getWindowIndex(i);
              String str1 = mDataHolder.getString(str2, i, k);
              if (str1 != null)
              {
                localObject3 = localObject1;
                if (str1.equals(localObject1)) {
                  break label233;
                }
                zamf.add(Integer.valueOf(i));
                localObject3 = str1;
                break label233;
              }
              localObject1 = new StringBuilder(String.valueOf(str2).length() + 78);
              ((StringBuilder)localObject1).append("Missing value for markerColumn: ");
              ((StringBuilder)localObject1).append(str2);
              ((StringBuilder)localObject1).append(", at row: ");
              ((StringBuilder)localObject1).append(i);
              ((StringBuilder)localObject1).append(", for window: ");
              ((StringBuilder)localObject1).append(k);
              throw new NullPointerException(((StringBuilder)localObject1).toString());
            }
          }
          zame = true;
        }
        else
        {
          return;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      label233:
      i += 1;
      Object localObject2 = localObject3;
    }
  }
  
  public final Object get(int paramInt)
  {
    zacb();
    int k = getIndex(paramInt);
    int j = 0;
    int i = j;
    if (paramInt >= 0) {
      if (paramInt == zamf.size())
      {
        i = j;
      }
      else
      {
        if (paramInt == zamf.size() - 1) {
          i = mDataHolder.getCount() - ((Integer)zamf.get(paramInt)).intValue();
        } else {
          i = ((Integer)zamf.get(paramInt + 1)).intValue() - ((Integer)zamf.get(paramInt)).intValue();
        }
        if (i == 1)
        {
          paramInt = getIndex(paramInt);
          int m = mDataHolder.getWindowIndex(paramInt);
          String str = getChildDataMarkerColumn();
          if ((str != null) && (mDataHolder.getString(str, paramInt, m) == null)) {
            i = j;
          }
        }
      }
    }
    return getEntry(k, i);
  }
  
  protected String getChildDataMarkerColumn()
  {
    return null;
  }
  
  public int getCount()
  {
    zacb();
    return zamf.size();
  }
  
  protected abstract Object getEntry(int paramInt1, int paramInt2);
  
  protected abstract String getPrimaryDataMarkerColumn();
}
