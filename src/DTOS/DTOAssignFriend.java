package DTOS;

import Kivun.Infra.DTO.Annotations.GetProperty;
import Kivun.Infra.DTO.Annotations.SetProperty;
import Kivun.Infra.Interfaces.IDTO;

public class DTOAssignFriend implements IDTO{

	String _userID1;
	String _userID2;
	
	@GetProperty(PropName="USERID1")
	public String getUserID1() {
		return _userID1;
	}
	
	@SetProperty(PropName="USERID1")
	public void setUserID1(String userID1) {
		this._userID1 = userID1;
	}
	
	@GetProperty(PropName="USERID2")
	public String getUserID2() {
		return _userID2;
	}
	
	@SetProperty(PropName="USERID2")
	public void setUserID2(String userID2) {
		this._userID2 = userID2;
	}
	
	
	
}
