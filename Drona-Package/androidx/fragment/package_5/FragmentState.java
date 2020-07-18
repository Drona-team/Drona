package androidx.fragment.package_5;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

@SuppressLint({"BanParcelableUsage"})
final class FragmentState
  implements Parcelable
{
  public static final Parcelable.Creator<androidx.fragment.app.FragmentState> CREATOR = new Parcelable.Creator()
  {
    public FragmentState createFromParcel(Parcel paramAnonymousParcel)
    {
      return new FragmentState(paramAnonymousParcel);
    }
    
    public FragmentState[] newArray(int paramAnonymousInt)
    {
      return new FragmentState[paramAnonymousInt];
    }
  };
  final Bundle mArguments;
  final String mClassName;
  final int mContainerId;
  final boolean mDetached;
  final int mFragmentId;
  final boolean mFromLayout;
  final boolean mHidden;
  final int mMaxLifecycleState;
  final boolean mRemoving;
  final boolean mRetainInstance;
  Bundle mSavedFragmentState;
  final String mTag;
  final String mWho;
  
  FragmentState(Parcel paramParcel)
  {
    mClassName = paramParcel.readString();
    mWho = paramParcel.readString();
    int i = paramParcel.readInt();
    boolean bool2 = false;
    if (i != 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    mFromLayout = bool1;
    mFragmentId = paramParcel.readInt();
    mContainerId = paramParcel.readInt();
    mTag = paramParcel.readString();
    if (paramParcel.readInt() != 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    mRetainInstance = bool1;
    if (paramParcel.readInt() != 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    mRemoving = bool1;
    if (paramParcel.readInt() != 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    mDetached = bool1;
    mArguments = paramParcel.readBundle();
    boolean bool1 = bool2;
    if (paramParcel.readInt() != 0) {
      bool1 = true;
    }
    mHidden = bool1;
    mSavedFragmentState = paramParcel.readBundle();
    mMaxLifecycleState = paramParcel.readInt();
  }
  
  FragmentState(Fragment paramFragment)
  {
    mClassName = paramFragment.getClass().getName();
    mWho = mWho;
    mFromLayout = mFromLayout;
    mFragmentId = mFragmentId;
    mContainerId = mContainerId;
    mTag = mTag;
    mRetainInstance = mRetainInstance;
    mRemoving = mRemoving;
    mDetached = mDetached;
    mArguments = mArguments;
    mHidden = mHidden;
    mMaxLifecycleState = mMaxState.ordinal();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("FragmentState{");
    localStringBuilder.append(mClassName);
    localStringBuilder.append(" (");
    localStringBuilder.append(mWho);
    localStringBuilder.append(")}:");
    if (mFromLayout) {
      localStringBuilder.append(" fromLayout");
    }
    if (mContainerId != 0)
    {
      localStringBuilder.append(" id=0x");
      localStringBuilder.append(Integer.toHexString(mContainerId));
    }
    if ((mTag != null) && (!mTag.isEmpty()))
    {
      localStringBuilder.append(" tag=");
      localStringBuilder.append(mTag);
    }
    if (mRetainInstance) {
      localStringBuilder.append(" retainInstance");
    }
    if (mRemoving) {
      localStringBuilder.append(" removing");
    }
    if (mDetached) {
      localStringBuilder.append(" detached");
    }
    if (mHidden) {
      localStringBuilder.append(" hidden");
    }
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e1expr(TypeTransformer.java:496)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:713)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:698)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
  }
}
