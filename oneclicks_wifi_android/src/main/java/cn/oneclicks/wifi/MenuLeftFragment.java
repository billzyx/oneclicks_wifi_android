package cn.oneclicks.wifi;

import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class MenuLeftFragment extends Fragment
{
	private View mView;
	private ListView mCategories;
	private List<String> mDatas = Arrays
			.asList("校园网设置", "关于");
	private ListAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		if (mView == null)
		{
			initView(inflater, container);
		}
		return mView;
	}

	private void initView(LayoutInflater inflater, ViewGroup container)
	{
		mView = inflater.inflate(R.layout.left_menu, container, false);
		mCategories = (ListView) mView
				.findViewById(R.id.id_listview_categories);
		mAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, mDatas);
		mCategories.setAdapter(mAdapter);
		mCategories.setOnItemClickListener(itemClickListener);
	
			    
	}
	 private  OnItemClickListener itemClickListener =new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				switch(position)
				{
					case 0:{
						Intent intent = new Intent(getActivity(),Setting.class); 
						MenuLeftFragment.this.startActivity(intent);
					} break;
					case 1:{Intent intent = new Intent(getActivity(),About.class);
                        MenuLeftFragment.this.startActivity(intent);} break;
				}
				
			}
		};
	
}
