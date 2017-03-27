package mobi.android.adlibrary.internal.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import mobi.android.adlibrary.internal.ad.NativeAdData;
import mobi.android.adlibrary.internal.ad.bean.AdNode;
import mobi.android.adlibrary.internal.app.AdConstants;
import mobi.android.adlibrary.internal.utils.MyLog;

/**
 * Created by vincent on 16/5/11.
 */
public class CacheManager {

    public static final int FB = 0;
    public static final int ADMOB = 1;
    public static final int OTHER = 2;
    public static final int ADMOB_APP = 3;
    public static final int ADMOB_CONTENT = 4;
    public static final int MOPUB = 5;
    public static final int MOPUB_BANNER = 6;

    public static final int YH = 7;
    private static volatile CacheManager instance;
    private HashMap<String,  HashSet<NativeAdData>[]> mAdCaches;

    private CacheManager() {
        mAdCaches = new HashMap<>();
        MyLog.d(MyLog.TAG, "初始化CacheManager");
    }

    public static CacheManager getInstance() {
        if (instance == null) {
            synchronized (CacheManager.class) {
                if (instance == null) {
                    instance = new CacheManager();
                }
            }
        }
        return instance;
    }

    public NativeAdData getNativeCache(AdNode adNode) {
        if (adNode == null) {
            return null;
        }
        sortTheCache(adNode.slot_id, adNode.cache_size);

        HashSet[] flowArray = mAdCaches.get(adNode.slot_id);
        if (flowArray == null || flowArray != null && flowArray.length == 0) {
            return null;
        }
        int length = flowArray.length;
        for (int i = 0; i < length; i++) {
            HashSet<NativeAdData> datas = (HashSet) flowArray[i];
            if (datas == null) {
                continue;
            }
            Iterator<NativeAdData> iterator = datas.iterator();
            if(iterator.hasNext()){
                return  iterator.next();
            }

        }

        return null;
    }

    public boolean isHaveNativeCache(AdNode adNode) {
        if (adNode == null) {
            return false;
        }
        boolean isHasCache = false;
        sortTheCache(adNode.slot_id, adNode.cache_size);
        HashSet[] flowArray = mAdCaches.get(adNode.slot_id);
        if (flowArray == null || flowArray != null && flowArray.length == 0) {
            return false;
        }
        int length = flowArray.length;
        int totalCacheCount = 0;
        for (int i = 0; i < length; i++) {
            HashSet<NativeAdData> datas = (HashSet) flowArray[i];
            if (datas == null) {
                continue;
            }
            totalCacheCount += datas.size();
            if (totalCacheCount > 0) {
                isHasCache = true;
                break;
            }
        }

        return isHasCache;
    }

    /**
     * ad can get flow arry from mAdCache by slotId.
     * flow array,every flow contains few NativeAdDatas,total size is not more than cahcesize.
     * <p/>
     * we take the flow by index ,after that,wo can get the native datas from list in first location;
     * before wo get the flow cache ,we must clean an sort the flow.
     *
     * @param adNode
     * @param index
     * @return
     */
    public NativeAdData getCacheInFirstIndex(AdNode adNode, int index) {
        HashSet<NativeAdData> flowDataList = getCacheDataByIndex(adNode,index);

        NativeAdData nativeAdData = null;
        if (flowDataList != null && flowDataList.size() > 0) {
            nativeAdData = flowDataList.iterator().next();
        }
        return nativeAdData;
    }

    public HashSet<NativeAdData> getCacheDataByIndex(AdNode adNode, int index){
        HashSet[] flowArray = mAdCaches.get(adNode.slot_id);
        if (flowArray == null || flowArray != null && flowArray.length == 0) {
            MyLog.d(MyLog.TAG, AdConstants.CACHE_LOG +"没有获取任何缓存getCacheInFirstIndex"+ adNode.slot_id);
            return null;
        }
        if (flowArray.length > 0 && flowArray.length < index) {
            MyLog.d(MyLog.TAG, AdConstants.CACHE_LOG +"没有获取任何缓存InFirstIndex"+ adNode.slot_id);
            return null;
        }
        if (flowArray[index] == null) {
            MyLog.d(MyLog.TAG, AdConstants.CACHE_LOG +"没有获取任何缓存flowArray[index] == null"+ adNode.slot_id);
            return null;
        }
        sortTheCache(adNode.slot_id, adNode.cache_size);

        HashSet<NativeAdData> flowDataList = (HashSet<NativeAdData>) flowArray[index];

        return flowDataList;
    }

