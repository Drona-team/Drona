package com.bumptech.glide.load.engine;

import com.bumptech.glide.GlideContext;
import com.bumptech.glide.Priority;
import com.bumptech.glide.Registry;
import com.bumptech.glide.Registry.NoModelLoaderAvailableException;
import com.bumptech.glide.Registry.NoSourceEncoderAvailableException;
import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.load.resource.UnitTransformation;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

final class DecodeHelper<Transcode>
{
  private final List<Key> cacheKeys = new ArrayList();
  private DecodeJob.DiskCacheProvider diskCacheProvider;
  private DiskCacheStrategy diskCacheStrategy;
  private GlideContext glideContext;
  private int height;
  private boolean isCacheKeysSet;
  private boolean isLoadDataSet;
  private boolean isScaleOnlyOrNoTransform;
  private boolean isTransformationRequired;
  private final List<ModelLoader.LoadData<?>> loadData = new ArrayList();
  private Object model;
  private Options options;
  private Priority priority;
  private Class<?> resourceClass;
  private Key signature;
  private Class<Transcode> transcodeClass;
  private Map<Class<?>, Transformation<?>> transformations;
  private int width;
  
  DecodeHelper() {}
  
  void clear()
  {
    glideContext = null;
    model = null;
    signature = null;
    resourceClass = null;
    transcodeClass = null;
    options = null;
    priority = null;
    transformations = null;
    diskCacheStrategy = null;
    loadData.clear();
    isLoadDataSet = false;
    cacheKeys.clear();
    isCacheKeysSet = false;
  }
  
  ArrayPool getArrayPool()
  {
    return glideContext.getArrayPool();
  }
  
  List getCacheKeys()
  {
    if (!isCacheKeysSet)
    {
      isCacheKeysSet = true;
      cacheKeys.clear();
      List localList = getLoadData();
      int k = localList.size();
      int i = 0;
      while (i < k)
      {
        ModelLoader.LoadData localLoadData = (ModelLoader.LoadData)localList.get(i);
        if (!cacheKeys.contains(sourceKey)) {
          cacheKeys.add(sourceKey);
        }
        int j = 0;
        while (j < alternateKeys.size())
        {
          if (!cacheKeys.contains(alternateKeys.get(j))) {
            cacheKeys.add(alternateKeys.get(j));
          }
          j += 1;
        }
        i += 1;
      }
    }
    return cacheKeys;
  }
  
  DiskCache getDiskCache()
  {
    return diskCacheProvider.getDiskCache();
  }
  
  DiskCacheStrategy getDiskCacheStrategy()
  {
    return diskCacheStrategy;
  }
  
  int getHeight()
  {
    return height;
  }
  
  List getLoadData()
  {
    if (!isLoadDataSet)
    {
      isLoadDataSet = true;
      loadData.clear();
      List localList = glideContext.getRegistry().getModelLoaders(model);
      int i = 0;
      int j = localList.size();
      while (i < j)
      {
        ModelLoader.LoadData localLoadData = ((ModelLoader)localList.get(i)).buildLoadData(model, width, height, options);
        if (localLoadData != null) {
          loadData.add(localLoadData);
        }
        i += 1;
      }
    }
    return loadData;
  }
  
  LoadPath getLoadPath(Class paramClass)
  {
    return glideContext.getRegistry().getLoadPath(paramClass, resourceClass, transcodeClass);
  }
  
  Class getModelClass()
  {
    return model.getClass();
  }
  
  List getModelLoaders(File paramFile)
    throws Registry.NoModelLoaderAvailableException
  {
    return glideContext.getRegistry().getModelLoaders(paramFile);
  }
  
  Options getOptions()
  {
    return options;
  }
  
  Priority getPriority()
  {
    return priority;
  }
  
  List getRegisteredResourceClasses()
  {
    return glideContext.getRegistry().getRegisteredResourceClasses(model.getClass(), resourceClass, transcodeClass);
  }
  
  ResourceEncoder getResultEncoder(Resource paramResource)
  {
    return glideContext.getRegistry().getResultEncoder(paramResource);
  }
  
  Key getSignature()
  {
    return signature;
  }
  
  Encoder getSourceEncoder(Object paramObject)
    throws Registry.NoSourceEncoderAvailableException
  {
    return glideContext.getRegistry().getSourceEncoder(paramObject);
  }
  
  Class getTranscodeClass()
  {
    return transcodeClass;
  }
  
  Transformation getTransformation(Class paramClass)
  {
    Object localObject1 = transformations;
    Object localObject3 = this;
    Object localObject2 = (Transformation)((Map)localObject1).get(paramClass);
    localObject1 = localObject2;
    if (localObject2 == null)
    {
      localObject3 = transformations.entrySet().iterator();
      do
      {
        localObject1 = localObject2;
        if (!((Iterator)localObject3).hasNext()) {
          break;
        }
        localObject1 = (Map.Entry)((Iterator)localObject3).next();
      } while (!((Class)((Map.Entry)localObject1).getKey()).isAssignableFrom(paramClass));
      localObject1 = (Transformation)((Map.Entry)localObject1).getValue();
    }
    localObject2 = this;
    if (localObject1 == null)
    {
      if ((transformations.isEmpty()) && (isTransformationRequired))
      {
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("Missing transformation for ");
        ((StringBuilder)localObject1).append(paramClass);
        ((StringBuilder)localObject1).append(". If you wish to ignore unknown resource types, use the optional transformation methods.");
        throw new IllegalArgumentException(((StringBuilder)localObject1).toString());
      }
      return UnitTransformation.get();
    }
    return localObject1;
  }
  
  int getWidth()
  {
    return width;
  }
  
  boolean hasLoadPath(Class paramClass)
  {
    return getLoadPath(paramClass) != null;
  }
  
  void init(GlideContext paramGlideContext, Object paramObject, Key paramKey, int paramInt1, int paramInt2, DiskCacheStrategy paramDiskCacheStrategy, Class paramClass1, Class paramClass2, Priority paramPriority, Options paramOptions, Map paramMap, boolean paramBoolean1, boolean paramBoolean2, DecodeJob.DiskCacheProvider paramDiskCacheProvider)
  {
    glideContext = paramGlideContext;
    model = paramObject;
    signature = paramKey;
    width = paramInt1;
    height = paramInt2;
    diskCacheStrategy = paramDiskCacheStrategy;
    resourceClass = paramClass1;
    diskCacheProvider = paramDiskCacheProvider;
    transcodeClass = paramClass2;
    priority = paramPriority;
    options = paramOptions;
    transformations = paramMap;
    isTransformationRequired = paramBoolean1;
    isScaleOnlyOrNoTransform = paramBoolean2;
  }
  
  boolean isResourceEncoderAvailable(Resource paramResource)
  {
    return glideContext.getRegistry().isResourceEncoderAvailable(paramResource);
  }
  
  boolean isScaleOnlyOrNoTransform()
  {
    return isScaleOnlyOrNoTransform;
  }
  
  boolean isSourceKey(Key paramKey)
  {
    List localList = getLoadData();
    int j = localList.size();
    int i = 0;
    while (i < j)
    {
      if (getsourceKey.equals(paramKey)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
}
