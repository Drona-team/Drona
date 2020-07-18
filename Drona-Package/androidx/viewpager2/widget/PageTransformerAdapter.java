package androidx.viewpager2.widget;

import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import java.util.Locale;

final class PageTransformerAdapter
  extends ViewPager2.OnPageChangeCallback
{
  private final LinearLayoutManager mLayoutManager;
  private ViewPager2.PageTransformer mPageTransformer;
  
  PageTransformerAdapter(LinearLayoutManager paramLinearLayoutManager)
  {
    mLayoutManager = paramLinearLayoutManager;
  }
  
  ViewPager2.PageTransformer getPageTransformer()
  {
    return mPageTransformer;
  }
  
  public void onPageScrollStateChanged(int paramInt) {}
  
  public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
  {
    if (mPageTransformer == null) {
      return;
    }
    paramFloat = -paramFloat;
    paramInt2 = 0;
    while (paramInt2 < mLayoutManager.getChildCount())
    {
      View localView = mLayoutManager.getChildAt(paramInt2);
      if (localView != null)
      {
        float f = mLayoutManager.getPosition(localView) - paramInt1;
        mPageTransformer.transformPage(localView, f + paramFloat);
        paramInt2 += 1;
      }
      else
      {
        throw new IllegalStateException(String.format(Locale.US, "LayoutManager returned a null child at pos %d/%d while transforming pages", new Object[] { Integer.valueOf(paramInt2), Integer.valueOf(mLayoutManager.getChildCount()) }));
      }
    }
  }
  
  public void onPageSelected(int paramInt) {}
  
  void setPageTransformer(ViewPager2.PageTransformer paramPageTransformer)
  {
    mPageTransformer = paramPageTransformer;
  }
}
