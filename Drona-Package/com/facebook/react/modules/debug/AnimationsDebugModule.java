package com.facebook.react.modules.debug;

import android.widget.Toast;
import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.JSApplicationCausedNativeException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.debug.interfaces.DeveloperSettings;
import java.util.Locale;

@ReactModule(name="AnimationsDebugModule")
public class AnimationsDebugModule
  extends ReactContextBaseJavaModule
{
  protected static final String NAME = "AnimationsDebugModule";
  @Nullable
  private final DeveloperSettings mCatalystSettings;
  @Nullable
  private FpsDebugFrameCallback mFrameCallback;
  
  public AnimationsDebugModule(ReactApplicationContext paramReactApplicationContext, DeveloperSettings paramDeveloperSettings)
  {
    super(paramReactApplicationContext);
    mCatalystSettings = paramDeveloperSettings;
  }
  
  public String getName()
  {
    return "AnimationsDebugModule";
  }
  
  public void onCatalystInstanceDestroy()
  {
    if (mFrameCallback != null)
    {
      mFrameCallback.stop();
      mFrameCallback = null;
    }
  }
  
  public void startRecordingFps()
  {
    if (mCatalystSettings != null)
    {
      if (!mCatalystSettings.isAnimationFpsDebugEnabled()) {
        return;
      }
      if (mFrameCallback == null)
      {
        mFrameCallback = new FpsDebugFrameCallback(getReactApplicationContext());
        mFrameCallback.startAndRecordFpsAtEachFrame();
        return;
      }
      throw new JSApplicationCausedNativeException("Already recording FPS!");
    }
  }
  
  public void stopRecordingFps(double paramDouble)
  {
    if (mFrameCallback == null) {
      return;
    }
    mFrameCallback.stop();
    Object localObject = mFrameCallback.getFpsInfo(paramDouble);
    if (localObject == null)
    {
      Toast.makeText(getReactApplicationContext(), "Unable to get FPS info", 1);
    }
    else
    {
      String str1 = String.format(Locale.US, "FPS: %.2f, %d frames (%d expected)", new Object[] { Double.valueOf(mAmount), Integer.valueOf(totalFrames), Integer.valueOf(totalExpectedFrames) });
      String str2 = String.format(Locale.US, "JS FPS: %.2f, %d frames (%d expected)", new Object[] { Double.valueOf(jsFps), Integer.valueOf(totalJsFrames), Integer.valueOf(totalExpectedFrames) });
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(str1);
      localStringBuilder.append("\n");
      localStringBuilder.append(str2);
      localStringBuilder.append("\nTotal Time MS: ");
      localStringBuilder.append(String.format(Locale.US, "%d", new Object[] { Integer.valueOf(totalTimeMs) }));
      localObject = localStringBuilder.toString();
      FLog.d("ReactNative", (String)localObject);
      Toast.makeText(getReactApplicationContext(), (CharSequence)localObject, 1).show();
    }
    mFrameCallback = null;
  }
}