    public boolean isCacheLevelFull(AdNode adNode, int index){
        HashSet<NativeAdData> flowDataList = getCacheDataByIndex(adNode,index);
        if(flowDataList == null){
            return false;
        }
        return flowDataList.size() >= adNode.cache_size;
    }

    /**
     * get the lowest flow level from caches
     *
     * @return
     */
    public int getTheLowestFlowLevel(String slotId) {
        int level = -1;
        HashSet[] flowArray = mAdCaches.get(slotId);
        if (flowArray == null) {
            MyLog.d(MyLog.TAG,"获取当前的层级："+level+"slotId"+slotId);
            return level;
        }
        int flowSizes = flowArray.length;
        if (flowSizes == 0) {
            MyLog.d(MyLog.TAG,"获取当前的层级："+level+"slotId"+slotId);
            return level;
        }

        for (int i = 0; i < flowSizes; i++) {
            HashSet<NativeAdData> flowDatas = (HashSet<NativeAdData>) flowArray[flowSizes - 1-i];
            if (flowDatas != null && flowDatas.size() > 0) {
                return flowSizes - 1-i;
            }
        }
        MyLog.d(MyLog.TAG,"获取当前的层级："+level+"slotId"+slotId);
        return level;
    }

    /**
     * judge ad cache is full or not
     *
     * @param adNode
     * @return
     */
    public boolean adCacheIsFull(AdNode adNode) {
        if (adNode == null) {
            MyLog.d(MyLog.TAG, AdConstants.CACHE_LOG +"node.slot_id"+ adNode.slot_id+ "node == null-->return");
            return false;
        }
        boolean isFullCache = false;
        sortTheCache(adNode.slot_id, adNode.cache_size);
        HashSet[] flowArray = mAdCaches.get(adNode.slot_id);
        if (flowArray == null || flowArray != null && flowArray.length == 0) {
            MyLog.d(MyLog.TAG, AdConstants.CACHE_LOG +"没有获取任何缓存"+ adNode.slot_id);
            return false;
        }
        int length = flowArray.length;
        int totalCacheCount = 0;
        for (int i = 0; i < length; i++) {
            HashSet<NativeAdData> datas = (HashSet) flowArray[i];
            if (datas == null) {
                continue;
            }
            int size = datas.size();
            MyLog.d(MyLog.TAG, AdConstants.CACHE_LOG +"node.slot_id"+ adNode.slot_id+ "当前flow中的缓存个数。"+size+"level:"+i);
            totalCacheCount += size;
        }

        if (totalCacheCount >= adNode.cache_size) {
            isFullCache = true;
            MyLog.d(MyLog.TAG, AdConstants.CACHE_LOG +"node.slot_id"+ adNode.slot_id+ "当前缓存已满");
        }else{
            MyLog.d(MyLog.TAG, AdConstants.CACHE_LOG +"node.slot_id"+ adNode.slot_id+ "当前缓存个数为："+totalCacheCount);
        }

        return isFullCache;
    }

