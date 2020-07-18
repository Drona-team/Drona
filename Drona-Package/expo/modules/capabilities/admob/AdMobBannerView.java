package expo.modules.capabilities.admob;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.package_8.AdRequest.Builder;
import com.google.android.gms.package_8.AdSize;
import com.google.android.gms.package_8.AdView;
import org.unimodules.core.interfaces.services.EventEmitter;
import org.unimodules.core.interfaces.services.EventEmitter.Event;

public class AdMobBannerView
  extends FrameLayout
{
  private Bundle mAdditionalRequestParams;
  private EventEmitter mEventEmitter;
  private String mSizeString;
  
  public AdMobBannerView(Context paramContext, EventEmitter paramEventEmitter)
  {
    super(paramContext);
    mEventEmitter = paramEventEmitter;
    init();
  }
  
  private void init()
  {
    attachNewAdView();
    addOnLayoutChangeListener(new AdMobBannerView.1(this));
  }
  
  private void loadAd(AdView paramAdView)
  {
    if ((paramAdView.getAdSize() != null) && (paramAdView.getAdUnitId() != null) && (mAdditionalRequestParams != null))
    {
      AdRequest.Builder localBuilder2 = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, mAdditionalRequestParams);
      AdRequest.Builder localBuilder1 = localBuilder2;
      String str = AdMobModule.getTestDeviceID();
      if (str != null) {
        localBuilder1 = localBuilder2.addTestDevice(str);
      }
      paramAdView.loadAd(localBuilder1.build());
    }
  }
  
  private void sendEvent(AdMobBannerViewManager.Events paramEvents)
  {
    sendEvent(paramEvents, new Bundle());
  }
  
  private void sendEvent(AdMobBannerViewManager.Events paramEvents, Bundle paramBundle)
  {
    mEventEmitter.emit(getId(), (EventEmitter.Event)new AdMobBannerView.3(this, paramEvents, paramBundle));
  }
  
  protected void attachEvents()
  {
    AdView localAdView = (AdView)getChildAt(0);
    localAdView.setAdListener(new AdMobBannerView.2(this, localAdView));
  }
  
  protected void attachNewAdView()
  {
    AdView localAdView1 = new AdView(getContext());
    AdView localAdView2 = (AdView)getChildAt(0);
    removeAllViews();
    if (localAdView2 != null) {
      localAdView2.destroy();
    }
    addView(localAdView1, new FrameLayout.LayoutParams(-1, -1));
    attachEvents();
  }
  
  public void setAdUnitID(String paramString)
  {
    AdSize localAdSize = ((AdView)getChildAt(0)).getAdSize();
    attachNewAdView();
    AdView localAdView = (AdView)getChildAt(0);
    localAdView.setAdUnitId(paramString);
    localAdView.setAdSize(localAdSize);
    loadAd(localAdView);
  }
  
  public void setAdditionalRequestParams(Bundle paramBundle)
  {
    if (!paramBundle.equals(mAdditionalRequestParams))
    {
      mAdditionalRequestParams = paramBundle;
      loadAd((AdView)getChildAt(0));
    }
  }
  
  public void setBannerSize(String paramString)
  {
    mSizeString = paramString;
    paramString = AdMobUtils.getAdSizeFromString(paramString);
    String str = ((AdView)getChildAt(0)).getAdUnitId();
    attachNewAdView();
    AdView localAdView = (AdView)getChildAt(0);
    localAdView.setAdSize(paramString);
    localAdView.setAdUnitId(str);
    sendEvent(AdMobBannerViewManager.Events.EVENT_SIZE_CHANGE, AdMobUtils.createEventForSizeChange(getContext(), paramString));
    loadAd(localAdView);
  }
}
