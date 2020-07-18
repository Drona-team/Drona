package androidx.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.content.delay.TypedArrayUtils;
import androidx.core.view.ViewCompat;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;

public class ChangeTransform
  extends Transition
{
  private static final Property<PathAnimatorMatrix, float[]> NON_TRANSLATIONS_PROPERTY;
  private static final String PROPNAME_INTERMEDIATE_MATRIX = "android:changeTransform:intermediateMatrix";
  private static final String PROPNAME_INTERMEDIATE_PARENT_MATRIX = "android:changeTransform:intermediateParentMatrix";
  private static final String PROPNAME_MATRIX = "android:changeTransform:matrix";
  private static final String PROPNAME_PARENT = "android:changeTransform:parent";
  private static final String PROPNAME_PARENT_MATRIX = "android:changeTransform:parentMatrix";
  private static final String PROPNAME_TRANSFORMS = "android:changeTransform:transforms";
  private static final boolean SUPPORTS_VIEW_REMOVAL_SUPPRESSION;
  private static final Property<PathAnimatorMatrix, PointF> TRANSLATIONS_PROPERTY;
  private static final String[] sTransitionProperties = { "android:changeTransform:matrix", "android:changeTransform:transforms", "android:changeTransform:parentMatrix" };
  private boolean mReparent = true;
  private Matrix mTempMatrix = new Matrix();
  boolean mUseOverlay = true;
  
  static
  {
    NON_TRANSLATIONS_PROPERTY = new Property([F.class, "nonTranslations")
    {
      public float[] get(ChangeTransform.PathAnimatorMatrix paramAnonymousPathAnimatorMatrix)
      {
        return null;
      }
      
      public void setMatrix(ChangeTransform.PathAnimatorMatrix paramAnonymousPathAnimatorMatrix, float[] paramAnonymousArrayOfFloat)
      {
        paramAnonymousPathAnimatorMatrix.setValues(paramAnonymousArrayOfFloat);
      }
    };
    TRANSLATIONS_PROPERTY = new Property(PointF.class, "translations")
    {
      public void drawBitmap(ChangeTransform.PathAnimatorMatrix paramAnonymousPathAnimatorMatrix, PointF paramAnonymousPointF)
      {
        paramAnonymousPathAnimatorMatrix.setTranslation(paramAnonymousPointF);
      }
      
      public PointF getPrevious(ChangeTransform.PathAnimatorMatrix paramAnonymousPathAnimatorMatrix)
      {
        return null;
      }
    };
    boolean bool;
    if (Build.VERSION.SDK_INT >= 21) {
      bool = true;
    } else {
      bool = false;
    }
    SUPPORTS_VIEW_REMOVAL_SUPPRESSION = bool;
  }
  
  public ChangeTransform() {}
  
  public ChangeTransform(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, Styleable.CHANGE_TRANSFORM);
    paramAttributeSet = (XmlPullParser)paramAttributeSet;
    mUseOverlay = TypedArrayUtils.getNamedBoolean(paramContext, paramAttributeSet, "reparentWithOverlay", 1, true);
    mReparent = TypedArrayUtils.getNamedBoolean(paramContext, paramAttributeSet, "reparent", 0, true);
    paramContext.recycle();
  }
  
  private void captureValues(TransitionValues paramTransitionValues)
  {
    View localView = view;
    if (localView.getVisibility() == 8) {
      return;
    }
    values.put("android:changeTransform:parent", localView.getParent());
    Object localObject = new Transforms(localView);
    values.put("android:changeTransform:transforms", localObject);
    localObject = localView.getMatrix();
    if ((localObject != null) && (!((Matrix)localObject).isIdentity())) {
      localObject = new Matrix((Matrix)localObject);
    } else {
      localObject = null;
    }
    values.put("android:changeTransform:matrix", localObject);
    if (mReparent)
    {
      localObject = new Matrix();
      ViewGroup localViewGroup = (ViewGroup)localView.getParent();
      ViewUtils.transformMatrixToGlobal(localViewGroup, (Matrix)localObject);
      ((Matrix)localObject).preTranslate(-localViewGroup.getScrollX(), -localViewGroup.getScrollY());
      values.put("android:changeTransform:parentMatrix", localObject);
      values.put("android:changeTransform:intermediateMatrix", localView.getTag(R.id.transition_transform));
      values.put("android:changeTransform:intermediateParentMatrix", localView.getTag(R.id.parent_matrix));
    }
  }
  
  private void createGhostView(ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    View localView = view;
    Object localObject = new Matrix((Matrix)values.get("android:changeTransform:parentMatrix"));
    ViewUtils.transformMatrixToLocal(paramViewGroup, (Matrix)localObject);
    localObject = GhostViewUtils.addGhost(localView, paramViewGroup, (Matrix)localObject);
    if (localObject == null) {
      return;
    }
    ((GhostView)localObject).reserveEndViewTransition((ViewGroup)values.get("android:changeTransform:parent"), view);
    for (paramViewGroup = this; mParent != null; paramViewGroup = mParent) {}
    paramViewGroup.addListener(new GhostListener(localView, (GhostView)localObject));
    if (SUPPORTS_VIEW_REMOVAL_SUPPRESSION)
    {
      if (view != view) {
        ViewUtils.setTransitionAlpha(view, 0.0F);
      }
      ViewUtils.setTransitionAlpha(localView, 1.0F);
    }
  }
  
  private ObjectAnimator createTransformAnimator(final TransitionValues paramTransitionValues1, final TransitionValues paramTransitionValues2, final boolean paramBoolean)
  {
    paramTransitionValues1 = (Matrix)values.get("android:changeTransform:matrix");
    Object localObject2 = (Matrix)values.get("android:changeTransform:matrix");
    Object localObject1 = paramTransitionValues1;
    if (paramTransitionValues1 == null) {
      localObject1 = MatrixUtils.IDENTITY_MATRIX;
    }
    paramTransitionValues1 = (TransitionValues)localObject2;
    if (localObject2 == null) {
      paramTransitionValues1 = MatrixUtils.IDENTITY_MATRIX;
    }
    if (((Matrix)localObject1).equals(paramTransitionValues1)) {
      return null;
    }
    localObject2 = (Transforms)values.get("android:changeTransform:transforms");
    paramTransitionValues2 = view;
    setIdentityTransforms(paramTransitionValues2);
    Object localObject3 = new float[9];
    ((Matrix)localObject1).getValues((float[])localObject3);
    float[] arrayOfFloat = new float[9];
    paramTransitionValues1.getValues(arrayOfFloat);
    localObject1 = new PathAnimatorMatrix(paramTransitionValues2, (float[])localObject3);
    PropertyValuesHolder localPropertyValuesHolder = PropertyValuesHolder.ofObject(NON_TRANSLATIONS_PROPERTY, new FloatArrayEvaluator(new float[9]), new float[][] { localObject3, arrayOfFloat });
    localObject3 = getPathMotion().getPath(localObject3[2], localObject3[5], arrayOfFloat[2], arrayOfFloat[5]);
    localObject3 = ObjectAnimator.ofPropertyValuesHolder(localObject1, new PropertyValuesHolder[] { localPropertyValuesHolder, PropertyValuesHolderUtils.ofPointF(TRANSLATIONS_PROPERTY, (Path)localObject3) });
    paramTransitionValues1 = new AnimatorListenerAdapter()
    {
      private boolean mIsCanceled;
      private Matrix mTempMatrix = new Matrix();
      
      private void setCurrentMatrix(Matrix paramAnonymousMatrix)
      {
        mTempMatrix.set(paramAnonymousMatrix);
        paramTransitionValues2.setTag(R.id.transition_transform, mTempMatrix);
        val$transforms.restore(paramTransitionValues2);
      }
      
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        mIsCanceled = true;
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if (!mIsCanceled) {
          if ((paramBoolean) && (mUseOverlay))
          {
            setCurrentMatrix(paramTransitionValues1);
          }
          else
          {
            paramTransitionValues2.setTag(R.id.transition_transform, null);
            paramTransitionValues2.setTag(R.id.parent_matrix, null);
          }
        }
        ViewUtils.setAnimationMatrix(paramTransitionValues2, null);
        val$transforms.restore(paramTransitionValues2);
      }
      
      public void onAnimationPause(Animator paramAnonymousAnimator)
      {
        setCurrentMatrix(val$pathAnimatorMatrix.getMatrix());
      }
      
      public void onAnimationResume(Animator paramAnonymousAnimator)
      {
        ChangeTransform.setIdentityTransforms(paramTransitionValues2);
      }
    };
    ((Animator)localObject3).addListener(paramTransitionValues1);
    AnimatorUtils.addPauseListener((Animator)localObject3, paramTransitionValues1);
    return localObject3;
  }
  
  private boolean parentsMatch(ViewGroup paramViewGroup1, ViewGroup paramViewGroup2)
  {
    if ((isValidTarget(paramViewGroup1)) && (isValidTarget(paramViewGroup2)))
    {
      paramViewGroup1 = getMatchedTransitionValues(paramViewGroup1, true);
      if ((paramViewGroup1 == null) || (paramViewGroup2 != view)) {}
    }
    else
    {
      while (paramViewGroup1 == paramViewGroup2) {
        return true;
      }
    }
    return false;
  }
  
  static void setIdentityTransforms(View paramView)
  {
    setTransforms(paramView, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
  }
  
  private void setMatricesForParent(TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    Matrix localMatrix1 = (Matrix)values.get("android:changeTransform:parentMatrix");
    view.setTag(R.id.parent_matrix, localMatrix1);
    Matrix localMatrix2 = mTempMatrix;
    localMatrix2.reset();
    localMatrix1.invert(localMatrix2);
    localMatrix1 = (Matrix)values.get("android:changeTransform:matrix");
    paramTransitionValues2 = localMatrix1;
    if (localMatrix1 == null)
    {
      paramTransitionValues2 = new Matrix();
      values.put("android:changeTransform:matrix", paramTransitionValues2);
    }
    paramTransitionValues2.postConcat((Matrix)values.get("android:changeTransform:parentMatrix"));
    paramTransitionValues2.postConcat(localMatrix2);
  }
  
  static void setTransforms(View paramView, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8)
  {
    paramView.setTranslationX(paramFloat1);
    paramView.setTranslationY(paramFloat2);
    ViewCompat.setTranslationZ(paramView, paramFloat3);
    paramView.setScaleX(paramFloat4);
    paramView.setScaleY(paramFloat5);
    paramView.setRotationX(paramFloat6);
    paramView.setRotationY(paramFloat7);
    paramView.setRotation(paramFloat8);
  }
  
  public void captureEndValues(TransitionValues paramTransitionValues)
  {
    captureValues(paramTransitionValues);
  }
  
  public void captureStartValues(TransitionValues paramTransitionValues)
  {
    captureValues(paramTransitionValues);
    if (!SUPPORTS_VIEW_REMOVAL_SUPPRESSION) {
      ((ViewGroup)view.getParent()).startViewTransition(view);
    }
  }
  
  public Animator createAnimator(ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    Object localObject;
    if ((paramTransitionValues1 != null) && (paramTransitionValues2 != null) && (values.containsKey("android:changeTransform:parent")) && (values.containsKey("android:changeTransform:parent")))
    {
      ViewGroup localViewGroup = (ViewGroup)values.get("android:changeTransform:parent");
      localObject = (ViewGroup)values.get("android:changeTransform:parent");
      boolean bool;
      if ((mReparent) && (!parentsMatch(localViewGroup, (ViewGroup)localObject))) {
        bool = true;
      } else {
        bool = false;
      }
      localObject = (Matrix)values.get("android:changeTransform:intermediateMatrix");
      if (localObject != null) {
        values.put("android:changeTransform:matrix", localObject);
      }
      localObject = (Matrix)values.get("android:changeTransform:intermediateParentMatrix");
      if (localObject != null) {
        values.put("android:changeTransform:parentMatrix", localObject);
      }
      if (bool) {
        setMatricesForParent(paramTransitionValues1, paramTransitionValues2);
      }
      localObject = createTransformAnimator(paramTransitionValues1, paramTransitionValues2, bool);
      if ((bool) && (localObject != null) && (mUseOverlay))
      {
        createGhostView(paramViewGroup, paramTransitionValues1, paramTransitionValues2);
        return localObject;
      }
      if (!SUPPORTS_VIEW_REMOVAL_SUPPRESSION)
      {
        localViewGroup.endViewTransition(view);
        return localObject;
      }
    }
    else
    {
      return null;
    }
    return localObject;
  }
  
  public boolean getReparent()
  {
    return mReparent;
  }
  
  public boolean getReparentWithOverlay()
  {
    return mUseOverlay;
  }
  
  public String[] getTransitionProperties()
  {
    return sTransitionProperties;
  }
  
  public void setReparent(boolean paramBoolean)
  {
    mReparent = paramBoolean;
  }
  
  public void setReparentWithOverlay(boolean paramBoolean)
  {
    mUseOverlay = paramBoolean;
  }
  
  private static class GhostListener
    extends TransitionListenerAdapter
  {
    private GhostView mGhostView;
    private View mView;
    
    GhostListener(View paramView, GhostView paramGhostView)
    {
      mView = paramView;
      mGhostView = paramGhostView;
    }
    
    public void onTransitionEnd(Transition paramTransition)
    {
      paramTransition.removeListener(this);
      GhostViewUtils.removeGhost(mView);
      mView.setTag(R.id.transition_transform, null);
      mView.setTag(R.id.parent_matrix, null);
    }
    
    public void onTransitionPause(Transition paramTransition)
    {
      mGhostView.setVisibility(4);
    }
    
    public void onTransitionResume(Transition paramTransition)
    {
      mGhostView.setVisibility(0);
    }
  }
  
  private static class PathAnimatorMatrix
  {
    private final Matrix mMatrix = new Matrix();
    private float mTranslationX;
    private float mTranslationY;
    private final float[] mValues;
    private final View mView;
    
    PathAnimatorMatrix(View paramView, float[] paramArrayOfFloat)
    {
      mView = paramView;
      mValues = ((float[])paramArrayOfFloat.clone());
      mTranslationX = mValues[2];
      mTranslationY = mValues[5];
      setAnimationMatrix();
    }
    
    private void setAnimationMatrix()
    {
      mValues[2] = mTranslationX;
      mValues[5] = mTranslationY;
      mMatrix.setValues(mValues);
      ViewUtils.setAnimationMatrix(mView, mMatrix);
    }
    
    Matrix getMatrix()
    {
      return mMatrix;
    }
    
    void setTranslation(PointF paramPointF)
    {
      mTranslationX = x;
      mTranslationY = y;
      setAnimationMatrix();
    }
    
    void setValues(float[] paramArrayOfFloat)
    {
      System.arraycopy(paramArrayOfFloat, 0, mValues, 0, paramArrayOfFloat.length);
      setAnimationMatrix();
    }
  }
  
  private static class Transforms
  {
    final float mRotationX;
    final float mRotationY;
    final float mRotationZ;
    final float mScaleX;
    final float mScaleY;
    final float mTranslationX;
    final float mTranslationY;
    final float mTranslationZ;
    
    Transforms(View paramView)
    {
      mTranslationX = paramView.getTranslationX();
      mTranslationY = paramView.getTranslationY();
      mTranslationZ = ViewCompat.getTranslationZ(paramView);
      mScaleX = paramView.getScaleX();
      mScaleY = paramView.getScaleY();
      mRotationX = paramView.getRotationX();
      mRotationY = paramView.getRotationY();
      mRotationZ = paramView.getRotation();
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof Transforms)) {
        return false;
      }
      paramObject = (Transforms)paramObject;
      return (mTranslationX == mTranslationX) && (mTranslationY == mTranslationY) && (mTranslationZ == mTranslationZ) && (mScaleX == mScaleX) && (mScaleY == mScaleY) && (mRotationX == mRotationX) && (mRotationY == mRotationY) && (mRotationZ == mRotationZ);
    }
    
    public int hashCode()
    {
      float f = mTranslationX;
      int i3 = 0;
      int i;
      if (f != 0.0F) {
        i = Float.floatToIntBits(mTranslationX);
      } else {
        i = 0;
      }
      int j;
      if (mTranslationY != 0.0F) {
        j = Float.floatToIntBits(mTranslationY);
      } else {
        j = 0;
      }
      int k;
      if (mTranslationZ != 0.0F) {
        k = Float.floatToIntBits(mTranslationZ);
      } else {
        k = 0;
      }
      int m;
      if (mScaleX != 0.0F) {
        m = Float.floatToIntBits(mScaleX);
      } else {
        m = 0;
      }
      int n;
      if (mScaleY != 0.0F) {
        n = Float.floatToIntBits(mScaleY);
      } else {
        n = 0;
      }
      int i1;
      if (mRotationX != 0.0F) {
        i1 = Float.floatToIntBits(mRotationX);
      } else {
        i1 = 0;
      }
      int i2;
      if (mRotationY != 0.0F) {
        i2 = Float.floatToIntBits(mRotationY);
      } else {
        i2 = 0;
      }
      if (mRotationZ != 0.0F) {
        i3 = Float.floatToIntBits(mRotationZ);
      }
      return ((((((i * 31 + j) * 31 + k) * 31 + m) * 31 + n) * 31 + i1) * 31 + i2) * 31 + i3;
    }
    
    public void restore(View paramView)
    {
      ChangeTransform.setTransforms(paramView, mTranslationX, mTranslationY, mTranslationZ, mScaleX, mScaleY, mRotationX, mRotationY, mRotationZ);
    }
  }
}
