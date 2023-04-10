package kafka;

import java.util.List;

public class MainTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String topic = "News";
		String groupid = "group-363";
		String msg = "try consumer thread";
		List<String> mtl;
		int count;
		
		MyConsumer mc = new MyConsumer(topic);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mtl = mc.getWordsCountList();
		String allWordsCount = "";
		for(String wc : mtl){
			allWordsCount += wc;
		}
		count = mc.getAmountMessages();
		
		System.out.println("Messages List: "+allWordsCount);
		System.out.println("Amount messages: "+count);
		
		mc.close();
		
	}

}
