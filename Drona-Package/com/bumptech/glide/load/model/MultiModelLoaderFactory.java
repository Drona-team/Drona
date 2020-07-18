package com.bumptech.glide.load.model;

import androidx.core.util.Pools.Pool;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.util.Preconditions;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MultiModelLoaderFactory
{
  private static final Factory DEFAULT_FACTORY = new Factory();
  private static final ModelLoader<Object, Object> EMPTY_MODEL_LOADER = new EmptyModelLoader();
  private final Set<Entry<?, ?>> alreadyUsedEntries = new HashSet();
  private final List<Entry<?, ?>> entries = new ArrayList();
  private final Factory factory;
  private final Pools.Pool<List<Throwable>> throwableListPool;
  
  public MultiModelLoaderFactory(Pools.Pool paramPool)
  {
    this(paramPool, DEFAULT_FACTORY);
  }
  
  MultiModelLoaderFactory(Pools.Pool paramPool, Factory paramFactory)
  {
    throwableListPool = paramPool;
    factory = paramFactory;
  }
  
  private void add(Class paramClass1, Class paramClass2, ModelLoaderFactory paramModelLoaderFactory, boolean paramBoolean)
  {
    paramClass1 = new Entry(paramClass1, paramClass2, paramModelLoaderFactory);
    paramClass2 = entries;
    int i;
    if (paramBoolean) {
      i = entries.size();
    } else {
      i = 0;
    }
    paramClass2.add(i, paramClass1);
  }
  
  private ModelLoader build(Entry paramEntry)
  {
    return (ModelLoader)Preconditions.checkNotNull(factory.build(this));
  }
  
  private static ModelLoader emptyModelLoader()
  {
    return EMPTY_MODEL_LOADER;
  }
  
  private ModelLoaderFactory getFactory(Entry paramEntry)
  {
    return factory;
  }
  
  void append(Class paramClass1, Class paramClass2, ModelLoaderFactory paramModelLoaderFactory)
  {
    try
    {
      add(paramClass1, paramClass2, paramModelLoaderFactory, true);
      return;
    }
    catch (Throwable paramClass1)
    {
      throw paramClass1;
    }
  }
  
  /* Error */
  public ModelLoader build(Class paramClass1, Class paramClass2)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: new 46	java/util/ArrayList
    //   5: dup
    //   6: invokespecial 47	java/util/ArrayList:<init>	()V
    //   9: astore 4
    //   11: aload_0
    //   12: getfield 49	com/bumptech/glide/load/model/MultiModelLoaderFactory:entries	Ljava/util/List;
    //   15: invokeinterface 104 1 0
    //   20: astore 5
    //   22: iconst_0
    //   23: istore_3
    //   24: aload 5
    //   26: invokeinterface 110 1 0
    //   31: ifeq +85 -> 116
    //   34: aload 5
    //   36: invokeinterface 114 1 0
    //   41: checkcast 9	com/bumptech/glide/load/model/MultiModelLoaderFactory$Entry
    //   44: astore 6
    //   46: aload_0
    //   47: getfield 54	com/bumptech/glide/load/model/MultiModelLoaderFactory:alreadyUsedEntries	Ljava/util/Set;
    //   50: aload 6
    //   52: invokeinterface 120 2 0
    //   57: ifeq +8 -> 65
    //   60: iconst_1
    //   61: istore_3
    //   62: goto -38 -> 24
    //   65: aload 6
    //   67: aload_1
    //   68: aload_2
    //   69: invokevirtual 124	com/bumptech/glide/load/model/MultiModelLoaderFactory$Entry:handles	(Ljava/lang/Class;Ljava/lang/Class;)Z
    //   72: ifeq -48 -> 24
    //   75: aload_0
    //   76: getfield 54	com/bumptech/glide/load/model/MultiModelLoaderFactory:alreadyUsedEntries	Ljava/util/Set;
    //   79: aload 6
    //   81: invokeinterface 126 2 0
    //   86: pop
    //   87: aload 4
    //   89: aload_0
    //   90: aload 6
    //   92: invokespecial 128	com/bumptech/glide/load/model/MultiModelLoaderFactory:build	(Lcom/bumptech/glide/load/model/MultiModelLoaderFactory$Entry;)Lcom/bumptech/glide/load/model/ModelLoader;
    //   95: invokeinterface 129 2 0
    //   100: pop
    //   101: aload_0
    //   102: getfield 54	com/bumptech/glide/load/model/MultiModelLoaderFactory:alreadyUsedEntries	Ljava/util/Set;
    //   105: aload 6
    //   107: invokeinterface 132 2 0
    //   112: pop
    //   113: goto -89 -> 24
    //   116: aload 4
    //   118: invokeinterface 69 1 0
    //   123: iconst_1
    //   124: if_icmple +21 -> 145
    //   127: aload_0
    //   128: getfield 58	com/bumptech/glide/load/model/MultiModelLoaderFactory:factory	Lcom/bumptech/glide/load/model/MultiModelLoaderFactory$Factory;
    //   131: aload 4
    //   133: aload_0
    //   134: getfield 56	com/bumptech/glide/load/model/MultiModelLoaderFactory:throwableListPool	Landroidx/core/util/Pools$Pool;
    //   137: invokevirtual 135	com/bumptech/glide/load/model/MultiModelLoaderFactory$Factory:build	(Ljava/util/List;Landroidx/core/util/Pools$Pool;)Lcom/bumptech/glide/load/model/MultiModelLoader;
    //   140: astore_1
    //   141: aload_0
    //   142: monitorexit
    //   143: aload_1
    //   144: areturn
    //   145: aload 4
    //   147: invokeinterface 69 1 0
    //   152: iconst_1
    //   153: if_icmpne +19 -> 172
    //   156: aload 4
    //   158: iconst_0
    //   159: invokeinterface 139 2 0
    //   164: checkcast 90	com/bumptech/glide/load/model/ModelLoader
    //   167: astore_1
    //   168: aload_0
    //   169: monitorexit
    //   170: aload_1
    //   171: areturn
    //   172: iload_3
    //   173: ifeq +11 -> 184
    //   176: invokestatic 141	com/bumptech/glide/load/model/MultiModelLoaderFactory:emptyModelLoader	()Lcom/bumptech/glide/load/model/ModelLoader;
    //   179: astore_1
    //   180: aload_0
    //   181: monitorexit
    //   182: aload_1
    //   183: areturn
    //   184: new 143	com/bumptech/glide/Registry$NoModelLoaderAvailableException
    //   187: dup
    //   188: aload_1
    //   189: aload_2
    //   190: invokespecial 146	com/bumptech/glide/Registry$NoModelLoaderAvailableException:<init>	(Ljava/lang/Class;Ljava/lang/Class;)V
    //   193: athrow
    //   194: astore_1
    //   195: goto +15 -> 210
    //   198: astore_1
    //   199: aload_0
    //   200: getfield 54	com/bumptech/glide/load/model/MultiModelLoaderFactory:alreadyUsedEntries	Ljava/util/Set;
    //   203: invokeinterface 149 1 0
    //   208: aload_1
    //   209: athrow
    //   210: aload_0
    //   211: monitorexit
    //   212: aload_1
    //   213: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	214	0	this	MultiModelLoaderFactory
    //   0	214	1	paramClass1	Class
    //   0	214	2	paramClass2	Class
    //   23	150	3	i	int
    //   9	148	4	localArrayList	ArrayList
    //   20	15	5	localIterator	Iterator
    //   44	62	6	localEntry	Entry
    // Exception table:
    //   from	to	target	type
    //   199	210	194	java/lang/Throwable
    //   2	22	198	java/lang/Throwable
    //   24	60	198	java/lang/Throwable
    //   65	113	198	java/lang/Throwable
    //   116	141	198	java/lang/Throwable
    //   145	168	198	java/lang/Throwable
    //   176	180	198	java/lang/Throwable
    //   184	194	198	java/lang/Throwable
  }
  
  /* Error */
  List build(Class paramClass)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: new 46	java/util/ArrayList
    //   5: dup
    //   6: invokespecial 47	java/util/ArrayList:<init>	()V
    //   9: astore_2
    //   10: aload_0
    //   11: getfield 49	com/bumptech/glide/load/model/MultiModelLoaderFactory:entries	Ljava/util/List;
    //   14: invokeinterface 104 1 0
    //   19: astore_3
    //   20: aload_3
    //   21: invokeinterface 110 1 0
    //   26: ifeq +80 -> 106
    //   29: aload_3
    //   30: invokeinterface 114 1 0
    //   35: checkcast 9	com/bumptech/glide/load/model/MultiModelLoaderFactory$Entry
    //   38: astore 4
    //   40: aload_0
    //   41: getfield 54	com/bumptech/glide/load/model/MultiModelLoaderFactory:alreadyUsedEntries	Ljava/util/Set;
    //   44: aload 4
    //   46: invokeinterface 120 2 0
    //   51: ifeq +6 -> 57
    //   54: goto -34 -> 20
    //   57: aload 4
    //   59: aload_1
    //   60: invokevirtual 153	com/bumptech/glide/load/model/MultiModelLoaderFactory$Entry:handles	(Ljava/lang/Class;)Z
    //   63: ifeq -43 -> 20
    //   66: aload_0
    //   67: getfield 54	com/bumptech/glide/load/model/MultiModelLoaderFactory:alreadyUsedEntries	Ljava/util/Set;
    //   70: aload 4
    //   72: invokeinterface 126 2 0
    //   77: pop
    //   78: aload_2
    //   79: aload_0
    //   80: aload 4
    //   82: invokespecial 128	com/bumptech/glide/load/model/MultiModelLoaderFactory:build	(Lcom/bumptech/glide/load/model/MultiModelLoaderFactory$Entry;)Lcom/bumptech/glide/load/model/ModelLoader;
    //   85: invokeinterface 129 2 0
    //   90: pop
    //   91: aload_0
    //   92: getfield 54	com/bumptech/glide/load/model/MultiModelLoaderFactory:alreadyUsedEntries	Ljava/util/Set;
    //   95: aload 4
    //   97: invokeinterface 132 2 0
    //   102: pop
    //   103: goto -83 -> 20
    //   106: aload_0
    //   107: monitorexit
    //   108: aload_2
    //   109: areturn
    //   110: astore_1
    //   111: goto +15 -> 126
    //   114: astore_1
    //   115: aload_0
    //   116: getfield 54	com/bumptech/glide/load/model/MultiModelLoaderFactory:alreadyUsedEntries	Ljava/util/Set;
    //   119: invokeinterface 149 1 0
    //   124: aload_1
    //   125: athrow
    //   126: aload_0
    //   127: monitorexit
    //   128: aload_1
    //   129: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	130	0	this	MultiModelLoaderFactory
    //   0	130	1	paramClass	Class
    //   9	100	2	localArrayList	ArrayList
    //   19	11	3	localIterator	Iterator
    //   38	58	4	localEntry	Entry
    // Exception table:
    //   from	to	target	type
    //   115	126	110	java/lang/Throwable
    //   2	20	114	java/lang/Throwable
    //   20	54	114	java/lang/Throwable
    //   57	103	114	java/lang/Throwable
  }
  
  List getDataClasses(Class paramClass)
  {
    try
    {
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = entries.iterator();
      while (localIterator.hasNext())
      {
        Entry localEntry = (Entry)localIterator.next();
        if ((!localArrayList.contains(dataClass)) && (localEntry.handles(paramClass))) {
          localArrayList.add(dataClass);
        }
      }
      return localArrayList;
    }
    catch (Throwable paramClass)
    {
      throw paramClass;
    }
  }
  
  void prepend(Class paramClass1, Class paramClass2, ModelLoaderFactory paramModelLoaderFactory)
  {
    try
    {
      add(paramClass1, paramClass2, paramModelLoaderFactory, false);
      return;
    }
    catch (Throwable paramClass1)
    {
      throw paramClass1;
    }
  }
  
  List remove(Class paramClass1, Class paramClass2)
  {
    try
    {
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = entries.iterator();
      while (localIterator.hasNext())
      {
        Entry localEntry = (Entry)localIterator.next();
        if (localEntry.handles(paramClass1, paramClass2))
        {
          localIterator.remove();
          localArrayList.add(getFactory(localEntry));
        }
      }
      return localArrayList;
    }
    catch (Throwable paramClass1)
    {
      throw paramClass1;
    }
  }
  
  List replace(Class paramClass1, Class paramClass2, ModelLoaderFactory paramModelLoaderFactory)
  {
    try
    {
      List localList = remove(paramClass1, paramClass2);
      append(paramClass1, paramClass2, paramModelLoaderFactory);
      return localList;
    }
    catch (Throwable paramClass1)
    {
      throw paramClass1;
    }
  }
  
  private static class EmptyModelLoader
    implements ModelLoader<Object, Object>
  {
    EmptyModelLoader() {}
    
    public ModelLoader.LoadData buildLoadData(Object paramObject, int paramInt1, int paramInt2, Options paramOptions)
    {
      return null;
    }
    
    public boolean handles(Object paramObject)
    {
      return false;
    }
  }
  
  private static class Entry<Model, Data>
  {
    final Class<Data> dataClass;
    final ModelLoaderFactory<? extends Model, ? extends Data> factory;
    private final Class<Model> modelClass;
    
    public Entry(Class paramClass1, Class paramClass2, ModelLoaderFactory paramModelLoaderFactory)
    {
      modelClass = paramClass1;
      dataClass = paramClass2;
      factory = paramModelLoaderFactory;
    }
    
    public boolean handles(Class paramClass)
    {
      return modelClass.isAssignableFrom(paramClass);
    }
    
    public boolean handles(Class paramClass1, Class paramClass2)
    {
      return (handles(paramClass1)) && (dataClass.isAssignableFrom(paramClass2));
    }
  }
  
  static class Factory
  {
    Factory() {}
    
    public MultiModelLoader build(List paramList, Pools.Pool paramPool)
    {
      return new MultiModelLoader(paramList, paramPool);
    }
  }
}
