package com.bumptech.glide;

import androidx.core.util.Pools.Pool;
import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.data.DataRewinder;
import com.bumptech.glide.load.data.DataRewinder.Factory;
import com.bumptech.glide.load.data.DataRewinderRegistry;
import com.bumptech.glide.load.engine.DecodePath;
import com.bumptech.glide.load.engine.LoadPath;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.ModelLoaderRegistry;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.bumptech.glide.load.resource.transcode.TranscoderRegistry;
import com.bumptech.glide.provider.EncoderRegistry;
import com.bumptech.glide.provider.ImageHeaderParserRegistry;
import com.bumptech.glide.provider.LoadPathCache;
import com.bumptech.glide.provider.ModelToResourceClassCache;
import com.bumptech.glide.provider.ResourceDecoderRegistry;
import com.bumptech.glide.provider.ResourceEncoderRegistry;
import com.bumptech.glide.util.pool.FactoryPools;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Registry
{
  private static final String BUCKET_APPEND_ALL = "legacy_append";
  public static final String BUCKET_BITMAP = "Bitmap";
  public static final String BUCKET_BITMAP_DRAWABLE = "BitmapDrawable";
  public static final String BUCKET_GIF = "Gif";
  private static final String BUCKET_PREPEND_ALL = "legacy_prepend_all";
  private final DataRewinderRegistry dataRewinderRegistry = new DataRewinderRegistry();
  private final ResourceDecoderRegistry decoderRegistry = new ResourceDecoderRegistry();
  private final EncoderRegistry encoderRegistry = new EncoderRegistry();
  private final ImageHeaderParserRegistry imageHeaderParserRegistry = new ImageHeaderParserRegistry();
  private final LoadPathCache loadPathCache = new LoadPathCache();
  private final ModelLoaderRegistry modelLoaderRegistry = new ModelLoaderRegistry(throwableListPool);
  private final ModelToResourceClassCache modelToResourceClassCache = new ModelToResourceClassCache();
  private final ResourceEncoderRegistry resourceEncoderRegistry = new ResourceEncoderRegistry();
  private final Pools.Pool<List<Throwable>> throwableListPool = FactoryPools.threadSafeList();
  private final TranscoderRegistry transcoderRegistry = new TranscoderRegistry();
  
  public Registry()
  {
    setResourceDecoderBucketPriorityList(Arrays.asList(new String[] { "Gif", "Bitmap", "BitmapDrawable" }));
  }
  
  private List getDecodePaths(Class paramClass1, Class paramClass2, Class paramClass3)
  {
    ArrayList localArrayList = new ArrayList();
    paramClass2 = decoderRegistry.getResourceClasses(paramClass1, paramClass2).iterator();
    while (paramClass2.hasNext())
    {
      Class localClass1 = (Class)paramClass2.next();
      Iterator localIterator = transcoderRegistry.getTranscodeClasses(localClass1, paramClass3).iterator();
      while (localIterator.hasNext())
      {
        Class localClass2 = (Class)localIterator.next();
        localArrayList.add(new DecodePath(paramClass1, localClass1, localClass2, decoderRegistry.getDecoders(paramClass1, localClass1), transcoderRegistry.get(localClass1, localClass2), throwableListPool));
      }
    }
    return localArrayList;
  }
  
  public Registry append(Class paramClass, Encoder paramEncoder)
  {
    encoderRegistry.append(paramClass, paramEncoder);
    return this;
  }
  
  public Registry append(Class paramClass, ResourceEncoder paramResourceEncoder)
  {
    resourceEncoderRegistry.append(paramClass, paramResourceEncoder);
    return this;
  }
  
  public Registry append(Class paramClass1, Class paramClass2, ResourceDecoder paramResourceDecoder)
  {
    append("legacy_append", paramClass1, paramClass2, paramResourceDecoder);
    return this;
  }
  
  public Registry append(Class paramClass1, Class paramClass2, ModelLoaderFactory paramModelLoaderFactory)
  {
    modelLoaderRegistry.append(paramClass1, paramClass2, paramModelLoaderFactory);
    return this;
  }
  
  public Registry append(String paramString, Class paramClass1, Class paramClass2, ResourceDecoder paramResourceDecoder)
  {
    decoderRegistry.append(paramString, paramResourceDecoder, paramClass1, paramClass2);
    return this;
  }
  
  public List getImageHeaderParsers()
  {
    List localList = imageHeaderParserRegistry.getParsers();
    if (!localList.isEmpty()) {
      return localList;
    }
    throw new NoImageHeaderParserException();
  }
  
  public LoadPath getLoadPath(Class paramClass1, Class paramClass2, Class paramClass3)
  {
    LoadPath localLoadPath = loadPathCache.resolve(paramClass1, paramClass2, paramClass3);
    if (loadPathCache.isEmptyLoadPath(localLoadPath)) {
      return null;
    }
    Object localObject = localLoadPath;
    if (localLoadPath == null)
    {
      localObject = getDecodePaths(paramClass1, paramClass2, paramClass3);
      if (((List)localObject).isEmpty()) {
        localObject = null;
      } else {
        localObject = new LoadPath(paramClass1, paramClass2, paramClass3, (List)localObject, throwableListPool);
      }
      loadPathCache.putAll(paramClass1, paramClass2, paramClass3, (LoadPath)localObject);
    }
    return localObject;
  }
  
  public List getModelLoaders(Object paramObject)
  {
    List localList = modelLoaderRegistry.getModelLoaders(paramObject);
    if (!localList.isEmpty()) {
      return localList;
    }
    throw new NoModelLoaderAvailableException(paramObject);
  }
  
  public List getRegisteredResourceClasses(Class paramClass1, Class paramClass2, Class paramClass3)
  {
    Object localObject1 = modelToResourceClassCache.get(paramClass1, paramClass2, paramClass3);
    if (localObject1 == null)
    {
      localObject1 = new ArrayList();
      Iterator localIterator = modelLoaderRegistry.getDataClasses(paramClass1).iterator();
      while (localIterator.hasNext())
      {
        Object localObject2 = (Class)localIterator.next();
        localObject2 = decoderRegistry.getResourceClasses((Class)localObject2, paramClass2).iterator();
        while (((Iterator)localObject2).hasNext())
        {
          Class localClass = (Class)((Iterator)localObject2).next();
          if ((!transcoderRegistry.getTranscodeClasses(localClass, paramClass3).isEmpty()) && (!((List)localObject1).contains(localClass))) {
            ((List)localObject1).add(localClass);
          }
        }
      }
      modelToResourceClassCache.putAll(paramClass1, paramClass2, paramClass3, Collections.unmodifiableList((List)localObject1));
      return localObject1;
    }
    return localObject1;
  }
  
  public ResourceEncoder getResultEncoder(Resource paramResource)
    throws Registry.NoResultEncoderAvailableException
  {
    ResourceEncoder localResourceEncoder = resourceEncoderRegistry.decode(paramResource.getResourceClass());
    if (localResourceEncoder != null) {
      return localResourceEncoder;
    }
    throw new NoResultEncoderAvailableException(paramResource.getResourceClass());
  }
  
  public DataRewinder getRewinder(Object paramObject)
  {
    return dataRewinderRegistry.build(paramObject);
  }
  
  public Encoder getSourceEncoder(Object paramObject)
    throws Registry.NoSourceEncoderAvailableException
  {
    Encoder localEncoder = encoderRegistry.getEncoder(paramObject.getClass());
    if (localEncoder != null) {
      return localEncoder;
    }
    throw new NoSourceEncoderAvailableException(paramObject.getClass());
  }
  
  public boolean isResourceEncoderAvailable(Resource paramResource)
  {
    return resourceEncoderRegistry.decode(paramResource.getResourceClass()) != null;
  }
  
  public Registry prepend(Class paramClass, Encoder paramEncoder)
  {
    encoderRegistry.prepend(paramClass, paramEncoder);
    return this;
  }
  
  public Registry prepend(Class paramClass, ResourceEncoder paramResourceEncoder)
  {
    resourceEncoderRegistry.prepend(paramClass, paramResourceEncoder);
    return this;
  }
  
  public Registry prepend(Class paramClass1, Class paramClass2, ResourceDecoder paramResourceDecoder)
  {
    prepend("legacy_prepend_all", paramClass1, paramClass2, paramResourceDecoder);
    return this;
  }
  
  public Registry prepend(Class paramClass1, Class paramClass2, ModelLoaderFactory paramModelLoaderFactory)
  {
    modelLoaderRegistry.prepend(paramClass1, paramClass2, paramModelLoaderFactory);
    return this;
  }
  
  public Registry prepend(String paramString, Class paramClass1, Class paramClass2, ResourceDecoder paramResourceDecoder)
  {
    decoderRegistry.prepend(paramString, paramResourceDecoder, paramClass1, paramClass2);
    return this;
  }
  
  public Registry register(ImageHeaderParser paramImageHeaderParser)
  {
    imageHeaderParserRegistry.sendPacket(paramImageHeaderParser);
    return this;
  }
  
  public Registry register(DataRewinder.Factory paramFactory)
  {
    dataRewinderRegistry.register(paramFactory);
    return this;
  }
  
  public Registry register(Class paramClass, Encoder paramEncoder)
  {
    return append(paramClass, paramEncoder);
  }
  
  public Registry register(Class paramClass, ResourceEncoder paramResourceEncoder)
  {
    return append(paramClass, paramResourceEncoder);
  }
  
  public Registry register(Class paramClass1, Class paramClass2, ResourceTranscoder paramResourceTranscoder)
  {
    transcoderRegistry.register(paramClass1, paramClass2, paramResourceTranscoder);
    return this;
  }
  
  public Registry replace(Class paramClass1, Class paramClass2, ModelLoaderFactory paramModelLoaderFactory)
  {
    modelLoaderRegistry.replace(paramClass1, paramClass2, paramModelLoaderFactory);
    return this;
  }
  
  public final Registry setResourceDecoderBucketPriorityList(List paramList)
  {
    ArrayList localArrayList = new ArrayList(paramList.size());
    localArrayList.addAll(paramList);
    localArrayList.add(0, "legacy_prepend_all");
    localArrayList.add("legacy_append");
    decoderRegistry.setBucketPriorityList(localArrayList);
    return this;
  }
  
  public static class MissingComponentException
    extends RuntimeException
  {
    public MissingComponentException(String paramString)
    {
      super();
    }
  }
  
  public static final class NoImageHeaderParserException
    extends Registry.MissingComponentException
  {
    public NoImageHeaderParserException()
    {
      super();
    }
  }
  
  public static class NoModelLoaderAvailableException
    extends Registry.MissingComponentException
  {
    public NoModelLoaderAvailableException(Class paramClass1, Class paramClass2)
    {
      super();
    }
    
    public NoModelLoaderAvailableException(Object paramObject)
    {
      super();
    }
  }
  
  public static class NoResultEncoderAvailableException
    extends Registry.MissingComponentException
  {
    public NoResultEncoderAvailableException(Class paramClass)
    {
      super();
    }
  }
  
  public static class NoSourceEncoderAvailableException
    extends Registry.MissingComponentException
  {
    public NoSourceEncoderAvailableException(Class paramClass)
    {
      super();
    }
  }
}
