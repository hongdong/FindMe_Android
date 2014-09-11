package cn.ifanmi.findme.ui.view;

import java.io.File;
import cn.ifanmi.findme.R;
import cn.ifanmi.findme.string.IntentString;
import cn.ifanmi.findme.string.NormalString;
import cn.ifanmi.findme.util.SdCardUtil;
import cn.ifanmi.findme.util.ToastUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class HeadPopupWindow extends PopupWindow {
	
	public HeadPopupWindow(Context context) {
		
	}

	public HeadPopupWindow(final Activity activity, View parent) {
		View view = View.inflate(activity, R.layout.photos_popupwindow, null);
		view.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.photos_popupwindow_fade_ins));
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout_photos_popupwindow);
		layout.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.photos_popupwindow_bottom_in));

		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		update();

		Button btn_camera = (Button) view.findViewById(R.id.btn_photos_popupwindow_camera);
		Button btn_photo = (Button) view.findViewById(R.id.btn_photos_popupwindow_photo);
		Button btn_cancle = (Button) view.findViewById(R.id.btn_photos_popupwindow_cancel);
		btn_camera.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!SdCardUtil.hasSdcard()) {  
					ToastUtil.prompt(v.getContext(), "未找到存储卡，无法存储照片！");
	            } else { 
	            	File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() 
	            			+ NormalString.File.DIR_FANMI);  
	                if(!file.exists()){  
	                    file.mkdir();  
	                }  
	                File fileName = new File(
	                		Environment.getExternalStorageDirectory().getAbsolutePath() + NormalString.File.DIR_FANMI, 
	                		NormalString.File.IMAGE_FILE_NAME);  
	                Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileName));
					activity.startActivityForResult(intentFromCapture, IntentString.RequestCode.CAMERA);
				}
				dismiss();
			}
		});
		btn_photo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intentFromGallery = new Intent();
				intentFromGallery.setType("image/*"); // 设置文件类型
				intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
				activity.startActivityForResult(intentFromGallery, IntentString.RequestCode.IMAGE);
				dismiss();
			}
		});
		btn_cancle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});
	}
}
