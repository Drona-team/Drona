package com.facebook.react.views.imagehelper;

import androidx.annotation.Nullable;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import java.util.Iterator;
import java.util.List;

public class MultiSourceHelper
{
  public MultiSourceHelper() {}
  
  public static MultiSourceResult getBestSourceForSize(int paramInt1, int paramInt2, List paramList)
  {
    return getBestSourceForSize(paramInt1, paramInt2, paramList, 1.0D);
  }
  
  public static MultiSourceResult getBestSourceForSize(int paramInt1, int paramInt2, List paramList, double paramDouble)
  {
    if (paramList.isEmpty()) {
      return new MultiSourceResult(null, null, null);
    }
    if (paramList.size() == 1) {
      return new MultiSourceResult((ImageSource)paramList.get(0), null, null);
    }
    if ((paramInt1 > 0) && (paramInt2 > 0))
    {
      ImagePipeline localImagePipeline = ImagePipelineFactory.getInstance().getImagePipeline();
      double d5 = paramInt1 * paramInt2;
      Iterator localIterator = paramList.iterator();
      double d2 = Double.MAX_VALUE;
      double d1 = Double.MAX_VALUE;
      paramList = null;
      Object localObject1 = null;
      while (localIterator.hasNext())
      {
        localObject2 = (ImageSource)localIterator.next();
        double d3 = Math.abs(1.0D - ((ImageSource)localObject2).getSize() / (d5 * paramDouble));
        double d4 = d2;
        Object localObject3 = localObject1;
        if (d3 < d2)
        {
          localObject3 = localObject2;
          d4 = d3;
        }
        d2 = d4;
        localObject1 = localObject3;
        if (d3 < d1) {
          if (!localImagePipeline.isInBitmapMemoryCache(((ImageSource)localObject2).getUri()))
          {
            d2 = d4;
            localObject1 = localObject3;
            if (!localImagePipeline.isInDiskCacheSync(((ImageSource)localObject2).getUri())) {}
          }
          else
          {
            paramList = (List)localObject2;
            d1 = d3;
            d2 = d4;
            localObject1 = localObject3;
          }
        }
      }
      Object localObject2 = paramList;
      if (paramList != null)
      {
        localObject2 = paramList;
        if (localObject1 != null)
        {
          localObject2 = paramList;
          if (paramList.getSource().equals(localObject1.getSource())) {
            localObject2 = null;
          }
        }
      }
      return new MultiSourceResult(localObject1, (ImageSource)localObject2, null);
    }
    return new MultiSourceResult(null, null, null);
  }
  
  public static class MultiSourceResult
  {
    @Nullable
    private final ImageSource bestResult;
    @Nullable
    private final ImageSource bestResultInCache;
    
    private MultiSourceResult(ImageSource paramImageSource1, ImageSource paramImageSource2)
    {
      bestResult = paramImageSource1;
      bestResultInCache = paramImageSource2;
    }
    
    public ImageSource getBestResult()
    {
      return bestResult;
    }
    
    public ImageSource getBestResultInCache()
    {
      return bestResultInCache;
    }
  }
}