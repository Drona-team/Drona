package androidx.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.content.delay.TypedArrayUtils;
import androidx.core.view.ViewCompat;
import java.util.Map;

public class ChangeBounds
  extends Transition
{
  private static final Property<View, PointF> BOTTOM_RIGHT_ONLY_PROPERTY;
  private static final Property<ViewBounds, PointF> BOTTOM_RIGHT_PROPERTY;
  private static final Property<Drawable, PointF> DRAWABLE_ORIGIN_PROPERTY;
  private static final Property<View, PointF> POSITION_PROPERTY = new Property(PointF.class, "position")
  {
    public void draw(View paramAnonymousView, PointF paramAnonymousPointF)
    {
      int i = Math.round(x);
      int j = Math.round(y);
      ViewUtils.setLeftTopRightBottom(paramAnonymousView, i, j, paramAnonymousView.getWidth() + i, paramAnonymousView.getHeight() + j);
    }
    
    public PointF getPrevious(View paramAnonymousView)
    {
      return null;
    }
  };
  private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";
  private static final String PROPNAME_CLIP = "android:changeBounds:clip";
  private static final String PROPNAME_PARENT = "android:changeBounds:parent";
  private static final String PROPNAME_WINDOW_X = "android:changeBounds:windowX";
  private static final String PROPNAME_WINDOW_Y = "android:changeBounds:windowY";
  private static final Property<View, PointF> TOP_LEFT_ONLY_PROPERTY;
  private static final Property<ViewBounds, PointF> TOP_LEFT_PROPERTY;
  private static RectEvaluator sRectEvaluator = new RectEvaluator();
  private static final String[] sTransitionProperties = { "android:changeBounds:bounds", "android:changeBounds:clip", "android:changeBounds:parent", "android:changeBounds:windowX", "android:changeBounds:windowY" };
  private boolean mReparent = false;
  private boolean mResizeClip = false;
  private int[] mTempLocation = new int[2];
  
  static
  {
    DRAWABLE_ORIGIN_PROPERTY = new Property(PointF.class, "boundsOrigin")
    {
      private Rect mBounds = new Rect();
      
      public PointF draw(Drawable paramAnonymousDrawable)
      {
        paramAnonymousDrawable.copyBounds(mBounds);
        return new PointF(mBounds.left, mBounds.top);
      }
      
      public void draw(Drawable paramAnonymousDrawable, PointF paramAnonymousPointF)
      {
        paramAnonymousDrawable.copyBounds(mBounds);
        mBounds.offsetTo(Math.round(x), Math.round(y));
        paramAnonymousDrawable.setBounds(mBounds);
      }
    };
    TOP_LEFT_PROPERTY = new Property(PointF.class, "topLeft")
    {
      public void drawBitmap(ChangeBounds.ViewBounds paramAnonymousViewBounds, PointF paramAnonymousPointF)
      {
        paramAnonymousViewBounds.setTopLeft(paramAnonymousPointF);
      }
      
      public PointF getPrevious(ChangeBounds.ViewBounds paramAnonymousViewBounds)
      {
        return null;
      }
    };
    BOTTOM_RIGHT_PROPERTY = new Property(PointF.class, "bottomRight")
    {
      public void drawBitmap(ChangeBounds.ViewBounds paramAnonymousViewBounds, PointF paramAnonymousPointF)
      {
        paramAnonymousViewBounds.setBottomRight(paramAnonymousPointF);
      }
      
      public PointF getPrevious(ChangeBounds.ViewBounds paramAnonymousViewBounds)
      {
        return null;
      }
    };
    BOTTOM_RIGHT_ONLY_PROPERTY = new Property(PointF.class, "bottomRight")
    {
      public void drawLabel(View paramAnonymousView, PointF paramAnonymousPointF)
      {
        ViewUtils.setLeftTopRightBottom(paramAnonymousView, paramAnonymousView.getLeft(), paramAnonymousView.getTop(), Math.round(x), Math.round(y));
      }
      
      public PointF getPrevious(View paramAnonymousView)
      {
        return null;
      }
    };
    TOP_LEFT_ONLY_PROPERTY = new Property(PointF.class, "topLeft")
    {
      public void drawLabel(View paramAnonymousView, PointF paramAnonymousPointF)
      {
        ViewUtils.setLeftTopRightBottom(paramAnonymousView, Math.round(x), Math.round(y), paramAnonymousView.getRight(), paramAnonymousView.getBottom());
      }
      
      public PointF getPrevious(View paramAnonymousView)
      {
        return null;
      }
    };
  }
  
  public ChangeBounds() {}
  
  public ChangeBounds(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, Styleable.CHANGE_BOUNDS);
    boolean bool = TypedArrayUtils.getNamedBoolean(paramContext, (XmlResourceParser)paramAttributeSet, "resizeClip", 0, false);
    paramContext.recycle();
    setResizeClip(bool);
  }
  
  private void captureValues(TransitionValues paramTransitionValues)
  {
    View localView = view;
    if ((ViewCompat.isLaidOut(localView)) || (localView.getWidth() != 0) || (localView.getHeight() != 0))
    {
      values.put("android:changeBounds:bounds", new Rect(localView.getLeft(), localView.getTop(), localView.getRight(), localView.getBottom()));
      values.put("android:changeBounds:parent", view.getParent());
      if (mReparent)
      {
        view.getLocationInWindow(mTempLocation);
        values.put("android:changeBounds:windowX", Integer.valueOf(mTempLocation[0]));
        values.put("android:changeBounds:windowY", Integer.valueOf(mTempLocation[1]));
      }
      if (mResizeClip) {
        values.put("android:changeBounds:clip", ViewCompat.getClipBounds(localView));
      }
    }
  }
  
  private boolean parentMatches(View paramView1, View paramView2)
  {
    if (mReparent)
    {
      TransitionValues localTransitionValues = getMatchedTransitionValues(paramView1, true);
      if (localTransitionValues == null) {
        if (paramView1 == paramView2) {
          return true;
        }
      }
      while (paramView2 != view) {
        return false;
      }
    }
    return true;
  }
  
  public void captureEndValues(TransitionValues paramTransitionValues)
  {
    captureValues(paramTransitionValues);
  }
  
  public void captureStartValues(TransitionValues paramTransitionValues)
  {
    captureValues(paramTransitionValues);
  }
  
  public Animator createAnimator(final ViewGroup paramViewGroup, final TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    if ((paramTransitionValues1 != null) && (paramTransitionValues2 != null))
    {
      Object localObject2 = values;
      Object localObject1 = values;
      localObject2 = (ViewGroup)((Map)localObject2).get("android:changeBounds:parent");
      Object localObject3 = (ViewGroup)((Map)localObject1).get("android:changeBounds:parent");
      if ((localObject2 != null) && (localObject3 != null))
      {
        localObject1 = view;
        int k;
        final int m;
        int j;
        int i;
        if (parentMatches((View)localObject2, (View)localObject3))
        {
          paramViewGroup = (Rect)values.get("android:changeBounds:bounds");
          localObject2 = (Rect)values.get("android:changeBounds:bounds");
          k = left;
          m = left;
          int n = top;
          final int i1 = top;
          int i2 = right;
          final int i3 = right;
          int i4 = bottom;
          final int i5 = bottom;
          int i6 = i2 - k;
          int i7 = i4 - n;
          int i8 = i3 - m;
          int i9 = i5 - i1;
          localObject3 = (Rect)values.get("android:changeBounds:clip");
          localObject2 = (Rect)values.get("android:changeBounds:clip");
          if (((i6 != 0) && (i7 != 0)) || ((i8 != 0) && (i9 != 0)))
          {
            if ((k == m) && (n == i1)) {
              j = 0;
            } else {
              j = 1;
            }
            if (i2 == i3)
            {
              i = j;
              if (i4 == i5) {}
            }
            else
            {
              i = j + 1;
            }
          }
          else
          {
            i = 0;
          }
          if ((localObject3 == null) || (((Rect)localObject3).equals(localObject2)))
          {
            j = i;
            if (localObject3 == null)
            {
              j = i;
              if (localObject2 == null) {}
            }
          }
          else
          {
            j = i + 1;
          }
          if (j > 0)
          {
            if (!mResizeClip)
            {
              ViewUtils.setLeftTopRightBottom((View)localObject1, k, n, i2, i4);
              if (j == 2)
              {
                if ((i6 == i8) && (i7 == i9))
                {
                  paramViewGroup = getPathMotion().getPath(k, n, m, i1);
                  paramViewGroup = ObjectAnimatorUtils.ofPointF(localObject1, POSITION_PROPERTY, paramViewGroup);
                }
                else
                {
                  paramTransitionValues1 = new ViewBounds((View)localObject1);
                  paramViewGroup = getPathMotion().getPath(k, n, m, i1);
                  paramTransitionValues2 = ObjectAnimatorUtils.ofPointF(paramTransitionValues1, TOP_LEFT_PROPERTY, paramViewGroup);
                  paramViewGroup = getPathMotion().getPath(i2, i4, i3, i5);
                  localObject2 = ObjectAnimatorUtils.ofPointF(paramTransitionValues1, BOTTOM_RIGHT_PROPERTY, paramViewGroup);
                  paramViewGroup = new AnimatorSet();
                  paramViewGroup.playTogether(new Animator[] { paramTransitionValues2, localObject2 });
                  paramViewGroup.addListener(new AnimatorListenerAdapter()
                  {
                    private ChangeBounds.ViewBounds mViewBounds = paramTransitionValues1;
                  });
                }
              }
              else if ((k == m) && (n == i1))
              {
                paramViewGroup = getPathMotion().getPath(i2, i4, i3, i5);
                paramViewGroup = ObjectAnimatorUtils.ofPointF(localObject1, BOTTOM_RIGHT_ONLY_PROPERTY, paramViewGroup);
              }
              else
              {
                paramViewGroup = getPathMotion().getPath(k, n, m, i1);
                paramViewGroup = ObjectAnimatorUtils.ofPointF(localObject1, TOP_LEFT_ONLY_PROPERTY, paramViewGroup);
              }
            }
            else
            {
              ViewUtils.setLeftTopRightBottom((View)localObject1, k, n, Math.max(i6, i8) + k, Math.max(i7, i9) + n);
              if ((k == m) && (n == i1))
              {
                paramViewGroup = null;
              }
              else
              {
                paramViewGroup = getPathMotion().getPath(k, n, m, i1);
                paramViewGroup = ObjectAnimatorUtils.ofPointF(localObject1, POSITION_PROPERTY, paramViewGroup);
              }
              paramTransitionValues1 = (TransitionValues)localObject3;
              if (localObject3 == null) {
                paramTransitionValues1 = new Rect(0, 0, i6, i7);
              }
              if (localObject2 == null) {
                paramTransitionValues2 = new Rect(0, 0, i8, i9);
              } else {
                paramTransitionValues2 = (TransitionValues)localObject2;
              }
              if (!paramTransitionValues1.equals(paramTransitionValues2))
              {
                ViewCompat.setClipBounds((View)localObject1, paramTransitionValues1);
                paramTransitionValues2 = ObjectAnimator.ofObject(localObject1, "clipBounds", sRectEvaluator, new Object[] { paramTransitionValues1, paramTransitionValues2 });
                paramTransitionValues1 = paramTransitionValues2;
                paramTransitionValues2.addListener(new AnimatorListenerAdapter()
                {
                  private boolean mIsCanceled;
                  
                  public void onAnimationCancel(Animator paramAnonymousAnimator)
                  {
                    mIsCanceled = true;
                  }
                  
                  public void onAnimationEnd(Animator paramAnonymousAnimator)
                  {
                    if (!mIsCanceled)
                    {
                      ViewCompat.setClipBounds(val$view, val$finalClip);
                      ViewUtils.setLeftTopRightBottom(val$view, m, i1, i3, i5);
                    }
                  }
                });
              }
              else
              {
                paramTransitionValues1 = null;
              }
              paramViewGroup = TransitionUtils.mergeAnimators(paramViewGroup, paramTransitionValues1);
            }
            if (!(((View)localObject1).getParent() instanceof ViewGroup)) {
              return paramViewGroup;
            }
            paramTransitionValues1 = (ViewGroup)((View)localObject1).getParent();
            ViewGroupUtils.suppressLayout(paramTransitionValues1, true);
            addListener(new TransitionListenerAdapter()
            {
              boolean mCanceled = false;
              
              public void onTransitionCancel(Transition paramAnonymousTransition)
              {
                ViewGroupUtils.suppressLayout(paramTransitionValues1, false);
                mCanceled = true;
              }
              
              public void onTransitionEnd(Transition paramAnonymousTransition)
              {
                if (!mCanceled) {
                  ViewGroupUtils.suppressLayout(paramTransitionValues1, false);
                }
                paramAnonymousTransition.removeListener(this);
              }
              
              public void onTransitionPause(Transition paramAnonymousTransition)
              {
                ViewGroupUtils.suppressLayout(paramTransitionValues1, false);
              }
              
              public void onTransitionResume(Transition paramAnonymousTransition)
              {
                ViewGroupUtils.suppressLayout(paramTransitionValues1, true);
              }
            });
            return paramViewGroup;
          }
        }
        else
        {
          i = ((Integer)values.get("android:changeBounds:windowX")).intValue();
          j = ((Integer)values.get("android:changeBounds:windowY")).intValue();
          k = ((Integer)values.get("android:changeBounds:windowX")).intValue();
          m = ((Integer)values.get("android:changeBounds:windowY")).intValue();
          if ((i != k) || (j != m)) {
            break label952;
          }
        }
        return null;
        label952:
        paramViewGroup.getLocationInWindow(mTempLocation);
        paramTransitionValues1 = Bitmap.createBitmap(((View)localObject1).getWidth(), ((View)localObject1).getHeight(), Bitmap.Config.ARGB_8888);
        ((View)localObject1).draw(new Canvas(paramTransitionValues1));
        paramTransitionValues1 = new BitmapDrawable(paramTransitionValues1);
        final float f = ViewUtils.getTransitionAlpha((View)localObject1);
        ViewUtils.setTransitionAlpha((View)localObject1, 0.0F);
        ViewUtils.getOverlay(paramViewGroup).addWaypoints(paramTransitionValues1);
        paramTransitionValues2 = getPathMotion().getPath(i - mTempLocation[0], j - mTempLocation[1], k - mTempLocation[0], m - mTempLocation[1]);
        paramTransitionValues2 = ObjectAnimator.ofPropertyValuesHolder(paramTransitionValues1, new PropertyValuesHolder[] { PropertyValuesHolderUtils.ofPointF(DRAWABLE_ORIGIN_PROPERTY, paramTransitionValues2) });
        paramTransitionValues2.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            ViewUtils.getOverlay(paramViewGroup).remove(paramTransitionValues1);
            ViewUtils.setTransitionAlpha(val$view, f);
          }
        });
        return paramTransitionValues2;
      }
    }
    return null;
    return paramViewGroup;
  }
  
  public boolean getResizeClip()
  {
    return mResizeClip;
  }
  
  public String[] getTransitionProperties()
  {
    return sTransitionProperties;
  }
  
  public void setResizeClip(boolean paramBoolean)
  {
    mResizeClip = paramBoolean;
  }
  
  private static class ViewBounds
  {
    private int mBottom;
    private int mBottomRightCalls;
    private int mLeft;
    private int mRight;
    private int mTop;
    private int mTopLeftCalls;
    private View mView;
    
    ViewBounds(View paramView)
    {
      mView = paramView;
    }
    
    private void setLeftTopRightBottom()
    {
      ViewUtils.setLeftTopRightBottom(mView, mLeft, mTop, mRight, mBottom);
      mTopLeftCalls = 0;
      mBottomRightCalls = 0;
    }
    
    void setBottomRight(PointF paramPointF)
    {
      mRight = Math.round(x);
      mBottom = Math.round(y);
      mBottomRightCalls += 1;
      if (mTopLeftCalls == mBottomRightCalls) {
        setLeftTopRightBottom();
      }
    }
    
    void setTopLeft(PointF paramPointF)
    {
      mLeft = Math.round(x);
      mTop = Math.round(y);
      mTopLeftCalls += 1;
      if (mTopLeftCalls == mBottomRightCalls) {
        setLeftTopRightBottom();
      }
    }
  }
}
