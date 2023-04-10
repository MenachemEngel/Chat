package DTOS;

import Kivun.Infra.DTO.Annotations.GetProperty;
import Kivun.Infra.DTO.Annotations.SetProperty;
import Kivun.Infra.Interfaces.IDTO;

public class DTOFriendsOnline implements IDTO{
	
	private String _userID;

	@GetProperty(PropName="USERID")
	public String getUsers() {
		return _userID;
	}

	@SetProperty(PropName="USERID")
	public void setUsers(String userID) {
		this._userID = userID;
	}
	
	

}
