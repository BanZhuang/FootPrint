package com.Lru;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import com.Activity.R;
import com.Bll.PictureSocket;

/**
 * descreption:���湤����
 * company:
 * Created by vince on 15/3/27.
 */
public class LruCacheUtils {
    private static LruCacheUtils lruCacheUtils;

    public DiskLruCache diskLruCache;  //LRU���̻���
    private LruCache<String,Bitmap> lruCache; //LRU�ڴ滺��
    private Context context;
    private int BITMAP_WIDTH;
	private int BITMAP_HEIGHT;


    private LruCacheUtils(){}

    public static LruCacheUtils getInstance(){
        if (lruCacheUtils ==null){
            lruCacheUtils = new LruCacheUtils();
        }
        return lruCacheUtils;
    }
    
    public void setBitmapSize(int w, int h){
		this.BITMAP_HEIGHT = h;
		this.BITMAP_WIDTH = w;
	}

    public void open(Context context,String disk_cache_subdir,int disk_cache_size){
        try {
            this.context = context;
            //��ȡ��ǰactivity�ڴ��С
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            int memoryClass = am.getMemoryClass();
            lruCache = new LruCache<String, Bitmap>(memoryClass/8 * 1024 * 1024);//�ֽ�  8/1���ڴ���Ϊ�����С��ͨ����
            //open()���������ĸ���������һ������ָ���������ݵĻ����ַ��
            // �ڶ�������ָ����ǰӦ�ó���İ汾�ţ�
            // ����������ָ��ͬһ��key���Զ�Ӧ���ٸ������ļ����������Ǵ�1��
            // ���ĸ�����ָ�������Ի�������ֽڵ����ݡ�ͨ��10MB
            diskLruCache = DiskLruCache.open(getCacheDir(disk_cache_subdir), getAppVersion(), 1, disk_cache_size);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //��ӻ���
    public void addBitmapToCache(String url,Bitmap bitmap){
        String key = hashKeyForDisk(url);
        if(getBitmapFromCahce(key)==null){
            System.out.println("key===="+key);
            System.out.println("bitmap===="+bitmap);
            lruCache.put(key,bitmap);
        }
    }

    //�ӻ����ж�ȡ����
    public Bitmap getBitmapFromCahce(String url){
        String key = hashKeyForDisk(url);
        return lruCache.get(key);
    }

    /**
     * ��ȡ���̻���
     * @param url
     * @return
     */
    public InputStream getDiskCache(String url) {
        String key = hashKeyForDisk(url);
        System.out.println("getDiskCache="+key);
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
            System.out.println(snapshot);
            if (snapshot!=null){
                return snapshot.getInputStream(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ����ͼƬ�����浽�ڴ�ʹ���
     * @param url
     * @param callBack
     */
    public void putCache(final String url, final CallBack callBack){
        new AsyncTask<String,Void,Bitmap>(){
            @Override
            protected Bitmap doInBackground(String... params) {
                String key = hashKeyForDisk(params[0]);
                System.out.println("key="+key);
                DiskLruCache.Editor editor = null;
                Bitmap bitmap = null;
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(1000 * 30);
                    conn.setConnectTimeout(1000 * 30);
                    ByteArrayOutputStream baos = null;
                    if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                        BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                        baos = new ByteArrayOutputStream();
                        byte[] bytes = new byte[1024];
                        int len = -1;
                        while((len=bis.read(bytes))!=-1){
                            baos.write(bytes,0,len);
                        }
                        bis.close();
                        baos.close();
                        conn.disconnect();
                    }
                    if (baos!=null){
                        bitmap = decodeSampledBitmapFromStream(baos.toByteArray(),100,100);
                        addBitmapToCache(params[0],bitmap);
                        editor = diskLruCache.edit(key);
                        System.out.println(url.getFile());
                        //λͼѹ���������������ѹ����ʽ������(100��ʾ��ѹ����30��ʾѹ��70%)���������
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, editor.newOutputStream(0));
                        editor.commit();//�ύ
                    }
                } catch (IOException e) {
                    try {
                        editor.abort();//����д��
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                callBack.response(bitmap);
            }
        }.execute(url);
    }
    
    public void loadBitmap(ImageView imageview, String url,int tagpage) {
		// ���ڴ滺����ȡͼƬ
		Bitmap bitmap = getBitmapFromCahce(url);
		if (bitmap == null) { 
			// �ٴӴ��̻�����ȡͼƬ
			InputStream in = getDiskCache(url);
			if (in == null) {
				JSONObject json=new JSONObject();
				try { 
					json.put("tag", 101);
					json.put("number", 1);
					json.put("tagpage",tagpage);
					json.put("fileName", url);
					PictureSocket.SendMessage(json);
					
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("disk cache");
				bitmap = BitmapFactory.decodeStream(in);
				addBitmapToCache(url, bitmap);
				if(bitmap!=null){
					imageview.setImageBitmap(bitmap);
				}
				else{
					imageview.setImageResource(R.drawable.noneimage);
				}
			}
		} else {
			System.out.println("memory cache");
			if(bitmap!=null){
				imageview.setImageBitmap(bitmap);
			}
			else{
				imageview.setImageResource(R.drawable.noneimage);
			}
		}
	}


public float getLruSize(){
		
		if(diskLruCache != null){
			return diskLruCache.size();
		}else {
			return 0;
		}
	}

    public void delete(){
		if(diskLruCache != null){
			try {
				diskLruCache.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
    
    public void close(){
        if (diskLruCache!=null && !diskLruCache.isClosed()){
            try {
                diskLruCache.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void flush(){
        if (diskLruCache!=null){
            try {
                diskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface CallBack<T>{
        public void response(T entity);
    }

//    public interface

    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight){
        //��ȡλͼ��ԭ���
        int w = options.outWidth;
        int h = options.outHeight;
        int inSampleSize = 1;
        if(w>reqWidth || h>reqHeight){
            if(w>h){
                inSampleSize = Math.round((float)h / (float)reqHeight);
            }else{
                inSampleSize = Math.round((float)w / (float)reqWidth);
            }
        }
        return inSampleSize;
    }


    public static Bitmap decodeSampledBitmapFromStream(byte[] bytes,int reqWidth,int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    /**
     * MD5����ժҪ
     * @param key
     * @return
     */
    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }


    /**
     * ��ȡ�汾��
     * @return
     */
    private int getAppVersion() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    //��ȡ����Ŀ¼
    private File getCacheDir(String name) {
        String cachePath = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED || !Environment.isExternalStorageRemovable() ?
                context.getExternalCacheDir().getPath() : context.getCacheDir().getPath();
        name = cachePath + File.separator + name;
        System.out.println(name);
        return new File(name);
    }
}


















