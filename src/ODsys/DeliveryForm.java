package ODsys;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import ODsys.DeliveryMarketForm.addForm;

public class DeliveryForm {
	AddDeliveryForm DeliveryF;
	DeliveryMarketForm DelMarketF;
	private JFrame frame = new JFrame("DeliveryForm");
	private Connection conn = null;
	private Statement stmt = null;
	private JScrollPane scrollPane;
	private DefaultTableModel model;
	private JTable table;
	private JButton OrderList = new JButton("OrderList");
	private JButton DeliveryList = new JButton("DeliveryList");
	private JButton Market = new JButton("Market");
	private JButton ReturnList = new JButton("ReturnList");
	private JLabel Company = new JLabel("CompanyID");
	private JButton Select = new JButton("Select");
	private JButton Delivery = new JButton("Delivery");
	private JLabel NowStatus = new JLabel("NowStatus");
	private JTextField CompanyText = new JTextField();	    
	public DeliveryForm(String usercompany){
		frame.setSize(600, 700);
		frame.setLocation(180, 150);
		
		Container CompantPane = frame.getContentPane();
		JPanel jpane = new JPanel();
		jpane.setLayout(null);
		JPanel bottomjpane = new JPanel();
		bottomjpane.setLayout(new GridLayout(1,4));
		OrderList.setBounds(20, 20, 120, 30);
		OrderList.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				bottomjpane.add(Delivery);
				jpane.remove(scrollPane);
				String OrdercolNames[] = {"OrderID","ProductID","ProductName","amount","OrderCompany","OrderDate"};
				model = new DefaultTableModel(OrdercolNames,0);
				table = new JTable(model);
				scrollPane = new JScrollPane(table);
				scrollPane.setBounds(0, 100, 583, 500);
				jpane.add(scrollPane);
				initOrderList(usercompany);
			}
			
		});
		DeliveryList.setBounds(160, 20, 120, 30);
		DeliveryList.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				bottomjpane.remove(Delivery);
				jpane.remove(scrollPane);
				String DeliverycolNames[] = {"DeliveryID","ProductID","ProductName","amount","ReceiveCompany","DeliveryDate"};
				model = new DefaultTableModel(DeliverycolNames,0);
				table = new JTable(model);
				scrollPane = new JScrollPane(table);
				scrollPane.setBounds(0, 100, 583, 500);
				jpane.add(scrollPane);
				initDeliveryList(usercompany);
			}
			
		});
		ReturnList.setBounds(300, 20, 120, 30);
		ReturnList.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				bottomjpane.remove(Delivery);
				jpane.remove(scrollPane);
				String BackcolNames[] = {"BackID","ProductID","ProductName","amount","BackCompany","BackDate"};
				model = new DefaultTableModel(BackcolNames,0);
				table = new JTable(model);
				scrollPane = new JScrollPane(table);
				scrollPane.setBounds(0, 100, 583, 500);
				jpane.add(scrollPane);
				initReturnList(usercompany);				
			}
			
		});
		
		Market.setBounds(440, 20, 120, 30);
		Market.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				DelMarketF = new DeliveryMarketForm();
				
			}
			
		});
		Delivery.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selectrow = table.getSelectedRow();
				if(selectrow > -1)
				{
					DeliveryF = new AddDeliveryForm(usercompany);
				}
				else
				{
					JOptionPane.showMessageDialog(null, "값을 선택해주세요.");
				}
			}
			
		});
		
		Select.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				switch(NowStatus.getText()){
					case "OrderList":
						Orderselect(usercompany,CompanyText.getText());
						CompanyText.setText("");
					break;
				
					case "DeliveryList":
						Deliveryselect(usercompany,CompanyText.getText());
						CompanyText.setText("");
					break;
				
					case "ReturnList":
						Returnselect(usercompany,CompanyText.getText());
						CompanyText.setText("");
					break;
				
					default :
						JOptionPane.showMessageDialog(null, "검색 할 List를 선택하세요");
						CompanyText.setText("");
				
				}
										
			}
			
		});
		
		NowStatus.setBounds(240, 60, 180, 30);
		NowStatus.setFont(new Font("",Font.BOLD,20));
		Company.setFont(new Font("",Font.BOLD,20));
		
		
		String OrdercolNames[] = {"OrderID","ProductID","ProductName","amount","OrderCompany","OrderDate"};
		model = new DefaultTableModel(OrdercolNames,0);
		table = new JTable(model);
		scrollPane = new JScrollPane(table);
		scrollPane.setBounds(0, 100, 583, 500);
		
		jpane.add(scrollPane);
		jpane.add(OrderList);
		jpane.add(DeliveryList);
		jpane.add(ReturnList);
		jpane.add(Market);
		jpane.add(NowStatus);
		
		bottomjpane.add(Company);
		bottomjpane.add(CompanyText);
		bottomjpane.add(Select);
		bottomjpane.add(Delivery);
		CompantPane.add(jpane);
		CompantPane.add(BorderLayout.SOUTH, bottomjpane);
		frame.setVisible(true);
     }
	public void initOrderList(String usercompany){
		 try {
			    NowStatus.setText("OrderList");
			    model.setRowCount(0);
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("select a.demand_id , b.product_id , b.product_name , a.amount , a.demand_company , a.demand_date from demand a , market b where a.product_id = b.product_id and b.sale_company = '" + usercompany + "';");
				while(rs.next())
				{
					model.addRow(new Object[]{rs.getString("a.demand_id") , rs.getString("b.product_id") , rs.getString("b.product_name"), rs.getString("a.amount") ,rs.getString("a.demand_company") ,rs.getString("a.demand_date") });
				}
				stmt.close();
				conn.close();
				
			}catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			catch(SQLException se){
									
			}
	 }
    
	public void initDeliveryList(String usercompany)
	{
		try {
		    NowStatus.setText("DeliveryList");
		    model.setRowCount(0);
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select a.delivery_id , a.product_id , b.product_name , a.amount , a.receive_company , a.delivery_date from delivery a , market b where a.product_id = b.product_id and b.sale_company = '" + usercompany + "';");
			while(rs.next())
			{
				model.addRow(new Object[]{rs.getString("a.delivery_id") , rs.getString("a.product_id") ,rs.getString("b.product_name") , rs.getString("a.amount"), rs.getString("a.receive_company") ,rs.getString("a.delivery_date")});
			}
			stmt.close();
			conn.close();
			
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch(SQLException se){
								
		}
	}
	
	public void initReturnList(String usercompany){
		 try {
			    NowStatus.setText("ReturnList");
			    model.setRowCount(0);
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("select a.back_id , a.product_id , b.product_name , a.amount , a.back_company ,a.back_date from back a , market b where a.product_id = b.product_id and b.sale_company ='" + usercompany + "';");
				while(rs.next())
				{
					model.addRow(new Object[]{rs.getString("a.back_id") , rs.getString("a.product_id") , rs.getString("b.product_name"), rs.getString("a.amount") ,rs.getString("a.back_company") ,rs.getString("a.back_date") });
				}
				stmt.close();
				conn.close();
				
			}catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			catch(SQLException se){
									
			}
	}
	
	public void Orderselect(String usercompany , String ordercompanyID)
	{
		try {
			
			model.setRowCount(0);
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select a.demand_id , b.product_id , b.product_name , a.amount , a.demand_company , a.demand_date from demand a , market b where a.product_id = b.product_id and b.sale_company = '" + usercompany + "' and a.demand_company = '" + ordercompanyID + "';");
			while(rs.next())
			{
				model.addRow(new Object[]{rs.getString("a.demand_id") , rs.getString("b.product_id") , rs.getString("b.product_name"), rs.getString("a.amount") ,rs.getString("a.demand_company") ,rs.getString("a.demand_date") });
			}
			stmt.close();
			conn.close();
			
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch(SQLException se){
								
		}
	}
	public void Deliveryselect(String usercompany ,String deliverycompanyID){
	try {
			
			model.setRowCount(0);
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select a.delivery_id , a.product_id , b.product_name , a.amount , a.receive_company , a.delivery_date from delivery a , market b where a.product_id = b.product_id and b.sale_company =  '" + usercompany + "' and a.receive_company = '" + deliverycompanyID + "';");
			while(rs.next())
			{
				model.addRow(new Object[]{rs.getString("a.delivery_id") , rs.getString("a.product_id") ,rs.getString("b.product_name") , rs.getString("a.amount"), rs.getString("a.receive_company") ,rs.getString("a.delivery_date")});
			}
			stmt.close();
			conn.close();
			
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch(SQLException se){
								
		}
	}
	public void Returnselect(String usercompany , String Returncompany){
		try {
		    NowStatus.setText("ReturnList");
		    model.setRowCount(0);
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select a.back_id , a.product_id , b.product_name , a.amount , a.back_company ,a.back_date from back a , market b where a.product_id = b.product_id and b.sale_company ='" + usercompany + "' and a.back_company = '" + Returncompany + "';");
			while(rs.next())
			{
				model.addRow(new Object[]{rs.getString("a.back_id") , rs.getString("a.product_id") , rs.getString("b.product_name"), rs.getString("a.amount") ,rs.getString("a.back_company") ,rs.getString("a.back_date") });
			}
			stmt.close();
			conn.close();
			
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch(SQLException se){
								
		}
	}
	public class AddDeliveryForm
	{
		private Connection conn = null;
		private Statement stmt = null;
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		private JFrame frame = new JFrame("DeliveryForm");
		private JLabel DeliveryID = new JLabel("DeliveryID");
		private JTextField DeliveryText = new JTextField();
		private JButton Delivery = new JButton("Delivery");
				
		
	public AddDeliveryForm(String usercompany){
			frame.setSize(300, 300);
			frame.setLocation(400, 300);
			Container CompantPane = frame.getContentPane();
			frame.setLayout(null);
			DeliveryID.setBounds(10, 10, 100, 25);
			DeliveryID.setFont(new Font("",Font.BOLD,20));
			DeliveryText.setBounds(110, 10, 150, 25);
			DeliveryText.setFont(new Font("",Font.BOLD,20));
			Delivery.setBounds(90, 40, 120, 25);
			Delivery.setFont(new Font("",Font.BOLD,20));
			Delivery.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(checkdelivery())
					{
						JOptionPane.showMessageDialog(null, "존재하는 deliveryID 입니다.");
					}
					else
					{
						DeliveryDB();
						
					}
					
					
				}
				
			});
			CompantPane.add(DeliveryID);
			CompantPane.add(DeliveryText);
			CompantPane.add(Delivery);
					
			frame.setVisible(true);
						
		}
	public void DeliveryDB(){
	    	try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
				stmt = conn.createStatement();
				
				String DeliveryID = DeliveryText.getText();
				int selectrow = table.getSelectedRow();
				String ProductID = "";
				if(selectrow > -1)
				{
					ProductID = (table.getValueAt(selectrow, 1)).toString();
				}
				else
				{
					JOptionPane.showMessageDialog(null, "물품을 선택하세요.");
				}
				int amount = Integer.valueOf((table.getValueAt(selectrow, 3)).toString());
				String Receovecompany = (table.getValueAt(selectrow, 4)).toString();
				String Deliverydate = dateFormat.format(calendar.getTime());
				int result = stmt.executeUpdate("insert into delivery(delivery_id ,product_id ,amount , receive_company, delivery_date ) values('" + DeliveryID +"','"
						+ ProductID +"', " + amount + ", '" + Receovecompany+ "', '" + Deliverydate+ "');");
				if(result > 0)
				{
					JOptionPane.showMessageDialog(null, "납품 하였습니다.");
					frame.dispose();
				}else
				{
					JOptionPane.showMessageDialog(null, "납품 실패 하였습니다.");
				}
				stmt.close();
				conn.close();
				DeliveryText.setText("");
					
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			catch(SQLException se){
									
			}
		}	
	
	public boolean checkdelivery()
	{
		boolean checkproductID = false;
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select delivery_id from delivery;");
			while(rs.next())
			{
				if((DeliveryText.getText()).equals(rs.getString("delivery_id")))
				 {
					checkproductID = true;
				 }
			}
			
			stmt.close();
			conn.close();
			
			
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch(SQLException se){
								
		}
		return checkproductID;
	}

	}
}



