package services;

import constants.Constants;
import interfaces.ISucceededResponse;
import interfaces.IUnSucceededResponse;
import DAL.HBaseDAL;
import DTOS.DTOResult;
import DTOS.DTOSignIn;
import Kivun.Infra.DTO.ServiceMessage;
import Kivun.Infra.Interfaces.IDTO;
import Kivun.Infra.Interfaces.IService;
import Kivun.Infra.Interfaces.IServiceMessage;

public class SignInService implements IService{
	
	DTOSignIn _dto;
	HBaseDAL _dal;
	IServiceMessage _response;
	DTOResult _dtoResult;
	
	public SignInService() {
		// TODO Auto-generated constructor stub
		_dal = new HBaseDAL();
	}

	@Override
	public void set_Params(IDTO dto) {
		// TODO Auto-generated method stub
		_dto = (DTOSignIn)dto;
	}

	@Override
	public void Execute() {
		_dtoResult = new DTOResult();
		_response = new ServiceMessage();
		_dtoResult.setResult(_dto.getEmail());
		if(_dto.getEmail().length()<1 || _dto.getPassword().length()<1){
			_response.set_Handler(IUnSucceededResponse.class);
			_dtoResult.setResult("Email or Password not accepted");
		}else{
			String s = _dto.getPassword();
			System.out.println(s);
			String st;
			try{
				st =_dal.GetPassword(_dto.getEmail());
			}catch(Exception e){
				st = null;
			}
			System.out.println(st);
			boolean pass = s.equals(st);
			System.out.println(pass);
			if(!_dal.IsUserExists(_dto.getEmail())){
				_response.set_Handler(IUnSucceededResponse.class);
				_dtoResult.setResult("User not exists");
			}else{
				if(pass != false && _dal.GetUserStatus(_dto.getEmail()).equals(HBaseDAL.Users.Data.Status.OFFLINE)){
						_response.set_Handler(ISucceededResponse.class);
						_dal.UpdateUserStatus(_dto.getEmail(), HBaseDAL.Users.Data.Status.ONLINE);
						_dtoResult.setResult("User connected now");
				}else{
					_response.set_Handler(IUnSucceededResponse.class);
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
	public Class<?> get_DTOType() {
		// TODO Auto-generated method stub
		return DTOSignIn.class;
	}
	

}
