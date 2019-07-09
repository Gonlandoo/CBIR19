package CBIR;

import java.sql.*;

public class JDBC {

//	private static String url="jdbc:mysql://localhost:3306/cbir";
	private static String url="jdbc:mysql://localhost:3306/cbir?useSSL=false&serverTimezone=UTC";
	private static String user="root";
	private static String password="19981118";
	public static Connection conn=null;
	public static Statement st=null;
	
	//连接数据库
	public static void connection() throws SQLException{
		try {
			//Class.forName(com.mysql.cj.jdbc.Driver);
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn=(Connection) DriverManager.getConnection(url, user, password);
			System.out.println("连接成功");
		} catch (ClassNotFoundException e) {
			System.out.println("连接失败");
			e.printStackTrace();
		}
	}
	//将图片地址写入数据库
	/*
	public static void prepare() throws Exception{
			String address;
			String sql;
			st=(Statement) conn.createStatement();
			FileReader fr=new FileReader("D:\K\sc\19Path.txt");
			@SuppressWarnings("resource")
			BufferedReader br=new BufferedReader(fr);
			while((address=br.readLine())!=null){
				address=address.replace("\\", "\\\\");
				sql="insert into image(address) values ('"+address+"')";
				st.executeUpdate(sql);
			}
			System.exit(0);
		}
	 */

	//用余弦定理求匹配图片与数据库中图片的相似度
	public double cos_similar(double[] daicha, double[] kuzhi) {
		double cosvalue=1,fenzi=0,fenmu1=0,fenmu2=0;
		for (int i=0;i<kuzhi.length;i++) {
			fenzi+=daicha[i]*kuzhi[i];
			fenmu1+=daicha[i]*daicha[i];
			fenmu2+=kuzhi[i]*kuzhi[i];
		}
		fenmu1=Math.sqrt(fenmu1);
		fenmu2=Math.sqrt(fenmu2);
		cosvalue=fenzi/(fenmu1*fenmu2);
		return cosvalue;
	}
	//汉明距离计算Hash匹配相似度,相似度为1时，图片最相似
	public double calculateSimilarity(String str1, String str2) {
		int num=0;
		for(int i=0;i<64;i++){
			if(str1.charAt(i)==str2.charAt(i)){
				num++;
			}
		}
		return (double)num/64.0;
	}
	//颜色特征写入数据库
	public void Color_feature(double a1, double a2, double a3, double b1, double b2, double b3,
			double c1, double c2, double c3) throws Exception{
		// TODO Auto-generated method stub
		connection();
		st=(Statement) conn.createStatement();
		String sql="insert into color(color1,color2,color3,color4,color5,color6,color7,color8,color9)"
				+ " values ('"+a1+"','"+a2+"','"+a3+"','"+b1+"','"+b2+"','"+b3+"','"+c1+"','"+c2+"','"+c3+"')";
		st.executeUpdate(sql);
		st.close();
		conn.close();
	}
	//纹理特征写入数据库
	public void Texture_feature(double[] exp, double[] stadv) throws Exception {
		// TODO Auto-generated method stub
		connection();
		st=(Statement) conn.createStatement();
		double a1=exp[0];double a2=exp[1];double a3=exp[2];double a4=exp[3];
		double b1=stadv[0];double b2=stadv[1];double b3=stadv[2];double b4=stadv[3];
		String sql="insert into texture(exp1,exp2,exp3,exp4,stadv1,stadv2,stadv3,stadv4)"
				+ " values ('"+a1+"','"+a2+"','"+a3+"','"+a4+"','"+b1+"','"+b2+"','"+b3+"','"+b4+"')";
		st.executeUpdate(sql);
		st.close();
		conn.close();
	}
	//hash特征写入数据库
	public void Hash_feature(String hash) throws Exception{
		// TODO Auto-generated method stub
		connection();
		st=(Statement) conn.createStatement();
		String sql="insert into hash(feature) values ('"+hash+"')";
		st.executeUpdate(sql);
		st.close();
		conn.close();
	}
	//形状特征写入数据库
	public void shape_feature(double[] shape) throws SQLException {
		connection();
		st=(Statement) conn.createStatement();
		double a0=shape[0];double a1=shape[1];double a2=shape[2];double a3=shape[3];
		double a4=shape[4];double a5=shape[5];double a6=shape[6];
		String sql="insert into shape(shape0,shape1,shape2,shape3,shape4,shape5,shape6)"
				+ " values ('"+a0+"','"+a1+"','"+a2+"','"+a3+"','"+a4+"','"+a5+"','"+a6+"')";
		st.executeUpdate(sql);
		st.close();
		conn.close();
	}
	//查询颜色特征数据库
	public ResultSet query_color(Statement s) throws SQLException{
		String query=new String("SELECT* FROM color;");
		ResultSet result=s.executeQuery(query);
		return result;
	}
	//查询纹理特征数据库
	public ResultSet query_texture(Statement s)throws SQLException{
		String query=new String("SELECT* FROM texture;");
		ResultSet result=s.executeQuery(query);
		return result;
	}
	//查询hash特征数据库
	public ResultSet query_hash(Statement s)throws SQLException{
		String query=new String("SELECT* FROM hash;");
		ResultSet result=s.executeQuery(query);
		return result;
	}
	//查询形状特征数据库
	public ResultSet query_shape(Statement s) throws SQLException {
		String query=new String("SELECT* FROM shape;");
		ResultSet result=s.executeQuery(query);
		return result;
	}

	public static void main(String[] args) throws Exception{
		connection();
		//prepare();
	}
	
}
