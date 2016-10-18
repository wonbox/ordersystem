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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import ODsys.OrderMarketForm.OrderForm;

public class DeliveryMarketForm {
	private addForm AddF;
	private updateForm updateF;
	private Connection conn = null;
	private Statement stmt = null;
	private JFrame frame = new JFrame("DeliveryMarket");
	private String colNames[] = {"ProductID","ProductName","UnitPrice","SaleCompanyID"};
	private DefaultTableModel model = new DefaultTableModel(colNames,0);
	private JTable table = new JTable(model);
	private JScrollPane scrollPane = new JScrollPane(table);
	private JButton add = new JButton("add");
	private JButton revise = new JButton("revise");
	public DeliveryMarketForm()
    {
		frame.setSize(600, 700);
		frame.setLocation(250, 150);
		Container CompantPane = frame.getContentPane();
		scrollPane.setSize(583, 800);
		JPanel Panel = new JPanel();
		initMarket();
		
			
		add.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selectrow = table.getSelectedRow();
							
				if(selectrow >= -1)
				{
					AddF = new addForm();
				}
				else
				{
					JOptionPane.showMessageDialog(null, "값을 선택해주세요.");
				}
			}
		});
		
		revise.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selectrow = table.getSelectedRow();
				if(selectrow > -1)
				{
					updateF = new updateForm();
				}
				else
				{
					JOptionPane.showMessageDialog(null, "값을 선택해주세요.");
				}
				
				
			}
			
		});
	
		Panel.add(add);
		Panel.add(revise);
		CompantPane.add(BorderLayout.NORTH , scrollPane);
		CompantPane.add(BorderLayout.SOUTH , Panel);
		frame.setVisible(true);
    
    }
	//마켓 리스트 보여줌
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
	
	public class addForm
	{
	    
		private Connection conn = null;
		private Statement stmt = null;
		private JFrame frame = new JFrame("AddMarket");
		private JButton register = new JButton("Register");
		private JLabel ProductIDLabel = new JLabel("ProductID");
		private JLabel ProductNameLabel = new JLabel("ProductName");
		private JLabel PriceLabel = new JLabel("UnitPrice");
		private JLabel CompanyID = new JLabel("CompanyID");
		private JTextField IDText = new JTextField();
		private JTextField NameText = new JTextField();
		private JTextField PriceText = new JTextField();
		private JTextField CompanyText = new JTextField();
		private JPanel panel = new JPanel();
		
	public addForm()
		{
			frame.setSize(400, 450);
			frame.setLocation(500, 300);
			Container contentPane = frame.getContentPane();
			panel.setLayout(null);
			register.setBounds(150, 370, 100, 25);
			register.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if(checkProduct())
					{
						JOptionPane.showMessageDialog(null, "존재하는 ProductID 입니다.");
					}
					else
					{
						AddMarket();
					}
				}
				
			});
			ProductIDLabel.setBounds(20, 30, 100, 25);
			ProductIDLabel.setFont(new Font("",Font.BOLD,20));
			ProductNameLabel.setBounds(20, 70, 140, 25);
			ProductNameLabel.setFont(new Font("",Font.BOLD,20));
			PriceLabel.setBounds(20, 110, 100, 25);
			PriceLabel.setFont(new Font("",Font.BOLD,20));
			CompanyID.setBounds(20, 150, 120, 25);
			CompanyID.setFont(new Font("",Font.BOLD,20));
			IDText.setBounds(170, 30, 160, 25);
			NameText.setBounds(170, 70, 160, 25);
			PriceText.setBounds(170, 110, 160, 25);
			CompanyText.setBounds(170, 150, 160, 25);
			
			panel.add(register);
			panel.add(ProductIDLabel);
			panel.add(ProductNameLabel);
			panel.add(PriceLabel);
			panel.add(CompanyID);
			panel.add(IDText);
			panel.add(PriceText);
			panel.add(NameText);
			panel.add(CompanyText);
			contentPane.add(panel);
			frame.setVisible(true);
		}
		//마켓에 물품 추가
	public void AddMarket()
		{
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
				stmt = conn.createStatement();
				
				String ID = IDText.getText();
				String Name =  NameText.getText();
				int price = Integer.valueOf(PriceText.getText());
				String Company = CompanyText.getText();
				
				stmt.executeUpdate("insert into market(product_id , product_name , unit_price , sale_company) values('" + ID +"','"
						+ Name +"'," + price + ",'" + Company+ "');");
				JOptionPane.showMessageDialog(null, "상품을 등록하였습니다.");
				stmt.close();
				conn.close();
				NameText.setText("");
				IDText.setText("");
				PriceText.setText("");
				CompanyText.setText("");
				initMarket();
				frame.dispose();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			catch(SQLException se){
									
			}
		}
	//자신의 회사가 등록한 상품이 마켓이 존재하는지 검사
	public boolean checkProduct()
	{
		boolean checkproductID = false;
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select product_id from market;");
			while(rs.next())
			{
				if((IDText.getText()).equals(rs.getString("product_id")))
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
	
    public class updateForm{
		
		private Connection conn = null;
		private Statement stmt = null;
		private JFrame frame = new JFrame("AddMarket");
		private JButton revise = new JButton("Revise ");
		private JLabel ProductIDLabel = new JLabel("ProductID");
		private JLabel ProductNameLabel = new JLabel("ProductName");
		private JLabel PriceLabel = new JLabel("UnitPrice");
		private JLabel CompanyID = new JLabel("CompanyID");
		private JLabel IDText = new JLabel();
		private JTextField NameText = new JTextField();
		private JTextField PriceText = new JTextField();
		private JTextField CompanyLabel = new JTextField(); 
		private JPanel panel = new JPanel();
		
	public updateForm()
		{
			frame.setSize(400, 450);
			frame.setLocation(400, 300);
			Container contentPane = frame.getContentPane();
			panel.setLayout(null);
			revise.setBounds(150, 370, 100, 25);
			revise.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					updateMarket();
					
				}
				
			});
			ProductIDLabel.setBounds(20, 30, 100, 25);
			ProductIDLabel.setFont(new Font("",Font.BOLD,20));
			ProductNameLabel.setBounds(20, 70, 130, 25);
			ProductNameLabel.setFont(new Font("",Font.BOLD,20));
			PriceLabel.setBounds(20, 110, 100, 25);
			PriceLabel.setFont(new Font("",Font.BOLD,20));
			CompanyID.setBounds(20, 150, 130, 25);
			CompanyID.setFont(new Font("",Font.BOLD,20));
			IDText.setBounds(170, 30, 160, 25);
			NameText.setBounds(170, 70, 160, 25);
			PriceText.setBounds(170, 110, 160, 25);
			CompanyLabel.setBounds(170, 150, 160, 25);
			
			int selectrow = table.getSelectedRow();
			IDText.setText((table.getValueAt(selectrow, 0)).toString());
			NameText.setText((table.getValueAt(selectrow, 1)).toString());
			PriceText.setText((table.getValueAt(selectrow, 2)).toString());
			CompanyLabel.setText((table.getValueAt(selectrow, 3)).toString());
			
			panel.add(revise);
			panel.add(ProductIDLabel);
			panel.add(ProductNameLabel);
			panel.add(PriceLabel);
			panel.add(CompanyID);
			panel.add(IDText);
			panel.add(PriceText);
			panel.add(NameText);
			panel.add(CompanyLabel);
			contentPane.add(panel);
			frame.setVisible(true);
		}
		//마켓에 등록한 상품 상세정보 수정
	public void updateMarket()
		{
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/odsys","root","0507");
				stmt = conn.createStatement();
				
				String ID = IDText.getText();
				String Name =  NameText.getText();
				int price = Integer.valueOf(PriceText.getText());
				String Company = CompanyLabel.getText();
				
				int result = stmt.executeUpdate("update market set product_name = '" + Name + "', unit_price = " + price + " , sale_company = '" + Company + "' where product_id = '" + ID + "';");
				if(result > 0)
				{
					JOptionPane.showMessageDialog(null, "수정되었습니다.");
				}else
				{
					JOptionPane.showMessageDialog(null, "실패했습니다.");
				}
				stmt.close();
				conn.close();
				frame.dispose();
				initMarket();			
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			catch(SQLException se){
									
			}
		}
	}	

}
