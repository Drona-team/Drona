package expo.modules.barcodescanner;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import java.util.List;
import org.unimodules.core.ModuleRegistry;
import org.unimodules.core.interfaces.services.EventEmitter;
import org.unimodules.core.interfaces.services.EventEmitter.Event;
import org.unimodules.interfaces.barcodescanner.BarCodeScannerResult;
import org.unimodules.interfaces.barcodescanner.BarCodeScannerSettings;

public class BarCodeScannerView
  extends ViewGroup
{
  private int mActualDeviceOrientation = -1;
  private final Context mContext;
  private int mLeftPadding = 0;
  private final ModuleRegistry mModuleRegistry;
  private final OrientationEventListener mOrientationListener;
  private int mTopPadding = 0;
  private int mType = 0;
  private BarCodeScannerViewFinder mViewFinder = null;
  
  public BarCodeScannerView(final Context paramContext, ModuleRegistry paramModuleRegistry)
  {
    super(paramContext);
    mContext = paramContext;
    mModuleRegistry = paramModuleRegistry;
    ExpoBarCodeScanner.createInstance(getDeviceOrientation(paramContext));
    mOrientationListener = new OrientationEventListener(paramContext, 3)
    {
      public void onOrientationChanged(int paramAnonymousInt)
      {
        if (BarCodeScannerView.this.setActualDeviceOrientation(paramContext)) {
          layoutViewFinder();
        }
      }
    };
    if (mOrientationListener.canDetectOrientation())
    {
      mOrientationListener.enable();
      return;
    }
    mOrientationListener.disable();
  }
  
  private int getDeviceOrientation(Context paramContext)
  {
    return ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay().getRotation();
  }
  
  private void layoutViewFinder(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (mViewFinder == null) {
      return;
    }
    float f1 = paramInt3 - paramInt1;
    float f2 = paramInt4 - paramInt2;
    double d1 = mViewFinder.getRatio();
    double d2 = f2 * d1;
    double d3 = f1;
    if (d2 < d3)
    {
      paramInt2 = (int)d2;
      paramInt1 = (int)f2;
    }
    else
    {
      paramInt1 = (int)(d3 / d1);
      paramInt2 = (int)f1;
    }
    paramInt3 = (int)((f1 - paramInt2) / 2.0F);
    paramInt4 = (int)((f2 - paramInt1) / 2.0F);
    mLeftPadding = paramInt3;
    mTopPadding = paramInt4;
    mViewFinder.layout(paramInt3, paramInt4, paramInt2 + paramInt3, paramInt1 + paramInt4);
    postInvalidate(getLeft(), getTop(), getRight(), getBottom());
  }
  
  private boolean setActualDeviceOrientation(Context paramContext)
  {
    int i = getDeviceOrientation(paramContext);
    if (mActualDeviceOrientation != i)
    {
      mActualDeviceOrientation = i;
      ExpoBarCodeScanner.getInstance().setActualDeviceOrientation(mActualDeviceOrientation);
      return true;
    }
    return false;
  }
  
  private void transformBarCodeScannerResultToViewCoordinates(BarCodeScannerResult paramBarCodeScannerResult)
  {
    List localList = paramBarCodeScannerResult.getCornerPoints();
    int i2 = getWidth();
    int i3 = mLeftPadding;
    int n = getHeight();
    int i1 = mTopPadding;
    int i = mType;
    int k = 1;
    if ((i == 1) && (getDeviceOrientation(mContext) % 2 == 0))
    {
      i = 1;
      while (i < localList.size())
      {
        localList.set(i, Integer.valueOf(paramBarCodeScannerResult.getReferenceImageHeight() - ((Integer)localList.get(i)).intValue()));
        i += 2;
      }
    }
    int j = mType;
    int m = 0;
    i = m;
    if (j == 1)
    {
      i = m;
      if (getDeviceOrientation(mContext) % 2 != 0)
      {
        j = 0;
        for (;;)
        {
          i = m;
          if (j >= localList.size()) {
            break;
          }
          localList.set(j, Integer.valueOf(paramBarCodeScannerResult.getReferenceImageWidth() - ((Integer)localList.get(j)).intValue()));
          j += 2;
        }
      }
    }
    for (;;)
    {
      j = k;
      if (i >= localList.size()) {
        break;
      }
      localList.set(i, Integer.valueOf(Math.round(((Integer)localList.get(i)).intValue() * (i2 - i3 * 2) / paramBarCodeScannerResult.getReferenceImageWidth() + mLeftPadding)));
      i += 2;
    }
    while (j < localList.size())
    {
      localList.set(j, Integer.valueOf(Math.round(((Integer)localList.get(j)).intValue() * (n - i1 * 2) / paramBarCodeScannerResult.getReferenceImageHeight() + mTopPadding)));
      j += 2;
    }
    paramBarCodeScannerResult.setReferenceImageHeight(getHeight());
    paramBarCodeScannerResult.setReferenceImageWidth(getWidth());
    paramBarCodeScannerResult.setCornerPoints(localList);
  }
  
  public float getDisplayDensity()
  {
    return getResourcesgetDisplayMetricsdensity;
  }
  
  public void layoutViewFinder()
  {
    layoutViewFinder(getLeft(), getTop(), getRight(), getBottom());
  }
  
  public void onBarCodeScanned(BarCodeScannerResult paramBarCodeScannerResult)
  {
    EventEmitter localEventEmitter = (EventEmitter)mModuleRegistry.getModule(EventEmitter.class);
    transformBarCodeScannerResultToViewCoordinates(paramBarCodeScannerResult);
    paramBarCodeScannerResult = BarCodeScannedEvent.obtain(getId(), paramBarCodeScannerResult, getDisplayDensity());
    localEventEmitter.emit(getId(), (EventEmitter.Event)paramBarCodeScannerResult);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (mOrientationListener != null) {
      mOrientationListener.disable();
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    layoutViewFinder(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void onViewAdded(View paramView)
  {
    if (mViewFinder == paramView) {
      return;
    }
    removeView(mViewFinder);
    addView(mViewFinder, 0);
  }
  
  public void setBarCodeScannerSettings(BarCodeScannerSettings paramBarCodeScannerSettings)
  {
    mViewFinder.setBarCodeScannerSettings(paramBarCodeScannerSettings);
  }
  
  public void setCameraType(int paramInt)
  {
    mType = paramInt;
    if (mViewFinder != null)
    {
      mViewFinder.setCameraType(paramInt);
      ExpoBarCodeScanner.getInstance().adjustPreviewLayout(paramInt);
      return;
    }
    mViewFinder = new BarCodeScannerViewFinder(mContext, paramInt, this, mModuleRegistry);
    addView(mViewFinder);
  }
}
