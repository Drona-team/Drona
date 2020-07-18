package androidx.transition;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AndroidRuntimeException;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.content.delay.TypedArrayUtils;
import java.util.ArrayList;
import java.util.Iterator;

public class TransitionSet
  extends Transition
{
  private static final int FLAG_CHANGE_EPICENTER = 8;
  private static final int FLAG_CHANGE_INTERPOLATOR = 1;
  private static final int FLAG_CHANGE_PATH_MOTION = 4;
  private static final int FLAG_CHANGE_PROPAGATION = 2;
  public static final int ORDERING_SEQUENTIAL = 1;
  public static final int ORDERING_TOGETHER = 0;
  private int mChangeFlags = 0;
  int mCurrentListeners;
  private boolean mPlayTogether = true;
  boolean mStarted = false;
  private ArrayList<Transition> mTransitions = new ArrayList();
  
  public TransitionSet() {}
  
  public TransitionSet(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, Styleable.TRANSITION_SET);
    setOrdering(TypedArrayUtils.getNamedInt(paramContext, (XmlResourceParser)paramAttributeSet, "transitionOrdering", 0, 0));
    paramContext.recycle();
  }
  
  private void addTransitionInternal(Transition paramTransition)
  {
    mTransitions.add(paramTransition);
    mParent = this;
  }
  
  private void setupStartEndListeners()
  {
    TransitionSetListener localTransitionSetListener = new TransitionSetListener(this);
    Iterator localIterator = mTransitions.iterator();
    while (localIterator.hasNext()) {
      ((Transition)localIterator.next()).addListener(localTransitionSetListener);
    }
    mCurrentListeners = mTransitions.size();
  }
  
  public TransitionSet addListener(Transition.TransitionListener paramTransitionListener)
  {
    return (TransitionSet)super.addListener(paramTransitionListener);
  }
  
  public TransitionSet addTarget(int paramInt)
  {
    int i = 0;
    while (i < mTransitions.size())
    {
      ((Transition)mTransitions.get(i)).addTarget(paramInt);
      i += 1;
    }
    return (TransitionSet)super.addTarget(paramInt);
  }
  
  public TransitionSet addTarget(View paramView)
  {
    int i = 0;
    while (i < mTransitions.size())
    {
      ((Transition)mTransitions.get(i)).addTarget(paramView);
      i += 1;
    }
    return (TransitionSet)super.addTarget(paramView);
  }
  
  public TransitionSet addTarget(Class paramClass)
  {
    int i = 0;
    while (i < mTransitions.size())
    {
      ((Transition)mTransitions.get(i)).addTarget(paramClass);
      i += 1;
    }
    return (TransitionSet)super.addTarget(paramClass);
  }
  
  public TransitionSet addTarget(String paramString)
  {
    int i = 0;
    while (i < mTransitions.size())
    {
      ((Transition)mTransitions.get(i)).addTarget(paramString);
      i += 1;
    }
    return (TransitionSet)super.addTarget(paramString);
  }
  
  public TransitionSet addTransition(Transition paramTransition)
  {
    addTransitionInternal(paramTransition);
    if (mDuration >= 0L) {
      paramTransition.setDuration(mDuration);
    }
    if ((mChangeFlags & 0x1) != 0) {
      paramTransition.setInterpolator(getInterpolator());
    }
    if ((mChangeFlags & 0x2) != 0) {
      paramTransition.setPropagation(getPropagation());
    }
    if ((mChangeFlags & 0x4) != 0) {
      paramTransition.setPathMotion(getPathMotion());
    }
    if ((mChangeFlags & 0x8) != 0) {
      paramTransition.setEpicenterCallback(getEpicenterCallback());
    }
    return this;
  }
  
  protected void cancel()
  {
    super.cancel();
    int j = mTransitions.size();
    int i = 0;
    while (i < j)
    {
      ((Transition)mTransitions.get(i)).cancel();
      i += 1;
    }
  }
  
  public void captureEndValues(TransitionValues paramTransitionValues)
  {
    if (isValidTarget(view))
    {
      Iterator localIterator = mTransitions.iterator();
      while (localIterator.hasNext())
      {
        Transition localTransition = (Transition)localIterator.next();
        if (localTransition.isValidTarget(view))
        {
          localTransition.captureEndValues(paramTransitionValues);
          mTargetedTransitions.add(localTransition);
        }
      }
    }
  }
  
  void capturePropagationValues(TransitionValues paramTransitionValues)
  {
    super.capturePropagationValues(paramTransitionValues);
    int j = mTransitions.size();
    int i = 0;
    while (i < j)
    {
      ((Transition)mTransitions.get(i)).capturePropagationValues(paramTransitionValues);
      i += 1;
    }
  }
  
  public void captureStartValues(TransitionValues paramTransitionValues)
  {
    if (isValidTarget(view))
    {
      Iterator localIterator = mTransitions.iterator();
      while (localIterator.hasNext())
      {
        Transition localTransition = (Transition)localIterator.next();
        if (localTransition.isValidTarget(view))
        {
          localTransition.captureStartValues(paramTransitionValues);
          mTargetedTransitions.add(localTransition);
        }
      }
    }
  }
  
  public Transition clone()
  {
    TransitionSet localTransitionSet = (TransitionSet)super.clone();
    mTransitions = new ArrayList();
    int j = mTransitions.size();
    int i = 0;
    while (i < j)
    {
      localTransitionSet.addTransitionInternal(((Transition)mTransitions.get(i)).clone());
      i += 1;
    }
    return localTransitionSet;
  }
  
  protected void createAnimators(ViewGroup paramViewGroup, TransitionValuesMaps paramTransitionValuesMaps1, TransitionValuesMaps paramTransitionValuesMaps2, ArrayList paramArrayList1, ArrayList paramArrayList2)
  {
    long l1 = getStartDelay();
    int j = mTransitions.size();
    int i = 0;
    while (i < j)
    {
      Transition localTransition = (Transition)mTransitions.get(i);
      if ((l1 > 0L) && ((mPlayTogether) || (i == 0)))
      {
        long l2 = localTransition.getStartDelay();
        if (l2 > 0L) {
          localTransition.setStartDelay(l2 + l1);
        } else {
          localTransition.setStartDelay(l1);
        }
      }
      localTransition.createAnimators(paramViewGroup, paramTransitionValuesMaps1, paramTransitionValuesMaps2, paramArrayList1, paramArrayList2);
      i += 1;
    }
  }
  
  public Transition excludeTarget(int paramInt, boolean paramBoolean)
  {
    int i = 0;
    while (i < mTransitions.size())
    {
      ((Transition)mTransitions.get(i)).excludeTarget(paramInt, paramBoolean);
      i += 1;
    }
    return super.excludeTarget(paramInt, paramBoolean);
  }
  
  public Transition excludeTarget(View paramView, boolean paramBoolean)
  {
    int i = 0;
    while (i < mTransitions.size())
    {
      ((Transition)mTransitions.get(i)).excludeTarget(paramView, paramBoolean);
      i += 1;
    }
    return super.excludeTarget(paramView, paramBoolean);
  }
  
  public Transition excludeTarget(Class paramClass, boolean paramBoolean)
  {
    int i = 0;
    while (i < mTransitions.size())
    {
      ((Transition)mTransitions.get(i)).excludeTarget(paramClass, paramBoolean);
      i += 1;
    }
    return super.excludeTarget(paramClass, paramBoolean);
  }
  
  public Transition excludeTarget(String paramString, boolean paramBoolean)
  {
    int i = 0;
    while (i < mTransitions.size())
    {
      ((Transition)mTransitions.get(i)).excludeTarget(paramString, paramBoolean);
      i += 1;
    }
    return super.excludeTarget(paramString, paramBoolean);
  }
  
  void forceToEnd(ViewGroup paramViewGroup)
  {
    super.forceToEnd(paramViewGroup);
    int j = mTransitions.size();
    int i = 0;
    while (i < j)
    {
      ((Transition)mTransitions.get(i)).forceToEnd(paramViewGroup);
      i += 1;
    }
  }
  
  public int getOrdering()
  {
    return mPlayTogether ^ true;
  }
  
  public Transition getTransitionAt(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < mTransitions.size())) {
      return (Transition)mTransitions.get(paramInt);
    }
    return null;
  }
  
  public int getTransitionCount()
  {
    return mTransitions.size();
  }
  
  public void pause(View paramView)
  {
    super.pause(paramView);
    int j = mTransitions.size();
    int i = 0;
    while (i < j)
    {
      ((Transition)mTransitions.get(i)).pause(paramView);
      i += 1;
    }
  }
  
  public TransitionSet removeListener(Transition.TransitionListener paramTransitionListener)
  {
    return (TransitionSet)super.removeListener(paramTransitionListener);
  }
  
  public TransitionSet removeTarget(int paramInt)
  {
    int i = 0;
    while (i < mTransitions.size())
    {
      ((Transition)mTransitions.get(i)).removeTarget(paramInt);
      i += 1;
    }
    return (TransitionSet)super.removeTarget(paramInt);
  }
  
  public TransitionSet removeTarget(View paramView)
  {
    int i = 0;
    while (i < mTransitions.size())
    {
      ((Transition)mTransitions.get(i)).removeTarget(paramView);
      i += 1;
    }
    return (TransitionSet)super.removeTarget(paramView);
  }
  
  public TransitionSet removeTarget(Class paramClass)
  {
    int i = 0;
    while (i < mTransitions.size())
    {
      ((Transition)mTransitions.get(i)).removeTarget(paramClass);
      i += 1;
    }
    return (TransitionSet)super.removeTarget(paramClass);
  }
  
  public TransitionSet removeTarget(String paramString)
  {
    int i = 0;
    while (i < mTransitions.size())
    {
      ((Transition)mTransitions.get(i)).removeTarget(paramString);
      i += 1;
    }
    return (TransitionSet)super.removeTarget(paramString);
  }
  
  public TransitionSet removeTransition(Transition paramTransition)
  {
    mTransitions.remove(paramTransition);
    mParent = null;
    return this;
  }
  
  public void resume(View paramView)
  {
    super.resume(paramView);
    int j = mTransitions.size();
    int i = 0;
    while (i < j)
    {
      ((Transition)mTransitions.get(i)).resume(paramView);
      i += 1;
    }
  }
  
  protected void runAnimators()
  {
    if (mTransitions.isEmpty())
    {
      start();
      onAnimationEnd();
      return;
    }
    setupStartEndListeners();
    Object localObject;
    if (!mPlayTogether)
    {
      int i = 1;
      while (i < mTransitions.size())
      {
        ((Transition)mTransitions.get(i - 1)).addListener(new TransitionListenerAdapter()
        {
          public void onTransitionEnd(Transition paramAnonymousTransition)
          {
            val$nextTransition.runAnimators();
            paramAnonymousTransition.removeListener(this);
          }
        });
        i += 1;
      }
      localObject = (Transition)mTransitions.get(0);
      if (localObject != null) {
        ((Transition)localObject).runAnimators();
      }
    }
    else
    {
      localObject = mTransitions.iterator();
      while (((Iterator)localObject).hasNext()) {
        ((Transition)((Iterator)localObject).next()).runAnimators();
      }
    }
  }
  
  void setCanRemoveViews(boolean paramBoolean)
  {
    super.setCanRemoveViews(paramBoolean);
    int j = mTransitions.size();
    int i = 0;
    while (i < j)
    {
      ((Transition)mTransitions.get(i)).setCanRemoveViews(paramBoolean);
      i += 1;
    }
  }
  
  public TransitionSet setDuration(long paramLong)
  {
    super.setDuration(paramLong);
    if ((mDuration >= 0L) && (mTransitions != null))
    {
      int j = mTransitions.size();
      int i = 0;
      while (i < j)
      {
        ((Transition)mTransitions.get(i)).setDuration(paramLong);
        i += 1;
      }
    }
    return this;
  }
  
  public void setEpicenterCallback(Transition.EpicenterCallback paramEpicenterCallback)
  {
    super.setEpicenterCallback(paramEpicenterCallback);
    mChangeFlags |= 0x8;
    int j = mTransitions.size();
    int i = 0;
    while (i < j)
    {
      ((Transition)mTransitions.get(i)).setEpicenterCallback(paramEpicenterCallback);
      i += 1;
    }
  }
  
  public TransitionSet setInterpolator(TimeInterpolator paramTimeInterpolator)
  {
    mChangeFlags |= 0x1;
    if (mTransitions != null)
    {
      int j = mTransitions.size();
      int i = 0;
      while (i < j)
      {
        ((Transition)mTransitions.get(i)).setInterpolator(paramTimeInterpolator);
        i += 1;
      }
    }
    return (TransitionSet)super.setInterpolator(paramTimeInterpolator);
  }
  
  public TransitionSet setOrdering(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Invalid parameter for TransitionSet ordering: ");
      localStringBuilder.append(paramInt);
      throw new AndroidRuntimeException(localStringBuilder.toString());
    case 1: 
      mPlayTogether = false;
      return this;
    }
    mPlayTogether = true;
    return this;
  }
  
  public void setPathMotion(PathMotion paramPathMotion)
  {
    super.setPathMotion(paramPathMotion);
    mChangeFlags |= 0x4;
    if (mTransitions != null)
    {
      int i = 0;
      while (i < mTransitions.size())
      {
        ((Transition)mTransitions.get(i)).setPathMotion(paramPathMotion);
        i += 1;
      }
    }
  }
  
  public void setPropagation(TransitionPropagation paramTransitionPropagation)
  {
    super.setPropagation(paramTransitionPropagation);
    mChangeFlags |= 0x2;
    int j = mTransitions.size();
    int i = 0;
    while (i < j)
    {
      ((Transition)mTransitions.get(i)).setPropagation(paramTransitionPropagation);
      i += 1;
    }
  }
  
  TransitionSet setSceneRoot(ViewGroup paramViewGroup)
  {
    super.setSceneRoot(paramViewGroup);
    int j = mTransitions.size();
    int i = 0;
    while (i < j)
    {
      ((Transition)mTransitions.get(i)).setSceneRoot(paramViewGroup);
      i += 1;
    }
    return this;
  }
  
  public TransitionSet setStartDelay(long paramLong)
  {
    return (TransitionSet)super.setStartDelay(paramLong);
  }
  
  String toString(String paramString)
  {
    Object localObject = super.toString(paramString);
    int i = 0;
    while (i < mTransitions.size())
    {
      StringBuilder localStringBuilder1 = new StringBuilder();
      localStringBuilder1.append((String)localObject);
      localStringBuilder1.append("\n");
      localObject = (Transition)mTransitions.get(i);
      StringBuilder localStringBuilder2 = new StringBuilder();
      localStringBuilder2.append(paramString);
      localStringBuilder2.append("  ");
      localStringBuilder1.append(((Transition)localObject).toString(localStringBuilder2.toString()));
      localObject = localStringBuilder1.toString();
      i += 1;
    }
    return localObject;
  }
  
  static class TransitionSetListener
    extends TransitionListenerAdapter
  {
    TransitionSet mTransitionSet;
    
    TransitionSetListener(TransitionSet paramTransitionSet)
    {
      mTransitionSet = paramTransitionSet;
    }
    
    public void onTransitionEnd(Transition paramTransition)
    {
      TransitionSet localTransitionSet = mTransitionSet;
      mCurrentListeners -= 1;
      if (mTransitionSet.mCurrentListeners == 0)
      {
        mTransitionSet.mStarted = false;
        mTransitionSet.onAnimationEnd();
      }
      paramTransition.removeListener(this);
    }
    
    public void onTransitionStart(Transition paramTransition)
    {
      if (!mTransitionSet.mStarted)
      {
        mTransitionSet.start();
        mTransitionSet.mStarted = true;
      }
    }
  }
}
