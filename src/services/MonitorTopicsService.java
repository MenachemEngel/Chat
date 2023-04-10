package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kafka.MyConsumer;
import interfaces.ISucceededResponse;
import interfaces.IUnSucceededResponse;
import DAL.HBaseDAL;
import DAL.HBaseDAL.Users;
import DAL.KafkaDAL;
import DTOS.DTOMonitor;
import DTOS.DTOResult;
import Kivun.Infra.DTO.ServiceMessage;
import Kivun.Infra.Interfaces.IDTO;
import Kivun.Infra.Interfaces.IService;
import Kivun.Infra.Interfaces.IServiceMessage;

public class MonitorTopicsService implements IService {

	HBaseDAL _dal;
	KafkaDAL _dalK;
	IServiceMessage _response;
	DTOResult _dtoResult;
	DTOMonitor _dto;
	MyConsumer _consumer;
	
	public MonitorTopicsService() {
		// TODO Auto-generated constructor stub
		_dal = new HBaseDAL();
		_dalK = new KafkaDAL();
	}
	
	@Override
	public void set_Params(IDTO dto) {
		// TODO Auto-generated method stub
		_dto = (DTOMonitor) dto;
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
				if(_dalK.isTopicExists(_dto.getTopic()) && _dal.isManager(_dto.getUserID())){
					_consumer = new MyConsumer(_dto.getTopic());
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					_response.set_Handler(ISucceededResponse.class);
					List<String> wordsList = _consumer.getWordsCountList();
					_dtoResult.setResult("Words count =>"+wordsList+" \n Messages number =>"+_consumer.getAmountMessages());
				}else{
					_response.set_Handler(IUnSucceededResponse.class);
					_dtoResult.setResult("Topic does not exists or you not have permission to access");
				}
			}else{
				_response.set_Handler(IUnSucceededResponse.class);
				_dtoResult.setResult("User does not exists or not connected");
			}
		}
		_response.set_DTO(_dtoResult);
		_consumer.close();
	}

	@Override
	public IServiceMessage get_Response() {
		// TODO Auto-generated method stub
		return _response;
	}

	@Override
	public Class<?> get_DTOType() {
		// TODO Auto-generated method stub
		return DTOMonitor.class;
	}

}
