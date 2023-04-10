package DTOS;

import Kivun.Infra.DTO.Annotations.GetProperty;
import Kivun.Infra.DTO.Annotations.SetProperty;
import Kivun.Infra.Interfaces.IDTO;

public class DTOMonitor implements IDTO {
	
	private String _userID;
	private String _topic;
	
	@GetProperty(PropName="USERID")
	public String getUserID(){
		return _userID;
	}
	
	@SetProperty(PropName="USERID")
	public void setUserID(String userID){
		this._userID = userID;
	}
	
	@GetProperty(PropName="TOPIC")
	public String getTopic(){
		return _topic;
	}
	
	@SetProperty(PropName="TOPIC")
	public void setTopic(String topic){
		this._topic = topic;
	}
}
