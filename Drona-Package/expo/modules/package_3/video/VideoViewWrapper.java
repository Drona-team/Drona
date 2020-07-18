package expo.modules.package_3.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.unimodules.core.ModuleRegistry;

@SuppressLint({"ViewConstructor"})
public class VideoViewWrapper
  extends FrameLayout
{
  private final Runnable mLayoutRunnable = new Runnable()
  {
    public void run()
    {
      measure(View.MeasureSpec.makeMeasureSpec(getWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(getHeight(), 1073741824));
      layout(getLeft(), getTop(), getRight(), getBottom());
    }
  };
  private VideoView mVideoView = null;
  
  public VideoViewWrapper(Context paramContext, ModuleRegistry paramModuleRegistry)
  {
    super(paramContext);
    mVideoView = new VideoView(paramContext, this, paramModuleRegistry);
    addView(mVideoView, generateDefaultLayoutParams());
  }
  
  public VideoView getVideoViewInstance()
  {
    return mVideoView;
  }
  
  public void requestLayout()
  {
    super.requestLayout();
    post(mLayoutRunnable);
  }
}
