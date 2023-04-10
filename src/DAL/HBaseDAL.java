package DAL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Properties;

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

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.FamilyFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.filter.ValueFilter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.protobuf.generated.ZooKeeperProtos.Table;
import org.apache.hadoop.hbase.rest.client.Client;
import org.apache.hadoop.hbase.rest.client.Cluster;
import org.apache.hadoop.hbase.rest.client.RemoteHTable;
import org.apache.hadoop.hbase.util.Bytes;
import org.json.JSONException;
import org.json.JSONObject;

import DAL.KafkaDAL.Con;


public class HBaseDAL implements IChatDAL {

	@Override
	public boolean IsUserExists(String username) {
		// TODO Auto-generated method stub
		RemoteHTable table = new RemoteHTable(new Client(new Cluster().add("127.0.0.1",60080)),Tables.USERS);
		Get get = new Get(username.getBytes());
		System.out.println("GET:"+get.toString());
		try {
			Result result = table.get(get);
			System.out.println(result);
			NavigableMap<byte[],byte[]> familyMap = result.getFamilyMap(Users.Data.NAME.getBytes());
			System.out.println(familyMap);
			if(familyMap == null){
				table.close();
				return false;
			}else{
				table.close();
				return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return true;
		}
	}

	@Override
	public boolean isManager(String userID){
		if(IsUserExists(userID)){
			try {
				RemoteHTable table = new RemoteHTable(new Client(new Cluster().add(Con.IP,Con.PORT)), Tables.USERS);
				Get get = new Get(userID.getBytes());
				Result result = table.get(get);
				String type = new String(result.getValue(Users.DATA.getBytes(), Users.Data.TYPE.getBytes()));
				if(type.equals(Users.Data.Type.REGULAR)){
					table.close();
					return false;
				}else{
					table.close();
					return true;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}else{
			try {
				throw new UserNotExistsException();
			} catch (UserNotExistsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
	}

	@Override
	public void RegisterUser(String username, String name, String password) {
		// TODO Auto-generated method stub
		try {
			if(!IsUserExists(username)){
				RemoteHTable table = new RemoteHTable(new Client(new Cluster().add(Con.IP,Con.PORT)),Tables.USERS);
				
//				List<Put> putList = new ArrayList<Put>();
					String status = "offline";
					Put p = new Put(username.getBytes());
					p.add(Users.DATA.getBytes(), Users.Data.NAME.getBytes(), name.getBytes());
					p.add(Users.DATA.getBytes(), Users.Data.STATUS.getBytes(), status.getBytes());
					p.add(Users.DATA.getBytes(), Users.Data.PASSWORD.getBytes(), password.getBytes());
					p.add(Users.DATA.getBytes(), Users.Data.TYPE.getBytes(), "regular".getBytes());
//					putList.add(p);
					table.put(p);
					table.close();
			}else{
				throw new UserAlreadyExistsException();
			}
			} catch (IOException | UserAlreadyExistsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	}

	@Override
	public String GetPassword(String username) {
		// TODO Auto-generated method stub
		try {
			if(IsUserExists(username)){
				RemoteHTable table = new RemoteHTable(new Client(new Cluster().add(Con.IP,Con.PORT)),Tables.USERS);
				Get get = new Get(username.getBytes());
				Result result = null;
				result = table.get(get);
				System.out.println(result);
//				NavigableMap<byte[],byte[]> familyMap = result.getFamilyMap(Users.USERID.getBytes());
				String password = new String(result.getValue(Users.DATA.getBytes(), Users.Data.PASSWORD.getBytes()));
				table.close();
				return password;
			}else{
				throw new UserNotExistsException();
			}
		}catch(IOException | UserNotExistsException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void UpdateUserStatus(String username, String status) {
		// TODO Auto-generated method stub
		if(IsUserExists(username)){
				
			RemoteHTable table = new RemoteHTable(new Client(new Cluster().add("127.0.0.1",60080)),Tables.USERS);
			
			Get get = new Get(username.getBytes());
			//get.addFamily(friendsCF.getBytes());
			Result result = null;
			try {
				result = table.get(get);
				String name = new String(result.getValue(Users.DATA.getBytes(), Users.Data.NAME.getBytes()));
				JSONObject obj = new JSONObject(); 
				obj.put(Users.Data.NAME, name);
				obj.put(Users.Data.STATUS, status);
				
				NavigableMap<byte[],byte[]> familyMap = result.getFamilyMap(Users.FRIENDS.getBytes());
				List<Put> putList = new ArrayList<Put>();
				
				for(byte[] friendID : familyMap.keySet())
			    {
				
					Put p = new Put(friendID);
					p.add(Users.FRIENDS.getBytes(), username.getBytes(), obj.toString().getBytes());
					putList.add(p);
					
			    }
				Put p = new Put(username.getBytes());
				p.add(Users.DATA.getBytes(), Users.Data.STATUS.getBytes(), status.getBytes());
				putList.add(p);
				table.put(putList);
				table.close();
	
				
				
			}catch (IOException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				throw new UserNotExistsException();
			} catch (UserNotExistsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public String GetUserStatus(String username) {
		// TODO Auto-generated method stub
		try {
			if(IsUserExists(username)){
				RemoteHTable table = new RemoteHTable(new Client(new Cluster().add(Con.IP,Con.PORT)),Tables.USERS);
				Get get = new Get(username.getBytes());
				Result result = null;
				result = table.get(get);
				String status = Users.Data.Status.OFFLINE;
				String stat = new String(result.getValue(Users.DATA.getBytes(), Users.Data.STATUS.getBytes()));
				if(stat.equals(Users.Data.Status.ONLINE)){
					status = Users.Data.Status.ONLINE;
				}else{
					status = Users.Data.Status.OFFLINE;
				}
				table.close();
				return status;
			}else{
				throw new UserNotExistsException();
			}
		}catch(IOException | UserNotExistsException e){
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public List<String> getOnlineFriends(String username){
		if(IsUserExists(username)){
			List<String> retVal = new ArrayList<String>();
			// TODO Auto-generated method stub
			RemoteHTable table = new RemoteHTable(new Client(new Cluster().add(Con.IP,Con.PORT)),Tables.USERS);
			Get get = new Get(username.getBytes());
			//get.addFamily(friendsCF.getBytes());
			Result result;
			try {
				result = table.get(get);
				
				//for (byte[] key:result.getNoVersionMap().keySet()){
					NavigableMap<byte[],byte[]> familyMap = result.getFamilyMap(Users.FRIENDS.getBytes());
					for(byte[] k : familyMap.keySet())
				    {
						String value = Bytes.toString(familyMap.get(k));
						if(value.contains("\"status\":\"online\"")){
				          	System.out.println(Bytes.toString(k)+"=>"+value);
				          	retVal.add(Bytes.toString(k));
						}
				    }
				//}
				table.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return retVal;
		}else{
			try {
				throw new UserNotExistsException();
			} catch (UserNotExistsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
	}
	
	@Override
	public List<String> getAllMessages(String username,String chatID) {
		// TODO Auto-generated method stub
		List<String> retVal = new ArrayList<String>();
		String []splitID = chatID.split(":");
		if(IsUserExists(splitID[0]) && IsUserExists(splitID[1])){
			try {
				RemoteHTable table = new RemoteHTable(new Client(new Cluster().add(Con.IP,Con.PORT)),Tables.MESSAGES);
				Scan scan = new Scan(chatID.getBytes());
				PrefixFilter prefixFilter = new PrefixFilter(chatID.getBytes());
				scan.setFilter(prefixFilter);
				ResultScanner scanner = table.getScanner(scan);
				for (Result result:scanner){
					for (byte[] key:result.getNoVersionMap().keySet()){
						NavigableMap<byte[],byte[]> familyMap = result.getNoVersionMap().get(key);
						for(byte[] k : familyMap.keySet())
					    {
							if(Bytes.toString(key).equals("message")){
								retVal.add(Messages.MESSAGE+","+Bytes.toString(familyMap.get(k)));
							}else if(Bytes.toString(key).equals("data") && Bytes.toString(k).equals("from")){
					        	retVal.add(Messages.Data.FROM+","+Bytes.toString(familyMap.get(k)));
					        }
							System.out.println(Bytes.toString(key)+":"+Bytes.toString(k)+"=>"+Bytes.toString(familyMap.get(k)));
					    }
						String id = chatID+":"+username;
						if(isRowExists(id)){
							System.out.println("GAM="+id);
							int num = getCounterCurrent(id);
							setCounterRead(id, num);
						}
					}
				}
				table.close();
				return retVal;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return retVal;
			}
		}else{
			try {
				throw new UserNotExistsException();
			} catch (UserNotExistsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return retVal;
		}
	}
	
	@Override
	public List<String> getAllTopicMessages(String userID, String topic){
		List<String> retVal = new ArrayList<String>();
		if(IsUserExists(userID)){
			try {
				RemoteHTable table = new RemoteHTable(new Client(new Cluster().add(Con.IP,Con.PORT)), Tables.MESSAGES);
				Scan scan = new Scan(topic.getBytes());
				ResultScanner scanner = table.getScanner(scan);
				for(Result result:scanner){
					for(byte []key:result.getNoVersionMap().keySet()){
						NavigableMap<byte[] ,byte[]> familyMap = result.getNoVersionMap().get(key);
						String fm = new String(key);
						System.out.println("fm="+fm);
						if(fm.equals(Messages.MESSAGE)){
							for(byte []k:familyMap.keySet()){
								String qf = new String(k);
								System.out.println("qf="+qf);
								if(qf.equals(topic)){
									String msg = new String(familyMap.get(k));
									retVal.add(msg);
									System.out.println(msg);
								}
							}
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				throw new UserNotExistsException();
			} catch (UserNotExistsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retVal;
	}
	
	@Override
	public List<String> getAllChats(String userID){
		List<String> retVal = new ArrayList<String>();
		if(IsUserExists(userID)){
			try {
					RemoteHTable table = new RemoteHTable(new Client(new Cluster().add(Con.IP,Con.PORT)), Tables.MESSAGES);
					Scan scan = new Scan();
//					PrefixFilter pf = new PrefixFilter(userID.getBytes());
//					scan.setFilter(pf);
					ResultScanner scanner = table.getScanner(scan);
					for(Result result:scanner){
						for(byte []key:result.getNoVersionMap().keySet()){
							String row = Bytes.toString(result.getRow());
							System.out.println("Row="+row);
							if(row.contains(userID+":") || row.contains(":"+userID+":")){
								String fm = Bytes.toString(key);
								System.out.println("FM="+fm);
								NavigableMap<byte[],byte[]> familyMap = result.getNoVersionMap().get(key);
								for(byte []k:familyMap.keySet()){
									if(fm.equals(Messages.DATA)){
										String qf = Bytes.toString(k);
										if(qf.equals(Messages.Data.FROM) || qf.equals(Messages.Data.TO)){
											String []rk = row.split(":");
											String id = rk[0]+":"+rk[1];
											if(!(retVal.contains(id) || retVal.contains(id+" New Message"))){
												
												if(unreadMessages(id+":"+userID)==0){
													retVal.add(id);
												}else{
													retVal.add(id+" New Message");
												}
											}
										}
									}
								}
							}
						}
					}
					return retVal;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return retVal;
			}
		}else{
			try {
				throw new UserNotExistsException();
			} catch (UserNotExistsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
	}
	
	@Override
	public void sendMessage(String to, String message, String from) {
		// TODO Auto-generated method stub
		if(IsUserExists(to) && IsUserExists(from)){
			RemoteHTable table = new RemoteHTable(new Client(new Cluster().add(Con.IP,Con.PORT)), Tables.MESSAGES);
			String id = null;
			if(to.compareTo(from) < 0){
				System.out.println(to.compareTo(from));
				id = to+":"+from;
			}else{
				id = from+":"+to;
			}
			String idM = id+":"+System.currentTimeMillis();
			System.out.println(id+"=>"+idM);
			List<Put> puts = new ArrayList<Put>();
			Put put = new Put(idM.getBytes());
			put.add(Messages.DATA.getBytes(), Messages.Data.FROM.getBytes(), from.getBytes());
			put.add(Messages.DATA.getBytes(), Messages.Data.TO.getBytes(), to.getBytes());
			put.add(Messages.MESSAGE.getBytes(), (System.currentTimeMillis()+"").getBytes(), message.getBytes());
			puts.add(put);
			String idFrom = id+":"+from;
			String idTo = id+":"+to;
			if(isRowExists(idFrom)){
				int currentFrom = getCounterCurrent(idFrom);
				int currentTo = getCounterCurrent(idTo);
				setCounterCurrent(idFrom, currentFrom+1);
				setCounterRead(idFrom, currentFrom+1);
				setCounterCurrent(idTo, currentTo+1);
			}else{
				setCounterCurrent(idFrom, 1);
				setCounterRead(idFrom, 1);
				setCounterCurrent(idTo, 1);
				setCounterRead(idTo, 0);				
			}
			try {
				table.put(puts);
				table.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				throw new UserNotExistsException();
			} catch (UserNotExistsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * **/
	@Override
	public List<String> getAllTopicsUsers(String userID){
		List<String> retVal = new ArrayList<String>();
		if(IsUserExists(userID)){
			try {
				RemoteHTable hTable = new RemoteHTable(new Client(new Cluster().add(Con.IP, Con.PORT)),Tables.USERS);
				Get get = new Get(userID.getBytes());
				Result result = hTable.get(get);
				NavigableMap<byte[],byte[]> familyMap = result.getFamilyMap(Users.TOPIC.getBytes());
				for(byte []k:familyMap.keySet()){
					String topic = new String(familyMap.get(k));
					System.out.println("GATU="+topic);
					retVal.add(topic);
				}
				return retVal;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return retVal;
			}
		}else{
			try {
				throw new UserNotExistsException();
			} catch (UserNotExistsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
	}
	
	@Override
	public List<String> getAllFriends(String username) {
		// TODO Auto-generated method stub
		if(IsUserExists(username)){
			List<String> retVal = new ArrayList<String>();
			// TODO Auto-generated method stub
			RemoteHTable table = new RemoteHTable(new Client(new Cluster().add(Con.IP,Con.PORT)),Tables.USERS);
			Get get = new Get(username.getBytes());
			//get.addFamily(friendsCF.getBytes());
			Result result;
			try {
				result = table.get(get);
				
				//for (byte[] key:result.getNoVersionMap().keySet()){
					NavigableMap<byte[],byte[]> familyMap = result.getFamilyMap(Users.FRIENDS.getBytes());
					for(byte[] k : familyMap.keySet()){
						String value = Bytes.toString(familyMap.get(k));
			          	System.out.println(Bytes.toString(k)+"=>"+value);
			          	JSONObject jo = new JSONObject(value);
			          	retVal.add(jo.getString("name"));
						}
				//}
					table.close();
			} catch (IOException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return retVal;
		}else{
			try {
				throw new UserNotExistsException();
			} catch (UserNotExistsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
	}

	@Override
	public void assignFriend(String userID1, String userID2) {
		// TODO Auto-generated method stub
		try {
			if(IsUserExists(userID1) && IsUserExists(userID2)){
				RemoteHTable table = new RemoteHTable(new Client(new Cluster().add(Con.IP,Con.PORT)),Tables.USERS);
				
				String f1 = makeFriends(userID1);
				String f2 = makeFriends(userID2);
				if(f1 != null && f2 != null){
					
					addfriendsColumn(userID1, userID2, f2);
					addfriendsColumn(userID2, userID1, f1);
					
				}
				table.close();
			}else{
				throw new UserNotExistsException();
			}
		} catch (IOException | UserNotExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String makeFriends(String userID){
		RemoteHTable table = new RemoteHTable(new Client(new Cluster().add(Con.IP,Con.PORT)),Tables.USERS);
		try {
			Get get = new Get(userID.getBytes());
			Result result = null;
			result = table.get(get);
			
			String name = new String(result.getValue(Bytes.toBytes(Users.DATA), Bytes.toBytes(Users.Data.NAME)));
			String status = new String(result.getValue(Bytes.toBytes(Users.DATA), Bytes.toBytes(Users.Data.STATUS)));
			table.close();
			
			JSONObject json	= new JSONObject();
			json.put(Users.Data.NAME, name);
			json.put(Users.Data.STATUS, status);
			
			return json.toString();
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private void addfriendsColumn(String userID1, String userID2, String jsonFriend){
		RemoteHTable table = new RemoteHTable(new Client(new Cluster().add(Con.IP,Con.PORT)),Tables.USERS);
		
		Put p = new Put(userID1.getBytes());
		p.add(Users.FRIENDS.getBytes(), userID2.getBytes(), jsonFriend.getBytes());
		
		try {
			table.put(p);
			table.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void registerToTopic(String topic, String userID) {
		// TODO Auto-generated method stub
		try {
			if(IsUserExists(userID)){
				RemoteHTable table = new RemoteHTable(new Client(new Cluster().add(Con.IP ,Con.PORT)), Tables.USERS);
				
				Put p = new Put(userID.getBytes());
				p.add(Users.TOPIC.getBytes(), topic.getBytes(), topic.getBytes());
				table.put(p);
				table.close();
			}else{
				throw new UserNotExistsException();
			}
		}catch(UserNotExistsException | IOException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void setCounterCurrent(String id, int num){
		try {
			RemoteHTable hTable = new RemoteHTable(new Client(new Cluster().add(Con.IP,Con.PORT)), Tables.MESSAGESCOUNTER);
			Put p = new Put(id.getBytes());
			p.add(MessagesCounter.COUNTERS.getBytes(), MessagesCounter.Counters.CURRENT.getBytes(), Bytes.toBytes(num));
			hTable.put(p);
			hTable.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public void setCounterRead(String id, int num){
		try {
			RemoteHTable hTable = new RemoteHTable(new Client(new Cluster().add(Con.IP, Con.PORT)), Tables.MESSAGESCOUNTER);
			Put p = new Put(id.getBytes());
			p.add(MessagesCounter.COUNTERS.getBytes(), MessagesCounter.Counters.READ.getBytes(), Bytes.toBytes(num));
			hTable.put(p);
			hTable.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean isRowExists(String row){
		RemoteHTable table = new RemoteHTable(new Client(new Cluster().add("127.0.0.1",60080)),Tables.MESSAGESCOUNTER);
		Get get = new Get(row.getBytes());
		System.out.println("GET:"+get.toString());
		try {
			Result result = table.get(get);
			System.out.println(result);
			NavigableMap<byte[],byte[]> familyMap = result.getFamilyMap(MessagesCounter.Counters.CURRENT.getBytes());
			System.out.println(familyMap);
			if(familyMap == null){
				table.close();
				return false;
			}else{
				table.close();
				return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return true;
		}
	}
	
	@Override
	public int getCounterCurrent(String id){
		int retVal = -1;
		try {
			RemoteHTable hTable = new RemoteHTable(new Client(new Cluster().add(Con.IP, Con.PORT)), Tables.MESSAGESCOUNTER);
			Get get = new Get(id.getBytes());
			Result result = hTable.get(get);
			NavigableMap<byte[], byte[]> familyMap = result.getFamilyMap(MessagesCounter.COUNTERS.getBytes());
			for(byte[] k:familyMap.keySet()){
				if(Bytes.toString(k).equals(MessagesCounter.Counters.CURRENT)){
				retVal = Bytes.toInt(familyMap.get(k));	
				}
			}
			hTable.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retVal;
		
	}
	
	@Override
	public int getCounterReader(String id){
		int retVal = -1;
		try {
			RemoteHTable hTable = new RemoteHTable(new Client(new Cluster().add(Con.IP, Con.PORT)), Tables.MESSAGESCOUNTER);
			Get get = new Get(id.getBytes());
			Result result = hTable.get(get);
			NavigableMap<byte[], byte[]> familyMap = result.getFamilyMap(MessagesCounter.COUNTERS.getBytes());
			for(byte[] k:familyMap.keySet()){
				if(Bytes.toString(k).equals(MessagesCounter.Counters.READ)){
				retVal = Bytes.toInt(familyMap.get(k));	
				}
			}
			hTable.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retVal;
		
	}
	
	@Override
	public int unreadMessages(String id){
		int retVal = 0;
		try {
			if(isRowExists(id)){
				RemoteHTable hTable = new RemoteHTable(new Client(new Cluster().add(Con.IP, Con.PORT)), Tables.MESSAGESCOUNTER);
				Get get = new Get(id.getBytes());
				Result result = hTable.get(get);
				int read = getCounterReader(id);
				int current = getCounterCurrent(id);
				retVal = (current-read);
				hTable.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			retVal = -1;
		}
		return retVal;
	}
	
	@Override
	public int getNumberOfMessages(String id){
		int retVal = -1;
		try {
			RemoteHTable hTable = new RemoteHTable(new Client(new Cluster().add(Con.IP, Con.PORT)), Tables.MESSAGES);
			Get get = new Get(id.getBytes());
			Result result = hTable.get(get);
			NavigableMap<byte[],byte[]> familyMap = result.getFamilyMap(Messages.MESSAGE.getBytes());
			for(byte[] k: familyMap.keySet()){
				retVal++;
			}
			hTable.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retVal;
	}
	
	@Override
	public void putMessageInTable(String topic, String msg){
		RemoteHTable hTable = new RemoteHTable(new Client(new Cluster().add(Con.IP, Con.PORT)), Tables.MESSAGES);
		String id = topic+":"+System.currentTimeMillis();
		Put p = new Put(id.getBytes());
		p.add(Messages.MESSAGE.getBytes(), topic.getBytes(), msg.getBytes());
		List<Put> lp = new ArrayList<Put>();
		lp.add(p);
		try {
			hTable.put(lp);
			hTable.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public String getUserName(String userID){
		String retVal = null;
		try {
			RemoteHTable table = new RemoteHTable(new Client(new Cluster().add(Con.IP,Con.PORT)), Tables.USERS);
			Get get = new Get(userID.getBytes());
			Result result = table.get(get);
			retVal = new String(result.getValue(Users.DATA.getBytes(), Users.Data.NAME.getBytes()));
			table.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retVal;
	}
	
	public class Con{
		public static final int PORT = 60080;
		public static final String IP = "127.0.0.1";
	}
	
	public class Tables{
		public static final String USERS = "chat:users";
		public static final String MESSAGES = "chat:messages";
		public static final String MESSAGESCOUNTER = "chat:messagescounter";
		
	}
	
	public class Users{
		public static final String FRIENDS = "friends";
		public static final String DATA = "data";
		public static final String USERID= "UserID";
		public static final String TOPIC = "topic";
		
		
		public class Data{
			public static final String STATUS = "status"; 
			public static final String NAME = "name";
			public static final String PASSWORD = "password";
			public static final String TYPE = "type";
			
			public class Status{
				public static final String ONLINE = "online";
				public static final String OFFLINE = "offline";
			}
			
			public class Type{
				public static final String REGULAR = "regular";
				public static final String MANAGER = "manager";
			}
		}
	}
	
	public class Messages{
		public static final String MESSAGEID = "messageID";
		public static final String DATA = "data";
		public static final String MESSAGE = "message";
		public static final String TOPIC = "topic";
		
		public class Data{
			public static final String FROM = "from";
			public static final String TO = "to";
		}
	}
	
	public class MessagesCounter{
		public static final String COUNTERS = "counters";
		
		public class Counters{
			public static final String READ = "read";
			public static final String CURRENT = "current";
		}
	}
	
	protected class UserNotExistsException extends Exception{}
	protected class UserAlreadyExistsException extends Exception{}
}
