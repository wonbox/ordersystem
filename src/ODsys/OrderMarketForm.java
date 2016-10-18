package ODsys;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
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

public class OrderMarketForm {
	private OrderForm orderF;
	private Connection conn = null;
	private Statement stmt = null;
	private JFrame frame = new JFrame("Market");
	private String colNames[] = {"ProductID","ProductName","UnitPrice","SaleCompanyID"};
	private DefaultTableModel model = new DefaultTableModel(colNames,0);
	private JTable table = new JTable(model);
	private JScrollPane scrollPane = new JScrollPane(table);
	private JButton Order = new JButton("Order");
	
    public OrderMarketForm(String usercompany)
	{
		frame.setSize(600, 700);
		frame.setLocation(180, 150);
		Container CompantPane = frame.getContentPane();
		scrollPane.setSize(583, 800);
		JPanel Panel = new JPanel();
		initMarket();
		Order.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selectrow = table.getSelectedRow();
				if(selectrow > -1)
				{
					orderF = new OrderForm(usercompany);
				}
				else
				{
					JOptionPane.showMessageDialog(null, "값을 선택해주세요.");
				}
			}
			
		});
		Panel.add(Order);
		CompantPane.add(BorderLayout.NORTH , scrollPane);
		CompantPane.add(BorderLayout.SOUTH , Panel);
		frame.setVisible(true);
	}
    //마켓 목록 보여줌
	public void initMarket()
	{
		try {
			model.setRowCount(0);
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select product_id , product_name , unit_price , sale_company from market ;");
			while(rs.next())
			{
				model.addRow(new Object[]{rs.getString("product_id") , rs.getString("product_name") , rs.getString("unit_price"), rs.getString("sale_company")});
			}
			stmt.close();
			conn.close();
			
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch(SQLException se){
								
		}
	}
	
	public class OrderForm
	{
		private Connection conn = null;
		private Statement stmt = null;
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		private JFrame frame = new JFrame("OrderForm");
		private JLabel amount = new JLabel("Amount");
		private JButton Order = new JButton("order");
		private JTextField amountText = new JTextField();
		private JLabel orderID = new JLabel("OrderID");
		private JTextField orderIDText = new JTextField();
		public OrderForm(String usercompany)
		{
			frame.setSize(300, 300);
			frame.setLocation(400, 300);
			Container CompantPane = frame.getContentPane();
			frame.setLayout(null);
			
			orderID.setBounds(10, 10, 100, 25);
			orderID.setFont(new Font("",Font.BOLD,20));
			orderIDText.setBounds(110, 10, 150, 25);
			orderIDText.setFont(new Font("",Font.BOLD,20));
			amount.setBounds(10, 50, 100, 25);
			amount.setFont(new Font("",Font.BOLD,20));
			amountText.setBounds(110, 50, 150, 25);
			amountText.setFont(new Font("",Font.BOLD,20));
			Order.setBounds(100, 100, 100, 25);
			Order.setFont(new Font("",Font.BOLD,20));
			Order.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(checkOrder())
					{
						JOptionPane.showMessageDialog(null, "존재하는 OrderID 입니다.");
						
					}else
					{
						addOrder(usercompany);
					}
					
					
				}
				
			});
			CompantPane.add(orderID);
			CompantPane.add(orderIDText);
			CompantPane.add(amount);
			CompantPane.add(amountText);
			CompantPane.add(Order);
			
			frame.setVisible(true);
		}
		
		
		//주문하기
		public void addOrder(String usercompany)
		{
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
				stmt = conn.createStatement();
				
				String OrderID = orderIDText.getText();
				int selectrow = table.getSelectedRow();
				String ProductID = "";
				if(selectrow > -1)
				{
					ProductID = (table.getValueAt(selectrow, 0)).toString();
				}
				else
				{
					JOptionPane.showMessageDialog(null, "물품을 선택하세요.");
				}
				String Amount = amountText.getText();
				String companyID = usercompany;
				String orderdate = dateFormat.format(calendar.getTime());
				int result = stmt.executeUpdate("insert into demand(demand_id ,product_id ,amount ,demand_company ,demand_date) values('" + OrderID +"','"
						+ ProductID +"', '" + Amount + "', '" + companyID+ "', '" + orderdate+ "');");
				if(result > -1)
				{
					JOptionPane.showMessageDialog(null, "주문이 완료되었습니다.");
					frame.dispose();
				}else
				{
					JOptionPane.showMessageDialog(null, "주문이 실패하였습니다.");
				}
				stmt.close();
				conn.close();
				orderIDText.setText("");
				amountText.setText("");
				
						
			
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			catch(SQLException se){
									
			}
			
		}
		//주문 하였는지 검사	
		public boolean checkOrder()
		{
			boolean checkproductID = false;
			try {
				
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("select demand_id from demand;");
				while(rs.next())
				{
					if((orderIDText.getText()).equals(rs.getString("demand_id")))
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





