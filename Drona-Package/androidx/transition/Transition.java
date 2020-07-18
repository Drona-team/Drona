package androidx.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.annotation.RestrictTo;
import androidx.collection.ArrayMap;
import androidx.collection.LongSparseArray;
import androidx.collection.SimpleArrayMap;
import androidx.core.content.delay.TypedArrayUtils;
import androidx.core.view.ViewCompat;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public abstract class Transition
  implements Cloneable
{
  static final boolean $assertionsDisabled = false;
  private static final int[] DEFAULT_MATCH_ORDER = { 2, 1, 3, 4 };
  private static final String LOG_TAG = "Transition";
  private static final int MATCH_FIRST = 1;
  public static final int MATCH_ID = 3;
  private static final String MATCH_ID_STR = "id";
  public static final int MATCH_INSTANCE = 1;
  private static final String MATCH_INSTANCE_STR = "instance";
  public static final int MATCH_ITEM_ID = 4;
  private static final String MATCH_ITEM_ID_STR = "itemId";
  private static final int MATCH_LAST = 4;
  public static final int MATCH_NAME = 2;
  private static final String MATCH_NAME_STR = "name";
  private static final PathMotion STRAIGHT_PATH_MOTION = new PathMotion()
  {
    public Path getPath(float paramAnonymousFloat1, float paramAnonymousFloat2, float paramAnonymousFloat3, float paramAnonymousFloat4)
    {
      Path localPath = new Path();
      localPath.moveTo(paramAnonymousFloat1, paramAnonymousFloat2);
      localPath.lineTo(paramAnonymousFloat3, paramAnonymousFloat4);
      return localPath;
    }
  };
  private static ThreadLocal<ArrayMap<Animator, AnimationInfo>> sRunningAnimators = new ThreadLocal();
  private ArrayList<Animator> mAnimators = new ArrayList();
  boolean mCanRemoveViews = false;
  ArrayList<Animator> mCurrentAnimators = new ArrayList();
  long mDuration = -1L;
  private TransitionValuesMaps mEndValues = new TransitionValuesMaps();
  private ArrayList<TransitionValues> mEndValuesList;
  private boolean mEnded = false;
  private EpicenterCallback mEpicenterCallback;
  private TimeInterpolator mInterpolator = null;
  private ArrayList<TransitionListener> mListeners = null;
  private int[] mMatchOrder = DEFAULT_MATCH_ORDER;
  private String mName = getClass().getName();
  private ArrayMap<String, String> mNameOverrides;
  private int mNumInstances = 0;
  TransitionSet mParent = null;
  private PathMotion mPathMotion = STRAIGHT_PATH_MOTION;
  private boolean mPaused = false;
  TransitionPropagation mPropagation;
  private ViewGroup mSceneRoot = null;
  private long mStartDelay = -1L;
  private TransitionValuesMaps mStartValues = new TransitionValuesMaps();
  private ArrayList<TransitionValues> mStartValuesList;
  private ArrayList<View> mTargetChildExcludes = null;
  private ArrayList<View> mTargetExcludes = null;
  private ArrayList<Integer> mTargetIdChildExcludes = null;
  private ArrayList<Integer> mTargetIdExcludes = null;
  ArrayList<Integer> mTargetIds = new ArrayList();
  private ArrayList<String> mTargetNameExcludes = null;
  private ArrayList<String> mTargetNames = null;
  private ArrayList<Class<?>> mTargetTypeChildExcludes = null;
  private ArrayList<Class<?>> mTargetTypeExcludes = null;
  private ArrayList<Class<?>> mTargetTypes = null;
  ArrayList<View> mTargets = new ArrayList();
  
  public Transition() {}
  
  public Transition(Context paramContext, AttributeSet paramAttributeSet)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, Styleable.TRANSITION);
    paramAttributeSet = (XmlResourceParser)paramAttributeSet;
    long l = TypedArrayUtils.getNamedInt(localTypedArray, paramAttributeSet, "duration", 1, -1);
    if (l >= 0L) {
      setDuration(l);
    }
    l = TypedArrayUtils.getNamedInt(localTypedArray, paramAttributeSet, "startDelay", 2, -1);
    if (l > 0L) {
      setStartDelay(l);
    }
    int i = TypedArrayUtils.getNamedResourceId(localTypedArray, paramAttributeSet, "interpolator", 0, 0);
    if (i > 0) {
      setInterpolator(AnimationUtils.loadInterpolator(paramContext, i));
    }
    paramContext = TypedArrayUtils.getNamedString(localTypedArray, paramAttributeSet, "matchOrder", 3);
    if (paramContext != null) {
      setMatchOrder(parseMatchOrder(paramContext));
    }
    localTypedArray.recycle();
  }
  
  private void addUnmatched(ArrayMap paramArrayMap1, ArrayMap paramArrayMap2)
  {
    int k = 0;
    int i = 0;
    int j;
    for (;;)
    {
      j = k;
      if (i >= paramArrayMap1.size()) {
        break;
      }
      TransitionValues localTransitionValues = (TransitionValues)paramArrayMap1.valueAt(i);
      if (isValidTarget(view))
      {
        mStartValuesList.add(localTransitionValues);
        mEndValuesList.add(null);
      }
      i += 1;
    }
    while (j < paramArrayMap2.size())
    {
      paramArrayMap1 = (TransitionValues)paramArrayMap2.valueAt(j);
      if (isValidTarget(view))
      {
        mEndValuesList.add(paramArrayMap1);
        mStartValuesList.add(null);
      }
      j += 1;
    }
  }
  
  private static void addViewValues(TransitionValuesMaps paramTransitionValuesMaps, View paramView, TransitionValues paramTransitionValues)
  {
    mViewValues.put(paramView, paramTransitionValues);
    int i = paramView.getId();
    if (i >= 0) {
      if (mIdValues.indexOfKey(i) >= 0) {
        mIdValues.put(i, null);
      } else {
        mIdValues.put(i, paramView);
      }
    }
    paramTransitionValues = ViewCompat.getTransitionName(paramView);
    if (paramTransitionValues != null) {
      if (mNameValues.containsKey(paramTransitionValues)) {
        mNameValues.put(paramTransitionValues, null);
      } else {
        mNameValues.put(paramTransitionValues, paramView);
      }
    }
    if ((paramView.getParent() instanceof ListView))
    {
      paramTransitionValues = (ListView)paramView.getParent();
      if (paramTransitionValues.getAdapter().hasStableIds())
      {
        long l = paramTransitionValues.getItemIdAtPosition(paramTransitionValues.getPositionForView(paramView));
        if (mItemIdValues.indexOfKey(l) >= 0)
        {
          paramView = (View)mItemIdValues.get(l);
          if (paramView != null)
          {
            ViewCompat.setHasTransientState(paramView, false);
            mItemIdValues.put(l, null);
          }
        }
        else
        {
          ViewCompat.setHasTransientState(paramView, true);
          mItemIdValues.put(l, paramView);
        }
      }
    }
  }
  
  private static boolean alreadyContains(int[] paramArrayOfInt, int paramInt)
  {
    int j = paramArrayOfInt[paramInt];
    int i = 0;
    while (i < paramInt)
    {
      if (paramArrayOfInt[i] == j) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void captureHierarchy(View paramView, boolean paramBoolean)
  {
    if (paramView == null) {
      return;
    }
    int k = paramView.getId();
    if ((mTargetIdExcludes != null) && (mTargetIdExcludes.contains(Integer.valueOf(k)))) {
      return;
    }
    if ((mTargetExcludes != null) && (mTargetExcludes.contains(paramView))) {
      return;
    }
    Object localObject = mTargetTypeExcludes;
    int j = 0;
    int i;
    if (localObject != null)
    {
      int m = mTargetTypeExcludes.size();
      i = 0;
      while (i < m)
      {
        if (((Class)mTargetTypeExcludes.get(i)).isInstance(paramView)) {
          return;
        }
        i += 1;
      }
    }
    if ((paramView.getParent() instanceof ViewGroup))
    {
      localObject = new TransitionValues(paramView);
      if (paramBoolean) {
        captureStartValues((TransitionValues)localObject);
      } else {
        captureEndValues((TransitionValues)localObject);
      }
      mTargetedTransitions.add(this);
      capturePropagationValues((TransitionValues)localObject);
      if (paramBoolean) {
        addViewValues(mStartValues, paramView, (TransitionValues)localObject);
      } else {
        addViewValues(mEndValues, paramView, (TransitionValues)localObject);
      }
    }
    if ((paramView instanceof ViewGroup))
    {
      if ((mTargetIdChildExcludes != null) && (mTargetIdChildExcludes.contains(Integer.valueOf(k)))) {
        return;
      }
      if ((mTargetChildExcludes != null) && (mTargetChildExcludes.contains(paramView))) {
        return;
      }
      if (mTargetTypeChildExcludes != null)
      {
        k = mTargetTypeChildExcludes.size();
        i = 0;
        while (i < k)
        {
          if (((Class)mTargetTypeChildExcludes.get(i)).isInstance(paramView)) {
            return;
          }
          i += 1;
        }
      }
      paramView = (ViewGroup)paramView;
      i = j;
      while (i < paramView.getChildCount())
      {
        captureHierarchy(paramView.getChildAt(i), paramBoolean);
        i += 1;
      }
    }
  }
  
  private ArrayList excludeId(ArrayList paramArrayList, int paramInt, boolean paramBoolean)
  {
    ArrayList localArrayList = paramArrayList;
    if (paramInt > 0)
    {
      if (paramBoolean) {
        return ArrayListManager.processLine(paramArrayList, Integer.valueOf(paramInt));
      }
      localArrayList = ArrayListManager.remove(paramArrayList, Integer.valueOf(paramInt));
    }
    return localArrayList;
  }
  
  private static ArrayList excludeObject(ArrayList paramArrayList, Object paramObject, boolean paramBoolean)
  {
    ArrayList localArrayList = paramArrayList;
    if (paramObject != null)
    {
      if (paramBoolean) {
        return ArrayListManager.processLine(paramArrayList, paramObject);
      }
      localArrayList = ArrayListManager.remove(paramArrayList, paramObject);
    }
    return localArrayList;
  }
  
  private ArrayList excludeType(ArrayList paramArrayList, Class paramClass, boolean paramBoolean)
  {
    ArrayList localArrayList = paramArrayList;
    if (paramClass != null)
    {
      if (paramBoolean) {
        return ArrayListManager.processLine(paramArrayList, paramClass);
      }
      localArrayList = ArrayListManager.remove(paramArrayList, paramClass);
    }
    return localArrayList;
  }
  
  private ArrayList excludeView(ArrayList paramArrayList, View paramView, boolean paramBoolean)
  {
    ArrayList localArrayList = paramArrayList;
    if (paramView != null)
    {
      if (paramBoolean) {
        return ArrayListManager.processLine(paramArrayList, paramView);
      }
      localArrayList = ArrayListManager.remove(paramArrayList, paramView);
    }
    return localArrayList;
  }
  
  private static ArrayMap getRunningAnimators()
  {
    ArrayMap localArrayMap2 = (ArrayMap)sRunningAnimators.get();
    ArrayMap localArrayMap1 = localArrayMap2;
    if (localArrayMap2 == null)
    {
      localArrayMap1 = new ArrayMap();
      sRunningAnimators.set(localArrayMap1);
    }
    return localArrayMap1;
  }
  
  private static boolean isValidMatch(int paramInt)
  {
    return (paramInt >= 1) && (paramInt <= 4);
  }
  
  private static boolean isValueChanged(TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2, String paramString)
  {
    paramTransitionValues1 = values.get(paramString);
    paramTransitionValues2 = values.get(paramString);
    if ((paramTransitionValues1 == null) && (paramTransitionValues2 == null)) {
      return false;
    }
    if (paramTransitionValues1 != null)
    {
      if (paramTransitionValues2 == null) {
        return true;
      }
      return true ^ paramTransitionValues1.equals(paramTransitionValues2);
    }
    return true;
  }
  
  private void matchIds(ArrayMap paramArrayMap1, ArrayMap paramArrayMap2, SparseArray paramSparseArray1, SparseArray paramSparseArray2)
  {
    int j = paramSparseArray1.size();
    int i = 0;
    while (i < j)
    {
      View localView1 = (View)paramSparseArray1.valueAt(i);
      if ((localView1 != null) && (isValidTarget(localView1)))
      {
        View localView2 = (View)paramSparseArray2.get(paramSparseArray1.keyAt(i));
        if ((localView2 != null) && (isValidTarget(localView2)))
        {
          TransitionValues localTransitionValues1 = (TransitionValues)paramArrayMap1.get(localView1);
          TransitionValues localTransitionValues2 = (TransitionValues)paramArrayMap2.get(localView2);
          if ((localTransitionValues1 != null) && (localTransitionValues2 != null))
          {
            mStartValuesList.add(localTransitionValues1);
            mEndValuesList.add(localTransitionValues2);
            paramArrayMap1.remove(localView1);
            paramArrayMap2.remove(localView2);
          }
        }
      }
      i += 1;
    }
  }
  
  private void matchInstances(ArrayMap paramArrayMap1, ArrayMap paramArrayMap2)
  {
    int i = paramArrayMap1.size() - 1;
    while (i >= 0)
    {
      Object localObject = (View)paramArrayMap1.keyAt(i);
      if ((localObject != null) && (isValidTarget((View)localObject)))
      {
        localObject = (TransitionValues)paramArrayMap2.remove(localObject);
        if ((localObject != null) && (isValidTarget(view)))
        {
          TransitionValues localTransitionValues = (TransitionValues)paramArrayMap1.removeAt(i);
          mStartValuesList.add(localTransitionValues);
          mEndValuesList.add(localObject);
        }
      }
      i -= 1;
    }
  }
  
  private void matchItemIds(ArrayMap paramArrayMap1, ArrayMap paramArrayMap2, LongSparseArray paramLongSparseArray1, LongSparseArray paramLongSparseArray2)
  {
    int j = paramLongSparseArray1.size();
    int i = 0;
    while (i < j)
    {
      View localView1 = (View)paramLongSparseArray1.valueAt(i);
      if ((localView1 != null) && (isValidTarget(localView1)))
      {
        View localView2 = (View)paramLongSparseArray2.get(paramLongSparseArray1.keyAt(i));
        if ((localView2 != null) && (isValidTarget(localView2)))
        {
          TransitionValues localTransitionValues1 = (TransitionValues)paramArrayMap1.get(localView1);
          TransitionValues localTransitionValues2 = (TransitionValues)paramArrayMap2.get(localView2);
          if ((localTransitionValues1 != null) && (localTransitionValues2 != null))
          {
            mStartValuesList.add(localTransitionValues1);
            mEndValuesList.add(localTransitionValues2);
            paramArrayMap1.remove(localView1);
            paramArrayMap2.remove(localView2);
          }
        }
      }
      i += 1;
    }
  }
  
  private void matchNames(ArrayMap paramArrayMap1, ArrayMap paramArrayMap2, ArrayMap paramArrayMap3, ArrayMap paramArrayMap4)
  {
    int j = paramArrayMap3.size();
    int i = 0;
    while (i < j)
    {
      View localView1 = (View)paramArrayMap3.valueAt(i);
      if ((localView1 != null) && (isValidTarget(localView1)))
      {
        View localView2 = (View)paramArrayMap4.get(paramArrayMap3.keyAt(i));
        if ((localView2 != null) && (isValidTarget(localView2)))
        {
          TransitionValues localTransitionValues1 = (TransitionValues)paramArrayMap1.get(localView1);
          TransitionValues localTransitionValues2 = (TransitionValues)paramArrayMap2.get(localView2);
          if ((localTransitionValues1 != null) && (localTransitionValues2 != null))
          {
            mStartValuesList.add(localTransitionValues1);
            mEndValuesList.add(localTransitionValues2);
            paramArrayMap1.remove(localView1);
            paramArrayMap2.remove(localView2);
          }
        }
      }
      i += 1;
    }
  }
  
  private void matchStartAndEnd(TransitionValuesMaps paramTransitionValuesMaps1, TransitionValuesMaps paramTransitionValuesMaps2)
  {
    ArrayMap localArrayMap1 = new ArrayMap(mViewValues);
    ArrayMap localArrayMap2 = new ArrayMap(mViewValues);
    int i = 0;
    while (i < mMatchOrder.length)
    {
      switch (mMatchOrder[i])
      {
      default: 
        break;
      case 4: 
        matchItemIds(localArrayMap1, localArrayMap2, mItemIdValues, mItemIdValues);
        break;
      case 3: 
        matchIds(localArrayMap1, localArrayMap2, mIdValues, mIdValues);
        break;
      case 2: 
        matchNames(localArrayMap1, localArrayMap2, mNameValues, mNameValues);
        break;
      case 1: 
        matchInstances(localArrayMap1, localArrayMap2);
      }
      i += 1;
    }
    addUnmatched(localArrayMap1, localArrayMap2);
  }
  
  private static int[] parseMatchOrder(String paramString)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
    paramString = new int[localStringTokenizer.countTokens()];
    int i = 0;
    while (localStringTokenizer.hasMoreTokens())
    {
      Object localObject = localStringTokenizer.nextToken().trim();
      if ("id".equalsIgnoreCase((String)localObject))
      {
        paramString[i] = 3;
      }
      else if ("instance".equalsIgnoreCase((String)localObject))
      {
        paramString[i] = 1;
      }
      else if ("name".equalsIgnoreCase((String)localObject))
      {
        paramString[i] = 2;
      }
      else if ("itemId".equalsIgnoreCase((String)localObject))
      {
        paramString[i] = 4;
      }
      else
      {
        if (!((String)localObject).isEmpty()) {
          break label135;
        }
        localObject = new int[paramString.length - 1];
        System.arraycopy(paramString, 0, localObject, 0, i);
        i -= 1;
        paramString = (String)localObject;
      }
      i += 1;
      continue;
      label135:
      paramString = new StringBuilder();
      paramString.append("Unknown match type in matchOrder: '");
      paramString.append((String)localObject);
      paramString.append("'");
      throw new InflateException(paramString.toString());
    }
    return paramString;
  }
  
  private void runAnimator(Animator paramAnimator, final ArrayMap paramArrayMap)
  {
    if (paramAnimator != null)
    {
      paramAnimator.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          paramArrayMap.remove(paramAnonymousAnimator);
          mCurrentAnimators.remove(paramAnonymousAnimator);
        }
        
        public void onAnimationStart(Animator paramAnonymousAnimator)
        {
          mCurrentAnimators.add(paramAnonymousAnimator);
        }
      });
      animate(paramAnimator);
    }
  }
  
  public Transition addListener(TransitionListener paramTransitionListener)
  {
    if (mListeners == null) {
      mListeners = new ArrayList();
    }
    mListeners.add(paramTransitionListener);
    return this;
  }
  
  public Transition addTarget(int paramInt)
  {
    if (paramInt != 0) {
      mTargetIds.add(Integer.valueOf(paramInt));
    }
    return this;
  }
  
  public Transition addTarget(View paramView)
  {
    mTargets.add(paramView);
    return this;
  }
  
  public Transition addTarget(Class paramClass)
  {
    if (mTargetTypes == null) {
      mTargetTypes = new ArrayList();
    }
    mTargetTypes.add(paramClass);
    return this;
  }
  
  public Transition addTarget(String paramString)
  {
    if (mTargetNames == null) {
      mTargetNames = new ArrayList();
    }
    mTargetNames.add(paramString);
    return this;
  }
  
  protected void animate(Animator paramAnimator)
  {
    if (paramAnimator == null)
    {
      onAnimationEnd();
      return;
    }
    if (getDuration() >= 0L) {
      paramAnimator.setDuration(getDuration());
    }
    if (getStartDelay() >= 0L) {
      paramAnimator.setStartDelay(getStartDelay() + paramAnimator.getStartDelay());
    }
    if (getInterpolator() != null) {
      paramAnimator.setInterpolator(getInterpolator());
    }
    paramAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        onAnimationEnd();
        paramAnonymousAnimator.removeListener(this);
      }
    });
    paramAnimator.start();
  }
  
  protected void cancel()
  {
    int i = mCurrentAnimators.size() - 1;
    while (i >= 0)
    {
      ((Animator)mCurrentAnimators.get(i)).cancel();
      i -= 1;
    }
    if ((mListeners != null) && (mListeners.size() > 0))
    {
      ArrayList localArrayList = (ArrayList)mListeners.clone();
      int j = localArrayList.size();
      i = 0;
      while (i < j)
      {
        ((TransitionListener)localArrayList.get(i)).onTransitionCancel(this);
        i += 1;
      }
    }
  }
  
  public abstract void captureEndValues(TransitionValues paramTransitionValues);
  
  void capturePropagationValues(TransitionValues paramTransitionValues)
  {
    if ((mPropagation != null) && (!values.isEmpty()))
    {
      String[] arrayOfString = mPropagation.getPropagationProperties();
      if (arrayOfString == null) {
        return;
      }
      int j = 0;
      int i = 0;
      while (i < arrayOfString.length)
      {
        if (!values.containsKey(arrayOfString[i]))
        {
          i = j;
          break label75;
        }
        i += 1;
      }
      i = 1;
      label75:
      if (i == 0) {
        mPropagation.captureValues(paramTransitionValues);
      }
    }
  }
  
  public abstract void captureStartValues(TransitionValues paramTransitionValues);
  
  void captureValues(ViewGroup paramViewGroup, boolean paramBoolean)
  {
    clearValues(paramBoolean);
    int i = mTargetIds.size();
    int k = 0;
    Object localObject1;
    Object localObject2;
    if (((i <= 0) && (mTargets.size() <= 0)) || ((mTargetNames != null) && (!mTargetNames.isEmpty())) || ((mTargetTypes != null) && (!mTargetTypes.isEmpty())))
    {
      captureHierarchy(paramViewGroup, paramBoolean);
    }
    else
    {
      i = 0;
      while (i < mTargetIds.size())
      {
        localObject1 = paramViewGroup.findViewById(((Integer)mTargetIds.get(i)).intValue());
        if (localObject1 != null)
        {
          localObject2 = new TransitionValues((View)localObject1);
          if (paramBoolean) {
            captureStartValues((TransitionValues)localObject2);
          } else {
            captureEndValues((TransitionValues)localObject2);
          }
          mTargetedTransitions.add(this);
          capturePropagationValues((TransitionValues)localObject2);
          if (paramBoolean) {
            addViewValues(mStartValues, (View)localObject1, (TransitionValues)localObject2);
          } else {
            addViewValues(mEndValues, (View)localObject1, (TransitionValues)localObject2);
          }
        }
        i += 1;
      }
      i = 0;
      while (i < mTargets.size())
      {
        paramViewGroup = (View)mTargets.get(i);
        localObject1 = new TransitionValues(paramViewGroup);
        if (paramBoolean) {
          captureStartValues((TransitionValues)localObject1);
        } else {
          captureEndValues((TransitionValues)localObject1);
        }
        mTargetedTransitions.add(this);
        capturePropagationValues((TransitionValues)localObject1);
        if (paramBoolean) {
          addViewValues(mStartValues, paramViewGroup, (TransitionValues)localObject1);
        } else {
          addViewValues(mEndValues, paramViewGroup, (TransitionValues)localObject1);
        }
        i += 1;
      }
    }
    if ((!paramBoolean) && (mNameOverrides != null))
    {
      int m = mNameOverrides.size();
      paramViewGroup = new ArrayList(m);
      i = 0;
      int j;
      for (;;)
      {
        j = k;
        if (i >= m) {
          break;
        }
        localObject1 = (String)mNameOverrides.keyAt(i);
        paramViewGroup.add(mStartValues.mNameValues.remove(localObject1));
        i += 1;
      }
      while (j < m)
      {
        localObject1 = (View)paramViewGroup.get(j);
        if (localObject1 != null)
        {
          localObject2 = (String)mNameOverrides.valueAt(j);
          mStartValues.mNameValues.put(localObject2, localObject1);
        }
        j += 1;
      }
    }
  }
  
  void clearValues(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      mStartValues.mViewValues.clear();
      mStartValues.mIdValues.clear();
      mStartValues.mItemIdValues.clear();
      return;
    }
    mEndValues.mViewValues.clear();
    mEndValues.mIdValues.clear();
    mEndValues.mItemIdValues.clear();
  }
  
  public Transition clone()
  {
    try
    {
      Object localObject1 = super.clone();
      localObject1 = (Transition)localObject1;
      Object localObject2 = new ArrayList();
      mAnimators = ((ArrayList)localObject2);
      localObject2 = new TransitionValuesMaps();
      mStartValues = ((TransitionValuesMaps)localObject2);
      localObject2 = new TransitionValuesMaps();
      mEndValues = ((TransitionValuesMaps)localObject2);
      mStartValuesList = null;
      mEndValuesList = null;
      return localObject1;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
  
  public Animator createAnimator(ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    return null;
  }
  
  protected void createAnimators(ViewGroup paramViewGroup, TransitionValuesMaps paramTransitionValuesMaps1, TransitionValuesMaps paramTransitionValuesMaps2, ArrayList paramArrayList1, ArrayList paramArrayList2)
  {
    ArrayMap localArrayMap = getRunningAnimators();
    SparseIntArray localSparseIntArray = new SparseIntArray();
    int k = paramArrayList1.size();
    long l1 = Long.MAX_VALUE;
    int i = 0;
    int j;
    while (i < k)
    {
      Object localObject1 = (TransitionValues)paramArrayList1.get(i);
      paramTransitionValuesMaps1 = (TransitionValues)paramArrayList2.get(i);
      Object localObject2 = localObject1;
      if (localObject1 != null)
      {
        localObject2 = localObject1;
        if (!mTargetedTransitions.contains(this)) {
          localObject2 = null;
        }
      }
      TransitionValuesMaps localTransitionValuesMaps = paramTransitionValuesMaps1;
      if (paramTransitionValuesMaps1 != null)
      {
        localTransitionValuesMaps = paramTransitionValuesMaps1;
        if (!mTargetedTransitions.contains(this)) {
          localTransitionValuesMaps = null;
        }
      }
      if ((localObject2 == null) && (localTransitionValuesMaps == null)) {}
      do
      {
        do
        {
          l2 = l1;
          break;
          if ((localObject2 != null) && (localTransitionValuesMaps != null) && (!isTransitionRequired(localObject2, localTransitionValuesMaps))) {
            j = 0;
          } else {
            j = 1;
          }
        } while (j == 0);
        localObject1 = createAnimator(paramViewGroup, localObject2, localTransitionValuesMaps);
      } while (localObject1 == null);
      View localView;
      if (localTransitionValuesMaps != null)
      {
        localView = view;
        Object localObject3 = getTransitionProperties();
        if ((localObject3 != null) && (localObject3.length > 0))
        {
          paramTransitionValuesMaps1 = new TransitionValues(localView);
          TransitionValues localTransitionValues = (TransitionValues)mViewValues.get(localView);
          if (localTransitionValues != null)
          {
            j = 0;
            while (j < localObject3.length)
            {
              values.put(localObject3[j], values.get(localObject3[j]));
              j += 1;
            }
          }
          int m = localArrayMap.size();
          j = 0;
          while (j < m)
          {
            localObject3 = (AnimationInfo)localArrayMap.get((Animator)localArrayMap.keyAt(j));
            if ((mValues != null) && (mView == localView) && (mName.equals(getName())) && (mValues.equals(paramTransitionValuesMaps1)))
            {
              localObject1 = null;
              break;
            }
            j += 1;
          }
        }
        else
        {
          paramTransitionValuesMaps1 = null;
        }
      }
      else
      {
        localView = view;
        paramTransitionValuesMaps1 = null;
      }
      long l2 = l1;
      if (localObject1 != null)
      {
        l2 = l1;
        if (mPropagation != null)
        {
          l2 = mPropagation.getStartDelay(paramViewGroup, this, localObject2, localTransitionValuesMaps);
          localSparseIntArray.put(mAnimators.size(), (int)l2);
          l2 = Math.min(l2, l1);
        }
        localArrayMap.put(localObject1, new AnimationInfo(localView, getName(), this, ViewUtils.getWindowId(paramViewGroup), paramTransitionValuesMaps1));
        mAnimators.add(localObject1);
      }
      i += 1;
      l1 = l2;
    }
    if (localSparseIntArray.size() != 0)
    {
      i = 0;
      while (i < localSparseIntArray.size())
      {
        j = localSparseIntArray.keyAt(i);
        paramViewGroup = (Animator)mAnimators.get(j);
        paramViewGroup.setStartDelay(localSparseIntArray.valueAt(i) - l1 + paramViewGroup.getStartDelay());
        i += 1;
      }
    }
  }
  
  public Transition excludeChildren(int paramInt, boolean paramBoolean)
  {
    mTargetIdChildExcludes = excludeId(mTargetIdChildExcludes, paramInt, paramBoolean);
    return this;
  }
  
  public Transition excludeChildren(View paramView, boolean paramBoolean)
  {
    mTargetChildExcludes = excludeView(mTargetChildExcludes, paramView, paramBoolean);
    return this;
  }
  
  public Transition excludeChildren(Class paramClass, boolean paramBoolean)
  {
    mTargetTypeChildExcludes = excludeType(mTargetTypeChildExcludes, paramClass, paramBoolean);
    return this;
  }
  
  public Transition excludeTarget(int paramInt, boolean paramBoolean)
  {
    mTargetIdExcludes = excludeId(mTargetIdExcludes, paramInt, paramBoolean);
    return this;
  }
  
  public Transition excludeTarget(View paramView, boolean paramBoolean)
  {
    mTargetExcludes = excludeView(mTargetExcludes, paramView, paramBoolean);
    return this;
  }
  
  public Transition excludeTarget(Class paramClass, boolean paramBoolean)
  {
    mTargetTypeExcludes = excludeType(mTargetTypeExcludes, paramClass, paramBoolean);
    return this;
  }
  
  public Transition excludeTarget(String paramString, boolean paramBoolean)
  {
    mTargetNameExcludes = excludeObject(mTargetNameExcludes, paramString, paramBoolean);
    return this;
  }
  
  void forceToEnd(ViewGroup paramViewGroup)
  {
    Object localObject = getRunningAnimators();
    int i = ((SimpleArrayMap)localObject).size();
    if (paramViewGroup != null)
    {
      if (i == 0) {
        return;
      }
      paramViewGroup = ViewUtils.getWindowId(paramViewGroup);
      ArrayMap localArrayMap = new ArrayMap((SimpleArrayMap)localObject);
      ((SimpleArrayMap)localObject).clear();
      i -= 1;
      while (i >= 0)
      {
        localObject = (AnimationInfo)localArrayMap.valueAt(i);
        if ((mView != null) && (paramViewGroup != null) && (paramViewGroup.equals(mWindowId))) {
          ((Animator)localArrayMap.keyAt(i)).end();
        }
        i -= 1;
      }
    }
  }
  
  public long getDuration()
  {
    return mDuration;
  }
  
  public Rect getEpicenter()
  {
    if (mEpicenterCallback == null) {
      return null;
    }
    return mEpicenterCallback.onGetEpicenter(this);
  }
  
  public EpicenterCallback getEpicenterCallback()
  {
    return mEpicenterCallback;
  }
  
  public TimeInterpolator getInterpolator()
  {
    return mInterpolator;
  }
  
  TransitionValues getMatchedTransitionValues(View paramView, boolean paramBoolean)
  {
    if (mParent != null) {
      return mParent.getMatchedTransitionValues(paramView, paramBoolean);
    }
    ArrayList localArrayList;
    if (paramBoolean) {
      localArrayList = mStartValuesList;
    } else {
      localArrayList = mEndValuesList;
    }
    if (localArrayList == null) {
      return null;
    }
    int m = localArrayList.size();
    int k = -1;
    int i = 0;
    int j;
    for (;;)
    {
      j = k;
      if (i >= m) {
        break;
      }
      TransitionValues localTransitionValues = (TransitionValues)localArrayList.get(i);
      if (localTransitionValues == null) {
        return null;
      }
      if (view == paramView)
      {
        j = i;
        break;
      }
      i += 1;
    }
    if (j >= 0)
    {
      if (paramBoolean) {
        paramView = mEndValuesList;
      } else {
        paramView = mStartValuesList;
      }
      return (TransitionValues)paramView.get(j);
    }
    return null;
  }
  
  public String getName()
  {
    return mName;
  }
  
  public PathMotion getPathMotion()
  {
    return mPathMotion;
  }
  
  public TransitionPropagation getPropagation()
  {
    return mPropagation;
  }
  
  public long getStartDelay()
  {
    return mStartDelay;
  }
  
  public List getTargetIds()
  {
    return mTargetIds;
  }
  
  public List getTargetNames()
  {
    return mTargetNames;
  }
  
  public List getTargetTypes()
  {
    return mTargetTypes;
  }
  
  public List getTargets()
  {
    return mTargets;
  }
  
  public String[] getTransitionProperties()
  {
    return null;
  }
  
  public TransitionValues getTransitionValues(View paramView, boolean paramBoolean)
  {
    if (mParent != null) {
      return mParent.getTransitionValues(paramView, paramBoolean);
    }
    TransitionValuesMaps localTransitionValuesMaps;
    if (paramBoolean) {
      localTransitionValuesMaps = mStartValues;
    } else {
      localTransitionValuesMaps = mEndValues;
    }
    return (TransitionValues)mViewValues.get(paramView);
  }
  
  public boolean isTransitionRequired(TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    if ((paramTransitionValues1 != null) && (paramTransitionValues2 != null))
    {
      Object localObject = getTransitionProperties();
      if (localObject != null)
      {
        int j = localObject.length;
        int i = 0;
        for (;;)
        {
          if (i >= j) {
            break label100;
          }
          if (isValueChanged(paramTransitionValues1, paramTransitionValues2, localObject[i])) {
            break;
          }
          i += 1;
        }
      }
      localObject = values.keySet().iterator();
      do
      {
        if (!((Iterator)localObject).hasNext()) {
          break;
        }
      } while (!isValueChanged(paramTransitionValues1, paramTransitionValues2, (String)((Iterator)localObject).next()));
      return true;
    }
    label100:
    return false;
  }
  
  boolean isValidTarget(View paramView)
  {
    int j = paramView.getId();
    if ((mTargetIdExcludes != null) && (mTargetIdExcludes.contains(Integer.valueOf(j)))) {
      return false;
    }
    if ((mTargetExcludes != null) && (mTargetExcludes.contains(paramView))) {
      return false;
    }
    int i;
    if (mTargetTypeExcludes != null)
    {
      int k = mTargetTypeExcludes.size();
      i = 0;
      while (i < k)
      {
        if (((Class)mTargetTypeExcludes.get(i)).isInstance(paramView)) {
          return false;
        }
        i += 1;
      }
    }
    if ((mTargetNameExcludes != null) && (ViewCompat.getTransitionName(paramView) != null) && (mTargetNameExcludes.contains(ViewCompat.getTransitionName(paramView)))) {
      return false;
    }
    if ((mTargetIds.size() == 0) && (mTargets.size() == 0) && ((mTargetTypes == null) || (mTargetTypes.isEmpty())))
    {
      if (mTargetNames == null) {
        break label284;
      }
      if (mTargetNames.isEmpty()) {
        return true;
      }
    }
    if (!mTargetIds.contains(Integer.valueOf(j)))
    {
      if (mTargets.contains(paramView)) {
        return true;
      }
      if ((mTargetNames != null) && (mTargetNames.contains(ViewCompat.getTransitionName(paramView)))) {
        return true;
      }
      if (mTargetTypes != null)
      {
        i = 0;
        while (i < mTargetTypes.size())
        {
          if (((Class)mTargetTypes.get(i)).isInstance(paramView)) {
            return true;
          }
          i += 1;
        }
      }
      return false;
    }
    label284:
    return true;
    return false;
  }
  
  protected void onAnimationEnd()
  {
    mNumInstances -= 1;
    if (mNumInstances == 0)
    {
      Object localObject;
      if ((mListeners != null) && (mListeners.size() > 0))
      {
        localObject = (ArrayList)mListeners.clone();
        int j = ((ArrayList)localObject).size();
        i = 0;
        while (i < j)
        {
          ((TransitionListener)((ArrayList)localObject).get(i)).onTransitionEnd(this);
          i += 1;
        }
      }
      int i = 0;
      while (i < mStartValues.mItemIdValues.size())
      {
        localObject = (View)mStartValues.mItemIdValues.valueAt(i);
        if (localObject != null) {
          ViewCompat.setHasTransientState((View)localObject, false);
        }
        i += 1;
      }
      i = 0;
      while (i < mEndValues.mItemIdValues.size())
      {
        localObject = (View)mEndValues.mItemIdValues.valueAt(i);
        if (localObject != null) {
          ViewCompat.setHasTransientState((View)localObject, false);
        }
        i += 1;
      }
      mEnded = true;
    }
  }
  
  public void pause(View paramView)
  {
    if (!mEnded)
    {
      ArrayMap localArrayMap = getRunningAnimators();
      int i = localArrayMap.size();
      paramView = ViewUtils.getWindowId(paramView);
      i -= 1;
      while (i >= 0)
      {
        AnimationInfo localAnimationInfo = (AnimationInfo)localArrayMap.valueAt(i);
        if ((mView != null) && (paramView.equals(mWindowId))) {
          AnimatorUtils.pause((Animator)localArrayMap.keyAt(i));
        }
        i -= 1;
      }
      if ((mListeners != null) && (mListeners.size() > 0))
      {
        paramView = (ArrayList)mListeners.clone();
        int j = paramView.size();
        i = 0;
        while (i < j)
        {
          ((TransitionListener)paramView.get(i)).onTransitionPause(this);
          i += 1;
        }
      }
      mPaused = true;
    }
  }
  
  void playTransition(ViewGroup paramViewGroup)
  {
    mStartValuesList = new ArrayList();
    mEndValuesList = new ArrayList();
    matchStartAndEnd(mStartValues, mEndValues);
    ArrayMap localArrayMap = getRunningAnimators();
    int i = localArrayMap.size();
    WindowIdImpl localWindowIdImpl = ViewUtils.getWindowId(paramViewGroup);
    i -= 1;
    while (i >= 0)
    {
      Animator localAnimator = (Animator)localArrayMap.keyAt(i);
      if (localAnimator != null)
      {
        AnimationInfo localAnimationInfo = (AnimationInfo)localArrayMap.get(localAnimator);
        if ((localAnimationInfo != null) && (mView != null) && (localWindowIdImpl.equals(mWindowId)))
        {
          TransitionValues localTransitionValues4 = mValues;
          View localView = mView;
          TransitionValues localTransitionValues5 = getTransitionValues(localView, true);
          TransitionValues localTransitionValues3 = getMatchedTransitionValues(localView, true);
          TransitionValues localTransitionValues1 = localTransitionValues3;
          TransitionValues localTransitionValues2 = localTransitionValues1;
          if (localTransitionValues5 == null)
          {
            localTransitionValues2 = localTransitionValues1;
            if (localTransitionValues3 == null) {
              localTransitionValues2 = (TransitionValues)mEndValues.mViewValues.get(localView);
            }
          }
          int j;
          if (((localTransitionValues5 != null) || (localTransitionValues2 != null)) && (mTransition.isTransitionRequired(localTransitionValues4, localTransitionValues2))) {
            j = 1;
          } else {
            j = 0;
          }
          if (j != 0) {
            if ((!localAnimator.isRunning()) && (!localAnimator.isStarted())) {
              localArrayMap.remove(localAnimator);
            } else {
              localAnimator.cancel();
            }
          }
        }
      }
      i -= 1;
    }
    createAnimators(paramViewGroup, mStartValues, mEndValues, mStartValuesList, mEndValuesList);
    runAnimators();
  }
  
  public Transition removeListener(TransitionListener paramTransitionListener)
  {
    if (mListeners == null) {
      return this;
    }
    mListeners.remove(paramTransitionListener);
    if (mListeners.size() == 0) {
      mListeners = null;
    }
    return this;
  }
  
  public Transition removeTarget(int paramInt)
  {
    if (paramInt != 0) {
      mTargetIds.remove(Integer.valueOf(paramInt));
    }
    return this;
  }
  
  public Transition removeTarget(View paramView)
  {
    mTargets.remove(paramView);
    return this;
  }
  
  public Transition removeTarget(Class paramClass)
  {
    if (mTargetTypes != null) {
      mTargetTypes.remove(paramClass);
    }
    return this;
  }
  
  public Transition removeTarget(String paramString)
  {
    if (mTargetNames != null) {
      mTargetNames.remove(paramString);
    }
    return this;
  }
  
  public void resume(View paramView)
  {
    if (mPaused)
    {
      if (!mEnded)
      {
        ArrayMap localArrayMap = getRunningAnimators();
        int i = localArrayMap.size();
        paramView = ViewUtils.getWindowId(paramView);
        i -= 1;
        while (i >= 0)
        {
          AnimationInfo localAnimationInfo = (AnimationInfo)localArrayMap.valueAt(i);
          if ((mView != null) && (paramView.equals(mWindowId))) {
            AnimatorUtils.resume((Animator)localArrayMap.keyAt(i));
          }
          i -= 1;
        }
        if ((mListeners != null) && (mListeners.size() > 0))
        {
          paramView = (ArrayList)mListeners.clone();
          int j = paramView.size();
          i = 0;
          while (i < j)
          {
            ((TransitionListener)paramView.get(i)).onTransitionResume(this);
            i += 1;
          }
        }
      }
      mPaused = false;
    }
  }
  
  protected void runAnimators()
  {
    start();
    ArrayMap localArrayMap = getRunningAnimators();
    Iterator localIterator = mAnimators.iterator();
    while (localIterator.hasNext())
    {
      Animator localAnimator = (Animator)localIterator.next();
      if (localArrayMap.containsKey(localAnimator))
      {
        start();
        runAnimator(localAnimator, localArrayMap);
      }
    }
    mAnimators.clear();
    onAnimationEnd();
  }
  
  void setCanRemoveViews(boolean paramBoolean)
  {
    mCanRemoveViews = paramBoolean;
  }
  
  public Transition setDuration(long paramLong)
  {
    mDuration = paramLong;
    return this;
  }
  
  public void setEpicenterCallback(EpicenterCallback paramEpicenterCallback)
  {
    mEpicenterCallback = paramEpicenterCallback;
  }
  
  public Transition setInterpolator(TimeInterpolator paramTimeInterpolator)
  {
    mInterpolator = paramTimeInterpolator;
    return this;
  }
  
  public void setMatchOrder(int... paramVarArgs)
  {
    if ((paramVarArgs != null) && (paramVarArgs.length != 0))
    {
      int i = 0;
      while (i < paramVarArgs.length) {
        if (isValidMatch(paramVarArgs[i]))
        {
          if (!alreadyContains(paramVarArgs, i)) {
            i += 1;
          } else {
            throw new IllegalArgumentException("matches contains a duplicate value");
          }
        }
        else {
          throw new IllegalArgumentException("matches contains invalid value");
        }
      }
      mMatchOrder = ((int[])paramVarArgs.clone());
      return;
    }
    mMatchOrder = DEFAULT_MATCH_ORDER;
  }
  
  public void setPathMotion(PathMotion paramPathMotion)
  {
    if (paramPathMotion == null)
    {
      mPathMotion = STRAIGHT_PATH_MOTION;
      return;
    }
    mPathMotion = paramPathMotion;
  }
  
  public void setPropagation(TransitionPropagation paramTransitionPropagation)
  {
    mPropagation = paramTransitionPropagation;
  }
  
  Transition setSceneRoot(ViewGroup paramViewGroup)
  {
    mSceneRoot = paramViewGroup;
    return this;
  }
  
  public Transition setStartDelay(long paramLong)
  {
    mStartDelay = paramLong;
    return this;
  }
  
  protected void start()
  {
    if (mNumInstances == 0)
    {
      if ((mListeners != null) && (mListeners.size() > 0))
      {
        ArrayList localArrayList = (ArrayList)mListeners.clone();
        int j = localArrayList.size();
        int i = 0;
        while (i < j)
        {
          ((TransitionListener)localArrayList.get(i)).onTransitionStart(this);
          i += 1;
        }
      }
      mEnded = false;
    }
    mNumInstances += 1;
  }
  
  public String toString()
  {
    return toString("");
  }
  
  String toString(String paramString)
  {
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append(paramString);
    ((StringBuilder)localObject).append(getClass().getSimpleName());
    ((StringBuilder)localObject).append("@");
    ((StringBuilder)localObject).append(Integer.toHexString(hashCode()));
    ((StringBuilder)localObject).append(": ");
    localObject = ((StringBuilder)localObject).toString();
    paramString = (String)localObject;
    if (mDuration != -1L)
    {
      paramString = new StringBuilder();
      paramString.append((String)localObject);
      paramString.append("dur(");
      paramString.append(mDuration);
      paramString.append(") ");
      paramString = paramString.toString();
    }
    localObject = paramString;
    if (mStartDelay != -1L)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append(paramString);
      ((StringBuilder)localObject).append("dly(");
      ((StringBuilder)localObject).append(mStartDelay);
      ((StringBuilder)localObject).append(") ");
      localObject = ((StringBuilder)localObject).toString();
    }
    paramString = (String)localObject;
    if (mInterpolator != null)
    {
      paramString = new StringBuilder();
      paramString.append((String)localObject);
      paramString.append("interp(");
      paramString.append(mInterpolator);
      paramString.append(") ");
      paramString = paramString.toString();
    }
    if ((mTargetIds.size() > 0) || (mTargets.size() > 0))
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append(paramString);
      ((StringBuilder)localObject).append("tgts(");
      localObject = ((StringBuilder)localObject).toString();
      paramString = (String)localObject;
      int i = mTargetIds.size();
      int j = 0;
      if (i > 0)
      {
        i = 0;
        paramString = (String)localObject;
        while (i < mTargetIds.size())
        {
          localObject = paramString;
          if (i > 0)
          {
            localObject = new StringBuilder();
            ((StringBuilder)localObject).append(paramString);
            ((StringBuilder)localObject).append(", ");
            localObject = ((StringBuilder)localObject).toString();
          }
          paramString = new StringBuilder();
          paramString.append((String)localObject);
          paramString.append(mTargetIds.get(i));
          paramString = paramString.toString();
          i += 1;
        }
      }
      localObject = paramString;
      if (mTargets.size() > 0)
      {
        i = j;
        for (;;)
        {
          localObject = paramString;
          if (i >= mTargets.size()) {
            break;
          }
          localObject = paramString;
          if (i > 0)
          {
            localObject = new StringBuilder();
            ((StringBuilder)localObject).append(paramString);
            ((StringBuilder)localObject).append(", ");
            localObject = ((StringBuilder)localObject).toString();
          }
          paramString = new StringBuilder();
          paramString.append((String)localObject);
          paramString.append(mTargets.get(i));
          paramString = paramString.toString();
          i += 1;
        }
      }
      paramString = new StringBuilder();
      paramString.append((String)localObject);
      paramString.append(")");
      return paramString.toString();
    }
    return paramString;
  }
  
  private static class AnimationInfo
  {
    String mName;
    Transition mTransition;
    TransitionValues mValues;
    View mView;
    WindowIdImpl mWindowId;
    
    AnimationInfo(View paramView, String paramString, Transition paramTransition, WindowIdImpl paramWindowIdImpl, TransitionValues paramTransitionValues)
    {
      mView = paramView;
      mName = paramString;
      mValues = paramTransitionValues;
      mWindowId = paramWindowIdImpl;
      mTransition = paramTransition;
    }
  }
  
  private static class ArrayListManager
  {
    private ArrayListManager() {}
    
    static ArrayList processLine(ArrayList paramArrayList, Object paramObject)
    {
      ArrayList localArrayList = paramArrayList;
      if (paramArrayList == null) {
        localArrayList = new ArrayList();
      }
      if (!localArrayList.contains(paramObject)) {
        localArrayList.add(paramObject);
      }
      return localArrayList;
    }
    
    static ArrayList remove(ArrayList paramArrayList, Object paramObject)
    {
      if (paramArrayList != null)
      {
        paramArrayList.remove(paramObject);
        if (paramArrayList.isEmpty()) {
          return null;
        }
      }
      return paramArrayList;
    }
  }
  
  public static abstract class EpicenterCallback
  {
    public EpicenterCallback() {}
    
    public abstract Rect onGetEpicenter(Transition paramTransition);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
  public static @interface MatchOrder {}
  
  public static abstract interface TransitionListener
  {
    public abstract void onTransitionCancel(Transition paramTransition);
    
    public abstract void onTransitionEnd(Transition paramTransition);
    
    public abstract void onTransitionPause(Transition paramTransition);
    
    public abstract void onTransitionResume(Transition paramTransition);
    
    public abstract void onTransitionStart(Transition paramTransition);
  }
}
