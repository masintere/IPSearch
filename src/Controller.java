

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.baselib.servlet.BaseServlet;

/**
 * Servlet implementation class Controller
 */
@WebServlet("/Controller")
public class Controller extends BaseServlet{
	private static final long serialVersionUID = 1L;
	private String index = "index.jsp";
	private ServletContext sc = null;
	private DBConnect db;
	private Connection con;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Controller() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init(ServletConfig config){
    	sc = config.getServletContext();
    }

	/* (non-Javadoc)
	 * @see com.baselib.servlet.BaseServlet#getAndPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void getAndPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		String iPAdress = "";
		if(request.getParameter("IPAddress") != null)
			iPAdress = request.getParameter("IPAddress");
		iPAdress = parseIPAddress(iPAdress);
		String ipCity = getIPAddress(iPAdress);
		session.setAttribute("location", ipCity);
		sc.getRequestDispatcher("/WEB-INF/include/" + index).forward(request, response);
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * searches through the sql database to find the city and state that match the ip address
	 * @param ipAddress ip address to search with
	 * @return the city and state that match the ip address or if it is an invalid ip
	 */
	public String getIPAddress(String ipAddress){
		db = new DBConnect();
		con = db.connect();
		String result = "";
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT CITY_NM, STATE_NM FROM Geolocation.GEOLOCATION " +
				"WHERE ? BETWEEN IP_START_TXT AND IP_END_TXT;");
		try {
			PreparedStatement ps = con.prepareStatement(sb.toString());
			ps.setString(1, ipAddress);
			ResultSet rs = ps.executeQuery();
			if(rs.next())
				result = "IP : " + ipAddress + "<br>City : " + rs.getString(1) + "<br>State : " + rs.getString(2);
			else
				result = "Invalid IP";
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Invalid IP";
	}
	
	/**
	 * adds the correct number of 0's to the ip address so that there will be 3 characters in each segment of the ip
	 * @param ipAddress
	 * @return the corrected ip address
	 */
	public String parseIPAddress(String ipAddress){
		String[] adress = ipAddress.split("\\.");
		String result = "";
		for(String s : adress){
			if(s.length() == 2)
				s = "0" + s;
			if(s.length() == 1)
				s = "00" + s;
			s = s+".";
			result += s;
		}
		result = result.substring(0, result.length() - 1);
		return result;
	}

}
