package com.google.android.exoplayer2.upstream;

import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.Arrays;

public final class DefaultAllocator
  implements Allocator
{
  private static final int AVAILABLE_EXTRA_CAPACITY = 100;
  private int allocatedCount;
  private Allocation[] availableAllocations;
  private int availableCount;
  private final int individualAllocationSize;
  private final byte[] initialAllocationBlock;
  private final Allocation[] singleAllocationReleaseHolder;
  private int targetBufferSize;
  private final boolean trimOnReset;
  
  public DefaultAllocator(boolean paramBoolean, int paramInt)
  {
    this(paramBoolean, paramInt, 0);
  }
  
  public DefaultAllocator(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    int i = 0;
    boolean bool;
    if (paramInt1 > 0) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    if (paramInt2 >= 0) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    trimOnReset = paramBoolean;
    individualAllocationSize = paramInt1;
    availableCount = paramInt2;
    availableAllocations = new Allocation[paramInt2 + 100];
    if (paramInt2 > 0)
    {
      initialAllocationBlock = new byte[paramInt2 * paramInt1];
      while (i < paramInt2)
      {
        availableAllocations[i] = new Allocation(initialAllocationBlock, i * paramInt1);
        i += 1;
      }
    }
    initialAllocationBlock = null;
    singleAllocationReleaseHolder = new Allocation[1];
  }
  
  public Allocation allocate()
  {
    try
    {
      allocatedCount += 1;
      Object localObject;
      if (availableCount > 0)
      {
        localObject = availableAllocations;
        int i = availableCount - 1;
        availableCount = i;
        localObject = localObject[i];
        availableAllocations[availableCount] = null;
      }
      else
      {
        localObject = new Allocation(new byte[individualAllocationSize], 0);
      }
      return localObject;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public int getIndividualAllocationLength()
  {
    return individualAllocationSize;
  }
  
  public int getTotalBytesAllocated()
  {
    try
    {
      int i = allocatedCount;
      int j = individualAllocationSize;
      return i * j;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void release(Allocation paramAllocation)
  {
    try
    {
      singleAllocationReleaseHolder[0] = paramAllocation;
      release(singleAllocationReleaseHolder);
      return;
    }
    catch (Throwable paramAllocation)
    {
      throw paramAllocation;
    }
  }
  
  public void release(Allocation[] paramArrayOfAllocation)
  {
    try
    {
      if (availableCount + paramArrayOfAllocation.length >= availableAllocations.length) {
        availableAllocations = ((Allocation[])Arrays.copyOf(availableAllocations, Math.max(availableAllocations.length * 2, availableCount + paramArrayOfAllocation.length)));
      }
      int j = paramArrayOfAllocation.length;
      int i = 0;
      while (i < j)
      {
        Allocation localAllocation = paramArrayOfAllocation[i];
        Allocation[] arrayOfAllocation = availableAllocations;
        int k = availableCount;
        availableCount = (k + 1);
        arrayOfAllocation[k] = localAllocation;
        i += 1;
      }
      allocatedCount -= paramArrayOfAllocation.length;
      notifyAll();
      return;
    }
    catch (Throwable paramArrayOfAllocation)
    {
      throw paramArrayOfAllocation;
    }
  }
  
  public void reset()
  {
    try
    {
      if (trimOnReset) {
        setTargetBufferSize(0);
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void setTargetBufferSize(int paramInt)
  {
    for (;;)
    {
      try
      {
        if (paramInt < targetBufferSize)
        {
          i = 1;
          targetBufferSize = paramInt;
          if (i != 0) {
            trim();
          }
          return;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      int i = 0;
    }
  }
  
  public void trim()
  {
    try
    {
      int i = Util.ceilDivide(targetBufferSize, individualAllocationSize);
      int j = allocatedCount;
      int m = 0;
      int k = Math.max(0, i - j);
      i = k;
      j = availableCount;
      if (k >= j) {
        return;
      }
      if (initialAllocationBlock != null)
      {
        j = availableCount - 1;
        i = m;
        while (i <= j)
        {
          Allocation localAllocation1 = availableAllocations[i];
          if (data == initialAllocationBlock)
          {
            i += 1;
          }
          else
          {
            Allocation localAllocation2 = availableAllocations[j];
            if (data != initialAllocationBlock)
            {
              j -= 1;
            }
            else
            {
              availableAllocations[i] = localAllocation2;
              availableAllocations[j] = localAllocation1;
              j -= 1;
              i += 1;
            }
          }
        }
        j = Math.max(k, i);
        i = j;
        k = availableCount;
        if (j >= k) {
          return;
        }
      }
      Arrays.fill(availableAllocations, i, availableCount, null);
      availableCount = i;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
}
