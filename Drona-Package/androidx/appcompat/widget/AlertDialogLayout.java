package androidx.appcompat.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import androidx.annotation.RestrictTo;
import androidx.appcompat.R.id;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public class AlertDialogLayout
  extends LinearLayoutCompat
{
  public AlertDialogLayout(Context paramContext)
  {
    super(paramContext);
  }
  
  public AlertDialogLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void forceUniformWidth(int paramInt1, int paramInt2)
  {
    int j = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
    int i = 0;
    while (i < paramInt1)
    {
      View localView = getChildAt(i);
      if (localView.getVisibility() != 8)
      {
        LinearLayoutCompat.LayoutParams localLayoutParams = (LinearLayoutCompat.LayoutParams)localView.getLayoutParams();
        if (width == -1)
        {
          int k = height;
          height = localView.getMeasuredHeight();
          measureChildWithMargins(localView, j, 0, paramInt2, 0);
          height = k;
        }
      }
      i += 1;
    }
  }
  
  private static int resolveMinimumHeight(View paramView)
  {
    int i = ViewCompat.getMinimumHeight(paramView);
    if (i > 0) {
      return i;
    }
    if ((paramView instanceof ViewGroup))
    {
      paramView = (ViewGroup)paramView;
      if (paramView.getChildCount() == 1) {
        return resolveMinimumHeight(paramView.getChildAt(0));
      }
    }
    return 0;
  }
  
  private void setChildFrame(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramView.layout(paramInt1, paramInt2, paramInt3 + paramInt1, paramInt4 + paramInt2);
  }
  
  private boolean tryOnMeasure(int paramInt1, int paramInt2)
  {
    int i5 = getChildCount();
    Object localObject3 = null;
    Object localObject2 = null;
    Object localObject1 = null;
    int i = 0;
    View localView;
    while (i < i5)
    {
      localView = getChildAt(i);
      if (localView.getVisibility() != 8)
      {
        j = localView.getId();
        if (j == R.id.topPanel)
        {
          localObject3 = localView;
        }
        else if (j == R.id.buttonPanel)
        {
          localObject2 = localView;
        }
        else
        {
          if ((j != R.id.contentPanel) && (j != R.id.customPanel)) {
            return false;
          }
          if (localObject1 != null) {
            return false;
          }
          localObject1 = localView;
        }
      }
      i += 1;
    }
    int i7 = View.MeasureSpec.getMode(paramInt2);
    int n = View.MeasureSpec.getSize(paramInt2);
    int i6 = View.MeasureSpec.getMode(paramInt1);
    int k = getPaddingTop() + getPaddingBottom();
    if (localObject3 != null)
    {
      localObject3.measure(paramInt1, 0);
      k += localObject3.getMeasuredHeight();
      j = View.combineMeasuredStates(0, localObject3.getMeasuredState());
    }
    else
    {
      j = 0;
    }
    int i1;
    if (localObject2 != null)
    {
      localObject2.measure(paramInt1, 0);
      m = resolveMinimumHeight(localObject2);
      i = m;
      i1 = localObject2.getMeasuredHeight() - m;
      k += m;
      j = View.combineMeasuredStates(j, localObject2.getMeasuredState());
    }
    else
    {
      i = 0;
      i1 = 0;
    }
    int i2;
    if (localObject1 != null)
    {
      if (i7 == 0) {
        m = 0;
      } else {
        m = View.MeasureSpec.makeMeasureSpec(Math.max(0, n - k), i7);
      }
      localObject1.measure(paramInt1, m);
      m = localObject1.getMeasuredHeight();
      i2 = m;
      k += m;
      j = View.combineMeasuredStates(j, localObject1.getMeasuredState());
    }
    else
    {
      i2 = 0;
    }
    int i4 = n - k;
    n = k;
    int m = j;
    int i3 = i4;
    if (localObject2 != null)
    {
      i1 = Math.min(i4, i1);
      m = i4;
      n = i;
      if (i1 > 0)
      {
        m = i4 - i1;
        n = i + i1;
      }
      localObject2.measure(paramInt1, View.MeasureSpec.makeMeasureSpec(n, 1073741824));
      n = k - i + localObject2.getMeasuredHeight();
      i = View.combineMeasuredStates(j, localObject2.getMeasuredState());
      i3 = m;
      m = i;
    }
    int j = n;
    i = m;
    if (localObject1 != null)
    {
      j = n;
      i = m;
      if (i3 > 0)
      {
        localObject1.measure(paramInt1, View.MeasureSpec.makeMeasureSpec(i2 + i3, i7));
        j = n - i2 + localObject1.getMeasuredHeight();
        i = View.combineMeasuredStates(m, localObject1.getMeasuredState());
      }
    }
    k = 0;
    for (m = 0; k < i5; m = n)
    {
      localView = getChildAt(k);
      n = m;
      if (localView.getVisibility() != 8) {
        n = Math.max(m, localView.getMeasuredWidth());
      }
      k += 1;
    }
    setMeasuredDimension(View.resolveSizeAndState(m + (getPaddingLeft() + getPaddingRight()), paramInt1, i), View.resolveSizeAndState(j, paramInt2, 0));
    if (i6 != 1073741824) {
      forceUniformWidth(i5, paramInt2);
    }
    return true;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int j = getPaddingLeft();
    int k = paramInt3 - paramInt1;
    int m = getPaddingRight();
    int n = getPaddingRight();
    paramInt1 = getMeasuredHeight();
    int i1 = getChildCount();
    int i2 = getGravity();
    paramInt3 = i2 & 0x70;
    if (paramInt3 != 16)
    {
      if (paramInt3 != 80) {
        paramInt1 = getPaddingTop();
      } else {
        paramInt1 = getPaddingTop() + paramInt4 - paramInt2 - paramInt1;
      }
    }
    else
    {
      paramInt3 = getPaddingTop();
      paramInt1 = (paramInt4 - paramInt2 - paramInt1) / 2 + paramInt3;
    }
    Object localObject = getDividerDrawable();
    if (localObject == null) {
      paramInt3 = 0;
    } else {
      paramInt3 = ((Drawable)localObject).getIntrinsicHeight();
    }
    paramInt4 = 0;
    while (paramInt4 < i1)
    {
      localObject = getChildAt(paramInt4);
      paramInt2 = paramInt1;
      if (localObject != null)
      {
        paramInt2 = paramInt1;
        if (((View)localObject).getVisibility() != 8)
        {
          int i3 = ((View)localObject).getMeasuredWidth();
          int i4 = ((View)localObject).getMeasuredHeight();
          LinearLayoutCompat.LayoutParams localLayoutParams = (LinearLayoutCompat.LayoutParams)((View)localObject).getLayoutParams();
          int i = gravity;
          paramInt2 = i;
          if (i < 0) {
            paramInt2 = i2 & 0x800007;
          }
          paramInt2 = GravityCompat.getAbsoluteGravity(paramInt2, ViewCompat.getLayoutDirection(this)) & 0x7;
          if (paramInt2 != 1)
          {
            if (paramInt2 != 5) {
              paramInt2 = leftMargin + j;
            } else {
              paramInt2 = k - m - i3 - rightMargin;
            }
          }
          else {
            paramInt2 = (k - j - n - i3) / 2 + j + leftMargin - rightMargin;
          }
          i = paramInt1;
          if (hasDividerBeforeChildAt(paramInt4)) {
            i = paramInt1 + paramInt3;
          }
          paramInt1 = i + topMargin;
          setChildFrame((View)localObject, paramInt2, paramInt1, i3, i4);
          paramInt2 = paramInt1 + (i4 + bottomMargin);
        }
      }
      paramInt4 += 1;
      paramInt1 = paramInt2;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (!tryOnMeasure(paramInt1, paramInt2)) {
      super.onMeasure(paramInt1, paramInt2);
    }
  }
}