    /**
     * save cache into flow by slotId and index
     *
     * @param slotId
     * @param index
     * @param nativeAdData
     */
    public void saveCache(String slotId, int index, NativeAdData nativeAdData) {
        MyLog.e(MyLog.TAG, "调用SaveData,nodeid"+slotId);
        if (index >= 20) {
            MyLog.e(MyLog.TAG, "当前广告层级过深，请处理--20层标准");
            return;
        }
        if(nativeAdData == null){
            MyLog.e(MyLog.TAG, "保存的数据为空"+"node.slot_id"+ slotId);
            return;
        }
        HashSet[] flowArray = mAdCaches.get(slotId);
        HashSet<NativeAdData> flowLists = null;
        if (flowArray == null) {
            flowArray = new HashSet[AdConstants.MAX_FLOW_NUM];
            mAdCaches.put(slotId, flowArray);
            flowLists = new HashSet<>();
            flowArray[index] = flowLists;
        } else {
            if (flowArray[index] == null) {
                flowLists = new HashSet<>();
                flowArray[index] = flowLists;
            } else {
                flowLists = (HashSet<NativeAdData>) flowArray[index];
            }
        }
        flowLists.add(nativeAdData);
        if(flowLists.size()>1){
            MyLog.d(MyLog.TAG, "保存的数据个数为:"+flowLists.size()+"node.slot_id"+ slotId);
        }

        return;
    }

    /**
     * 将当前广告位数据晴空
     */
    public void clearSlotCache(String slotId) {
        if(mAdCaches!=null){
            mAdCaches.remove(slotId);
        }
    }
    /**
     * 将当前广告位的所有FLOW缓存取出,先剔除过期的或者已经展示过的
     */
    public void sortTheCache(String slotId, int cacheSize) {
        HashSet[] flowArray = mAdCaches.get(slotId);

        if (flowArray == null || flowArray != null && flowArray.length == 0) {
            MyLog.d(MyLog.TAG, "要清理的缓存池为空:"+"node.slot_id"+ slotId);
            return;
        }
        int flowArrayLength = flowArray.length;
        int allCacheCount = 0;
        for (int i = 0; i < flowArrayLength; i++) {
            if (flowArray[i] == null) {
                continue;
            }
            HashSet<NativeAdData> flowNativeDatas = (HashSet<NativeAdData>) flowArray[i];
            flowNativeDatas = deleteUnqualifiedAd(flowNativeDatas,slotId);
            if (flowNativeDatas != null) {
                allCacheCount += flowNativeDatas.size();
            }
        }
        MyLog.d(MyLog.TAG, AdConstants.CACHE_LOG + "当前广告缓存的个数为：" + allCacheCount+"  node.slot_id"+ slotId);
        //if fill count is low fixed ,just off Unqualified ad cache(shown)
        if (allCacheCount <= cacheSize) {
            MyLog.d(MyLog.TAG, AdConstants.CACHE_LOG + "当前广告缓存的个数小于缓存池的大小"+"node.slot_id"+ slotId+"缓存池大小：   "+cacheSize);
            return;
        }
        //remove data from last flow ,last index
        for (int j = flowArrayLength - 1; j > 0; j--) {
            HashSet<NativeAdData> flowNativeAdDatas = (HashSet) flowArray[j];
            if (flowNativeAdDatas == null) {
                return;
            }
            Iterator<NativeAdData> iter = flowNativeAdDatas.iterator();
            while (iter.hasNext()) {
                iter.next();
                if (allCacheCount > cacheSize) {
                    iter.remove();
                    allCacheCount--;
                } else {
                    break;
                }
            }
        }
        MyLog.d(MyLog.TAG, AdConstants.CACHE_LOG + "剔除无效数据的广告缓存的个数：" + allCacheCount+"node.slot_id"+ slotId);
    }

    /**
     * remove unqualified ad from cache
     */
    public HashSet<NativeAdData> deleteUnqualifiedAd(HashSet<NativeAdData> flowNativeDatas,String slotId) {
        if (flowNativeDatas == null) {
            return null;
        }
        Iterator<NativeAdData> iter = flowNativeDatas.iterator();
        while (iter.hasNext()) {
            NativeAdData nativeAdData = iter.next();
            if (nativeAdData.getIsShowed() || nativeAdData.getNavtiveDataExpired()) {
                iter.remove();
                MyLog.d(MyLog.TAG, AdConstants.CACHE_LOG + "剔除无效数据"+"node.slot_id"+ slotId);
            }
        }
        return flowNativeDatas;
    }

}
