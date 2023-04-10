package services;

import java.util.List;

import kafka.MyConsumer;
import interfaces.ISucceededResponse;
import interfaces.IUnSucceededResponse;
import DAL.HBaseDAL;
import DAL.HBaseDAL.Users;
import DAL.KafkaDAL;
import DTOS.DTOGetTopicMessage;
import DTOS.DTORegisterToTopic;
import DTOS.DTOResult;
import Kivun.Infra.DTO.ServiceMessage;
import Kivun.Infra.Interfaces.IDTO;
import Kivun.Infra.Interfaces.IService;
import Kivun.Infra.Interfaces.IServiceMessage;

public class GetTopicMessageService implements IService {

	DTOGetTopicMessage _dto;
	DTOResult _dtoResult;
	HBaseDAL _dal;
	IServiceMessage _response;
	KafkaDAL _dalK;
	
	public GetTopicMessageService() {
		// TODO Auto-generated constructor stub
		_dal = new HBaseDAL();
		_dalK = new KafkaDAL();
	}
	
	@Override
	public void set_Params(IDTO dto) {
		// TODO Auto-generated method stub
		_dto = (DTOGetTopicMessage) dto;
	}

	@Override
	public void Execute() {
		// TODO Auto-generated method stub
		_dtoResult = new DTOResult();
		_response = new ServiceMessage();
		List<String> messages = null;
		if(_dto.getUserID().length()<1 || _dto.getTopic().length()<1){
			_response.set_Handler(IUnSucceededResponse.class);
			_dtoResult.setResult("UserID or Topic not accepted");
		}else{
			if(_dal.IsUserExists(_dto.getUserID()) || _dal.GetUserStatus(_dto.getUserID()).equals(Users.Data.Status.ONLINE)){
				if(_dalK.isTopicExists(_dto.getTopic()) && _dalK.isRegisteredToTopic(_dto.getUserID(), _dto.getTopic())){
					
					_response.set_Handler(ISucceededResponse.class);
					messages = _dal.getAllTopicMessages(_dto.getUserID(), _dto.getTopic());
					_dtoResult.setResult(_dto.getUserID()+" "+_dto.getTopic()+ "\n"+messages);
				}else{
					_response.set_Handler(IUnSucceededResponse.class);
					_dtoResult.setResult("Topic does not exists or you not registered to");
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
		return DTOGetTopicMessage.class;
	}
	
}
