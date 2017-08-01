package com.moneyboxadmin.pda;

import hdjc.rfid.operator.RFID_Device;

import java.util.List;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.entity.BoxDetail;
import com.example.pda.R;
import com.golbal.pda.GolbalView;
import com.imple.getnumber.StopNewClearBox;

/**
 * 缺少周转箱提示框
 * 
 * @author Administrator
 * 
 */
public class BoxDialog extends Dialog implements OnTouchListener {

	private ListView listView;
	private Button btnQueding;
	private List<BoxDetail> boxList; // 数据源
	private Context context;
	private RFID_Device rfid;

	public BoxDialog(Context context, List<BoxDetail> boxList,RFID_Device rfid) {
		super(context);
		// TODO Auto-generated constructor stub
		this.boxList = boxList;
		this.context = context;
		this.rfid=rfid;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_box);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		listView = (ListView) findViewById(R.id.listView1);
		btnQueding = (Button) findViewById(R.id.btnlgoin);
		listView.setAdapter(new Adapter());
		btnQueding.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg1.getAction()) {
		case MotionEvent.ACTION_DOWN: // 按下

			break;

		case MotionEvent.ACTION_MOVE: // 滑动

			break;
		case MotionEvent.ACTION_UP: // 松起

			if (arg0.getId() == R.id.btnlgoin) {
				rfid.addNotifly(new StopNewClearBox());
				rfid.open_a20();
				this.dismiss();
			}
			break;
		}
		return false;
	}

	/**
	 * list数据适配器
	 * 
	 * @author Administrator
	 * 
	 */
	private class Adapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return boxList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			Holder holder = null;
			if (arg1 == null) {
				holder = new Holder();
				arg1 = GolbalView.getLF(context).inflate(
						R.layout.boxinformation_item, null);
				holder.boxNum = (TextView) arg1.findViewById(R.id.box_num);
				holder.btndel = (TextView) arg1.findViewById(R.id.brand);
				arg1.setTag(holder);
			} else {
				holder = (Holder) arg1.getTag();
			}

			holder.boxNum.setText(boxList.get(arg0).getNum());
			holder.btndel.setText(boxList.get(arg0).getBrand());

			return arg1;
		}

	}

	private class Holder {
		public TextView boxNum;
		public TextView btndel;
	}

}
