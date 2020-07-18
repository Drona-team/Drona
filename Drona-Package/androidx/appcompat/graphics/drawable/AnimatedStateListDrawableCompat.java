package androidx.appcompat.graphics.drawable;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StateSet;
import android.util.Xml;
import androidx.appcompat.resources.R.styleable;
import androidx.appcompat.widget.ResourceManagerInternal;
import androidx.collection.LongSparseArray;
import androidx.collection.SparseArrayCompat;
import androidx.core.content.delay.TypedArrayUtils;
import androidx.core.graphics.drawable.TintAwareDrawable;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@SuppressLint({"RestrictedAPI"})
public class AnimatedStateListDrawableCompat
  extends StateListDrawable
  implements TintAwareDrawable
{
  private static final String ELEMENT_ITEM = "item";
  private static final String ELEMENT_TRANSITION = "transition";
  private static final String ITEM_MISSING_DRAWABLE_ERROR = ": <item> tag requires a 'drawable' attribute or child tag defining a drawable";
  private static final String LOGTAG = "AnimatedStateListDrawableCompat";
  private static final String TRANSITION_MISSING_DRAWABLE_ERROR = ": <transition> tag requires a 'drawable' attribute or child tag defining a drawable";
  private static final String TRANSITION_MISSING_FROM_TO_ID = ": <transition> tag requires 'fromId' & 'toId' attributes";
  private boolean mMutated;
  private AnimatedStateListState mState;
  private Transition mTransition;
  private int mTransitionFromIndex = -1;
  private int mTransitionToIndex = -1;
  
  public AnimatedStateListDrawableCompat()
  {
    this(null, null);
  }
  
  AnimatedStateListDrawableCompat(AnimatedStateListState paramAnimatedStateListState, Resources paramResources)
  {
    super(null);
    setConstantState(new AnimatedStateListState(paramAnimatedStateListState, this, paramResources));
    onStateChange(getState());
    jumpToCurrentState();
  }
  
  public static AnimatedStateListDrawableCompat create(Context paramContext, int paramInt, Resources.Theme paramTheme)
  {
    try
    {
      Resources localResources = paramContext.getResources();
      XmlResourceParser localXmlResourceParser = localResources.getXml(paramInt);
      AttributeSet localAttributeSet = Xml.asAttributeSet(localXmlResourceParser);
      do
      {
        paramInt = localXmlResourceParser.next();
      } while ((paramInt != 2) && (paramInt != 1));
      if (paramInt == 2)
      {
        paramContext = createFromXmlInner(paramContext, localResources, localXmlResourceParser, localAttributeSet, paramTheme);
        return paramContext;
      }
      paramContext = new XmlPullParserException("No start tag found");
      throw paramContext;
    }
    catch (IOException paramContext)
    {
      Log.e(LOGTAG, "parser error", paramContext);
    }
    catch (XmlPullParserException paramContext)
    {
      Log.e(LOGTAG, "parser error", paramContext);
    }
    return null;
  }
  
  public static AnimatedStateListDrawableCompat createFromXmlInner(Context paramContext, Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws IOException, XmlPullParserException
  {
    Object localObject = paramXmlPullParser.getName();
    if (((String)localObject).equals("animated-selector"))
    {
      localObject = new AnimatedStateListDrawableCompat();
      ((AnimatedStateListDrawableCompat)localObject).inflate(paramContext, paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
      return localObject;
    }
    paramContext = new StringBuilder();
    paramContext.append(paramXmlPullParser.getPositionDescription());
    paramContext.append(": invalid animated-selector tag ");
    paramContext.append((String)localObject);
    throw new XmlPullParserException(paramContext.toString());
  }
  
  private void inflateChildElements(Context paramContext, Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlPullParser.getDepth() + 1;
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if (j == 1) {
        break;
      }
      int k = paramXmlPullParser.getDepth();
      if ((k < i) && (j == 3)) {
        break;
      }
      if ((j == 2) && (k <= i)) {
        if (paramXmlPullParser.getName().equals("item")) {
          parseItem(paramContext, paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
        } else if (paramXmlPullParser.getName().equals("transition")) {
          parseTransition(paramContext, paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
        }
      }
    }
  }
  
  private void init()
  {
    onStateChange(getState());
  }
  
  private int parseItem(Context paramContext, Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    Object localObject = TypedArrayUtils.obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.AnimatedStateListDrawableItem);
    int i = ((TypedArray)localObject).getResourceId(R.styleable.AnimatedStateListDrawableItem_android_id, 0);
    int j = ((TypedArray)localObject).getResourceId(R.styleable.AnimatedStateListDrawableItem_android_drawable, -1);
    Drawable localDrawable;
    if (j > 0) {
      localDrawable = ResourceManagerInternal.get().getDrawable(paramContext, j);
    } else {
      localDrawable = null;
    }
    ((TypedArray)localObject).recycle();
    localObject = extractStateSet(paramAttributeSet);
    paramContext = localDrawable;
    if (localDrawable == null)
    {
      do
      {
        j = paramXmlPullParser.next();
      } while (j == 4);
      if (j == 2)
      {
        if (paramXmlPullParser.getName().equals("vector")) {
          paramContext = VectorDrawableCompat.createFromXmlInner(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
        } else if (Build.VERSION.SDK_INT >= 21) {
          paramContext = Drawable.createFromXmlInner(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
        } else {
          paramContext = Drawable.createFromXmlInner(paramResources, paramXmlPullParser, paramAttributeSet);
        }
      }
      else
      {
        paramContext = new StringBuilder();
        paramContext.append(paramXmlPullParser.getPositionDescription());
        paramContext.append(": <item> tag requires a 'drawable' attribute or child tag defining a drawable");
        throw new XmlPullParserException(paramContext.toString());
      }
    }
    if (paramContext != null) {
      return mState.addStateSet((int[])localObject, paramContext, i);
    }
    paramContext = new StringBuilder();
    paramContext.append(paramXmlPullParser.getPositionDescription());
    paramContext.append(": <item> tag requires a 'drawable' attribute or child tag defining a drawable");
    throw new XmlPullParserException(paramContext.toString());
  }
  
  private int parseTransition(Context paramContext, Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    Object localObject = TypedArrayUtils.obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.AnimatedStateListDrawableTransition);
    int i = ((TypedArray)localObject).getResourceId(R.styleable.AnimatedStateListDrawableTransition_android_fromId, -1);
    int j = ((TypedArray)localObject).getResourceId(R.styleable.AnimatedStateListDrawableTransition_android_toId, -1);
    int k = ((TypedArray)localObject).getResourceId(R.styleable.AnimatedStateListDrawableTransition_android_drawable, -1);
    Drawable localDrawable;
    if (k > 0) {
      localDrawable = ResourceManagerInternal.get().getDrawable(paramContext, k);
    } else {
      localDrawable = null;
    }
    boolean bool = ((TypedArray)localObject).getBoolean(R.styleable.AnimatedStateListDrawableTransition_android_reversible, false);
    ((TypedArray)localObject).recycle();
    localObject = localDrawable;
    if (localDrawable == null)
    {
      do
      {
        k = paramXmlPullParser.next();
      } while (k == 4);
      if (k == 2)
      {
        if (paramXmlPullParser.getName().equals("animated-vector")) {
          localObject = AnimatedVectorDrawableCompat.createFromXmlInner(paramContext, paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
        } else if (Build.VERSION.SDK_INT >= 21) {
          localObject = Drawable.createFromXmlInner(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
        } else {
          localObject = Drawable.createFromXmlInner(paramResources, paramXmlPullParser, paramAttributeSet);
        }
      }
      else
      {
        paramContext = new StringBuilder();
        paramContext.append(paramXmlPullParser.getPositionDescription());
        paramContext.append(": <transition> tag requires a 'drawable' attribute or child tag defining a drawable");
        throw new XmlPullParserException(paramContext.toString());
      }
    }
    if (localObject != null)
    {
      if ((i != -1) && (j != -1)) {
        return mState.addTransition(i, j, (Drawable)localObject, bool);
      }
      paramContext = new StringBuilder();
      paramContext.append(paramXmlPullParser.getPositionDescription());
      paramContext.append(": <transition> tag requires 'fromId' & 'toId' attributes");
      throw new XmlPullParserException(paramContext.toString());
    }
    paramContext = new StringBuilder();
    paramContext.append(paramXmlPullParser.getPositionDescription());
    paramContext.append(": <transition> tag requires a 'drawable' attribute or child tag defining a drawable");
    throw new XmlPullParserException(paramContext.toString());
  }
  
  private boolean selectTransition(int paramInt)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a14 = a13\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:552)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:1)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:166)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:331)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:387)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:90)\n\t... 17 more\n");
  }
  
  private void updateStateFromTypedArray(TypedArray paramTypedArray)
  {
    AnimatedStateListState localAnimatedStateListState = mState;
    if (Build.VERSION.SDK_INT >= 21) {
      mChangingConfigurations |= paramTypedArray.getChangingConfigurations();
    }
    localAnimatedStateListState.setVariablePadding(paramTypedArray.getBoolean(R.styleable.AnimatedStateListDrawableCompat_android_variablePadding, mVariablePadding));
    localAnimatedStateListState.setConstantSize(paramTypedArray.getBoolean(R.styleable.AnimatedStateListDrawableCompat_android_constantSize, mConstantSize));
    localAnimatedStateListState.setEnterFadeDuration(paramTypedArray.getInt(R.styleable.AnimatedStateListDrawableCompat_android_enterFadeDuration, mEnterFadeDuration));
    localAnimatedStateListState.setExitFadeDuration(paramTypedArray.getInt(R.styleable.AnimatedStateListDrawableCompat_android_exitFadeDuration, mExitFadeDuration));
    setDither(paramTypedArray.getBoolean(R.styleable.AnimatedStateListDrawableCompat_android_dither, mDither));
  }
  
  public void addState(int[] paramArrayOfInt, Drawable paramDrawable, int paramInt)
  {
    if (paramDrawable != null)
    {
      mState.addStateSet(paramArrayOfInt, paramDrawable, paramInt);
      onStateChange(getState());
      return;
    }
    throw new IllegalArgumentException("Drawable must not be null");
  }
  
  public void addTransition(int paramInt1, int paramInt2, Drawable paramDrawable, boolean paramBoolean)
  {
    if (paramDrawable != null)
    {
      mState.addTransition(paramInt1, paramInt2, paramDrawable, paramBoolean);
      return;
    }
    throw new IllegalArgumentException("Transition drawable must not be null");
  }
  
  void clearMutated()
  {
    super.clearMutated();
    mMutated = false;
  }
  
  AnimatedStateListState cloneConstantState()
  {
    return new AnimatedStateListState(mState, this, null);
  }
  
  public void inflate(Context paramContext, Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    TypedArray localTypedArray = TypedArrayUtils.obtainAttributes(paramResources, paramTheme, paramAttributeSet, R.styleable.AnimatedStateListDrawableCompat);
    setVisible(localTypedArray.getBoolean(R.styleable.AnimatedStateListDrawableCompat_android_visible, true), true);
    updateStateFromTypedArray(localTypedArray);
    updateDensity(paramResources);
    localTypedArray.recycle();
    inflateChildElements(paramContext, paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    init();
  }
  
  public boolean isStateful()
  {
    return true;
  }
  
  public void jumpToCurrentState()
  {
    super.jumpToCurrentState();
    if (mTransition != null)
    {
      mTransition.stop();
      mTransition = null;
      selectDrawable(mTransitionToIndex);
      mTransitionToIndex = -1;
      mTransitionFromIndex = -1;
    }
  }
  
  public Drawable mutate()
  {
    if ((!mMutated) && (super.mutate() == this))
    {
      mState.mutate();
      mMutated = true;
    }
    return this;
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    int i = mState.indexOfKeyframe(paramArrayOfInt);
    boolean bool1;
    if ((i != getCurrentIndex()) && ((selectTransition(i)) || (selectDrawable(i)))) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Drawable localDrawable = getCurrent();
    boolean bool2 = bool1;
    if (localDrawable != null) {
      bool2 = bool1 | localDrawable.setState(paramArrayOfInt);
    }
    return bool2;
  }
  
  void setConstantState(DrawableContainer.DrawableContainerState paramDrawableContainerState)
  {
    super.setConstantState(paramDrawableContainerState);
    if ((paramDrawableContainerState instanceof AnimatedStateListState)) {
      mState = ((AnimatedStateListState)paramDrawableContainerState);
    }
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = super.setVisible(paramBoolean1, paramBoolean2);
    if ((mTransition != null) && ((bool) || (paramBoolean2)))
    {
      if (paramBoolean1)
      {
        mTransition.start();
        return bool;
      }
      jumpToCurrentState();
    }
    return bool;
  }
  
  private static class AnimatableTransition
    extends AnimatedStateListDrawableCompat.Transition
  {
    private final Animatable watchdog;
    
    AnimatableTransition(Animatable paramAnimatable)
    {
      super();
      watchdog = paramAnimatable;
    }
    
    public void start()
    {
      watchdog.start();
    }
    
    public void stop()
    {
      watchdog.stop();
    }
  }
  
  static class AnimatedStateListState
    extends StateListDrawable.StateListState
  {
    private static final long REVERSED_BIT = 4294967296L;
    private static final long REVERSIBLE_FLAG_BIT = 8589934592L;
    SparseArrayCompat<Integer> mStateIds;
    LongSparseArray<Long> mTransitions;
    
    AnimatedStateListState(AnimatedStateListState paramAnimatedStateListState, AnimatedStateListDrawableCompat paramAnimatedStateListDrawableCompat, Resources paramResources)
    {
      super(paramAnimatedStateListDrawableCompat, paramResources);
      if (paramAnimatedStateListState != null)
      {
        mTransitions = mTransitions;
        mStateIds = mStateIds;
        return;
      }
      mTransitions = new LongSparseArray();
      mStateIds = new SparseArrayCompat();
    }
    
    private static long generateTransitionKey(int paramInt1, int paramInt2)
    {
      long l = paramInt1;
      return paramInt2 | l << 32;
    }
    
    int addStateSet(int[] paramArrayOfInt, Drawable paramDrawable, int paramInt)
    {
      int i = super.addStateSet(paramArrayOfInt, paramDrawable);
      mStateIds.put(i, Integer.valueOf(paramInt));
      return i;
    }
    
    int addTransition(int paramInt1, int paramInt2, Drawable paramDrawable, boolean paramBoolean)
    {
      int i = super.addChild(paramDrawable);
      long l2 = generateTransitionKey(paramInt1, paramInt2);
      long l1;
      if (paramBoolean) {
        l1 = 8589934592L;
      } else {
        l1 = 0L;
      }
      paramDrawable = mTransitions;
      long l3 = i;
      paramDrawable.append(l2, Long.valueOf(l3 | l1));
      if (paramBoolean)
      {
        l2 = generateTransitionKey(paramInt2, paramInt1);
        mTransitions.append(l2, Long.valueOf(0x100000000 | l3 | l1));
      }
      return i;
    }
    
    int getKeyframeIdAt(int paramInt)
    {
      if (paramInt < 0) {
        return 0;
      }
      return ((Integer)mStateIds.get(paramInt, Integer.valueOf(0))).intValue();
    }
    
    int indexOfKeyframe(int[] paramArrayOfInt)
    {
      int i = super.indexOfStateSet(paramArrayOfInt);
      if (i >= 0) {
        return i;
      }
      return super.indexOfStateSet(StateSet.WILD_CARD);
    }
    
    int indexOfTransition(int paramInt1, int paramInt2)
    {
      long l = generateTransitionKey(paramInt1, paramInt2);
      return (int)((Long)mTransitions.get(l, Long.valueOf(-1L))).longValue();
    }
    
    boolean isTransitionReversed(int paramInt1, int paramInt2)
    {
      long l = generateTransitionKey(paramInt1, paramInt2);
      return (((Long)mTransitions.get(l, Long.valueOf(-1L))).longValue() & 0x100000000) != 0L;
    }
    
    void mutate()
    {
      mTransitions = mTransitions.clone();
      mStateIds = mStateIds.clone();
    }
    
    public Drawable newDrawable()
    {
      return new AnimatedStateListDrawableCompat(this, null);
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return new AnimatedStateListDrawableCompat(this, paramResources);
    }
    
    boolean transitionHasReversibleFlag(int paramInt1, int paramInt2)
    {
      long l = generateTransitionKey(paramInt1, paramInt2);
      return (((Long)mTransitions.get(l, Long.valueOf(-1L))).longValue() & 0x200000000) != 0L;
    }
  }
  
  private static class AnimatedVectorDrawableTransition
    extends AnimatedStateListDrawableCompat.Transition
  {
    private final AnimatedVectorDrawableCompat mAvd;
    
    AnimatedVectorDrawableTransition(AnimatedVectorDrawableCompat paramAnimatedVectorDrawableCompat)
    {
      super();
      mAvd = paramAnimatedVectorDrawableCompat;
    }
    
    public void start()
    {
      mAvd.start();
    }
    
    public void stop()
    {
      mAvd.stop();
    }
  }
  
  private static class AnimationDrawableTransition
    extends AnimatedStateListDrawableCompat.Transition
  {
    private final ObjectAnimator mAnim;
    private final boolean mHasReversibleFlag;
    
    AnimationDrawableTransition(AnimationDrawable paramAnimationDrawable, boolean paramBoolean1, boolean paramBoolean2)
    {
      super();
      int j = paramAnimationDrawable.getNumberOfFrames();
      int i;
      if (paramBoolean1) {
        i = j - 1;
      } else {
        i = 0;
      }
      if (paramBoolean1) {
        j = 0;
      } else {
        j -= 1;
      }
      AnimatedStateListDrawableCompat.FrameInterpolator localFrameInterpolator = new AnimatedStateListDrawableCompat.FrameInterpolator(paramAnimationDrawable, paramBoolean1);
      paramAnimationDrawable = ObjectAnimator.ofInt(paramAnimationDrawable, "currentIndex", new int[] { i, j });
      if (Build.VERSION.SDK_INT >= 18) {
        paramAnimationDrawable.setAutoCancel(true);
      }
      paramAnimationDrawable.setDuration(localFrameInterpolator.getTotalDuration());
      paramAnimationDrawable.setInterpolator(localFrameInterpolator);
      mHasReversibleFlag = paramBoolean2;
      mAnim = paramAnimationDrawable;
    }
    
    public boolean canReverse()
    {
      return mHasReversibleFlag;
    }
    
    public void reverse()
    {
      mAnim.reverse();
    }
    
    public void start()
    {
      mAnim.start();
    }
    
    public void stop()
    {
      mAnim.cancel();
    }
  }
  
  private static class FrameInterpolator
    implements TimeInterpolator
  {
    private int[] mFrameTimes;
    private int mFrames;
    private int mTotalDuration;
    
    FrameInterpolator(AnimationDrawable paramAnimationDrawable, boolean paramBoolean)
    {
      updateFrames(paramAnimationDrawable, paramBoolean);
    }
    
    public float getInterpolation(float paramFloat)
    {
      int j = (int)(paramFloat * mTotalDuration + 0.5F);
      int k = mFrames;
      int[] arrayOfInt = mFrameTimes;
      int i = 0;
      while ((i < k) && (j >= arrayOfInt[i]))
      {
        j -= arrayOfInt[i];
        i += 1;
      }
      if (i < k) {
        paramFloat = j / mTotalDuration;
      } else {
        paramFloat = 0.0F;
      }
      return i / k + paramFloat;
    }
    
    int getTotalDuration()
    {
      return mTotalDuration;
    }
    
    int updateFrames(AnimationDrawable paramAnimationDrawable, boolean paramBoolean)
    {
      int m = paramAnimationDrawable.getNumberOfFrames();
      mFrames = m;
      if ((mFrameTimes == null) || (mFrameTimes.length < m)) {
        mFrameTimes = new int[m];
      }
      int[] arrayOfInt = mFrameTimes;
      int i = 0;
      int j = 0;
      while (i < m)
      {
        if (paramBoolean) {
          k = m - i - 1;
        } else {
          k = i;
        }
        int k = paramAnimationDrawable.getDuration(k);
        arrayOfInt[i] = k;
        j += k;
        i += 1;
      }
      mTotalDuration = j;
      return j;
    }
  }
  
  private static abstract class Transition
  {
    private Transition() {}
    
    public boolean canReverse()
    {
      return false;
    }
    
    public void reverse() {}
    
    public abstract void start();
    
    public abstract void stop();
  }
}
