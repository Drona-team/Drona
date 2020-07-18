package androidx.appcompat.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.DataSetObserver;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.AbsSavedState;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AbsSpinner;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.R.attr;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.content.wiki.AppCompatResources;
import androidx.appcompat.view.menu.ShowableListMenu;
import androidx.core.view.TintableBackgroundView;
import androidx.core.view.ViewCompat;

public class AppCompatSpinner
  extends Spinner
  implements TintableBackgroundView
{
  private static final int[] ATTRS_ANDROID_SPINNERMODE = { 16843505 };
  private static final int MAX_ITEMS_MEASURED = 15;
  private static final int MODE_DIALOG = 0;
  private static final int MODE_DROPDOWN = 1;
  private static final int MODE_THEME = -1;
  private static final String TAG = "AppCompatSpinner";
  private final AppCompatBackgroundHelper mBackgroundTintHelper;
  int mDropDownWidth;
  private ForwardingListener mForwardingListener;
  private SpinnerPopup mPopup;
  private final Context mPopupContext;
  private final boolean mPopupSet;
  private SpinnerAdapter mTempAdapter;
  final Rect mTempRect;
  
  public AppCompatSpinner(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AppCompatSpinner(Context paramContext, int paramInt)
  {
    this(paramContext, null, R.attr.spinnerStyle, paramInt);
  }
  
  public AppCompatSpinner(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, R.attr.spinnerStyle);
  }
  
  public AppCompatSpinner(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, -1);
  }
  
  public AppCompatSpinner(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    this(paramContext, paramAttributeSet, paramInt1, paramInt2, null);
  }
  
  /* Error */
  public AppCompatSpinner(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2, final Resources.Theme paramTheme)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: aload_2
    //   3: iload_3
    //   4: invokespecial 94	android/widget/Spinner:<init>	(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    //   7: aload_0
    //   8: new 96	android/graphics/Rect
    //   11: dup
    //   12: invokespecial 98	android/graphics/Rect:<init>	()V
    //   15: putfield 100	androidx/appcompat/widget/AppCompatSpinner:mTempRect	Landroid/graphics/Rect;
    //   18: aload_1
    //   19: aload_2
    //   20: getstatic 105	androidx/appcompat/R$styleable:Spinner	[I
    //   23: iload_3
    //   24: iconst_0
    //   25: invokestatic 111	androidx/appcompat/widget/TintTypedArray:obtainStyledAttributes	(Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroidx/appcompat/widget/TintTypedArray;
    //   28: astore 11
    //   30: aload_0
    //   31: new 113	androidx/appcompat/widget/AppCompatBackgroundHelper
    //   34: dup
    //   35: aload_0
    //   36: invokespecial 116	androidx/appcompat/widget/AppCompatBackgroundHelper:<init>	(Landroid/view/View;)V
    //   39: putfield 118	androidx/appcompat/widget/AppCompatSpinner:mBackgroundTintHelper	Landroidx/appcompat/widget/AppCompatBackgroundHelper;
    //   42: aload 5
    //   44: ifnull +20 -> 64
    //   47: aload_0
    //   48: new 120	androidx/appcompat/view/ContextThemeWrapper
    //   51: dup
    //   52: aload_1
    //   53: aload 5
    //   55: invokespecial 123	androidx/appcompat/view/ContextThemeWrapper:<init>	(Landroid/content/Context;Landroid/content/res/Resources$Theme;)V
    //   58: putfield 125	androidx/appcompat/widget/AppCompatSpinner:mPopupContext	Landroid/content/Context;
    //   61: goto +41 -> 102
    //   64: aload 11
    //   66: getstatic 128	androidx/appcompat/R$styleable:Spinner_popupTheme	I
    //   69: iconst_0
    //   70: invokevirtual 132	androidx/appcompat/widget/TintTypedArray:getResourceId	(II)I
    //   73: istore 6
    //   75: iload 6
    //   77: ifeq +20 -> 97
    //   80: aload_0
    //   81: new 120	androidx/appcompat/view/ContextThemeWrapper
    //   84: dup
    //   85: aload_1
    //   86: iload 6
    //   88: invokespecial 134	androidx/appcompat/view/ContextThemeWrapper:<init>	(Landroid/content/Context;I)V
    //   91: putfield 125	androidx/appcompat/widget/AppCompatSpinner:mPopupContext	Landroid/content/Context;
    //   94: goto +8 -> 102
    //   97: aload_0
    //   98: aload_1
    //   99: putfield 125	androidx/appcompat/widget/AppCompatSpinner:mPopupContext	Landroid/content/Context;
    //   102: iload 4
    //   104: istore 7
    //   106: iload 4
    //   108: iconst_m1
    //   109: if_icmpne +138 -> 247
    //   112: getstatic 68	androidx/appcompat/widget/AppCompatSpinner:ATTRS_ANDROID_SPINNERMODE	[I
    //   115: astore 5
    //   117: aload_1
    //   118: aload_2
    //   119: aload 5
    //   121: iload_3
    //   122: iconst_0
    //   123: invokevirtual 139	android/content/Context:obtainStyledAttributes	(Landroid/util/AttributeSet;[III)Landroid/content/res/TypedArray;
    //   126: astore 10
    //   128: aload 10
    //   130: astore 5
    //   132: aload 5
    //   134: astore 9
    //   136: aload 10
    //   138: iconst_0
    //   139: invokevirtual 145	android/content/res/TypedArray:hasValue	(I)Z
    //   142: istore 8
    //   144: iload 4
    //   146: istore 6
    //   148: iload 8
    //   150: ifeq +16 -> 166
    //   153: aload 5
    //   155: astore 9
    //   157: aload 10
    //   159: iconst_0
    //   160: iconst_0
    //   161: invokevirtual 148	android/content/res/TypedArray:getInt	(II)I
    //   164: istore 6
    //   166: iload 6
    //   168: istore 7
    //   170: aload 10
    //   172: ifnull +75 -> 247
    //   175: iload 6
    //   177: istore 4
    //   179: aload 5
    //   181: invokevirtual 151	android/content/res/TypedArray:recycle	()V
    //   184: iload 4
    //   186: istore 7
    //   188: goto +59 -> 247
    //   191: astore 10
    //   193: goto +15 -> 208
    //   196: astore_1
    //   197: aconst_null
    //   198: astore 9
    //   200: goto +35 -> 235
    //   203: astore 10
    //   205: aconst_null
    //   206: astore 5
    //   208: aload 5
    //   210: astore 9
    //   212: ldc 48
    //   214: ldc -103
    //   216: aload 10
    //   218: invokestatic 159	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   221: pop
    //   222: iload 4
    //   224: istore 7
    //   226: aload 5
    //   228: ifnull +19 -> 247
    //   231: goto -52 -> 179
    //   234: astore_1
    //   235: aload 9
    //   237: ifnull +8 -> 245
    //   240: aload 9
    //   242: invokevirtual 151	android/content/res/TypedArray:recycle	()V
    //   245: aload_1
    //   246: athrow
    //   247: iload 7
    //   249: lookupswitch	default:+27->276, 0:+133->382, 1:+33->282
    //   276: goto +3 -> 279
    //   279: goto +132 -> 411
    //   282: new 18	androidx/appcompat/widget/AppCompatSpinner$DropdownPopup
    //   285: dup
    //   286: aload_0
    //   287: aload_0
    //   288: getfield 125	androidx/appcompat/widget/AppCompatSpinner:mPopupContext	Landroid/content/Context;
    //   291: aload_2
    //   292: iload_3
    //   293: invokespecial 162	androidx/appcompat/widget/AppCompatSpinner$DropdownPopup:<init>	(Landroidx/appcompat/widget/AppCompatSpinner;Landroid/content/Context;Landroid/util/AttributeSet;I)V
    //   296: astore 5
    //   298: aload_0
    //   299: getfield 125	androidx/appcompat/widget/AppCompatSpinner:mPopupContext	Landroid/content/Context;
    //   302: aload_2
    //   303: getstatic 105	androidx/appcompat/R$styleable:Spinner	[I
    //   306: iload_3
    //   307: iconst_0
    //   308: invokestatic 111	androidx/appcompat/widget/TintTypedArray:obtainStyledAttributes	(Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroidx/appcompat/widget/TintTypedArray;
    //   311: astore 9
    //   313: aload_0
    //   314: aload 9
    //   316: getstatic 165	androidx/appcompat/R$styleable:Spinner_android_dropDownWidth	I
    //   319: bipush -2
    //   321: invokevirtual 168	androidx/appcompat/widget/TintTypedArray:getLayoutDimension	(II)I
    //   324: putfield 170	androidx/appcompat/widget/AppCompatSpinner:mDropDownWidth	I
    //   327: aload 5
    //   329: aload 9
    //   331: getstatic 173	androidx/appcompat/R$styleable:Spinner_android_popupBackground	I
    //   334: invokevirtual 177	androidx/appcompat/widget/TintTypedArray:getDrawable	(I)Landroid/graphics/drawable/Drawable;
    //   337: invokevirtual 183	androidx/appcompat/widget/ListPopupWindow:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   340: aload 5
    //   342: aload 11
    //   344: getstatic 186	androidx/appcompat/R$styleable:Spinner_android_prompt	I
    //   347: invokevirtual 190	androidx/appcompat/widget/TintTypedArray:getString	(I)Ljava/lang/String;
    //   350: invokevirtual 194	androidx/appcompat/widget/AppCompatSpinner$DropdownPopup:setPromptText	(Ljava/lang/CharSequence;)V
    //   353: aload 9
    //   355: invokevirtual 195	androidx/appcompat/widget/TintTypedArray:recycle	()V
    //   358: aload_0
    //   359: aload 5
    //   361: putfield 197	androidx/appcompat/widget/AppCompatSpinner:mPopup	Landroidx/appcompat/widget/AppCompatSpinner$SpinnerPopup;
    //   364: aload_0
    //   365: new 8	androidx/appcompat/widget/AppCompatSpinner$1
    //   368: dup
    //   369: aload_0
    //   370: aload_0
    //   371: aload 5
    //   373: invokespecial 200	androidx/appcompat/widget/AppCompatSpinner$1:<init>	(Landroidx/appcompat/widget/AppCompatSpinner;Landroid/view/View;Landroidx/appcompat/widget/AppCompatSpinner$DropdownPopup;)V
    //   376: putfield 202	androidx/appcompat/widget/AppCompatSpinner:mForwardingListener	Landroidx/appcompat/widget/ForwardingListener;
    //   379: goto +32 -> 411
    //   382: aload_0
    //   383: new 12	androidx/appcompat/widget/AppCompatSpinner$DialogPopup
    //   386: dup
    //   387: aload_0
    //   388: invokespecial 205	androidx/appcompat/widget/AppCompatSpinner$DialogPopup:<init>	(Landroidx/appcompat/widget/AppCompatSpinner;)V
    //   391: putfield 197	androidx/appcompat/widget/AppCompatSpinner:mPopup	Landroidx/appcompat/widget/AppCompatSpinner$SpinnerPopup;
    //   394: aload_0
    //   395: getfield 197	androidx/appcompat/widget/AppCompatSpinner:mPopup	Landroidx/appcompat/widget/AppCompatSpinner$SpinnerPopup;
    //   398: aload 11
    //   400: getstatic 186	androidx/appcompat/R$styleable:Spinner_android_prompt	I
    //   403: invokevirtual 190	androidx/appcompat/widget/TintTypedArray:getString	(I)Ljava/lang/String;
    //   406: invokeinterface 206 2 0
    //   411: aload 11
    //   413: getstatic 209	androidx/appcompat/R$styleable:Spinner_android_entries	I
    //   416: invokevirtual 213	androidx/appcompat/widget/TintTypedArray:getTextArray	(I)[Ljava/lang/CharSequence;
    //   419: astore 5
    //   421: aload 5
    //   423: ifnull +28 -> 451
    //   426: new 215	android/widget/ArrayAdapter
    //   429: dup
    //   430: aload_1
    //   431: ldc -40
    //   433: aload 5
    //   435: invokespecial 219	android/widget/ArrayAdapter:<init>	(Landroid/content/Context;I[Ljava/lang/Object;)V
    //   438: astore_1
    //   439: aload_1
    //   440: getstatic 224	androidx/appcompat/R$layout:support_simple_spinner_dropdown_item	I
    //   443: invokevirtual 228	android/widget/ArrayAdapter:setDropDownViewResource	(I)V
    //   446: aload_0
    //   447: aload_1
    //   448: invokevirtual 232	androidx/appcompat/widget/AppCompatSpinner:setAdapter	(Landroid/widget/SpinnerAdapter;)V
    //   451: aload 11
    //   453: invokevirtual 195	androidx/appcompat/widget/TintTypedArray:recycle	()V
    //   456: aload_0
    //   457: iconst_1
    //   458: putfield 234	androidx/appcompat/widget/AppCompatSpinner:mPopupSet	Z
    //   461: aload_0
    //   462: getfield 236	androidx/appcompat/widget/AppCompatSpinner:mTempAdapter	Landroid/widget/SpinnerAdapter;
    //   465: ifnull +16 -> 481
    //   468: aload_0
    //   469: aload_0
    //   470: getfield 236	androidx/appcompat/widget/AppCompatSpinner:mTempAdapter	Landroid/widget/SpinnerAdapter;
    //   473: invokevirtual 232	androidx/appcompat/widget/AppCompatSpinner:setAdapter	(Landroid/widget/SpinnerAdapter;)V
    //   476: aload_0
    //   477: aconst_null
    //   478: putfield 236	androidx/appcompat/widget/AppCompatSpinner:mTempAdapter	Landroid/widget/SpinnerAdapter;
    //   481: aload_0
    //   482: getfield 118	androidx/appcompat/widget/AppCompatSpinner:mBackgroundTintHelper	Landroidx/appcompat/widget/AppCompatBackgroundHelper;
    //   485: aload_2
    //   486: iload_3
    //   487: invokevirtual 240	androidx/appcompat/widget/AppCompatBackgroundHelper:loadFromAttributes	(Landroid/util/AttributeSet;I)V
    //   490: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	491	0	this	AppCompatSpinner
    //   0	491	1	paramContext	Context
    //   0	491	2	paramAttributeSet	AttributeSet
    //   0	491	3	paramInt1	int
    //   0	491	4	paramInt2	int
    //   0	491	5	paramTheme	Resources.Theme
    //   73	103	6	i	int
    //   104	144	7	j	int
    //   142	7	8	bool	boolean
    //   134	220	9	localObject	Object
    //   126	45	10	localTypedArray	android.content.res.TypedArray
    //   191	1	10	localException1	Exception
    //   203	14	10	localException2	Exception
    //   28	424	11	localTintTypedArray	TintTypedArray
    // Exception table:
    //   from	to	target	type
    //   136	144	191	java/lang/Exception
    //   157	166	191	java/lang/Exception
    //   117	128	196	java/lang/Throwable
    //   117	128	203	java/lang/Exception
    //   136	144	234	java/lang/Throwable
    //   157	166	234	java/lang/Throwable
    //   212	222	234	java/lang/Throwable
  }
  
  int compatMeasureContentWidth(SpinnerAdapter paramSpinnerAdapter, Drawable paramDrawable)
  {
    int k = 0;
    if (paramSpinnerAdapter == null) {
      return 0;
    }
    int i1 = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 0);
    int i2 = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 0);
    int i = Math.max(0, getSelectedItemPosition());
    int i3 = Math.min(paramSpinnerAdapter.getCount(), i + 15);
    i = Math.max(0, i - (15 - (i3 - i)));
    Object localObject = null;
    int j = 0;
    while (i < i3)
    {
      int n = paramSpinnerAdapter.getItemViewType(i);
      int m = k;
      if (n != k)
      {
        localObject = null;
        m = n;
      }
      View localView = paramSpinnerAdapter.getView(i, (View)localObject, this);
      localObject = localView;
      if (localView.getLayoutParams() == null) {
        localView.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
      }
      localView.measure(i1, i2);
      j = Math.max(j, localView.getMeasuredWidth());
      i += 1;
      k = m;
    }
    if (paramDrawable != null)
    {
      paramDrawable.getPadding(mTempRect);
      return j + (mTempRect.left + mTempRect.right);
    }
    return j;
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    if (mBackgroundTintHelper != null) {
      mBackgroundTintHelper.applySupportBackgroundTint();
    }
  }
  
  public int getDropDownHorizontalOffset()
  {
    if (mPopup != null) {
      return mPopup.getHorizontalOffset();
    }
    if (Build.VERSION.SDK_INT >= 16) {
      return super.getDropDownHorizontalOffset();
    }
    return 0;
  }
  
  public int getDropDownVerticalOffset()
  {
    if (mPopup != null) {
      return mPopup.getVerticalOffset();
    }
    if (Build.VERSION.SDK_INT >= 16) {
      return super.getDropDownVerticalOffset();
    }
    return 0;
  }
  
  public int getDropDownWidth()
  {
    if (mPopup != null) {
      return mDropDownWidth;
    }
    if (Build.VERSION.SDK_INT >= 16) {
      return super.getDropDownWidth();
    }
    return 0;
  }
  
  final SpinnerPopup getInternalPopup()
  {
    return mPopup;
  }
  
  public Drawable getPopupBackground()
  {
    if (mPopup != null) {
      return mPopup.getBackground();
    }
    if (Build.VERSION.SDK_INT >= 16) {
      return super.getPopupBackground();
    }
    return null;
  }
  
  public Context getPopupContext()
  {
    return mPopupContext;
  }
  
  public CharSequence getPrompt()
  {
    if (mPopup != null) {
      return mPopup.getHintText();
    }
    return super.getPrompt();
  }
  
  public ColorStateList getSupportBackgroundTintList()
  {
    if (mBackgroundTintHelper != null) {
      return mBackgroundTintHelper.getSupportBackgroundTintList();
    }
    return null;
  }
  
  public PorterDuff.Mode getSupportBackgroundTintMode()
  {
    if (mBackgroundTintHelper != null) {
      return mBackgroundTintHelper.getSupportBackgroundTintMode();
    }
    return null;
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if ((mPopup != null) && (mPopup.isShowing())) {
      mPopup.dismiss();
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if ((mPopup != null) && (View.MeasureSpec.getMode(paramInt1) == Integer.MIN_VALUE)) {
      setMeasuredDimension(Math.min(Math.max(getMeasuredWidth(), compatMeasureContentWidth(getAdapter(), getBackground())), View.MeasureSpec.getSize(paramInt1)), getMeasuredHeight());
    }
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    if (mShowDropdown)
    {
      paramParcelable = getViewTreeObserver();
      if (paramParcelable != null) {
        paramParcelable.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
          public void onGlobalLayout()
          {
            if (!getInternalPopup().isShowing()) {
              showPopup();
            }
            ViewTreeObserver localViewTreeObserver = getViewTreeObserver();
            if (localViewTreeObserver != null)
            {
              if (Build.VERSION.SDK_INT >= 16)
              {
                localViewTreeObserver.removeOnGlobalLayoutListener(this);
                return;
              }
              localViewTreeObserver.removeGlobalOnLayoutListener(this);
            }
          }
        });
      }
    }
  }
  
  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    boolean bool;
    if ((mPopup != null) && (mPopup.isShowing())) {
      bool = true;
    } else {
      bool = false;
    }
    mShowDropdown = bool;
    return localSavedState;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((mForwardingListener != null) && (mForwardingListener.onTouch(this, paramMotionEvent))) {
      return true;
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public boolean performClick()
  {
    if (mPopup != null)
    {
      if (!mPopup.isShowing()) {
        showPopup();
      }
      return true;
    }
    return super.performClick();
  }
  
  public void setAdapter(SpinnerAdapter paramSpinnerAdapter)
  {
    if (!mPopupSet)
    {
      mTempAdapter = paramSpinnerAdapter;
      return;
    }
    super.setAdapter(paramSpinnerAdapter);
    if (mPopup != null)
    {
      Context localContext;
      if (mPopupContext == null) {
        localContext = getContext();
      } else {
        localContext = mPopupContext;
      }
      mPopup.setAdapter(new DropDownAdapter(paramSpinnerAdapter, localContext.getTheme()));
    }
  }
  
  public void setBackgroundDrawable(Drawable paramDrawable)
  {
    super.setBackgroundDrawable(paramDrawable);
    if (mBackgroundTintHelper != null) {
      mBackgroundTintHelper.onSetBackgroundDrawable(paramDrawable);
    }
  }
  
  public void setBackgroundResource(int paramInt)
  {
    super.setBackgroundResource(paramInt);
    if (mBackgroundTintHelper != null) {
      mBackgroundTintHelper.onSetBackgroundResource(paramInt);
    }
  }
  
  public void setDropDownHorizontalOffset(int paramInt)
  {
    if (mPopup != null)
    {
      mPopup.setHorizontalOriginalOffset(paramInt);
      mPopup.setHorizontalOffset(paramInt);
      return;
    }
    if (Build.VERSION.SDK_INT >= 16) {
      super.setDropDownHorizontalOffset(paramInt);
    }
  }
  
  public void setDropDownVerticalOffset(int paramInt)
  {
    if (mPopup != null)
    {
      mPopup.setVerticalOffset(paramInt);
      return;
    }
    if (Build.VERSION.SDK_INT >= 16) {
      super.setDropDownVerticalOffset(paramInt);
    }
  }
  
  public void setDropDownWidth(int paramInt)
  {
    if (mPopup != null)
    {
      mDropDownWidth = paramInt;
      return;
    }
    if (Build.VERSION.SDK_INT >= 16) {
      super.setDropDownWidth(paramInt);
    }
  }
  
  public void setPopupBackgroundDrawable(Drawable paramDrawable)
  {
    if (mPopup != null)
    {
      mPopup.setBackgroundDrawable(paramDrawable);
      return;
    }
    if (Build.VERSION.SDK_INT >= 16) {
      super.setPopupBackgroundDrawable(paramDrawable);
    }
  }
  
  public void setPopupBackgroundResource(int paramInt)
  {
    setPopupBackgroundDrawable(AppCompatResources.getDrawable(getPopupContext(), paramInt));
  }
  
  public void setPrompt(CharSequence paramCharSequence)
  {
    if (mPopup != null)
    {
      mPopup.setPromptText(paramCharSequence);
      return;
    }
    super.setPrompt(paramCharSequence);
  }
  
  public void setSupportBackgroundTintList(ColorStateList paramColorStateList)
  {
    if (mBackgroundTintHelper != null) {
      mBackgroundTintHelper.setSupportBackgroundTintList(paramColorStateList);
    }
  }
  
  public void setSupportBackgroundTintMode(PorterDuff.Mode paramMode)
  {
    if (mBackgroundTintHelper != null) {
      mBackgroundTintHelper.setSupportBackgroundTintMode(paramMode);
    }
  }
  
  void showPopup()
  {
    if (Build.VERSION.SDK_INT >= 17)
    {
      mPopup.show(getTextDirection(), getTextAlignment());
      return;
    }
    mPopup.show(-1, -1);
  }
  
  @VisibleForTesting
  class DialogPopup
    implements AppCompatSpinner.SpinnerPopup, DialogInterface.OnClickListener
  {
    private ListAdapter mListAdapter;
    @VisibleForTesting
    AlertDialog mPopup;
    private CharSequence mPrompt;
    
    DialogPopup() {}
    
    public void dismiss()
    {
      if (mPopup != null)
      {
        mPopup.dismiss();
        mPopup = null;
      }
    }
    
    public Drawable getBackground()
    {
      return null;
    }
    
    public CharSequence getHintText()
    {
      return mPrompt;
    }
    
    public int getHorizontalOffset()
    {
      return 0;
    }
    
    public int getHorizontalOriginalOffset()
    {
      return 0;
    }
    
    public int getVerticalOffset()
    {
      return 0;
    }
    
    public boolean isShowing()
    {
      if (mPopup != null) {
        return mPopup.isShowing();
      }
      return false;
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      setSelection(paramInt);
      if (getOnItemClickListener() != null) {
        performItemClick(null, paramInt, mListAdapter.getItemId(paramInt));
      }
      dismiss();
    }
    
    public void setAdapter(ListAdapter paramListAdapter)
    {
      mListAdapter = paramListAdapter;
    }
    
    public void setBackgroundDrawable(Drawable paramDrawable)
    {
      Log.e("AppCompatSpinner", "Cannot set popup background for MODE_DIALOG, ignoring");
    }
    
    public void setHorizontalOffset(int paramInt)
    {
      Log.e("AppCompatSpinner", "Cannot set horizontal offset for MODE_DIALOG, ignoring");
    }
    
    public void setHorizontalOriginalOffset(int paramInt)
    {
      Log.e("AppCompatSpinner", "Cannot set horizontal (original) offset for MODE_DIALOG, ignoring");
    }
    
    public void setPromptText(CharSequence paramCharSequence)
    {
      mPrompt = paramCharSequence;
    }
    
    public void setVerticalOffset(int paramInt)
    {
      Log.e("AppCompatSpinner", "Cannot set vertical offset for MODE_DIALOG, ignoring");
    }
    
    public void show(int paramInt1, int paramInt2)
    {
      if (mListAdapter == null) {
        return;
      }
      Object localObject = new AlertDialog.Builder(getPopupContext());
      if (mPrompt != null) {
        ((AlertDialog.Builder)localObject).setTitle(mPrompt);
      }
      mPopup = ((AlertDialog.Builder)localObject).setSingleChoiceItems(mListAdapter, getSelectedItemPosition(), this).create();
      localObject = mPopup.getListView();
      if (Build.VERSION.SDK_INT >= 17)
      {
        ((View)localObject).setTextDirection(paramInt1);
        ((View)localObject).setTextAlignment(paramInt2);
      }
      mPopup.show();
    }
  }
  
  private static class DropDownAdapter
    implements ListAdapter, SpinnerAdapter
  {
    private SpinnerAdapter mAdapter;
    private ListAdapter mListAdapter;
    
    public DropDownAdapter(SpinnerAdapter paramSpinnerAdapter, Resources.Theme paramTheme)
    {
      mAdapter = paramSpinnerAdapter;
      if ((paramSpinnerAdapter instanceof ListAdapter)) {
        mListAdapter = ((ListAdapter)paramSpinnerAdapter);
      }
      if (paramTheme != null) {
        if ((Build.VERSION.SDK_INT >= 23) && ((paramSpinnerAdapter instanceof android.widget.ThemedSpinnerAdapter)))
        {
          paramSpinnerAdapter = (android.widget.ThemedSpinnerAdapter)paramSpinnerAdapter;
          if (paramSpinnerAdapter.getDropDownViewTheme() != paramTheme) {
            paramSpinnerAdapter.setDropDownViewTheme(paramTheme);
          }
        }
        else if ((paramSpinnerAdapter instanceof ThemedSpinnerAdapter))
        {
          paramSpinnerAdapter = (ThemedSpinnerAdapter)paramSpinnerAdapter;
          if (paramSpinnerAdapter.getDropDownViewTheme() == null) {
            paramSpinnerAdapter.setDropDownViewTheme(paramTheme);
          }
        }
      }
    }
    
    public boolean areAllItemsEnabled()
    {
      ListAdapter localListAdapter = mListAdapter;
      if (localListAdapter != null) {
        return localListAdapter.areAllItemsEnabled();
      }
      return true;
    }
    
    public int getCount()
    {
      if (mAdapter == null) {
        return 0;
      }
      return mAdapter.getCount();
    }
    
    public View getDropDownView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (mAdapter == null) {
        return null;
      }
      return mAdapter.getDropDownView(paramInt, paramView, paramViewGroup);
    }
    
    public Object getItem(int paramInt)
    {
      if (mAdapter == null) {
        return null;
      }
      return mAdapter.getItem(paramInt);
    }
    
    public long getItemId(int paramInt)
    {
      if (mAdapter == null) {
        return -1L;
      }
      return mAdapter.getItemId(paramInt);
    }
    
    public int getItemViewType(int paramInt)
    {
      return 0;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      return getDropDownView(paramInt, paramView, paramViewGroup);
    }
    
    public int getViewTypeCount()
    {
      return 1;
    }
    
    public boolean hasStableIds()
    {
      return (mAdapter != null) && (mAdapter.hasStableIds());
    }
    
    public boolean isEmpty()
    {
      return getCount() == 0;
    }
    
    public boolean isEnabled(int paramInt)
    {
      ListAdapter localListAdapter = mListAdapter;
      if (localListAdapter != null) {
        return localListAdapter.isEnabled(paramInt);
      }
      return true;
    }
    
    public void registerDataSetObserver(DataSetObserver paramDataSetObserver)
    {
      if (mAdapter != null) {
        mAdapter.registerDataSetObserver(paramDataSetObserver);
      }
    }
    
    public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver)
    {
      if (mAdapter != null) {
        mAdapter.unregisterDataSetObserver(paramDataSetObserver);
      }
    }
  }
  
  @VisibleForTesting
  class DropdownPopup
    extends ListPopupWindow
    implements AppCompatSpinner.SpinnerPopup
  {
    ListAdapter mAdapter;
    private CharSequence mHintText;
    private int mOriginalHorizontalOffset;
    private final Rect mVisibleRect = new Rect();
    
    public DropdownPopup(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
      super(paramAttributeSet, paramInt);
      setAnchorView(AppCompatSpinner.this);
      setModal(true);
      setPromptPosition(0);
      setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          setSelection(paramAnonymousInt);
          if (getOnItemClickListener() != null) {
            performItemClick(paramAnonymousView, paramAnonymousInt, mAdapter.getItemId(paramAnonymousInt));
          }
          dismiss();
        }
      });
    }
    
    void computeContentWidth()
    {
      Object localObject = getBackground();
      int i = 0;
      if (localObject != null)
      {
        ((Drawable)localObject).getPadding(mTempRect);
        if (ViewUtils.isLayoutRtl(AppCompatSpinner.this)) {}
        for (i = mTempRect.right;; i = -mTempRect.left) {
          break;
        }
      }
      localObject = mTempRect;
      mTempRect.right = 0;
      left = 0;
      int n = getPaddingLeft();
      int i1 = getPaddingRight();
      int i2 = getWidth();
      if (mDropDownWidth == -2)
      {
        int k = compatMeasureContentWidth((SpinnerAdapter)mAdapter, getBackground());
        int j = k;
        int m = getContext().getResources().getDisplayMetrics().widthPixels - mTempRect.left - mTempRect.right;
        if (k > m) {
          j = m;
        }
        setContentWidth(Math.max(j, i2 - n - i1));
      }
      else if (mDropDownWidth == -1)
      {
        setContentWidth(i2 - n - i1);
      }
      else
      {
        setContentWidth(mDropDownWidth);
      }
      if (ViewUtils.isLayoutRtl(AppCompatSpinner.this)) {
        i += i2 - i1 - getWidth() - getHorizontalOriginalOffset();
      } else {
        i += n + getHorizontalOriginalOffset();
      }
      setHorizontalOffset(i);
    }
    
    public CharSequence getHintText()
    {
      return mHintText;
    }
    
    public int getHorizontalOriginalOffset()
    {
      return mOriginalHorizontalOffset;
    }
    
    boolean isVisibleToUser(View paramView)
    {
      return (ViewCompat.isAttachedToWindow(paramView)) && (paramView.getGlobalVisibleRect(mVisibleRect));
    }
    
    public void setAdapter(ListAdapter paramListAdapter)
    {
      super.setAdapter(paramListAdapter);
      mAdapter = paramListAdapter;
    }
    
    public void setHorizontalOriginalOffset(int paramInt)
    {
      mOriginalHorizontalOffset = paramInt;
    }
    
    public void setPromptText(CharSequence paramCharSequence)
    {
      mHintText = paramCharSequence;
    }
    
    public void show(int paramInt1, int paramInt2)
    {
      boolean bool = isShowing();
      computeContentWidth();
      setInputMethodMode(2);
      super.show();
      Object localObject = getListView();
      ((AbsListView)localObject).setChoiceMode(1);
      if (Build.VERSION.SDK_INT >= 17)
      {
        ((View)localObject).setTextDirection(paramInt1);
        ((View)localObject).setTextAlignment(paramInt2);
      }
      setSelection(getSelectedItemPosition());
      if (bool) {
        return;
      }
      localObject = getViewTreeObserver();
      if (localObject != null)
      {
        final ViewTreeObserver.OnGlobalLayoutListener local2 = new ViewTreeObserver.OnGlobalLayoutListener()
        {
          public void onGlobalLayout()
          {
            if (!isVisibleToUser(AppCompatSpinner.this))
            {
              dismiss();
              return;
            }
            computeContentWidth();
            AppCompatSpinner.DropdownPopup.this.show();
          }
        };
        ((ViewTreeObserver)localObject).addOnGlobalLayoutListener(local2);
        setOnDismissListener(new PopupWindow.OnDismissListener()
        {
          public void onDismiss()
          {
            ViewTreeObserver localViewTreeObserver = getViewTreeObserver();
            if (localViewTreeObserver != null) {
              localViewTreeObserver.removeGlobalOnLayoutListener(local2);
            }
          }
        });
      }
    }
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public AppCompatSpinner.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new AppCompatSpinner.SavedState(paramAnonymousParcel);
      }
      
      public AppCompatSpinner.SavedState[] newArray(int paramAnonymousInt)
      {
        return new AppCompatSpinner.SavedState[paramAnonymousInt];
      }
    };
    boolean mShowDropdown;
    
    SavedState(Parcel paramParcel)
    {
      super();
      boolean bool;
      if (paramParcel.readByte() != 0) {
        bool = true;
      } else {
        bool = false;
      }
      mShowDropdown = bool;
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeByte((byte)mShowDropdown);
    }
  }
  
  @VisibleForTesting
  static abstract interface SpinnerPopup
  {
    public abstract void dismiss();
    
    public abstract Drawable getBackground();
    
    public abstract CharSequence getHintText();
    
    public abstract int getHorizontalOffset();
    
    public abstract int getHorizontalOriginalOffset();
    
    public abstract int getVerticalOffset();
    
    public abstract boolean isShowing();
    
    public abstract void setAdapter(ListAdapter paramListAdapter);
    
    public abstract void setBackgroundDrawable(Drawable paramDrawable);
    
    public abstract void setHorizontalOffset(int paramInt);
    
    public abstract void setHorizontalOriginalOffset(int paramInt);
    
    public abstract void setPromptText(CharSequence paramCharSequence);
    
    public abstract void setVerticalOffset(int paramInt);
    
    public abstract void show(int paramInt1, int paramInt2);
  }
}
