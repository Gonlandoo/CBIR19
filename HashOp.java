package CBIR;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class HashOp {
	static String pathin;
	private static BufferedImage img;
	private final static int WIDTH=32;
	private final static int HEIGHT=32;
	
	public static void HashOp(String pathin) throws Exception{
		File sourceimage=new File(pathin);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(sourceimage.getPath()));
		img=ImageIO.read(in);
		//将图片缩放至32*32大小
        BufferedImage changeimg=new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		changeimg.getGraphics().drawImage(img,0,0, WIDTH, HEIGHT, null);
		//int[][] gray=new int[width][height];

		//xy坐标的最小值
		int minx=changeimg.getMinX();
		int miny=changeimg.getMinY();
		int[][] matrix=new int[WIDTH-minx][HEIGHT-miny];
		
		for(int i=minx;i<32;i++){
			for(int j=miny;j<32;j++){
				int pixel=changeimg.getRGB(i, j);
				double r=(pixel&0xff0000)>>16;//R����
				double g=(pixel&0xff00)>>8;//G����
				double b=(pixel&0xff);//B����
				matrix[i][j]=(int)(r*0.3+g*0.59+b*0.11);
				//System.out.println(gray[i][j]);
			}
		}
		int gray[][]=matrix;
		//计算DCT
		gray=DCT(gray,32);
		//缩小DCT，计算平均值ֵ
		int[][] newMatrix=new int[8][8];
		double average=0;
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				newMatrix[i][j]=gray[i][j];
				average+=gray[i][j];
			}
		}
		average/=64.0;
		//计算hash值ֵ
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
		JDBC jdbc=new JDBC();
		jdbc.Hash_feature(hash);
	}
	//离散余弦变换
	public static int[][] DCT(int[][] pix,int n) {
		double[][] iMatrix=new double[n][n];
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				iMatrix[i][j]=(double)(pix[i][j]);
			}
		}
		double[][] quotient=coefficient(n);//求系数矩阵
		double[][] quotientT=coefficientT(quotient, n);//转置系数矩阵

		double[][] temp=new double[n][n];
		//矩阵相乘
		temp=matrixMultiply(quotient, iMatrix, n);
		iMatrix=matrixMultiply(temp, quotientT, n);
		
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				pix[i][j]=(int)(iMatrix[i][j]);
			}
		}
		return pix;
	}
	private static double[][] matrixMultiply(double[][] A,double[][] B, int n) {
		double nMatrix[][]=new double[n][n];
		int t=0;
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				t=0;
				for(int k=0;k<n;k++){
					t+=A[i][k]*B[k][j];
				}
				nMatrix[i][j]=t;
			}
		}
		return nMatrix;
	}
	//求系数矩阵
	private static double[][] coefficient(int n) {
		double[][] coeff=new double[n][n];
		double sqrt=1.0/Math.sqrt(n);
		for(int i=0;i<n;i++){
			coeff[0][i]=sqrt;
		}
		for(int i=1;i<n;i++){
			for(int j=0;j<n;j++){
				coeff[i][j]=Math.sqrt(2.0/n)*Math.cos(i*Math.PI*(j+0.5)/(double) n);
			}
		}
		return coeff;
	}
	//转置系数矩阵
	private static double[][] coefficientT(double[][] matrix,int n) {
		double coeffT[][]=new double[n][n];
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				coeffT[i][j]=matrix[j][i];
			}
		}
		return coeffT;
	}

	public static void main(String[] arg) throws Exception{
		FileReader fr=new FileReader("D:\\K\\sc\\19\\Path.txt");
		@SuppressWarnings("resource")
		BufferedReader br=new BufferedReader(fr);
		while((pathin=br.readLine())!=null){
			//pathin=br.readLine();
			HashOp(pathin);
			//System.out.println(pathin);
		}
		System.exit(0);
	}
}
