package com.bumptech.glide.load.model.stream;

import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class BaseGlideUrlLoader<Model>
  implements ModelLoader<Model, InputStream>
{
  private final ModelLoader<GlideUrl, InputStream> concreteLoader;
  @Nullable
  private final ModelCache<Model, GlideUrl> modelCache;
  
  protected BaseGlideUrlLoader(ModelLoader paramModelLoader)
  {
    this(paramModelLoader, null);
  }
  
  protected BaseGlideUrlLoader(ModelLoader paramModelLoader, ModelCache paramModelCache)
  {
    concreteLoader = paramModelLoader;
    modelCache = paramModelCache;
  }
  
  private static List getAlternateKeys(Collection paramCollection)
  {
    ArrayList localArrayList = new ArrayList(paramCollection.size());
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext()) {
      localArrayList.add(new GlideUrl((String)paramCollection.next()));
    }
    return localArrayList;
  }
  
  public ModelLoader.LoadData buildLoadData(Object paramObject, int paramInt1, int paramInt2, Options paramOptions)
  {
    Object localObject1;
    if (modelCache != null) {
      localObject1 = (GlideUrl)modelCache.put(paramObject, paramInt1, paramInt2);
    } else {
      localObject1 = null;
    }
    Object localObject2 = localObject1;
    if (localObject1 == null)
    {
      localObject1 = getUrl(paramObject, paramInt1, paramInt2, paramOptions);
      if (TextUtils.isEmpty((CharSequence)localObject1)) {
        return null;
      }
      localObject1 = new GlideUrl((String)localObject1, getHeaders(paramObject, paramInt1, paramInt2, paramOptions));
      localObject2 = localObject1;
      if (modelCache != null)
      {
        modelCache.put(paramObject, paramInt1, paramInt2, localObject1);
        localObject2 = localObject1;
      }
    }
    paramObject = getAlternateUrls(paramObject, paramInt1, paramInt2, paramOptions);
    paramOptions = concreteLoader.buildLoadData(localObject2, paramInt1, paramInt2, paramOptions);
    if (paramOptions != null)
    {
      if (paramObject.isEmpty()) {
        return paramOptions;
      }
      return new ModelLoader.LoadData(sourceKey, getAlternateKeys(paramObject), fetcher);
    }
    return paramOptions;
  }
  
  protected List getAlternateUrls(Object paramObject, int paramInt1, int paramInt2, Options paramOptions)
  {
    return Collections.emptyList();
  }
  
  protected Headers getHeaders(Object paramObject, int paramInt1, int paramInt2, Options paramOptions)
  {
    return Headers.DEFAULT;
  }
  
  protected abstract String getUrl(Object paramObject, int paramInt1, int paramInt2, Options paramOptions);
}
