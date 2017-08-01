package com.ljsw.tjbankpda.util;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;

public class TurnListviewHeight {

	public TurnListviewHeight(ListView	 lv) {
		super();
		//实例化ListAdapter对象
				ListAdapter listAdapter=lv.getAdapter();
				//获取行高
				int hangshu=listAdapter.getCount();
				int totalhanggao=0;
				//循环遍历
				for (int i = 0; i < hangshu; i++) {
					View view=listAdapter.getView(i, null, lv);
					//测量高度

					view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					view.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
					totalhanggao+=view.getMeasuredHeight();
				}
				double fenge =lv.getDividerHeight()*(hangshu-1);
				totalhanggao+=fenge;
				LayoutParams lp=lv.getLayoutParams();
				lp.height=totalhanggao;
				lv.setLayoutParams(lp);
	}
	

}
