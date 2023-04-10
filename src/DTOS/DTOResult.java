package DTOS;

import org.json.JSONObject;

import Kivun.Infra.DTO.DTOJSONConverter;
import Kivun.Infra.DTO.Annotations.GetProperty;
import Kivun.Infra.DTO.Annotations.SetProperty;
import Kivun.Infra.Interfaces.IDTO;

public class DTOResult implements IDTO {
	
	String _result;
	
	@GetProperty(PropName="RESULT")
	public String getResult() {
		return _result;
	}
	@SetProperty(PropName="RESULT")
	public void setResult(String result) {
		this._result = result;
	}
	
	@Override
	public String toString() {
		return _result;
	}

}
