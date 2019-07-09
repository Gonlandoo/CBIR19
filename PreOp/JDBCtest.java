package CBIR.PreOp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;

public class JDBCtest {
//    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
//    static final String DB_URL = "jdbc:mysql://localhost:3306/cbir";
    //8.0以上的mysql需要使用以下语句
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/cbir?useSSL=false&serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "19981118";
    public static Statement st=null;

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try{
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            //
            String address;
            String sql;
            st=(Statement) conn.createStatement();
            FileReader fr=new FileReader("D:\\K\\sc\\19\\Path.txt");
            @SuppressWarnings("resource")
            BufferedReader br=new BufferedReader(fr);
            while((address=br.readLine())!=null){
                address=address.replace("\\", "\\\\");
                sql="insert into image(address) values ('"+address+"')";
                st.executeUpdate(sql);
            }
//            System.exit(0);
//            stmt = conn.createStatement();
//            String sql;
//            sql = "SELECT * from image";
//            ResultSet rs = stmt.executeQuery(sql);

            // 展开结果集数据库
//            while(rs.next()){
//                // 通过字段检索
//                int id  = rs.getInt("id");
//                String address = rs.getString("address");
//
//                // 输出数据
//                System.out.print("ID: " + id);
//                System.out.println(", address: " + address);
//            }
            // 完成后关闭
            // rs.close();
            st.close();
            conn.close();
        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }finally{
            // 关闭资源
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }// 什么都不做
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }
}
