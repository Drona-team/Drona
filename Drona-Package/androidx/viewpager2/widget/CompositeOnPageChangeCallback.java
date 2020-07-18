package androidx.viewpager2.widget;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

final class CompositeOnPageChangeCallback
  extends ViewPager2.OnPageChangeCallback
{
  @NonNull
  private final List<ViewPager2.OnPageChangeCallback> mCallbacks;
  
  CompositeOnPageChangeCallback(int paramInt)
  {
    mCallbacks = new ArrayList(paramInt);
  }
  
  private void throwCallbackListModifiedWhileInUse(ConcurrentModificationException paramConcurrentModificationException)
  {
    throw new IllegalStateException("Adding and removing callbacks during dispatch to callbacks is not supported", paramConcurrentModificationException);
  }
  
  void addOnPageChangeCallback(ViewPager2.OnPageChangeCallback paramOnPageChangeCallback)
  {
    mCallbacks.add(paramOnPageChangeCallback);
  }
  
  public void onPageScrollStateChanged(int paramInt)
  {
    Object localObject1 = mCallbacks;
    try
    {
      localObject1 = ((List)localObject1).iterator();
      for (;;)
      {
        boolean bool = ((Iterator)localObject1).hasNext();
        if (!bool) {
          break;
        }
        Object localObject2 = ((Iterator)localObject1).next();
        localObject2 = (ViewPager2.OnPageChangeCallback)localObject2;
        ((ViewPager2.OnPageChangeCallback)localObject2).onPageScrollStateChanged(paramInt);
      }
      return;
    }
    catch (ConcurrentModificationException localConcurrentModificationException)
    {
      throwCallbackListModifiedWhileInUse(localConcurrentModificationException);
    }
  }
  
  public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
  {
    Object localObject1 = mCallbacks;
    try
    {
      localObject1 = ((List)localObject1).iterator();
      for (;;)
      {
        boolean bool = ((Iterator)localObject1).hasNext();
        if (!bool) {
          break;
        }
        Object localObject2 = ((Iterator)localObject1).next();
        localObject2 = (ViewPager2.OnPageChangeCallback)localObject2;
        ((ViewPager2.OnPageChangeCallback)localObject2).onPageScrolled(paramInt1, paramFloat, paramInt2);
      }
      return;
    }
    catch (ConcurrentModificationException localConcurrentModificationException)
    {
      throwCallbackListModifiedWhileInUse(localConcurrentModificationException);
    }
  }
  
  public void onPageSelected(int paramInt)
  {
    Object localObject1 = mCallbacks;
    try
    {
      localObject1 = ((List)localObject1).iterator();
      for (;;)
      {
        boolean bool = ((Iterator)localObject1).hasNext();
        if (!bool) {
          break;
        }
        Object localObject2 = ((Iterator)localObject1).next();
        localObject2 = (ViewPager2.OnPageChangeCallback)localObject2;
        ((ViewPager2.OnPageChangeCallback)localObject2).onPageSelected(paramInt);
      }
      return;
    }
    catch (ConcurrentModificationException localConcurrentModificationException)
    {
      throwCallbackListModifiedWhileInUse(localConcurrentModificationException);
    }
  }
  
  void removeOnPageChangeCallback(ViewPager2.OnPageChangeCallback paramOnPageChangeCallback)
  {
    mCallbacks.remove(paramOnPageChangeCallback);
  }
}
