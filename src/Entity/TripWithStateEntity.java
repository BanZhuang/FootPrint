package Entity;

import org.json.JSONException;
import org.json.JSONObject;

public class TripWithStateEntity {

	private String trip_id;        //�г�id
	private String trip_title;     //�г̱���
	private String trip_abstract;  //�г̼��
	private String trip_state;      //�г�״̬
	private String trip_author;    //�г̴�����
	
	public TripWithStateEntity() {}
	
    public TripWithStateEntity(String ti,String tt,String ta,String ts,String tau) {
		
		this.trip_id=ti;
		this.trip_title=tt;
		this.trip_abstract=ta;
		this.trip_state=ts;
		this.trip_author=tau;
	}
    
    
    
    
    
    
    
	public String getTrip_id() {
		return trip_id;
	}

	public void setTrip_id(String trip_id) {
		this.trip_id = trip_id;
	}

	public String getTrip_title() {
		return trip_title;
	}

	public void setTrip_title(String trip_title) {
		this.trip_title = trip_title;
	}

	public String getTrip_abstract() {
		return trip_abstract;
	}

	public void setTrip_abstract(String trip_abstract) {
		this.trip_abstract = trip_abstract;
	}

	public String getTrip_state() {
		return trip_state;
	}

	public void setTrip_state(String trip_state) {
		this.trip_state = trip_state;
	}

	public String getTrip_author() {
		return trip_author;
	}

	public void setTrip_author(String trip_author) {
		this.trip_author = trip_author;
	}

	
    
	
}
