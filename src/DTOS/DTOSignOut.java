package DTOS;

import Kivun.Infra.DTO.Annotations.GetProperty;
import Kivun.Infra.DTO.Annotations.SetProperty;
import Kivun.Infra.Interfaces.IDTO;

public class DTOSignOut implements IDTO{
	
	String _email;

	@GetProperty(PropName="EMAIL")
	public String getEmail() {
		return _email;
	}
	@SetProperty(PropName="EMAIL")
	public void setEmail(String email) {
		this._email = email;
	}
	
}
