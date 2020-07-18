package com.airbnb.android.react.maps;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build.VERSION;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import androidx.core.content.PermissionChecker;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.MotionEventCompat;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener;
import com.google.android.gms.maps.GoogleMap.OnGroundOverlayClickListener;
import com.google.android.gms.maps.GoogleMap.OnIndoorStateChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.GoogleMap.OnPoiClickListener;
import com.google.android.gms.maps.GoogleMap.OnPolygonClickListener;
import com.google.android.gms.maps.GoogleMap.OnPolylineClickListener;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CameraPosition.Builder;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.IndoorLevel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.data.kml.KmlLayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AirMapView
  extends MapView
  implements GoogleMap.InfoWindowAdapter, GoogleMap.OnMarkerDragListener, OnMapReadyCallback, GoogleMap.OnPoiClickListener, GoogleMap.OnIndoorStateChangeListener
{
  private static final String[] PERMISSIONS = { "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION" };
  private ViewAttacherGroup attacherGroup;
  private final int baseMapPadding = 50;
  private LatLngBounds boundsToMove;
  private boolean cacheEnabled = false;
  private ImageView cacheImageView;
  private LatLngBounds cameraLastIdleBounds;
  private int cameraMoveReason = 0;
  private CameraUpdate cameraToSet;
  private final ThemedReactContext context;
  private boolean destroyed = false;
  private final EventDispatcher eventDispatcher;
  private final List<AirMapFeature> features = new ArrayList();
  private final GestureDetectorCompat gestureDetector;
  private final Map<TileOverlay, AirMapGradientPolyline> gradientPolylineMap = new HashMap();
  private boolean handlePanDrag = false;
  private final Map<TileOverlay, AirMapHeatmap> heatmapMap = new HashMap();
  private boolean initialCameraSet = false;
  private boolean initialRegionSet = false;
  private Boolean isMapLoaded = Boolean.valueOf(false);
  private KmlLayer kmlLayer;
  private LifecycleEventListener lifecycleListener;
  private Integer loadingBackgroundColor = null;
  private Integer loadingIndicatorColor = null;
  private final AirMapManager manager;
  private RelativeLayout mapLoadingLayout;
  private ProgressBar mapLoadingProgressBar;
  private final Map<Marker, AirMapMarker> markerMap = new HashMap();
  private boolean moveOnMarkerPress = true;
  private final Map<GroundOverlay, AirMapOverlay> overlayMap = new HashMap();
  private boolean paused = false;
  private final Map<Polygon, AirMapPolygon> polygonMap = new HashMap();
  private final Map<Polyline, AirMapPolyline> polylineMap = new HashMap();
  public GoogleMap record;
  private boolean showUserLocation = false;
  
  public AirMapView(ThemedReactContext paramThemedReactContext, ReactApplicationContext paramReactApplicationContext, AirMapManager paramAirMapManager, GoogleMapOptions paramGoogleMapOptions)
  {
    super(getNonBuggyContext(paramThemedReactContext, paramReactApplicationContext), paramGoogleMapOptions);
    manager = paramAirMapManager;
    context = paramThemedReactContext;
    super.onCreate(null);
    super.onResume();
    super.getMapAsync(this);
    gestureDetector = new GestureDetectorCompat(paramThemedReactContext, new GestureDetector.SimpleOnGestureListener()
    {
      public boolean onDoubleTap(MotionEvent paramAnonymousMotionEvent)
      {
        onDoublePress(paramAnonymousMotionEvent);
        return false;
      }
      
      public boolean onScroll(MotionEvent paramAnonymousMotionEvent1, MotionEvent paramAnonymousMotionEvent2, float paramAnonymousFloat1, float paramAnonymousFloat2)
      {
        if (handlePanDrag) {
          onPanDrag(paramAnonymousMotionEvent2);
        }
        return false;
      }
    });
    addOnLayoutChangeListener(new View.OnLayoutChangeListener()
    {
      public void onLayoutChange(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5, int paramAnonymousInt6, int paramAnonymousInt7, int paramAnonymousInt8)
      {
        if (!paused) {
          AirMapView.this.cacheView();
        }
      }
    });
    eventDispatcher = ((UIManagerModule)paramThemedReactContext.getNativeModule(UIManagerModule.class)).getEventDispatcher();
    attacherGroup = new ViewAttacherGroup(context);
    paramThemedReactContext = new FrameLayout.LayoutParams(0, 0);
    width = 0;
    height = 0;
    leftMargin = 99999999;
    topMargin = 99999999;
    attacherGroup.setLayoutParams(paramThemedReactContext);
    addView(attacherGroup);
  }
  
  private void cacheView()
  {
    if (cacheEnabled)
    {
      final ImageView localImageView = getCacheImageView();
      final RelativeLayout localRelativeLayout = getMapLoadingLayoutView();
      localImageView.setVisibility(4);
      localRelativeLayout.setVisibility(0);
      if (isMapLoaded.booleanValue()) {
        record.snapshot(new GoogleMap.SnapshotReadyCallback()
        {
          public void onSnapshotReady(Bitmap paramAnonymousBitmap)
          {
            localImageView.setImageBitmap(paramAnonymousBitmap);
            localImageView.setVisibility(0);
            localRelativeLayout.setVisibility(4);
          }
        });
      }
    }
    else
    {
      removeCacheImageView();
      if (isMapLoaded.booleanValue()) {
        removeMapLoadingLayoutView();
      }
    }
  }
  
  private static boolean contextHasBug(Context paramContext)
  {
    return (paramContext == null) || (paramContext.getResources() == null) || (paramContext.getResources().getConfiguration() == null);
  }
  
  private ImageView getCacheImageView()
  {
    if (cacheImageView == null)
    {
      cacheImageView = new ImageView(getContext());
      addView(cacheImageView, new ViewGroup.LayoutParams(-1, -1));
      cacheImageView.setVisibility(4);
    }
    return cacheImageView;
  }
  
  private RelativeLayout getMapLoadingLayoutView()
  {
    if (mapLoadingLayout == null)
    {
      mapLoadingLayout = new RelativeLayout(getContext());
      mapLoadingLayout.setBackgroundColor(-3355444);
      addView(mapLoadingLayout, new ViewGroup.LayoutParams(-1, -1));
      RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(-2, -2);
      localLayoutParams.addRule(13);
      mapLoadingLayout.addView(getMapLoadingProgressBar(), localLayoutParams);
      mapLoadingLayout.setVisibility(4);
    }
    setLoadingBackgroundColor(loadingBackgroundColor);
    return mapLoadingLayout;
  }
  
  private ProgressBar getMapLoadingProgressBar()
  {
    if (mapLoadingProgressBar == null)
    {
      mapLoadingProgressBar = new ProgressBar(getContext());
      mapLoadingProgressBar.setIndeterminate(true);
    }
    if (loadingIndicatorColor != null) {
      setLoadingIndicatorColor(loadingIndicatorColor);
    }
    return mapLoadingProgressBar;
  }
  
  private AirMapMarker getMarkerMap(Marker paramMarker)
  {
    AirMapMarker localAirMapMarker = (AirMapMarker)markerMap.get(paramMarker);
    if (localAirMapMarker != null) {
      return localAirMapMarker;
    }
    Iterator localIterator = markerMap.entrySet().iterator();
    do
    {
      localObject = localAirMapMarker;
      if (!localIterator.hasNext()) {
        break;
      }
      localObject = (Map.Entry)localIterator.next();
    } while ((!((Marker)((Map.Entry)localObject).getKey()).getPosition().equals(paramMarker.getPosition())) || (!((Marker)((Map.Entry)localObject).getKey()).getTitle().equals(paramMarker.getTitle())));
    Object localObject = (AirMapMarker)((Map.Entry)localObject).getValue();
    return localObject;
  }
  
  private static Context getNonBuggyContext(ThemedReactContext paramThemedReactContext, ReactApplicationContext paramReactApplicationContext)
  {
    if (!contextHasBug(paramReactApplicationContext.getCurrentActivity())) {
      return paramReactApplicationContext.getCurrentActivity();
    }
    if (contextHasBug(paramThemedReactContext))
    {
      if (!contextHasBug(paramThemedReactContext.getCurrentActivity())) {
        return paramThemedReactContext.getCurrentActivity();
      }
      if (!contextHasBug(paramThemedReactContext.getApplicationContext())) {
        return paramThemedReactContext.getApplicationContext();
      }
    }
    return paramThemedReactContext;
  }
  
  private boolean hasPermissions()
  {
    if (PermissionChecker.checkSelfPermission(getContext(), PERMISSIONS[0]) != 0) {
      return PermissionChecker.checkSelfPermission(getContext(), PERMISSIONS[1]) == 0;
    }
    return true;
  }
  
  private void removeCacheImageView()
  {
    if (cacheImageView != null)
    {
      ((ViewGroup)cacheImageView.getParent()).removeView(cacheImageView);
      cacheImageView = null;
    }
  }
  
  private void removeMapLoadingLayoutView()
  {
    removeMapLoadingProgressBar();
    if (mapLoadingLayout != null)
    {
      ((ViewGroup)mapLoadingLayout.getParent()).removeView(mapLoadingLayout);
      mapLoadingLayout = null;
    }
  }
  
  private void removeMapLoadingProgressBar()
  {
    if (mapLoadingProgressBar != null)
    {
      ((ViewGroup)mapLoadingProgressBar.getParent()).removeView(mapLoadingProgressBar);
      mapLoadingProgressBar = null;
    }
  }
  
  public void addFeature(View paramView, int paramInt)
  {
    Object localObject;
    if ((paramView instanceof AirMapMarker))
    {
      paramView = (AirMapMarker)paramView;
      paramView.addToMap(record);
      features.add(paramInt, paramView);
      paramInt = paramView.getVisibility();
      paramView.setVisibility(4);
      localObject = (ViewGroup)paramView.getParent();
      if (localObject != null) {
        ((ViewGroup)localObject).removeView(paramView);
      }
      attacherGroup.addView(paramView);
      paramView.setVisibility(paramInt);
      localObject = (Marker)paramView.getFeature();
      markerMap.put(localObject, paramView);
      return;
    }
    if ((paramView instanceof AirMapPolyline))
    {
      paramView = (AirMapPolyline)paramView;
      paramView.addToMap(record);
      features.add(paramInt, paramView);
      localObject = (Polyline)paramView.getFeature();
      polylineMap.put(localObject, paramView);
      return;
    }
    if ((paramView instanceof AirMapGradientPolyline))
    {
      paramView = (AirMapGradientPolyline)paramView;
      paramView.addToMap(record);
      features.add(paramInt, paramView);
      localObject = (TileOverlay)paramView.getFeature();
      gradientPolylineMap.put(localObject, paramView);
      return;
    }
    if ((paramView instanceof AirMapPolygon))
    {
      paramView = (AirMapPolygon)paramView;
      paramView.addToMap(record);
      features.add(paramInt, paramView);
      localObject = (Polygon)paramView.getFeature();
      polygonMap.put(localObject, paramView);
      return;
    }
    if ((paramView instanceof AirMapCircle))
    {
      paramView = (AirMapCircle)paramView;
      paramView.addToMap(record);
      features.add(paramInt, paramView);
      return;
    }
    if ((paramView instanceof AirMapUrlTile))
    {
      paramView = (AirMapUrlTile)paramView;
      paramView.addToMap(record);
      features.add(paramInt, paramView);
      return;
    }
    if ((paramView instanceof AirMapWMSTile))
    {
      paramView = (AirMapWMSTile)paramView;
      paramView.addToMap(record);
      features.add(paramInt, paramView);
      return;
    }
    if ((paramView instanceof AirMapLocalTile))
    {
      paramView = (AirMapLocalTile)paramView;
      paramView.addToMap(record);
      features.add(paramInt, paramView);
      return;
    }
    if ((paramView instanceof AirMapOverlay))
    {
      paramView = (AirMapOverlay)paramView;
      paramView.addToMap(record);
      features.add(paramInt, paramView);
      localObject = (GroundOverlay)paramView.getFeature();
      overlayMap.put(localObject, paramView);
      return;
    }
    if ((paramView instanceof AirMapHeatmap))
    {
      paramView = (AirMapHeatmap)paramView;
      paramView.addToMap(record);
      features.add(paramInt, paramView);
      localObject = (TileOverlay)paramView.getFeature();
      heatmapMap.put(localObject, paramView);
      return;
    }
    if ((paramView instanceof ViewGroup))
    {
      paramView = (ViewGroup)paramView;
      int i = 0;
      while (i < paramView.getChildCount())
      {
        addFeature(paramView.getChildAt(i), paramInt);
        i += 1;
      }
    }
    addView(paramView, paramInt);
  }
  
  public void animateToBearing(float paramFloat, int paramInt)
  {
    if (record == null) {
      return;
    }
    CameraPosition localCameraPosition = new CameraPosition.Builder(record.getCameraPosition()).bearing(paramFloat).build();
    record.animateCamera(CameraUpdateFactory.newCameraPosition(localCameraPosition), paramInt, null);
  }
  
  public void animateToCamera(ReadableMap paramReadableMap, int paramInt)
  {
    if (record == null) {
      return;
    }
    CameraPosition.Builder localBuilder = new CameraPosition.Builder(record.getCameraPosition());
    if (paramReadableMap.hasKey("zoom")) {
      localBuilder.zoom((float)paramReadableMap.getDouble("zoom"));
    }
    if (paramReadableMap.hasKey("heading")) {
      localBuilder.bearing((float)paramReadableMap.getDouble("heading"));
    }
    if (paramReadableMap.hasKey("pitch")) {
      localBuilder.tilt((float)paramReadableMap.getDouble("pitch"));
    }
    if (paramReadableMap.hasKey("center"))
    {
      paramReadableMap = paramReadableMap.getMap("center");
      localBuilder.target(new LatLng(paramReadableMap.getDouble("latitude"), paramReadableMap.getDouble("longitude")));
    }
    paramReadableMap = CameraUpdateFactory.newCameraPosition(localBuilder.build());
    if (paramInt <= 0)
    {
      record.moveCamera(paramReadableMap);
      return;
    }
    record.animateCamera(paramReadableMap, paramInt, null);
  }
  
  public void animateToCoordinate(LatLng paramLatLng, int paramInt)
  {
    if (record == null) {
      return;
    }
    record.animateCamera(CameraUpdateFactory.newLatLng(paramLatLng), paramInt, null);
  }
  
  public void animateToNavigation(LatLng paramLatLng, float paramFloat1, float paramFloat2, int paramInt)
  {
    if (record == null) {
      return;
    }
    paramLatLng = new CameraPosition.Builder(record.getCameraPosition()).bearing(paramFloat1).tilt(paramFloat2).target(paramLatLng).build();
    record.animateCamera(CameraUpdateFactory.newCameraPosition(paramLatLng), paramInt, null);
  }
  
  public void animateToRegion(LatLngBounds paramLatLngBounds, int paramInt)
  {
    if (record == null) {
      return;
    }
    record.animateCamera(CameraUpdateFactory.newLatLngBounds(paramLatLngBounds, 0), paramInt, null);
  }
  
  public void animateToViewingAngle(float paramFloat, int paramInt)
  {
    if (record == null) {
      return;
    }
    CameraPosition localCameraPosition = new CameraPosition.Builder(record.getCameraPosition()).tilt(paramFloat).build();
    record.animateCamera(CameraUpdateFactory.newCameraPosition(localCameraPosition), paramInt, null);
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    gestureDetector.onTouchEvent(paramMotionEvent);
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    boolean bool2 = false;
    switch (i)
    {
    default: 
      break;
    case 1: 
      getParent().requestDisallowInterceptTouchEvent(false);
      break;
    case 0: 
      ViewParent localViewParent = getParent();
      boolean bool1 = bool2;
      if (record != null)
      {
        bool1 = bool2;
        if (record.getUiSettings().isScrollGesturesEnabled()) {
          bool1 = true;
        }
      }
      localViewParent.requestDisallowInterceptTouchEvent(bool1);
    }
    super.dispatchTouchEvent(paramMotionEvent);
    return true;
  }
  
  public void doDestroy()
  {
    try
    {
      boolean bool = destroyed;
      if (bool) {
        return;
      }
      destroyed = true;
      if ((lifecycleListener != null) && (context != null))
      {
        context.removeLifecycleEventListener(lifecycleListener);
        lifecycleListener = null;
      }
      if (!paused)
      {
        onPause();
        paused = true;
      }
      onDestroy();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void enableMapLoading(boolean paramBoolean)
  {
    if ((paramBoolean) && (!isMapLoaded.booleanValue())) {
      getMapLoadingLayoutView().setVisibility(0);
    }
  }
  
  public void fitToCoordinates(ReadableArray paramReadableArray, ReadableMap paramReadableMap, boolean paramBoolean)
  {
    if (record == null) {
      return;
    }
    LatLngBounds.Builder localBuilder = new LatLngBounds.Builder();
    int i = 0;
    while (i < paramReadableArray.size())
    {
      ReadableMap localReadableMap = paramReadableArray.getMap(i);
      double d1 = localReadableMap.getDouble("latitude");
      double d2 = localReadableMap.getDouble("longitude");
      localBuilder.include(new LatLng(Double.valueOf(d1).doubleValue(), Double.valueOf(d2).doubleValue()));
      i += 1;
    }
    paramReadableArray = CameraUpdateFactory.newLatLngBounds(localBuilder.build(), 50);
    if (paramReadableMap != null) {
      record.setPadding(paramReadableMap.getInt("left"), paramReadableMap.getInt("top"), paramReadableMap.getInt("right"), paramReadableMap.getInt("bottom"));
    }
    if (paramBoolean) {
      record.animateCamera(paramReadableArray);
    } else {
      record.moveCamera(paramReadableArray);
    }
    record.setPadding(0, 0, 0, 0);
  }
  
  public void fitToElements(boolean paramBoolean)
  {
    if (record == null) {
      return;
    }
    Object localObject = new LatLngBounds.Builder();
    int i = 0;
    Iterator localIterator = features.iterator();
    while (localIterator.hasNext())
    {
      AirMapFeature localAirMapFeature = (AirMapFeature)localIterator.next();
      if ((localAirMapFeature instanceof AirMapMarker))
      {
        ((LatLngBounds.Builder)localObject).include(((Marker)localAirMapFeature.getFeature()).getPosition());
        i = 1;
      }
    }
    if (i != 0)
    {
      localObject = CameraUpdateFactory.newLatLngBounds(((LatLngBounds.Builder)localObject).build(), 50);
      if (paramBoolean)
      {
        record.animateCamera((CameraUpdate)localObject);
        return;
      }
      record.moveCamera((CameraUpdate)localObject);
    }
  }
  
  public void fitToSuppliedMarkers(ReadableArray paramReadableArray, ReadableMap paramReadableMap, boolean paramBoolean)
  {
    if (record == null) {
      return;
    }
    LatLngBounds.Builder localBuilder = new LatLngBounds.Builder();
    Object localObject1 = new String[paramReadableArray.size()];
    int j = 0;
    int i = 0;
    while (i < paramReadableArray.size())
    {
      localObject1[i] = paramReadableArray.getString(i);
      i += 1;
    }
    paramReadableArray = Arrays.asList((Object[])localObject1);
    localObject1 = features.iterator();
    i = j;
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = (AirMapFeature)((Iterator)localObject1).next();
      if ((localObject2 instanceof AirMapMarker))
      {
        String str = ((AirMapMarker)localObject2).getIdentifier();
        localObject2 = (Marker)((AirMapFeature)localObject2).getFeature();
        if (paramReadableArray.contains(str))
        {
          localBuilder.include(((Marker)localObject2).getPosition());
          i = 1;
        }
      }
    }
    if (i != 0)
    {
      paramReadableArray = CameraUpdateFactory.newLatLngBounds(localBuilder.build(), 50);
      if (paramReadableMap != null) {
        record.setPadding(paramReadableMap.getInt("left"), paramReadableMap.getInt("top"), paramReadableMap.getInt("right"), paramReadableMap.getInt("bottom"));
      }
      if (paramBoolean)
      {
        record.animateCamera(paramReadableArray);
        return;
      }
      record.moveCamera(paramReadableArray);
    }
  }
  
  public View getFeatureAt(int paramInt)
  {
    return (View)features.get(paramInt);
  }
  
  public int getFeatureCount()
  {
    return features.size();
  }
  
  public View getInfoContents(Marker paramMarker)
  {
    return getMarkerMap(paramMarker).getInfoContents();
  }
  
  public View getInfoWindow(Marker paramMarker)
  {
    return getMarkerMap(paramMarker).getCallout();
  }
  
  public double[][] getMapBoundaries()
  {
    Object localObject = record.getProjection().getVisibleRegion().latLngBounds;
    LatLng localLatLng = northeast;
    localObject = southwest;
    double d1 = longitude;
    double d2 = latitude;
    double d3 = longitude;
    double d4 = latitude;
    return new double[][] { { d1, d2 }, { d3, d4 } };
  }
  
  public WritableMap makeClickEventData(LatLng paramLatLng)
  {
    WritableNativeMap localWritableNativeMap1 = new WritableNativeMap();
    WritableNativeMap localWritableNativeMap2 = new WritableNativeMap();
    localWritableNativeMap2.putDouble("latitude", latitude);
    localWritableNativeMap2.putDouble("longitude", longitude);
    localWritableNativeMap1.putMap("coordinate", localWritableNativeMap2);
    paramLatLng = record.getProjection().toScreenLocation(paramLatLng);
    localWritableNativeMap2 = new WritableNativeMap();
    localWritableNativeMap2.putDouble("x", x);
    localWritableNativeMap2.putDouble("y", y);
    localWritableNativeMap1.putMap("position", localWritableNativeMap2);
    return localWritableNativeMap1;
  }
  
  public void onDoublePress(MotionEvent paramMotionEvent)
  {
    paramMotionEvent = new Point((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
    paramMotionEvent = makeClickEventData(record.getProjection().fromScreenLocation(paramMotionEvent));
    manager.pushEvent(context, (View)this, "onDoublePress", paramMotionEvent);
  }
  
  public void onIndoorBuildingFocused()
  {
    Object localObject1 = record.getFocusedBuilding();
    int i = 0;
    if (localObject1 != null)
    {
      localObject2 = ((IndoorBuilding)localObject1).getLevels();
      localWritableArray = Arguments.createArray();
      localObject2 = ((List)localObject2).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (IndoorLevel)((Iterator)localObject2).next();
        WritableMap localWritableMap = Arguments.createMap();
        localWritableMap.putInt("index", i);
        localWritableMap.putString("name", ((IndoorLevel)localObject3).getName());
        localWritableMap.putString("shortName", ((IndoorLevel)localObject3).getShortName());
        localWritableArray.pushMap(localWritableMap);
        i += 1;
      }
      localObject2 = Arguments.createMap();
      Object localObject3 = Arguments.createMap();
      ((WritableMap)localObject3).putArray("levels", localWritableArray);
      ((WritableMap)localObject3).putInt("activeLevelIndex", ((IndoorBuilding)localObject1).getActiveLevelIndex());
      ((WritableMap)localObject3).putBoolean("underground", ((IndoorBuilding)localObject1).isUnderground());
      ((WritableMap)localObject2).putMap("IndoorBuilding", (ReadableMap)localObject3);
      manager.pushEvent(context, (View)this, "onIndoorBuildingFocused", (WritableMap)localObject2);
      return;
    }
    localObject1 = Arguments.createMap();
    WritableArray localWritableArray = Arguments.createArray();
    Object localObject2 = Arguments.createMap();
    ((WritableMap)localObject2).putArray("levels", localWritableArray);
    ((WritableMap)localObject2).putInt("activeLevelIndex", 0);
    ((WritableMap)localObject2).putBoolean("underground", false);
    ((WritableMap)localObject1).putMap("IndoorBuilding", (ReadableMap)localObject2);
    manager.pushEvent(context, (View)this, "onIndoorBuildingFocused", (WritableMap)localObject1);
  }
  
  public void onIndoorLevelActivated(IndoorBuilding paramIndoorBuilding)
  {
    if (paramIndoorBuilding == null) {
      return;
    }
    int i = paramIndoorBuilding.getActiveLevelIndex();
    if (i >= 0)
    {
      if (i >= paramIndoorBuilding.getLevels().size()) {
        return;
      }
      paramIndoorBuilding = (IndoorLevel)paramIndoorBuilding.getLevels().get(i);
      WritableMap localWritableMap1 = Arguments.createMap();
      WritableMap localWritableMap2 = Arguments.createMap();
      localWritableMap2.putInt("activeLevelIndex", i);
      localWritableMap2.putString("name", paramIndoorBuilding.getName());
      localWritableMap2.putString("shortName", paramIndoorBuilding.getShortName());
      localWritableMap1.putMap("IndoorLevel", localWritableMap2);
      manager.pushEvent(context, (View)this, "onIndoorLevelActivated", localWritableMap1);
    }
  }
  
  public void onMapReady(final GoogleMap paramGoogleMap)
  {
    if (destroyed) {
      return;
    }
    record = paramGoogleMap;
    record.setInfoWindowAdapter(this);
    record.setOnMarkerDragListener(this);
    record.setOnPoiClickListener(this);
    record.setOnIndoorStateChangeListener(this);
    AirMapManager localAirMapManager = manager;
    ThemedReactContext localThemedReactContext = context;
    WritableNativeMap localWritableNativeMap = new WritableNativeMap();
    localAirMapManager.pushEvent(localThemedReactContext, (View)this, "onMapReady", localWritableNativeMap);
    paramGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener()
    {
      public void onMyLocationChange(Location paramAnonymousLocation)
      {
        WritableNativeMap localWritableNativeMap1 = new WritableNativeMap();
        WritableNativeMap localWritableNativeMap2 = new WritableNativeMap();
        localWritableNativeMap2.putDouble("latitude", paramAnonymousLocation.getLatitude());
        localWritableNativeMap2.putDouble("longitude", paramAnonymousLocation.getLongitude());
        localWritableNativeMap2.putDouble("altitude", paramAnonymousLocation.getAltitude());
        localWritableNativeMap2.putDouble("timestamp", paramAnonymousLocation.getTime());
        localWritableNativeMap2.putDouble("accuracy", paramAnonymousLocation.getAccuracy());
        localWritableNativeMap2.putDouble("speed", paramAnonymousLocation.getSpeed());
        localWritableNativeMap2.putDouble("heading", paramAnonymousLocation.getBearing());
        if (Build.VERSION.SDK_INT >= 18) {
          localWritableNativeMap2.putBoolean("isFromMockProvider", paramAnonymousLocation.isFromMockProvider());
        }
        localWritableNativeMap1.putMap("coordinate", localWritableNativeMap2);
        manager.pushEvent(context, (View)jdField_this, "onUserLocationChange", localWritableNativeMap1);
      }
    });
    paramGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
    {
      public boolean onMarkerClick(Marker paramAnonymousMarker)
      {
        AirMapMarker localAirMapMarker = AirMapView.this.getMarkerMap(paramAnonymousMarker);
        WritableMap localWritableMap = makeClickEventData(paramAnonymousMarker.getPosition());
        localWritableMap.putString("action", "marker-press");
        localWritableMap.putString("id", localAirMapMarker.getIdentifier());
        manager.pushEvent(context, (View)jdField_this, "onMarkerPress", localWritableMap);
        localWritableMap = makeClickEventData(paramAnonymousMarker.getPosition());
        localWritableMap.putString("action", "marker-press");
        localWritableMap.putString("id", localAirMapMarker.getIdentifier());
        manager.pushEvent(context, localAirMapMarker, "onPress", localWritableMap);
        if (jdField_thismoveOnMarkerPress) {
          return false;
        }
        paramAnonymousMarker.showInfoWindow();
        return true;
      }
    });
    paramGoogleMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener()
    {
      public void onPolygonClick(Polygon paramAnonymousPolygon)
      {
        WritableMap localWritableMap = makeClickEventData((LatLng)paramAnonymousPolygon.getPoints().get(0));
        localWritableMap.putString("action", "polygon-press");
        manager.pushEvent(context, (View)polygonMap.get(paramAnonymousPolygon), "onPress", localWritableMap);
      }
    });
    paramGoogleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener()
    {
      public void onPolylineClick(Polyline paramAnonymousPolyline)
      {
        WritableMap localWritableMap = makeClickEventData((LatLng)paramAnonymousPolyline.getPoints().get(0));
        localWritableMap.putString("action", "polyline-press");
        manager.pushEvent(context, (View)polylineMap.get(paramAnonymousPolyline), "onPress", localWritableMap);
      }
    });
    paramGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
    {
      public void onInfoWindowClick(Marker paramAnonymousMarker)
      {
        Object localObject = makeClickEventData(paramAnonymousMarker.getPosition());
        ((WritableMap)localObject).putString("action", "callout-press");
        manager.pushEvent(context, (View)jdField_this, "onCalloutPress", (WritableMap)localObject);
        WritableMap localWritableMap = makeClickEventData(paramAnonymousMarker.getPosition());
        localWritableMap.putString("action", "callout-press");
        localObject = AirMapView.this.getMarkerMap(paramAnonymousMarker);
        manager.pushEvent(context, (View)localObject, "onCalloutPress", localWritableMap);
        paramAnonymousMarker = makeClickEventData(paramAnonymousMarker.getPosition());
        paramAnonymousMarker.putString("action", "callout-press");
        localObject = ((AirMapMarker)localObject).getCalloutView();
        if (localObject != null) {
          manager.pushEvent(context, (View)localObject, "onPress", paramAnonymousMarker);
        }
      }
    });
    paramGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
    {
      public void onMapClick(LatLng paramAnonymousLatLng)
      {
        paramAnonymousLatLng = makeClickEventData(paramAnonymousLatLng);
        paramAnonymousLatLng.putString("action", "press");
        manager.pushEvent(context, (View)jdField_this, "onPress", paramAnonymousLatLng);
      }
    });
    paramGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener()
    {
      public void onMapLongClick(LatLng paramAnonymousLatLng)
      {
        makeClickEventData(paramAnonymousLatLng).putString("action", "long-press");
        AirMapManager localAirMapManager = manager;
        ThemedReactContext localThemedReactContext = context;
        AirMapView localAirMapView = jdField_this;
        paramAnonymousLatLng = makeClickEventData(paramAnonymousLatLng);
        localAirMapManager.pushEvent(localThemedReactContext, (View)localAirMapView, "onLongPress", paramAnonymousLatLng);
      }
    });
    paramGoogleMap.setOnGroundOverlayClickListener(new GoogleMap.OnGroundOverlayClickListener()
    {
      public void onGroundOverlayClick(GroundOverlay paramAnonymousGroundOverlay)
      {
        WritableMap localWritableMap = makeClickEventData(paramAnonymousGroundOverlay.getPosition());
        localWritableMap.putString("action", "overlay-press");
        manager.pushEvent(context, (View)overlayMap.get(paramAnonymousGroundOverlay), "onPress", localWritableMap);
      }
    });
    paramGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener()
    {
      public void onCameraMoveStarted(int paramAnonymousInt)
      {
        AirMapView.access$1002(AirMapView.this, paramAnonymousInt);
      }
    });
    paramGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener()
    {
      public void onCameraMove()
      {
        LatLngBounds localLatLngBounds = paramGoogleMapgetProjectiongetVisibleRegionlatLngBounds;
        AirMapView.access$1102(AirMapView.this, null);
        eventDispatcher.dispatchEvent(new RegionChangeEvent(getId(), localLatLngBounds, true));
      }
    });
    paramGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener()
    {
      public void onCameraIdle()
      {
        LatLngBounds localLatLngBounds = paramGoogleMapgetProjectiongetVisibleRegionlatLngBounds;
        if ((cameraMoveReason != 0) && ((cameraLastIdleBounds == null) || (LatLngBoundsUtils.BoundsAreDifferent(localLatLngBounds, cameraLastIdleBounds))))
        {
          AirMapView.access$1102(AirMapView.this, localLatLngBounds);
          eventDispatcher.dispatchEvent(new RegionChangeEvent(getId(), localLatLngBounds, false));
        }
      }
    });
    paramGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback()
    {
      public void onMapLoaded()
      {
        AirMapView.access$1302(AirMapView.this, Boolean.valueOf(true));
        AirMapManager localAirMapManager = manager;
        ThemedReactContext localThemedReactContext = context;
        AirMapView localAirMapView = jdField_this;
        WritableNativeMap localWritableNativeMap = new WritableNativeMap();
        localAirMapManager.pushEvent(localThemedReactContext, (View)localAirMapView, "onMapLoaded", localWritableNativeMap);
        AirMapView.this.cacheView();
      }
    });
    lifecycleListener = new LifecycleEventListener()
    {
      public void onHostDestroy()
      {
        doDestroy();
      }
      
      public void onHostPause()
      {
        if (AirMapView.this.hasPermissions()) {
          paramGoogleMap.setMyLocationEnabled(false);
        }
        AirMapView localAirMapView = AirMapView.this;
        try
        {
          if (!destroyed) {
            onPause();
          }
          AirMapView.access$102(AirMapView.this, true);
          return;
        }
        catch (Throwable localThrowable)
        {
          throw localThrowable;
        }
      }
      
      public void onHostResume()
      {
        if (AirMapView.this.hasPermissions()) {
          paramGoogleMap.setMyLocationEnabled(showUserLocation);
        }
        AirMapView localAirMapView = AirMapView.this;
        try
        {
          if (!destroyed) {
            onResume();
          }
          AirMapView.access$102(AirMapView.this, false);
          return;
        }
        catch (Throwable localThrowable)
        {
          throw localThrowable;
        }
      }
    };
    context.addLifecycleEventListener(lifecycleListener);
  }
  
  public void onMarkerDrag(Marker paramMarker)
  {
    Object localObject = makeClickEventData(paramMarker.getPosition());
    manager.pushEvent(context, (View)this, "onMarkerDrag", (WritableMap)localObject);
    localObject = getMarkerMap(paramMarker);
    paramMarker = makeClickEventData(paramMarker.getPosition());
    manager.pushEvent(context, (View)localObject, "onDrag", paramMarker);
  }
  
  public void onMarkerDragEnd(Marker paramMarker)
  {
    Object localObject = makeClickEventData(paramMarker.getPosition());
    manager.pushEvent(context, (View)this, "onMarkerDragEnd", (WritableMap)localObject);
    localObject = getMarkerMap(paramMarker);
    paramMarker = makeClickEventData(paramMarker.getPosition());
    manager.pushEvent(context, (View)localObject, "onDragEnd", paramMarker);
  }
  
  public void onMarkerDragStart(Marker paramMarker)
  {
    Object localObject = makeClickEventData(paramMarker.getPosition());
    manager.pushEvent(context, (View)this, "onMarkerDragStart", (WritableMap)localObject);
    localObject = getMarkerMap(paramMarker);
    paramMarker = makeClickEventData(paramMarker.getPosition());
    manager.pushEvent(context, (View)localObject, "onDragStart", paramMarker);
  }
  
  public void onPanDrag(MotionEvent paramMotionEvent)
  {
    paramMotionEvent = new Point((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
    paramMotionEvent = makeClickEventData(record.getProjection().fromScreenLocation(paramMotionEvent));
    manager.pushEvent(context, (View)this, "onPanDrag", paramMotionEvent);
  }
  
  public void onPoiClick(PointOfInterest paramPointOfInterest)
  {
    WritableMap localWritableMap = makeClickEventData(latLng);
    localWritableMap.putString("placeId", placeId);
    localWritableMap.putString("name", name);
    manager.pushEvent(context, (View)this, "onPoiClick", localWritableMap);
  }
  
  public void removeFeatureAt(int paramInt)
  {
    AirMapFeature localAirMapFeature = (AirMapFeature)features.remove(paramInt);
    if ((localAirMapFeature instanceof AirMapMarker)) {
      markerMap.remove(localAirMapFeature.getFeature());
    } else if ((localAirMapFeature instanceof AirMapHeatmap)) {
      heatmapMap.remove(localAirMapFeature.getFeature());
    }
    localAirMapFeature.removeFromMap(record);
  }
  
  public void setCacheEnabled(boolean paramBoolean)
  {
    cacheEnabled = paramBoolean;
    cacheView();
  }
  
  public void setCamera(ReadableMap paramReadableMap)
  {
    if (paramReadableMap == null) {
      return;
    }
    CameraPosition.Builder localBuilder = new CameraPosition.Builder();
    ReadableMap localReadableMap = paramReadableMap.getMap("center");
    if (localReadableMap != null)
    {
      double d = localReadableMap.getDouble("longitude");
      localBuilder.target(new LatLng(Double.valueOf(localReadableMap.getDouble("latitude")).doubleValue(), Double.valueOf(d).doubleValue()));
    }
    localBuilder.tilt((float)paramReadableMap.getDouble("pitch"));
    localBuilder.bearing((float)paramReadableMap.getDouble("heading"));
    localBuilder.zoom(paramReadableMap.getInt("zoom"));
    paramReadableMap = CameraUpdateFactory.newCameraPosition(localBuilder.build());
    if ((super.getHeight() > 0) && (super.getWidth() > 0))
    {
      record.moveCamera(paramReadableMap);
      cameraToSet = null;
      return;
    }
    cameraToSet = paramReadableMap;
  }
  
  public void setHandlePanDrag(boolean paramBoolean)
  {
    handlePanDrag = paramBoolean;
  }
  
  public void setIndoorActiveLevelIndex(int paramInt)
  {
    Object localObject = record.getFocusedBuilding();
    if ((localObject != null) && (paramInt >= 0) && (paramInt < ((IndoorBuilding)localObject).getLevels().size()))
    {
      localObject = (IndoorLevel)((IndoorBuilding)localObject).getLevels().get(paramInt);
      if (localObject != null) {
        ((IndoorLevel)localObject).activate();
      }
    }
  }
  
  public void setInitialCamera(ReadableMap paramReadableMap)
  {
    if ((!initialCameraSet) && (paramReadableMap != null))
    {
      setCamera(paramReadableMap);
      initialCameraSet = true;
    }
  }
  
  public void setInitialRegion(ReadableMap paramReadableMap)
  {
    if ((!initialRegionSet) && (paramReadableMap != null))
    {
      setRegion(paramReadableMap);
      initialRegionSet = true;
    }
  }
  
  public void setKmlSrc(String paramString)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a32 = a31\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:552)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:1)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:166)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:331)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:387)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:90)\n\t... 17 more\n");
  }
  
  public void setLoadingBackgroundColor(Integer paramInteger)
  {
    loadingBackgroundColor = paramInteger;
    if (mapLoadingLayout != null)
    {
      if (paramInteger == null)
      {
        mapLoadingLayout.setBackgroundColor(-1);
        return;
      }
      mapLoadingLayout.setBackgroundColor(loadingBackgroundColor.intValue());
    }
  }
  
  public void setLoadingIndicatorColor(Integer paramInteger)
  {
    loadingIndicatorColor = paramInteger;
    if (mapLoadingProgressBar != null)
    {
      Object localObject;
      if (paramInteger == null) {
        localObject = Integer.valueOf(Color.parseColor("#606060"));
      } else {
        localObject = paramInteger;
      }
      if (Build.VERSION.SDK_INT >= 21)
      {
        localObject = ColorStateList.valueOf(paramInteger.intValue());
        ColorStateList localColorStateList = ColorStateList.valueOf(paramInteger.intValue());
        paramInteger = ColorStateList.valueOf(paramInteger.intValue());
        mapLoadingProgressBar.setProgressTintList((ColorStateList)localObject);
        mapLoadingProgressBar.setSecondaryProgressTintList(localColorStateList);
        mapLoadingProgressBar.setIndeterminateTintList(paramInteger);
        return;
      }
      paramInteger = PorterDuff.Mode.SRC_IN;
      if (Build.VERSION.SDK_INT <= 10) {
        paramInteger = PorterDuff.Mode.MULTIPLY;
      }
      if (mapLoadingProgressBar.getIndeterminateDrawable() != null) {
        mapLoadingProgressBar.getIndeterminateDrawable().setColorFilter(((Integer)localObject).intValue(), paramInteger);
      }
      if (mapLoadingProgressBar.getProgressDrawable() != null) {
        mapLoadingProgressBar.getProgressDrawable().setColorFilter(((Integer)localObject).intValue(), paramInteger);
      }
    }
  }
  
  public void setMapBoundaries(ReadableMap paramReadableMap1, ReadableMap paramReadableMap2)
  {
    if (record == null) {
      return;
    }
    LatLngBounds.Builder localBuilder = new LatLngBounds.Builder();
    double d1 = paramReadableMap1.getDouble("latitude");
    double d2 = paramReadableMap1.getDouble("longitude");
    localBuilder.include(new LatLng(Double.valueOf(d1).doubleValue(), Double.valueOf(d2).doubleValue()));
    d1 = paramReadableMap2.getDouble("latitude");
    d2 = paramReadableMap2.getDouble("longitude");
    localBuilder.include(new LatLng(Double.valueOf(d1).doubleValue(), Double.valueOf(d2).doubleValue()));
    paramReadableMap1 = localBuilder.build();
    record.setLatLngBoundsForCameraTarget(paramReadableMap1);
  }
  
  public void setMoveOnMarkerPress(boolean paramBoolean)
  {
    moveOnMarkerPress = paramBoolean;
  }
  
  public void setRegion(ReadableMap paramReadableMap)
  {
    if (paramReadableMap == null) {
      return;
    }
    Double localDouble1 = Double.valueOf(paramReadableMap.getDouble("longitude"));
    Double localDouble2 = Double.valueOf(paramReadableMap.getDouble("latitude"));
    Double localDouble3 = Double.valueOf(paramReadableMap.getDouble("longitudeDelta"));
    paramReadableMap = Double.valueOf(paramReadableMap.getDouble("latitudeDelta"));
    paramReadableMap = new LatLngBounds(new LatLng(localDouble2.doubleValue() - paramReadableMap.doubleValue() / 2.0D, localDouble1.doubleValue() - localDouble3.doubleValue() / 2.0D), new LatLng(localDouble2.doubleValue() + paramReadableMap.doubleValue() / 2.0D, localDouble1.doubleValue() + localDouble3.doubleValue() / 2.0D));
    if ((super.getHeight() > 0) && (super.getWidth() > 0))
    {
      record.moveCamera(CameraUpdateFactory.newLatLngBounds(paramReadableMap, 0));
      boundsToMove = null;
      return;
    }
    record.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(localDouble2.doubleValue(), localDouble1.doubleValue()), 10.0F));
    boundsToMove = paramReadableMap;
  }
  
  public void setShowsMyLocationButton(boolean paramBoolean)
  {
    if ((hasPermissions()) || (!paramBoolean)) {
      record.getUiSettings().setMyLocationButtonEnabled(paramBoolean);
    }
  }
  
  public void setShowsUserLocation(boolean paramBoolean)
  {
    showUserLocation = paramBoolean;
    if (hasPermissions()) {
      record.setMyLocationEnabled(paramBoolean);
    }
  }
  
  public void setToolbarEnabled(boolean paramBoolean)
  {
    if ((hasPermissions()) || (!paramBoolean)) {
      record.getUiSettings().setMapToolbarEnabled(paramBoolean);
    }
  }
  
  public void updateExtraData(Object paramObject)
  {
    if (boundsToMove != null)
    {
      paramObject = (HashMap)paramObject;
      int i;
      if (paramObject.get("width") == null) {
        i = 0;
      } else {
        i = ((Float)paramObject.get("width")).intValue();
      }
      int j;
      if (paramObject.get("height") == null) {
        j = 0;
      } else {
        j = ((Float)paramObject.get("height")).intValue();
      }
      if ((i > 0) && (j > 0)) {
        record.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsToMove, i, j, 0));
      } else {
        record.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsToMove, 0));
      }
      boundsToMove = null;
      cameraToSet = null;
      return;
    }
    if (cameraToSet != null)
    {
      record.moveCamera(cameraToSet);
      cameraToSet = null;
    }
  }
}
