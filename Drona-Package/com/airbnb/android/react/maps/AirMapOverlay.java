package com.airbnb.android.react.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import com.facebook.react.bridge.ReadableArray;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class AirMapOverlay
  extends AirMapFeature
  implements ImageReadable
{
  private LatLngBounds bounds;
  private GroundOverlay groundOverlay;
  private GroundOverlayOptions groundOverlayOptions;
  private Bitmap iconBitmap;
  private BitmapDescriptor iconBitmapDescriptor;
  private final ImageReader mImageReader = new ImageReader(paramContext, getResources(), this);
  private GoogleMap popup;
  private boolean tappable;
  private float transparency;
  private float zIndex;
  
  public AirMapOverlay(Context paramContext)
  {
    super(paramContext);
  }
  
  private GroundOverlayOptions createGroundOverlayOptions()
  {
    if (groundOverlayOptions != null) {
      return groundOverlayOptions;
    }
    GroundOverlayOptions localGroundOverlayOptions = new GroundOverlayOptions();
    if (iconBitmapDescriptor != null)
    {
      localGroundOverlayOptions.image(iconBitmapDescriptor);
    }
    else
    {
      localGroundOverlayOptions.image(BitmapDescriptorFactory.defaultMarker());
      localGroundOverlayOptions.visible(false);
    }
    localGroundOverlayOptions.positionFromBounds(bounds);
    localGroundOverlayOptions.zIndex(zIndex);
    return localGroundOverlayOptions;
  }
  
  private GroundOverlay getGroundOverlay()
  {
    if (groundOverlay != null) {
      return groundOverlay;
    }
    if (popup == null) {
      return null;
    }
    GroundOverlayOptions localGroundOverlayOptions = getGroundOverlayOptions();
    if (localGroundOverlayOptions != null) {
      return popup.addGroundOverlay(localGroundOverlayOptions);
    }
    return null;
  }
  
  public void addToMap(GoogleMap paramGoogleMap)
  {
    GroundOverlayOptions localGroundOverlayOptions = getGroundOverlayOptions();
    if (localGroundOverlayOptions != null)
    {
      groundOverlay = paramGoogleMap.addGroundOverlay(localGroundOverlayOptions);
      groundOverlay.setClickable(tappable);
      return;
    }
    popup = paramGoogleMap;
  }
  
  public Object getFeature()
  {
    return groundOverlay;
  }
  
  public GroundOverlayOptions getGroundOverlayOptions()
  {
    if (groundOverlayOptions == null) {
      groundOverlayOptions = createGroundOverlayOptions();
    }
    return groundOverlayOptions;
  }
  
  public void removeFromMap(GoogleMap paramGoogleMap)
  {
    popup = null;
    if (groundOverlay != null)
    {
      groundOverlay.remove();
      groundOverlay = null;
      groundOverlayOptions = null;
    }
  }
  
  public void setBounds(ReadableArray paramReadableArray)
  {
    bounds = new LatLngBounds(new LatLng(paramReadableArray.getArray(1).getDouble(0), paramReadableArray.getArray(0).getDouble(1)), new LatLng(paramReadableArray.getArray(0).getDouble(0), paramReadableArray.getArray(1).getDouble(1)));
    if (groundOverlay != null) {
      groundOverlay.setPositionFromBounds(bounds);
    }
  }
  
  public void setIconBitmap(Bitmap paramBitmap)
  {
    iconBitmap = paramBitmap;
  }
  
  public void setIconBitmapDescriptor(BitmapDescriptor paramBitmapDescriptor)
  {
    iconBitmapDescriptor = paramBitmapDescriptor;
  }
  
  public void setImage(String paramString)
  {
    mImageReader.setImage(paramString);
  }
  
  public void setTappable(boolean paramBoolean)
  {
    tappable = paramBoolean;
    if (groundOverlay != null) {
      groundOverlay.setClickable(tappable);
    }
  }
  
  public void setZIndex(float paramFloat)
  {
    zIndex = paramFloat;
    if (groundOverlay != null) {
      groundOverlay.setZIndex(paramFloat);
    }
  }
  
  public void update()
  {
    groundOverlay = getGroundOverlay();
    if (groundOverlay != null)
    {
      groundOverlay.setVisible(true);
      groundOverlay.setImage(iconBitmapDescriptor);
      groundOverlay.setClickable(tappable);
    }
  }
}
