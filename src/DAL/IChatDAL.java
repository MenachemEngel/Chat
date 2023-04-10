package DAL;

import java.util.List;

public interface IChatDAL {
	boolean IsUserExists(String username);
	void RegisterUser(String username,String name,String password);
	String GetPassword(String username);
	void UpdateUserStatus(String username, String status); 
	String GetUserStatus(String username);
	void assignFriend(String userID1, String userID2);
	List<String> getAllFriends(String username);
	List<String> getOnlineFriends(String username);
	List<String> getAllMessages(String username ,String chatID);
	void sendMessage(String to ,String message ,String from);
	int getNumberOfMessages(String id);
	void setCounterRead(String id, int num);
	void setCounterCurrent(String id, int num);
	int getCounterCurrent(String id);
	int getCounterReader(String id);
	int unreadMessages(String id);
	void registerToTopic(String topic, String userID);
	List<String> getAllChats(String userID);
	List<String> getAllTopicsUsers(String userID);
	String getUserName(String userID);
	boolean isManager(String userID);
	boolean isRowExists(String rowID);
	void putMessageInTable(String topic, String msg);
	List<String> getAllTopicMessages(String userID, String topic);
	
}
