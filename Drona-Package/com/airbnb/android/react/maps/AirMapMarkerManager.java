package com.airbnb.android.react.maps;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.view.View;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.BaseViewManager;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class AirMapMarkerManager
  extends ViewGroupManager<AirMapMarker>
{
  private static final int ANIMATE_MARKER_TO_COORDINATE = 3;
  private static final int HIDE_INFO_WINDOW = 2;
  private static final int REDRAW = 4;
  private static final int SHOW_INFO_WINDOW = 1;
  private Map<String, AirMapMarkerSharedIcon> sharedIcons = new ConcurrentHashMap();
  
  public AirMapMarkerManager() {}
  
  public void addView(AirMapMarker paramAirMapMarker, View paramView, int paramInt)
  {
    if ((paramView instanceof AirMapCallout))
    {
      paramAirMapMarker.setCalloutView((AirMapCallout)paramView);
      return;
    }
    super.addView(paramAirMapMarker, paramView, paramInt);
    paramAirMapMarker.update(true);
  }
  
  public LayoutShadowNode createShadowNodeInstance()
  {
    return new SizeReportingShadowNode();
  }
  
  public AirMapMarker createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new AirMapMarker(paramThemedReactContext, this);
  }
  
  public Map getCommandsMap()
  {
    return MapBuilder.get("showCallout", Integer.valueOf(1), "hideCallout", Integer.valueOf(2), "animateMarkerToCoordinate", Integer.valueOf(3), "redraw", Integer.valueOf(4));
  }
  
  public Map getExportedCustomDirectEventTypeConstants()
  {
    Map localMap = MapBuilder.of("onPress", MapBuilder.get("registrationName", "onPress"), "onCalloutPress", MapBuilder.get("registrationName", "onCalloutPress"), "onDragStart", MapBuilder.get("registrationName", "onDragStart"), "onDrag", MapBuilder.get("registrationName", "onDrag"), "onDragEnd", MapBuilder.get("registrationName", "onDragEnd"));
    localMap.putAll(MapBuilder.get("onDragStart", MapBuilder.get("registrationName", "onDragStart"), "onDrag", MapBuilder.get("registrationName", "onDrag"), "onDragEnd", MapBuilder.get("registrationName", "onDragEnd")));
    return localMap;
  }
  
  public String getName()
  {
    return "AIRMapMarker";
  }
  
  public AirMapMarkerSharedIcon getSharedIcon(String paramString)
  {
    Object localObject = (AirMapMarkerSharedIcon)sharedIcons.get(paramString);
    if (localObject == null) {
      try
      {
        AirMapMarkerSharedIcon localAirMapMarkerSharedIcon = (AirMapMarkerSharedIcon)sharedIcons.get(paramString);
        localObject = localAirMapMarkerSharedIcon;
        if (localAirMapMarkerSharedIcon == null)
        {
          localObject = new AirMapMarkerSharedIcon();
          sharedIcons.put(paramString, localObject);
        }
        return localObject;
      }
      catch (Throwable paramString)
      {
        throw paramString;
      }
    }
    return localObject;
  }
  
  public void receiveCommand(AirMapMarker paramAirMapMarker, int paramInt, ReadableArray paramReadableArray)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 4: 
      paramAirMapMarker.updateMarkerIcon();
      return;
    case 3: 
      ReadableMap localReadableMap = paramReadableArray.getMap(0);
      paramInt = paramReadableArray.getInt(1);
      double d = localReadableMap.getDouble("longitude");
      paramAirMapMarker.animateToCoodinate(new LatLng(Double.valueOf(localReadableMap.getDouble("latitude")).doubleValue(), Double.valueOf(d).doubleValue()), Integer.valueOf(paramInt));
      return;
    case 2: 
      ((Marker)paramAirMapMarker.getFeature()).hideInfoWindow();
      return;
    }
    ((Marker)paramAirMapMarker.getFeature()).showInfoWindow();
  }
  
  public void removeSharedIconIfEmpty(String paramString)
  {
    AirMapMarkerSharedIcon localAirMapMarkerSharedIcon = (AirMapMarkerSharedIcon)sharedIcons.get(paramString);
    if (localAirMapMarkerSharedIcon == null) {
      return;
    }
    if (!localAirMapMarkerSharedIcon.hasMarker()) {
      try
      {
        localAirMapMarkerSharedIcon = (AirMapMarkerSharedIcon)sharedIcons.get(paramString);
        if ((localAirMapMarkerSharedIcon != null) && (!localAirMapMarkerSharedIcon.hasMarker())) {
          sharedIcons.remove(paramString);
        }
        return;
      }
      catch (Throwable paramString)
      {
        throw paramString;
      }
    }
  }
  
  public void removeViewAt(AirMapMarker paramAirMapMarker, int paramInt)
  {
    super.removeViewAt(paramAirMapMarker, paramInt);
    paramAirMapMarker.update(true);
  }
  
  public void setAnchor(AirMapMarker paramAirMapMarker, ReadableMap paramReadableMap)
  {
    double d1;
    if ((paramReadableMap != null) && (paramReadableMap.hasKey("x"))) {
      d1 = paramReadableMap.getDouble("x");
    } else {
      d1 = 0.5D;
    }
    double d2;
    if ((paramReadableMap != null) && (paramReadableMap.hasKey("y"))) {
      d2 = paramReadableMap.getDouble("y");
    } else {
      d2 = 1.0D;
    }
    paramAirMapMarker.setAnchor(d1, d2);
  }
  
  public void setCalloutAnchor(AirMapMarker paramAirMapMarker, ReadableMap paramReadableMap)
  {
    double d1;
    if ((paramReadableMap != null) && (paramReadableMap.hasKey("x"))) {
      d1 = paramReadableMap.getDouble("x");
    } else {
      d1 = 0.5D;
    }
    double d2;
    if ((paramReadableMap != null) && (paramReadableMap.hasKey("y"))) {
      d2 = paramReadableMap.getDouble("y");
    } else {
      d2 = 0.0D;
    }
    paramAirMapMarker.setCalloutAnchor(d1, d2);
  }
  
  public void setCoordinate(AirMapMarker paramAirMapMarker, ReadableMap paramReadableMap)
  {
    paramAirMapMarker.setCoordinate(paramReadableMap);
  }
  
  public void setDescription(AirMapMarker paramAirMapMarker, String paramString)
  {
    paramAirMapMarker.setSnippet(paramString);
  }
  
  public void setDraggable(AirMapMarker paramAirMapMarker, boolean paramBoolean)
  {
    paramAirMapMarker.setDraggable(paramBoolean);
  }
  
  public void setFlat(AirMapMarker paramAirMapMarker, boolean paramBoolean)
  {
    paramAirMapMarker.setFlat(paramBoolean);
  }
  
  public void setIcon(AirMapMarker paramAirMapMarker, String paramString)
  {
    paramAirMapMarker.setImage(paramString);
  }
  
  public void setIdentifier(AirMapMarker paramAirMapMarker, String paramString)
  {
    paramAirMapMarker.setIdentifier(paramString);
  }
  
  public void setImage(AirMapMarker paramAirMapMarker, String paramString)
  {
    paramAirMapMarker.setImage(paramString);
  }
  
  public void setMarkerRotation(AirMapMarker paramAirMapMarker, float paramFloat)
  {
    paramAirMapMarker.setRotation(paramFloat);
  }
  
  public void setOpacity(AirMapMarker paramAirMapMarker, float paramFloat)
  {
    super.setOpacity(paramAirMapMarker, paramFloat);
    paramAirMapMarker.setOpacity(paramFloat);
  }
  
  public void setPinColor(AirMapMarker paramAirMapMarker, int paramInt)
  {
    float[] arrayOfFloat = new float[3];
    Color.colorToHSV(paramInt, arrayOfFloat);
    paramAirMapMarker.setMarkerHue(arrayOfFloat[0]);
  }
  
  public void setTitle(AirMapMarker paramAirMapMarker, String paramString)
  {
    paramAirMapMarker.setTitle(paramString);
  }
  
  public void setTracksViewChanges(AirMapMarker paramAirMapMarker, boolean paramBoolean)
  {
    paramAirMapMarker.setTracksViewChanges(paramBoolean);
  }
  
  public void setZIndex(AirMapMarker paramAirMapMarker, float paramFloat)
  {
    super.setZIndex(paramAirMapMarker, paramFloat);
    paramAirMapMarker.setZIndex(Math.round(paramFloat));
  }
  
  public void updateExtraData(AirMapMarker paramAirMapMarker, Object paramObject)
  {
    paramObject = (HashMap)paramObject;
    float f1 = ((Float)paramObject.get("width")).floatValue();
    float f2 = ((Float)paramObject.get("height")).floatValue();
    paramAirMapMarker.update((int)f1, (int)f2);
  }
  
  public static class AirMapMarkerSharedIcon
  {
    private Bitmap bitmap;
    private BitmapDescriptor iconBitmapDescriptor;
    private boolean loadImageStarted = false;
    private Map<AirMapMarker, Boolean> markers = new WeakHashMap();
    
    public AirMapMarkerSharedIcon() {}
    
    public void addMarker(AirMapMarker paramAirMapMarker)
    {
      try
      {
        markers.put(paramAirMapMarker, Boolean.valueOf(true));
        if (iconBitmapDescriptor != null) {
          paramAirMapMarker.setIconBitmapDescriptor(iconBitmapDescriptor, bitmap);
        }
        return;
      }
      catch (Throwable paramAirMapMarker)
      {
        throw paramAirMapMarker;
      }
    }
    
    public boolean hasMarker()
    {
      try
      {
        boolean bool = markers.isEmpty();
        return bool;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    public void removeMarker(AirMapMarker paramAirMapMarker)
    {
      try
      {
        markers.remove(paramAirMapMarker);
        return;
      }
      catch (Throwable paramAirMapMarker)
      {
        throw paramAirMapMarker;
      }
    }
    
    public boolean shouldLoadImage()
    {
      try
      {
        if (!loadImageStarted)
        {
          loadImageStarted = true;
          return true;
        }
        return false;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    public void updateIcon(BitmapDescriptor paramBitmapDescriptor, Bitmap paramBitmap)
    {
      try
      {
        iconBitmapDescriptor = paramBitmapDescriptor;
        bitmap = paramBitmap.copy(Bitmap.Config.ARGB_8888, true);
        boolean bool = markers.isEmpty();
        if (bool) {
          return;
        }
        Iterator localIterator = markers.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          if (localEntry.getKey() != null) {
            ((AirMapMarker)localEntry.getKey()).setIconBitmapDescriptor(paramBitmapDescriptor, paramBitmap);
          }
        }
        return;
      }
      catch (Throwable paramBitmapDescriptor)
      {
        throw paramBitmapDescriptor;
      }
    }
  }
}
