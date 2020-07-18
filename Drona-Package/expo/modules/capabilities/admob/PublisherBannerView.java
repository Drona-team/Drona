package expo.modules.capabilities.admob;

import android.content.Context;
import android.os.BaseBundle;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.package_8.AdSize;
import com.google.android.gms.package_8.doubleclick.AppEventListener;
import com.google.android.gms.package_8.doubleclick.PublisherAdRequest.Builder;
import com.google.android.gms.package_8.doubleclick.PublisherAdView;
import org.unimodules.core.interfaces.services.EventEmitter;
import org.unimodules.core.interfaces.services.EventEmitter.Event;

public class PublisherBannerView
  extends FrameLayout
  implements AppEventListener
{
  private Bundle mAdditionalRequestParams;
  private EventEmitter mEventEmitter;
  
  public PublisherBannerView(Context paramContext, EventEmitter paramEventEmitter)
  {
    super(paramContext);
    mEventEmitter = paramEventEmitter;
    attachNewAdView();
  }
  
  private void loadAd(PublisherAdView paramPublisherAdView)
  {
    if ((paramPublisherAdView.getAdSizes() != null) && (paramPublisherAdView.getAdUnitId() != null) && (mAdditionalRequestParams != null))
    {
      PublisherAdRequest.Builder localBuilder2 = new PublisherAdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, mAdditionalRequestParams);
      PublisherAdRequest.Builder localBuilder1 = localBuilder2;
      String str = AdMobModule.getTestDeviceID();
      if (str != null) {
        localBuilder1 = localBuilder2.addTestDevice(str);
      }
      paramPublisherAdView.loadAd(localBuilder1.build());
    }
  }
  
  private void sendEvent(PublisherBannerViewManager.Events paramEvents)
  {
    sendEvent(paramEvents, new Bundle());
  }
  
  private void sendEvent(PublisherBannerViewManager.Events paramEvents, Bundle paramBundle)
  {
    mEventEmitter.emit(getId(), (EventEmitter.Event)new PublisherBannerView.2(this, paramEvents, paramBundle));
  }
  
  protected void attachEvents()
  {
    PublisherAdView localPublisherAdView = (PublisherAdView)getChildAt(0);
    localPublisherAdView.setAdListener(new PublisherBannerView.1(this, localPublisherAdView));
  }
  
  protected void attachNewAdView()
  {
    PublisherAdView localPublisherAdView1 = new PublisherAdView(getContext());
    localPublisherAdView1.setAppEventListener(this);
    PublisherAdView localPublisherAdView2 = (PublisherAdView)getChildAt(0);
    removeAllViews();
    if (localPublisherAdView2 != null) {
      localPublisherAdView2.destroy();
    }
    addView(localPublisherAdView1, new FrameLayout.LayoutParams(-1, -1));
    attachEvents();
  }
  
  public void onAppEvent(String paramString1, String paramString2)
  {
    Log.d("PublisherAdBanner", String.format("Received app event (%s, %s)", new Object[] { paramString1, paramString2 }));
    Bundle localBundle = new Bundle();
    localBundle.putString(paramString1, paramString2);
    sendEvent(PublisherBannerViewManager.Events.EVENT_ADMOB_EVENT_RECEIVED, localBundle);
  }
  
  public void setAdUnitID(String paramString)
  {
    AdSize[] arrayOfAdSize = ((PublisherAdView)getChildAt(0)).getAdSizes();
    attachNewAdView();
    PublisherAdView localPublisherAdView = (PublisherAdView)getChildAt(0);
    localPublisherAdView.setAdUnitId(paramString);
    localPublisherAdView.setAdSizes(arrayOfAdSize);
    loadAd(localPublisherAdView);
  }
  
  public void setAdditionalRequestParams(Bundle paramBundle)
  {
    if (!paramBundle.equals(mAdditionalRequestParams))
    {
      mAdditionalRequestParams = paramBundle;
      loadAd((PublisherAdView)getChildAt(0));
    }
  }
  
  public void setBannerSize(String paramString)
  {
    paramString = AdMobUtils.getAdSizeFromString(paramString);
    String str = ((PublisherAdView)getChildAt(0)).getAdUnitId();
    attachNewAdView();
    PublisherAdView localPublisherAdView = (PublisherAdView)getChildAt(0);
    localPublisherAdView.setAdSizes(new AdSize[] { paramString });
    localPublisherAdView.setAdUnitId(str);
    sendEvent(PublisherBannerViewManager.Events.EVENT_SIZE_CHANGE, AdMobUtils.createEventForSizeChange(getContext(), paramString));
    loadAd(localPublisherAdView);
  }
}
