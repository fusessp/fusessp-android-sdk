package mobi.android.adlibrary.internal.ad.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vincent on 2016/5/10.
 */
public class Flow {
    @SerializedName("platform")
    public String platform;//admob
    @SerializedName("weight")
    public int weight;//当前同级别的配置中的不同广告源（不同的平台或者id）展示的机会。
    @SerializedName("type")
    public String type;
    @SerializedName("admob_type")
    public int admob_type;
    @SerializedName("native_style")
    public int native_style;
    @SerializedName("mopub_type")
    public int mopub_type;
    @SerializedName("key")
    public String key;//xxxx_xxxx
    @SerializedName("ad_clcik_enable")
    public int ad_clcik_enable;//

}
