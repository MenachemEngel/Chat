package DAL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.I0Itec.zkclient.ZkClient;

import DAL.HBaseDAL.UserNotExistsException;
import kafka.admin.AdminUtils;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.TopicMetadataResponse;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.producer.Producer;
import kafka.message.MessageAndMetadata;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.utils.ZKStringSerializer$;

public class KafkaDAL implements IKafkaDAL{
	
	HBaseDAL _dal;
	
	public KafkaDAL(){
		_dal = new HBaseDAL();
	}
	
	@Override
	public void sendTopicMessage(String userID, String topic, String msg) {
		if(_dal.IsUserExists(userID)){
			Properties props = new Properties(); 
			props.put("metadata.broker.list", Con.URL+":"+Con.PRODUSER_PORT);
			props.put("serializer.class", "kafka.serializer.StringEncoder");
//				props.put("partitioner.class", SimplePartitioner.class.getName());
			props.put("request.required.ack", "1");
			ProducerConfig config = new ProducerConfig(props);
			
			Producer<String,String> producer = new Producer<String,String>(config);
			String name = _dal.getUserName(userID);
			String message = "("+userID+","+name+") "+msg;
			String key = topic+":"+System.currentTimeMillis();
			KeyedMessage<String,String> msg2 = new KeyedMessage<String,String>(topic,key,message);
			System.out.println("Sending");
			producer.send(msg2);
			System.out.println("Sent");
			producer.close(); 
			System.out.println("Closing");
		}
	}
	
	@Override
	public void putTopicMessages(String topic, String userID) {
		// TODO Auto-generated method stub
		ConsumerConfig config;
		Properties props = new Properties(); 
		props.put("zookeeper.connect",Con.URL+":"+Con.CONSUMER_PORT);
		props.put("group.id", userID);
		props.put("zookeeper.session.timeout.ms", "400");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "200");
		config = new ConsumerConfig(props);
		ConsumerConnector consumer = kafka.consumer.Consumer.createJavaConsumerConnector(config);
		System.out.println("Running");
//		Run(consumer,topics,topic);
		HashMap<String,Integer> topicCountMap = new HashMap<String,Integer>();
		topicCountMap.put(topic, 1);
		Map<String,List<KafkaStream<byte[],byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		KafkaStream<byte[],byte[]> stream = consumerMap.get(topic).get(0);
		ConsumerIterator<byte[],byte[]> it =  stream.iterator();
		System.out.println("Waiting");
		MessageAndMetadata<byte[],byte[]> msgAndData = it.next(); 
		String msg = new String(msgAndData.message());
		System.out.println(msg);
		_dal.putMessageInTable(topic, msg);
	}
	
	@Override
	public boolean isTopicExists(String topic){
		List<String> topicList = getTopicsList();
		for(String t:topicList){
			if(topic.equals(t)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isRegisteredToTopic(String userID ,String topic){
		System.out.println("yolo");
		if(_dal.IsUserExists(userID) && isTopicExists(topic)){
			System.out.println("After the check");
			List<String> rt = _dal.getAllTopicsUsers(userID);
			System.out.println("rt="+rt.toString());
			for(String t:rt){
				if(topic.equals(t)){
					return true;
				}
			}
		}else{
			try {
				throw _dal.new UserNotExistsException();
			} catch (UserNotExistsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	@Override
	public List<String> getTopicsList() {
		// TODO Auto-generated method stub
		SimpleConsumer sc = new SimpleConsumer(Con.IP, Con.PRODUSER_PORT, 100000, 64*1024, "test");
		List<String> topics = new ArrayList<String>();
	    TopicMetadataRequest req = new TopicMetadataRequest(topics);
	    TopicMetadataResponse resp = sc.send(req);
	    List<TopicMetadata> data =  resp.topicsMetadata();
//	    List<String> r = new ArrayList<String>();
	    for (TopicMetadata item : data) {
	        System.out.println("TopicK: " + item.topic());
	        topics.add(item.topic());
	    }
	    return topics; 
	}
	
	@Override
	public void createTopic(String topicName){
		ZkClient zkc = new ZkClient("127.0.0.1:2181", 10000, 10000, ZKStringSerializer$.MODULE$);
		AdminUtils.createTopic(zkc, topicName, 1, 1, new Properties());
	}
	
	public class Con{
		public static final int CONSUMER_PORT = 2181;
		public static final int PRODUSER_PORT = 6667;
		public static final String IP = "127.0.0.1";
		public static final String URL = "sandbox.hortonworks.com";
	}

}
