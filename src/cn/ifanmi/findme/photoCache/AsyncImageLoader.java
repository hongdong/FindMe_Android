package cn.ifanmi.findme.photoCache;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

public class AsyncImageLoader {

	private HashMap<String, SoftReference<Drawable>> imageCache;
    
    public AsyncImageLoader() {
        imageCache = new HashMap<String, SoftReference<Drawable>>();
    }

	@SuppressLint("HandlerLeak")
	public Drawable loadDrawable(final String imageUrl, final ImageCallback imageCallback) {
    	Drawable drawable = getFromCache(imageUrl);
        if (drawable != null) {
        	return drawable;
        }
        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
            }
        };
        new Thread() {
            public void run() {
                Drawable drawable = loadImageFromUrl(imageUrl);
                imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
                Message message = handler.obtainMessage(0, drawable);
                handler.sendMessage(message);
            }
        }.start();
        return null;
    }
    
    public Drawable getFromCache(String imageUrl) {
    	Drawable drawable = null;
    	drawable = getFromMap(imageUrl);
    	if (null == drawable) {
    		drawable = getFromFile(imageUrl);
    	}
    	return drawable;
    }
    
    public Drawable getFromMap(String imageUrl) {
    	Drawable drawable = null;
    	if (imageCache.containsKey(imageUrl)) {
            SoftReference<Drawable> softReference = imageCache.get(imageUrl);
            drawable = softReference.get();
        }
    	return drawable;
    }
    
    public Drawable getFromFile(String imageUrl) {
    	Drawable drawable = null;
    	return drawable;
    }
 
    public Drawable loadImageFromUrl(String imageUrl) {
    	Drawable drawable = null;
    	try {
    		URL url = new URL(imageUrl);
			InputStream is = (InputStream) url.getContent();
			drawable = Drawable.createFromStream(is, null);
    	} catch (Exception e) {
    		
    	}
	    return drawable;
    }
 
	public interface ImageCallback {
	    public void imageLoaded(Drawable imageDrawable, String imageUrl);
	}
}
