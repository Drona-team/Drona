package com.bumptech.glide;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.Util;
import java.util.List;
import java.util.Queue;

public class ListPreloader<T>
  implements AbsListView.OnScrollListener
{
  private boolean isIncreasing = true;
  private int lastEnd;
  private int lastFirstVisible = -1;
  private int lastStart;
  private final int maxPreload;
  private final PreloadSizeProvider<T> preloadDimensionProvider;
  private final PreloadModelProvider<T> preloadModelProvider;
  private final PreloadTargetQueue preloadTargetQueue;
  private final RequestManager requestManager;
  private int totalItemCount;
  
  public ListPreloader(RequestManager paramRequestManager, PreloadModelProvider paramPreloadModelProvider, PreloadSizeProvider paramPreloadSizeProvider, int paramInt)
  {
    requestManager = paramRequestManager;
    preloadModelProvider = paramPreloadModelProvider;
    preloadDimensionProvider = paramPreloadSizeProvider;
    maxPreload = paramInt;
    preloadTargetQueue = new PreloadTargetQueue(paramInt + 1);
  }
  
  private void cancelAll()
  {
    int i = 0;
    while (i < maxPreload)
    {
      requestManager.clear(preloadTargetQueue.next(0, 0));
      i += 1;
    }
  }
  
  private void preload(int paramInt1, int paramInt2)
  {
    if (paramInt1 < paramInt2)
    {
      i = Math.max(lastEnd, paramInt1);
      j = paramInt2;
    }
    else
    {
      j = Math.min(lastStart, paramInt1);
      i = paramInt2;
    }
    int j = Math.min(totalItemCount, j);
    int i = Math.min(totalItemCount, Math.max(0, i));
    if (paramInt1 < paramInt2)
    {
      paramInt1 = i;
      while (paramInt1 < j)
      {
        preloadAdapterPosition(preloadModelProvider.getPreloadItems(paramInt1), paramInt1, true);
        paramInt1 += 1;
      }
    }
    paramInt1 = j - 1;
    while (paramInt1 >= i)
    {
      preloadAdapterPosition(preloadModelProvider.getPreloadItems(paramInt1), paramInt1, false);
      paramInt1 -= 1;
    }
    lastStart = i;
    lastEnd = j;
  }
  
  private void preload(int paramInt, boolean paramBoolean)
  {
    if (isIncreasing != paramBoolean)
    {
      isIncreasing = paramBoolean;
      cancelAll();
    }
    int i;
    if (paramBoolean) {
      i = maxPreload;
    } else {
      i = -maxPreload;
    }
    preload(paramInt, i + paramInt);
  }
  
  private void preloadAdapterPosition(List paramList, int paramInt, boolean paramBoolean)
  {
    int j = paramList.size();
    if (paramBoolean)
    {
      i = 0;
      while (i < j)
      {
        preloadItem(paramList.get(i), paramInt, i);
        i += 1;
      }
    }
    int i = j - 1;
    while (i >= 0)
    {
      preloadItem(paramList.get(i), paramInt, i);
      i -= 1;
    }
  }
  
  private void preloadItem(Object paramObject, int paramInt1, int paramInt2)
  {
    if (paramObject == null) {
      return;
    }
    int[] arrayOfInt = preloadDimensionProvider.getPreloadSize(paramObject, paramInt1, paramInt2);
    if (arrayOfInt == null) {
      return;
    }
    paramObject = preloadModelProvider.getPreloadRequestBuilder(paramObject);
    if (paramObject == null) {
      return;
    }
    paramObject.into(preloadTargetQueue.next(arrayOfInt[0], arrayOfInt[1]));
  }
  
  public void onScroll(AbsListView paramAbsListView, int paramInt1, int paramInt2, int paramInt3)
  {
    totalItemCount = paramInt3;
    if (paramInt1 > lastFirstVisible) {
      preload(paramInt2 + paramInt1, true);
    } else if (paramInt1 < lastFirstVisible) {
      preload(paramInt1, false);
    }
    lastFirstVisible = paramInt1;
  }
  
  public void onScrollStateChanged(AbsListView paramAbsListView, int paramInt) {}
  
  public static abstract interface PreloadModelProvider<U>
  {
    public abstract List getPreloadItems(int paramInt);
    
    public abstract RequestBuilder getPreloadRequestBuilder(Object paramObject);
  }
  
  public static abstract interface PreloadSizeProvider<T>
  {
    public abstract int[] getPreloadSize(Object paramObject, int paramInt1, int paramInt2);
  }
  
  private static final class PreloadTarget
    extends BaseTarget<Object>
  {
    int photoHeight;
    int photoWidth;
    
    PreloadTarget() {}
    
    public void getSize(SizeReadyCallback paramSizeReadyCallback)
    {
      paramSizeReadyCallback.onSizeReady(photoWidth, photoHeight);
    }
    
    public void onResourceReady(Object paramObject, Transition paramTransition) {}
    
    public void removeCallback(SizeReadyCallback paramSizeReadyCallback) {}
  }
  
  private static final class PreloadTargetQueue
  {
    private final Queue<ListPreloader.PreloadTarget> queue;
    
    PreloadTargetQueue(int paramInt)
    {
      queue = Util.createQueue(paramInt);
      int i = 0;
      while (i < paramInt)
      {
        queue.offer(new ListPreloader.PreloadTarget());
        i += 1;
      }
    }
    
    public ListPreloader.PreloadTarget next(int paramInt1, int paramInt2)
    {
      ListPreloader.PreloadTarget localPreloadTarget = (ListPreloader.PreloadTarget)queue.poll();
      queue.offer(localPreloadTarget);
      photoWidth = paramInt1;
      photoHeight = paramInt2;
      return localPreloadTarget;
    }
  }
}