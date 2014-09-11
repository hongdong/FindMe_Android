package cn.ifanmi.findme.util;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.bitmap.core.BitmapDisplayConfig;
import cn.ifanmi.findme.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.widget.ImageView;

public class ImageViewUtil {

	/**
	 * FinalBitmap显示图片的默认动画，我抄出来的
	 * @param imageView
	 * @param bitmap
	 */
	@SuppressWarnings("deprecation")
	public static void fadeInDisplay(View imageView,Bitmap bitmap){
		final TransitionDrawable td =
                new TransitionDrawable(new Drawable[] {
                        new ColorDrawable(android.R.color.transparent),
                        new BitmapDrawable(imageView.getResources(), bitmap)
                });
        if (imageView instanceof ImageView) {
			((ImageView)imageView).setImageDrawable(td);
		} else {
			imageView.setBackgroundDrawable(td);
		}
        td.startTransition(300);
	}
	
	/**
	 * CircleImageView显示。
	 * 因为FinalBitmap的显示器是全局的，不能针对单个ImageView来设置，
	 * 所以这里就只能设置单个ImageView的显示动画。
	 * 全局显示器会判断当前ImageView的显示动画是默认的还是用户自定义的。
	 * 在这里我们都设成用户自定义的。
	 * 这样做的话，除了CircleImageView之外，其它的图片显示还是用默认动画，效果会好一些。
	 * 当然这个默认动画代码也是我抄出来的，就是上面那个方法。
	 * 
	 * 目前CircleImageView显示的都是头像，所以loadingBitmap都设置为R.drawable.default_photo
	 * @param context
	 * @param imageView
	 * @param url
	 */
	public static void displayCircleImageView(Context context, ImageView imageView, String url) {
		FinalBitmap finalBitmap = FinalBitmap.create(context);
		Bitmap loadingBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_photo);
		BitmapDisplayConfig config = new BitmapDisplayConfig();
		config.setLoadingBitmap(loadingBitmap);
		config.setAnimationType(BitmapDisplayConfig.AnimationType.userDefined);
		finalBitmap.display(imageView, url, config);
	}
	
	public static void displayNormalImageView(Context context, ImageView imageView, String url) {
		FinalBitmap finalBitmap = FinalBitmap.create(context);
		Bitmap loadingBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_photo_loading);
		Bitmap loadfailBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_photo_loadfail);
		BitmapDisplayConfig config = new BitmapDisplayConfig();
		config.setLoadingBitmap(loadingBitmap);
		config.setLoadfailBitmap(loadfailBitmap);
		finalBitmap.display(imageView, url, config);
	}
}
