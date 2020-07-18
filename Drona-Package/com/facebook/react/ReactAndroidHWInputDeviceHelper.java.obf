package com.facebook.react;

import android.view.KeyEvent;
import android.view.View;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.common.MapBuilder.Builder;
import java.util.Map;

public class ReactAndroidHWInputDeviceHelper
{
  private static final Map<Integer, String> KEY_EVENTS_ACTIONS = MapBuilder.builder().put(Integer.valueOf(23), "select").put(Integer.valueOf(66), "select").put(Integer.valueOf(62), "select").put(Integer.valueOf(85), "playPause").put(Integer.valueOf(89), "rewind").put(Integer.valueOf(90), "fastForward").put(Integer.valueOf(19), "up").put(Integer.valueOf(22), "right").put(Integer.valueOf(20), "down").put(Integer.valueOf(21), "left").build();
  private int mLastFocusedViewId = -1;
  private final ReactRootView mReactRootView;
  
  ReactAndroidHWInputDeviceHelper(ReactRootView paramReactRootView)
  {
    mReactRootView = paramReactRootView;
  }
  
  private void dispatchEvent(String paramString, int paramInt)
  {
    dispatchEvent(paramString, paramInt, -1);
  }
  
  private void dispatchEvent(String paramString, int paramInt1, int paramInt2)
  {
    WritableNativeMap localWritableNativeMap = new WritableNativeMap();
    localWritableNativeMap.putString("eventType", paramString);
    localWritableNativeMap.putInt("eventKeyAction", paramInt2);
    if (paramInt1 != -1) {
      localWritableNativeMap.putInt("tag", paramInt1);
    }
    mReactRootView.sendEvent("onHWKeyEvent", localWritableNativeMap);
  }
  
  public void clearFocus()
  {
    if (mLastFocusedViewId != -1) {
      dispatchEvent("blur", mLastFocusedViewId);
    }
    mLastFocusedViewId = -1;
  }
  
  public void handleKeyEvent(KeyEvent paramKeyEvent)
  {
    int i = paramKeyEvent.getKeyCode();
    int j = paramKeyEvent.getAction();
    if (((j == 1) || (j == 0)) && (KEY_EVENTS_ACTIONS.containsKey(Integer.valueOf(i)))) {
      dispatchEvent((String)KEY_EVENTS_ACTIONS.get(Integer.valueOf(i)), mLastFocusedViewId, j);
    }
  }
  
  public void onFocusChanged(View paramView)
  {
    if (mLastFocusedViewId == paramView.getId()) {
      return;
    }
    if (mLastFocusedViewId != -1) {
      dispatchEvent("blur", mLastFocusedViewId);
    }
    mLastFocusedViewId = paramView.getId();
    dispatchEvent("focus", paramView.getId());
  }
}
