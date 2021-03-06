package com.google.android.exoplayer2.pc;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.media.session.MediaSessionCompat.Token;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.package_4.NotificationCompat.Builder;
import androidx.core.package_4.NotificationCompat.Style;
import androidx.core.package_4.NotificationManagerCompat;
import androidx.media.routing.NotificationCompat.MediaStyle;
import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Timeline.Window;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerNotificationManager
{
  public static final String ACTION_FAST_FORWARD = "com.google.android.exoplayer.ffwd";
  public static final String ACTION_NEXT = "com.google.android.exoplayer.next";
  public static final String ACTION_PAUSE = "com.google.android.exoplayer.pause";
  public static final String ACTION_PLAY = "com.google.android.exoplayer.play";
  public static final String ACTION_PREVIOUS = "com.google.android.exoplayer.prev";
  public static final String ACTION_REWIND = "com.google.android.exoplayer.rewind";
  public static final String ACTION_STOP = "com.google.android.exoplayer.stop";
  public static final int DEFAULT_FAST_FORWARD_MS = 15000;
  public static final int DEFAULT_REWIND_MS = 5000;
  public static final String EXTRA_INSTANCE_ID = "INSTANCE_ID";
  private static final long MAX_POSITION_FOR_SEEK_TO_PREVIOUS = 3000L;
  private static int instanceIdCounter;
  private int badgeIconType;
  private final String channelId;
  private int color;
  private boolean colorized;
  private final Context context;
  private ControlDispatcher controlDispatcher;
  private int currentNotificationTag;
  @Nullable
  private final CustomActionReceiver customActionReceiver;
  private final Map<String, androidx.core.app.NotificationCompat.Action> customActions;
  private int defaults;
  private long fastForwardMs;
  private final int instanceId;
  private final IntentFilter intentFilter;
  private boolean isNotificationStarted;
  private int lastPlaybackState;
  private final Handler mainHandler;
  private final MediaDescriptionAdapter mediaDescriptionAdapter;
  @Nullable
  private MediaSessionCompat.Token mediaSessionToken;
  private final NotificationBroadcastReceiver notificationBroadcastReceiver;
  private final int notificationId;
  @Nullable
  private NotificationListener notificationListener;
  private final NotificationManagerCompat notificationManager;
  private boolean ongoing;
  private final Map<String, androidx.core.app.NotificationCompat.Action> playbackActions;
  @Nullable
  private Player player;
  private final Player.EventListener playerListener;
  private int priority;
  private long rewindMs;
  @DrawableRes
  private int smallIconResourceId;
  @Nullable
  private String stopAction;
  @Nullable
  private PendingIntent stopPendingIntent;
  private boolean useChronometer;
  private boolean useNavigationActions;
  private boolean usePlayPauseActions;
  private int visibility;
  private boolean wasPlayWhenReady;
  
  public PlayerNotificationManager(Context paramContext, String paramString, int paramInt, MediaDescriptionAdapter paramMediaDescriptionAdapter)
  {
    this(paramContext, paramString, paramInt, paramMediaDescriptionAdapter, null);
  }
  
  public PlayerNotificationManager(Context paramContext, String paramString, int paramInt, MediaDescriptionAdapter paramMediaDescriptionAdapter, CustomActionReceiver paramCustomActionReceiver)
  {
    context = paramContext.getApplicationContext();
    channelId = paramString;
    notificationId = paramInt;
    mediaDescriptionAdapter = paramMediaDescriptionAdapter;
    customActionReceiver = paramCustomActionReceiver;
    controlDispatcher = new DefaultControlDispatcher();
    paramInt = instanceIdCounter;
    instanceIdCounter = paramInt + 1;
    instanceId = paramInt;
    mainHandler = new Handler(Looper.getMainLooper());
    notificationManager = NotificationManagerCompat.from(paramContext);
    playerListener = new PlayerListener(null);
    notificationBroadcastReceiver = new NotificationBroadcastReceiver();
    intentFilter = new IntentFilter();
    useNavigationActions = true;
    usePlayPauseActions = true;
    ongoing = true;
    colorized = true;
    useChronometer = true;
    color = 0;
    smallIconResourceId = R.drawable.exo_notification_small_icon;
    defaults = 0;
    priority = -1;
    fastForwardMs = 15000L;
    rewindMs = 5000L;
    stopAction = "com.google.android.exoplayer.stop";
    badgeIconType = 1;
    visibility = 1;
    playbackActions = createPlaybackActions(paramContext, instanceId);
    paramString = playbackActions.keySet().iterator();
    while (paramString.hasNext())
    {
      paramMediaDescriptionAdapter = (String)paramString.next();
      intentFilter.addAction(paramMediaDescriptionAdapter);
    }
    if (paramCustomActionReceiver != null) {
      paramContext = paramCustomActionReceiver.createCustomActions(paramContext, instanceId);
    } else {
      paramContext = Collections.emptyMap();
    }
    customActions = paramContext;
    paramContext = customActions.keySet().iterator();
    while (paramContext.hasNext())
    {
      paramString = (String)paramContext.next();
      intentFilter.addAction(paramString);
    }
    stopPendingIntent = checkNotNullplaybackActions.get("com.google.android.exoplayer.stop"))).actionIntent;
  }
  
  private static PendingIntent createBroadcastIntent(String paramString, Context paramContext, int paramInt)
  {
    paramString = new Intent(paramString).setPackage(paramContext.getPackageName());
    paramString.putExtra("INSTANCE_ID", paramInt);
    return PendingIntent.getBroadcast(paramContext, paramInt, paramString, 268435456);
  }
  
  private static Map createPlaybackActions(Context paramContext, int paramInt)
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("com.google.android.exoplayer.play", new androidx.core.package_4.NotificationCompat.Action(R.drawable.exo_notification_play, paramContext.getString(R.string.exo_controls_play_description), createBroadcastIntent("com.google.android.exoplayer.play", paramContext, paramInt)));
    localHashMap.put("com.google.android.exoplayer.pause", new androidx.core.package_4.NotificationCompat.Action(R.drawable.exo_notification_pause, paramContext.getString(R.string.exo_controls_pause_description), createBroadcastIntent("com.google.android.exoplayer.pause", paramContext, paramInt)));
    localHashMap.put("com.google.android.exoplayer.stop", new androidx.core.package_4.NotificationCompat.Action(R.drawable.exo_notification_stop, paramContext.getString(R.string.exo_controls_stop_description), createBroadcastIntent("com.google.android.exoplayer.stop", paramContext, paramInt)));
    localHashMap.put("com.google.android.exoplayer.rewind", new androidx.core.package_4.NotificationCompat.Action(R.drawable.exo_notification_rewind, paramContext.getString(R.string.exo_controls_rewind_description), createBroadcastIntent("com.google.android.exoplayer.rewind", paramContext, paramInt)));
    localHashMap.put("com.google.android.exoplayer.ffwd", new androidx.core.package_4.NotificationCompat.Action(R.drawable.exo_notification_fastforward, paramContext.getString(R.string.exo_controls_fastforward_description), createBroadcastIntent("com.google.android.exoplayer.ffwd", paramContext, paramInt)));
    localHashMap.put("com.google.android.exoplayer.prev", new androidx.core.package_4.NotificationCompat.Action(R.drawable.exo_notification_previous, paramContext.getString(R.string.exo_controls_previous_description), createBroadcastIntent("com.google.android.exoplayer.prev", paramContext, paramInt)));
    localHashMap.put("com.google.android.exoplayer.next", new androidx.core.package_4.NotificationCompat.Action(R.drawable.exo_notification_next, paramContext.getString(R.string.exo_controls_next_description), createBroadcastIntent("com.google.android.exoplayer.next", paramContext, paramInt)));
    return localHashMap;
  }
  
  public static PlayerNotificationManager createWithNotificationChannel(Context paramContext, String paramString, int paramInt1, int paramInt2, MediaDescriptionAdapter paramMediaDescriptionAdapter)
  {
    NotificationUtil.createNotificationChannel(paramContext, paramString, paramInt1, 2);
    return new PlayerNotificationManager(paramContext, paramString, paramInt2, paramMediaDescriptionAdapter);
  }
  
  private void startOrUpdateNotification()
  {
    if (player != null)
    {
      Notification localNotification = updateNotification(null);
      if (!isNotificationStarted)
      {
        isNotificationStarted = true;
        context.registerReceiver(notificationBroadcastReceiver, intentFilter);
        if (notificationListener != null) {
          notificationListener.onNotificationStarted(notificationId, localNotification);
        }
      }
    }
  }
  
  private void stopNotification()
  {
    if (isNotificationStarted)
    {
      notificationManager.cancel(notificationId);
      isNotificationStarted = false;
      context.unregisterReceiver(notificationBroadcastReceiver);
      if (notificationListener != null) {
        notificationListener.onNotificationCancelled(notificationId);
      }
    }
  }
  
  private Notification updateNotification(Bitmap paramBitmap)
  {
    paramBitmap = createNotification(player, paramBitmap);
    notificationManager.notify(notificationId, paramBitmap);
    return paramBitmap;
  }
  
  protected Notification createNotification(Player paramPlayer, Bitmap paramBitmap)
  {
    NotificationCompat.Builder localBuilder = new NotificationCompat.Builder(context, channelId);
    List localList = getActions(paramPlayer);
    int i = 0;
    while (i < localList.size())
    {
      localObject = (String)localList.get(i);
      if (playbackActions.containsKey(localObject)) {
        localObject = (androidx.core.package_4.NotificationCompat.Action)playbackActions.get(localObject);
      } else {
        localObject = (androidx.core.package_4.NotificationCompat.Action)customActions.get(localObject);
      }
      if (localObject != null) {
        localBuilder.addAction((androidx.core.package_4.NotificationCompat.Action)localObject);
      }
      i += 1;
    }
    Object localObject = new NotificationCompat.MediaStyle();
    if (mediaSessionToken != null) {
      ((NotificationCompat.MediaStyle)localObject).setMediaSession(mediaSessionToken);
    }
    ((NotificationCompat.MediaStyle)localObject).setShowActionsInCompactView(getActionIndicesForCompactView(localList, paramPlayer));
    boolean bool;
    if (stopAction != null) {
      bool = true;
    } else {
      bool = false;
    }
    ((NotificationCompat.MediaStyle)localObject).setShowCancelButton(bool);
    if ((bool) && (stopPendingIntent != null))
    {
      localBuilder.setDeleteIntent(stopPendingIntent);
      ((NotificationCompat.MediaStyle)localObject).setCancelButtonIntent(stopPendingIntent);
    }
    localBuilder.setStyle((NotificationCompat.Style)localObject);
    localBuilder.setBadgeIconType(badgeIconType).setOngoing(ongoing).setColor(color).setColorized(colorized).setSmallIcon(smallIconResourceId).setVisibility(visibility).setPriority(priority).setDefaults(defaults);
    if ((useChronometer) && (!paramPlayer.isPlayingAd()) && (!paramPlayer.isCurrentWindowDynamic()) && (paramPlayer.getPlayWhenReady()) && (paramPlayer.getPlaybackState() == 3)) {
      localBuilder.setWhen(System.currentTimeMillis() - paramPlayer.getContentPosition()).setShowWhen(true).setUsesChronometer(true);
    } else {
      localBuilder.setShowWhen(false).setUsesChronometer(false);
    }
    localBuilder.setContentTitle(mediaDescriptionAdapter.getCurrentContentTitle(paramPlayer));
    localBuilder.setContentText(mediaDescriptionAdapter.getCurrentContentText(paramPlayer));
    localObject = paramBitmap;
    if (paramBitmap == null)
    {
      paramBitmap = mediaDescriptionAdapter;
      i = currentNotificationTag + 1;
      currentNotificationTag = i;
      localObject = paramBitmap.getCurrentLargeIcon(paramPlayer, new BitmapCallback(i, null));
    }
    if (localObject != null) {
      localBuilder.setLargeIcon((Bitmap)localObject);
    }
    paramPlayer = mediaDescriptionAdapter.createCurrentContentIntent(paramPlayer);
    if (paramPlayer != null) {
      localBuilder.setContentIntent(paramPlayer);
    }
    return localBuilder.build();
  }
  
  protected int[] getActionIndicesForCompactView(List paramList, Player paramPlayer)
  {
    int i = paramList.indexOf("com.google.android.exoplayer.pause");
    int j = paramList.indexOf("com.google.android.exoplayer.play");
    if (i != -1) {
      return new int[] { i };
    }
    if (j != -1) {
      return new int[] { j };
    }
    return new int[0];
  }
  
  protected List getActions(Player paramPlayer)
  {
    boolean bool = paramPlayer.isPlayingAd();
    ArrayList localArrayList = new ArrayList();
    if (!bool)
    {
      if (useNavigationActions) {
        localArrayList.add("com.google.android.exoplayer.prev");
      }
      if (rewindMs > 0L) {
        localArrayList.add("com.google.android.exoplayer.rewind");
      }
    }
    if (usePlayPauseActions) {
      if (paramPlayer.getPlayWhenReady()) {
        localArrayList.add("com.google.android.exoplayer.pause");
      } else {
        localArrayList.add("com.google.android.exoplayer.play");
      }
    }
    Object localObject = this;
    if (!bool)
    {
      if (fastForwardMs > 0L) {
        localArrayList.add("com.google.android.exoplayer.ffwd");
      }
      bool = useNavigationActions;
      PlayerNotificationManager localPlayerNotificationManager = this;
      localObject = localPlayerNotificationManager;
      if (bool)
      {
        localObject = localPlayerNotificationManager;
        if (paramPlayer.getNextWindowIndex() != -1)
        {
          localArrayList.add("com.google.android.exoplayer.next");
          localObject = localPlayerNotificationManager;
        }
      }
    }
    if (customActionReceiver != null) {
      localArrayList.addAll(customActionReceiver.getCustomActions(paramPlayer));
    }
    if ("com.google.android.exoplayer.stop".equals(stopAction)) {
      localArrayList.add(stopAction);
    }
    return localArrayList;
  }
  
  public void invalidate()
  {
    if ((isNotificationStarted) && (player != null)) {
      updateNotification(null);
    }
  }
  
  public final void setBadgeIconType(int paramInt)
  {
    if (badgeIconType == paramInt) {
      return;
    }
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException();
    }
    badgeIconType = paramInt;
    invalidate();
  }
  
  public final void setColor(int paramInt)
  {
    if (color != paramInt)
    {
      color = paramInt;
      invalidate();
    }
  }
  
  public final void setColorized(boolean paramBoolean)
  {
    if (colorized != paramBoolean)
    {
      colorized = paramBoolean;
      invalidate();
    }
  }
  
  public final void setControlDispatcher(ControlDispatcher paramControlDispatcher)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a3 = a2\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  public final void setDefaults(int paramInt)
  {
    if (defaults != paramInt)
    {
      defaults = paramInt;
      invalidate();
    }
  }
  
  public final void setFastForwardIncrementMs(long paramLong)
  {
    if (fastForwardMs == paramLong) {
      return;
    }
    fastForwardMs = paramLong;
    invalidate();
  }
  
  public final void setMediaSessionToken(MediaSessionCompat.Token paramToken)
  {
    if (!Util.areEqual(mediaSessionToken, paramToken))
    {
      mediaSessionToken = paramToken;
      invalidate();
    }
  }
  
  public final void setNotificationListener(NotificationListener paramNotificationListener)
  {
    notificationListener = paramNotificationListener;
  }
  
  public final void setOngoing(boolean paramBoolean)
  {
    if (ongoing != paramBoolean)
    {
      ongoing = paramBoolean;
      invalidate();
    }
  }
  
  public final void setPlayer(Player paramPlayer)
  {
    Looper localLooper1 = Looper.myLooper();
    Looper localLooper2 = Looper.getMainLooper();
    boolean bool2 = false;
    boolean bool1;
    if (localLooper1 == localLooper2) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Assertions.checkState(bool1);
    if (paramPlayer != null)
    {
      bool1 = bool2;
      if (paramPlayer.getApplicationLooper() != Looper.getMainLooper()) {}
    }
    else
    {
      bool1 = true;
    }
    Assertions.checkArgument(bool1);
    if (player == paramPlayer) {
      return;
    }
    if (player != null)
    {
      player.removeListener(playerListener);
      if (paramPlayer == null) {
        stopNotification();
      }
    }
    player = paramPlayer;
    if (paramPlayer != null)
    {
      wasPlayWhenReady = paramPlayer.getPlayWhenReady();
      lastPlaybackState = paramPlayer.getPlaybackState();
      paramPlayer.addListener(playerListener);
      if (lastPlaybackState != 1) {
        startOrUpdateNotification();
      }
    }
  }
  
  public final void setPriority(int paramInt)
  {
    if (priority == paramInt) {
      return;
    }
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException();
    }
    priority = paramInt;
    invalidate();
  }
  
  public final void setRewindIncrementMs(long paramLong)
  {
    if (rewindMs == paramLong) {
      return;
    }
    rewindMs = paramLong;
    invalidate();
  }
  
  public final void setSmallIcon(int paramInt)
  {
    if (smallIconResourceId != paramInt)
    {
      smallIconResourceId = paramInt;
      invalidate();
    }
  }
  
  public final void setStopAction(String paramString)
  {
    if (Util.areEqual(paramString, stopAction)) {
      return;
    }
    stopAction = paramString;
    if ("com.google.android.exoplayer.stop".equals(paramString)) {
      stopPendingIntent = checkNotNullplaybackActions.get("com.google.android.exoplayer.stop"))).actionIntent;
    } else if (paramString != null) {
      stopPendingIntent = checkNotNullcustomActions.get(paramString))).actionIntent;
    } else {
      stopPendingIntent = null;
    }
    invalidate();
  }
  
  public final void setUseChronometer(boolean paramBoolean)
  {
    if (useChronometer != paramBoolean)
    {
      useChronometer = paramBoolean;
      invalidate();
    }
  }
  
  public final void setUseNavigationActions(boolean paramBoolean)
  {
    if (useNavigationActions != paramBoolean)
    {
      useNavigationActions = paramBoolean;
      invalidate();
    }
  }
  
  public final void setUsePlayPauseActions(boolean paramBoolean)
  {
    if (usePlayPauseActions != paramBoolean)
    {
      usePlayPauseActions = paramBoolean;
      invalidate();
    }
  }
  
  public final void setVisibility(int paramInt)
  {
    if (visibility == paramInt) {
      return;
    }
    switch (paramInt)
    {
    default: 
      throw new IllegalStateException();
    }
    visibility = paramInt;
    invalidate();
  }
  
  public final class BitmapCallback
  {
    private final int notificationTag;
    
    private BitmapCallback(int paramInt)
    {
      notificationTag = paramInt;
    }
    
    public void onBitmap(Bitmap paramBitmap)
    {
      if (paramBitmap != null) {
        mainHandler.post(new -..Lambda.PlayerNotificationManager.BitmapCallback.ai-lvTgLEQ8d7uyKftaUKVPjkgA(this, paramBitmap));
      }
    }
  }
  
  public abstract interface CustomActionReceiver
  {
    public abstract Map createCustomActions(Context paramContext, int paramInt);
    
    public abstract List getCustomActions(Player paramPlayer);
    
    public abstract void onCustomAction(Player paramPlayer, String paramString, Intent paramIntent);
  }
  
  public abstract interface MediaDescriptionAdapter
  {
    public abstract PendingIntent createCurrentContentIntent(Player paramPlayer);
    
    public abstract String getCurrentContentText(Player paramPlayer);
    
    public abstract String getCurrentContentTitle(Player paramPlayer);
    
    public abstract Bitmap getCurrentLargeIcon(Player paramPlayer, PlayerNotificationManager.BitmapCallback paramBitmapCallback);
  }
  
  class NotificationBroadcastReceiver
    extends BroadcastReceiver
  {
    private final Timeline.Window window = new Timeline.Window();
    
    public NotificationBroadcastReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramContext = player;
      if ((paramContext != null) && (isNotificationStarted))
      {
        if (paramIntent.getIntExtra("INSTANCE_ID", instanceId) != instanceId) {
          return;
        }
        String str = paramIntent.getAction();
        if ((!"com.google.android.exoplayer.play".equals(str)) && (!"com.google.android.exoplayer.pause".equals(str)))
        {
          if ((!"com.google.android.exoplayer.ffwd".equals(str)) && (!"com.google.android.exoplayer.rewind".equals(str)))
          {
            int i;
            if ("com.google.android.exoplayer.next".equals(str))
            {
              i = paramContext.getNextWindowIndex();
              if (i != -1) {
                controlDispatcher.dispatchSeekTo(paramContext, i, -9223372036854775807L);
              }
            }
            else
            {
              if ("com.google.android.exoplayer.prev".equals(str))
              {
                paramContext.getCurrentTimeline().getWindow(paramContext.getCurrentWindowIndex(), window);
                i = paramContext.getPreviousWindowIndex();
                if ((i != -1) && ((paramContext.getCurrentPosition() <= 3000L) || ((window.isDynamic) && (!window.isSeekable))))
                {
                  controlDispatcher.dispatchSeekTo(paramContext, i, -9223372036854775807L);
                  return;
                }
                controlDispatcher.dispatchSeekTo(paramContext, paramContext.getCurrentWindowIndex(), -9223372036854775807L);
                return;
              }
              if ("com.google.android.exoplayer.stop".equals(str))
              {
                controlDispatcher.dispatchStop(paramContext, true);
                PlayerNotificationManager.this.stopNotification();
                return;
              }
              if ((customActionReceiver != null) && (customActions.containsKey(str))) {
                customActionReceiver.onCustomAction(paramContext, str, paramIntent);
              }
            }
          }
          else
          {
            long l;
            if ("com.google.android.exoplayer.ffwd".equals(str)) {
              l = fastForwardMs;
            } else {
              l = -rewindMs;
            }
            controlDispatcher.dispatchSeekTo(paramContext, paramContext.getCurrentWindowIndex(), paramContext.getCurrentPosition() + l);
          }
        }
        else {
          controlDispatcher.dispatchSetPlayWhenReady(paramContext, "com.google.android.exoplayer.play".equals(str));
        }
      }
    }
  }
  
  public abstract interface NotificationListener
  {
    public abstract void onNotificationCancelled(int paramInt);
    
    public abstract void onNotificationStarted(int paramInt, Notification paramNotification);
  }
  
  class PlayerListener
    implements Player.EventListener
  {
    private PlayerListener() {}
    
    public void onPlaybackParametersChanged(PlaybackParameters paramPlaybackParameters)
    {
      if (player != null)
      {
        if (player.getPlaybackState() == 1) {
          return;
        }
        PlayerNotificationManager.this.startOrUpdateNotification();
      }
    }
    
    public void onPlayerStateChanged(boolean paramBoolean, int paramInt)
    {
      if (((wasPlayWhenReady != paramBoolean) && (paramInt != 1)) || (lastPlaybackState != paramInt)) {
        PlayerNotificationManager.this.startOrUpdateNotification();
      }
      PlayerNotificationManager.access$702(PlayerNotificationManager.this, paramBoolean);
      PlayerNotificationManager.access$802(PlayerNotificationManager.this, paramInt);
    }
    
    public void onPositionDiscontinuity(int paramInt)
    {
      PlayerNotificationManager.this.startOrUpdateNotification();
    }
    
    public void onRepeatModeChanged(int paramInt)
    {
      if (player != null)
      {
        if (player.getPlaybackState() == 1) {
          return;
        }
        PlayerNotificationManager.this.startOrUpdateNotification();
      }
    }
    
    public void onTimelineChanged(Timeline paramTimeline, Object paramObject, int paramInt)
    {
      if (player != null)
      {
        if (player.getPlaybackState() == 1) {
          return;
        }
        PlayerNotificationManager.this.startOrUpdateNotification();
      }
    }
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public @interface Priority {}
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public @interface Visibility {}
}
