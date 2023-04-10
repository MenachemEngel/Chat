package DTOS;

import Kivun.Infra.DTO.Annotations.GetProperty;
import Kivun.Infra.DTO.Annotations.SetProperty;
import Kivun.Infra.Interfaces.IDTO;

public class DTOGetMessage implements IDTO {
	
	String _userName;
	String _chatWith;
	
	@GetProperty(PropName="USERNAME")
	public String getUserName(){
		return _userName;
	}
	@SetProperty(PropName="USERNAME")
	public void setUserName(String userName){
		this._userName = userName;
	}

	@GetProperty(PropName="CHAT-WITH")
	public String getChatWith(){
		return _chatWith;
	}
	@SetProperty(PropName="CHAT-WITH")
	public void setChatWith(String chatWith){
		this._chatWith = chatWith;
	}
	
}
