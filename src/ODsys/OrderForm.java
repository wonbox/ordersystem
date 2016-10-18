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


public class OrderForm{
	
	private OrderMarketForm MarketF; 
	private ReturnForm returnF;
	private intoInventory invenF;
	private Connection conn = null;
	private Statement stmt = null;
	private String colNames[];
	private JScrollPane scrollPane;
	private DefaultTableModel model;
	private JTable jtable;	
	private JFrame frame = new JFrame("OrderFrom");
	private JButton StockStatus = new JButton("StockStatus");
	private JLabel ProductIDLabel = new JLabel("ProductID");
	private JButton Select = new JButton("Select");
	private JButton Market = new JButton("Market");
	private JButton DeliveryList = new JButton("DeliveryList");
	private JLabel NowStatus = new JLabel("NowStatus");
    private JButton Return = new JButton("Return");
	private JTextField SelectText = new JTextField();
	private JButton inventory = new JButton("Inventory");
	
    
	public OrderForm(String usercompany)
	{
		
		frame.setSize(600, 700);
		frame.setLocation(200, 200);
		Container CompantPane = frame.getContentPane();
		JPanel jpane = new JPanel();
		JPanel bottomjpane = new JPanel();
		jpane.setLayout(null);
		bottomjpane.setLayout(new GridLayout(1,5));
		String colNames[] = {"invenID","ProductID","ProductName","amount","price"};
		model = new DefaultTableModel(colNames,0);
		jtable = new JTable(model);
		scrollPane = new JScrollPane(jtable);
		scrollPane.setBounds(0, 100, 583, 500);;		
		
		NowStatus.setBounds(240, 60, 180, 30);
		NowStatus.setFont(new Font("",Font.BOLD,20));
		
		initStock(usercompany);	
		StockStatus.setBounds(40, 20, 110, 30);
		StockStatus.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				bottomjpane.remove(inventory);
				bottomjpane.add(Return);
				jpane.remove(scrollPane);
				String colNames[] = {"invenID","ProductID","ProductName","amount","price"};;
				model = new DefaultTableModel(colNames,0);
				jtable = new JTable(model);
				scrollPane = new JScrollPane(jtable);
				scrollPane.setBounds(0, 100, 583, 500);
				jpane.add(scrollPane);
				initStock(usercompany);
				
			}
			
		});
		DeliveryList.setBounds(200, 20, 110, 30);
		DeliveryList.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				bottomjpane.remove(Return);
				bottomjpane.add(inventory);
				jpane.remove(scrollPane);
				String colNames[] = {"DeliveryID","ProductID","ProductName","amount","DeliveryCompany","DeliveryDate"};
				model = new DefaultTableModel(colNames,0);
				jtable = new JTable(model);
				scrollPane = new JScrollPane(jtable);
				scrollPane.setBounds(0, 100, 583, 500);
				jpane.add(scrollPane);
				initDeliveryList(usercompany);
				
			}
			
		});
		Market.setBounds(430, 20, 120, 30);
		Market.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				MarketF = new OrderMarketForm(usercompany);
			}
			
		});
		
		Select.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				switch(NowStatus.getText()){
				case "StockList":
					stockselect(usercompany,SelectText.getText());
					SelectText.setText("");
				break;
			
				case "DeliveryList":
					deliveryselect(usercompany,SelectText.getText());
					SelectText.setText("");
				break;
						
				default :
					JOptionPane.showMessageDialog(null, "검색 할 List를 선택하세요");
					SelectText.setText("");
			
			}
		}
			
	});
		
		Return.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectrow = jtable.getSelectedRow();
				
				if(selectrow > -1)
				{
					returnF = new ReturnForm(usercompany);
				}
				else
				{
					JOptionPane.showMessageDialog(null, "값을 선택해주세요.");
				}
				
			}
			
		});
		inventory.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selectrow = jtable.getSelectedRow();
				
				if(selectrow > -1)
				{
					//창고 안 똑같은 물품이 존재 하는 경우
					if((Inventorycheck(usercompany)).equals((jtable.getValueAt(selectrow, 1)).toString()))
					{
						update((jtable.getValueAt(selectrow, 1)).toString(),usercompany); //수량 수정
						delete((jtable.getValueAt(selectrow, 0)).toString()); 
						initDeliveryList(usercompany);
					}
					else
					{
						invenF = new intoInventory(usercompany,(jtable.getValueAt(selectrow, 1)).toString() ,Integer.valueOf((jtable.getValueAt(selectrow, 3)).toString()));
					}
				}
				else
				{
					JOptionPane.showMessageDialog(null, "값을 선택해주세요.");
				}
			}
			
		});
		ProductIDLabel.setFont(new Font("",Font.BOLD,25));
		SelectText.setFont(new Font("",Font.BOLD,25));
		jpane.add(NowStatus);
		jpane.add(scrollPane);
		jpane.add(StockStatus);
		jpane.add(Market);
		jpane.add(DeliveryList);
		bottomjpane.add(ProductIDLabel);
		bottomjpane.add(SelectText);
		bottomjpane.add(Select);
		bottomjpane.add(Return);
		
		
		
		CompantPane.add(jpane);
		CompantPane.add(BorderLayout.SOUTH, bottomjpane);
		frame.setVisible(true);
	}
	//창고 목록을 보여줌
	public void initStock(String usercompany)
	{
		try {
			NowStatus.setText("StockList");
			model.setRowCount(0);
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select a.inventory_id, a.product_id , b.product_name , a.amount , b.unit_price from inventory a , market b , company c where a.product_id = b.product_id and a.inventory_company = c.company_id and c.company_id = '" + usercompany +"';");
			while(rs.next())
			{
				model.addRow(new Object[]{rs.getString("a.inventory_id") , rs.getString("a.product_id") , rs.getString("b.product_name"), rs.getString("a.amount") ,rs.getString("b.unit_price")});
			}
			stmt.close();
			conn.close();
			
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch(SQLException se){
								
		}
		
	}
    //배달 목록 보여줌
	public void initDeliveryList(String usercompany){
		try {
		    NowStatus.setText("DeliveryList");
		    model.setRowCount(0);
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select a.delivery_id , a.product_id , b.product_name , a.amount , b.sale_company , a.delivery_date from delivery a , market b where a.product_id = b.product_id and a.receive_company = '" + usercompany + "';");
			while(rs.next())
			{
				model.addRow(new Object[]{rs.getString("a.delivery_id") , rs.getString("a.product_id") ,rs.getString("b.product_name") , rs.getString("a.amount"), rs.getString("b.sale_company") ,rs.getString("a.delivery_date")});
			}
			stmt.close();
			conn.close();
			
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch(SQLException se){
								
		}
	}
	
	/*창고 안 물품 한가지 총가격 환산 함수
	public int sumprice()
    {
    	int sum = 0;
		int rowNum = model.getRowCount();
    	
    	
    	for(int i= 0 ; i < rowNum ; i++)
    	{
    		Object amount = model.getValueAt(i,3);
    		Object price = model.getValueAt(i, 4);
    		sum = sum + Integer.valueOf(amount.toString()) * Integer.valueOf(price.toString());
    	}
    		
    		return sum;
    }*/
	
	//창고 목록에서 원하는 상품 검색
	public void stockselect(String usercompany , String ProductID)
	{
		try {
			
			model.setRowCount(0);
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select a.inventory_id, a.product_id , b.product_name , a.amount , b.unit_price from inventory a , market b , company c where a.product_id = b.product_id and a.inventory_company = c.company_id and c.company_id = '" + usercompany +"' and a.product_id = '" + ProductID + "';");
			while(rs.next())
			{
				model.addRow(new Object[]{rs.getString("a.inventory_id") , rs.getString("a.product_id") , rs.getString("b.product_name"), rs.getString("a.amount") ,rs.getString("b.unit_price")});
			}
			stmt.close();
			conn.close();
			
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch(SQLException se){
								
		}
	}
	//배달 목록에서 원하는 상품 검색
	public void deliveryselect(String usercompany , String ProductID)
	{
		try {
			
			model.setRowCount(0);
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select a.delivery_id , a.product_id , b.product_name , a.amount , b.sale_company , a.delivery_date from delivery a , market b where a.product_id = b.product_id and a.receive_company = '" + usercompany +"' and a.product_id = '" + ProductID + "';");
			while(rs.next())
			{
				model.addRow(new Object[]{rs.getString("a.delivery_id") , rs.getString("a.product_id") ,rs.getString("b.product_name") , rs.getString("a.amount"), rs.getString("b.sale_company") ,rs.getString("a.delivery_date")});
			}
			stmt.close();
			conn.close();
			
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch(SQLException se){
								
		}
	}
	
	//배달 온 상품을 창고에 추가 하기 전 같은 상품이 있는지 검사
	public String Inventorycheck(String usercompany){
		String checkproductID = "";
		int selectrow = jtable.getSelectedRow();
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select a.product_id from inventory a , market b , company c where a.product_id = b.product_id and a.inventory_company = c.company_id and a.inventory_company = '" + usercompany +"';");
			
			while(rs.next())
			{
				
				if(((jtable.getValueAt(selectrow, 1)).toString()).equals(rs.getString("a.product_id")))
				 {
					 checkproductID = rs.getString("a.product_id");
					
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
	//창고에 기존에 존재하던 상품이 추가 될 경우 수량 수정
	public void update(String productID , String usercompany){
		try {
			int selectrow = jtable.getSelectedRow();
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
			stmt = conn.createStatement();
			int result = stmt.executeUpdate("update inventory set amount = amount + " + Integer.valueOf(jtable.getValueAt(selectrow, 3).toString()) + " where product_id ='" + productID + "' and inventory_company = '" + usercompany + "';");
			
			if(result > -1)
			{
				JOptionPane.showMessageDialog(null, "창고에 저장되었습니다.");
			}
			else
			{
				JOptionPane.showMessageDialog(null, "실패하였습니다.");
			}
			stmt.close();
			conn.close();
			
			
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch(SQLException se){
								
		}
		
					
	}
	//배달 DB 삭제
	public void delete(String DeliveryID){
		try {
			int selectrow = jtable.getSelectedRow();
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
			stmt = conn.createStatement();
			int result = stmt.executeUpdate("delete from delivery where delivery_id = '" +  DeliveryID +"';");
			stmt.close();
			conn.close();
			
			
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch(SQLException se){
								
		}
	}
	//반품 값 입력  Form
	public class ReturnForm
	{
		private Connection conn = null;
		private Statement stmt = null;
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		private JFrame frame = new JFrame("ReturnForm");
		private JLabel returnID = new JLabel("ReturnID");
		private JTextField returnText = new JTextField();
		private JButton Return = new JButton("Return");
				
		
		public ReturnForm(String usercompany){
			frame.setSize(300, 300);
			frame.setLocation(400, 300);
			Container CompantPane = frame.getContentPane();
			frame.setLayout(null);
			
			returnID.setBounds(10, 10, 100, 25);
			returnID.setFont(new Font("",Font.BOLD,20));
			returnText.setBounds(110, 10, 150, 25);
			returnText.setFont(new Font("",Font.BOLD,20));
			Return.setBounds(100, 40, 100, 25);
			Return.setFont(new Font("",Font.BOLD,20));
			Return.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(checkReturn())
					{
						JOptionPane.showMessageDialog(null, "존재하는 ReturnID 입니다.");
					}
					else
					{
					addReturn(usercompany);	
					initStock(usercompany);
					}
				}
				
			});
			CompantPane.add(returnID);
			CompantPane.add(returnText);
			CompantPane.add(Return);
					
			frame.setVisible(true);
						
		}
		//반품 추가 DB 삽입
		public void addReturn(String usercompany)
		{
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
				stmt = conn.createStatement();
				
				String BackID = returnText.getText();
				int selectrow = jtable.getSelectedRow();
				String ProductID = "";
				if(selectrow > -1)
				{
					ProductID = (jtable.getValueAt(selectrow, 1)).toString();
				}
				else
				{
					JOptionPane.showMessageDialog(null, "물품을 선택하세요.");
				}
				int amount = Integer.valueOf((jtable.getValueAt(selectrow, 3)).toString());
				String backcompany = usercompany;
				String backdate = dateFormat.format(calendar.getTime());
				int result = stmt.executeUpdate("insert into back(back_id ,product_id ,amount ,back_company,back_date) values('" + BackID +"','"
						+ ProductID +"', " + amount + ", '" + backcompany + "', '" + backdate+ "');");
				
				if(result > 0)
				{
					JOptionPane.showMessageDialog(null, "반품처리 되었습니다.");
					deleteDB();
					frame.dispose();
						
				}else
				{
					JOptionPane.showMessageDialog(null, "반품 실패 하였습니다.");
				}
							
				stmt.close();
				conn.close();
				
					
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			catch(SQLException se){
									
			}
			
		}
		//인벤DB 삭제
		public void deleteDB()
		{
			try{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
			stmt = conn.createStatement();
			int selectrow = jtable.getSelectedRow();
			String inventoryID = (jtable.getValueAt(selectrow, 0)).toString();
 			stmt.executeUpdate("delete from inventory where inventory_id = '" + inventoryID + "';");
			stmt.close();
			conn.close();
			
			
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch(SQLException se){
								
		}
		}
	
		public boolean checkReturn()
		{
			boolean checkproductID = false;
			try {
				
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("select back_id from back;");
				while(rs.next())
				{
					if((returnText.getText()).equals(rs.getString("back_id")))
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
	
	//인벤토리 값 입력 form
	public class intoInventory{
		private Connection conn = null;
		private Statement stmt = null;
		private JFrame frame = new JFrame("intoInventoryForm");
		private JLabel InventoryID = new JLabel("InventoryID");
		private JTextField InventoryText = new JTextField();
		private JButton Inventory = new JButton("IntoStock");
		public intoInventory(String usercompany , String productID , int amount)
		{
			frame.setSize(300, 300);
			frame.setLocation(400, 300);
			Container CompantPane = frame.getContentPane();
			frame.setLayout(null);
			InventoryID.setBounds(10, 10, 140, 25);
			InventoryID.setFont(new Font("",Font.BOLD,20));
			InventoryText.setBounds(110, 10, 150, 25);
			InventoryText.setFont(new Font("",Font.BOLD,20));
			Inventory.setBounds(90, 40, 140, 25);
			Inventory.setFont(new Font("",Font.BOLD,20));
			Inventory.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(checkInven())
					{
						
						JOptionPane.showMessageDialog(null, "존재하는 InvenID 입니다.");
						
					}
					else
					{
						int selectrow = jtable.getSelectedRow();
						insertInventory(productID,usercompany,amount);
						delete((jtable.getValueAt(selectrow, 0)).toString());
						initDeliveryList(usercompany);
					}
				}
				
			});
			
			CompantPane.add(InventoryID);
			CompantPane.add(InventoryText);
			CompantPane.add(Inventory);
			frame.setVisible(true);
		}
		//인벤토리DB 삽입
		public void insertInventory(String productID ,String usercompany , int amount){
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
				stmt = conn.createStatement();
				
				String InvenID = InventoryText.getText();
				
				int r = stmt.executeUpdate("insert into inventory(inventory_id , product_id , amount , inventory_company) values('" + InvenID + "','" + productID+ "'," + amount + ",'" + usercompany + "');");
				
				if(r > -1)
				{
					JOptionPane.showMessageDialog(null, "창고에 저장되었습니다.");
					frame.dispose();
				}
				else
				{
					JOptionPane.showMessageDialog(null, "실패하였습니다.");
					
				}
				
				stmt.close();
				conn.close();
				InventoryText.setText("");
							
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			catch(SQLException se){
									
			}
		}
		//인벤토리 id 값 중복 검사
		public boolean checkInven()
		{
			boolean checkproductID = false;
			try {
				
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("select inventory_id from inventory;");
				while(rs.next())
				{
					if((InventoryText.getText()).equals(rs.getString("inventory_id")))
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
