package DTOS;

import org.json.JSONObject;

import Kivun.Infra.DTO.DTOJSONConverter;
import Kivun.Infra.DTO.Annotations.GetProperty;
import Kivun.Infra.DTO.Annotations.SetProperty;
import Kivun.Infra.Interfaces.IDTO;

public class DTOSignUp implements IDTO{
	
	String _nickName;
	String _email;
	String _password;
	
	@GetProperty(PropName="NICKNAME")
	public String getNickName() {
		return _nickName;
	}
	@SetProperty(PropName="NICKNAME")
	public void setNickName(String nickName) {
		this._nickName = nickName;
	}
	
	@GetProperty(PropName="EMAIL")
	public String getEmail() {
		return _email;
	}
	@SetProperty(PropName="EMAIL")
	public void setEmail(String email) {
		this._email = email;
	}
	
	@GetProperty(PropName="PASSWORD")
	public String getPassword() {
		return _password;
	}
	@SetProperty(PropName="PASSWORD")
	public void setPassword(String password) {
		this._password = password;
	}
	

}
