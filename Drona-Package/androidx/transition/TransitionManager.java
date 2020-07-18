package androidx.transition;

import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import androidx.collection.ArrayMap;
import androidx.collection.SimpleArrayMap;
import androidx.core.view.ViewCompat;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

public class TransitionManager
{
  private static final String LOG_TAG = "TransitionManager";
  private static Transition sDefaultTransition = new AutoTransition();
  static ArrayList<ViewGroup> sPendingTransitions = new ArrayList();
  private static ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>>> sRunningTransitions = new ThreadLocal();
  private ArrayMap<Scene, ArrayMap<Scene, Transition>> mScenePairTransitions = new ArrayMap();
  private ArrayMap<Scene, Transition> mSceneTransitions = new ArrayMap();
  
  public TransitionManager() {}
  
  public static void addTransition(Scene paramScene)
  {
    changeScene(paramScene, sDefaultTransition);
  }
  
  public static void addTransition(Scene paramScene, Transition paramTransition)
  {
    changeScene(paramScene, paramTransition);
  }
  
  public static void beginDelayedTransition(ViewGroup paramViewGroup)
  {
    beginDelayedTransition(paramViewGroup, null);
  }
  
  public static void beginDelayedTransition(ViewGroup paramViewGroup, Transition paramTransition)
  {
    if ((!sPendingTransitions.contains(paramViewGroup)) && (ViewCompat.isLaidOut(paramViewGroup)))
    {
      sPendingTransitions.add(paramViewGroup);
      Transition localTransition = paramTransition;
      if (paramTransition == null) {
        localTransition = sDefaultTransition;
      }
      paramTransition = localTransition.clone();
      sceneChangeSetup(paramViewGroup, paramTransition);
      Scene.setCurrentScene(paramViewGroup, null);
      sceneChangeRunTransition(paramViewGroup, paramTransition);
    }
  }
  
  private static void changeScene(Scene paramScene, Transition paramTransition)
  {
    ViewGroup localViewGroup = paramScene.getSceneRoot();
    if (!sPendingTransitions.contains(localViewGroup))
    {
      Scene localScene = Scene.getCurrentScene(localViewGroup);
      if (paramTransition == null)
      {
        if (localScene != null) {
          localScene.exit();
        }
        paramScene.enter();
        return;
      }
      sPendingTransitions.add(localViewGroup);
      paramTransition = paramTransition.clone();
      paramTransition.setSceneRoot(localViewGroup);
      if ((localScene != null) && (localScene.isCreatedFromLayoutResource())) {
        paramTransition.setCanRemoveViews(true);
      }
      sceneChangeSetup(localViewGroup, paramTransition);
      paramScene.enter();
      sceneChangeRunTransition(localViewGroup, paramTransition);
    }
  }
  
  public static void endTransitions(ViewGroup paramViewGroup)
  {
    sPendingTransitions.remove(paramViewGroup);
    ArrayList localArrayList = (ArrayList)getRunningTransitions().get(paramViewGroup);
    if ((localArrayList != null) && (!localArrayList.isEmpty()))
    {
      localArrayList = new ArrayList(localArrayList);
      int i = localArrayList.size() - 1;
      while (i >= 0)
      {
        ((Transition)localArrayList.get(i)).forceToEnd(paramViewGroup);
        i -= 1;
      }
    }
  }
  
  static ArrayMap getRunningTransitions()
  {
    Object localObject = (WeakReference)sRunningTransitions.get();
    if (localObject != null)
    {
      localObject = (ArrayMap)((WeakReference)localObject).get();
      if (localObject != null) {
        return localObject;
      }
    }
    localObject = new ArrayMap();
    WeakReference localWeakReference = new WeakReference(localObject);
    sRunningTransitions.set(localWeakReference);
    return localObject;
  }
  
  private Transition getTransition(Scene paramScene)
  {
    Object localObject = paramScene.getSceneRoot();
    if (localObject != null)
    {
      localObject = Scene.getCurrentScene((ViewGroup)localObject);
      if (localObject != null)
      {
        ArrayMap localArrayMap = (ArrayMap)mScenePairTransitions.get(paramScene);
        if (localArrayMap != null)
        {
          localObject = (Transition)localArrayMap.get(localObject);
          if (localObject != null) {
            return localObject;
          }
        }
      }
    }
    paramScene = (Transition)mSceneTransitions.get(paramScene);
    if (paramScene != null) {
      return paramScene;
    }
    return sDefaultTransition;
  }
  
