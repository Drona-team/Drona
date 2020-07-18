package com.google.android.exoplayer2.pc.spherical;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.Player.VideoComponent;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

@TargetApi(15)
public final class SphericalSurfaceView
  extends GLSurfaceView
{
  private static final int FIELD_OF_VIEW_DEGREES = 90;
  private static final float PX_PER_DEGREES = 25.0F;
  static final float UPRIGHT_ROLL = 3.1415927F;
  private static final float Z_FAR = 100.0F;
  private static final float Z_NEAR = 0.1F;
  private final Handler mainHandler = new Handler(Looper.getMainLooper());
  @Nullable
  private final Sensor orientationSensor;
  private final PhoneOrientationListener phoneOrientationListener;
  private final Renderer renderer;
  private final SceneRenderer scene;
  private final SensorManager sensorManager;
  @Nullable
  private Surface surface;
  @Nullable
  private SurfaceListener surfaceListener;
  @Nullable
  private SurfaceTexture surfaceTexture;
  private final TouchTracker touchTracker;
  @Nullable
  private Player.VideoComponent videoComponent;
  
  public SphericalSurfaceView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SphericalSurfaceView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    sensorManager = ((SensorManager)Assertions.checkNotNull(paramContext.getSystemService("sensor")));
    if (Util.SDK_INT >= 18) {
      paramAttributeSet = sensorManager.getDefaultSensor(15);
    } else {
      paramAttributeSet = null;
    }
    Object localObject = paramAttributeSet;
    if (paramAttributeSet == null) {
      localObject = sensorManager.getDefaultSensor(11);
    }
    orientationSensor = ((Sensor)localObject);
    scene = new SceneRenderer();
    renderer = new Renderer(scene);
    touchTracker = new TouchTracker(paramContext, renderer, 25.0F);
    phoneOrientationListener = new PhoneOrientationListener(((WindowManager)Assertions.checkNotNull((WindowManager)paramContext.getSystemService("window"))).getDefaultDisplay(), touchTracker, renderer);
    setEGLContextClientVersion(2);
    setRenderer(renderer);
    setOnTouchListener(touchTracker);
  }
  
  private void onSurfaceTextureAvailable(SurfaceTexture paramSurfaceTexture)
  {
    mainHandler.post(new -..Lambda.SphericalSurfaceView.6n4Tp0yadhyexFmfBUZ25TM8HJ4(this, paramSurfaceTexture));
  }
  
  private static void releaseSurface(SurfaceTexture paramSurfaceTexture, Surface paramSurface)
  {
    if (paramSurfaceTexture != null) {
      paramSurfaceTexture.release();
    }
    if (paramSurface != null) {
      paramSurface.release();
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    mainHandler.post(new -..Lambda.SphericalSurfaceView.IhaXIqfpp9iCqyi6i6bEIB2VCio(this));
  }
  
  public void onPause()
  {
    if (orientationSensor != null) {
      sensorManager.unregisterListener(phoneOrientationListener);
    }
    super.onPause();
  }
  
  public void onResume()
  {
    super.onResume();
    if (orientationSensor != null) {
      sensorManager.registerListener(phoneOrientationListener, orientationSensor, 0);
    }
  }
  
  public void setDefaultStereoMode(int paramInt)
  {
    scene.setDefaultStereoMode(paramInt);
  }
  
  public void setSingleTapListener(SingleTapListener paramSingleTapListener)
  {
    touchTracker.setSingleTapListener(paramSingleTapListener);
  }
  
  public void setSurfaceListener(SurfaceListener paramSurfaceListener)
  {
    surfaceListener = paramSurfaceListener;
  }
  
  public void setVideoComponent(Player.VideoComponent paramVideoComponent)
  {
    if (paramVideoComponent == videoComponent) {
      return;
    }
    if (videoComponent != null)
    {
      if (surface != null) {
        videoComponent.clearVideoSurface(surface);
      }
      videoComponent.clearVideoFrameMetadataListener(scene);
      videoComponent.clearCameraMotionListener(scene);
    }
    videoComponent = paramVideoComponent;
    if (videoComponent != null)
    {
      videoComponent.setVideoFrameMetadataListener(scene);
      videoComponent.setCameraMotionListener(scene);
      videoComponent.setVideoSurface(surface);
    }
  }
  
  class PhoneOrientationListener
    implements SensorEventListener
  {
    private final float[] angles = new float[3];
    private final float[] phoneInWorldSpaceMatrix = new float[16];
    private final float[] remappedPhoneMatrix = new float[16];
    private final SphericalSurfaceView.Renderer renderer;
    private final TouchTracker touchTracker;
    
    public PhoneOrientationListener(TouchTracker paramTouchTracker, SphericalSurfaceView.Renderer paramRenderer)
    {
      touchTracker = paramTouchTracker;
      renderer = paramRenderer;
    }
    
    public void onAccuracyChanged(Sensor paramSensor, int paramInt) {}
    
    public void onSensorChanged(SensorEvent paramSensorEvent)
    {
      SensorManager.getRotationMatrixFromVector(remappedPhoneMatrix, values);
      int k = getRotation();
      int i = 130;
      int j = 129;
      switch (k)
      {
      default: 
        i = 1;
        j = 2;
        break;
      case 3: 
        j = 1;
        break;
      case 2: 
        i = 129;
        j = 130;
        break;
      case 1: 
        i = 2;
      }
      SensorManager.remapCoordinateSystem(remappedPhoneMatrix, i, j, phoneInWorldSpaceMatrix);
      SensorManager.remapCoordinateSystem(phoneInWorldSpaceMatrix, 1, 131, remappedPhoneMatrix);
      SensorManager.getOrientation(remappedPhoneMatrix, angles);
      float f = angles[2];
      touchTracker.setRoll(f);
      Matrix.rotateM(phoneInWorldSpaceMatrix, 0, 90.0F, 1.0F, 0.0F, 0.0F);
      renderer.setDeviceOrientation(phoneInWorldSpaceMatrix, f);
    }
  }
  
  class Renderer
    implements GLSurfaceView.Renderer, TouchTracker.Listener
  {
    private final float[] deviceOrientationMatrix = new float[16];
    private float deviceRoll;
    private final float[] projectionMatrix = new float[16];
    private final SceneRenderer scene;
    private final float[] tempMatrix = new float[16];
    private float touchPitch;
    private final float[] touchPitchMatrix = new float[16];
    private final float[] touchYawMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    
    public Renderer(SceneRenderer paramSceneRenderer)
    {
      scene = paramSceneRenderer;
      Matrix.setIdentityM(deviceOrientationMatrix, 0);
      Matrix.setIdentityM(touchPitchMatrix, 0);
      Matrix.setIdentityM(touchYawMatrix, 0);
      deviceRoll = 3.1415927F;
    }
    
    private float calculateFieldOfViewInYDirection(float paramFloat)
    {
      int i;
      if (paramFloat > 1.0F) {
        i = 1;
      } else {
        i = 0;
      }
      if (i != 0) {
        return (float)(Math.toDegrees(Math.atan(Math.tan(Math.toRadians(45.0D)) / paramFloat)) * 2.0D);
      }
      return 90.0F;
    }
    
    private void updatePitchMatrix()
    {
      Matrix.setRotateM(touchPitchMatrix, 0, -touchPitch, (float)Math.cos(deviceRoll), (float)Math.sin(deviceRoll), 0.0F);
    }
    
    public void onDrawFrame(GL10 paramGL10)
    {
      try
      {
        Matrix.multiplyMM(tempMatrix, 0, deviceOrientationMatrix, 0, touchYawMatrix, 0);
        Matrix.multiplyMM(viewMatrix, 0, touchPitchMatrix, 0, tempMatrix, 0);
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        scene.drawFrame(viewProjectionMatrix, 0);
        return;
      }
      catch (Throwable paramGL10)
      {
        throw paramGL10;
      }
    }
    
    public void onScrollChange(PointF paramPointF)
    {
      try
      {
        touchPitch = y;
        updatePitchMatrix();
        Matrix.setRotateM(touchYawMatrix, 0, -x, 0.0F, 1.0F, 0.0F);
        return;
      }
      catch (Throwable paramPointF)
      {
        throw paramPointF;
      }
    }
    
    public void onSurfaceChanged(GL10 paramGL10, int paramInt1, int paramInt2)
    {
      GLES20.glViewport(0, 0, paramInt1, paramInt2);
      float f1 = paramInt1 / paramInt2;
      float f2 = calculateFieldOfViewInYDirection(f1);
      Matrix.perspectiveM(projectionMatrix, 0, f2, f1, 0.1F, 100.0F);
    }
    
    public void onSurfaceCreated(GL10 paramGL10, EGLConfig paramEGLConfig)
    {
      try
      {
        SphericalSurfaceView.this.onSurfaceTextureAvailable(scene.init());
        return;
      }
      catch (Throwable paramGL10)
      {
        throw paramGL10;
      }
    }
    
    public void setDeviceOrientation(float[] paramArrayOfFloat, float paramFloat)
    {
      try
      {
        System.arraycopy(paramArrayOfFloat, 0, deviceOrientationMatrix, 0, deviceOrientationMatrix.length);
        deviceRoll = (-paramFloat);
        updatePitchMatrix();
        return;
      }
      catch (Throwable paramArrayOfFloat)
      {
        throw paramArrayOfFloat;
      }
    }
  }
  
  public abstract interface SurfaceListener
  {
    public abstract void surfaceChanged(Surface paramSurface);
  }
}
