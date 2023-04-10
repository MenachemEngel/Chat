package kafka;

import java.util.List;

public interface IConsumer {
	
	void close();
	List<String> getAllTopicMessages();
	int getAmountMessages();
	List<String> getWordsCountList();
	void setGroupID(String groupID);
	void setTopic(String topic);
	
}
