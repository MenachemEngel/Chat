package DAL;
import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;


public class SimplePartitioner implements Partitioner {

	public SimplePartitioner(VerifiableProperties prop){
		
	}
	@Override
	public int partition(Object key, int numberOfPartitions) {
		// TODO Auto-generated method stub
		int retVal = 0; 
		String stringKey = (String)key; 
		int offset = stringKey.lastIndexOf('.');
		if (offset>0){
			retVal = Integer.parseInt(stringKey.substring(offset+1)) % numberOfPartitions;
		}
		System.out.println(retVal); 
		return retVal; 
		
	}

}
