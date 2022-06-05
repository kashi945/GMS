/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import database.Database;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;
import java.util.Date;
import javax.swing.ImageIcon;
/**
 *
 * @author Kashif
 */
public class CashierPage extends javax.swing.JFrame {

    
    Statement st;
    Connection conn;
    ResultSet rs;
    PreparedStatement pst;
    private String date;
    private int dlt;
    PrintScreen ps;
    
    public CashierPage() throws SQLException {
        
        initComponents();
        conn = Database.connection();
       
        updateCombo();
        updateTable();
        ps = new PrintScreen ();
        
        
        //Getting current date
        SimpleDateFormat dateTimeInGMT = new SimpleDateFormat("MMM dd,yyyy");
	dateTimeInGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
	date = dateTimeInGMT.format(new Date());
        
        //Setting Current date and Cashier's name
        jTextField10.setText (date);
        jTextField8.setText (CashierLogin.cashier);
        
    }
    
    
    public void updateTable()
    {
        String sql = "select * from SalesTemp";
        
        try{
            
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            jTable1.setModel(DbUtils.resultSetToTableModel(rs));
             
        pst.close();
        rs.close();
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null,e);
        }
       
    }
    
    public void updateTable1()
    {
        String sql = "select * from SalesTemp";
        
        try{
            
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            ps.jTable1.setModel(DbUtils.resultSetToTableModel(rs));
            
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null,e);
        }
        
        
    }

    
    public void newBillNo() {

        String get = "select MAX(BillNo)+1 from CashierSales";
        int id = 0;
        try {
            pst = conn.prepareStatement(get);
            rs = pst.executeQuery();
            if (rs.next()) {
                id = Integer.parseInt(rs.getString(1));
                jTextField1.setText("" + id);
                rs.close();
                pst.close();
            }

//          id++;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    
    public void updateCombo ()
    {
     
        String sql = "select * from ItemTable";
        
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next () )
            {
                jComboBox1.addItem(rs.getString("ProductID"));
            }
            rs.close();
            pst.close();
        }
        catch (Exception e)
        {
            
        }
        
    }
    
    public void updateQuan ()
    {
        int q = parseInt(jTextField7.getText()) - parseInt(jTextField2.getText());
        
//Gets the current productID
        String s = jComboBox1.getSelectedItem().toString();
        String sql = " update ItemTable set Quantity = ? where ProductID = " + s;
        
        //Query to update Available product field
        String sql2 = "select * from ItemTable where ProductID = "+s;
        
        try
        {
            pst = conn.prepareStatement(sql);
            pst.setString (1, valueOf(q));
            pst.executeUpdate();
            pst.close();// Updating available product field
            pst = conn.prepareStatement(sql2);
            rs=pst.executeQuery();
            jTextField7.setText(rs.getString(7));
            rs.close();
            pst.close();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e);
        }
    
    }
    
    public void SearchProduct ()
    {
        String a1 = jComboBox1.getSelectedItem().toString();
        String sql = "select * from ItemTable where ProductID='"+a1+"'";
        
        try{
            pst = conn.prepareStatement(sql);
            rs=pst.executeQuery();
            if (rs.next())
            {
                jTextField5.setText(rs.getString(2));
                jTextField6.setText(rs.getString(6));
                jTextField7.setText(rs.getString(7));
          //    jTextField9.setText(mul( jTextField2.getText() , jTextField6.getText() ));
          //    jTextField4.setText(mul( jTextField2.getText() , rs.getString(5) ));
                
                rs.close();
                pst.close();
                
                
            }
            else{
                JOptionPane.showMessageDialog(null,"No such Item Found");
            }
            
            
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null,e);
        }
        
        
    }
    
    public void deleteTable ()
    {
        
       
        String sql = "delete from SalesTemp";
        try {
               
                  pst = conn.prepareStatement(sql);
                  pst.executeUpdate();

                  pst.close();
                  updateTable();
              
            }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            e.printStackTrace();
        }
        
    }
    
      public void beforeRemove()
        {
            String method = null ;
            int cur_quan=0, new_quan = 0 ;
            String cell = null;
            int row = jTable1.getSelectedRow () ,cid;
            cid = Integer.parseInt(jComboBox1.getSelectedItem().toString());
            
            if(!jTable1.getSelectionModel().isSelectionEmpty())
            {
                cell = jTable1.getModel().getValueAt(row, 1).toString();
            }
           
            String sql = "SELECT sum(Quantity)  FROM SalesTemp where ProductId = " + cid ;
            String sql2 = "SELECT sum(Quantity)  FROM SalesTemp where ProductId = " + cell ;
            
        try {
              if (!jTable1.getSelectionModel().isSelectionEmpty())
            {
                pst = conn.prepareStatement(sql2);
                rs = pst.executeQuery();
                dlt = Integer.parseInt(rs.getString("sum(Quantity)"));
                method = "table";
            }
            
            if (jTable1.getSelectionModel().isSelectionEmpty())
            {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();
                dlt = Integer.parseInt(rs.getString("sum(Quantity)"));
                method = "find";
            }
            pst.close();
            rs.close();
        }
        catch (SQLException ex) 
        {
           ex.printStackTrace();
        }
        
        try 
        {
            if( method == "table")
            {
                pst = conn.prepareStatement("Select * from ItemTable where ProductID = "+ cell);
            }
            else if( method == "find")
            {
                pst = conn.prepareStatement("Select * from ItemTable where ProductID = "+ cid);
            }
            rs = pst.executeQuery();
            
            cur_quan = Integer.parseInt( rs.getString(7) );
            pst.close();
            rs.close();
            
 
            new_quan = (cur_quan) + dlt ;
            String st = null;
            if( method == "table")
            {
                st = "update ItemTable set "
                    + " Quantity = '" + new_quan + "'"
                    + " where ProductID = " + cell ;
            }
            else if( method == "find")
            {
                st = "update ItemTable set "
                    + " Quantity = '" + new_quan + "'"
                    + " where ProductID = " + cid ;
            }  
            
            jTextField7.setText(""+ new_quan );

            pst = conn.prepareStatement(st);
            pst.executeUpdate();
            
            pst.close();
          
        } 
        catch (SQLException ex) 
        {
            JOptionPane.showMessageDialog(null,ex);
            ex.printStackTrace();
        }
        }
        
        
        
        public void removeItem() {
       
        String cell = null;
        int row = jTable1.getSelectedRow () ,cid;
        cid = Integer.parseInt(jComboBox1.getSelectedItem().toString());
        if(!jTable1.getSelectionModel().isSelectionEmpty())
        {
            cell = jTable1.getModel().getValueAt(row, 1).toString();
        }
        
       
        String sql = "DELETE FROM  SalesTemp WHERE ProductId = " + cid;
        String sql2 = "DELETE FROM SalesTemp WHERE ProductId = " + cell;
        try {
              if (!jTable1.getSelectionModel().isSelectionEmpty())
              {
                  pst = conn.prepareStatement(sql2);
                  pst.executeUpdate();
                  JOptionPane.showMessageDialog(null, "Table Item deleted Successfully");
              }
            
            if (jTable1.getSelectionModel().isSelectionEmpty())
              {
                  pst = conn.prepareStatement(sql);
                  pst.executeUpdate();
                  JOptionPane.showMessageDialog(null, "Searched Item deleted Successfully");
              }
            
            updateTable();
            pst.close();
            }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            e.printStackTrace();
        }

    
       
         sql = "delete from CashierSales where ProductID = " + cell;
        try {
                
              if (!jTable1.getSelectionModel().isSelectionEmpty())
              {
                  pst = conn.prepareStatement(sql);
                  pst.executeUpdate();

                  JOptionPane.showMessageDialog(null, "Item Deleted" );
              }
              pst.close();
            }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            e.printStackTrace();
        }

    }
    
    public String mul ( String s1 , String s2)
    {
        double d1 = Double.parseDouble(s1);
        double d2 = Double.parseDouble(s2);
        String eq = String.valueOf(d1 * d2);
        return eq;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jTextField7 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Generate Bill");
        setIconImage(new ImageIcon(getClass().getResource("initialFrame.png")).getImage());

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/cart.png"))); // NOI18N
        jLabel1.setAlignmentY(3.0F);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Bill Generation");

        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("Date: ");

        jLabel13.setForeground(new java.awt.Color(0, 0, 0));
        jLabel13.setText("Cashier: ");

        jTextField10.setEditable(false);

        jTextField8.setEditable(false);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("Grocery Management");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 0, 0));
        jLabel14.setText("System");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(jLabel14)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(74, 74, 74)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(jLabel12))
                .addGap(8, 8, 8)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jTextField8)
                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(332, 332, 332))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addGap(6, 6, 6))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel13)
                                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel12)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jLabel3.setText("Bill No.");

        jTextField1.setEditable(false);
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel4.setText("Name");

        jLabel6.setText("Total Price");

        jLabel8.setText("Prod. ID");

        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel9.setText("Discount");

        jLabel11.setText("Quantity");

        jTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jLabel16.setText("Total");

        jTextField3.setEditable(false);
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/print.png"))); // NOI18N
        jButton1.setText("Print");
        jButton1.setToolTipText("");
        jButton1.setActionCommand("");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/cross.png"))); // NOI18N
        jButton9.setText("Remove");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/add +.png"))); // NOI18N
        jButton10.setText("Add");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jTextField4.setEditable(false);
        jTextField4.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jTextField5.setEditable(false);
        jTextField5.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jTextField6.setEditable(false);
        jTextField6.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/Search.png"))); // NOI18N
        jButton2.setText("Find");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextField7.setEditable(false);
        jTextField7.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel5.setText("/ Available");

        jTextField9.setEditable(false);
        jTextField9.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField9ActionPerformed(evt);
            }
        });

        jLabel7.setText("Sales Price");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(93, 93, 93)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(150, 150, 150)
                                .addComponent(jTextField1))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(144, 144, 144))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(65, 65, 65))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel9)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel8)
                                            .addComponent(jLabel7))
                                        .addGap(123, 123, 123)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(155, 155, 155)
                                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 694, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(112, 112, 112)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(292, 292, 292))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel16)
                .addGap(50, 50, 50)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(200, 200, 200))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(47, 47, 47)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addGap(66, 66, 66)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 399, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 49, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        
        beforeRemove();
        removeItem();
        
         String sql2 = "SELECT sum(TotalPrice)  FROM SalesTemp";
        try {
            pst = conn.prepareStatement(sql2);
            rs = pst.executeQuery();
            
            if (rs.next())
            {
                String sum = rs.getString("sum(TotalPrice)");
                jTextField3.setText(sum);
            }
            pst.close();
            rs.close();
            updateTable();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,ex);
            ex.printStackTrace();
        }
        
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed

        String tax_item=null, cost_item=null;
        String a1 = jComboBox1.getSelectedItem().toString();
        String sql = "select * from ItemTable where ProductID='"+a1+"'";
        String unit_disc = null;
        
        try
        {
            pst = conn.prepareStatement(sql);
            rs=pst.executeQuery();
            if (rs.next())
            {
                jTextField5.setText(rs.getString(2));
                jTextField6.setText(rs.getString(6));
                jTextField7.setText(rs.getString(7));
          //    jTextField9.setText(mul( jTextField2.getText() , jTextField6.getText() ));
                unit_disc = rs.getString(5) ;
                cost_item = (rs.getString(3)) ;
                tax_item = (rs.getString(4)) ;
                rs.close();
                pst.close();
                
                
            }
            else{
                JOptionPane.showMessageDialog(null,"No such Item Found");
            }
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null,e);
        }
        
        try{
            int sale = Integer.parseInt(jTextField2.getText());
            int available = Integer.parseInt(jTextField7.getText());
            
                if( sale > available )
                    throw new Exception();
            
            jTextField4.setText(mul( jTextField2.getText() , unit_disc ));
            jTextField9.setText(mul( jTextField2.getText() , jTextField6.getText() ));
            String s="Insert into SalesTemp (BillNo, ProductId, ProductName, SalesPrice, Quantity, Discount, Time ,Date ,TotalPrice ) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pst = conn.prepareStatement(s);
            pst.setString(1, jTextField1.getText());
            pst.setString(2, jComboBox1.getSelectedItem().toString());
            pst.setString(3, jTextField5.getText());
            pst.setString(4, jTextField6.getText());
            pst.setString(5, jTextField2.getText());
            pst.setString(6, jTextField4.getText());
            pst.setString(7, ""+java.time.LocalTime.now());
            pst.setString(8, ""+date);
            pst.setString(9,  jTextField9.getText() );
            
            pst.execute();
            updateQuan ();
            updateTable();
            pst.close();

        }
        
        catch(Exception e) {

            JOptionPane.showMessageDialog(null,"Please enter 'VALID' Quantity of items");
            e.printStackTrace();

        }
        
        String sql2 = "SELECT sum(TotalPrice)  FROM SalesTemp";
        try {
            pst = conn.prepareStatement(sql2);
            rs = pst.executeQuery();
            
            if (rs.next())
            {
                String sum = rs.getString("sum(TotalPrice)");
                jTextField3.setText(sum);
            }
            pst.close();
            rs.close();
            updateTable();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,ex);
            ex.printStackTrace();
        }
        
        String tSalePrice = mul( jTextField2.getText() , jTextField6.getText() );
        
        double tSPrice = Double.parseDouble(tSalePrice);
        double tCost = Double.parseDouble( mul(cost_item , jTextField2.getText()) );
        double tTax = Double.parseDouble( mul(tax_item , jTextField2.getText()) );
        double  tProf = tSPrice - tCost - tTax;
            
        try{
            
            
            
            String s="Insert into CashierSales (BillNo, ProductID, ProductName,SalesPrice, Quantity, Discount, Time ,Date, Cashier, TotalPrice , Price ,TotalProfit) values( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pst = conn.prepareStatement(s);
           
            pst.setString(1, jTextField1.getText());
            pst.setString(2, jComboBox1.getSelectedItem().toString());
            pst.setString(3, jTextField5.getText());
            pst.setString(4, jTextField6.getText());
            pst.setString(5, jTextField2.getText());
            pst.setString(6, jTextField4.getText());
            pst.setString(7, ""+java.time.LocalTime.now());
            pst.setString(8, ""+date);
            pst.setString(9, ""+CashierLogin.cashier);
            pst.setString(10, tSalePrice);
            pst.setString(11, cost_item );
            pst.setString(12, ""+tProf);
            
            
            pst.execute();
            updateTable();
            pst.close();

        }
        
        catch(Exception e) {

            JOptionPane.showMessageDialog(null,e);
            e.printStackTrace();

        }
        
        SearchProduct();
        
        
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        
        updateTable1();
        ps.jTextField1.setText(jTextField3.getText());
        ps.jLabel6.setText(java.time.LocalTime.now().toString());
        ps.jTextField2.setText(date);
        ps.jTextField3.setText(CashierLogin.cashier);
        ps.jTextField1.setText(jTextField3.getText());
        ps.setVisible(true);
        
        deleteTable();
        jTextField3.setText("" + 0);
        
      newBillNo();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        
        SearchProduct();
  
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jTextField9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField9ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CashierPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CashierPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CashierPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CashierPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new CashierPage().setVisible(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton jButton1;
    protected javax.swing.JButton jButton10;
    protected javax.swing.JButton jButton2;
    protected javax.swing.JButton jButton9;
    protected javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    protected javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    protected javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    protected javax.swing.JLabel jLabel3;
    protected javax.swing.JLabel jLabel4;
    protected javax.swing.JLabel jLabel5;
    protected javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    protected javax.swing.JLabel jLabel8;
    protected javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    protected javax.swing.JPanel jPanel2;
    protected javax.swing.JPanel jPanel3;
    protected javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    protected javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    protected javax.swing.JTextField jTextField2;
    protected javax.swing.JTextField jTextField3;
    protected javax.swing.JTextField jTextField4;
    protected javax.swing.JTextField jTextField5;
    protected javax.swing.JTextField jTextField6;
    protected javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    protected javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables
}
