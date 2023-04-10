package services;


import interfaces.ISucceededResponse;
import interfaces.IUnSucceededResponse;
import Kivun.Infra.DTO.ServiceMessage;
import Kivun.Infra.Interfaces.IDTO;
import Kivun.Infra.Interfaces.IService;
import Kivun.Infra.Interfaces.IServiceMessage;
import DAL.HBaseDAL;
import DTOS.DTOResult;
import DTOS.DTOSendMessage;
import DTOS.DTOSignUp;

public class SendMessageService implements IService{
	DTOSendMessage _dto;
	HBaseDAL _dal;
	IServiceMessage _response;
	DTOResult _dtoResult;
	
	public SendMessageService() {
		// TODO Auto-generated constructor stub
		_dal = new HBaseDAL();
	}
	
	@Override
	public void Execute() {
		// TODO Auto-generated method stub
		_dtoResult = new DTOResult();
		_response = new ServiceMessage();
		if(_dto.getFrom().length()<1 || _dto.getMessage().length()<1 || _dto.getTo().length()<1){
			_response.set_Handler(IUnSucceededResponse.class);
			_dtoResult.setResult("Form or Message or To not accepted");
		}else{
			if(!(_dal.IsUserExists(_dto.getTo()) && _dal.IsUserExists(_dto.getFrom()))){
				_response.set_Handler(IUnSucceededResponse.class);
				_dtoResult.setResult("User not exists");
			}else{
				if(_dal.GetUserStatus(_dto.getFrom()).equals(HBaseDAL.Users.Data.Status.ONLINE)){
					String msg = "From:"+_dto.getFrom()+", To:"+_dto.getTo()+", Message:"+_dto.getMessage();
					_dtoResult.setResult(msg);
					_response.set_Handler(ISucceededResponse.class);
					_dal.sendMessage(_dto.getTo(), _dto.getMessage(), _dto.getFrom());
				}else{
					_response.set_Handler(IUnSucceededResponse.class);
					_dtoResult.setResult("User not connected");
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
		_dto = (DTOSendMessage)dto; 
	}

	@Override
	public Class<?> get_DTOType() {
		// TODO Auto-generated method stub
		return DTOSendMessage.class;
	}
	

}
