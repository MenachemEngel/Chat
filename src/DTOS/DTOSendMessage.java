package DTOS;

import Kivun.Infra.DTO.Annotations.GetProperty;
import Kivun.Infra.DTO.Annotations.SetProperty;
import Kivun.Infra.Interfaces.IDTO;

public class DTOSendMessage implements IDTO{

	private String _to;
	private String _message;
	private String _from;
	
	@GetProperty(PropName="TO")
	public String getTo(){
		return _to;
	}
	@SetProperty(PropName="TO")
	public void setTo(String to){
		this._to = to;
	}
	
	@GetProperty(PropName="MESSAGE")
	public String getMessage(){
		return _message;
	}
	@SetProperty(PropName="MESSAGE")
	public void setMessage(String message){
		this._message = message;
	}
	
	@GetProperty(PropName="FROM")
	public String getFrom(){
		return _from;
	}
	@SetProperty(PropName="FROM")
	public void setFrom(String from){
		this._from = from;
	}
	
}
