package com.bumptech.glide.load.engine;

import android.util.Log;
import androidx.core.util.Pools.Pool;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.data.DataRewinder;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.bumptech.glide.util.Preconditions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DecodePath<DataType, ResourceType, Transcode>
{
  private static final String PAGE_KEY = "DecodePath";
  private final Class<DataType> dataClass;
  private final List<? extends ResourceDecoder<DataType, ResourceType>> decoders;
  private final String failureMessage;
  private final Pools.Pool<List<Throwable>> listPool;
  private final ResourceTranscoder<ResourceType, Transcode> transcoder;
  
  public DecodePath(Class paramClass1, Class paramClass2, Class paramClass3, List paramList, ResourceTranscoder paramResourceTranscoder, Pools.Pool paramPool)
  {
    dataClass = paramClass1;
    decoders = paramList;
    transcoder = paramResourceTranscoder;
    listPool = paramPool;
    paramList = new StringBuilder();
    paramList.append("Failed DecodePath{");
    paramList.append(paramClass1.getSimpleName());
    paramList.append("->");
    paramList.append(paramClass2.getSimpleName());
    paramList.append("->");
    paramList.append(paramClass3.getSimpleName());
    paramList.append("}");
    failureMessage = paramList.toString();
  }
  
  private Resource decodeResource(DataRewinder paramDataRewinder, int paramInt1, int paramInt2, Options paramOptions)
    throws GlideException
  {
    List localList = (List)Preconditions.checkNotNull(listPool.acquire());
    try
    {
      paramDataRewinder = decodeResourceWithList(paramDataRewinder, paramInt1, paramInt2, paramOptions, localList);
      listPool.release(localList);
      return paramDataRewinder;
    }
    catch (Throwable paramDataRewinder)
    {
      listPool.release(localList);
      throw paramDataRewinder;
    }
  }
  
  private Resource decodeResourceWithList(DataRewinder paramDataRewinder, int paramInt1, int paramInt2, Options paramOptions, List paramList)
    throws GlideException
  {
    int j = decoders.size();
    Object localObject1 = null;
    int i = 0;
    Object localObject3;
    for (;;)
    {
      Object localObject2 = localObject1;
      if (i >= j) {
        break;
      }
      ResourceDecoder localResourceDecoder = (ResourceDecoder)decoders.get(i);
      try
      {
        boolean bool = localResourceDecoder.handles(paramDataRewinder.rewindAndGet(), paramOptions);
        localObject2 = localObject1;
        if (bool) {
          localObject2 = localResourceDecoder.decode(paramDataRewinder.rewindAndGet(), paramInt1, paramInt2, paramOptions);
        }
      }
      catch (IOException|RuntimeException|OutOfMemoryError localIOException)
      {
        if (Log.isLoggable("DecodePath", 2))
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("Failed to decode data for ");
          localStringBuilder.append(localResourceDecoder);
          Log.v("DecodePath", localStringBuilder.toString(), localIOException);
        }
        paramList.add(localIOException);
        localObject3 = localObject1;
      }
      if (localObject3 != null) {
        break;
      }
      i += 1;
      localObject1 = localObject3;
    }
    if (localObject3 != null) {
      return localObject3;
    }
    throw new GlideException(failureMessage, new ArrayList(paramList));
  }
  
  public Resource decode(DataRewinder paramDataRewinder, int paramInt1, int paramInt2, Options paramOptions, DecodeCallback paramDecodeCallback)
    throws GlideException
  {
    paramDataRewinder = paramDecodeCallback.onResourceDecoded(decodeResource(paramDataRewinder, paramInt1, paramInt2, paramOptions));
    return transcoder.transcode(paramDataRewinder, paramOptions);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("DecodePath{ dataClass=");
    localStringBuilder.append(dataClass);
    localStringBuilder.append(", decoders=");
    localStringBuilder.append(decoders);
    localStringBuilder.append(", transcoder=");
    localStringBuilder.append(transcoder);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  static abstract interface DecodeCallback<ResourceType>
  {
    public abstract Resource onResourceDecoded(Resource paramResource);
  }
}
