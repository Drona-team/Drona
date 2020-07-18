package androidx.lifecycle;

import androidx.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class ViewModel
{
  @Nullable
  private final Map<String, Object> mBagOfTags = new HashMap();
  private volatile boolean mCleared = false;
  
  public ViewModel() {}
  
  private static void closeWithRuntimeException(Object paramObject)
  {
    if ((paramObject instanceof Closeable))
    {
      paramObject = (Closeable)paramObject;
      try
      {
        paramObject.close();
        return;
      }
      catch (IOException paramObject)
      {
        throw new RuntimeException(paramObject);
      }
    }
  }
  
  final void clear()
  {
    mCleared = true;
    if (mBagOfTags != null)
    {
      Map localMap = mBagOfTags;
      try
      {
        Iterator localIterator = mBagOfTags.values().iterator();
        while (localIterator.hasNext()) {
          closeWithRuntimeException(localIterator.next());
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    onCleared();
  }
  
  Object getTag(String paramString)
  {
    if (mBagOfTags == null) {
      return null;
    }
    Map localMap = mBagOfTags;
    try
    {
      paramString = mBagOfTags.get(paramString);
      return paramString;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  protected void onCleared() {}
  
  Object setTagIfAbsent(String paramString, Object paramObject)
  {
    Map localMap = mBagOfTags;
    try
    {
      Object localObject = mBagOfTags.get(paramString);
      if (localObject == null) {
        mBagOfTags.put(paramString, paramObject);
      }
      if (localObject == null) {
        paramString = paramObject;
      } else {
        paramString = localObject;
      }
      if (mCleared)
      {
        closeWithRuntimeException(paramString);
        return paramString;
      }
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
    return paramString;
  }
}
