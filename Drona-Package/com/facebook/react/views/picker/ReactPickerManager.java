package com.facebook.react.views.picker;

import android.view.View;
import android.widget.Spinner;
import androidx.appcompat.widget.AppCompatSpinner;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.BaseViewManager;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.views.picker.events.PickerItemSelectEvent;

public abstract class ReactPickerManager
  extends SimpleViewManager<ReactPicker>
{
  public ReactPickerManager() {}
  
  protected void addEventEmitters(ThemedReactContext paramThemedReactContext, ReactPicker paramReactPicker)
  {
    paramReactPicker.setOnSelectListener(new PickerEventEmitter(paramReactPicker, ((UIManagerModule)paramThemedReactContext.getNativeModule(UIManagerModule.class)).getEventDispatcher()));
  }
  
  protected void onAfterUpdateTransaction(ReactPicker paramReactPicker)
  {
    super.onAfterUpdateTransaction(paramReactPicker);
    paramReactPicker.commitStagedData();
  }
  
  public void setColor(ReactPicker paramReactPicker, Integer paramInteger)
  {
    paramReactPicker.setStagedPrimaryTextColor(paramInteger);
  }
  
  public void setEnabled(ReactPicker paramReactPicker, boolean paramBoolean)
  {
    paramReactPicker.setEnabled(paramBoolean);
  }
  
  public void setItems(ReactPicker paramReactPicker, ReadableArray paramReadableArray)
  {
    paramReactPicker.setStagedItems(ReactPickerItem.createFromJsArrayMap(paramReadableArray));
  }
  
  public void setPrompt(ReactPicker paramReactPicker, String paramString)
  {
    paramReactPicker.setPrompt(paramString);
  }
  
  public void setSelected(ReactPicker paramReactPicker, int paramInt)
  {
    paramReactPicker.setStagedSelection(paramInt);
  }
  
  private static class PickerEventEmitter
    implements ReactPicker.OnSelectListener
  {
    private final EventDispatcher mEventDispatcher;
    private final ReactPicker mReactPicker;
    
    public PickerEventEmitter(ReactPicker paramReactPicker, EventDispatcher paramEventDispatcher)
    {
      mReactPicker = paramReactPicker;
      mEventDispatcher = paramEventDispatcher;
    }
    
    public void onItemSelected(int paramInt)
    {
      mEventDispatcher.dispatchEvent(new PickerItemSelectEvent(mReactPicker.getId(), paramInt));
    }
  }
}
