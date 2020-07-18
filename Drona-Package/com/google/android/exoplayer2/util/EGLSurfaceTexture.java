package com.google.android.exoplayer2.util;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Handler;
import androidx.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@TargetApi(17)
public final class EGLSurfaceTexture
  implements SurfaceTexture.OnFrameAvailableListener, Runnable
{
  private static final int[] EGL_CONFIG_ATTRIBUTES = { 12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12327, 12344, 12339, 4, 12344 };
  private static final int EGL_PROTECTED_CONTENT_EXT = 12992;
  private static final int EGL_SURFACE_HEIGHT = 1;
  private static final int EGL_SURFACE_WIDTH = 1;
  public static final int SECURE_MODE_NONE = 0;
  public static final int SECURE_MODE_PROTECTED_PBUFFER = 2;
  public static final int SECURE_MODE_SURFACELESS_CONTEXT = 1;
  @Nullable
  private final TextureImageListener callback;
  @Nullable
  private EGLContext context;
  @Nullable
  private EGLDisplay display;
  private final Handler handler;
  @Nullable
  private EGLSurface surface;
  @Nullable
  private SurfaceTexture texture;
  private final int[] textureIdHolder;
  
  public EGLSurfaceTexture(Handler paramHandler)
  {
    this(paramHandler, null);
  }
  
  public EGLSurfaceTexture(Handler paramHandler, TextureImageListener paramTextureImageListener)
  {
    handler = paramHandler;
    callback = paramTextureImageListener;
    textureIdHolder = new int[1];
  }
  
  private static EGLConfig chooseEGLConfig(EGLDisplay paramEGLDisplay)
  {
    EGLConfig[] arrayOfEGLConfig = new EGLConfig[1];
    int[] arrayOfInt = new int[1];
    boolean bool = EGL14.eglChooseConfig(paramEGLDisplay, EGL_CONFIG_ATTRIBUTES, 0, arrayOfEGLConfig, 0, 1, arrayOfInt, 0);
    if ((bool) && (arrayOfInt[0] > 0) && (arrayOfEGLConfig[0] != null)) {
      return arrayOfEGLConfig[0];
    }
    throw new GlException(Util.formatInvariant("eglChooseConfig failed: success=%b, numConfigs[0]=%d, configs[0]=%s", new Object[] { Boolean.valueOf(bool), Integer.valueOf(arrayOfInt[0]), arrayOfEGLConfig[0] }), null);
  }
  
  private static EGLContext createEGLContext(EGLDisplay paramEGLDisplay, EGLConfig paramEGLConfig, int paramInt)
  {
    int[] arrayOfInt;
    if (paramInt == 0)
    {
      arrayOfInt = new int[3];
      int[] tmp9_8 = arrayOfInt;
      tmp9_8[0] = '?';
      int[] tmp15_9 = tmp9_8;
      tmp15_9[1] = 2;
      int[] tmp19_15 = tmp15_9;
      tmp19_15[2] = '?';
      tmp19_15;
    }
    else
    {
      arrayOfInt = new int[5];
      int[] tmp34_33 = arrayOfInt;
      tmp34_33[0] = '?';
      int[] tmp40_34 = tmp34_33;
      tmp40_34[1] = 2;
      int[] tmp44_40 = tmp40_34;
      tmp44_40[2] = '?';
      int[] tmp50_44 = tmp44_40;
      tmp50_44[3] = 1;
      int[] tmp54_50 = tmp50_44;
      tmp54_50[4] = '?';
      tmp54_50;
    }
    paramEGLDisplay = EGL14.eglCreateContext(paramEGLDisplay, paramEGLConfig, EGL14.EGL_NO_CONTEXT, arrayOfInt, 0);
    if (paramEGLDisplay != null) {
      return paramEGLDisplay;
    }
    throw new GlException("eglCreateContext failed", null);
  }
  
  private static EGLSurface createEGLSurface(EGLDisplay paramEGLDisplay, EGLConfig paramEGLConfig, EGLContext paramEGLContext, int paramInt)
  {
    if (paramInt == 1)
    {
      paramEGLConfig = EGL14.EGL_NO_SURFACE;
    }
    else
    {
      if (paramInt == 2)
      {
        localObject = new int[7];
        Object tmp25_23 = localObject;
        tmp25_23[0] = '?';
        Object tmp31_25 = tmp25_23;
        tmp31_25[1] = 1;
        Object tmp35_31 = tmp31_25;
        tmp35_31[2] = '?';
        Object tmp41_35 = tmp35_31;
        tmp41_35[3] = 1;
        Object tmp45_41 = tmp41_35;
        tmp45_41[4] = '?';
        Object tmp51_45 = tmp45_41;
        tmp51_45[5] = 1;
        Object tmp55_51 = tmp51_45;
        tmp55_51[6] = '?';
        tmp55_51;
      }
      else
      {
        localObject = new int[5];
        Object tmp73_71 = localObject;
        tmp73_71[0] = '?';
        Object tmp79_73 = tmp73_71;
        tmp79_73[1] = 1;
        Object tmp83_79 = tmp79_73;
        tmp83_79[2] = '?';
        Object tmp89_83 = tmp83_79;
        tmp89_83[3] = 1;
        Object tmp93_89 = tmp89_83;
        tmp93_89[4] = '?';
        tmp93_89;
      }
      Object localObject = EGL14.eglCreatePbufferSurface(paramEGLDisplay, paramEGLConfig, (int[])localObject, 0);
      paramEGLConfig = (EGLConfig)localObject;
      if (localObject == null) {
        break label141;
      }
    }
    if (EGL14.eglMakeCurrent(paramEGLDisplay, paramEGLConfig, paramEGLConfig, paramEGLContext)) {
      return paramEGLConfig;
    }
    throw new GlException("eglMakeCurrent failed", null);
    label141:
    throw new GlException("eglCreatePbufferSurface failed", null);
  }
  
  private void dispatchOnFrameAvailable()
  {
    if (callback != null) {
      callback.onFrameAvailable();
    }
  }
  
  private static void generateTextureIds(int[] paramArrayOfInt)
  {
    GLES20.glGenTextures(1, paramArrayOfInt, 0);
    int i = GLES20.glGetError();
    if (i == 0) {
      return;
    }
    paramArrayOfInt = new StringBuilder();
    paramArrayOfInt.append("glGenTextures failed. Error: ");
    paramArrayOfInt.append(Integer.toHexString(i));
    throw new GlException(paramArrayOfInt.toString(), null);
  }
  
  private static EGLDisplay getDefaultDisplay()
  {
    EGLDisplay localEGLDisplay = EGL14.eglGetDisplay(0);
    if (localEGLDisplay != null)
    {
      int[] arrayOfInt = new int[2];
      if (EGL14.eglInitialize(localEGLDisplay, arrayOfInt, 0, arrayOfInt, 1)) {
        return localEGLDisplay;
      }
      throw new GlException("eglInitialize failed", null);
    }
    throw new GlException("eglGetDisplay failed", null);
  }
  
  public SurfaceTexture getSurfaceTexture()
  {
    return (SurfaceTexture)Assertions.checkNotNull(texture);
  }
  
  public void init(int paramInt)
  {
    display = getDefaultDisplay();
    EGLConfig localEGLConfig = chooseEGLConfig(display);
    context = createEGLContext(display, localEGLConfig, paramInt);
    surface = createEGLSurface(display, localEGLConfig, context, paramInt);
    generateTextureIds(textureIdHolder);
    texture = new SurfaceTexture(textureIdHolder[0]);
    texture.setOnFrameAvailableListener(this);
  }
  
  public void onFrameAvailable(SurfaceTexture paramSurfaceTexture)
  {
    handler.post(this);
  }
  
  public void release()
  {
    handler.removeCallbacks(this);
    try
    {
      SurfaceTexture localSurfaceTexture = texture;
      if (localSurfaceTexture != null)
      {
        texture.release();
        GLES20.glDeleteTextures(1, textureIdHolder, 0);
      }
      if ((display != null) && (!display.equals(EGL14.EGL_NO_DISPLAY))) {
        EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
      }
      if ((surface != null) && (!surface.equals(EGL14.EGL_NO_SURFACE))) {
        EGL14.eglDestroySurface(display, surface);
      }
      if (context != null) {
        EGL14.eglDestroyContext(display, context);
      }
      if (Util.SDK_INT >= 19) {
        EGL14.eglReleaseThread();
      }
      if ((display != null) && (!display.equals(EGL14.EGL_NO_DISPLAY))) {
        EGL14.eglTerminate(display);
      }
      display = null;
      context = null;
      surface = null;
      texture = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      if ((display != null) && (!display.equals(EGL14.EGL_NO_DISPLAY))) {
        EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
      }
      if ((surface != null) && (!surface.equals(EGL14.EGL_NO_SURFACE))) {
        EGL14.eglDestroySurface(display, surface);
      }
      if (context != null) {
        EGL14.eglDestroyContext(display, context);
      }
      if (Util.SDK_INT >= 19) {
        EGL14.eglReleaseThread();
      }
      if ((display != null) && (!display.equals(EGL14.EGL_NO_DISPLAY))) {
        EGL14.eglTerminate(display);
      }
      display = null;
      context = null;
      surface = null;
      texture = null;
      throw localThrowable;
    }
  }
  
  public void run()
  {
    dispatchOnFrameAvailable();
    if (texture != null) {
      try
      {
        texture.updateTexImage();
        return;
      }
      catch (RuntimeException localRuntimeException) {}
    }
  }
  
  public static final class GlException
    extends RuntimeException
  {
    private GlException(String paramString)
    {
      super();
    }
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public static @interface SecureMode {}
  
  public static abstract interface TextureImageListener
  {
    public abstract void onFrameAvailable();
  }
}
