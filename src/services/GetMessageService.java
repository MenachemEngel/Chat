package services;


import java.util.ArrayList;
import java.util.List;

import interfaces.ISucceededResponse;
import interfaces.IUnSucceededResponse;
import Kivun.Infra.DTO.ServiceMessage;
import Kivun.Infra.Interfaces.IDTO;
import Kivun.Infra.Interfaces.IService;
import Kivun.Infra.Interfaces.IServiceMessage;
import DAL.HBaseDAL;
import DAL.HBaseDAL.Users;
import DTOS.DTOGetMessage;
import DTOS.DTOResult;
import DTOS.DTOSendMessage;
import DTOS.DTOSignUp;

public class GetMessageService implements IService{
	DTOGetMessage _dto;
	HBaseDAL _dal;
	IServiceMessage _response;
	DTOResult _dtoResult;
	
	public GetMessageService() {
		// TODO Auto-generated constructor stub
		_dal = new HBaseDAL();
	}
	
	@Override
	public void Execute() {
		// TODO Auto-generated method stub
		_dtoResult = new DTOResult();
		_response = new ServiceMessage();
		List<String> messages = null;
		if(_dto.getUserName().length()<1 || _dto.getChatWith().length()<1){
			_response.set_Handler(IUnSucceededResponse.class);
			_dtoResult.setResult("Username or ChatWith not accepted");
		}else{
			if(_dal.IsUserExists(_dto.getUserName()) && _dal.IsUserExists(_dto.getChatWith())){
				int num = _dal.unreadMessages(buildID(_dto.getUserName(), _dto.getChatWith())+":"+_dto.getUserName());
				messages = _dal.getAllMessages(_dto.getUserName(),buildID(_dto.getUserName(), _dto.getChatWith()));
				System.out.println("Messages => "+messages.toString());
				if(num != 0){
					System.out.println("NUM"+num);
					int l = messages.size() - (num+1);
					messages.add(l, "New message");
				}
			}
			
			
			if(!(_dal.IsUserExists(_dto.getUserName()) || _dal.IsUserExists(_dto.getChatWith()))){
				_response.set_Handler(IUnSucceededResponse.class);
				_dtoResult.setResult("Username or ChatWith does not exist");
			}else{
				if(_dal.GetUserStatus(_dto.getUserName()).equals(Users.Data.Status.ONLINE)){
					_response.set_Handler(ISucceededResponse.class);
					if(messages == null){
						_response.set_Handler(IUnSucceededResponse.class);
						_dtoResult.setResult("Error 404");
					}else if(messages.isEmpty()){
						_dtoResult.setResult("No messages");
					}else{
						_dtoResult.setResult(messages.toString());
					}
				}else{
					_response.set_Handler(IUnSucceededResponse.class);
					_dtoResult.setResult("User is not connected");
				}
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
	public void set_Params(IDTO dto) {
		// TODO Auto-generated method stub
		_dto = (DTOGetMessage)dto; 
	}

	@Override
	public Class<?> get_DTOType() {
		// TODO Auto-generated method stub
		return DTOGetMessage.class;
	}
	
	public String buildID(String id1, String id2){
		String id;
		if(id1.compareTo(id2) < 0){
			System.out.println(id1.compareTo(id2));
			id = id1+":"+id2;
		}else{
			id = id2+":"+id1;
		}
		System.out.println(id);
		return id;
	}
	

}
