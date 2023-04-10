package services;

import interfaces.ISucceededResponse;
import interfaces.IUnSucceededResponse;
import DAL.HBaseDAL;
import DTOS.DTOResult;
import DTOS.DTOSignOut;
import Kivun.Infra.DTO.ServiceMessage;
import Kivun.Infra.Interfaces.IDTO;
import Kivun.Infra.Interfaces.IService;
import Kivun.Infra.Interfaces.IServiceMessage;

public class SignOutService implements IService{
	
	DTOSignOut _dto;
	HBaseDAL _dal;
	IServiceMessage _response;
	DTOResult _dtoResult;
	
	public SignOutService() {
		// TODO Auto-generated constructor stub
		_dal = new HBaseDAL();
	}

	@Override
	public void set_Params(IDTO dto) {
		// TODO Auto-generated method stub
		_dto = (DTOSignOut)dto;
	}

	@Override
	public void Execute() {
		// TODO Auto-generated method stub
		_dtoResult = new DTOResult();
		_response = new ServiceMessage();
		_dtoResult.setResult(_dto.getEmail());
		String status;
		try{
			status = _dal.GetUserStatus(_dto.getEmail());
		}catch(Exception e){
			status = HBaseDAL.Users.Data.Status.OFFLINE;
		}
		if(_dto.getEmail().length()<1){
			_response.set_Handler(IUnSucceededResponse.class);
			_dtoResult.setResult("Email not accepted");
		}else{
			if(_dal.IsUserExists(_dto.getEmail())){
				if(status.equals(HBaseDAL.Users.Data.Status.OFFLINE)){
					_response.set_Handler(IUnSucceededResponse.class);				
				}else{
					_response.set_Handler(ISucceededResponse.class);
					_dal.UpdateUserStatus(_dto.getEmail(), HBaseDAL.Users.Data.Status.OFFLINE);
				}
			}else{
				_response.set_Handler(IUnSucceededResponse.class);
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
		return DTOSignOut.class;
	}

}
