package services;

import java.util.List;

import interfaces.ISucceededResponse;
import interfaces.IUnSucceededResponse;
import DAL.HBaseDAL;
import DAL.HBaseDAL.Users;
import DAL.KafkaDAL;
import DTOS.DTOResult;
import DTOS.DTOSendMsgToTopic;
import Kivun.Infra.DTO.ServiceMessage;
import Kivun.Infra.Interfaces.IDTO;
import Kivun.Infra.Interfaces.IService;
import Kivun.Infra.Interfaces.IServiceMessage;

public class SendMsgToTopicService implements IService {

	DTOSendMsgToTopic _dto;
	IServiceMessage _response;
	DTOResult _dtoResult;
	HBaseDAL _dal;
	KafkaDAL _dalK;
	
	public SendMsgToTopicService() {
		// TODO Auto-generated constructor stub
		_dal = new HBaseDAL();
		_dalK = new KafkaDAL();
	}
	
	@Override
	public void set_Params(IDTO dto) {
		// TODO Auto-generated method stub
		_dto = (DTOSendMsgToTopic) dto;
	}

	@Override
	public void Execute() {
		// TODO Auto-generated method stub
		_dtoResult = new DTOResult();
		_response = new ServiceMessage();
		
		if(_dto.getUserID().length()<1 || _dto.getTopic().length()<1 || _dto.getMessage().length()<1){
			_response.set_Handler(IUnSucceededResponse.class);
			_dtoResult.setResult("UserID or Message or Topic not accepted");
		}else{
			if(_dal.IsUserExists(_dto.getUserID()) || _dal.GetUserStatus(_dto.getUserID()).equals(Users.Data.Status.ONLINE)){
				if(_dalK.isTopicExists(_dto.getTopic()) && _dalK.isRegisteredToTopic(_dto.getUserID(), _dto.getTopic())){
					_response.set_Handler(ISucceededResponse.class);
					_dtoResult.setResult(_dto.getUserID()+" "+_dto.getTopic()+" "+_dto.getMessage());
					final String id = Integer.toString(_dto.getUserID().hashCode());
					Thread cons = new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							_dalK.putTopicMessages(_dto.getTopic(), id);							
						}
					});
					cons.start();
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					_dalK.sendTopicMessage(_dto.getUserID(), _dto.getTopic(), _dto.getMessage());
					cons.interrupt();
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
		return DTOSendMsgToTopic.class;
	}
	
}
