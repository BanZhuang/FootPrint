package com.Common;

//�ļ���ʽ
import java.io.Serializable;  


/** 
 * �ļ��ϴ����� 
 * @author dujinkai 
 * 
 */  
public class FileUploadRequest implements Serializable  
{  
    /** 
     * ���к� 
     */  
    private static final long serialVersionUID = 1547212123169330600L;  
      
    private String fileName;        //�ļ���
    
    private byte [] bytes;          //�ļ��ֽ� 
    
    private int number;             //�����־
    
    private int tripPlanId;        //�г̰���id
    
    private int tripId ;           //�г�id
    
    private int tagpage;           //�ж��ĸ�ҳ���handler
    
    private int alonght;           //�ļ��ܳ���
	private int imagenamelongth;   //ͼƬ���ֳ���
	private long imagelongth;      //ͼƬ����
    
    public int getAlonght() {
		return alonght;
	}

	public void setAlonght(int alonght) {
		this.alonght = alonght;
	}

	public int getImagenamelongth() {
		return imagenamelongth;
	}

	public void setImagenamelongth(int imagenamelongth) {
		this.imagenamelongth = imagenamelongth;
	}

	public long getImagelongth() {
		return imagelongth;
	}

	public void setImagelongth(long imagelongth) {
		this.imagelongth = imagelongth;
	}

    public int getTagPage() {
		return tagpage;
	}

	public void setTagPage(int tagpage) {
		this.tagpage = tagpage;
	}

	public String getFileName()   
    {  
        return fileName;  
    }  
  
    public void setFileName(String fileName)   
    {  
        this.fileName = fileName;  
    }  
  
    public byte[] getBytes()   
    {  
        return bytes;  
    }  
  
    public void setBytes(byte[] bytes)   
    {  
        this.bytes = bytes;  
    }  
    
    public int getNumber()   
    {  
        return number;  
    }  
  
    public void setNumber(int number)   
    {  
        this.number = number;  
    } 
    
    public int getTripPlanId()   
    {  
        return tripPlanId;  
    }  
  
    public void setTripPlanId(int tripPlanId)   
    {  
        this.tripPlanId = tripPlanId;  
    } 
    
    public int getTripId()   
    {  
        return tripId;  
    }  
  
    public void setTripId(int tripId)   
    {  
        this.tripId = tripId;  
    }  
//    public JSONObject toJson(){
//    	
//    	JSONObject json = new JSONObject();
//    	json.put("fileName", fileName);
//    	json.put("bytes", bytes);
//    	return json;
//    }
//  
//    @Override  
//    public String toString() {  
//        return "FileUploadRequest [toJson()=" + toJson() + "]";  
//    }  
      
      
  
}  
