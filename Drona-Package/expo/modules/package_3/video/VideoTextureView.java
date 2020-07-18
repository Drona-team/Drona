package expo.modules.package_3.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Pair;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import com.yqritc.scalablevideoview.ScalableType;
import com.yqritc.scalablevideoview.ScaleManager;
import com.yqritc.scalablevideoview.Size;

@SuppressLint({"ViewConstructor"})
public class VideoTextureView
  extends TextureView
  implements TextureView.SurfaceTextureListener
{
  private boolean mIsAttachedToWindow = false;
  private Surface mSurface = null;
  private VideoView mVideoView = null;
  
  public VideoTextureView(Context paramContext, VideoView paramVideoView)
  {
    super(paramContext, null, 0);
    mVideoView = paramVideoView;
    setSurfaceTextureListener(this);
  }
  
  public Surface getSurface()
  {
    return mSurface;
  }
  
  public boolean isAttachedToWindow()
  {
    return mIsAttachedToWindow;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    mIsAttachedToWindow = true;
    mVideoView.tryUpdateVideoSurface(mSurface);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    mIsAttachedToWindow = false;
  }
  
  public void onSurfaceTextureAvailable(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
  {
    mSurface = new Surface(paramSurfaceTexture);
    mVideoView.tryUpdateVideoSurface(mSurface);
  }
  
  public boolean onSurfaceTextureDestroyed(SurfaceTexture paramSurfaceTexture)
  {
    mSurface = null;
    mVideoView.tryUpdateVideoSurface(null);
    return true;
  }
  
  public void onSurfaceTextureSizeChanged(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2) {}
  
  public void onSurfaceTextureUpdated(SurfaceTexture paramSurfaceTexture) {}
  
  public void scaleVideoSize(Pair paramPair, ScalableType paramScalableType)
  {
    int i = ((Integer)first).intValue();
    int j = ((Integer)second).intValue();
    if (i != 0)
    {
      if (j == 0) {
        return;
      }
      paramPair = new ScaleManager(new Size(getWidth(), getHeight()), new Size(i, j)).getScaleMatrix(paramScalableType);
      if (paramPair != null) {
        setTransform(paramPair);
      }
    }
  }
}
