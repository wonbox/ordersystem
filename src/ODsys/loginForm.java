package ODsys;

import java.awt.Container;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class loginForm {
	static private Connection conn = null;
	static private Statement stmt = null;
	static private boolean check = false;
	static private RegisterForm RegisterF;
	static private OrderForm orderF;
	static private DeliveryForm deliveryF;
	static private JFrame frame = new JFrame("Login");
	static private String usercompanytype = "";
	static private String usercompanyID = "";
	static private String Ordertype = "order";
	static private String Deliverytype = "delivery";
	static private JButton btnLogin;
	static private JButton btnRegister;
	static private Label userLabel;
	static private Label passLabel;
	static private JTextField userText;
	static private JPasswordField pwdText;
	

	public static void main(String[] args) {
		
		frame.setSize(400, 150);
		frame.setResizable(false);
		frame.setLocation(800, 450);
		Container contentPane = frame.getContentPane();
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		
		userLabel = new Label("user:");
		userLabel.setBounds(10, 20, 80, 25);
		
		passLabel = new Label("pass:");
		passLabel.setBounds(10, 60 , 80, 25);
		
		userText = new JTextField(20);
		userText.setBounds(100, 20, 160, 25);
		
		pwdText = new JPasswordField(20);
		pwdText.setBounds(100, 60, 160, 25);
		
		btnLogin = new JButton("Login");
		btnLogin.setBounds(280, 20, 100, 25);
		btnLogin.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				  check = IDcheck();
				 if(check)
				 {   
					 if(usercompanytype.equals(Ordertype))
					 {
						 frame.dispose();
						 orderF = new OrderForm(usercompanyID);
					 }
					 else if(usercompanytype.equals(Deliverytype))
					 {
						 frame.dispose();
						 deliveryF = new DeliveryForm(usercompanyID);
					 }
					 else
					 {
						 JOptionPane.showMessageDialog(null, "회사 타입이 존재하지 않습니다.");
					 }
				 }
				 else
				 {
					 JOptionPane.showMessageDialog(null, "아이디 또는 비번이 틀렸습니다.");
					 userText.setText("");
					 pwdText.setText("");
					 
				 }
				
			}
			
		});
		
				
		btnRegister = new JButton("Register");
		btnRegister.setBounds(280, 60, 100, 25);
		btnRegister.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				RegisterF = new RegisterForm(); 
															
			}	
		});
			
		
		panel.add(userLabel);	
		panel.add(passLabel);
		panel.add(userText);
		panel.add(pwdText);
		panel.add(btnLogin);
		panel.add(btnRegister);
		contentPane.add(panel);
		
	
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	//중복된 아이디 및 비밀번호 확인
	static boolean IDcheck()
	{
		boolean CheckID = false;
		boolean CheckPwd = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select a.member_id , a.member_pwd , b.type , b.company_id from membership a , company b where a.member_companyID = b.company_id");
			while(rs.next())
			{
				String ID = rs.getString("a.member_id");
				
				if(ID.equals(userText.getText()))
				{
					CheckID = true;
					String Pwd = rs.getString("a.member_pwd");
					char[] getPwd = pwdText.getPassword();
					String Pwd_compare = "";
					for(char C_Pwd:getPwd)
					{
						Pwd_compare += C_Pwd;
					}
					if(Pwd.equals(Pwd_compare))
					{
						CheckPwd = true;
						usercompanytype = rs.getString("b.type");
						usercompanyID = rs.getString("b.company_id");
				        
					}
				}
					
			}
			
			stmt.close();
			conn.close();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch(SQLException se){
								
		}
		
		if(CheckID && CheckPwd)
		{
			
			return true;
			
		}
		else
		{
			
			return false;
		}
	}
	
}
