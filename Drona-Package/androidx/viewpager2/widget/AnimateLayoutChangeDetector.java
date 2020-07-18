package androidx.viewpager2.widget;

import I;
import android.animation.LayoutTransition;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;

final class AnimateLayoutChangeDetector
{
  private static final ViewGroup.MarginLayoutParams ZERO_MARGIN_LAYOUT_PARAMS = new ViewGroup.MarginLayoutParams(-1, -1);
  private LinearLayoutManager mLayoutManager;
  
  static
  {
    ZERO_MARGIN_LAYOUT_PARAMS.setMargins(0, 0, 0, 0);
  }
  
  AnimateLayoutChangeDetector(LinearLayoutManager paramLinearLayoutManager)
  {
    mLayoutManager = paramLinearLayoutManager;
  }
  
  private boolean arePagesLaidOutContiguously()
  {
    int m = mLayoutManager.getChildCount();
    if (m == 0) {
      return true;
    }
    if (mLayoutManager.getOrientation() == 0) {
      i = 1;
    } else {
      i = 0;
    }
    int[][] arrayOfInt = (int[][])Array.newInstance(I.class, new int[] { m, 2 });
    int j = 0;
    while (j < m)
    {
      View localView = mLayoutManager.getChildAt(j);
      if (localView != null)
      {
        Object localObject = localView.getLayoutParams();
        if ((localObject instanceof ViewGroup.MarginLayoutParams)) {
          localObject = (ViewGroup.MarginLayoutParams)localObject;
        } else {
          localObject = ZERO_MARGIN_LAYOUT_PARAMS;
        }
        int[] arrayOfInt1 = arrayOfInt[j];
        int k;
        if (i != 0) {
          k = localView.getLeft() - leftMargin;
        } else {
          k = localView.getTop() - topMargin;
        }
        arrayOfInt1[0] = k;
        arrayOfInt1 = arrayOfInt[j];
        if (i != 0) {
          k = localView.getRight() + rightMargin;
        } else {
          k = localView.getBottom() + bottomMargin;
        }
        arrayOfInt1[1] = k;
        j += 1;
      }
      else
      {
        throw new IllegalStateException("null view contained in the view hierarchy");
      }
    }
    Arrays.sort(arrayOfInt, new Comparator()
    {
      public int compare(int[] paramAnonymousArrayOfInt1, int[] paramAnonymousArrayOfInt2)
      {
        return paramAnonymousArrayOfInt1[0] - paramAnonymousArrayOfInt2[0];
      }
    });
    int i = 1;
    while (i < m)
    {
      if (arrayOfInt[(i - 1)][1] != arrayOfInt[i][0]) {
        return false;
      }
      i += 1;
    }
    i = arrayOfInt[0][1];
    j = arrayOfInt[0][0];
    if (arrayOfInt[0][0] <= 0) {
      return arrayOfInt[(m - 1)][1] >= i - j;
    }
    return false;
  }
  
  private boolean hasRunningChangingLayoutTransition()
  {
    int j = mLayoutManager.getChildCount();
    int i = 0;
    while (i < j)
    {
      if (hasRunningChangingLayoutTransition(mLayoutManager.getChildAt(i))) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private static boolean hasRunningChangingLayoutTransition(View paramView)
  {
    if ((paramView instanceof ViewGroup))
    {
      paramView = (ViewGroup)paramView;
      LayoutTransition localLayoutTransition = paramView.getLayoutTransition();
      if ((localLayoutTransition != null) && (localLayoutTransition.isChangingLayout())) {
        return true;
      }
      int j = paramView.getChildCount();
      int i = 0;
      while (i < j)
      {
        if (hasRunningChangingLayoutTransition(paramView.getChildAt(i))) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  boolean mayHaveInterferingAnimations()
  {
    return ((!arePagesLaidOutContiguously()) || (mLayoutManager.getChildCount() <= 1)) && (hasRunningChangingLayoutTransition());
  }
}
