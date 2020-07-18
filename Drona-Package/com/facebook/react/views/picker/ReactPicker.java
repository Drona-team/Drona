package com.facebook.react.views.picker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AbsSpinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import java.util.List;

public class ReactPicker
  extends AppCompatSpinner
{
  private final AdapterView.OnItemSelectedListener mItemSelectedListener = new AdapterView.OnItemSelectedListener()
  {
    public void onItemSelected(AdapterView paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      if (mOnSelectListener != null) {
        mOnSelectListener.onItemSelected(paramAnonymousInt);
      }
    }
    
    public void onNothingSelected(AdapterView paramAnonymousAdapterView)
    {
      if (mOnSelectListener != null) {
        mOnSelectListener.onItemSelected(-1);
      }
    }
  };
  @Nullable
  private List<ReactPickerItem> mItems;
  private int mMode = 0;
  @Nullable
  private OnSelectListener mOnSelectListener;
  @Nullable
  private List<ReactPickerItem> mStagedItems;
  @Nullable
  private Integer mStagedPrimaryTextColor;
  @Nullable
  private Integer mStagedSelection;
  private final Runnable measureAndLayout = new Runnable()
  {
    public void run()
    {
      measure(View.MeasureSpec.makeMeasureSpec(getWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(getHeight(), 1073741824));
      layout(getLeft(), getTop(), getRight(), getBottom());
    }
  };
  
  public ReactPicker(Context paramContext)
  {
    super(paramContext);
  }
  
  public ReactPicker(Context paramContext, int paramInt)
  {
    super(paramContext, paramInt);
    mMode = paramInt;
  }
  
  public ReactPicker(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public ReactPicker(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public ReactPicker(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    mMode = paramInt2;
  }
  
  void commitStagedData()
  {
    setOnItemSelectedListener(null);
    ReactPickerAdapter localReactPickerAdapter2 = (ReactPickerAdapter)getAdapter();
    int i = getSelectedItemPosition();
    ReactPickerAdapter localReactPickerAdapter1 = localReactPickerAdapter2;
    if (mStagedItems != null)
    {
      localReactPickerAdapter1 = localReactPickerAdapter2;
      if (mStagedItems != mItems)
      {
        mItems = mStagedItems;
        mStagedItems = null;
        if (localReactPickerAdapter2 == null)
        {
          localReactPickerAdapter1 = new ReactPickerAdapter(getContext(), mItems);
          setAdapter(localReactPickerAdapter1);
        }
        else
        {
          localReactPickerAdapter2.clear();
          localReactPickerAdapter2.addAll(mItems);
          localReactPickerAdapter2.notifyDataSetChanged();
          localReactPickerAdapter1 = localReactPickerAdapter2;
        }
      }
    }
    if ((mStagedSelection != null) && (mStagedSelection.intValue() != i))
    {
      setSelection(mStagedSelection.intValue(), false);
      mStagedSelection = null;
    }
    if ((mStagedPrimaryTextColor != null) && (localReactPickerAdapter1 != null) && (mStagedPrimaryTextColor != localReactPickerAdapter1.getPrimaryTextColor()))
    {
      localReactPickerAdapter1.setPrimaryTextColor(mStagedPrimaryTextColor);
      mStagedPrimaryTextColor = null;
    }
    setOnItemSelectedListener(mItemSelectedListener);
  }
  
  public int getMode()
  {
    return mMode;
  }
  
  public OnSelectListener getOnSelectListener()
  {
    return mOnSelectListener;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if (getOnItemSelectedListener() == null) {
      setOnItemSelectedListener(mItemSelectedListener);
    }
  }
  
  public void requestLayout()
  {
    super.requestLayout();
    post(measureAndLayout);
  }
  
  public void setOnSelectListener(OnSelectListener paramOnSelectListener)
  {
    mOnSelectListener = paramOnSelectListener;
  }
  
  void setStagedItems(List paramList)
  {
    mStagedItems = paramList;
  }
  
  void setStagedPrimaryTextColor(Integer paramInteger)
  {
    mStagedPrimaryTextColor = paramInteger;
  }
  
  void setStagedSelection(int paramInt)
  {
    mStagedSelection = Integer.valueOf(paramInt);
  }
  
  public static abstract interface OnSelectListener
  {
    public abstract void onItemSelected(int paramInt);
  }
}
