package com.facebook.imagepipeline.memory;

import java.nio.ByteBuffer;

public abstract interface MemoryChunk
{
  public abstract void close();
  
  public abstract void copy(int paramInt1, MemoryChunk paramMemoryChunk, int paramInt2, int paramInt3);
  
  public abstract ByteBuffer getByteBuffer();
  
  public abstract long getNativePtr()
    throws UnsupportedOperationException;
  
  public abstract int getSize();
  
  public abstract long getUniqueId();
  
  public abstract boolean isClosed();
  
  public abstract byte read(int paramInt);
  
  public abstract int read(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3);
  
  public abstract int write(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3);
}
