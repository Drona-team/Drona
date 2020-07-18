package com.google.android.exoplayer2.upstream;

public abstract interface Allocator
{
  public abstract Allocation allocate();
  
  public abstract int getIndividualAllocationLength();
  
  public abstract int getTotalBytesAllocated();
  
  public abstract void release(Allocation paramAllocation);
  
  public abstract void release(Allocation[] paramArrayOfAllocation);
  
  public abstract void trim();
}
