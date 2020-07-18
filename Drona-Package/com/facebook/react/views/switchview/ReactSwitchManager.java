package com.facebook.react.views.switchview;

import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.ReactShadowNodeImpl;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.yoga.YogaMeasureFunction;
import com.facebook.yoga.YogaMeasureMode;
import com.facebook.yoga.YogaMeasureOutput;
import com.facebook.yoga.YogaNode;

public class ReactSwitchManager
  extends SimpleViewManager<ReactSwitch>
{
  private static final CompoundButton.OnCheckedChangeListener ON_CHECKED_CHANGE_LISTENER = new CompoundButton.OnCheckedChangeListener()
  {
    public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
    {
      ((UIManagerModule)((ReactContext)paramAnonymousCompoundButton.getContext()).getNativeModule(UIManagerModule.class)).getEventDispatcher().dispatchEvent(new ReactSwitchEvent(paramAnonymousCompoundButton.getId(), paramAnonymousBoolean));
    }
  };
  public static final String REACT_CLASS = "AndroidSwitch";
  
  public ReactSwitchManager() {}
  
  protected void addEventEmitters(ThemedReactContext paramThemedReactContext, ReactSwitch paramReactSwitch)
  {
    paramReactSwitch.setOnCheckedChangeListener(ON_CHECKED_CHANGE_LISTENER);
  }
  
  public LayoutShadowNode createShadowNodeInstance()
  {
    return new ReactSwitchShadowNode(null);
  }
  
  protected ReactSwitch createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    paramThemedReactContext = new ReactSwitch(paramThemedReactContext);
    paramThemedReactContext.setShowText(false);
    return paramThemedReactContext;
  }
  
  public String getName()
  {
    return "AndroidSwitch";
  }
  
  public Class getShadowNodeClass()
  {
    return ReactSwitchShadowNode.class;
  }
  
  public void setDisabled(ReactSwitch paramReactSwitch, boolean paramBoolean)
  {
    paramReactSwitch.setEnabled(paramBoolean ^ true);
  }
  
  public void setEnabled(ReactSwitch paramReactSwitch, boolean paramBoolean)
  {
    paramReactSwitch.setEnabled(paramBoolean);
  }
  
  public void setOn(ReactSwitch paramReactSwitch, boolean paramBoolean)
  {
    setValue(paramReactSwitch, paramBoolean);
  }
  
  public void setThumbColor(ReactSwitch paramReactSwitch, Integer paramInteger)
  {
    paramReactSwitch.setThumbColor(paramInteger);
  }
  
  public void setThumbTintColor(ReactSwitch paramReactSwitch, Integer paramInteger)
  {
    setThumbColor(paramReactSwitch, paramInteger);
  }
  
  public void setTrackColorForFalse(ReactSwitch paramReactSwitch, Integer paramInteger)
  {
    paramReactSwitch.setTrackColorForFalse(paramInteger);
  }
  
  public void setTrackColorForTrue(ReactSwitch paramReactSwitch, Integer paramInteger)
  {
    paramReactSwitch.setTrackColorForTrue(paramInteger);
  }
  
  public void setTrackTintColor(ReactSwitch paramReactSwitch, Integer paramInteger)
  {
    paramReactSwitch.setTrackColor(paramInteger);
  }
  
  public void setValue(ReactSwitch paramReactSwitch, boolean paramBoolean)
  {
    paramReactSwitch.setOnCheckedChangeListener(null);
    paramReactSwitch.setOn(paramBoolean);
    paramReactSwitch.setOnCheckedChangeListener(ON_CHECKED_CHANGE_LISTENER);
  }
  
  static class ReactSwitchShadowNode
    extends LayoutShadowNode
    implements YogaMeasureFunction
  {
    private int mHeight;
    private boolean mMeasured;
    private int mWidth;
    
    private ReactSwitchShadowNode()
    {
      initMeasureFunction();
    }
    
    private void initMeasureFunction()
    {
      setMeasureFunction(this);
    }
    
    public long measure(YogaNode paramYogaNode, float paramFloat1, YogaMeasureMode paramYogaMeasureMode1, float paramFloat2, YogaMeasureMode paramYogaMeasureMode2)
    {
      if (!mMeasured)
      {
        paramYogaNode = new ReactSwitch(getThemedContext());
        paramYogaNode.setShowText(false);
        int i = View.MeasureSpec.makeMeasureSpec(0, 0);
        paramYogaNode.measure(i, i);
        mWidth = paramYogaNode.getMeasuredWidth();
        mHeight = paramYogaNode.getMeasuredHeight();
        mMeasured = true;
      }
      return YogaMeasureOutput.make(mWidth, mHeight);
    }
  }
}