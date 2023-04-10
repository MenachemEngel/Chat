package DAL;

import java.util.List;

public interface IKafkaDAL {
	
	List<String> getTopicsList();
	boolean isRegisteredToTopic(String userID, String topic);
	boolean isTopicExists(String topic);
	void putTopicMessages(String topic, String userID);
	void sendTopicMessage(String userID, String topic, String msg);
	void createTopic(String topicName);
	
}
