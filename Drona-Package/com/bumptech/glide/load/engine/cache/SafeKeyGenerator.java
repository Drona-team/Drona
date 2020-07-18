package com.bumptech.glide.load.engine.cache;

import androidx.core.util.Pools.Pool;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.LruCache;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import com.bumptech.glide.util.pool.FactoryPools;
import com.bumptech.glide.util.pool.FactoryPools.Factory;
import com.bumptech.glide.util.pool.FactoryPools.Poolable;
import com.bumptech.glide.util.pool.StateVerifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SafeKeyGenerator
{
  private final Pools.Pool<PoolableDigestContainer> digestPool = FactoryPools.threadSafe(10, new FactoryPools.Factory()
  {
    public SafeKeyGenerator.PoolableDigestContainer create()
    {
      try
      {
        SafeKeyGenerator.PoolableDigestContainer localPoolableDigestContainer = new SafeKeyGenerator.PoolableDigestContainer(MessageDigest.getInstance("SHA-256"));
        return localPoolableDigestContainer;
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        throw new RuntimeException(localNoSuchAlgorithmException);
      }
    }
  });
  private final LruCache<Key, String> loadIdToSafeHash = new LruCache(1000L);
  
  public SafeKeyGenerator() {}
  
  private String calculateHexStringDigest(Key paramKey)
  {
    PoolableDigestContainer localPoolableDigestContainer = (PoolableDigestContainer)Preconditions.checkNotNull(digestPool.acquire());
    try
    {
      paramKey.updateDiskCacheKey(messageDigest);
      paramKey = Util.sha256BytesToHex(messageDigest.digest());
      digestPool.release(localPoolableDigestContainer);
      return paramKey;
    }
    catch (Throwable paramKey)
    {
      digestPool.release(localPoolableDigestContainer);
      throw paramKey;
    }
  }
  
  /* Error */
  public String getSafeKey(Key paramKey)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 28	com/bumptech/glide/load/engine/cache/SafeKeyGenerator:loadIdToSafeHash	Lcom/bumptech/glide/util/LruCache;
    //   4: astore_2
    //   5: aload_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 28	com/bumptech/glide/load/engine/cache/SafeKeyGenerator:loadIdToSafeHash	Lcom/bumptech/glide/util/LruCache;
    //   11: aload_1
    //   12: invokevirtual 86	com/bumptech/glide/util/LruCache:put	(Ljava/lang/Object;)Ljava/lang/Object;
    //   15: checkcast 88	java/lang/String
    //   18: astore_3
    //   19: aload_2
    //   20: monitorexit
    //   21: aload_3
    //   22: astore_2
    //   23: aload_3
    //   24: ifnonnull +9 -> 33
    //   27: aload_0
    //   28: aload_1
    //   29: invokespecial 90	com/bumptech/glide/load/engine/cache/SafeKeyGenerator:calculateHexStringDigest	(Lcom/bumptech/glide/load/Key;)Ljava/lang/String;
    //   32: astore_2
    //   33: aload_0
    //   34: getfield 28	com/bumptech/glide/load/engine/cache/SafeKeyGenerator:loadIdToSafeHash	Lcom/bumptech/glide/util/LruCache;
    //   37: astore_3
    //   38: aload_3
    //   39: monitorenter
    //   40: aload_0
    //   41: getfield 28	com/bumptech/glide/load/engine/cache/SafeKeyGenerator:loadIdToSafeHash	Lcom/bumptech/glide/util/LruCache;
    //   44: aload_1
    //   45: aload_2
    //   46: invokevirtual 93	com/bumptech/glide/util/LruCache:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   49: pop
    //   50: aload_3
    //   51: monitorexit
    //   52: aload_2
    //   53: areturn
    //   54: astore_1
    //   55: aload_3
    //   56: monitorexit
    //   57: aload_1
    //   58: athrow
    //   59: astore_1
    //   60: aload_2
    //   61: monitorexit
    //   62: aload_1
    //   63: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	64	0	this	SafeKeyGenerator
    //   0	64	1	paramKey	Key
    //   4	57	2	localObject1	Object
    //   18	38	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   40	52	54	java/lang/Throwable
    //   55	57	54	java/lang/Throwable
    //   7	21	59	java/lang/Throwable
    //   60	62	59	java/lang/Throwable
  }
  
  private static final class PoolableDigestContainer
    implements FactoryPools.Poolable
  {
    final MessageDigest messageDigest;
    private final StateVerifier stateVerifier = StateVerifier.newInstance();
    
    PoolableDigestContainer(MessageDigest paramMessageDigest)
    {
      messageDigest = paramMessageDigest;
    }
    
    public StateVerifier getVerifier()
    {
      return stateVerifier;
    }
  }
}
