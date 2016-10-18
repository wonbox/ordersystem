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

public class Createcompany {
	static Connection conn = null;
	static Statement stmt = null;
	Calendar calendar = Calendar.getInstance();
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	JFrame frame = new JFrame("CompanyRegister");
	JButton register = new JButton("Register");
	JLabel ID = new JLabel("CompanyID");
	JLabel Name = new JLabel("CompanyName");
	JLabel Address = new JLabel("CompanyAddress");
	JLabel Type = new JLabel("CompanyType");
	JTextField IDText = new JTextField();
	JTextField NameText = new JTextField();
	JTextField AddressText = new JTextField();
	JTextField TypeText = new JTextField();
	JPanel panel = new JPanel();
	
	public Createcompany(){
		frame.setSize(400, 300);
		frame.setLocation(400, 300);
		Container contentPane = frame.getContentPane();
		panel.setLayout(null);
		register.setBounds(130, 200, 120, 25);
		register.setFont(new Font("",Font.BOLD,10));
		ID.setBounds(20, 30, 150, 25);
		ID.setFont(new Font("",Font.BOLD,15));
		Name.setBounds(20, 70, 150, 25);
		Name.setFont(new Font("",Font.BOLD,15));
		Address.setBounds(20, 110, 150, 25);
		Address.setFont(new Font("",Font.BOLD,15));
		Type.setBounds(20, 150, 250, 25);
		Type.setFont(new Font("",Font.BOLD,15));
		IDText.setBounds(170, 30, 160, 25);
		NameText.setBounds(170, 70, 160, 25);
		AddressText.setBounds(170, 110, 160, 25);
		TypeText.setBounds(170, 150, 160, 25);
		register.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
					insertCompany();
			}
		});
		panel.add(register);
		panel.add(ID);
		panel.add(Name);
		panel.add(Address);
		panel.add(IDText);
		panel.add(NameText);
		panel.add(TypeText);
		panel.add(Type);
		panel.add(AddressText);
		contentPane.add(panel);
		frame.setVisible(true);
	
	}
	//회사 등록
	public void insertCompany(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
			stmt = conn.createStatement();
			
			String ID = IDText.getText();
			String Name = NameText.getText();
			String Address =  AddressText.getText();
			String Type = TypeText.getText();
			
			stmt.executeUpdate("insert into company(company_id , company_name , company_address , type) values('" + ID + "' , '" + Name + "' , '" + Address + "', '" + Type + "');");
			JOptionPane.showMessageDialog(null, "회사ID가 생성되었습니다.");
			stmt.close();
			conn.close();
			IDText.setText("");
			NameText.setText("");
			AddressText.setText("");
			TypeText.setText("");
					
		
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch(SQLException se){
								
		}
	}
}
