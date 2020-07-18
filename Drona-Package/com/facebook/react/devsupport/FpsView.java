package com.facebook.react.devsupport;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.facebook.common.logging.FLog;
import com.facebook.react.R.id;
import com.facebook.react.R.layout;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.debug.FpsDebugFrameCallback;
import java.util.Locale;

public class FpsView
  extends FrameLayout
{
  private static final int UPDATE_INTERVAL_MS = 500;
  private final FPSMonitorRunnable mFPSMonitorRunnable;
  private final FpsDebugFrameCallback mFrameCallback;
  private final TextView mTextView;
  
  public FpsView(ReactContext paramReactContext)
  {
    super(paramReactContext);
    View.inflate(paramReactContext, R.layout.fps_view, this);
    mTextView = ((TextView)findViewById(R.id.fps_text));
    mFrameCallback = new FpsDebugFrameCallback(paramReactContext);
    mFPSMonitorRunnable = new FPSMonitorRunnable(null);
    setCurrentFPS(0.0D, 0.0D, 0, 0);
  }
  
  private void setCurrentFPS(double paramDouble1, double paramDouble2, int paramInt1, int paramInt2)
  {
    String str = String.format(Locale.US, "UI: %.1f fps\n%d dropped so far\n%d stutters (4+) so far\nJS: %.1f fps", new Object[] { Double.valueOf(paramDouble1), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Double.valueOf(paramDouble2) });
    mTextView.setText(str);
    FLog.d("ReactNative", str);
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    mFrameCallback.reset();
    mFrameCallback.start();
    mFPSMonitorRunnable.start();
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    mFrameCallback.stop();
    mFPSMonitorRunnable.stop();
  }
  
  private class FPSMonitorRunnable
    implements Runnable
  {
    private boolean mShouldStop = false;
    private int mTotal4PlusFrameStutters = 0;
    private int mTotalFramesDropped = 0;
    
    private FPSMonitorRunnable() {}
    
    public void run()
    {
      if (mShouldStop) {
        return;
      }
      mTotalFramesDropped += mFrameCallback.getExpectedNumFrames() - mFrameCallback.getNumFrames();
      mTotal4PlusFrameStutters += mFrameCallback.get4PlusFrameStutters();
      FpsView.this.setCurrentFPS(mFrameCallback.getFPS(), mFrameCallback.getJSFPS(), mTotalFramesDropped, mTotal4PlusFrameStutters);
      mFrameCallback.reset();
      postDelayed(this, 500L);
    }
    
    public void start()
    {
      mShouldStop = false;
      post(this);
    }
    
    public void stop()
    {
      mShouldStop = true;
    }
  }
}
