package com.bumptech.glide.load.engine;

import androidx.core.util.Pools.Pool;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataRewinder;
import com.bumptech.glide.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadPath<Data, ResourceType, Transcode>
{
  private final Class<Data> dataClass;
  private final List<? extends DecodePath<Data, ResourceType, Transcode>> decodePaths;
  private final String failureMessage;
  private final Pools.Pool<List<Throwable>> listPool;
  
  public LoadPath(Class paramClass1, Class paramClass2, Class paramClass3, List paramList, Pools.Pool paramPool)
  {
    dataClass = paramClass1;
    listPool = paramPool;
    decodePaths = ((List)Preconditions.checkNotEmpty(paramList));
    paramList = new StringBuilder();
    paramList.append("Failed LoadPath{");
    paramList.append(paramClass1.getSimpleName());
    paramList.append("->");
    paramList.append(paramClass2.getSimpleName());
    paramList.append("->");
    paramList.append(paramClass3.getSimpleName());
    paramList.append("}");
    failureMessage = paramList.toString();
  }
  
  private Resource loadWithExceptionList(DataRewinder paramDataRewinder, Options paramOptions, int paramInt1, int paramInt2, DecodePath.DecodeCallback paramDecodeCallback, List paramList)
    throws GlideException
  {
    int j = decodePaths.size();
    int i = 0;
    Object localObject1 = null;
    Object localObject3;
    for (;;)
    {
      Object localObject2 = localObject1;
      if (i >= j) {
        break;
      }
      localObject2 = (DecodePath)decodePaths.get(i);
      try
      {
        localObject2 = ((DecodePath)localObject2).decode(paramDataRewinder, paramInt1, paramInt2, paramOptions, paramDecodeCallback);
        localObject1 = localObject2;
      }
      catch (GlideException localGlideException)
      {
        paramList.add(localGlideException);
      }
      if (localObject1 != null)
      {
        localObject3 = localObject1;
        break;
      }
      i += 1;
    }
    if (localObject3 != null) {
      return localObject3;
    }
    throw new GlideException(failureMessage, new ArrayList(paramList));
  }
  
  public Class getDataClass()
  {
    return dataClass;
  }
  
  public Resource load(DataRewinder paramDataRewinder, Options paramOptions, int paramInt1, int paramInt2, DecodePath.DecodeCallback paramDecodeCallback)
    throws GlideException
  {
    List localList = (List)Preconditions.checkNotNull(listPool.acquire());
    try
    {
      paramDataRewinder = loadWithExceptionList(paramDataRewinder, paramOptions, paramInt1, paramInt2, paramDecodeCallback, localList);
      listPool.release(localList);
      return paramDataRewinder;
    }
    catch (Throwable paramDataRewinder)
    {
      listPool.release(localList);
      throw paramDataRewinder;
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("LoadPath{decodePaths=");
    localStringBuilder.append(Arrays.toString(decodePaths.toArray()));
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}
