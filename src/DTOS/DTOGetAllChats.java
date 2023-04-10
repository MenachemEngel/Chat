package DTOS;

import Kivun.Infra.DTO.Annotations.GetProperty;
import Kivun.Infra.DTO.Annotations.SetProperty;
import Kivun.Infra.Interfaces.IDTO;

public class DTOGetAllChats implements IDTO {

	private String _userID;
	
	@GetProperty(PropName="USERID")
	public String getUserID(){
		return _userID;
	}
	
	@SetProperty(PropName="USERID")
	public void setUserID(String userID){
		this._userID = userID;
	}
	
}
