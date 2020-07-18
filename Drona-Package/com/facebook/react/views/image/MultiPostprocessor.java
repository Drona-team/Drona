package com.facebook.react.views.image;

import android.graphics.Bitmap;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.MultiCacheKey;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.request.Postprocessor;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MultiPostprocessor
  implements Postprocessor
{
  private final List<Postprocessor> mPostprocessors;
  
  private MultiPostprocessor(List paramList)
  {
    mPostprocessors = new LinkedList(paramList);
  }
  
  public static Postprocessor from(List paramList)
  {
    switch (paramList.size())
    {
    default: 
      return new MultiPostprocessor(paramList);
    case 1: 
      return (Postprocessor)paramList.get(0);
    }
    return null;
  }
  
  public String getName()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = mPostprocessors.iterator();
    while (localIterator.hasNext())
    {
      Postprocessor localPostprocessor = (Postprocessor)localIterator.next();
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append(localPostprocessor.getName());
    }
    localStringBuilder.insert(0, "MultiPostProcessor (");
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
  
  public CacheKey getPostprocessorCacheKey()
  {
    LinkedList localLinkedList = new LinkedList();
    Iterator localIterator = mPostprocessors.iterator();
    while (localIterator.hasNext()) {
      localLinkedList.push(((Postprocessor)localIterator.next()).getPostprocessorCacheKey());
    }
    return new MultiCacheKey(localLinkedList);
  }
  
  public CloseableReference process(Bitmap paramBitmap, PlatformBitmapFactory paramPlatformBitmapFactory)
  {
    Object localObject = null;
    CloseableReference localCloseableReference1 = null;
    try
    {
      Iterator localIterator = mPostprocessors.iterator();
      CloseableReference localCloseableReference2 = null;
      for (;;)
      {
        localObject = localCloseableReference1;
        boolean bool = localIterator.hasNext();
        if (bool)
        {
          localObject = localCloseableReference1;
          Postprocessor localPostprocessor = (Postprocessor)localIterator.next();
          Bitmap localBitmap;
          if (localCloseableReference2 != null)
          {
            localObject = localCloseableReference1;
            localBitmap = (Bitmap)localCloseableReference2.get();
          }
          else
          {
            localBitmap = paramBitmap;
          }
          localObject = localCloseableReference1;
          localCloseableReference1 = localPostprocessor.process(localBitmap, paramPlatformBitmapFactory);
          try
          {
            CloseableReference.closeSafely(localCloseableReference2);
            localCloseableReference2 = localCloseableReference1.clone();
          }
          catch (Throwable paramBitmap)
          {
            localObject = localCloseableReference1;
            break label134;
          }
        }
      }
      localObject = localCloseableReference1;
      paramBitmap = localCloseableReference1.clone();
      CloseableReference.closeSafely(localCloseableReference1);
      return paramBitmap;
    }
    catch (Throwable paramBitmap)
    {
      label134:
      CloseableReference.closeSafely(localObject);
      throw paramBitmap;
    }
  }
}
