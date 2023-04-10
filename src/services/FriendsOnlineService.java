package services;

import java.util.ArrayList;
import java.util.List;

import interfaces.ISucceededResponse;
import interfaces.IUnSucceededResponse;
import DAL.HBaseDAL;
import DTOS.DTOFriendsOnline;
import DTOS.DTOResult;
import DTOS.DTOSignOut;
import Kivun.Infra.DTO.ServiceMessage;
import Kivun.Infra.Interfaces.IDTO;
import Kivun.Infra.Interfaces.IService;
import Kivun.Infra.Interfaces.IServiceMessage;

public class FriendsOnlineService implements IService{
	
	DTOFriendsOnline _dto;
	HBaseDAL _dal;
	IServiceMessage _response;
	DTOResult _dtoResult;
	
	
	public FriendsOnlineService() {
		// TODO Auto-generated constructor stub
		_dal = new HBaseDAL();
	}

	@Override
	public void set_Params(IDTO dto) {
		// TODO Auto-generated method stub
		_dto = (DTOFriendsOnline)dto;
	}

	@Override
	public void Execute() {
		// TODO Auto-generated method stub
		_dtoResult = new DTOResult();
		_response = new ServiceMessage();
		
		
		if(_dto.getUsers().length()<1){
			_response.set_Handler(IUnSucceededResponse.class);
			_dtoResult.setResult("Username not accepted");
		}else{
			List<String> users = _dal.getOnlineFriends(_dto.getUsers());
			System.out.println("USERS==>>"+users.toString());
			List<String> usersOnLine = new ArrayList<String>();
			
			for(int i = 0; i < users.size(); i++){
				String user = users.get(i);
				if(_dal.GetUserStatus(user).equals(HBaseDAL.Users.Data.Status.ONLINE)){
					if(!(user.equals(_dto.getUsers()))){
						usersOnLine.add(user);											
					}
				}
			}	
			if(_dal.IsUserExists(_dto.getUsers())){
				if(_dal.GetUserStatus(_dto.getUsers()).equals(HBaseDAL.Users.Data.Status.ONLINE)){
					if(usersOnLine.isEmpty()){
						_response.set_Handler(IUnSucceededResponse.class);
						_dtoResult.setResult("No users connected");
						
					}else{
						_response.set_Handler(ISucceededResponse.class);
						_dto.setUsers(_dto.getUsers()+" ==>> "+usersOnLine.toString());
						_dtoResult.setResult(_dto.getUsers());
						
					}
				}else{
					_response.set_Handler(IUnSucceededResponse.class);
					_dtoResult.setResult("User not connected");
					
				}
			}else{
				_response.set_Handler(IUnSucceededResponse.class);
				_dtoResult.setResult("User not exists");
				
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
		return DTOFriendsOnline.class;
	}
	
	
}
