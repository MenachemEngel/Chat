package services;

import interfaces.ISucceededResponse;
import interfaces.IUnSucceededResponse;

import java.util.List;

import kafka.MyConsumer;
import DAL.HBaseDAL;
import DAL.IChatDAL;
import DAL.IKafkaDAL;
import DAL.KafkaDAL;
import DAL.HBaseDAL.Users;
import DTOS.DTOCreateTopic;
import DTOS.DTOResult;
import Kivun.Infra.DTO.DTO;
import Kivun.Infra.DTO.ServiceMessage;
import Kivun.Infra.Interfaces.IDTO;
import Kivun.Infra.Interfaces.IService;
import Kivun.Infra.Interfaces.IServiceMessage;

public class CreateTopicService implements IService {

	IChatDAL _dal;
	IServiceMessage _response;
	DTOResult _dtoResult;
	DTOCreateTopic _dto;
	IKafkaDAL _dalK;
	
	public CreateTopicService() {
		// TODO Auto-generated constructor stub
		_dal = new HBaseDAL();
		_dalK = new KafkaDAL();
	}
	
	@Override
	public void set_Params(IDTO dto) {
		// TODO Auto-generated method stub
		_dto = (DTOCreateTopic) dto;
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
				if((!_dalK.isTopicExists(_dto.getTopic())) && _dal.isManager(_dto.getUserID())){
					_response.set_Handler(ISucceededResponse.class);
					_dalK.createTopic(_dto.getTopic());
				}else{
					_response.set_Handler(IUnSucceededResponse.class);
					_dtoResult.setResult("Topic already exists or you not have permission to access");
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
		return DTOCreateTopic.class;
	}

}
