package com.jeremyfeinstein.slidingmenu.lib;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.KeyEventCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;


public class CustomViewAbove extends ViewGroup {

	private static final String TAG = "CustomViewAbove";
	private static final boolean DEBUG = false;

	//锟角凤拷使锟矫伙拷锟斤拷
	private static final boolean USE_CACHE = false;

	//锟斤拷锟斤拷锟斤拷锟绞憋拷锟�
	private static final int MAX_SETTLE_DURATION = 600; // ms
	
	//锟斤拷小锟斤拷锟斤拷锟侥撅拷锟斤拷
	private static final int MIN_DISTANCE_FOR_FLING = 25; // dips

	/**
	 * 锟斤拷锟斤拷一锟斤拷锟斤拷锟轿讹拷锟斤拷锟斤拷效锟斤拷锟斤拷
	 * Interpolator锟斤拷锟斤拷锟斤拷锟斤拷锟轿讹拷锟斤拷效锟斤拷锟藉动锟斤拷锟侥变化锟绞ｏ拷锟斤拷锟斤拷使锟斤拷锟节的讹拷锟斤拷效锟斤拷锟斤拷锟� accelerated(锟斤拷锟斤拷)锟斤拷decelerated(锟斤拷锟斤拷),repeated(锟截革拷),bounced(锟斤拷锟斤拷)锟饺★拷
	 */
	private static final Interpolator sInterpolator = new Interpolator() {
		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t * t * t + 1.0f;
		}
	};

	//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷图
	private View mContent;

	//锟斤拷前锟斤拷选锟斤拷
	private int mCurItem;
	
	//锟斤拷锟斤拷锟斤拷锟斤拷
	private Scroller mScroller;

	//锟角凤拷锟杰癸拷使锟矫伙拷锟斤拷锟斤拷锟斤拷
	private boolean mScrollingCacheEnabled;

	//锟角凤拷锟斤拷锟节伙拷锟斤拷
	private boolean mScrolling;

	//锟角凤拷锟斤拷锟斤拷锟较讹拷
	private boolean mIsBeingDragged;
	
	//锟角凤拷锟杰癸拷锟较讹拷
	private boolean mIsUnableToDrag;
	
	//锟斤拷锟藉触锟斤拷锟斤拷锟斤拷锟街�
	private int mTouchSlop;
	
	//锟斤拷始锟斤拷锟斤拷锟斤拷锟斤拷幕X锟斤拷锟街�
	private float mInitialMotionX;
	
	//锟斤拷锟斤拷贫锟斤拷锟斤拷锟�X锟斤拷Y锟斤拷锟斤拷锟�
	private float mLastMotionX,mLastMotionY;
	
	/**
	 * 锟斤拷锟斤拷一锟斤拷锟筋动指锟诫，锟节讹拷愦ワ拷锟斤拷锟绞憋拷锟斤拷锟斤拷
	 */
	protected int mActivePointerId = INVALID_POINTER;
	
	/**
	 * 为锟斤拷前锟侥活动指锟诫赋值
	 */
	private static final int INVALID_POINTER = -1;

	/**
	 * 锟斤拷锟斤拷锟斤拷锟斤拷锟节硷拷木锟斤拷锟劫讹拷
	 */
	protected VelocityTracker mVelocityTracker;
	
	//锟斤拷小锟斤拷锟斤拷锟劫讹拷值
	private int mMinimumVelocity;
	
	//锟斤拷蠡�讹拷锟劫讹拷值
	protected int mMaximumVelocity;
	
	//锟斤拷锟斤拷锟侥撅拷锟斤拷
	private int mFlingDistance;

	//锟斤拷锟斤拷锟铰凤拷锟斤拷图锟斤拷锟斤拷
	private CustomViewBehind mViewBehind;

	//锟角凤拷锟杰癸拷使锟斤拷
	private boolean mEnabled = true;

	//页锟斤拷谋锟斤拷锟斤拷锟斤拷
	private OnPageChangeListener mOnPageChangeListener;
	
	//锟节诧拷页锟斤拷谋锟斤拷锟斤拷锟斤拷
	private OnPageChangeListener mInternalPageChangeListener;

	//锟截闭硷拷锟斤拷锟斤拷
	private OnClosedListener mClosedListener;
	
	//锟津开硷拷锟斤拷锟斤拷
	private OnOpenedListener mOpenedListener;

	//锟斤拷疟锟斤拷锟斤拷缘锟斤拷锟酵硷拷锟斤拷锟叫憋拷
	private List<View> mIgnoredViews = new ArrayList<View>();

	/**
	 * 锟斤拷锟矫此接匡拷去锟斤拷应锟侥憋拷选锟斤拷页锟斤拷锟阶刺�
	 */
	public interface OnPageChangeListener {

		/**
		 * This method will be invoked when the current page is scrolled, either as part
		 * of a programmatically initiated smooth scroll or a user initiated touch scroll.
		 *
		 * @param position Position index of the first page currently being displayed.
		 *                 Page position+1 will be visible if positionOffset is nonzero.
		 * @param positionOffset Value from [0, 1) indicating the offset from the page at position.
		 * @param positionOffsetPixels Value in pixels indicating the offset from position.
		 */
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

		/**
		 * This method will be invoked when a new page becomes selected. Animation is not
		 * necessarily complete.
		 *
		 * @param position Position index of the new selected page.
		 */
		public void onPageSelected(int position);

	}

	/**
	 * Simple implementation of the {@link OnPageChangeListener} interface with stub
	 * implementations of each method. Extend this if you do not intend to override
	 * every method of {@link OnPageChangeListener}.
	 */
	public static class SimpleOnPageChangeListener implements OnPageChangeListener {

		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			// This space for rent
		}

		public void onPageSelected(int position) {
			// This space for rent
		}

		public void onPageScrollStateChanged(int state) {
			// This space for rent
		}

	}

	public CustomViewAbove(Context context) {
		this(context, null);
	}

	public CustomViewAbove(Context context, AttributeSet attrs) {
		super(context, attrs);
		initCustomViewAbove();
	}

	/**
	 * 锟斤拷始锟斤拷锟斤拷锟较凤拷锟斤拷图
	 */
	void initCustomViewAbove() {
		//锟斤拷锟斤拷锟角凤拷锟杰癸拷锟斤拷锟斤拷锟皆讹拷锟斤拷牟锟斤拷郑锟�false锟角匡拷锟斤拷
		setWillNotDraw(false);
		//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷丶锟斤拷锟斤拷取锟斤拷锟斤拷锟斤拷
		setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
		//锟斤拷锟斤拷锟角凤拷锟杰癸拷锟斤拷取锟斤拷锟斤拷
		setFocusable(true);
		
		//锟矫碉拷锟斤拷锟斤拷锟斤拷
		final Context context = getContext();
		
		//实锟斤拷锟斤拷锟斤拷锟�
		mScroller = new Scroller(context, sInterpolator);
		
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		
		//锟斤拷锟斤拷芄锟斤拷锟斤拷锟斤拷锟斤拷苹锟斤拷锟斤拷木锟斤拷耄�锟斤拷示锟斤拷锟斤拷锟斤拷时锟斤拷锟街碉拷锟狡讹拷要锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟脚匡拷始锟狡讹拷锟截硷拷
		mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
		
		//锟斤拷锟斤拷锟斤拷锟街达拷锟揭伙拷锟�fling锟斤拷锟狡讹拷锟斤拷锟斤拷锟斤拷小锟劫讹拷值
		mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
		
		//锟斤拷锟斤拷锟斤拷锟街达拷锟揭伙拷锟�fling锟斤拷锟狡讹拷锟斤拷锟斤拷锟斤拷锟斤拷俣锟街�
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
		
		setInternalPageChangeListener(new SimpleOnPageChangeListener() {
			public void onPageSelected(int position) {
				if (mViewBehind != null) {
					switch (position) {
					case 0:
					case 2:
						mViewBehind.setChildrenEnabled(true);
						break;
					case 1:
						mViewBehind.setChildrenEnabled(false);
						break;
					}
				}
			}
		});
		//锟斤拷酶锟斤拷只锟斤拷璞革拷锟斤拷锟侥伙拷芏锟街�
		final float density = context.getResources().getDisplayMetrics().density;
		//锟斤拷锟斤拷锟侥撅拷锟斤拷
		mFlingDistance = (int) (MIN_DISTANCE_FOR_FLING * density);
	}

	/**
	 * 锟斤拷锟矫碉拷前选锟叫碉拷锟斤拷
	 */
	public void setCurrentItem(int item) {
		setCurrentItemInternal(item, true, false);
	}

	/**
	 * 锟斤拷锟矫碉拷前选锟叫碉拷锟筋，锟角凤拷平锟斤拷锟侥癸拷傻锟窖★拷锟斤拷锟斤拷页锟斤拷
	 */
	public void setCurrentItem(int item, boolean smoothScroll) {
		setCurrentItemInternal(item, smoothScroll, false);
	}

	/**
	 * 锟矫碉拷锟斤拷前选锟叫碉拷锟斤拷
	 */
	public int getCurrentItem() {
		return mCurItem;
	}

	/**
	 * 锟斤拷锟矫碉拷前锟节诧拷选锟叫碉拷锟斤拷
	 */
	void setCurrentItemInternal(int item, boolean smoothScroll, boolean always) {
		setCurrentItemInternal(item, smoothScroll, always, 0);
	}

	/**
	 * 锟斤拷锟矫碉拷前锟节诧拷选锟叫碉拷锟斤拷
	 */
	void setCurrentItemInternal(int item, boolean smoothScroll, boolean always, int velocity) {
		if (!always && mCurItem == item) {
			setScrollingCacheEnabled(false);
			return;
		}

		item = mViewBehind.getMenuPage(item);

		final boolean dispatchSelected = mCurItem != item;
		mCurItem = item;
		final int destX = getDestScrollX(mCurItem);
		if (dispatchSelected && mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageSelected(item);
		}
		if (dispatchSelected && mInternalPageChangeListener != null) {
			mInternalPageChangeListener.onPageSelected(item);
		}
		if (smoothScroll) {
			smoothScrollTo(destX, 0, velocity);
		} else {
			completeScroll();
			scrollTo(destX, 0);
		}
	}

	/**
	 * 锟斤拷锟斤拷一锟斤拷锟斤拷锟斤拷锟铰硷拷锟斤拷页锟斤拷谋锟斤拷锟竭硷拷锟劫癸拷锟斤拷锟斤拷时锟斤拷锟斤拷锟�
	 */
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		mOnPageChangeListener = listener;
	}

	/**
	 * 锟斤拷锟矫打开硷拷锟斤拷锟铰硷拷
	 */
	public void setOnOpenedListener(OnOpenedListener l) {
		mOpenedListener = l;
	}

	/**
	 * 锟斤拷锟矫关闭硷拷锟斤拷锟铰硷拷
	 */
	public void setOnClosedListener(OnClosedListener l) {
		mClosedListener = l;
	}

	/**
	 * Set a separate OnPageChangeListener for internal use by the support library.
	 *
	 * @param listener Listener to set
	 * @return The old listener that was set, if any.
	 */
	OnPageChangeListener setInternalPageChangeListener(OnPageChangeListener listener) {
		OnPageChangeListener oldListener = mInternalPageChangeListener;
		mInternalPageChangeListener = listener;
		return oldListener;
	}

	/**
	 * 锟斤拷颖锟斤拷锟斤拷缘锟斤拷锟斤拷
	 */
	public void addIgnoredView(View v) {
		if (!mIgnoredViews.contains(v)) {
			mIgnoredViews.add(v);
		}
	}

	/**
	 * 锟狡筹拷锟斤拷缘锟斤拷锟斤拷
	 */
	public void removeIgnoredView(View v) {
		mIgnoredViews.remove(v);
	}

	/**
	 * 锟斤拷毡锟斤拷锟斤拷缘锟斤拷锟斤拷
	 */
	public void clearIgnoredViews() {
		mIgnoredViews.clear();
	}

	// We want the duration of the page snap animation to be influenced by the distance that
	// the screen has to travel, however, we don't want this duration to be effected in a
	// purely linear fashion. Instead, we use this method to moderate the effect that the distance
	// of travel has on the overall snap duration.
	float distanceInfluenceForSnapDuration(float f) {
		f -= 0.5f; // center the values about 0.
		f *= 0.3f * Math.PI / 2.0f;
		return (float) FloatMath.sin(f);
	}

	/**
	 * 锟矫碉拷锟斤拷锟斤拷锟斤拷锟斤拷X锟斤拷锟斤拷锟斤拷
	 */
	public int getDestScrollX(int page) {
		switch (page) {
		case 0:
		case 2:
			return mViewBehind.getMenuLeft(mContent, page);
		case 1:
			return mContent.getLeft();
		}
		return 0;
	}

	/**
	 * 锟矫碉拷锟斤拷呖锟�
	 */
	private int getLeftBound() {
		return mViewBehind.getAbsLeftBound(mContent);
	}

	/**
	 * 锟矫碉拷锟揭边匡拷
	 */
	private int getRightBound() {
		return mViewBehind.getAbsRightBound(mContent);
	}

	public int getContentLeft() {
		return mContent.getLeft() + mContent.getPaddingLeft();
	}

	/**
	 * 锟矫碉拷锟斤拷锟斤拷锟剿碉拷锟角凤拷锟�
	 */
	public boolean isMenuOpen() {
		return mCurItem == 0 || mCurItem == 2;
	}

	/**
	 * 锟角凤拷锟斤拷锟斤拷锟酵�
	 */
	private boolean isInIgnoredView(MotionEvent ev) {
		Rect rect = new Rect();
		for (View v : mIgnoredViews) {
			v.getHitRect(rect);
			if (rect.contains((int)ev.getX(), (int)ev.getY())) return true;
		}
		return false;
	}

	/**
	 * 锟矫碉拷锟铰凤拷锟斤拷图锟侥匡拷锟�
	 */
	public int getBehindWidth() {
		if (mViewBehind == null) {
			return 0;
		} else {
			return mViewBehind.getBehindWidth();
		}
	}

	/**
	 * 锟矫碉拷锟接控硷拷锟侥匡拷锟�
	 */
	public int getChildWidth(int i) {
		switch (i) {
		case 0:
			return getBehindWidth();
		case 1:
			return mContent.getWidth();
		default:
			return 0;
		}
	}

	/**
	 * 锟矫碉拷锟角凤拷锟杰癸拷锟斤拷锟斤拷
	 */
	public boolean isSlidingEnabled() {
		return mEnabled;
	}

	/**
	 * 锟斤拷锟斤拷锟角凤拷锟杰癸拷锟斤拷锟斤拷
	 */
	public void setSlidingEnabled(boolean b) {
		mEnabled = b;
	}

	/**
	 * 平锟斤拷锟侥伙拷锟斤拷锟斤拷指锟斤拷锟斤拷位锟斤拷
	 */
	void smoothScrollTo(int x, int y) {
		smoothScrollTo(x, y, 0);
	}

	/**
	 * 通锟斤拷锟斤拷锟斤拷锟劫讹拷锟斤拷平锟斤拷锟侥伙拷锟斤拷锟斤拷指锟斤拷锟斤拷位锟斤拷
	 */
	void smoothScrollTo(int x, int y, int velocity) {
		if (getChildCount() == 0) {
			// Nothing to do.
			setScrollingCacheEnabled(false);
			return;
		}
		//锟斤拷玫锟角�View锟斤拷示锟斤拷锟街碉拷锟斤拷叩锟斤拷锟揭伙拷锟�View锟斤拷锟斤拷叩木锟斤拷锟�
		int sx = getScrollX();
		int sy = getScrollY();
		
		int dx = x - sx;
		int dy = y - sy;
		
		//锟斤拷锟斤拷锟斤拷0锟斤拷说锟斤拷锟斤拷锟斤拷腔锟斤拷锟斤拷锟揭伙拷锟斤拷锟侥伙拷木锟斤拷锟�
		if (dx == 0 && dy == 0) {
			completeScroll();
			if (isMenuOpen()) {
				if (mOpenedListener != null)
					mOpenedListener.onOpened();
			} else {
				if (mClosedListener != null)
					mClosedListener.onClosed();
			}
			return;
		}

		setScrollingCacheEnabled(true);
		mScrolling = true;

		//锟斤拷锟斤拷路锟斤拷锟酵硷拷目锟斤拷
		final int width = getBehindWidth();
		
		final int halfWidth = width / 2;
		
		//取锟斤拷锟斤拷锟斤拷锟斤拷小锟斤拷值锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟铰凤拷锟斤拷图锟斤拷鹊谋锟街�
		final float distanceRatio = Math.min(1f, 1.0f * Math.abs(dx) / width);
		
		//锟斤拷玫锟角帮拷锟斤拷锟斤拷木锟斤拷锟�
		final float distance = halfWidth + halfWidth * distanceInfluenceForSnapDuration(distanceRatio);

		//锟斤拷始锟斤拷锟斤拷锟斤拷锟绞憋拷锟�
		int duration = 0;
		
		//锟斤拷锟斤拷俣鹊木锟斤拷值
		velocity = Math.abs(velocity);
		
		if (velocity > 0) {
			//Math.round()锟斤拷锟斤拷锟斤拷锟斤拷
			duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
		} else {
			final float pageDelta = (float) Math.abs(dx) / width;
			duration = (int) ((pageDelta + 1) * 100);
			duration = MAX_SETTLE_DURATION;
		}
		//取锟斤拷锟斤拷锟斤拷锟斤拷小锟斤拷一锟斤拷值锟斤拷锟斤拷锟斤拷锟斤拷时锟斤拷
		duration = Math.min(duration, MAX_SETTLE_DURATION);

		//锟斤拷始锟斤拷锟斤拷
		mScroller.startScroll(sx, sy, dx, dy, duration);
		
		//刷锟铰斤拷锟斤拷
		invalidate();
	}

	/**
	 * 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷图
	 */
	public void setContent(View v) {
		if (mContent != null) 
			this.removeView(mContent);
		mContent = v;
		addView(mContent);
	}

	/**
	 * 锟矫碉拷锟斤拷锟斤拷锟斤拷图
	 */
	public View getContent() {
		return mContent;
	}

	/**
	 * 锟斤拷锟斤拷锟铰凤拷锟斤拷图
	 */
	public void setCustomViewBehind(CustomViewBehind cvb) {
		mViewBehind = cvb;
	}

	/**
	 * 锟节革拷元锟斤拷锟斤拷要锟斤拷锟矫该控硷拷时锟斤拷锟矫★拷锟斤拷锟斤拷锟揭伙拷锟斤拷锟斤拷猓�锟斤拷锟斤拷锟斤拷要锟矫讹拷锟截凤拷锟斤拷锟斤拷锟斤拷锟斤拷然锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷widthMeasureSpec锟斤拷heightMeasureSpec锟斤拷
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(0, widthMeasureSpec);
		int height = getDefaultSize(0, heightMeasureSpec);
		setMeasuredDimension(width, height);

		final int contentWidth = getChildMeasureSpec(widthMeasureSpec, 0, width);
		final int contentHeight = getChildMeasureSpec(heightMeasureSpec, 0, height);
		mContent.measure(contentWidth, contentHeight);
	}

	/**
	 * 锟斤拷锟斤拷图锟竭达拷谋锟斤拷时锟斤拷锟斤拷锟�
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// Make sure scroll position is set correctly.
		if (w != oldw) {
			// [ChrisJ] - This fixes the onConfiguration change for orientation issue..
			// maybe worth having a look why the recomputeScroll pos is screwing
			// up?
			completeScroll();
			scrollTo(getDestScrollX(mCurItem), getScrollY());
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int width = r - l;
		final int height = b - t;
		mContent.layout(0, 0, width, height);
	}

	/**
	 * 锟斤拷锟斤拷锟较凤拷锟斤拷图锟斤拷偏锟斤拷锟斤拷
	 */
	public void setAboveOffset(int i) {		
		mContent.setPadding(i, mContent.getPaddingTop(), mContent.getPaddingRight(), mContent.getPaddingBottom());
	}


	@Override
	public void computeScroll() {
		if (!mScroller.isFinished()) {
			if (mScroller.computeScrollOffset()) {
				int oldX = getScrollX();
				int oldY = getScrollY();
				int x = mScroller.getCurrX();
				int y = mScroller.getCurrY();

				if (oldX != x || oldY != y) {
					scrollTo(x, y);
					pageScrolled(x);
				}

				// Keep on drawing until the animation has finished.
				invalidate();
				return;
			}
		}

		//锟斤拷苫锟斤拷锟斤拷锟斤拷锟斤拷状态
		completeScroll();
	}

	/**
	 * 页锟斤拷锟斤拷锟�
	 */
	private void pageScrolled(int xpos) {
		final int widthWithMargin = getWidth();
		final int position = xpos / widthWithMargin;
		final int offsetPixels = xpos % widthWithMargin;
		final float offset = (float) offsetPixels / widthWithMargin;

		onPageScrolled(position, offset, offsetPixels);
	}

	/**
	 * 页锟斤拷锟斤拷锟�
	 *
	 * @param position Position index of the first page currently being displayed.
	 *                 Page position+1 will be visible if positionOffset is nonzero.
	 * @param offset Value from [0, 1) indicating the offset from the page at position.
	 * @param offsetPixels Value in pixels indicating the offset from position.
	 */
	protected void onPageScrolled(int position, float offset, int offsetPixels) {
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageScrolled(position, offset, offsetPixels);
		}
		if (mInternalPageChangeListener != null) {
			mInternalPageChangeListener.onPageScrolled(position, offset, offsetPixels);
		}
	}

	/**
	 * 锟斤拷苫锟斤拷锟�
	 */
	private void completeScroll() {
		//锟角凤拷锟斤拷要锟狡讹拷
		boolean needPopulate = mScrolling;
		
		if (needPopulate) {
			// Done with scroll, no longer want to cache view drawing.
			setScrollingCacheEnabled(false);
			//锟斤拷止锟斤拷锟斤拷效锟斤拷
			mScroller.abortAnimation();
			
			//锟斤拷霉锟斤拷锟斤拷锟斤拷锟绞硷拷锟斤拷锟斤拷
			int oldX = getScrollX();
			int oldY = getScrollY();
			
			//锟斤拷霉锟斤拷锟斤拷锟斤拷锟角帮拷锟斤拷锟斤拷
			int x = mScroller.getCurrX();
			int y = mScroller.getCurrY();
			
			//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷始锟斤拷锟斤拷锟酵碉拷前锟斤拷锟斤拷瓴伙拷锟斤拷蚧�讹拷
			if (oldX != x || oldY != y) {
				scrollTo(x, y);
			}
			if (isMenuOpen()) {
				if (mOpenedListener != null)
					mOpenedListener.onOpened();
			} else {
				if (mClosedListener != null)
					mClosedListener.onClosed();
			}
		}
		//锟斤拷锟斤拷锟斤拷锟斤拷状态锟斤拷锟斤拷为false
		mScrolling = false;
	}

	//锟斤拷么锟斤拷锟侥Ｊ斤拷锟街�
	protected int mTouchMode = SlidingMenu.TOUCHMODE_MARGIN;

	/**
	 * 锟斤拷锟矫达拷锟斤拷锟斤拷模式
	 */
	public void setTouchMode(int i) {
		mTouchMode = i;
	}

	/**
	 * 锟矫碉拷锟斤拷锟斤拷锟斤拷模式
	 */
	public int getTouchMode() {
		return mTouchMode;
	}

	/**
	 * 锟叫讹拷锟角凤拷锟斤拷锟�?锟斤拷锟津开伙拷锟斤拷锟剿碉拷
	 */
	private boolean thisTouchAllowed(MotionEvent ev) {
		int x = (int) (ev.getX() + mScrollX);
		if (isMenuOpen()) {
			return mViewBehind.menuOpenTouchAllowed(mContent, mCurItem, x);
		} else {
			switch (mTouchMode) {
			case SlidingMenu.TOUCHMODE_FULLSCREEN:
				return !isInIgnoredView(ev);
			case SlidingMenu.TOUCHMODE_NONE:
				return false;
			case SlidingMenu.TOUCHMODE_MARGIN:
				return mViewBehind.marginTouchAllowed(mContent, x);
			}
		}
		return false;
	}

	/**
	 * 锟叫讹拷锟角凤拷锟斤拷锟�?锟斤拷
	 */
	private boolean thisSlideAllowed(float dx) {
		boolean allowed = false;
		if (isMenuOpen()) {
			allowed = mViewBehind.menuOpenSlideAllowed(dx);
		} else {
			allowed = mViewBehind.menuClosedSlideAllowed(dx);
		}
		if (DEBUG)
			Log.v(TAG, "this slide allowed " + allowed + " dx: " + dx);
		return allowed;
	}

	/**
	 * 锟矫碉拷指锟斤拷锟斤拷锟斤拷锟街�
	 */
	private int getPointerIndex(MotionEvent ev, int id) {
		int activePointerIndex = MotionEventCompat.findPointerIndex(ev, id);
		if (activePointerIndex == -1)
			mActivePointerId = INVALID_POINTER;
		return activePointerIndex;
	}

	private boolean mQuickReturn = false;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		if (!mEnabled)
			return false;

		final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;

		if (DEBUG)
			if (action == MotionEvent.ACTION_DOWN)
				Log.v(TAG, "Received ACTION_DOWN");

		if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP
				|| (action != MotionEvent.ACTION_DOWN && mIsUnableToDrag)) {
			endDrag();
			return false;
		}

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			determineDrag(ev);
			break;
		case MotionEvent.ACTION_DOWN:
			int index = MotionEventCompat.getActionIndex(ev);
			mActivePointerId = MotionEventCompat.getPointerId(ev, index);
			if (mActivePointerId == INVALID_POINTER)
				break;
			mLastMotionX = mInitialMotionX = MotionEventCompat.getX(ev, index);
			mLastMotionY = MotionEventCompat.getY(ev, index);
			if (thisTouchAllowed(ev)) {
				mIsBeingDragged = false;
				mIsUnableToDrag = false;
				if (isMenuOpen() && mViewBehind.menuTouchInQuickReturn(mContent, mCurItem, ev.getX() + mScrollX)) {
					mQuickReturn = true;
				}
			} else {
				mIsUnableToDrag = true;
			}
			break;
		case MotionEventCompat.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			break;
		}

		if (!mIsBeingDragged) {
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
			}
			mVelocityTracker.addMovement(ev);
		}
		return mIsBeingDragged || mQuickReturn;
	}


	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if (!mEnabled)
			return false;

		if (!mIsBeingDragged && !thisTouchAllowed(ev))
			return false;

		//		if (!mIsBeingDragged && !mQuickReturn)
		//			return false;

		final int action = ev.getAction();

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		switch (action & MotionEventCompat.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			/*
			 * If being flinged and user touches, stop the fling. isFinished
			 * will be false if being flinged.
			 */
			completeScroll();

			// Remember where the motion event started
			int index = MotionEventCompat.getActionIndex(ev);
			mActivePointerId = MotionEventCompat.getPointerId(ev, index);
			mLastMotionX = mInitialMotionX = ev.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			if (!mIsBeingDragged) {	
				determineDrag(ev);
				if (mIsUnableToDrag)
					return false;
			}
			if (mIsBeingDragged) {
				// Scroll to follow the motion event
				final int activePointerIndex = getPointerIndex(ev, mActivePointerId);
				if (mActivePointerId == INVALID_POINTER)
					break;
				final float x = MotionEventCompat.getX(ev, activePointerIndex);
				final float deltaX = mLastMotionX - x;
				mLastMotionX = x;
				float oldScrollX = getScrollX();
				float scrollX = oldScrollX + deltaX;
				final float leftBound = getLeftBound();
				final float rightBound = getRightBound();
				if (scrollX < leftBound) {
					scrollX = leftBound;
				} else if (scrollX > rightBound) {
					scrollX = rightBound;
				}
				// Don't lose the rounded component
				mLastMotionX += scrollX - (int) scrollX;
				scrollTo((int) scrollX, getScrollY());
				pageScrolled((int) scrollX);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mIsBeingDragged) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				int initialVelocity = (int) VelocityTrackerCompat.getXVelocity(
						velocityTracker, mActivePointerId);
				final int scrollX = getScrollX();
				//				final int widthWithMargin = getWidth();
				//				final float pageOffset = (float) (scrollX % widthWithMargin) / widthWithMargin;
				// TODO test this. should get better flinging behavior
				final float pageOffset = (float) (scrollX - getDestScrollX(mCurItem)) / getBehindWidth();
				final int activePointerIndex = getPointerIndex(ev, mActivePointerId);
				if (mActivePointerId != INVALID_POINTER) {
					final float x = MotionEventCompat.getX(ev, activePointerIndex);
					final int totalDelta = (int) (x - mInitialMotionX);
					int nextPage = determineTargetPage(pageOffset, initialVelocity, totalDelta);
					setCurrentItemInternal(nextPage, true, true, initialVelocity);
				} else {	
					setCurrentItemInternal(mCurItem, true, true, initialVelocity);
				}
				mActivePointerId = INVALID_POINTER;
				endDrag();
			} else if (mQuickReturn && mViewBehind.menuTouchInQuickReturn(mContent, mCurItem, ev.getX() + mScrollX)) {
				// close the menu
				setCurrentItem(1);
				endDrag();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			if (mIsBeingDragged) {
				setCurrentItemInternal(mCurItem, true, true);
				mActivePointerId = INVALID_POINTER;
				endDrag();
			}
			break;
		case MotionEventCompat.ACTION_POINTER_DOWN: {
			final int indexx = MotionEventCompat.getActionIndex(ev);
			mLastMotionX = MotionEventCompat.getX(ev, indexx);
			mActivePointerId = MotionEventCompat.getPointerId(ev, indexx);
			break;
		}
		case MotionEventCompat.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			int pointerIndex = getPointerIndex(ev, mActivePointerId);
			if (mActivePointerId == INVALID_POINTER)
				break;
			mLastMotionX = MotionEventCompat.getX(ev, pointerIndex);
			break;
		}
		return true;
	}
	
	private void determineDrag(MotionEvent ev) {
		final int activePointerId = mActivePointerId;
		final int pointerIndex = getPointerIndex(ev, activePointerId);
		if (activePointerId == INVALID_POINTER)
			return;
		final float x = MotionEventCompat.getX(ev, pointerIndex);
		final float dx = x - mLastMotionX;
		final float xDiff = Math.abs(dx);
		final float y = MotionEventCompat.getY(ev, pointerIndex);
		final float dy = y - mLastMotionY;
		final float yDiff = Math.abs(dy);
		if (xDiff > (isMenuOpen()?mTouchSlop/2:mTouchSlop) && xDiff > yDiff && thisSlideAllowed(dx)) {		
			startDrag();
			mLastMotionX = x;
			mLastMotionY = y;
			setScrollingCacheEnabled(true);
			// TODO add back in touch slop check
		} else if (xDiff > mTouchSlop) {
			mIsUnableToDrag = true;
		}
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		mScrollX = x;
		mViewBehind.scrollBehindTo(mContent, x, y);	
		((SlidingMenu)getParent()).manageLayers(getPercentOpen());
	}

	private int determineTargetPage(float pageOffset, int velocity, int deltaX) {
		int targetPage = mCurItem;
		if (Math.abs(deltaX) > mFlingDistance && Math.abs(velocity) > mMinimumVelocity) {
			if (velocity > 0 && deltaX > 0) {
				targetPage -= 1;
			} else if (velocity < 0 && deltaX < 0){
				targetPage += 1;
			}
		} else {
			targetPage = (int) Math.round(mCurItem + pageOffset);
		}
		return targetPage;
	}

	protected float getPercentOpen() {
		return Math.abs(mScrollX-mContent.getLeft()) / getBehindWidth();
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		// Draw the margin drawable if needed.
		mViewBehind.drawShadow(mContent, canvas);
		mViewBehind.drawFade(mContent, canvas, getPercentOpen());
		mViewBehind.drawSelector(mContent, canvas, getPercentOpen());
	}

	// variables for drawing
	private float mScrollX = 0.0f;

	private void onSecondaryPointerUp(MotionEvent ev) {
		if (DEBUG) Log.v(TAG, "onSecondaryPointerUp called");
		final int pointerIndex = MotionEventCompat.getActionIndex(ev);
		final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
		if (pointerId == mActivePointerId) {
			// This was our active pointer going up. Choose a new
			// active pointer and adjust accordingly.
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mLastMotionX = MotionEventCompat.getX(ev, newPointerIndex);
			mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
			if (mVelocityTracker != null) {
				mVelocityTracker.clear();
			}
		}
	}

	/**
	 * 锟斤拷始锟较讹拷
	 */
	private void startDrag() {
		mIsBeingDragged = true;
		mQuickReturn = false;
	}

	/**
	 * 锟斤拷锟斤拷锟较讹拷
	 */
	private void endDrag() {
		mQuickReturn = false;
		mIsBeingDragged = false;
		mIsUnableToDrag = false;
		mActivePointerId = INVALID_POINTER;

		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	/**
	 * 锟斤拷锟斤拷锟杰凤拷使锟矫伙拷锟斤拷锟斤拷锟斤拷
	 */
	private void setScrollingCacheEnabled(boolean enabled) {
		if (mScrollingCacheEnabled != enabled) {
			mScrollingCacheEnabled = enabled;
			if (USE_CACHE) {
				final int size = getChildCount();
				for (int i = 0; i < size; ++i) {
					final View child = getChildAt(i);
					if (child.getVisibility() != GONE) {
						child.setDrawingCacheEnabled(enabled);
					}
				}
			}
		}
	}

	/**
	 * Tests scrollability within child views of v given a delta of dx.
	 *
	 * @param v View to test for horizontal scrollability
	 * @param checkV Whether the view v passed should itself be checked for scrollability (true),
	 *               or just its children (false).
	 * @param dx Delta scrolled in pixels
	 * @param x X coordinate of the active touch point
	 * @param y Y coordinate of the active touch point
	 * @return true if child views of v can be scrolled by delta of dx.
	 */
	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
		if (v instanceof ViewGroup) {
			final ViewGroup group = (ViewGroup) v;
			final int scrollX = v.getScrollX();
			final int scrollY = v.getScrollY();
			final int count = group.getChildCount();
			// Count backwards - let topmost views consume scroll distance first.
			for (int i = count - 1; i >= 0; i--) {
				final View child = group.getChildAt(i);
				if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight() &&
						y + scrollY >= child.getTop() && y + scrollY < child.getBottom() &&
						canScroll(child, true, dx, x + scrollX - child.getLeft(),
								y + scrollY - child.getTop())) {
					return true;
				}
			}
		}

		return checkV && ViewCompat.canScrollHorizontally(v, -dx);
	}


	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// Let the focused view and/or our descendants get the key first
		return super.dispatchKeyEvent(event) || executeKeyEvent(event);
	}

	/**
	 * 执锟叫帮拷锟斤拷锟斤拷应锟铰硷拷
	 */
	public boolean executeKeyEvent(KeyEvent event) {
		boolean handled = false;
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
				handled = arrowScroll(FOCUS_LEFT);
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				handled = arrowScroll(FOCUS_RIGHT);
				break;
			case KeyEvent.KEYCODE_TAB:
				if (Build.VERSION.SDK_INT >= 11) {
					// The focus finder had a bug handling FOCUS_FORWARD and FOCUS_BACKWARD
					// before Android 3.0. Ignore the tab key on those devices.
					if (KeyEventCompat.hasNoModifiers(event)) {
						handled = arrowScroll(FOCUS_FORWARD);
					} else if (KeyEventCompat.hasModifiers(event, KeyEvent.META_SHIFT_ON)) {
						handled = arrowScroll(FOCUS_BACKWARD);
					}
				}
				break;
			}
		}
		return handled;
	}

	/**
	 * 锟斤拷没锟斤拷锟斤拷姆锟斤拷锟�
	 */
	public boolean arrowScroll(int direction) {
		View currentFocused = findFocus();
		if (currentFocused == this) currentFocused = null;

		boolean handled = false;

		View nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused,
				direction);
		if (nextFocused != null && nextFocused != currentFocused) {
			if (direction == View.FOCUS_LEFT) {
				handled = nextFocused.requestFocus();
			} else if (direction == View.FOCUS_RIGHT) {
				// If there is nothing to the right, or this is causing us to
				// jump to the left, then what we really want to do is page right.
				if (currentFocused != null && nextFocused.getLeft() <= currentFocused.getLeft()) {
					handled = pageRight();
				} else {
					handled = nextFocused.requestFocus();
				}
			}
		} else if (direction == FOCUS_LEFT || direction == FOCUS_BACKWARD) {
			// Trying to move left and nothing there; try to page.
			handled = pageLeft();
		} else if (direction == FOCUS_RIGHT || direction == FOCUS_FORWARD) {
			// Trying to move right and nothing there; try to page.
			handled = pageRight();
		}
		if (handled) {
			playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
		}
		return handled;
	}

	/**
	 * 页锟斤拷锟角凤拷锟斤拷锟斤拷锟狡讹拷
	 */
	boolean pageLeft() {
		if (mCurItem > 0) {
			setCurrentItem(mCurItem-1, true);
			return true;
		}
		return false;
	}

	/**
	 * 页锟斤拷锟角凤拷锟斤拷锟斤拷锟狡讹拷
	 */
	boolean pageRight() {
		if (mCurItem < 1) {
			setCurrentItem(mCurItem+1, true);
			return true;
		}
		return false;
	}

}
