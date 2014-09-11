package cn.ifanmi.findme.ui.view.ptor;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.ifanmi.findme.R;

/**
 * 这个类封装了下拉刷新的布局
 * 
 * @author Li Hong
 * @since 2013-7-30
 */
public class RotateLoadingLayout extends LoadingLayout {
    /**旋转动画的时间*/
    static final int ROTATION_ANIMATION_DURATION = 1200;
    /**动画插值*/
    static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();
    /**Header的容器*/
    private RelativeLayout mHeaderContainer;
    /**箭头图片*/
    private ImageView mArrowImageView;
    /**状态提示TextView*/
    private TextView mHintTextView;
    /**最后更新时间的TextView*/
    private TextView mHeaderTimeView;
    /**最后更新时间的标题*/
    private TextView mHeaderTimeViewTitle;
    /**旋转的动画*/
    private Animation mRotateAnimation;
    
    /**箭头旋转的时间*/
    private final int ROTATE_ANIM_DURATION = 180;
    /**箭头向上旋转的动画*/
    private Animation mRotateUpAnim;
    /**箭头向下旋转的动画*/
	private Animation mRotateDownAnim;
    
    /**
     * 构造方法
     * 
     * @param context context
     */
    public RotateLoadingLayout(Context context) {
        super(context);
        init(context);
    }

    /**
     * 构造方法
     * 
     * @param context context
     * @param attrs attrs
     */
    public RotateLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化
     * 
     * @param context context
     */
    private void init(Context context) {
        mHeaderContainer = (RelativeLayout) findViewById(R.id.pull_to_refresh_header_content);
        mArrowImageView = (ImageView) findViewById(R.id.pull_to_refresh_header_arrow);
        mHintTextView = (TextView) findViewById(R.id.pull_to_refresh_header_hint_textview);
        mHeaderTimeView = (TextView) findViewById(R.id.pull_to_refresh_header_time);
        mHeaderTimeViewTitle = (TextView) findViewById(R.id.pull_to_refresh_last_update_time_text);
        
        mArrowImageView.setScaleType(ScaleType.CENTER);
        
        float pivotValue = 0.5f;    // SUPPRESS CHECKSTYLE
        float toDegree = 720.0f;    // SUPPRESS CHECKSTYLE
        mRotateAnimation = new RotateAnimation(0.0f, toDegree, Animation.RELATIVE_TO_SELF, pivotValue,
                Animation.RELATIVE_TO_SELF, pivotValue);
        mRotateAnimation.setFillAfter(true);
        mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
        
        /**两种箭头转动的动画，分别为顺时针和逆时针*/
        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);
		mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);
    }
    
    @Override
    protected View createLoadingView(Context context, AttributeSet attrs) {
        View container = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header, null);
        return container;
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {
        // 如果最后更新的时间的文本是空的话，隐藏前面的标题
        mHeaderTimeViewTitle.setVisibility(TextUtils.isEmpty(label) ? View.INVISIBLE : View.VISIBLE);
        mHeaderTimeView.setText(label);
    }

    @Override
    public int getContentSize() {
        if (null != mHeaderContainer) {
            return mHeaderContainer.getHeight();
        }
        
        return (int) (getResources().getDisplayMetrics().density * 60);
    }
    
    @Override
    protected void onStateChanged(State curState, State oldState) {
        super.onStateChanged(curState, oldState);
    }

    @Override
    protected void onReset() {
        resetRotation();
        /**刷新完成后清除动画，并修改成箭头的图片*/
        mArrowImageView.setImageResource(R.drawable.xsearch_msg_pull_arrow_down);
        mHintTextView.setText(R.string.pull_to_refresh_header_hint_normal);
    }

    @Override
    protected void onReleaseToRefresh() {
    	resetRotation();
    	/**当前松开可刷新，这个时候要清除动画，再设置新的动画*/
    	mArrowImageView.startAnimation(mRotateUpAnim);
        mHintTextView.setText(R.string.pull_to_refresh_header_hint_ready);
    }
    
    @Override
    protected void onPullToRefresh() {
    	/**根据先前的状态来判断现在要怎么设置动画*/
    	State preState = getPreState();
    	if (State.REFRESHING == preState) {
    		resetRotation();
    	} else if (State.RELEASE_TO_REFRESH == preState) {
    		mArrowImageView.startAnimation(mRotateDownAnim);
    	}   	
        mHintTextView.setText(R.string.pull_to_refresh_header_hint_normal);
    }
    
    @Override
    protected void onRefreshing() {
        resetRotation();
        /**刷新的时候要切换图片，再改成旋转的动画*/
        mArrowImageView.setImageResource(R.drawable.default_ptr_rotate);
        mArrowImageView.startAnimation(mRotateAnimation);
        mHintTextView.setText(R.string.pull_to_refresh_header_hint_loading);
    }
    
    @Override
    public void onPull(float scale) {
    	//已经使用箭头图片了，所以不要用这个函数了
//        float angle = scale * 180f; // SUPPRESS CHECKSTYLE
//        mArrowImageView.setRotation(angle);	要api 11才能够用这个函数
    }
    
    /**
     * 重置动画
     */
    private void resetRotation() {
        mArrowImageView.clearAnimation();
//        mArrowImageView.setRotation(0);	已经使用箭头图片了，所以不要用这个函数了，要api 11才能够用这个函数
    }
}
