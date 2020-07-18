package expo.modules.barcodescanner;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import java.util.List;
import org.unimodules.core.ModuleRegistry;
import org.unimodules.interfaces.barcodescanner.BarCodeScanner;
import org.unimodules.interfaces.barcodescanner.BarCodeScannerProvider;
import org.unimodules.interfaces.barcodescanner.BarCodeScannerResult;
import org.unimodules.interfaces.barcodescanner.BarCodeScannerSettings;

class BarCodeScannerViewFinder
  extends TextureView
  implements TextureView.SurfaceTextureListener, Camera.PreviewCallback
{
  public static volatile boolean barCodeScannerTaskLock;
  private BarCodeScanner mBarCodeScanner;
  private BarCodeScannerView mBarCodeScannerView;
  private Camera mCamera;
  private int mCameraType;
  private volatile boolean mIsChanging;
  private volatile boolean mIsStarting;
  private volatile boolean mIsStopping;
  private final ModuleRegistry mModuleRegistry;
  private SurfaceTexture mSurfaceTexture;
  
  public BarCodeScannerViewFinder(Context paramContext, int paramInt, BarCodeScannerView paramBarCodeScannerView, ModuleRegistry paramModuleRegistry)
  {
    super(paramContext);
    mModuleRegistry = paramModuleRegistry;
    mCameraType = paramInt;
    mBarCodeScannerView = paramBarCodeScannerView;
    setSurfaceTextureListener(this);
    initBarCodeScanner();
  }
  
  private void initBarCodeScanner()
  {
    BarCodeScannerProvider localBarCodeScannerProvider = (BarCodeScannerProvider)mModuleRegistry.getModule(BarCodeScannerProvider.class);
    if (localBarCodeScannerProvider != null) {
      mBarCodeScanner = localBarCodeScannerProvider.createBarCodeDetectorWithContext(getContext());
    }
  }
  
  private void startCamera()
  {
    Object localObject1 = this;
    try
    {
      boolean bool = mIsStarting;
      BarCodeScannerViewFinder localBarCodeScannerViewFinder = this;
      if (!bool)
      {
        localObject1 = localBarCodeScannerViewFinder;
        mIsStarting = true;
        try
        {
          localObject1 = ExpoBarCodeScanner.getInstance();
          int i = mCameraType;
          mCamera = ((ExpoBarCodeScanner)localObject1).acquireCameraInstance(i);
          localObject1 = mCamera.getParameters();
          bool = ((Camera.Parameters)localObject1).getSupportedFocusModes().contains("continuous-picture");
          if (bool) {
            ((Camera.Parameters)localObject1).setFocusMode("continuous-picture");
          }
          Object localObject3 = ExpoBarCodeScanner.getInstance().getBestSize(((Camera.Parameters)localObject1).getSupportedPictureSizes(), Integer.MAX_VALUE, Integer.MAX_VALUE);
          i = width;
          int j = height;
          ((Camera.Parameters)localObject1).setPictureSize(i, j);
          localObject3 = mCamera;
          ((Camera)localObject3).setParameters((Camera.Parameters)localObject1);
          localObject1 = mCamera;
          localObject3 = mSurfaceTexture;
          ((Camera)localObject1).setPreviewTexture((SurfaceTexture)localObject3);
          localObject1 = mCamera;
          ((Camera)localObject1).startPreview();
          localObject1 = mCamera;
          ((Camera)localObject1).setPreviewCallback(localBarCodeScannerViewFinder);
          mBarCodeScannerView.layoutViewFinder();
        }
        catch (Throwable localThrowable2) {}catch (Exception localException)
        {
          for (;;)
          {
            localException.printStackTrace();
            localBarCodeScannerViewFinder.stopCamera();
          }
        }
        catch (NullPointerException localNullPointerException)
        {
          for (;;)
          {
            localNullPointerException.printStackTrace();
          }
        }
        localObject1 = localBarCodeScannerViewFinder;
        mIsStarting = false;
        break label248;
        localObject2 = localBarCodeScannerViewFinder;
        mIsStarting = false;
        localObject2 = localBarCodeScannerViewFinder;
        throw localThrowable2;
      }
      label248:
      return;
    }
    catch (Throwable localThrowable1)
    {
      Object localObject2;
      throw localThrowable1;
    }
  }
  
  private void startPreview()
  {
    if (mSurfaceTexture != null) {
      startCamera();
    }
  }
  
  private void stopCamera()
  {
    try
    {
      if (!mIsStopping)
      {
        mIsStopping = true;
        for (;;)
        {
          try
          {
            if (mCamera != null) {
              localCamera = mCamera;
            }
          }
          catch (Throwable localThrowable1)
          {
            Camera localCamera;
            mIsStopping = false;
            throw localException;
          }
          try
          {
            localCamera.stopPreview();
            localCamera = mCamera;
            localCamera.setPreviewCallback(null);
            ExpoBarCodeScanner.getInstance().releaseCameraInstance();
            mCamera = null;
          }
          catch (Exception localException)
          {
            localException.printStackTrace();
          }
        }
        mIsStopping = false;
      }
      return;
    }
    catch (Throwable localThrowable2)
    {
      throw localThrowable2;
    }
  }
  
  private void stopPreview()
  {
    if (mCamera != null) {
      stopCamera();
    }
  }
  
  public double getRatio()
  {
    int i = ExpoBarCodeScanner.getInstance().getPreviewWidth(mCameraType);
    int j = ExpoBarCodeScanner.getInstance().getPreviewHeight(mCameraType);
    return i / j;
  }
  
  public void onPreviewFrame(byte[] paramArrayOfByte, Camera paramCamera)
  {
    if (!barCodeScannerTaskLock)
    {
      barCodeScannerTaskLock = true;
      new BarCodeScannerAsyncTask(paramCamera, paramArrayOfByte, mBarCodeScannerView).execute(new Void[0]);
    }
  }
  
  public void onSurfaceTextureAvailable(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
  {
    mSurfaceTexture = paramSurfaceTexture;
    startCamera();
  }
  
  public boolean onSurfaceTextureDestroyed(SurfaceTexture paramSurfaceTexture)
  {
    mSurfaceTexture = null;
    stopCamera();
    return true;
  }
  
  public void onSurfaceTextureSizeChanged(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2) {}
  
  public void onSurfaceTextureUpdated(SurfaceTexture paramSurfaceTexture)
  {
    mSurfaceTexture = paramSurfaceTexture;
  }
  
  public void setBarCodeScannerSettings(BarCodeScannerSettings paramBarCodeScannerSettings)
  {
    mBarCodeScanner.setSettings(paramBarCodeScannerSettings);
  }
  
  public void setCameraType(final int paramInt)
  {
    if (mCameraType == paramInt) {
      return;
    }
    new Thread(new Runnable()
    {
      public void run()
      {
        BarCodeScannerViewFinder.access$002(BarCodeScannerViewFinder.this, true);
        BarCodeScannerViewFinder.this.stopPreview();
        BarCodeScannerViewFinder.access$202(BarCodeScannerViewFinder.this, paramInt);
        BarCodeScannerViewFinder.this.startPreview();
        BarCodeScannerViewFinder.access$002(BarCodeScannerViewFinder.this, false);
      }
    }).start();
  }
  
  private class BarCodeScannerAsyncTask
    extends AsyncTask<Void, Void, Void>
  {
    private final Camera mCamera;
    private byte[] mImageData;
    
    BarCodeScannerAsyncTask(Camera paramCamera, byte[] paramArrayOfByte, BarCodeScannerView paramBarCodeScannerView)
    {
      mCamera = paramCamera;
      mImageData = paramArrayOfByte;
      BarCodeScannerViewFinder.access$402(BarCodeScannerViewFinder.this, paramBarCodeScannerView);
    }
    
    protected Void doInBackground(final Void... paramVarArgs)
    {
      if (isCancelled()) {
        return null;
      }
      if ((!mIsChanging) && (mCamera != null))
      {
        paramVarArgs = mCamera.getParameters().getPreviewSize();
        int i = width;
        int j = height;
        int k = ExpoBarCodeScanner.getInstance().getRotation();
        paramVarArgs = mBarCodeScanner.scan(mImageData, i, j, k);
        if (paramVarArgs != null) {
          new Handler(Looper.getMainLooper()).post(new Runnable()
          {
            public void run()
            {
              mBarCodeScannerView.onBarCodeScanned(paramVarArgs);
            }
          });
        }
      }
      BarCodeScannerViewFinder.barCodeScannerTaskLock = false;
      return null;
    }
  }
}
