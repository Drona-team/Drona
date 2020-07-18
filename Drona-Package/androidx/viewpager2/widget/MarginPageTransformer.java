package androidx.viewpager2.widget;

import android.view.View;
import android.view.ViewParent;
import androidx.core.util.Preconditions;
import androidx.recyclerview.widget.RecyclerView;

public final class MarginPageTransformer
  implements ViewPager2.PageTransformer
{
  private final int mMarginPx;
  
  public MarginPageTransformer(int paramInt)
  {
    Preconditions.checkArgumentNonnegative(paramInt, "Margin must be non-negative");
    mMarginPx = paramInt;
  }
  
  private ViewPager2 requireViewPager(View paramView)
  {
    paramView = paramView.getParent();
    ViewParent localViewParent = paramView.getParent();
    if (((paramView instanceof RecyclerView)) && ((localViewParent instanceof ViewPager2))) {
      return (ViewPager2)localViewParent;
    }
    throw new IllegalStateException("Expected the page view to be managed by a ViewPager2 instance.");
  }
  
  public void transformPage(View paramView, float paramFloat)
  {
    ViewPager2 localViewPager2 = requireViewPager(paramView);
    float f = mMarginPx * paramFloat;
    if (localViewPager2.getOrientation() == 0)
    {
      paramFloat = f;
      if (localViewPager2.isRtl()) {
        paramFloat = -f;
      }
      paramView.setTranslationX(paramFloat);
      return;
    }
    paramView.setTranslationY(f);
  }
}