  private static void sceneChangeRunTransition(ViewGroup paramViewGroup, Transition paramTransition)
  {
    if ((paramTransition != null) && (paramViewGroup != null))
    {
      paramTransition = new MultiListener(paramTransition, paramViewGroup);
      paramViewGroup.addOnAttachStateChangeListener(paramTransition);
      paramViewGroup.getViewTreeObserver().addOnPreDrawListener(paramTransition);
    }
  }
  
  private static void sceneChangeSetup(ViewGroup paramViewGroup, Transition paramTransition)
  {
    Object localObject = (ArrayList)getRunningTransitions().get(paramViewGroup);
    if ((localObject != null) && (((ArrayList)localObject).size() > 0))
    {
      localObject = ((ArrayList)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        ((Transition)((Iterator)localObject).next()).pause(paramViewGroup);
      }
    }
    if (paramTransition != null) {
      paramTransition.captureValues(paramViewGroup, true);
    }
    paramViewGroup = Scene.getCurrentScene(paramViewGroup);
    if (paramViewGroup != null) {
      paramViewGroup.exit();
    }
  }
  
  public void setTransition(Scene paramScene1, Scene paramScene2, Transition paramTransition)
  {
    ArrayMap localArrayMap2 = (ArrayMap)mScenePairTransitions.get(paramScene2);
    ArrayMap localArrayMap1 = localArrayMap2;
    if (localArrayMap2 == null)
    {
      localArrayMap1 = new ArrayMap();
      mScenePairTransitions.put(paramScene2, localArrayMap1);
    }
    localArrayMap1.put(paramScene1, paramTransition);
  }
  
  public void setTransition(Scene paramScene, Transition paramTransition)
  {
    mSceneTransitions.put(paramScene, paramTransition);
  }
  
  public void transitionTo(Scene paramScene)
  {
    changeScene(paramScene, getTransition(paramScene));
  }
  
  private static class MultiListener
    implements ViewTreeObserver.OnPreDrawListener, View.OnAttachStateChangeListener
  {
    ViewGroup mSceneRoot;
    Transition mTransition;
    
    MultiListener(Transition paramTransition, ViewGroup paramViewGroup)
    {
      mTransition = paramTransition;
      mSceneRoot = paramViewGroup;
    }
    
    private void removeListeners()
    {
      mSceneRoot.getViewTreeObserver().removeOnPreDrawListener(this);
      mSceneRoot.removeOnAttachStateChangeListener(this);
    }
    
    public boolean onPreDraw()
    {
      removeListeners();
      if (!TransitionManager.sPendingTransitions.remove(mSceneRoot)) {
        return true;
      }
      final ArrayMap localArrayMap = TransitionManager.getRunningTransitions();
      ArrayList localArrayList2 = (ArrayList)localArrayMap.get(mSceneRoot);
      ArrayList localArrayList1 = null;
      Object localObject;
      if (localArrayList2 == null)
      {
        localObject = new ArrayList();
        localArrayMap.put(mSceneRoot, localObject);
      }
      else
      {
        localObject = localArrayList2;
        if (localArrayList2.size() > 0)
        {
          localArrayList1 = new ArrayList(localArrayList2);
          localObject = localArrayList2;
        }
      }
      ((ArrayList)localObject).add(mTransition);
      mTransition.addListener(new TransitionListenerAdapter()
      {
        public void onTransitionEnd(Transition paramAnonymousTransition)
        {
          ((ArrayList)localArrayMap.get(mSceneRoot)).remove(paramAnonymousTransition);
          paramAnonymousTransition.removeListener(this);
        }
      });
      mTransition.captureValues(mSceneRoot, false);
      if (localArrayList1 != null)
      {
        localObject = localArrayList1.iterator();
        while (((Iterator)localObject).hasNext()) {
          ((Transition)((Iterator)localObject).next()).resume(mSceneRoot);
        }
      }
      mTransition.playTransition(mSceneRoot);
      return true;
    }
    
    public void onViewAttachedToWindow(View paramView) {}
    
    public void onViewDetachedFromWindow(View paramView)
    {
      removeListeners();
      TransitionManager.sPendingTransitions.remove(mSceneRoot);
      paramView = (ArrayList)TransitionManager.getRunningTransitions().get(mSceneRoot);
      if ((paramView != null) && (paramView.size() > 0))
      {
        paramView = paramView.iterator();
        while (paramView.hasNext()) {
          ((Transition)paramView.next()).resume(mSceneRoot);
        }
      }
      mTransition.clearValues(true);
    }
  }
}
