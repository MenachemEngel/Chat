package DTOS;

import Kivun.Infra.DTO.Annotations.GetProperty;
import Kivun.Infra.DTO.Annotations.SetProperty;
import Kivun.Infra.Interfaces.IDTO;

public class DTOSendMsgToTopic implements IDTO{

	String _userID;
	String _topic;
	String _message;
	
	@GetProperty(PropName="USERID")
	public String getUserID() {
		return _userID;
	}
	
	@SetProperty(PropName="USERID")
	public void setUserID(String userID) {
		this._userID = userID;
	}
	
	@GetProperty(PropName="TOPIC")
	public String getTopic() {
		return _topic;
	}
	
	@SetProperty(PropName="TOPIC")
	public void setTopic(String topic) {
		this._topic = topic;
	}
	
	@GetProperty(PropName="MESSAGE")
	public String getMessage() {
		return _message;
	}
	
	@SetProperty(PropName="MESSAGE")
	public void setMessage(String message) {
		this._message = message;
	}
	
	
	
}
