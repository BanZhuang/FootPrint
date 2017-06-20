package com.KeepAlive;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

/**
* @see ����������������
* getResponse()---->isResponse();��ȡ�����ж������¼���Ŀ�����ж��Ƿ񴥷�������ʱ�쳣��
* isRequest()----->getRequest(); д�������������¼����������ݣ�Ŀ��д�ظ����������ͻ��ˣ���������
* @author Herman.Xiong
*/
public class ClientKeepAliveImp implements KeepAliveMessageFactory{
	
	//����������
   private static final String HEARTBEATREQUEST = "HEARTBEATREQUEST";          //����
   private static final String HEARTBEATRESPONSE = "HEARTBEATRESPONSE";        //����
   
	/**
    * @see ���ظ��ͻ��˵����������� return ���ؽ�����ǿͻ����յ�������������
    * @author Herman.Xiong
    */
   //�ͻ��������
   public Object getRequest(IoSession session) {
//       return HEARTBEATREQUEST;
	   return HEARTBEATREQUEST;
   }

   /**
    * @see ���ܵ��Ŀͻ������ݰ�
    * @author Herman.Xiong
    */
   //�ͻ��˲��÷���������
   public Object getResponse(IoSession session, Object request) {
//       return request;
	   return null;
   }

   /**
    * @see �ж��Ƿ��ǿͻ��˷������ĵ����������ж�Ӱ�� KeepAliveRequestTimeoutHandlerʵ�����ж��Ƿ����������ͳ�ʱ
    * @author Herman.Xiong
    */
   public boolean isRequest(IoSession session, Object message) {
//       if(message.equals(HEARTBEATREQUEST)){
//           System.out.println("���յ��ͻ��˷�����������" + message);
//	        return true;
//	    }
       return false;
   }

   /**
    * @see  �жϷ�����Ϣ�Ƿ����������ݰ����ж�Ӱ�� KeepAliveRequestTimeoutHandlerʵ���� �ж��Ƿ����������ͳ�ʱ
    * @author Herman.Xiong
    */
   public boolean isResponse(IoSession session, Object message) {
       if(message.equals(HEARTBEATRESPONSE)){
           System.out.println("���յ��������ķ���: " + message);
           return true;
       }
       return false;
   }
}
