package cn.oneclicks.wifi;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity
{

	private ViewPager mViewPager;
	private FragmentPagerAdapter mAdapter;
	private List<Fragment> mFragments = new ArrayList<Fragment>();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		// 閸掓繂顫愰崠鏈ideMenu
		initRightMenu();
		// 閸掓繂顫愰崠鏈ewPager
		initViewPager();

	}

	private void initViewPager()
	{
		mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
		MainTab01 tab01 = new MainTab01();
		MainTab02 tab02 = new MainTab02();
		MainTab03 tab03 = new MainTab03();
		mFragments.add(tab01);
		mFragments.add(tab02);
		mFragments.add(tab03);
		/**
		 * 閸掓繂顫愰崠鏈卍apter
		 */
		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
		{
			@Override
			public int getCount()
			{
				return mFragments.size();
			}

			@Override
			public Fragment getItem(int arg0)
			{
				return mFragments.get(arg0);
			}
		};
		mViewPager.setAdapter(mAdapter);
	}

	private void initRightMenu()
	{

		Fragment leftMenuFragment = new MenuLeftFragment();
		setBehindContentView(R.layout.left_menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.id_left_menu_frame, leftMenuFragment).commit();
		SlidingMenu menu = getSlidingMenu();
		menu.setMode(SlidingMenu.LEFT);
		// 鐠佸墽鐤嗙憴锔芥嚋鐏炲繐绠烽惃鍕佸锟?
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		// 鐠佸墽鐤嗗鎴濆З閼挎粌宕熺憴鍡楁禈閻ㄥ嫬顔旀惔锟?
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
//		menu.setBehindWidth()
		// 鐠佸墽鐤嗗〒鎰弳濞撴劕鍤弫鍫熺亯閻ㄥ嫬锟斤拷
		menu.setFadeDegree(0.35f);
		// menu.setBehindScrollScale(1.0f);
		menu.setSecondaryShadowDrawable(R.drawable.shadow);
		//鐠佸墽鐤嗛崣瀹犵珶閿涘牅绨╃痪褝绱氭笟褎绮﹂懣婊冨礋

	}

	public void showLeftMenu(View view)
	{
		getSlidingMenu().showMenu();
	}

	public void showRightMenu(View view)
	{
		getSlidingMenu().showSecondaryMenu();
	}
}
