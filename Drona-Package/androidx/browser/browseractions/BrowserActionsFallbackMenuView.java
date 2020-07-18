package androidx.browser.browseractions;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import androidx.annotation.RestrictTo;
import androidx.browser.R.dimen;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class BrowserActionsFallbackMenuView
  extends LinearLayout
{
  private final int mBrowserActionsMenuMaxWidthPx = getResources().getDimensionPixelOffset(R.dimen.browser_actions_context_menu_max_width);
  private final int mBrowserActionsMenuMinPaddingPx = getResources().getDimensionPixelOffset(R.dimen.browser_actions_context_menu_min_padding);
  
  public BrowserActionsFallbackMenuView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(getResourcesgetDisplayMetricswidthPixels - mBrowserActionsMenuMinPaddingPx * 2, mBrowserActionsMenuMaxWidthPx), 1073741824), paramInt2);
  }
}
