package com.facebook.react.views.nativecode;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.SurfaceTexture;
import android.os.Build.VERSION;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.ReactShadowNode;
import com.facebook.react.uimanager.ReactShadowNodeImpl;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIViewOperationQueue;

public class ARTSurfaceViewShadowNode
  extends LayoutShadowNode
  implements TextureView.SurfaceTextureListener, LifecycleEventListener
{
  @Nullable
  private Integer mBackgroundColor;
  @Nullable
  private Surface mSurface;
  
  public ARTSurfaceViewShadowNode() {}
  
  private void drawOutput(boolean paramBoolean)
  {
    if ((mSurface != null) && (mSurface.isValid()))
    {
      Object localObject1 = mSurface;
      try
      {
        localObject1 = ((Surface)localObject1).lockCanvas(null);
        localObject2 = PorterDuff.Mode.CLEAR;
        int i = 0;
        ((Canvas)localObject1).drawColor(0, (PorterDuff.Mode)localObject2);
        if (mBackgroundColor != null)
        {
          localObject2 = mBackgroundColor;
          ((Canvas)localObject1).drawColor(((Integer)localObject2).intValue());
        }
        localObject2 = new Paint();
        for (;;)
        {
          int j = getChildCount();
          if (i >= j) {
            break;
          }
          Object localObject3 = getChildAt(i);
          localObject3 = (ARTVirtualNode)localObject3;
          ((ARTVirtualNode)localObject3).draw((Canvas)localObject1, (Paint)localObject2, 1.0F);
          if (paramBoolean) {
            ((ReactShadowNodeImpl)localObject3).markUpdated();
          } else {
            ((ReactShadowNodeImpl)localObject3).markUpdateSeen();
          }
          i += 1;
        }
        if (mSurface == null) {
          return;
        }
        localObject2 = mSurface;
        ((Surface)localObject2).unlockCanvasAndPost((Canvas)localObject1);
        return;
      }
      catch (IllegalArgumentException|IllegalStateException localIllegalArgumentException)
      {
        Object localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append(localIllegalArgumentException.getClass().getSimpleName());
        ((StringBuilder)localObject2).append(" in Surface.unlockCanvasAndPost");
        FLog.e("ReactNative", ((StringBuilder)localObject2).toString());
        return;
      }
    }
    markChildrenUpdatesSeen(this);
  }
  
  private void markChildrenUpdatesSeen(ReactShadowNode paramReactShadowNode)
  {
    int i = 0;
    while (i < paramReactShadowNode.getChildCount())
    {
      ReactShadowNode localReactShadowNode = paramReactShadowNode.getChildAt(i);
      localReactShadowNode.markUpdateSeen();
      markChildrenUpdatesSeen(localReactShadowNode);
      i += 1;
    }
  }
  
  public void dispose()
  {
    super.dispose();
    if (Build.VERSION.SDK_INT > 24) {
      getThemedContext().removeLifecycleEventListener(this);
    }
  }
  
  public boolean isVirtual()
  {
    return false;
  }
  
  public boolean isVirtualAnchor()
  {
    return true;
  }
  
  public void onCollectExtraUpdates(UIViewOperationQueue paramUIViewOperationQueue)
  {
    super.onCollectExtraUpdates(paramUIViewOperationQueue);
    drawOutput(false);
    paramUIViewOperationQueue.enqueueUpdateExtraData(getReactTag(), this);
  }
  
  public void onHostDestroy() {}
  
  public void onHostPause() {}
  
  public void onHostResume()
  {
    drawOutput(false);
  }
  
  public void onSurfaceTextureAvailable(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
  {
    mSurface = new Surface(paramSurfaceTexture);
    drawOutput(false);
  }
  
  public boolean onSurfaceTextureDestroyed(SurfaceTexture paramSurfaceTexture)
  {
    mSurface.release();
    mSurface = null;
    return true;
  }
  
  public void onSurfaceTextureSizeChanged(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2) {}
  
  public void onSurfaceTextureUpdated(SurfaceTexture paramSurfaceTexture) {}
  
  public void setBackgroundColor(Integer paramInteger)
  {
    mBackgroundColor = paramInteger;
    markUpdated();
  }
  
  public void setThemedContext(ThemedReactContext paramThemedReactContext)
  {
    super.setThemedContext(paramThemedReactContext);
    if (Build.VERSION.SDK_INT > 24) {
      paramThemedReactContext.addLifecycleEventListener(this);
    }
  }
  
  public void setupSurfaceTextureListener(ARTSurfaceView paramARTSurfaceView)
  {
    SurfaceTexture localSurfaceTexture = paramARTSurfaceView.getSurfaceTexture();
    paramARTSurfaceView.setSurfaceTextureListener(this);
    if ((localSurfaceTexture != null) && (mSurface == null))
    {
      mSurface = new Surface(localSurfaceTexture);
      drawOutput(true);
    }
  }
}
