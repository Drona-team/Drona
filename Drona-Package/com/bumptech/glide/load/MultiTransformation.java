package com.bumptech.glide.load;

import android.content.Context;
import com.bumptech.glide.load.engine.Resource;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class MultiTransformation<T>
  implements Transformation<T>
{
  private final Collection<? extends Transformation<T>> transformations;
  
  public MultiTransformation(Collection paramCollection)
  {
    if (!paramCollection.isEmpty())
    {
      transformations = paramCollection;
      return;
    }
    throw new IllegalArgumentException("MultiTransformation must contain at least one Transformation");
  }
  
  public MultiTransformation(Transformation... paramVarArgs)
  {
    if (paramVarArgs.length != 0)
    {
      transformations = Arrays.asList(paramVarArgs);
      return;
    }
    throw new IllegalArgumentException("MultiTransformation must contain at least one Transformation");
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof MultiTransformation))
    {
      paramObject = (MultiTransformation)paramObject;
      return transformations.equals(transformations);
    }
    return false;
  }
  
  public int hashCode()
  {
    return transformations.hashCode();
  }
  
  public Resource transform(Context paramContext, Resource paramResource, int paramInt1, int paramInt2)
  {
    Iterator localIterator = transformations.iterator();
    Resource localResource;
    for (Object localObject = paramResource; localIterator.hasNext(); localObject = localResource)
    {
      localResource = ((Transformation)localIterator.next()).transform(paramContext, (Resource)localObject, paramInt1, paramInt2);
      if ((localObject != null) && (!localObject.equals(paramResource)) && (!localObject.equals(localResource))) {
        ((Resource)localObject).recycle();
      }
    }
    return localObject;
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest)
  {
    Iterator localIterator = transformations.iterator();
    while (localIterator.hasNext()) {
      ((Transformation)localIterator.next()).updateDiskCacheKey(paramMessageDigest);
    }
  }
}
