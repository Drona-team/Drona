package com.google.android.gms.package_8;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.internal.ads.zzabb;
import com.google.android.gms.internal.ads.zzbad;
import com.google.android.gms.internal.ads.zzxr;
import com.google.android.gms.package_8.doubleclick.AppEventListener;

class BaseAdView
  extends ViewGroup
{
  protected final zzabb zzaaq;
  
  public BaseAdView(Context paramContext, int paramInt)
  {
    super(paramContext);
    zzaaq = new zzabb(this, paramInt);
  }
  
  public BaseAdView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet);
    zzaaq = new zzabb(this, paramAttributeSet, false, paramInt);
  }
  
  public BaseAdView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1);
    zzaaq = new zzabb(this, paramAttributeSet, false, paramInt2);
  }
  
  public void destroy()
  {
    zzaaq.destroy();
  }
  
  public AdListener getAdListener()
  {
    return zzaaq.getAdListener();
  }
  
  public AdSize getAdSize()
  {
    return zzaaq.getAdSize();
  }
  
  public String getAdUnitId()
  {
    return zzaaq.getAdUnitId();
  }
  
  public String getMediationAdapterClassName()
  {
    return zzaaq.getMediationAdapterClassName();
  }
  
  public boolean isLoading()
  {
    return zzaaq.isLoading();
  }
  
  public void loadAd(AdRequest paramAdRequest)
  {
    zzaaq.zza(paramAdRequest.zzde());
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    View localView = getChildAt(0);
    if ((localView != null) && (localView.getVisibility() != 8))
    {
      int i = localView.getMeasuredWidth();
      int j = localView.getMeasuredHeight();
      paramInt1 = (paramInt3 - paramInt1 - i) / 2;
      paramInt2 = (paramInt4 - paramInt2 - j) / 2;
      localView.layout(paramInt1, paramInt2, i + paramInt1, j + paramInt2);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = 0;
    Object localObject = getChildAt(0);
    if ((localObject != null) && (((View)localObject).getVisibility() != 8))
    {
      measureChild((View)localObject, paramInt1, paramInt2);
      i = ((View)localObject).getMeasuredWidth();
      j = ((View)localObject).getMeasuredHeight();
    }
    else
    {
      localObject = null;
      try
      {
        AdSize localAdSize = getAdSize();
        localObject = localAdSize;
      }
      catch (NullPointerException localNullPointerException)
      {
        zzbad.zzc("Unable to retrieve ad size.", localNullPointerException);
      }
      if (localObject != null)
      {
        Context localContext = getContext();
        i = ((AdSize)localObject).getWidthInPixels(localContext);
        j = ((AdSize)localObject).getHeightInPixels(localContext);
      }
      else
      {
        j = 0;
      }
    }
    i = Math.max(i, getSuggestedMinimumWidth());
    int j = Math.max(j, getSuggestedMinimumHeight());
    setMeasuredDimension(View.resolveSize(i, paramInt1), View.resolveSize(j, paramInt2));
  }
  
  public void pause()
  {
    zzaaq.pause();
  }
  
  public void resume()
  {
    zzaaq.resume();
  }
  
  public void setAdListener(AdListener paramAdListener)
  {
    zzaaq.setAdListener(paramAdListener);
    if (paramAdListener == null)
    {
      zzaaq.zza(null);
      zzaaq.setAppEventListener(null);
      return;
    }
    if ((paramAdListener instanceof zzxr)) {
      zzaaq.zza((zzxr)paramAdListener);
    }
    if ((paramAdListener instanceof AppEventListener)) {
      zzaaq.setAppEventListener((AppEventListener)paramAdListener);
    }
  }
  
  public void setAdSize(AdSize paramAdSize)
  {
    zzaaq.setAdSizes(new AdSize[] { paramAdSize });
  }
  
  public void setAdUnitId(String paramString)
  {
    zzaaq.setAdUnitId(paramString);
  }
}
