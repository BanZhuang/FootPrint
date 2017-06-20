package com.Activity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import com.Lru.DiskLruCache;
import com.Lru.LruCacheUtils;
import com.Tool.CircleImageView;
import com.Tool.SPUtils;
import com.Tool.ToastUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityChangeUserInfo extends Activity{

	private ImageView back;
	private CircleImageView userimage;
	private RelativeLayout myimage,myname,mysex;
	private TextView nickname,sex;
	private String imageurl;
	private Bitmap photo;
	
	//���ݸ��»�ȡ
	public static Handler mhander;
	private LruCacheUtils lruCache;
	
	/* ͷ���ļ� */
	private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
	/* ����ʶ���� */
	private static final int CODE_GALLERY_REQUEST = 0xa0;
	private static final int CODE_CAMERA_REQUEST = 0xa1;
	private static final int CODE_RESULT_REQUEST = 0xa2;
	// �ü���ͼƬ�Ŀ�(X)�͸�(Y),480 X 480�������Ρ�
	private static int output_X = 480;
	private static int output_Y = 480;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_changeuserinfo);
		lruCache = LruCacheUtils.getInstance();
		imageurl=(String) SPUtils.get(this, "userPicture", "");
		initView();
		setListen();
		updateUI();
	}


	private void setListen() {
		// TODO Auto-generated method stub
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		mysex.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ActivityChangeUserInfo.this);
                builder.setTitle("��ѡ���Ա�");		
                final String[] sexxx = {"��", "Ů"};
                builder.setItems(sexxx, new DialogInterface.OnClickListener() {
  					@Override
  					public void onClick(DialogInterface dialog, int which) {
  						// TODO Auto-generated method stub
  						switch (which) {
						case 0:
							sex.setText("��");
							break;
						case 1:
							sex.setText("Ů");
							break;
						default:
							break;
						}
  						
  					}
  		});	
                builder.show();
			}
		});
		myname.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				   final EditText et=new EditText(ActivityChangeUserInfo.this);
				   et.setBackgroundColor(Color.TRANSPARENT);
	  				AlertDialog.Builder builder = new AlertDialog.Builder(ActivityChangeUserInfo.this);
	  			    builder.setTitle("�������ǳ�")
	  			    .setView(et)
	  			    .setNegativeButton("ȡ��", null)
	  			    .setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
	  			    @Override
	  			    
	  			public void onClick(DialogInterface dialog,int which){
	  			    	 String str = et.getText().toString();
	  			    	
	  			    	if(str.equals("")){
	  			    		Toast.makeText(ActivityChangeUserInfo.this, "�������Ϊ��",Toast.LENGTH_SHORT).show();
	  			    	}
	  			    	else
	  			    		nickname.setText(str);
	  			    }
	  			    }).show();	
			}
		});
		myimage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String[] arrayContestLevel = new String[] {
						"�������", "�ֻ����" };

				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						ActivityChangeUserInfo.this).setTitle("ѡ��ͷ��")
						.setItems(arrayContestLevel,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(
											DialogInterface dialog,
											int which) { // TODO
															// Auto-generated
															// method
															// stub
										switch (which) {
										case 0:
											Toast.makeText(
													ActivityChangeUserInfo.this,
													"�������",
													Toast.LENGTH_SHORT)
													.show();
											choseHeadImageFromCameraCapture();
											break;
										case 1:
											Toast.makeText(
													ActivityChangeUserInfo.this,
													"�ֻ����",
													Toast.LENGTH_SHORT)
													.show();
											choseHeadImageFromGallery();
										default:
											break;
										}
										dialog.cancel();
									}
								});
				alertDialog.create().show();
			}
		});
		
	}


	private void initView() {
		// TODO Auto-generated method stub
		back=(ImageView) findViewById(R.id.back_image);
		userimage=(CircleImageView) findViewById(R.id.userhead);
		myimage=(RelativeLayout) findViewById(R.id.myimage);
		myname=(RelativeLayout) findViewById(R.id.myname);
		mysex=(RelativeLayout) findViewById(R.id.mysex);
		nickname=(TextView) findViewById(R.id.nickname);
		sex=(TextView) findViewById(R.id.sex);
		
		nickname.setText(SPUtils.get(this, "userNickname", "").toString());
		lruCache.loadBitmap(userimage,imageurl,10);
		
		
	}
	
	private void updateUI() {
		// TODO Auto-generated method stub
		mhander=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==1){
					Bitmap bitmap=(Bitmap) msg.obj;
					
					String url=imageurl;
					String key = lruCache.hashKeyForDisk(url);
	                DiskLruCache.Editor editor = null;
	                userimage.setImageBitmap(bitmap);
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
				}
				
			}
		};
	}
	private void choseHeadImageFromGallery() {
	        Intent intentFromGallery = new Intent();
	        // �����ļ�����
	        intentFromGallery.setType("image/*");
	        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
	        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
	    }
	// �����ֻ����������Ƭ��Ϊͷ��
    private void choseHeadImageFromCameraCapture() {
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
 
        // �жϴ洢���Ƿ���ã��洢��Ƭ�ļ�
        if (hasSdcard()) {
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                    .fromFile(new File(Environment
                            .getExternalStorageDirectory(), IMAGE_FILE_NAME)));
        }
 
        startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
    }
	 
	 
	 @Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			// �û�û�н�����Ч�����ò���������
			if (resultCode == RESULT_CANCELED) {
				ToastUtil.show(ActivityChangeUserInfo.this, "ȡ��");
				return;
			}
			switch (requestCode) {
			case CODE_GALLERY_REQUEST:

	            cropRawPhoto(data.getData());
	            break;

			case CODE_CAMERA_REQUEST:
				if (hasSdcard()) {
					File tempFile = new File(
							Environment.getExternalStorageDirectory(),
							IMAGE_FILE_NAME);
					cropRawPhoto(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(getApplication(), "û��SDCard!", Toast.LENGTH_LONG)
							.show();
				}
				break;

			case CODE_RESULT_REQUEST:
				if (data != null) {
					setImageToHeadView(data);
				}
				break;

			default:
				break;
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
	 /**
	     * ����豸�Ƿ����SDCard�Ĺ��߷���
	     */
	    public static boolean hasSdcard() {
	        String state = Environment.getExternalStorageState();
	        if (state.equals(Environment.MEDIA_MOUNTED)) {
	            // �д洢��SDCard
	            return true;
	        } else {
	            return false;
	        }
	    }
	    /**
	     * �ü�ԭʼ��ͼƬ
	     */
	    public void cropRawPhoto(Uri uri) {
	 
	        Intent intent = new Intent("com.android.camera.action.CROP");
	        intent.setDataAndType(uri, "image/*");
	 
	        // ���òü�
	        intent.putExtra("crop", "true");
	 
	        // aspectX, aspectY:��ߵı���
	        intent.putExtra("aspectX", 1);
	        intent.putExtra("aspectY", 1);
	 
	        // outputX , outputY : �ü�ͼƬ���
	        intent.putExtra("outputX", output_X);
	        intent.putExtra("outputY", output_Y);
	       // intent.putExtra("return-data", true);
	        File bitmapFile = new File(Environment
	                .getExternalStorageDirectory(), IMAGE_FILE_NAME);
	        Uri uritempFile = Uri.fromFile(bitmapFile);
	        // Uri uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" +IMAGE_FILE_NAME);  
	        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);  
	        //intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); 
	        startActivityForResult(intent, CODE_RESULT_REQUEST);
	    }
	    /**
	     * ��ȡ����ü�֮���ͼƬ���ݣ�������ͷ�񲿷ֵ�View
	     */
	    private void setImageToHeadView(Intent intent) {
	        Bundle extras = intent.getExtras();
	        if (extras != null) {
	        	File bitmapFile = new File(Environment
	                    .getExternalStorageDirectory(), IMAGE_FILE_NAME);
	        	
	        	photo =  BitmapFactory.decodeFile(bitmapFile.getPath());
	        	System.out.println("-------------------"+photo.getByteCount());
	        	
	        	
	        	
	        	userimage.setImageBitmap(photo);
	        }
	    }

}
