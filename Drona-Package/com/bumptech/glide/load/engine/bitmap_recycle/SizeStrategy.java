package com.bumptech.glide.load.engine.bitmap_recycle;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;
import com.bumptech.glide.util.Util;
import java.util.NavigableMap;

@RequiresApi(19)
final class SizeStrategy
  implements LruPoolStrategy
{
  private static final int MAX_SIZE_MULTIPLE = 8;
  private final GroupedLinkedMap<Key, Bitmap> groupedMap = new GroupedLinkedMap();
  private final KeyPool keyPool = new KeyPool();
  private final NavigableMap<Integer, Integer> sortedSizes = new PrettyPrintTreeMap();
  
  SizeStrategy() {}
  
  private void decrementBitmapOfSize(Integer paramInteger)
  {
    Integer localInteger = (Integer)sortedSizes.get(paramInteger);
    if (localInteger.intValue() == 1)
    {
      sortedSizes.remove(paramInteger);
      return;
    }
    sortedSizes.put(paramInteger, Integer.valueOf(localInteger.intValue() - 1));
  }
  
  static String getBitmapString(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("[");
    localStringBuilder.append(paramInt);
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  private static String getBitmapString(Bitmap paramBitmap)
  {
    return getBitmapString(Util.getBitmapByteSize(paramBitmap));
  }
  
  public Bitmap get(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    int i = Util.getBitmapByteSize(paramInt1, paramInt2, paramConfig);
    Key localKey = keyPool.get(i);
    Object localObject1 = localKey;
    Integer localInteger = (Integer)sortedSizes.ceilingKey(Integer.valueOf(i));
    Object localObject2 = localObject1;
    if (localInteger != null)
    {
      localObject2 = localObject1;
      if (localInteger.intValue() != i)
      {
        localObject2 = localObject1;
        if (localInteger.intValue() <= i * 8)
        {
          keyPool.offer(localKey);
          localObject2 = keyPool.get(localInteger.intValue());
        }
      }
    }
    localObject1 = (Bitmap)groupedMap.get((Poolable)localObject2);
    if (localObject1 != null)
    {
      ((Bitmap)localObject1).reconfigure(paramInt1, paramInt2, paramConfig);
      decrementBitmapOfSize(localInteger);
    }
    return localObject1;
  }
  
  public int getSize(Bitmap paramBitmap)
  {
    return Util.getBitmapByteSize(paramBitmap);
  }
  
  public String logBitmap(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    return getBitmapString(Util.getBitmapByteSize(paramInt1, paramInt2, paramConfig));
  }
  
  public String logBitmap(Bitmap paramBitmap)
  {
    return getBitmapString(paramBitmap);
  }
  
  public void put(Bitmap paramBitmap)
  {
    int i = Util.getBitmapByteSize(paramBitmap);
    Key localKey = keyPool.get(i);
    groupedMap.put(localKey, paramBitmap);
    paramBitmap = (Integer)sortedSizes.get(Integer.valueOf(size));
    NavigableMap localNavigableMap = sortedSizes;
    int j = size;
    i = 1;
    if (paramBitmap != null) {
      i = 1 + paramBitmap.intValue();
    }
    localNavigableMap.put(Integer.valueOf(j), Integer.valueOf(i));
  }
  
  public Bitmap removeLast()
  {
    Bitmap localBitmap = (Bitmap)groupedMap.removeLast();
    if (localBitmap != null) {
      decrementBitmapOfSize(Integer.valueOf(Util.getBitmapByteSize(localBitmap)));
    }
    return localBitmap;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("SizeStrategy:\n  ");
    localStringBuilder.append(groupedMap);
    localStringBuilder.append("\n  SortedSizes");
    localStringBuilder.append(sortedSizes);
    return localStringBuilder.toString();
  }
  
  @VisibleForTesting
  static final class Key
    implements Poolable
  {
    private final SizeStrategy.KeyPool pool;
    int size;
    
    Key(SizeStrategy.KeyPool paramKeyPool)
    {
      pool = paramKeyPool;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof Key))
      {
        paramObject = (Key)paramObject;
        if (size == size) {
          return true;
        }
      }
      return false;
    }
    
    public int hashCode()
    {
      return size;
    }
    
    public void init(int paramInt)
    {
      size = paramInt;
    }
    
    public void offer()
    {
      pool.offer(this);
    }
    
    public String toString()
    {
      return SizeStrategy.getBitmapString(size);
    }
  }
  
  @VisibleForTesting
  static class KeyPool
    extends BaseKeyPool<SizeStrategy.Key>
  {
    KeyPool() {}
    
    protected SizeStrategy.Key create()
    {
      return new SizeStrategy.Key(this);
    }
    
    public SizeStrategy.Key get(int paramInt)
    {
      SizeStrategy.Key localKey = (SizeStrategy.Key)super.get();
      localKey.init(paramInt);
      return localKey;
    }
  }
}
