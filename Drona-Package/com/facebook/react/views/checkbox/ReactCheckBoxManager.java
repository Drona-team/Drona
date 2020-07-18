package com.facebook.react.views.checkbox;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.util.TypedValue;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import androidx.appcompat.widget.TintContextWrapper;
import androidx.core.widget.CompoundButtonCompat;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.EventDispatcher;

public class ReactCheckBoxManager
  extends SimpleViewManager<ReactCheckBox>
{
  private static final CompoundButton.OnCheckedChangeListener ON_CHECKED_CHANGE_LISTENER = new CompoundButton.OnCheckedChangeListener()
  {
    private ReactContext getReactContext(CompoundButton paramAnonymousCompoundButton)
    {
      Context localContext = paramAnonymousCompoundButton.getContext();
      if ((localContext instanceof TintContextWrapper)) {
        return (ReactContext)((TintContextWrapper)localContext).getBaseContext();
      }
      return (ReactContext)paramAnonymousCompoundButton.getContext();
    }
    
    public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
    {
      ((UIManagerModule)getReactContext(paramAnonymousCompoundButton).getNativeModule(UIManagerModule.class)).getEventDispatcher().dispatchEvent(new ReactCheckBoxEvent(paramAnonymousCompoundButton.getId(), paramAnonymousBoolean));
    }
  };
  private static final String REACT_CLASS = "AndroidCheckBox";
  
  public ReactCheckBoxManager() {}
  
  private static int getIdentifier(Context paramContext, String paramString)
  {
    return paramContext.getResources().getIdentifier(paramString, "attr", paramContext.getPackageName());
  }
  
  private static int getThemeColor(Context paramContext, String paramString)
  {
    TypedValue localTypedValue = new TypedValue();
    paramContext.getTheme().resolveAttribute(getIdentifier(paramContext, paramString), localTypedValue, true);
    return data;
  }
  
  protected void addEventEmitters(ThemedReactContext paramThemedReactContext, ReactCheckBox paramReactCheckBox)
  {
    paramReactCheckBox.setOnCheckedChangeListener(ON_CHECKED_CHANGE_LISTENER);
  }
  
  protected ReactCheckBox createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new ReactCheckBox(paramThemedReactContext);
  }
  
  public String getName()
  {
    return "AndroidCheckBox";
  }
  
  public void setEnabled(ReactCheckBox paramReactCheckBox, boolean paramBoolean)
  {
    paramReactCheckBox.setEnabled(paramBoolean);
  }
  
  public void setOn(ReactCheckBox paramReactCheckBox, boolean paramBoolean)
  {
    paramReactCheckBox.setOnCheckedChangeListener(null);
    paramReactCheckBox.setOn(paramBoolean);
    paramReactCheckBox.setOnCheckedChangeListener(ON_CHECKED_CHANGE_LISTENER);
  }
  
  public void setTintColors(ReactCheckBox paramReactCheckBox, ReadableMap paramReadableMap)
  {
    int i;
    if ((paramReadableMap != null) && (paramReadableMap.hasKey("true"))) {
      i = paramReadableMap.getInt("true");
    } else {
      i = getThemeColor(paramReactCheckBox.getContext(), "colorAccent");
    }
    int j;
    if ((paramReadableMap != null) && (paramReadableMap.hasKey("false"))) {
      j = paramReadableMap.getInt("false");
    } else {
      j = getThemeColor(paramReactCheckBox.getContext(), "colorPrimaryDark");
    }
    CompoundButtonCompat.setButtonTintList(paramReactCheckBox, new ColorStateList(new int[][] { { 16842912 }, { -16842912 } }, new int[] { i, j }));
  }
}
