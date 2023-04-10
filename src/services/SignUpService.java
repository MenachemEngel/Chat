package services;


import interfaces.ISucceededResponse;
import interfaces.IUnSucceededResponse;
import Kivun.Infra.DTO.ServiceMessage;
import Kivun.Infra.Interfaces.IDTO;
import Kivun.Infra.Interfaces.IService;
import Kivun.Infra.Interfaces.IServiceMessage;
import DAL.HBaseDAL;
import DTOS.DTOResult;
import DTOS.DTOSignUp;

public class SignUpService implements IService{
	DTOSignUp _dto;
	HBaseDAL _dal;
	IServiceMessage _response;
	DTOResult _dtoResult;
	
	public SignUpService() {
		// TODO Auto-generated constructor stub
		_dal = new HBaseDAL();
	}
	
	@Override
	public void Execute() {
		// TODO Auto-generated method stub
		_dtoResult = new DTOResult();
		_response = new ServiceMessage();
		_dtoResult.setResult(_dto.getEmail());
		if(_dto.getEmail().length()<1 || _dto.getNickName().length()<1 || _dto.getPassword().length()<1){
			_response.set_Handler(IUnSucceededResponse.class);
			_dtoResult.setResult("Email or NickName or Password not accepted");
		}else{
			if(_dal.IsUserExists(_dto.getEmail())){
				_response.set_Handler(IUnSucceededResponse.class);
				_dtoResult.setResult("User already exists");
			}else{
				_response.set_Handler(ISucceededResponse.class);
				_dal.RegisterUser(_dto.getEmail(), _dto.getNickName(), _dto.getPassword());
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
		_dto = (DTOSignUp)dto; 
	}

	@Override
	public Class<?> get_DTOType() {
		// TODO Auto-generated method stub
		return DTOSignUp.class;
	}
	

}
