package CBIR;



import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.Map.Entry;

public class MainOp {

	private JFrame frame;
	private JLabel label;
	private JTextField text;
	private JButton brow;
	private JButton b1;
	private JButton b2;
	private JButton b3;
	private JButton b4;
	private String filepath;//保存获取的文件路径
	
	//待匹配图片颜色参数
	private static BufferedImage img;
	private static double r;
	private static double g;
	private static double b;
	private static double H;
	private static double S;
	private static double I;
	//待匹配图片纹理参数
	static double[] stadv=new double[4];//标准差
	static double[] exp=new double[4];//期望
	static int ori;//原始像素点的灰度值
	static int off;//偏移像素点的灰度值
	//灰度共生矩阵是图像中相距为D的两个灰度像素同时出现的联合概率分布
	static double[][][] glcm=new double[4][8][8];//灰度共生矩阵
	static int type;//第几个共生矩阵
	private JLabel label_2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainOp window = new MainOp();
					window.frame.setVisible(true);
					window.frame.setResizable(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainOp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("图像检索系统");
		frame.setBounds(100, 100, 600, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		label = new JLabel("选择文件");
		label.setFont(new Font("宋体", Font.PLAIN, 12));
		label.setBounds(37, 49, 78, 18);
		frame.getContentPane().add(label);
		
		text = new JTextField();
		text.setBounds(110, 47, 300, 22);
		frame.getContentPane().add(text);
		text.setColumns(10);
		
		brow = new JButton("浏览");
		brow.setFont(new Font("宋体", Font.PLAIN, 15));
		brow.setBounds(444, 46, 87, 27);
		frame.getContentPane().add(brow);
		//弹窗按钮监听
		brow.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				TanChuang();
			}
		});
		
		b1 = new JButton("基于颜色检索");
		b1.setFont(new Font("宋体", Font.PLAIN, 15));
		b1.setBounds(37, 120, 130, 30);
		frame.getContentPane().add(b1);
		//颜色检索按钮监听
		b1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					Color();//基于颜色检索
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		b2 = new JButton("基于纹理检索");
		b2.setFont(new Font("宋体", Font.PLAIN, 15));
		b2.setBounds(37, 165, 130, 30);
		frame.getContentPane().add(b2);
		//纹理检索按钮监听
		b2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					Texture();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}//基于纹理检索
			}
		});
		
		b3 = new JButton("基于指纹检索");
		b3.setFont(new Font("宋体", Font.PLAIN, 15));
		b3.setBounds(37, 210, 130, 30);
		frame.getContentPane().add(b3);
		//指纹检索按钮监听
		b3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					Hash();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}//基于pHash值检索
			}
		});

		b4 = new JButton("基于形状检索");
		b4.setFont(new Font("宋体", Font.PLAIN, 15));
		b4.setBounds(37, 255, 130, 30);
		frame.getContentPane().add(b4);
		//纹理检索按钮监听
		b4.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					Shape();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}//基于纹理检索
			}
		});
		
		JLabel label_1 = new JLabel("当前图像：");
		label_1.setFont(new Font("宋体", Font.PLAIN, 15));
		label_1.setBounds(200, 180, 81, 26);
		frame.getContentPane().add(label_1);
		
		label_2 = new JLabel("");
		label_2.setBounds(310, 139, 120, 150);
		frame.getContentPane().add(label_2);
	}	
	
	//弹窗选择文件
	public String TanChuang(){
		JFileChooser chooser=new JFileChooser();
		chooser.setDialogTitle("请选择图片文件");
		//设置为只能选择图片文件
		FileNameExtensionFilter filter = new FileNameExtensionFilter("jpg", "jpg");
		chooser.setFileFilter(filter);
		//弹出选择框
		int returnVal=chooser.showOpenDialog(null);
		//如果选择了文件
		if(JFileChooser.APPROVE_OPTION==returnVal){
			filepath=chooser.getSelectedFile().toString();//获取所选文件路径
			text.setText(filepath);//把路径值写到textField中
			//在当前面板上显示所选图像
			ImageIcon sourceimg=new ImageIcon(filepath);
			int width=sourceimg.getIconWidth();
			int height=sourceimg.getIconHeight();
			label_2.setIcon(sourceimg);
			return filepath;
		}
		else{
			System.out.println("还未选择文件");
			return null;
		}
	}
	
	//基于颜色检索
	protected void Color() throws Exception{
		// TODO Auto-generated method stub
		String pathin=text.getText();
		File sourceimage=new File(pathin);
		BufferedInputStream in=new BufferedInputStream(new FileInputStream(sourceimage.getPath()));
		img=ImageIO.read(in);
		int width=img.getWidth();
		int height=img.getHeight();
		int N=width*height;
		double sumh1=0,sums1=0,sumi1=0;
		double sumh2=0,sums2=0,sumi2=0;
		double sumh3=0,sums3=0,sumi3=0;

		//计算当前选择图片的颜色特征
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				//System.out.println(img.getRGB(i, j)&0xffffff);
				int pixel=img.getRGB(i, j);
				r=(pixel&0xff0000)>>16;//R
				g=(pixel&0xff00)>>8;//G
				b=(pixel&0xff);//B
				double hz;
				if((r==g&&g==b)||(r==0&&g==0&&b==0)){
					hz=Math.acos(0);
					H=hz;
					S=1.0;
				}
				else{
					hz=Math.acos(((r-g)+(r-b))/((0.001+2*Math.sqrt((r-g)*(r-g)+Math.abs(r-b)*Math.abs(g-b)))));
					//System.out.println(hz);
					if(g>b||g==b)
						H=hz;
					else
						H=2*Math.PI-hz;
						double d=Math.min(r, g);
						double min=Math.min(d,b); 
						S=1.0-3*min/(r+g+b);
					}	
				I=(r+g+b)/3;
				sumh1+=H;//H
				sums1+=S;//S
				sumi1+=I;//I
			}
		}
		double A1=sumh1/N;
		double B1=sums1/N;
		double C1=sumi1/N;
					
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				int pixel=img.getRGB(i, j);
				r=(pixel&0xff0000)>>16;//R
				g=(pixel&0xff00)>>8;//G
				b=(pixel&0xff);//B
				double hz;
				if((r==g&&g==b)||(r==0&&g==0&&b==0)){
					hz=Math.acos(0);
					H=hz;
					S=1.0;
				}
				else{
					hz=Math.acos(((r-g)+(r-b))/((0.001+2*Math.sqrt((r-g)*(r-g)+Math.abs(r-b)*Math.abs(g-b)))));
					if(g>b||g==b)
						H=hz;
					else
						H=2*Math.PI-hz;
					double d=Math.min(r, g);
					double min=Math.min(d,b); 
					S=1.0-3*min/(r+g+b);
				}	
				I=(r+g+b)/3;
				sumh2+=(H-A1)*(H-A1);
				sums2+=(S-B1)*(S-B1);
				sumi2+=(I-C1)*(I-C1);
				sumh3+=Math.abs((H-A1)*(H-A1)*(H-A1));
				sums3+=Math.abs((S-B1)*(S-B1)*(S-B1));
				sumi3+=Math.abs((I-C1)*(I-C1)*(I-C1));
			}
		}
		double A2=Math.sqrt(sumh2/N);
		double A3=Math.pow(sumh3/N, 1.0/3);
		double B2=Math.sqrt(sums2/N);
		double B3=Math.pow(sums3/N, 1.0/3);
		double C2=Math.sqrt(sumi2/N);
		double C3=Math.pow(sumi3/N, 1.0/3);
		//System.out.println(A1+","+A2+","+A3);
		//System.out.println(B1+","+B2+","+B3);
		//System.out.println(C1+","+C2+","+C3);
		in.close();

		JDBC jdbc=new JDBC();
		jdbc.connection();
		Statement st=(Statement) jdbc.conn.createStatement();
		ResultSet rs=jdbc.query_color(st);
		double daicha[]={A1,A2,A3,B1,B2,B3,C1,C2,C3};//待查特征值
		double kuzhi[]=new double[9];//数据库值
		int num=1;//匹配第几张图片
		TreeMap<Integer,Double> treemap=new TreeMap<Integer,Double>();//Map实现按值排序
		while(rs.next()){//遍历每行
			for(int i=2;i<=10;i++){
				kuzhi[i-2]=rs.getDouble(i);//保存数据库中该行第i列的值，得到该行9个特征值
				//System.out.println(kuzhi[i-2]);
			}
			treemap.put(num,jdbc.cos_similar(daicha,kuzhi));//向treemap中写值
			num++;
		}
		//System.out.println(treemap);
		ArrayList<Entry<Integer, Double>> entryArrayList=new ArrayList<>(treemap.entrySet());
        Collections.sort(entryArrayList, new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());//按value从大到小排序
            }
        });
        int[] id=new int[12];
        String[] address=new String[12];
        //弹出颜色检索结果窗口
        JFrame image=new JFrame("基于颜色检索结果");
        image.setResizable(false);
        image.getContentPane().setLayout(null);//默认为FlowLayout,此处相当于在面板中设置为Absolute layout
        image.setBounds(100, 100, 570, 580);
        image.setVisible(true);
        for(int i=0;i<12;i++){
        	Entry<Integer,Double>entry=entryArrayList.get(i);//获取排序后列表第i行数据
			id[i]=entry.getKey();//得到最匹配前12张图片id值
        	//System.out.println(id[i]);
        	String query="SELECT address FROM image WHERE id="+id[i];
        	//System.out.println(query);
        	ResultSet coloraddress=st.executeQuery(query);
        	while(coloraddress.next()){
        		address[i]=coloraddress.getString(1);
        		System.out.println(address[i]);
        	}
        }
        int i=0;
        for(int y=20;y<600;y+=165){
        	for(int x=25;x<540;x+=135){
        		if(i<12){
        			ImageIcon sourceimg=new ImageIcon(address[i++]);
	        		//System.out.println(address[i-1]);
	        		JLabel img=new JLabel(sourceimg);
	        		img.setBounds(x, y, width, height);
	        		//System.out.println(x+","+y);
	        		img.setIcon(sourceimg);
	        		image.getContentPane().add(img);
        		}
        	}
        }
	}

	//基于纹理检索
	protected void Texture() throws Exception{
		// TODO Auto-generated method stub
		int sum0=0;
		int sum45=0;
		int sum90=0;
		int sum135=0;
		double[] cons=new double[4];
		double[] cont=new double[4];
		double[] ent=new double[4];
		double[] corr=new double[4];
		double[] ux=new double[4];
		double[] uy=new double[4];
		double[] delterx=new double[4];
		double[] deltery=new double[4];
		String pathin=text.getText();
		File sourceimage=new File(pathin);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(sourceimage.getPath()));
		img=ImageIO.read(in);
		int width=img.getWidth();
		int height=img.getHeight();
		int[][] gray=new int[width][height];

		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				int pixel=img.getRGB(i, j);
				double r=(pixel&0xff0000)>>16;//R
				double g=(pixel&0xff00)>>8;//G
				double b=(pixel&0xff);//B
				gray[i][j]=(int)(r*0.3+g*0.59+b*0.11);
			}
		}
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				gray[i][j]/=32;
			}
		}

		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				//0
				if(i+2>=0&&i+2<width){
					ori=gray[i][j];
					off=gray[i+2][j];
					glcm[0][ori][off]+=1;
					sum0++;
				}
				//45
				if(i+2>=0&&i+2<width&&j+2>=0&&j+2<height){
					ori=gray[i][j];
					off=gray[i+2][j+2];
					glcm[1][ori][off]+=1;
					sum45++;
				}
				//90
				if(j+2>=0&&j+2<height){
					ori=gray[i][j];
					off=gray[i][j+2];
					glcm[2][ori][off]+=1;
					sum90++;
				}
				//135
				if(i-2>=0&&i-2<width&&j+2>=0&&j+2<height){
					ori=gray[i][j];
					off=gray[i-2][j+2];
					glcm[3][ori][off]+=1;
					sum135++;
				}
			}
		}

		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				glcm[0][i][j]=glcm[0][i][j]/sum0;//0
				glcm[1][i][j]=glcm[1][i][j]/sum45;//45
				glcm[2][i][j]=glcm[2][i][j]/sum90;//90
				glcm[3][i][j]=glcm[3][i][j]/sum135;//135
			}
		}

		for(type=0;type<4;type++){
			for(int i=0;i<8;i++){
				for(int j=0;j<8;j++){
					cons[type]+=Math.pow(glcm[type][i][j], 2);
					//System.out.print(cons[type]+"  ");
					cont[type]+=Math.pow(i-j,2)*glcm[type][i][j];
					if(glcm[type][i][j]!=0){
						ent[type]-=glcm[type][i][j]*Math.log(glcm[type][i][j]);
					}
				}
			}
			//System.out.print(cons[type]+"  ");
			//System.out.print(cont[type]+"  ");
			//System.out.print(ent[type]+"  ");
			//System.out.println();
		}

		for(type=0;type<4;type++){
			for(int i=0;i<8;i++){
				for(int j=0;j<8;j++){
					ux[type]+=i*glcm[type][i][j];
					uy[type]+=j*glcm[type][i][j];
				}
			}
			}
			for(type=0;type<4;type++){
				for(int i=0;i<8;i++){
					for(int j=0;j<8;j++){
						delterx[type]+=Math.pow(i-ux[type], 2)*glcm[type][i][j];
						deltery[type]+=Math.pow(j-uy[type], 2)*glcm[type][i][j];
						corr[type]+=i*j*glcm[type][i][j];
					}
				}
				//System.out.println(delterx[t]+"\t"+deltery[t]+"\t"+ux[t]+"\t"+uy[t]);
				corr[type]=(corr[type]-ux[type]*uy[type])/delterx[type]/deltery[type];
			}

		for(type=0;type<4;type++){
			exp[type]=(corr[type]+cont[type]+cons[type]+ent[type])/4;
			//System.out.print(exp[type]+"  ");
			stadv[type]=Math.sqrt(Math.pow(corr[type]-exp[type], 2)+Math.pow(cont[type]-exp[type], 2)+Math.pow(cons[type]-exp[type], 2)+Math.pow(ent[type]-exp[type], 2));
			//System.out.println(stadv[type]);
		}
		in.close();
		JDBC jdbc=new JDBC();
		jdbc.connection();
		Statement st=(Statement) jdbc.conn.createStatement();
		ResultSet rs=jdbc.query_texture(st);
		double daicha[]={exp[0],exp[1],exp[2],exp[3],stadv[0],stadv[1],stadv[2],stadv[3]};
		double kuzhi[]=new double[8];
		int num=1;
		TreeMap<Integer,Double> treemap=new TreeMap<Integer,Double>();
		while(rs.next()){//遍历每行
			for(int i=2;i<=9;i++){
				kuzhi[i-2]=rs.getDouble(i);//保存数据库中该行第i列的值，得到该行8个特征值
				//System.out.println(kuzhi[i-2]);
			}
			treemap.put(num,jdbc.cos_similar(daicha,kuzhi));
			num++;
		}
		//System.out.println(treemap);
		ArrayList<Entry<Integer, Double>> entryArrayList=new ArrayList<>(treemap.entrySet());
        Collections.sort(entryArrayList, new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        int[] id=new int[12];
        String[] address=new String[12];
        JFrame image=new JFrame("基于纹理检索结果");
        image.setResizable(false);
        image.getContentPane().setLayout(null);
        image.setBounds(100, 100, 580, 600);
        image.setVisible(true);
        for(int i=0;i<12;i++){
        	Entry<Integer,Double>entry=entryArrayList.get(i);
        	id[i]=entry.getKey();//获得特征值对应的id
        	//System.out.println(id[i]);
        	String query="SELECT address FROM image WHERE id="+id[i];
        	//System.out.println(query);
        	ResultSet textureaddress=st.executeQuery(query);
        	while(textureaddress.next()){
        		address[i]=textureaddress.getString(1);
        		System.out.println(address[i]);
        	}
        }
        int i=0;
        for(int y=20;y<600;y+=165){
        	for(int x=25;x<540;x+=135){
        		if(i<12){
        			ImageIcon sourceimg=new ImageIcon(address[i++]);
	        		JLabel img=new JLabel(sourceimg);
	        		img.setBounds(x, y, width, height);
	        		img.setIcon(sourceimg);
	        		image.getContentPane().add(img);
        		}
        	}
        }
	}

	//基于指纹检索
	protected void Hash() throws Exception{
		// TODO Auto-generated method stub
		int WIDTH=32,HEIGHT=32;
		String pathin=text.getText();
		File sourceimage=new File(pathin);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(sourceimage.getPath()));
		img=ImageIO.read(in);
		int width=img.getWidth();
		int height=img.getHeight();

        BufferedImage changeimg=new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		changeimg.getGraphics().drawImage(img,0,0, WIDTH, HEIGHT, null);
		//int[][] gray=new int[width][height];
		int minx=changeimg.getMinX();
		int miny=changeimg.getMinY();
		int[][] matrix=new int[WIDTH-minx][HEIGHT-miny];

		for(int i=minx;i<32;i++){
			for(int j=miny;j<32;j++){
				int pixel=changeimg.getRGB(i, j);
				double r=(pixel&0xff0000)>>16;//R
				double g=(pixel&0xff00)>>8;//G
				double b=(pixel&0xff);//B
				matrix[i][j]=(int)(r*0.3+g*0.59+b*0.11);
				//System.out.println(gray[i][j]);
			}
		}
		int gray[][]=matrix;

		gray=HashOp.DCT(gray,32);

		int[][] newMatrix=new int[8][8];
		double average=0;
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				newMatrix[i][j]=gray[i][j];
				average+=gray[i][j];
			}
		}
		average/=64.0;

		String hash="";
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				if(newMatrix[i][j]<average){
					hash+='0';
				}
				else{
					hash+='1';
				}
			}
		}
		//System.out.println(hash);
		in.close();

		JDBC jdbc=new JDBC();
		jdbc.connection();
		Statement st=(Statement) jdbc.conn.createStatement();
		ResultSet rs=jdbc.query_hash(st);
		String daicha=hash;
		String kuzhi=new String();
		int num=1;
		TreeMap<Integer,Double> treemap=new TreeMap<Integer,Double>();
		while(rs.next()){
			kuzhi=rs.getString(2);
			//System.out.println(kuzhi);
			treemap.put(num,jdbc.calculateSimilarity(daicha,kuzhi));
			num++;
		}
		//System.out.println(treemap);
		ArrayList<Entry<Integer, Double>> entryArrayList=new ArrayList<>(treemap.entrySet());
        Collections.sort(entryArrayList, new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        int[] id=new int[12];
        String[] address=new String[12];
        JFrame image=new JFrame("基于指纹检索结果");
        image.setResizable(false);
        image.getContentPane().setLayout(null);
        image.setBounds(100, 100, 580, 600);
        image.setVisible(true);
        for(int i=0;i<12;i++){
        	Entry<Integer,Double>entry=entryArrayList.get(i);
        	//System.out.println(entry.getValue());
        	id[i]=entry.getKey();//得到最匹配前12张图片id值
        	//System.out.println(id[i]);
        	String query="SELECT address FROM image WHERE id="+id[i];
        	//System.out.println(query);
        	ResultSet hashaddress=st.executeQuery(query);
        	while(hashaddress.next()){
        		address[i]=hashaddress.getString(1);
        		System.out.println(address[i]);
        	}
        }
        int i=0;
        for(int y=20;y<600;y+=165){
        	for(int x=25;x<540;x+=135){
        		if(i<12){
        			ImageIcon sourceimg=new ImageIcon(address[i++]);
	        		JLabel img=new JLabel(sourceimg);
	        		img.setBounds(x, y, width, height);
	        		img.setIcon(sourceimg);
	        		image.getContentPane().add(img);
        		}
        	}
        }
	}

	//基于形状检索
	protected void Shape() throws Exception{
		// TODO Auto-generated method stub
		String pathin=text.getText();
		File sourceimage=new File(pathin);
		BufferedInputStream in=new BufferedInputStream(new FileInputStream(sourceimage.getPath()));
		img=ImageIO.read(in);
		int width=img.getWidth();
		int height=img.getHeight();
		int[][] gray=new int[width][height];

		//计算当前选择图片的形状特征
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				int pixel=img.getRGB(i, j);
				double r=(pixel&0xff0000)>>16;
				double g=(pixel&0xff00)>>8;
				double b=(pixel&0xff);
				gray[i][j]=(int)(r*0.3+g*0.59+b*0.11);
				//System.out.println(gray[i][j]);
			}
		}

		//用sobel算子对图像进行锐化
		int ret[][] = new int[width][height];
		for (int i = 1; i < width - 1; i++) {
			for (int j = 1; j < height - 1; j++) {
				int gx = gray[i + 1][j - 1] + 2 * gray[i + 1][j] + gray[i + 1][j + 1] - gray[i - 1][j - 1]
						- 2 * gray[i - 1][j] - gray[i - 1][j + 1];
				int gy = gray[i - 1][j + 1] + 2 * gray[i][j + 1] + gray[i + 1][j + 1] - gray[i - 1][j - 1]
						- 2 * gray[i][j - 1] - gray[i + 1][j - 1];
				ret[i][j] = (int) Math.min(255, (Math.sqrt(gx*gx + gy*gy)));
			}
		}
		gray=ret;

		//灰度图像二值化，灰度图像空白为0，其余为1
		int sw=120;//设定阈值
		for (int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				int rs=gray[x][y]
						+ (x == 0 ? 255 : gray[x - 1][y])
						+ (x == 0 || y == 0 ? 255 : gray[x - 1][y - 1])
						+ (x == 0 || y == height - 1 ? 255 : gray[x - 1][y + 1])
						+ (y == 0 ? 255 : gray[x][y - 1])
						+ (y == height - 1 ? 255 : gray[x][y + 1])
						+ (x == width - 1 ? 255 : gray[x + 1][ y])
						+ (x == width - 1 || y == 0 ? 255 : gray[x + 1][y - 1])
						+ (x == width - 1 || y == height - 1 ? 255 : gray[x + 1][y + 1]);
				gray[x][y] = rs / 9;
				if(gray[x][y]>sw)
					gray[x][y]=1;
				else
					gray[x][y]=0;

			}
		}

		double m00=0,m11=0,m20=0,m02=0,m30=0,m03=0,m12=0,m21=0;  //中心矩
		double x0=0,y0=0;    //计算中心距时所使用的临时变量（x-x'）
		double u20=0,u02=0,u11=0,u30=0,u03=0,u12=0,u21=0;//规范化后的中心矩
		double M[]=new double[7];    //HU不变矩
		double t1=0,t2=0,t3=0,t4=0,t5=0;//临时变量，
		//double Center_x=0,Center_y=0;//重心
		int Center_x=0,Center_y=0;//重心
		int i,j;            //循环变量

		//获得图像的区域重心(普通矩)
		double s10=0,s01=0,s00=0;  //0阶矩和1阶矩,s10:图像上白色区域x坐标值的累加
		for(j=0;j<height;j++)
		{
			for(i=0;i<width;i++)
			{
				s10+=i*gray[i][j];
				s01+=j*gray[i][j];
				s00+=gray[i][j];
			}
		}
		Center_x=(int)(s10/s00+0.5);
		Center_y=(int)(s01/s00+0.5);

		//计算二阶、三阶矩（中心矩）
		m00=s00;
		for(j=0;j<height;j++){
			for(i=0;i<width;i++){
				x0=i-Center_x;
				y0=j-Center_y;
				m11+=x0*y0*gray[i][j];
				m20+=Math.pow(x0,2)*gray[i][j];
				m02+=Math.pow(y0,2)*gray[i][j];
				m03+=Math.pow(y0,3)*gray[i][j];
				m30+=Math.pow(x0,3)*gray[i][j];
				m12+=x0*Math.pow(y0,2)*gray[i][j];
				m21+=y0*Math.pow(x0,2)*gray[i][j];
			}
		}

		//计算规范化后的中心矩:mjj/pow(m00,(i+j+2)/2)
		u20=m20/Math.pow(m00,2);
		u02=m02/Math.pow(m00,2);
		u11=m11/Math.pow(m00,2);
		u30=m30/Math.pow(m00,2.5);
		u03=m03/Math.pow(m00,2.5);
		u12=m12/Math.pow(m00,2.5);
		u21=m21/Math.pow(m00,2.5);

		//计算中间变量
		t1=u20-u02;
		t2=u30-3*u12;
		t3=3*u21-u03;
		t4=u30+u12;
		t5=u21+u03;

		//计算不变矩
		double HU1=M[0]=u20+u02;
		double HU2=M[1]=t1*t1+4*u11*u11;
		double HU3=M[2]=t2*t2+t3*t3;
		double HU4=M[3]=t4*t4+t5*t5;
		double HU5=M[4]=t2*t4*(t4*t4-3*t5*t5)+t3*t5*(3*t4*t4-t5*t5);
		double HU6=M[5]=t1*(t4*t4-t5*t5)+4*u11*t4*t5;
		double HU7=M[6]=t3*t4*(t4*t4-3*t5*t5)-t2*t5*(3*t4*t4-t5*t5);

		in.close();

		JDBC jdbc=new JDBC();
		jdbc.connection();
		Statement st=(Statement) jdbc.conn.createStatement();
		ResultSet rs=jdbc.query_shape(st);
		double daicha[]={HU1,HU2,HU3,HU4,HU5,HU6,HU7};//待查特征值
		double kuzhi[]=new double[7];//数据库值
		int num=1;//匹配第几张图片
		TreeMap<Integer,Double> treemap=new TreeMap<Integer,Double>();//Map实现按值排序
		while(rs.next()){//遍历每行
			for(i=2;i<=8;i++){
				kuzhi[i-2]=rs.getDouble(i);//保存数据库中该行第i列的值，得到该行9个特征值
				//System.out.println(kuzhi[i-2]);
			}
			treemap.put(num,jdbc.cos_similar(daicha,kuzhi));//向treemap中写值
			num++;
		}
		//System.out.println(treemap);
		ArrayList<Entry<Integer, Double>> entryArrayList=new ArrayList<>(treemap.entrySet());
		Collections.sort(entryArrayList, new Comparator<Map.Entry<Integer, Double>>() {
			public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());//按value从大到小排序
			}
		});
		int[] id=new int[12];
		String[] address=new String[12];
		//弹出形状检索结果窗口
		JFrame image=new JFrame("基于形状检索结果");
		image.setResizable(false);
		image.getContentPane().setLayout(null);//默认为FlowLayout,此处相当于在面板中设置为Absolute layout
		image.setBounds(100, 100, 570, 580);
		image.setVisible(true);
		for(i=0;i<12;i++){
			Entry<Integer,Double>entry=entryArrayList.get(i);//获取排序后列表第i行数据
			id[i]=entry.getKey();//得到最匹配前12张图片id值
			//System.out.println(id[i]);
			String query="SELECT address FROM image WHERE id="+id[i];
			//System.out.println(query);
			ResultSet shapeaddress=st.executeQuery(query);
			while(shapeaddress.next()){
				address[i]=shapeaddress.getString(1);
				System.out.println(address[i]);
			}
		}
		int k=0;
		for(int y=20;y<600;y+=165){
			for(int x=25;x<540;x+=135){
				if(k<12){
					ImageIcon sourceimg=new ImageIcon(address[k++]);
					//System.out.println(address[i-1]);
					JLabel img=new JLabel(sourceimg);
					img.setBounds(x, y, width, height);
					//System.out.println(x+","+y);
					img.setIcon(sourceimg);
					image.getContentPane().add(img);
				}
			}
		}
	}
}

