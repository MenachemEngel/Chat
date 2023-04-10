package kafka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import DAL.HBaseDAL;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

public class MyConsumer implements IConsumer{

	private ConsumerConnector _consumer;
	private List<String> _messagesTopicList;
	private HashMap<String, Integer> _wordsCounter;
	private int _count;
	private String _topic;
	private String _groupid;
	
	
	private Thread _consumerThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("RUN");
			HashMap<String,Integer> topicCountMap = 
					new HashMap<String,Integer>();
			topicCountMap.put(_topic, 1);
			Map<String,List<KafkaStream<byte[],byte[]>>> 
			consumerMap = _consumer.createMessageStreams(topicCountMap);
			KafkaStream<byte[],byte[]> stream = consumerMap.get(_topic).get(0);
			ConsumerIterator<byte[],byte[]> it =  stream.iterator();
			System.out.println("Waiting");
			while(it.hasNext()){
				MessageAndMetadata<byte[],byte[]> msgAndData = it.next(); 
				String msg = new String(msgAndData.message());
				_messagesTopicList.add(msg);
				String [] splitMsg = msg.split(" ");
				for(int i = 0; i < splitMsg.length; i++){
					if(_wordsCounter.containsKey(splitMsg[i])){
						_wordsCounter.put(splitMsg[i], (_wordsCounter.get(splitMsg[i])+1));
					}else{
						_wordsCounter.put(splitMsg[i], 1);
					}
				}
				_count++;
				System.out.println(msg);
				System.out.println(_count);
				
			}
		}
	});
	
	public MyConsumer(String topic){
		setTopic(topic);
		setGroupID(System.currentTimeMillis()+"");
		System.out.println("GroupID="+_groupid);
		_messagesTopicList = new ArrayList<String>();
		_wordsCounter = new HashMap<String, Integer>();
		_count = 0;
		ConsumerConfig config;
		Properties props = new Properties(); 
		props.put("zookeeper.connect","sandbox.hortonworks.com:2181");
		props.put("group.id", _groupid);
		props.put("zookeeper.session.timeout.ms", "400");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "200");
		props.put("auto.offset.reset", "smallest");
		config = new ConsumerConfig(props);
		_consumer = kafka.consumer.Consumer.createJavaConsumerConnector
				(config); 
		System.out.println("Running");
		_consumerThread.start();
		
	}
	
	@Override
	public List<String> getWordsCountList(){
		List<String> retVal = new ArrayList<String>();
		for (Map.Entry<String,Integer> entry : _wordsCounter.entrySet()) {
			  String key = entry.getKey();
			  int value = entry.getValue();
			  retVal.add(key+" => "+value+"\n");
			}
		return retVal;
	}
	
	@Override
	public List<String> getAllTopicMessages(){
		return _messagesTopicList;
	}
	
	@Override
	public int getAmountMessages(){
		return _count;
	}
	
	@Override
	public void close(){
		_consumer.shutdown();
		_consumerThread.stop();
		System.out.println("Consumer closes");
	}

	@Override
	public void setTopic(String topic){
		this._topic = topic;
	}
	
	@Override
	public void setGroupID(String groupID){
		this._groupid = groupID;
	}

}
