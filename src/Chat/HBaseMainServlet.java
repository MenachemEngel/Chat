package Chat;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import constants.Constants;
import Kivun.Infra.DTO.DTOJSONConverter;
import Kivun.Infra.DTO.ServiceMessage;
import Kivun.Infra.Interfaces.IDTO;
import Kivun.Infra.Interfaces.IService;


public class HBaseMainServlet extends HBaseBaseServlet implements Constants{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = resp.getWriter();
		out.println("This works with get");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	    handleRequest(req , resp);
		
	}

	private void handleRequest(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		String msgParams = req.getParameter("ServiceMessage");
		ServiceMessage serviceMessage = new ServiceMessage(msgParams);
		IService service = _servicesFactory.createService(
				serviceMessage.get_Handler());
		DTOJSONConverter converter = new DTOJSONConverter();
		IDTO dto = null;
		try {
			dto = converter.ToDTO(new JSONObject(serviceMessage.get_DTO()));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		service.set_Params(dto);
		service.Execute();
		
		String className = null;
		String dtoData = null;
		
		String result = service.get_Response().toString();
		
		JSONObject obj = null;
		try {
			obj = new JSONObject(result);
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		System.out.println(obj);
		try {
			className = obj.getString("ServiceName");
			className = className.substring(12, className.length()-8);
			dtoData = obj.getString("DTO");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		try {
			PrintWriter out = resp.getWriter();
			out.println(HTML_AND_STYLE_FOR_MAINSERVLET_PART_1);			
			out.println(className+"</p><p>"+dtoData);
			out.println(HTML_AND_STYLE_FOR_MAINSERVLET_PART_2);
			out.println(service.get_Response());
			out.println(HTML_AND_STYLE_FOR_MAINSERVLET_PART_3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
