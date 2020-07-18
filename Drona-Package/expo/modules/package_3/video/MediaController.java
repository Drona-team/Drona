package expo.modules.package_3.video;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityRecord;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import expo.modules.package_3.R.drawable;
import expo.modules.package_3.R.id;
import expo.modules.package_3.R.layout;
import expo.modules.package_3.player.PlayerDataControl;
import java.lang.ref.WeakReference;
import java.util.Formatter;
import java.util.Locale;

public class MediaController
  extends FrameLayout
{
  private static final int FADE_OUT = 1;
  private static final int SHOW_PROGRESS = 2;
  private static final int sDefaultTimeout = 3000;
  private ViewGroup mAnchor;
  private Context mContext;
  private TextView mCurrentTime;
  private boolean mDragging;
  private TextView mEndTime;
  private ImageButton mFastForwardButton;
  private View.OnClickListener mFfwdListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (mPlayer == null) {
        return;
      }
      int i = mPlayer.getCurrentPosition();
      mPlayer.seekTo(i + 15000);
      MediaController.this.setProgress();
      show(3000);
    }
  };
  private StringBuilder mFormatBuilder;
  private Formatter mFormatter;
  private boolean mFromXml;
  private ImageButton mFullscreenButton;
  private View.OnClickListener mFullscreenListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      show(3000);
      MediaController.this.doToggleFullscreen();
    }
  };
  private Handler mHandler = new MessageHandler();
  private boolean mListenersSet;
  private ImageButton mNextButton;
  private View.OnClickListener mNextListener;
  private ImageButton mPauseButton;
  private View.OnClickListener mPauseListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      show(3000);
      MediaController.this.doPauseResume();
    }
  };
  private PlayerDataControl mPlayer;
  private ImageButton mPrevButton;
  private View.OnClickListener mPrevListener;
  private ProgressBar mProgress;
  private View.OnClickListener mRewListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (mPlayer == null) {
        return;
      }
      int i = mPlayer.getCurrentPosition();
      mPlayer.seekTo(i - 5000);
      MediaController.this.setProgress();
      show(3000);
    }
  };
  private ImageButton mRewindButton;
  private View mRoot;
  private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener()
  {
    public void onProgressChanged(SeekBar paramAnonymousSeekBar, int paramAnonymousInt, boolean paramAnonymousBoolean)
    {
      if (mPlayer == null) {
        return;
      }
      if (!paramAnonymousBoolean) {
        return;
      }
      long l = mPlayer.getDuration() * paramAnonymousInt / 1000L;
      paramAnonymousSeekBar = mPlayer;
      paramAnonymousInt = (int)l;
      paramAnonymousSeekBar.seekTo(paramAnonymousInt);
      if (mCurrentTime != null) {
        mCurrentTime.setText(MediaController.this.stringForTime(paramAnonymousInt));
      }
    }
    
    public void onStartTrackingTouch(SeekBar paramAnonymousSeekBar)
    {
      show(3600000);
      MediaController.access$202(MediaController.this, true);
      mHandler.removeMessages(2);
    }
    
    public void onStopTrackingTouch(SeekBar paramAnonymousSeekBar)
    {
      MediaController.access$202(MediaController.this, false);
      MediaController.this.setProgress();
      updatePausePlay();
      show(3000);
      mHandler.sendEmptyMessage(2);
    }
  };
  private boolean mShowing;
  private boolean mUseFastForward;
  
  public MediaController(Context paramContext)
  {
    this(paramContext, true);
  }
  
  public MediaController(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    mRoot = null;
    mContext = paramContext;
    mUseFastForward = true;
    mFromXml = true;
  }
  
  public MediaController(Context paramContext, boolean paramBoolean)
  {
    super(paramContext);
    mContext = paramContext;
    mUseFastForward = paramBoolean;
  }
  
  private void disableUnsupportedButtons()
  {
    if (mPlayer == null) {
      return;
    }
    try
    {
      ImageButton localImageButton = mPauseButton;
      boolean bool;
      if (localImageButton != null)
      {
        bool = mPlayer.canPause();
        if (!bool) {
          mPauseButton.setEnabled(false);
        }
      }
      localImageButton = mRewindButton;
      if (localImageButton != null)
      {
        bool = mPlayer.canSeekBackward();
        if (!bool) {
          mRewindButton.setEnabled(false);
        }
      }
      localImageButton = mFastForwardButton;
      if (localImageButton != null)
      {
        bool = mPlayer.canSeekForward();
        if (!bool)
        {
          mFastForwardButton.setEnabled(false);
          return;
        }
      }
    }
    catch (IncompatibleClassChangeError localIncompatibleClassChangeError) {}
  }
  
  private void doPauseResume()
  {
    if (mPlayer == null) {
      return;
    }
    if (mPlayer.isPlaying()) {
      mPlayer.pause();
    } else {
      mPlayer.start();
    }
    updatePausePlay();
  }
  
  private void doToggleFullscreen()
  {
    if (mPlayer == null) {
      return;
    }
    mPlayer.toggleFullscreen();
  }
  
  private void initControllerView(View paramView)
  {
    mPauseButton = ((ImageButton)paramView.findViewById(R.id.play_button));
    if (mPauseButton != null)
    {
      mPauseButton.requestFocus();
      mPauseButton.setOnClickListener(mPauseListener);
    }
    mFullscreenButton = ((ImageButton)paramView.findViewById(R.id.fullscreen_mode_button));
    if (mFullscreenButton != null)
    {
      mFullscreenButton.requestFocus();
      mFullscreenButton.setOnClickListener(mFullscreenListener);
    }
    mFastForwardButton = ((ImageButton)paramView.findViewById(R.id.fast_forward_button));
    ImageButton localImageButton = mFastForwardButton;
    int j = 0;
    int i;
    if (localImageButton != null)
    {
      mFastForwardButton.setOnClickListener(mFfwdListener);
      if (!mFromXml)
      {
        localImageButton = mFastForwardButton;
        if (mUseFastForward) {
          i = 0;
        } else {
          i = 8;
        }
        localImageButton.setVisibility(i);
      }
    }
    mRewindButton = ((ImageButton)paramView.findViewById(R.id.rewind_button));
    if (mRewindButton != null)
    {
      mRewindButton.setOnClickListener(mRewListener);
      if (!mFromXml)
      {
        localImageButton = mRewindButton;
        if (mUseFastForward) {
          i = j;
        } else {
          i = 8;
        }
        localImageButton.setVisibility(i);
      }
    }
    mNextButton = ((ImageButton)paramView.findViewById(R.id.skip_next_button));
    if ((mNextButton != null) && (!mFromXml) && (!mListenersSet)) {
      mNextButton.setVisibility(8);
    }
    mPrevButton = ((ImageButton)paramView.findViewById(R.id.skip_previous_button));
    if ((mPrevButton != null) && (!mFromXml) && (!mListenersSet)) {
      mPrevButton.setVisibility(8);
    }
    mProgress = ((ProgressBar)paramView.findViewById(R.id.seek_bar));
    if (mProgress != null)
    {
      if ((mProgress instanceof SeekBar)) {
        ((SeekBar)mProgress).setOnSeekBarChangeListener(mSeekListener);
      }
      mProgress.setMax(1000);
    }
    mEndTime = ((TextView)paramView.findViewById(R.id.end_time_text));
    mCurrentTime = ((TextView)paramView.findViewById(R.id.current_time_text));
    mFormatBuilder = new StringBuilder();
    mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    installPrevNextListeners();
  }
  
  private void installPrevNextListeners()
  {
    ImageButton localImageButton = mNextButton;
    boolean bool2 = false;
    boolean bool1;
    if (localImageButton != null)
    {
      mNextButton.setOnClickListener(mNextListener);
      localImageButton = mNextButton;
      if (mNextListener != null) {
        bool1 = true;
      } else {
        bool1 = false;
      }
      localImageButton.setEnabled(bool1);
    }
    if (mPrevButton != null)
    {
      mPrevButton.setOnClickListener(mPrevListener);
      localImageButton = mPrevButton;
      bool1 = bool2;
      if (mPrevListener != null) {
        bool1 = true;
      }
      localImageButton.setEnabled(bool1);
    }
  }
  
  private int setProgress()
  {
    int i;
    if ((mPlayer != null) && (!mDragging))
    {
      i = mPlayer.getCurrentPosition();
      int j = mPlayer.getDuration();
      if (mProgress != null)
      {
        if (j > 0)
        {
          long l = i * 1000L / j;
          mProgress.setProgress((int)l);
        }
        int k = mPlayer.getBufferPercentage();
        mProgress.setSecondaryProgress(k * 10);
      }
      if (mEndTime != null) {
        mEndTime.setText(stringForTime(j));
      }
      if (mCurrentTime != null)
      {
        mCurrentTime.setText(stringForTime(i));
        return i;
      }
    }
    else
    {
      return 0;
    }
    return i;
  }
  
  private String stringForTime(int paramInt)
  {
    int j = paramInt / 1000;
    paramInt = j % 60;
    int i = j / 60 % 60;
    j /= 3600;
    mFormatBuilder.setLength(0);
    if (j > 0) {
      return mFormatter.format("%d:%02d:%02d", new Object[] { Integer.valueOf(j), Integer.valueOf(i), Integer.valueOf(paramInt) }).toString();
    }
    return mFormatter.format("%02d:%02d", new Object[] { Integer.valueOf(i), Integer.valueOf(paramInt) }).toString();
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if (mPlayer != null)
    {
      if (!isEnabled()) {
        return true;
      }
      int j = paramKeyEvent.getKeyCode();
      int i;
      if ((paramKeyEvent.getRepeatCount() == 0) && (paramKeyEvent.getAction() == 0)) {
        i = 1;
      } else {
        i = 0;
      }
      if ((j != 79) && (j != 85) && (j != 62))
      {
        if (j == 126)
        {
          if ((i != 0) && (!mPlayer.isPlaying()))
          {
            mPlayer.start();
            updatePausePlay();
            show(3000);
            return true;
          }
        }
        else if ((j != 86) && (j != 127))
        {
          if ((j != 25) && (j != 24) && (j != 164))
          {
            if ((j != 4) && (j != 82))
            {
              show(3000);
              return super.dispatchKeyEvent(paramKeyEvent);
            }
            if (i != 0)
            {
              hide();
              return true;
            }
          }
          else
          {
            return super.dispatchKeyEvent(paramKeyEvent);
          }
        }
        else if ((i != 0) && (mPlayer.isPlaying()))
        {
          mPlayer.pause();
          updatePausePlay();
          show(3000);
          return true;
        }
      }
      else if (i != 0)
      {
        doPauseResume();
        show(3000);
        if (mPauseButton != null) {
          mPauseButton.requestFocus();
        }
      }
    }
    return true;
  }
  
  public void hide()
  {
    if (mAnchor == null) {
      return;
    }
    Object localObject = mAnchor;
    try
    {
      ((ViewGroup)localObject).removeView(this);
      localObject = mHandler;
      ((Handler)localObject).removeMessages(2);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;) {}
    }
    Log.w("MediaController", "already removed");
    mShowing = false;
  }
  
  public boolean isShowing()
  {
    return mShowing;
  }
  
  protected View makeControllerView()
  {
    mRoot = ((LayoutInflater)mContext.getSystemService("layout_inflater")).inflate(R.layout.expo_media_controller, null);
    initControllerView(mRoot);
    return mRoot;
  }
  
  public void onFinishInflate()
  {
    if (mRoot != null) {
      initControllerView(mRoot);
    }
    super.onFinishInflate();
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    paramAccessibilityEvent.setClassName(expo.modules.av.video.MediaController.class.getName());
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
    paramAccessibilityNodeInfo.setClassName(expo.modules.av.video.MediaController.class.getName());
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (isEnabled()) {
      show(3000);
    }
    return true;
  }
  
  public boolean onTrackballEvent(MotionEvent paramMotionEvent)
  {
    if (isEnabled()) {
      show(3000);
    }
    return false;
  }
  
  public void setAnchorView(ViewGroup paramViewGroup)
  {
    mAnchor = paramViewGroup;
    if (mRoot == null)
    {
      paramViewGroup = new FrameLayout.LayoutParams(-1, -2, 80);
      removeAllViews();
      addView(makeControllerView(), paramViewGroup);
    }
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    if (mPauseButton != null) {
      mPauseButton.setEnabled(paramBoolean);
    }
    if (mFastForwardButton != null) {
      mFastForwardButton.setEnabled(paramBoolean);
    }
    if (mRewindButton != null) {
      mRewindButton.setEnabled(paramBoolean);
    }
    ImageButton localImageButton = mNextButton;
    boolean bool2 = true;
    boolean bool1;
    if (localImageButton != null)
    {
      localImageButton = mNextButton;
      if ((paramBoolean) && (mNextListener != null)) {
        bool1 = true;
      } else {
        bool1 = false;
      }
      localImageButton.setEnabled(bool1);
    }
    if (mPrevButton != null)
    {
      localImageButton = mPrevButton;
      if ((paramBoolean) && (mPrevListener != null)) {
        bool1 = bool2;
      } else {
        bool1 = false;
      }
      localImageButton.setEnabled(bool1);
    }
    if (mProgress != null) {
      mProgress.setEnabled(paramBoolean);
    }
    disableUnsupportedButtons();
    super.setEnabled(paramBoolean);
  }
  
  public void setMediaPlayer(PlayerDataControl paramPlayerDataControl)
  {
    mPlayer = paramPlayerDataControl;
    updateControls();
  }
  
  public void setPrevNextListeners(View.OnClickListener paramOnClickListener1, View.OnClickListener paramOnClickListener2)
  {
    mNextListener = paramOnClickListener1;
    mPrevListener = paramOnClickListener2;
    mListenersSet = true;
    if (mRoot != null)
    {
      installPrevNextListeners();
      if ((mNextButton != null) && (!mFromXml)) {
        mNextButton.setVisibility(0);
      }
      if ((mPrevButton != null) && (!mFromXml)) {
        mPrevButton.setVisibility(0);
      }
    }
  }
  
  public void show()
  {
    show(3000);
  }
  
  public void show(int paramInt)
  {
    if ((!mShowing) && (mAnchor != null))
    {
      setProgress();
      if (mPauseButton != null) {
        mPauseButton.requestFocus();
      }
      disableUnsupportedButtons();
      localObject = new FrameLayout.LayoutParams(-1, -1);
      mAnchor.addView(this, (ViewGroup.LayoutParams)localObject);
      mShowing = true;
    }
    updateControls();
    mHandler.sendEmptyMessage(2);
    Object localObject = mHandler.obtainMessage(1);
    if (paramInt != 0)
    {
      mHandler.removeMessages(1);
      mHandler.sendMessageDelayed((Message)localObject, paramInt);
    }
  }
  
  public void updateControls()
  {
    setProgress();
    updatePausePlay();
    updateFullScreen();
  }
  
  public void updateFullScreen()
  {
    if ((mRoot != null) && (mFullscreenButton != null))
    {
      if (mPlayer == null) {
        return;
      }
      if (mPlayer.isFullscreen())
      {
        mFullscreenButton.setImageResource(R.drawable.ic_fullscreen_exit_32dp);
        return;
      }
      mFullscreenButton.setImageResource(R.drawable.ic_fullscreen_32dp);
    }
  }
  
  public void updatePausePlay()
  {
    if ((mRoot != null) && (mPauseButton != null))
    {
      if (mPlayer == null) {
        return;
      }
      if (mPlayer.isPlaying())
      {
        mPauseButton.setImageResource(R.drawable.exo_controls_pause);
        return;
      }
      mPauseButton.setImageResource(R.drawable.exo_controls_play);
    }
  }
  
  class MessageHandler
    extends Handler
  {
    private final WeakReference<expo.modules.av.video.MediaController> mView;
    
    MessageHandler()
    {
      mView = new WeakReference(this$1);
    }
    
    public void handleMessage(Message paramMessage)
    {
      MediaController localMediaController = (MediaController)mView.get();
      if (localMediaController != null)
      {
        if (mPlayer == null) {
          return;
        }
        switch (what)
        {
        default: 
          
        case 2: 
          int i = localMediaController.setProgress();
          if ((!mDragging) && (mShowing) && (mPlayer.isPlaying()))
          {
            sendMessageDelayed(obtainMessage(2), 1000 - i % 1000);
            return;
          }
          break;
        case 1: 
          localMediaController.hide();
        }
      }
    }
  }
}
