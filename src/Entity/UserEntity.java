package Entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.Tool.SPUtils;

import android.content.Context;


public class UserEntity {

	private	int	UserId	;//	�û�id
	private	String	UserName	;//	�û�����
	private	String	UserPicture	;//	�û�ͷ��
	private	String	UserPhone	;//	�û��绰
	private String  UserAccount ;// �û��˻�
	private	String	UserPassword	;//	�û�����
	private	String	UserNickname	;//	�û��ǳ�
	private	String	UserSex	;//	�û��Ա�
	private	String	Userbirth	;//	�û�����
	private	String	UserCity	;//	�û���ס����
	private	String	UserAddress	;//	�û���ַ
	private	String	UserRegisterTime	;//	�û�ע��ʱ��
	private	String	UserEmail	;//	�û�����
	private	int	UserIntegral	;//	�û�����
	private	String	UserSignature	;//	�û�����ǩ��
	private	int	UserState	;//	�û�״̬
	
	public UserEntity() {}

	public UserEntity(JSONObject json) {
		try {
			this.UserState=json.getInt("userState");
			this.UserIntegral=json.getInt("userIntegral");
			this.UserNickname=json.getString("userNickname");
			if(json.has("userPicture")){
				this.UserPicture=json.getString("userPicture");
			}
			this.UserId=json.getInt("userId");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public JSONObject ToJSON(int tag,String account,String password){
		JSONObject json=new JSONObject();
		try {
			json.put("tag", tag);
			json.put("userAccount", account);
			json.put("userPassword", password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
	public void saveUserInfo(Context context, UserEntity UserEntity) {
		SPUtils.put(context, "userId", UserEntity.getUserId());
		SPUtils.put(context, "userAccount", UserEntity.getUserAccount());
		SPUtils.put(context, "userPassword", UserEntity.getUserPassword());
		SPUtils.put(context, "userNickname", UserEntity.getUserNickname());
		SPUtils.put(context, "userState", UserEntity.getUserState());
		SPUtils.put(context, "userIntegral", UserEntity.getUserIntegral());
		SPUtils.put(context, "userPicture", UserEntity.getUserPicture());

	}
	
	
	
	
	
	
	
	
	
	
	public int getUserId() {
		return UserId;
	}

	public void setUserId(int userId) {
		UserId = userId;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getUserPicture() {
		return UserPicture;
	}

	public void setUserPicture(String userPicture) {
		UserPicture = userPicture;
	}

	public String getUserPhone() {
		return UserPhone;
	}

	public void setUserPhone(String userPhone) {
		UserPhone = userPhone;
	}

	public String getUserAccount() {
		return UserAccount;
	}

	public void setUserAccount(String userAccount) {
		UserAccount = userAccount;
	}

	public String getUserPassword() {
		return UserPassword;
	}

	public void setUserPassword(String userPassword) {
		UserPassword = userPassword;
	}

	public String getUserNickname() {
		return UserNickname;
	}

	public void setUserNickname(String userNickname) {
		UserNickname = userNickname;
	}

	public String getUserSex() {
		return UserSex;
	}

	public void setUserSex(String userSex) {
		UserSex = userSex;
	}

	public String getUserbirth() {
		return Userbirth;
	}

	public void setUserbirth(String userbirth) {
		Userbirth = userbirth;
	}

	public String getUserCity() {
		return UserCity;
	}

	public void setUserCity(String userCity) {
		UserCity = userCity;
	}

	public String getUserAddress() {
		return UserAddress;
	}

	public void setUserAddress(String userAddress) {
		UserAddress = userAddress;
	}

	public String getUserRegisterTime() {
		return UserRegisterTime;
	}

	public void setUserRegisterTime(String userRegisterTime) {
		UserRegisterTime = userRegisterTime;
	}

	public String getUserEmail() {
		return UserEmail;
	}

	public void setUserEmail(String userEmail) {
		UserEmail = userEmail;
	}

	public int getUserIntegral() {
		return UserIntegral;
	}

	public void setUserIntegral(int userIntegral) {
		UserIntegral = userIntegral;
	}

	public String getUserSignature() {
		return UserSignature;
	}

	public void setUserSignature(String userSignature) {
		UserSignature = userSignature;
	}

	public int getUserState() {
		return UserState;
	}

	public void setUserState(int userState) {
		UserState = userState;
	}
	
	
	
	
	
}
