package mobi.android.adlibrary.internal.ad.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by vincent on 16/5/4.
 */
public class AdNode implements Serializable {
    @SerializedName("slot_id")
    public String slot_id;
    @SerializedName("slot_name")
    public String slot_name;
    @SerializedName("open_status")
    public Boolean open_status;
    @SerializedName("show_priority")
    public int show_priority;//1:cache ,caches--flows;   2:flow , cache-flow-cache-flow
    @SerializedName("cache_size")
    public int cache_size;
    @SerializedName("frequency")
    public float frequency;
    @SerializedName("daily_times")
    public float daily_times;
    @SerializedName("flow")
    public ArrayList<ArrayList<Flow>> flow;
    @SerializedName("height")
    public int height;
    @SerializedName("width")
    public int width;

    @SerializedName("mTilteColor")
    public int mTilteColor;
    @SerializedName("mSubTitleColor")
    public int mSubTitleColor;
    @SerializedName("mButtonColor")
    public int mButtonColor;
    @SerializedName("transparent")
    public boolean transparent;
    @SerializedName("ctaBackground")
    public int ctaBackground;

}
