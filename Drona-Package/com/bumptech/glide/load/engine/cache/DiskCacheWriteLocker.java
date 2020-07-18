package com.bumptech.glide.load.engine.cache;

import com.bumptech.glide.util.Preconditions;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class DiskCacheWriteLocker
{
  private final Map<String, WriteLock> locks = new HashMap();
  private final WriteLockPool writeLockPool = new WriteLockPool();
  
  DiskCacheWriteLocker() {}
  
  void acquire(String paramString)
  {
    try
    {
      WriteLock localWriteLock2 = (WriteLock)locks.get(paramString);
      WriteLock localWriteLock1 = localWriteLock2;
      if (localWriteLock2 == null)
      {
        localWriteLock2 = writeLockPool.obtain();
        localWriteLock1 = localWriteLock2;
        locks.put(paramString, localWriteLock2);
      }
      interestedThreads += 1;
      lock.lock();
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  void release(String paramString)
  {
    Object localObject1 = this;
    try
    {
      Object localObject2 = locks;
      DiskCacheWriteLocker localDiskCacheWriteLocker = this;
      localObject1 = localDiskCacheWriteLocker;
      localObject2 = (WriteLock)Preconditions.checkNotNull(((Map)localObject2).get(paramString));
      localObject1 = localDiskCacheWriteLocker;
      if (interestedThreads >= 1)
      {
        localObject1 = localDiskCacheWriteLocker;
        interestedThreads -= 1;
        localObject1 = localDiskCacheWriteLocker;
        if (interestedThreads == 0)
        {
          localObject1 = localDiskCacheWriteLocker;
          localObject3 = locks;
          localObject1 = localDiskCacheWriteLocker;
          localObject3 = (WriteLock)((Map)localObject3).remove(paramString);
          localObject1 = localDiskCacheWriteLocker;
          if (localObject3.equals(localObject2))
          {
            localObject1 = localDiskCacheWriteLocker;
            writeLockPool.offer((WriteLock)localObject3);
          }
          else
          {
            localObject1 = localDiskCacheWriteLocker;
            StringBuilder localStringBuilder = new StringBuilder();
            localObject1 = localDiskCacheWriteLocker;
            localStringBuilder.append("Removed the wrong lock, expected to remove: ");
            localObject1 = localDiskCacheWriteLocker;
            localStringBuilder.append(localObject2);
            localObject1 = localDiskCacheWriteLocker;
            localStringBuilder.append(", but actually removed: ");
            localObject1 = localDiskCacheWriteLocker;
            localStringBuilder.append(localObject3);
            localObject1 = localDiskCacheWriteLocker;
            localStringBuilder.append(", safeKey: ");
            localObject1 = localDiskCacheWriteLocker;
            localStringBuilder.append(paramString);
            localObject1 = localDiskCacheWriteLocker;
            throw new IllegalStateException(localStringBuilder.toString());
          }
        }
        paramString = this;
        localObject1 = paramString;
        lock.unlock();
        return;
      }
      localObject1 = localDiskCacheWriteLocker;
      Object localObject3 = new StringBuilder();
      localObject1 = localDiskCacheWriteLocker;
      ((StringBuilder)localObject3).append("Cannot release a lock that is not held, safeKey: ");
      localObject1 = localDiskCacheWriteLocker;
      ((StringBuilder)localObject3).append(paramString);
      localObject1 = localDiskCacheWriteLocker;
      ((StringBuilder)localObject3).append(", interestedThreads: ");
      localObject1 = localDiskCacheWriteLocker;
      ((StringBuilder)localObject3).append(interestedThreads);
      localObject1 = localDiskCacheWriteLocker;
      throw new IllegalStateException(((StringBuilder)localObject3).toString());
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  private static class WriteLock
  {
    int interestedThreads;
    final Lock lock = new ReentrantLock();
    
    WriteLock() {}
  }
  
  private static class WriteLockPool
  {
    private static final int MAX_POOL_SIZE = 10;
    private final Queue<DiskCacheWriteLocker.WriteLock> pool = new ArrayDeque();
    
    WriteLockPool() {}
    
    DiskCacheWriteLocker.WriteLock obtain()
    {
      Queue localQueue = pool;
      try
      {
        DiskCacheWriteLocker.WriteLock localWriteLock = (DiskCacheWriteLocker.WriteLock)pool.poll();
        if (localWriteLock == null) {
          return new DiskCacheWriteLocker.WriteLock();
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      return localThrowable;
    }
    
    void offer(DiskCacheWriteLocker.WriteLock paramWriteLock)
    {
      Queue localQueue = pool;
      try
      {
        if (pool.size() < 10) {
          pool.offer(paramWriteLock);
        }
        return;
      }
      catch (Throwable paramWriteLock)
      {
        throw paramWriteLock;
      }
    }
  }
}
