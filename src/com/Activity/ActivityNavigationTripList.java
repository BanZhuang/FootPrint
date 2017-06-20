 package com.Activity;


import org.json.JSONObject;

import com.Adapter.ActivityNavigationTripAdapter;
import com.Bll.MainBindService;
import com.Bll.MinaSocket;
import com.Tool.SPUtils;
import com.Tool.ToastUtil;

import Entity.SetTripBriefListEntity;
import Entity.TripBriefEntity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

public class ActivityNavigationTripList extends Activity{
	
	private ImageView back;
	private ListView listview;
	
	//�Զ������
	private ActivityNavigationTripAdapter adapter;
	private SetTripBriefListEntity list;
	
	//���ݸ��»�ȡ
	public static Handler mhander;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_navigationtrip_list);
		initView();
		setlisten();
		int userid=(Integer) SPUtils.get(ActivityNavigationTripList.this, "userId",-1);
		JSONObject json=new JSONObject();
		json=new TripBriefEntity().ToJSON(13,1,userid);
		try {
			MinaSocket.SendMessage(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		UpdateUI();
		
	}
	private void UpdateUI() {
		mhander=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what==0){
					if(MainBindService.sNavigationTripBriefList.isFlag()){
						list=(SetTripBriefListEntity) MainBindService.sNavigationTripBriefList.getData();
						adapter = new ActivityNavigationTripAdapter(ActivityNavigationTripList.this, list);
						listview.setAdapter(adapter);
						listview.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {

								Intent intent=new Intent(ActivityNavigationTripList.this,ActivityNavigationTripDetails.class);
								intent.putExtra("navigationtripid", list.getItem(arg2).getTrip_id()+"");
								startActivity(intent);
								
							}
						});
					}else{
						ToastUtil.show(ActivityNavigationTripList.this, "���ݻ�ȡ�쳣");
					}
				}
			}
		};
	}
//	private void getData() {
//
//		for(int i=0;i<10;i++){
//			list.add(new TripWithStateEntity("1","�����ҵ��Լ����г�", "���Ǹ��������С��Ի�С�ֶӡ�������С�ֶӵ��ˣ����Ⱞ�ԣ��������~~��Ϊ���ǻ�������������·����ʶ��~~", "������","��������"));
//			
//		}
//	}
	private void initView() {
		back=(ImageView)findViewById(R.id.back_image);
		listview=(ListView)findViewById(R.id.listview);
	}
	private void setlisten() {
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

}
