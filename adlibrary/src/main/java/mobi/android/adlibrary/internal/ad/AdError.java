package mobi.android.adlibrary.internal.ad;

/**
 * Created by vincent on 2016/8/16.
 */
public class AdError {
    public String adError;
    public String slotid;
    public String extraMessage;
    public AdError(){

    }
    public AdError(String slotid,String adError){
        this.adError = adError;
        this.slotid = slotid;
    }
    public AdError(String slotid,String adError,String extraMessage){
        this.adError = adError;
        this.slotid = slotid;
        this.extraMessage = extraMessage;
    }

    public String toString(){
        return adError;
    }
}
