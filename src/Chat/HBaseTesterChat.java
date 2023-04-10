package Chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.ISignOutService;
import constants.Constants;
import DAL.HBaseDAL;
import DAL.KafkaDAL;
import Kivun.Infra.DTO.Annotations.GetProperty;
import Kivun.Infra.Interfaces.IService;


public class HBaseTesterChat extends HBaseBaseServlet implements Constants{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<String> _serviceList; 
	private final String SERVICENAME = "ServiceName";
	private final String PROPERTEPLACEHOLDER = "$$Property$$";
	private final String DTOPLACEHOLDER  = "$$DTO$$";
	
	private KafkaDAL _dal = new KafkaDAL();
//	private HBaseDAL _hdal = new HBaseDAL();
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		System.out.println("=====>>>>>Init Chat (TesterChat)");
		_serviceList = new ArrayList<String>();  
		
		Enumeration<String> servicesNames = servletConfig.getInitParameterNames();
		while (servicesNames.hasMoreElements()){
			String serviceName = servicesNames.nextElement();
				_serviceList.add(serviceName);
		}
	}
	
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response){
		String serviceName = request.getParameter(SERVICENAME);
		if(serviceName == null){
			System.out.println("Service name is null");
			GetServiceNames(request, response);
		}else{
//			Handler(request, response);
			System.out.println(serviceName);
			Handler(request, response);
			/*try {
				PrintWriter out = response.getWriter();
//				out.println("We are in"+serviceName);
				out.println("<script>window.open('/chat/SignUp.html','_self',false)</script>");
//				out.println("<script>document.location.url='/chat/SignUp.html'</script>");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	private void makeKafkaHTML(HttpServletRequest req, HttpServletResponse resp ,String serviceName) {
		// TODO Auto-generated method stub
		List<String> topicsList = _dal.getTopicsList();
		System.out.println(topicsList.toString());
		try {
			PrintWriter pw = resp.getWriter();
			pw.println("<!DOCTYPE html><html lang='en'><head><meta charset='utf-8' /><title>Chat</title><script type='text/javascript'>"
					+"function DoSomething(){var obj = {};var dto = {};dto.DTOType='DTOS.DTOKafkaTopic';obj.ServiceName="+serviceName+";" 
					+"obj['DTO']=dto;dto['TOPIC']=document.all['topic'].value;"
					+"dto['USERID'] = document.all['userID'].value;"
					+"document.forms['MyForm']['ServiceMessage'].value=JSON.stringify(obj);"
					+"alert(document.forms['MyForm']['ServiceMessage'].value);"
					+"return true;}"
					+"</script>"+STYLE_FOR_KAFKA+"</head><body><div id='topDiv'></div><h1 style='text-align: center;font-size: 60px;margin-top: -20px;'>Chat online</h1><div id='ferstDiv'>"
					+"<div style='width: 250px;margin-left: auto;margin-right: auto;'>"
					+"<h2 style='padding-top: 4px;'>"+serviceName+"</h2>"
					+"<select name='topic' required><option value=''>Select topic</option>");
			for(String topic : topicsList){
				pw.println("<option value='"+topic+"'>"+topic+"</option>");
			}
//			"<option value='button-1'>button-1</option><option value='button-2'>button-2</option></select>");
			pw.println("</select>&nbsp;<input type='text' name='userID'/>"
					+"<form action='HBaseMainServlet' method='post'>&nbsp;<input type='submit' value='submit'/>"
					+"<input type='hidden' name='ServiceMessage' />"
					+"</form></div></body></html>");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void Handler(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
			String serviceName = request.getParameter(SERVICENAME);
			
			//generate  html headers and body.
			//generate script 
			StringBuilder allHtmlBuilder = new StringBuilder();
			allHtmlBuilder.append("<html><head><title>Chat server</title>");
			allHtmlBuilder.append("<script type='text/javascript'>");
			allHtmlBuilder.append("function doSomething(){");
			allHtmlBuilder.append("var obj = {};");
			allHtmlBuilder.append("var dto = {};");
			allHtmlBuilder.append(makeDTO(request, "dto.DTOType = '"+DTOPLACEHOLDER+"';"));
			allHtmlBuilder.append("obj.ServiceName = '"+serviceName+"';");
			allHtmlBuilder.append("obj['DTO'] = dto;");
			allHtmlBuilder.append(makeBoth(request, "dto['"+PROPERTEPLACEHOLDER+"'] = document.all['"+PROPERTEPLACEHOLDER+"'].value;"));	
			allHtmlBuilder.append("document.forms['myForm']['ServiceMessage'].value = JSON.stringify(obj);");
			allHtmlBuilder.append("alert(document.forms['myForm']['ServiceMessage'].value);");
			allHtmlBuilder.append("return true;");
			allHtmlBuilder.append("}</script>");
			allHtmlBuilder.append(STYLE_FOR_HANDLER);
			allHtmlBuilder.append("</head>");
			allHtmlBuilder.append("<body>");
			allHtmlBuilder.append("<div id='topDiv'></div><h1 style='text-align: center;font-size: 60px;margin-top: -20px;'>Chat online</h1>"
					+"<div id='ferstDiv'>"
					+"<div style='width: 250px;margin-left: auto;margin-right: auto;'>"
					+"<h2 style='padding-top: 4px;'>"+getShortName(serviceName)+"</h2>");
			if(serviceName.contains("Topic")){
				List<String> topicsList = _dal.getTopicsList();
				String topics = "";
				for(String t:topicsList){
					topics += "\""+t+"\""+" ";
				}
				allHtmlBuilder.append("<p><span style='color:blue'>Topics exists: </span>"+topics+"</p>");
//				List<String> registeredTopics = _hdal.getAllTopicsUsers();
			}
			allHtmlBuilder.append("<div id='leftDiv'>"+makeBoth(request, "<p>"+PROPERTEPLACEHOLDER+":</p>"));
			allHtmlBuilder.append("</div><div id='rightDiv'>");
			allHtmlBuilder.append(makeBoth(request, "<input class='inputStyle' type='text' name='"+PROPERTEPLACEHOLDER+"'/><br/>"));
			allHtmlBuilder.append("</div><div id='clearDiv'></div>");
			allHtmlBuilder.append("<form action='HBaseMainServlet' onsubmit='doSomething()' name='myForm' method='POST'>");
			allHtmlBuilder.append("<input type='hidden' name='ServiceMessage'/>");
			allHtmlBuilder.append("<br/><input id='submit' type='submit' value='Submit'/>");
			allHtmlBuilder.append("</form></div>");
			allHtmlBuilder.append("</div></body>");
			
			
			try {
				PrintWriter out = response.getWriter();
				out.println(allHtmlBuilder.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
		
		private String makeBoth(HttpServletRequest request,String inputLine){
			
			String retVal = "";
			String serviceName = request.getParameter(SERVICENAME);
			Class<?> cls = getDTO(serviceName);
			StringBuilder sb = new StringBuilder(); 
			String dtoName = cls.getName();
			for (Method m:cls.getDeclaredMethods()){
				if (m.isAnnotationPresent(GetProperty.class)){
					GetProperty prop = m.getAnnotation(GetProperty.class);
					String propName = prop.PropName();
					String input = inputLine.replace(PROPERTEPLACEHOLDER,propName);
					sb.append(input);
				}
			}
				retVal = sb.toString();
			System.out.println(retVal);
			return retVal; 
			
		}
		
		private String makeDTO(HttpServletRequest request,String dto) {
			// TODO Auto-generated method stub
			String retVal = "";
			String serviceName = request.getParameter(SERVICENAME);
			Class<?> cls = getDTO(serviceName);
			String dtoName = cls.getName();
			retVal = dto.replace(DTOPLACEHOLDER,dtoName);
			return retVal;
		}
		
		private Class<?> getDTO(String serviceName){
			IService service = _servicesFactory.get_Service(serviceName);
			Class<?> retVal = service.get_DTOType();
			
			return retVal; 
		}
		
		private void GetServiceNames(HttpServletRequest request, HttpServletResponse response){
			String beginHTML = "<html>";
			String beginHEAD = "<head><title>List Of Chat Services</title>"+STYLE_FOR_SERVICENAMES;
			String endHEAD = "</head>";
			String endHTML = "</html>";
			String h1 = "<div id='topDiv'></div><h1 style='text-align: center;font-size: 60px;margin-top: -20px;'>Chat online</h1>";
			String beginBody = "<body>"+ h1 + "<div id='generalDiv'><h2 style='padding-top: 4px;'>Services list</h2><div id='jfw'>";
			String endBody = "<p></p></div></div></body>";
			String body = "";
			for (String serviceName:_serviceList){
				body += "<a href='HBaseTesterChat?ServiceName="+serviceName+"'><p class='linkStyle'>"
						+getShortName(serviceName)+"</p></a>";
			}
			String html = beginHTML+ beginHEAD + endHEAD + beginBody + body +endBody + endHTML;
			try {
				PrintWriter out = response.getWriter();
				out.println(html);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

					
			
	}
		
		public String getShortName(String name){
			String sn1 = name.substring(10);
			String sn2 = sn1.substring(0, sn1.length()-7);
			return sn2;
		}

}
