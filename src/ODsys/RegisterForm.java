package ODsys;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class RegisterForm {
	Createcompany CreateCF;
	static Connection conn = null;
	static Statement stmt = null;
	Calendar calendar = Calendar.getInstance();
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	JFrame frame = new JFrame("Register");
	JButton register = new JButton("Register");
	JButton companyID = new JButton("Create CompanyID");
	JLabel IDLabel = new JLabel("ID");
	JLabel PwdLabel = new JLabel("PassWord");
	JLabel NameLabel = new JLabel("Name");
	JLabel BirthLabel = new JLabel("Birth");
	JLabel AddressLabel = new JLabel("Address");
	JLabel CompanyLabel = new JLabel("CompanyID");
	JLabel DateLabel = new JLabel("Date");
	JTextField IDText = new JTextField();
	JPasswordField PwdText = new JPasswordField();
	JTextField NameText = new JTextField();
	JTextField BirthText = new JTextField();
	JTextField AddressText = new JTextField();
	JTextField CompanyText = new JTextField();
	JTextField DateText = new JTextField();
	JPanel panel = new JPanel();
	
	public RegisterForm()
	{
		frame.setSize(400, 450);
		frame.setLocation(400, 300);
		Container contentPane = frame.getContentPane();
		panel.setLayout(null);
		register.setBounds(210, 370, 100, 25);
		register.setFont(new Font("",Font.BOLD,10));
		companyID.setBounds(50, 370, 130, 25);
		companyID.setFont(new Font("",Font.BOLD,10));
		IDLabel.setBounds(20, 30, 100, 25);
		IDLabel.setFont(new Font("",Font.BOLD,20));
		PwdLabel.setBounds(20, 70, 100, 25);
		PwdLabel.setFont(new Font("",Font.BOLD,20));
		NameLabel.setBounds(20, 110, 100, 25);
		NameLabel.setFont(new Font("",Font.BOLD,20));
		BirthLabel.setBounds(20, 150, 100, 25);
		BirthLabel.setFont(new Font("",Font.BOLD,20));
		AddressLabel.setBounds(20, 190, 100, 25);
		AddressLabel.setFont(new Font("",Font.BOLD,20));
		CompanyLabel.setBounds(20, 230, 120, 25);
		CompanyLabel.setFont(new Font("",Font.BOLD,20));
		DateLabel.setBounds(20, 270, 100, 25);
		DateLabel.setFont(new Font("",Font.BOLD,20));
		IDText.setBounds(170, 30, 160, 25);
		PwdText.setBounds(170, 70, 160, 25);
		NameText.setBounds(170, 110, 160, 25);
		BirthText.setBounds(170, 150, 160, 25);
		AddressText.setBounds(170, 190, 160, 25);
		CompanyText.setBounds(170, 230, 160, 25);
		DateText.setBounds(170, 270, 160, 25);
		DateText.setText(dateFormat.format(calendar.getTime()));
		panel.add(register);
		panel.add(IDLabel);
		panel.add(PwdLabel);
		panel.add(NameLabel);
		panel.add(BirthLabel);
		panel.add(AddressLabel);
		panel.add(CompanyLabel);
		panel.add(DateLabel);
		panel.add(IDText);
		panel.add(PwdText);
		panel.add(NameText);
		panel.add(BirthText);
		panel.add(AddressText);
		panel.add(CompanyText);
		panel.add(DateText);
		panel.add(companyID);
				
		register.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
					
				Addmembership();	
			}
		});
		companyID.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				CreateCF = new Createcompany();			
			}
		});
		
		contentPane.add(panel);
		frame.setVisible(true);
	}
	//회원 추가
	public void Addmembership()
	{
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
			stmt = conn.createStatement();
			
			String ID = IDText.getText();
			char[] getPwd = PwdText.getPassword();
			String Pwd = "";
			for(char C_Pwd:getPwd)
			{
				Pwd += C_Pwd;
			}
			String Name =  NameText.getText();
			String Birth = BirthText.getText();
			String Address = AddressText.getText();
			String Company = CompanyText.getText();
			String date = DateText.getText();
			stmt.executeUpdate("insert into membership(member_id ,member_pwd ,member_name ,member_birth ,member_address ,member_companyID , member_registerdate) values('" + ID +"','"
					+ Pwd +"', '" + Name + "', '" + Birth+ "', '" + Address+ "', "
					+ "'" + Company+ "', '" + date + "');");
			JOptionPane.showMessageDialog(null, "아이디가 만들어 졌습니다.");
			stmt.close();
			conn.close();
			frame.dispose();
							
		
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch(SQLException se){
								
		}
	}

}

