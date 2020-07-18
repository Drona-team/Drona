package com.bumptech.glide.request.target;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.Preconditions;

public class AppWidgetTarget
  extends SimpleTarget<Bitmap>
{
  private final ComponentName componentName;
  private final Context context;
  private final RemoteViews remoteViews;
  private final int viewId;
  private final int[] widgetIds;
  
  public AppWidgetTarget(Context paramContext, int paramInt1, int paramInt2, int paramInt3, RemoteViews paramRemoteViews, ComponentName paramComponentName)
  {
    super(paramInt1, paramInt2);
    context = ((Context)Preconditions.checkNotNull(paramContext, "Context can not be null!"));
    remoteViews = ((RemoteViews)Preconditions.checkNotNull(paramRemoteViews, "RemoteViews object can not be null!"));
    componentName = ((ComponentName)Preconditions.checkNotNull(paramComponentName, "ComponentName can not be null!"));
    viewId = paramInt3;
    widgetIds = null;
  }
  
  public AppWidgetTarget(Context paramContext, int paramInt1, int paramInt2, int paramInt3, RemoteViews paramRemoteViews, int... paramVarArgs)
  {
    super(paramInt1, paramInt2);
    if (paramVarArgs.length != 0)
    {
      context = ((Context)Preconditions.checkNotNull(paramContext, "Context can not be null!"));
      remoteViews = ((RemoteViews)Preconditions.checkNotNull(paramRemoteViews, "RemoteViews object can not be null!"));
      widgetIds = ((int[])Preconditions.checkNotNull(paramVarArgs, "WidgetIds can not be null!"));
      viewId = paramInt3;
      componentName = null;
      return;
    }
    throw new IllegalArgumentException("WidgetIds must have length > 0");
  }
  
  public AppWidgetTarget(Context paramContext, int paramInt, RemoteViews paramRemoteViews, ComponentName paramComponentName)
  {
    this(paramContext, Integer.MIN_VALUE, Integer.MIN_VALUE, paramInt, paramRemoteViews, paramComponentName);
  }
  
  public AppWidgetTarget(Context paramContext, int paramInt, RemoteViews paramRemoteViews, int... paramVarArgs)
  {
    this(paramContext, Integer.MIN_VALUE, Integer.MIN_VALUE, paramInt, paramRemoteViews, paramVarArgs);
  }
  
  private void update()
  {
    AppWidgetManager localAppWidgetManager = AppWidgetManager.getInstance(context);
    if (componentName != null)
    {
      localAppWidgetManager.updateAppWidget(componentName, remoteViews);
      return;
    }
    localAppWidgetManager.updateAppWidget(widgetIds, remoteViews);
  }
  
  public void onResourceReady(Bitmap paramBitmap, Transition paramTransition)
  {
    remoteViews.setImageViewBitmap(viewId, paramBitmap);
    update();
  }
}
