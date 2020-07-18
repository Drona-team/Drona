package androidx.fragment.package_5;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater.Factory2;
import android.view.View;
import androidx.fragment.R.styleable;

class FragmentLayoutInflaterFactory
  implements LayoutInflater.Factory2
{
  private static final String TAG = "FragmentManager";
  private final FragmentManager mFragmentManager;
  
  FragmentLayoutInflaterFactory(FragmentManager paramFragmentManager)
  {
    mFragmentManager = paramFragmentManager;
  }
  
  public View onCreateView(View paramView, String paramString, Context paramContext, AttributeSet paramAttributeSet)
  {
    if (androidx.fragment.app.FragmentContainerView.class.getName().equals(paramString)) {
      return new FragmentContainerView(paramContext, paramAttributeSet, mFragmentManager);
    }
    boolean bool = "fragment".equals(paramString);
    paramString = null;
    if (!bool) {
      return null;
    }
    String str2 = paramAttributeSet.getAttributeValue(null, "class");
    String str1 = str2;
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.Fragment);
    if (str2 == null) {
      str1 = localTypedArray.getString(R.styleable.Fragment_android_name);
    }
    int k = localTypedArray.getResourceId(R.styleable.Fragment_android_id, -1);
    str2 = localTypedArray.getString(R.styleable.Fragment_android_tag);
    localTypedArray.recycle();
    if (str1 != null)
    {
      if (!FragmentFactory.isFragmentClass(paramContext.getClassLoader(), str1)) {
        return null;
      }
      int i;
      if (paramView != null) {
        i = paramView.getId();
      } else {
        i = 0;
      }
      if ((i == -1) && (k == -1) && (str2 == null))
      {
        paramView = new StringBuilder();
        paramView.append(paramAttributeSet.getPositionDescription());
        paramView.append(": Must specify unique android:id, android:tag, or have a parent with an id for ");
        paramView.append(str1);
        throw new IllegalArgumentException(paramView.toString());
      }
      if (k != -1) {
        paramString = mFragmentManager.findFragmentById(k);
      }
      paramView = paramString;
      if (paramString == null)
      {
        paramView = paramString;
        if (str2 != null) {
          paramView = mFragmentManager.findFragmentByTag(str2);
        }
      }
      paramString = paramView;
      if (paramView == null)
      {
        paramString = paramView;
        if (i != -1) {
          paramString = mFragmentManager.findFragmentById(i);
        }
      }
      if (FragmentManager.isLoggingEnabled(2))
      {
        paramView = new StringBuilder();
        paramView.append("onCreateView: id=0x");
        paramView.append(Integer.toHexString(k));
        paramView.append(" fname=");
        paramView.append(str1);
        paramView.append(" existing=");
        paramView.append(paramString);
        Log.v("FragmentManager", paramView.toString());
      }
      if (paramString == null)
      {
        paramString = mFragmentManager.getFragmentFactory().instantiate(paramContext.getClassLoader(), str1);
        paramView = paramString;
        mFromLayout = true;
        int j;
        if (k != 0) {
          j = k;
        } else {
          j = i;
        }
        mFragmentId = j;
        mContainerId = i;
        mTag = str2;
        mInLayout = true;
        mFragmentManager = mFragmentManager;
        mHost = mFragmentManager.mHost;
        paramString.onInflate(mFragmentManager.mHost.getContext(), paramAttributeSet, mSavedFragmentState);
        mFragmentManager.addFragment(paramString);
        mFragmentManager.moveToState(paramString);
      }
      else
      {
        if (mInLayout) {
          break label639;
        }
        mInLayout = true;
        mHost = mFragmentManager.mHost;
        paramString.onInflate(mFragmentManager.mHost.getContext(), paramAttributeSet, mSavedFragmentState);
        paramView = paramString;
      }
      if ((mFragmentManager.mCurState < 1) && (mFromLayout)) {
        mFragmentManager.moveToState(paramView, 1);
      } else {
        mFragmentManager.moveToState(paramView);
      }
      if (mView != null)
      {
        if (k != 0) {
          mView.setId(k);
        }
        if (mView.getTag() == null) {
          mView.setTag(str2);
        }
        return mView;
      }
      paramView = new StringBuilder();
      paramView.append("Fragment ");
      paramView.append(str1);
      paramView.append(" did not create a view.");
      throw new IllegalStateException(paramView.toString());
      label639:
      paramView = new StringBuilder();
      paramView.append(paramAttributeSet.getPositionDescription());
      paramView.append(": Duplicate id 0x");
      paramView.append(Integer.toHexString(k));
      paramView.append(", tag ");
      paramView.append(str2);
      paramView.append(", or parent id 0x");
      paramView.append(Integer.toHexString(i));
      paramView.append(" with another fragment for ");
      paramView.append(str1);
      throw new IllegalArgumentException(paramView.toString());
    }
    return null;
  }
  
  public View onCreateView(String paramString, Context paramContext, AttributeSet paramAttributeSet)
  {
    return onCreateView(null, paramString, paramContext, paramAttributeSet);
  }
}
