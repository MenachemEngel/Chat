package services;


import interfaces.ISucceededResponse;
import interfaces.IUnSucceededResponse;
import Kivun.Infra.DTO.ServiceMessage;
import Kivun.Infra.Interfaces.IDTO;
import Kivun.Infra.Interfaces.IService;
import Kivun.Infra.Interfaces.IServiceMessage;
import DAL.HBaseDAL;
import DAL.HBaseDAL.Users;
import DTOS.DTOAssignFriend;
import DTOS.DTOResult;
import DTOS.DTOSignUp;

public class AssignFriendService implements IService{
	DTOAssignFriend _dto;
	HBaseDAL _dal;
	IServiceMessage _response;
	DTOResult _dtoResult;
	
	public AssignFriendService() {
		// TODO Auto-generated constructor stub
		_dal = new HBaseDAL();
	}
	
	@Override
	public void Execute() {
		// TODO Auto-generated method stub
		_dtoResult = new DTOResult();
		_response = new ServiceMessage();
		_dtoResult.setResult("Friends=>"+_dto.getUserID1()+","+_dto.getUserID2());
		if(_dto.getUserID1().length()<1 || _dto.getUserID2().length()<1){
			_response.set_Handler(IUnSucceededResponse.class);
			_dtoResult.setResult("UserID1 or UserID2 not accepted");
		}else{
			if(_dal.IsUserExists(_dto.getUserID1()) && _dal.IsUserExists(_dto.getUserID2())){
				if(_dal.GetUserStatus(_dto.getUserID1()).equals(Users.Data.Status.ONLINE)){
					_response.set_Handler(ISucceededResponse.class);
					_dal.assignFriend(_dto.getUserID1(), _dto.getUserID2());
				}else{
					_response.set_Handler(IUnSucceededResponse.class);
					_dtoResult.setResult("User not connected");
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
	public void set_Params(IDTO dto) {
		// TODO Auto-generated method stub
		_dto = (DTOAssignFriend)dto; 
	}

	@Override
	public Class<?> get_DTOType() {
		// TODO Auto-generated method stub
		return DTOAssignFriend.class;
	}
	

}
