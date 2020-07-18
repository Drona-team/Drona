package androidx.room;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.Callable;

class InvalidationLiveDataContainer
{
  private final RoomDatabase mDatabase;
  @VisibleForTesting
  final Set<LiveData> mLiveDataSet = Collections.newSetFromMap(new IdentityHashMap());
  
  InvalidationLiveDataContainer(RoomDatabase paramRoomDatabase)
  {
    mDatabase = paramRoomDatabase;
  }
  
  LiveData create(String[] paramArrayOfString, boolean paramBoolean, Callable paramCallable)
  {
    return new RoomTrackingLiveData(mDatabase, this, paramBoolean, paramCallable, paramArrayOfString);
  }
  
  void onActive(LiveData paramLiveData)
  {
    mLiveDataSet.add(paramLiveData);
  }
  
  void onInactive(LiveData paramLiveData)
  {
    mLiveDataSet.remove(paramLiveData);
  }
}
