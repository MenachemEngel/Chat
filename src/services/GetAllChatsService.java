package services;

import java.util.List;

import interfaces.ISucceededResponse;
import interfaces.IUnSucceededResponse;
import DAL.HBaseDAL;
import DAL.HBaseDAL.Users;
import DTOS.DTOGetAllChats;
import DTOS.DTOResult;
import Kivun.Infra.DTO.ServiceMessage;
import Kivun.Infra.Interfaces.IDTO;
import Kivun.Infra.Interfaces.IService;
import Kivun.Infra.Interfaces.IServiceMessage;

public class GetAllChatsService implements IService {

	HBaseDAL _dal;
	IServiceMessage _response;
	DTOGetAllChats _dto;
	DTOResult _dtoResult;
	
	public GetAllChatsService() {
		// TODO Auto-generated constructor stub
		_dal = new HBaseDAL();
	}
	
	@Override
	public void set_Params(IDTO dto) {
		// TODO Auto-generated method stub
		_dto = (DTOGetAllChats) dto;
	}

	@Override
	public void Execute() {
		// TODO Auto-generated method stub
		_response = new ServiceMessage();
		_dtoResult = new DTOResult();
		if(_dto.getUserID().length()<1){
			_response.set_Handler(IUnSucceededResponse.class);
			_dtoResult.setResult("UserID not accepted");
		}else{
			if(_dal.IsUserExists(_dto.getUserID())){
				if(_dal.GetUserStatus(_dto.getUserID()).equals(Users.Data.Status.ONLINE)){
					List<String> l = _dal.getAllChats(_dto.getUserID());
					if(l != null){
						_response.set_Handler(ISucceededResponse.class);
						_dtoResult.setResult(l.toString());
					}else{
						_response.set_Handler(IUnSucceededResponse.class);
						_dtoResult.setResult("Error 404");
					}
				}else{
					_response.set_Handler(IUnSucceededResponse.class);
					_dtoResult.setResult("User not connected");
				}
			}else{
				_response.set_Handler(IUnSucceededResponse.class);
				_dtoResult.setResult("UserID not exists");
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
		return DTOGetAllChats.class;
	}

}
