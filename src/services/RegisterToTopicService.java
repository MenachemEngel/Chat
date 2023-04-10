package services;

import interfaces.ISucceededResponse;
import interfaces.IUnSucceededResponse;

import java.util.List;

import DAL.HBaseDAL;
import DAL.HBaseDAL.Users;
import DAL.KafkaDAL;
import DTOS.DTOGetMessage;
import DTOS.DTORegisterToTopic;
import DTOS.DTOResult;
import Kivun.Infra.DTO.ServiceMessage;
import Kivun.Infra.Interfaces.IDTO;
import Kivun.Infra.Interfaces.IService;
import Kivun.Infra.Interfaces.IServiceMessage;

public class RegisterToTopicService implements IService {

	DTORegisterToTopic _dto;
	HBaseDAL _dal;
	IServiceMessage _response;
	DTOResult _dtoResult;
	KafkaDAL _dalK;
	
	public RegisterToTopicService(){
		_dal = new HBaseDAL();
		_dalK = new KafkaDAL();
	}
	
	@Override
	public void set_Params(IDTO dto) {
		// TODO Auto-generated method stub
		_dto = (DTORegisterToTopic) dto;
	}

	@Override
	public void Execute() {
		// TODO Auto-generated method stub
		_dtoResult = new DTOResult();
		_response = new ServiceMessage();
		if(_dto.getTopic().length()<1 || _dto.getUserID().length()<1){
			_response.set_Handler(IUnSucceededResponse.class);
			_dtoResult.setResult("Topic or UserID not accepted");
		}else{
			if(_dal.IsUserExists(_dto.getUserID()) || _dal.GetUserStatus(_dto.getUserID()).equals(Users.Data.Status.ONLINE)){
				if(isTopicExists()){
					_response.set_Handler(ISucceededResponse.class);
					_dal.registerToTopic(_dto.getTopic(), _dto.getUserID());
					_dtoResult.setResult(_dto.getUserID()+" "+_dto.getTopic());
				}else{
					_response.set_Handler(IUnSucceededResponse.class);
					_dtoResult.setResult("Topic does not exists");
				}
			}else{
				_response.set_Handler(IUnSucceededResponse.class);
				_dtoResult.setResult("User does not exists or not connected");
			}
		}
		_response.set_DTO(_dtoResult);
	}

	@Override
	public IServiceMessage get_Response() {
		// TODO Auto-generated method stub
		return _response;
	}

	@Override
	public Class<?> get_DTOType() {
		// TODO Auto-generated method stub
		return DTORegisterToTopic.class;
	}
	
	public boolean isTopicExists(){
		List<String> topics = _dalK.getTopicsList();
		for(String t:topics){
			if(t.equals(_dto.getTopic())){
				return true;
			}
		}
		return false;
	}

}
