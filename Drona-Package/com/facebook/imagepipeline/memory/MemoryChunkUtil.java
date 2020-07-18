package com.facebook.imagepipeline.memory;

import com.facebook.common.internal.Preconditions;

public class MemoryChunkUtil
{
  public MemoryChunkUtil() {}
  
  static int adjustByteCount(int paramInt1, int paramInt2, int paramInt3)
  {
    return Math.min(Math.max(0, paramInt3 - paramInt1), paramInt2);
  }
  
  static void checkBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    boolean bool2 = false;
    if (paramInt4 >= 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    if (paramInt1 >= 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    if (paramInt3 >= 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    if (paramInt1 + paramInt4 <= paramInt5) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    boolean bool1 = bool2;
    if (paramInt3 + paramInt4 <= paramInt2) {
      bool1 = true;
    }
    Preconditions.checkArgument(bool1);
  }
}