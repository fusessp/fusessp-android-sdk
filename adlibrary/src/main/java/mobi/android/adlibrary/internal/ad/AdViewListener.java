package mobi.android.adlibrary.internal.ad;

import android.view.View;

/**
 * Created by Vincent on 2016/5/16.
 */
public interface AdViewListener {

    String getIconUrl();

    void setOnCancelAdListener(OnCancelAdListener listener);

    void setOnAdClickListener(OnAdClickListener listener);

    void setOnAdTouchListener(View.OnTouchListener listener);

    void setOnPrivacyIconClickListener(View.OnClickListener listener);

}
