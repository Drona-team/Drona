package androidx.viewpager2.widget;

import android.view.View;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class CompositePageTransformer
  implements ViewPager2.PageTransformer
{
  private final List<ViewPager2.PageTransformer> mTransformers = new ArrayList();
  
  public CompositePageTransformer() {}
  
  public void addTransformer(ViewPager2.PageTransformer paramPageTransformer)
  {
    mTransformers.add(paramPageTransformer);
  }
  
  public void removeTransformer(ViewPager2.PageTransformer paramPageTransformer)
  {
    mTransformers.remove(paramPageTransformer);
  }
  
  public void transformPage(View paramView, float paramFloat)
  {
    Iterator localIterator = mTransformers.iterator();
    while (localIterator.hasNext()) {
      ((ViewPager2.PageTransformer)localIterator.next()).transformPage(paramView, paramFloat);
    }
  }
}
