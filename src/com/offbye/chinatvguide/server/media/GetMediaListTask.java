
package com.offbye.chinatvguide.server.media;

import android.content.Context;
import android.os.AsyncTask;

public class GetMediaListTask extends AsyncTask<Void, Void, Boolean> {

    private Context mContext;

    public GetMediaListTask(Context context) {
        mContext = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return MediaStore.getInstance().requestMediaList(mContext);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
    }
}
