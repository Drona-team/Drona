package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.Preconditions;

class EngineResource<Z>
  implements Resource<Z>
{
  private int acquired;
  private final boolean isCacheable;
  private final boolean isRecyclable;
  private boolean isRecycled;
  private Key key;
  private ResourceListener listener;
  private final Resource<Z> resource;
  
  EngineResource(Resource paramResource, boolean paramBoolean1, boolean paramBoolean2)
  {
    resource = ((Resource)Preconditions.checkNotNull(paramResource));
    isCacheable = paramBoolean1;
    isRecyclable = paramBoolean2;
  }
  
  void acquire()
  {
    try
    {
      if (!isRecycled)
      {
        acquired += 1;
        return;
      }
      throw new IllegalStateException("Cannot acquire a recycled resource");
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public Object get()
  {
    return resource.get();
  }
  
  Resource getResource()
  {
    return resource;
  }
  
  public Class getResourceClass()
  {
    return resource.getResourceClass();
  }
  
  public int getSize()
  {
    return resource.getSize();
  }
  
  boolean isCacheable()
  {
    return isCacheable;
  }
  
  public void recycle()
  {
    try
    {
      if (acquired <= 0)
      {
        if (!isRecycled)
        {
          isRecycled = true;
          if (isRecyclable) {
            resource.recycle();
          }
          return;
        }
        throw new IllegalStateException("Cannot recycle a resource that has already been recycled");
      }
      throw new IllegalStateException("Cannot recycle a resource while it is still acquired");
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  /* Error */
  void release()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 80	com/bumptech/glide/load/engine/EngineResource:listener	Lcom/bumptech/glide/load/engine/EngineResource$ResourceListener;
    //   4: astore_2
    //   5: aload_2
    //   6: monitorenter
    //   7: aload_0
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 48	com/bumptech/glide/load/engine/EngineResource:acquired	I
    //   13: ifle +38 -> 51
    //   16: aload_0
    //   17: getfield 48	com/bumptech/glide/load/engine/EngineResource:acquired	I
    //   20: iconst_1
    //   21: isub
    //   22: istore_1
    //   23: aload_0
    //   24: iload_1
    //   25: putfield 48	com/bumptech/glide/load/engine/EngineResource:acquired	I
    //   28: iload_1
    //   29: ifne +17 -> 46
    //   32: aload_0
    //   33: getfield 80	com/bumptech/glide/load/engine/EngineResource:listener	Lcom/bumptech/glide/load/engine/EngineResource$ResourceListener;
    //   36: aload_0
    //   37: getfield 82	com/bumptech/glide/load/engine/EngineResource:key	Lcom/bumptech/glide/load/Key;
    //   40: aload_0
    //   41: invokeinterface 86 3 0
    //   46: aload_0
    //   47: monitorexit
    //   48: aload_2
    //   49: monitorexit
    //   50: return
    //   51: new 50	java/lang/IllegalStateException
    //   54: dup
    //   55: ldc 88
    //   57: invokespecial 55	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   60: athrow
    //   61: astore_3
    //   62: aload_0
    //   63: monitorexit
    //   64: aload_3
    //   65: athrow
    //   66: astore_3
    //   67: aload_2
    //   68: monitorexit
    //   69: aload_3
    //   70: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	71	0	this	EngineResource
    //   22	7	1	i	int
    //   4	64	2	localResourceListener	ResourceListener
    //   61	4	3	localThrowable1	Throwable
    //   66	4	3	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   9	28	61	java/lang/Throwable
    //   32	46	61	java/lang/Throwable
    //   46	48	61	java/lang/Throwable
    //   51	61	61	java/lang/Throwable
    //   62	64	61	java/lang/Throwable
    //   7	9	66	java/lang/Throwable
    //   48	50	66	java/lang/Throwable
    //   64	66	66	java/lang/Throwable
    //   67	69	66	java/lang/Throwable
  }
  
  void setResourceListener(Key paramKey, ResourceListener paramResourceListener)
  {
    try
    {
      key = paramKey;
      listener = paramResourceListener;
      return;
    }
    catch (Throwable paramKey)
    {
      throw paramKey;
    }
  }
  
  public String toString()
  {
    try
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("EngineResource{isCacheable=");
      ((StringBuilder)localObject).append(isCacheable);
      ((StringBuilder)localObject).append(", listener=");
      ((StringBuilder)localObject).append(listener);
      ((StringBuilder)localObject).append(", key=");
      ((StringBuilder)localObject).append(key);
      ((StringBuilder)localObject).append(", acquired=");
      ((StringBuilder)localObject).append(acquired);
      ((StringBuilder)localObject).append(", isRecycled=");
      ((StringBuilder)localObject).append(isRecycled);
      ((StringBuilder)localObject).append(", resource=");
      ((StringBuilder)localObject).append(resource);
      ((StringBuilder)localObject).append('}');
      localObject = ((StringBuilder)localObject).toString();
      return localObject;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  static abstract interface ResourceListener
  {
    public abstract void onResourceReleased(Key paramKey, EngineResource paramEngineResource);
  }
}
