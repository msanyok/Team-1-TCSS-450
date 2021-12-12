/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.io;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.collection.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * A Request queue class for http requests.
 *
 * @author Charles Bryan
 * @author Austn Attaway (added documentation)
 * @version Fall 2021
 */
public class RequestQueueSingleton {

    /** The single instance of the RequestQueueSingleton */
    private static RequestQueueSingleton mInstance;

    /** The context of this RequestQueueSingleton */
    private static Context mContext;

    /** The Request Queue */
    private RequestQueue mRequestQueue;

    /** The Image Loader */
    private ImageLoader mImageLoader;

    /**
     * Creates a new RequestQueueSingleton with default values.
     * @param theContext the context of this RequestQueueSingleton
     */
    private RequestQueueSingleton(final Context theContext) {
        RequestQueueSingleton.mContext = theContext;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(final String theUrl) {
                        return cache.get(theUrl);
                    }

                    @Override
                    public void putBitmap(final String theUrl, final Bitmap theBitmap) {
                        cache.put(theUrl, theBitmap);
                    }
                });
    }

    /**
     * Returns this RequestQueueSingleton
     *
     * @param theContext the context for this RequestQueueSingleton
     * @return this RequestQueueSingleton
     */
    public static synchronized RequestQueueSingleton getInstance(final Context theContext) {
        if (mInstance == null) {
            mInstance = new RequestQueueSingleton(theContext);
        }
        return mInstance;
    }

    /**
     * Returns the request queue.
     * @return the request queue.
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Adds the given request to this request queue.
     * @param theRequest the request
     * @param <T> the type of the request
     */
    public <T> void addToRequestQueue(final Request<T> theRequest) {
        getRequestQueue().add(theRequest);
    }
}