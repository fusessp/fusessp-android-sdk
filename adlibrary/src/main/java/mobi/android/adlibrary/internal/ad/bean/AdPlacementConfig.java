package mobi.android.adlibrary.internal.ad.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vincent on 16/5/4.
 */
public class AdPlacementConfig {
    @SerializedName("version")
    public String version;
    @SerializedName("version_desc")
    public String version_desc;
    @SerializedName("open_status")
    public boolean open_status;
    @SerializedName("refresh_frequence")
    public float refresh_frequence;
    @SerializedName("segment_id")
    public float segment_id;
    @SerializedName("yahoo_appkey")
    public String yahoo_appkey;
    @SerializedName("ADSlot_Config")
    public List<AdNode> ADSlot_Config;
    @SerializedName("SDK_Config")
    public SdkConfig SDK_Config;
    @SerializedName("InterAd_Config")
    public InterAd InterAd_Config;

    public class SdkConfig{
        @SerializedName("facebook_status")
        public boolean facebook_status;
        @SerializedName("admob_status")
        public boolean admob_status;
        @SerializedName("mopub_status")
        public boolean mopub_status;
        @SerializedName("facebook_lifetime")
        public long facebook_lifetime;
        @SerializedName("admob_lifetime")
        public long admob_lifetime;
        @SerializedName("mopub_lifetime")
        public long mopub_lifetime;
        @SerializedName("yahoo_lifetime")
        public long yahoo_lifetime;
        @SerializedName("vk_lifetime")
        public long vk_lifetime;
        @SerializedName("baidu_lifetime")
        public long baidu_lifetime;
    }

    public class InterAd{
        @SerializedName("enable")
        public boolean enable;
        @SerializedName("interval_time")
        public int interval_time;
        @SerializedName("max_show_time")
        public int max_show_time;
        @SerializedName("inter_flow")
        public ArrayList<InterFlow> inter_flow;

    }

    public class InterFlow{
        @SerializedName("inter_platform")
        public String inter_platform;
        @SerializedName("inter_wight")
        public int inter_wight;
        @SerializedName("inter_slotId")
        public String inter_slotId;
        @SerializedName("inter_slotName")
        public String inter_slotName;
    }

}
