package com.Adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.Activity.R;
import com.Bll.MinaSocket;
import com.Bll.PictureSocket;
import com.Lru.DiskLruCache;
import com.Lru.LruCacheUtils;
import com.Tool.CallBack;
import com.Tool.CallList;

import Entity.SetFocusBriefListEntity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FoucsBreifListAdapter extends BaseAdapter{

	private SetFocusBriefListEntity list;
	private Context context;
	
	
	private LruCacheUtils lruCache;
	
	//���ݸ��»�ȡ
	public static Handler mhander;
	public List<CallList> piclist=new ArrayList<CallList>();
	
	 public FoucsBreifListAdapter(Context c, SetFocusBriefListEntity l) {
	    	super();
	    	context = c;
	    	list = l;
	    	lruCache = LruCacheUtils.getInstance();
	    	
	    	mhander=new Handler(){
				@Override
				
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					
					if(msg.what==1){
						for(int i=0;i<piclist.size();i++){
							if(piclist.get(i).getPos()==msg.arg1){
								Bitmap bitmap=(Bitmap) msg.obj;
								
								String url=list.getItem(msg.arg1).getFocus_image();
								String key = lruCache.hashKeyForDisk(url);
				                DiskLruCache.Editor editor = null;
								
								piclist.get(i).getEntity().response(bitmap);
								
								lruCache.addBitmapToCache(url,bitmap);
								try {
									editor = lruCache.diskLruCache.edit(key);
									//λͼѹ���������������ѹ����ʽ������(100��ʾ��ѹ����30��ʾѹ��70%)���������
			                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, editor.newOutputStream(0));
			                        editor.commit();//�ύ
								} catch (IOException e) {
									 try {
					                        editor.abort();//����д��
					                    } catch (IOException e1) {
					                        e1.printStackTrace();
					                    }
								}
								piclist.remove(i);
								break;
							}
						}
	 				}
				}
			};
			
	    }
	 
	//�����ڲ�����Ϊռλ��
	    class ViewHolder{
	    	ImageView focus_image;
	    	TextView focus_title;
	    	TextView focus_abstract;
	    	TextView focus_author;
	    	TextView focus_address;
	    }
	
	@Override
	public int getCount() {
		if(list==null||list.getSize()==0){
			return 0;
		}else{
			return list.getSize();}
		
	}

	@Override
	public Object getItem(int position) {
		return list.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewholder = null;
		
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.fragment_home_list_item, null);
			viewholder = new ViewHolder();
			//�����ͼ�еĶ���ؼ�
			viewholder.focus_title = (TextView) convertView.findViewById(R.id.focus_title);
			viewholder.focus_abstract = (TextView) convertView.findViewById(R.id.focus_abstract);
			viewholder.focus_author = (TextView) convertView.findViewById(R.id.focus_username);
			viewholder.focus_address=(TextView)convertView.findViewById(R.id.focus_address);
			viewholder.focus_image=(ImageView) convertView.findViewById(R.id.focus_image);
			convertView.setTag(viewholder);
		}
		else{
			viewholder = (ViewHolder) convertView.getTag();
		}
		if(!list.getItem(position).getFocus_image().equals("")){
			loadBitmap(viewholder.focus_image,position);
		}
		viewholder.focus_title.setText(list.getItem(position).getFocus_title());
		viewholder.focus_abstract.setText(list.getItem(position).getFocus_abstract());
		viewholder.focus_author.setText(list.getItem(position).getFocus_author());
		viewholder.focus_address.setText(list.getItem(position).getFocus_address());

		return convertView;
	}
	
	
	private void loadBitmap(final ImageView focus_image, final int position) {
		String url=list.getItem(position).getFocus_image();
		// ���ڴ滺����ȡͼƬ
		Bitmap bitmap = lruCache.getBitmapFromCahce(url);
		if (bitmap == null) { 
			// �ٴӴ��̻�����ȡͼƬ
			InputStream in = lruCache.getDiskCache(url);
			if (in == null) {
				JSONObject json=new JSONObject();
				try { 
					json.put("tag", 101);
					json.put("number", position);
					json.put("tagpage",3);
					json.put("fileName", url);
					PictureSocket.SendMessage(json);
					
					
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				CallBack<Bitmap> back=new  CallBack<Bitmap>(){

					@Override
					public void response(Bitmap entity) {
						focus_image.setImageBitmap(entity);
					}
				};
				piclist.add(new CallList(position,back)); 
			} else {
				System.out.println("disk cache");
				bitmap = BitmapFactory.decodeStream(in);
				lruCache.addBitmapToCache(url, bitmap);
				if(bitmap!=null){
					focus_image.setImageBitmap(bitmap);
				}
				else{
					focus_image.setImageResource(R.drawable.aa);
				}
			}
		} else {
			System.out.println("memory cache");
			if(bitmap!=null){
				focus_image.setImageBitmap(bitmap);
			}
			else{
				focus_image.setImageResource(R.drawable.aa);
			}
		}
	}
	
	
	

}
