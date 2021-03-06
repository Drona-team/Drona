package com.facebook.device.yearclass;

import android.content.Context;
import java.util.ArrayList;
import java.util.Collections;

public class YearClass
{
  public static final int CLASS_2008 = 2008;
  public static final int CLASS_2009 = 2009;
  public static final int CLASS_2010 = 2010;
  public static final int CLASS_2011 = 2011;
  public static final int CLASS_2012 = 2012;
  public static final int CLASS_2013 = 2013;
  public static final int CLASS_2014 = 2014;
  public static final int CLASS_2015 = 2015;
  public static final int CLASS_2016 = 2016;
  public static final int CLASS_UNKNOWN = -1;
  private static final int MHZ_IN_KHZ = 1000;
  private static final long ONE_MB = 1048576L;
  private static volatile Integer mYearCategory;
  
  public YearClass() {}
  
  private static int categorizeByYear2014Method(Context paramContext)
  {
    ArrayList localArrayList = new ArrayList();
    conditionallyAdd(localArrayList, getNumCoresYear());
    conditionallyAdd(localArrayList, getClockSpeedYear());
    conditionallyAdd(localArrayList, getRamYear(paramContext));
    if (localArrayList.isEmpty()) {
      return -1;
    }
    Collections.sort(localArrayList);
    if ((localArrayList.size() & 0x1) == 1) {
      return ((Integer)localArrayList.get(localArrayList.size() / 2)).intValue();
    }
    int i = localArrayList.size() / 2 - 1;
    return ((Integer)localArrayList.get(i)).intValue() + (((Integer)localArrayList.get(i + 1)).intValue() - ((Integer)localArrayList.get(i)).intValue()) / 2;
  }
  
  private static int categorizeByYear2016Method(Context paramContext)
  {
    long l = DeviceInfo.getTotalMemory(paramContext);
    if (l == -1L) {
      return categorizeByYear2014Method(paramContext);
    }
    if (l <= 805306368L)
    {
      if (DeviceInfo.getNumberOfCPUCores() <= 1) {
        return 2009;
      }
      return 2010;
    }
    if (l <= 1073741824L)
    {
      if (DeviceInfo.getCPUMaxFreqKHz() < 1300000) {
        return 2011;
      }
    }
    else
    {
      if (l <= 1610612736L)
      {
        if (DeviceInfo.getCPUMaxFreqKHz() < 1800000) {
          return 2012;
        }
        return 2013;
      }
      if (l <= 2147483648L) {
        return 2013;
      }
      if (l <= 3221225472L) {
        return 2014;
      }
      if (l <= 5368709120L) {
        return 2015;
      }
      return 2016;
    }
    return 2012;
  }
  
  private static void conditionallyAdd(ArrayList paramArrayList, int paramInt)
  {
    if (paramInt != -1) {
      paramArrayList.add(Integer.valueOf(paramInt));
    }
  }
  
  public static int get(Context paramContext)
  {
    if (mYearCategory == null) {
      try
      {
        if (mYearCategory == null) {
          mYearCategory = Integer.valueOf(categorizeByYear2016Method(paramContext));
        }
      }
      catch (Throwable paramContext)
      {
        throw paramContext;
      }
    }
    return mYearCategory.intValue();
  }
  
  private static int getClockSpeedYear()
  {
    long l = DeviceInfo.getCPUMaxFreqKHz();
    if (l == -1L) {
      return -1;
    }
    if (l <= 528000L) {
      return 2008;
    }
    if (l <= 620000L) {
      return 2009;
    }
    if (l <= 1020000L) {
      return 2010;
    }
    if (l <= 1220000L) {
      return 2011;
    }
    if (l <= 1520000L) {
      return 2012;
    }
    if (l <= 2020000L) {
      return 2013;
    }
    return 2014;
  }
  
  private static int getNumCoresYear()
  {
    int i = DeviceInfo.getNumberOfCPUCores();
    if (i < 1) {
      return -1;
    }
    if (i == 1) {
      return 2008;
    }
    if (i <= 3) {
      return 2011;
    }
    return 2012;
  }
  
  private static int getRamYear(Context paramContext)
  {
    long l = DeviceInfo.getTotalMemory(paramContext);
    if (l <= 0L) {
      return -1;
    }
    if (l <= 201326592L) {
      return 2008;
    }
    if (l <= 304087040L) {
      return 2009;
    }
    if (l <= 536870912L) {
      return 2010;
    }
    if (l <= 1073741824L) {
      return 2011;
    }
    if (l <= 1610612736L) {
      return 2012;
    }
    if (l <= 2147483648L) {
      return 2013;
    }
    return 2014;
  }
}
