package com.progscrape.data;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.okhttp.OkHttpSpiceRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SpiceRequestExecutor implements RequestExecutor {
    private SpiceManager spiceManager;

    @Inject
    public SpiceRequestExecutor(SpiceManager spiceManager) {
        this.spiceManager = spiceManager;
    }

    @Override
    public <T> void execute(OkHttpSpiceRequest<T> req, RequestListener<T> listener, boolean force) {
        if (req instanceof Request) {
            Request cacheable = (Request) req;
            if (force)
                spiceManager.execute(req, cacheable.getCacheKey(), DurationInMillis.ALWAYS_EXPIRED, listener);
            else
                spiceManager.execute(req, cacheable.getCacheKey(), cacheable.getCacheDuration(), listener);
        } else {
            spiceManager.execute(req, listener);
        }
    }
}
