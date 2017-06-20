package com.Bll;

import java.util.List;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;

import com.Activity.ActivityFoucsDetail;
import com.Activity.ActivityNavigationTripDetails;
import com.Activity.ActivityShowNavigationTripPlanMap;
import com.Activity.ActivityTripDetail;
import com.Adapter.ActivityFootPrintListAdapter;
import com.Adapter.ActivityNavigationTripAdapter;
import com.Adapter.ActivityNavigationTripFocusListAdapter;
import com.Adapter.ActivityTripPlanListAdapter;
import com.Adapter.CommentListAdapter;
import com.Adapter.FoucsBreifListAdapter;
import com.Adapter.FragmentTripPageListAdapter;
import com.Common.FileUploadRequest;
import com.Fragment.MyinfoPageFragment;

public class PictureHander extends IoHandlerAdapter {	

	@SuppressWarnings("unchecked")
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
//		JSONObject jsonObject = new JSONObject(message.toString());
//		System.out.println("********"+jsonObject);
//		JSONtoDataSet dataset=new JSONtoDataSet(jsonObject);
//		CBLL Cbll=new CBLL();
		if(message instanceof List){
			List<FileUploadRequest>fileUploadRequest=(List<FileUploadRequest>) message;
			int number=fileUploadRequest.get(0).getNumber();
			int tagpage=fileUploadRequest.get(0).getTagPage();
			Bitmap bitmap = null;
			for (int i = 0; i < fileUploadRequest.size(); i++) {
				
	            String  fileName = fileUploadRequest.get(i).getFileName();  
	            byte [] bytes = fileUploadRequest.get(i).getBytes();  
	            //String file = new Date().getTime()+fileName.substring(fileName.lastIndexOf("."),fileName.length()); 
	            if(bytes.length!=0){
            		 bitmap=BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            		}
	            System.out.println("success");
            }
			
			if(tagpage==1){//��ҳ�г��б�ͼƬ
				 Message msg=new Message();
					msg.what=1;
					msg.arg1=number;
					msg.obj=bitmap;
					FragmentTripPageListAdapter.mhander.sendMessage(msg);
			}else if(tagpage==2){//�����г��б�ͼƬ
				 Message msg=new Message();
					msg.what=1;
					msg.arg1=number;
					msg.obj=bitmap;
					ActivityNavigationTripAdapter.mhander.sendMessage(msg);
			}else if(tagpage==3){//������ע���б�ͼƬ
				 Message msg=new Message();
					msg.what=1;
					msg.arg1=number;
					msg.obj=bitmap;
					FoucsBreifListAdapter.mhander.sendMessage(msg);
			}else if(tagpage==4){//�ѷ����г̰����б�ͼƬ
				 Message msg=new Message();
					msg.what=1;
					msg.arg1=number;
					msg.obj=bitmap;
					ActivityTripPlanListAdapter.mhander.sendMessage(msg);
			}else if(tagpage==5){//�ѷ����г̰����б�ͼƬ
				 Message msg=new Message();
					msg.what=1;
					msg.arg1=number;
					msg.obj=bitmap;
					ActivityNavigationTripFocusListAdapter.mhander.sendMessage(msg);
			}else if(tagpage==6){//������ͷ��
				 Message msg=new Message();
					msg.what=1;
					msg.arg1=number;
					msg.obj=bitmap;
					CommentListAdapter.mhander.sendMessage(msg);
			}else if(tagpage==7){//�����г̷���ͼ
				 Message msg=new Message();
					msg.what=2;
					msg.arg1=number;
					msg.obj=bitmap;
					ActivityNavigationTripDetails.mhander.sendMessage(msg);
			}else if(tagpage==8){//�ѷ����г̷���ͼ
				 Message msg=new Message();
					msg.what=1;
					msg.arg1=number;
					msg.obj=bitmap;
					ActivityTripDetail.mhander.sendMessage(msg);
			}else if(tagpage==9){//�ҵ���Ϣfragment�е�ͷ��
				 Message msg=new Message();
					msg.what=1;
					msg.arg1=number;
					msg.obj=bitmap;
					MyinfoPageFragment.mhander.sendMessage(msg);
			}else if(tagpage==10){//�޸���Ϣ��е�ͷ��
				 Message msg=new Message();
					msg.what=1;
					msg.arg1=number;
					msg.obj=bitmap;
					MyinfoPageFragment.mhander.sendMessage(msg);
			}else if(tagpage==11){//��ע���������
				 Message msg=new Message();
					msg.what=1;
					msg.arg1=number;
					msg.obj=bitmap;
					ActivityFoucsDetail.mhander.sendMessage(msg);
			}else if(tagpage==12){//�ȵ���Ϣ�б��û�ͷ��
				 Message msg=new Message();
					msg.what=1;
					msg.arg1=number;
					msg.obj=bitmap;
					ActivityFootPrintListAdapter.mhander.sendMessage(msg);
			}else if(tagpage==13){//�����켣briefͼƬ
				 Message msg=new Message();
					msg.what=2;
					msg.arg1=number;
					msg.obj=bitmap;
					ActivityShowNavigationTripPlanMap.mhander.sendMessage(msg);
			}
			
			
			
			

		}
	}
	
	//������Ϣ
    public void messageSent(IoSession session, Object message) throws Exception {
    	//logger.info("���͸���������Ϣ -> ��" + message);
    	System.out.println("���͸���������Ϣ -> ��" + message);
    }

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		//logger.error("�ͻ��˷����쳣...", cause);
		System.out.println("�ͻ��˷����쳣..."+cause);
	}
}
