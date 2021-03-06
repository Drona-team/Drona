package com.facebook.react.views.picker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;
import java.util.List;

class ReactPickerAdapter
  extends ArrayAdapter<ReactPickerItem>
{
  private final LayoutInflater mInflater;
  @Nullable
  private Integer mPrimaryTextColor;
  
  public ReactPickerAdapter(Context paramContext, List paramList)
  {
    super(paramContext, 0, paramList);
    mInflater = ((LayoutInflater)Assertions.assertNotNull(paramContext.getSystemService("layout_inflater")));
  }
  
  private View getView(int paramInt, View paramView, ViewGroup paramViewGroup, boolean paramBoolean)
  {
    ReactPickerItem localReactPickerItem = (ReactPickerItem)getItem(paramInt);
    paramInt = 0;
    View localView = paramView;
    if (paramView == null)
    {
      if (paramBoolean) {
        paramInt = 17367049;
      } else {
        paramInt = 17367048;
      }
      paramView = mInflater.inflate(paramInt, paramViewGroup, false);
      localView = paramView;
      paramView.setTag(((TextView)paramView).getTextColors());
      paramInt = 1;
    }
    paramView = (TextView)localView;
    paramView.setText(label);
    if ((!paramBoolean) && (mPrimaryTextColor != null))
    {
      paramView.setTextColor(mPrimaryTextColor.intValue());
      return paramView;
    }
    if (color != null)
    {
      paramView.setTextColor(color.intValue());
      return paramView;
    }
    if ((paramView.getTag() != null) && (paramInt == 0)) {
      paramView.setTextColor((ColorStateList)paramView.getTag());
    }
    return paramView;
  }
  
  public View getDropDownView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    return getView(paramInt, paramView, paramViewGroup, true);
  }
  
  public Integer getPrimaryTextColor()
  {
    return mPrimaryTextColor;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    return getView(paramInt, paramView, paramViewGroup, false);
  }
  
  public void setPrimaryTextColor(Integer paramInteger)
  {
    mPrimaryTextColor = paramInteger;
    notifyDataSetChanged();
  }
}
