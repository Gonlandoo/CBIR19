package CBIR;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class TextureOp {
	static String pathin;
	private static BufferedImage img;
	static double[] exp=new double[4];//期望
	static double[] stadv=new double[4];//标准差
	static int ori;//原始像素点的灰度值
	static int off;//偏移像素点的灰度值
	static double[][][] glcm=new double[4][8][8];
	static int type;//第几个共生矩阵
	
	public static void TextureOp(String pathin) throws Exception{
		//灰度共生矩阵中像素点个数
		int sum0=0;
		int sum45=0;
		int sum90=0;
		int sum135=0;
		//每次计算都创建，避免累加
		double[] homogeneity=new double[4];//纹理一致性
		double[] contrast=new double[4];//纹理对比度
		double[] entropy=new double[4];//纹理熵
		double[] correlation=new double[4];//纹理相关性
		double[] ux=new double[4];//相关性的μ
		double[] uy=new double[4];
		double[] delterx=new double[4];//相关性的σ
		double[] deltery=new double[4];
		//ͨ读取库中图片
		File sourceimage=new File(pathin);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(sourceimage.getPath()));
		img=ImageIO.read(in);
		int width=img.getWidth();
		int height=img.getHeight();
		int[][] gray=new int[width][height];
		
		//图像灰度化，获取像素点灰度值
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
		
		//降低图像灰度等级
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				gray[i][j]/=32;
				//System.out.println(gray[i][j]);
			}
		}
		//按四个方向遍历图片，记录灰度值对出现的次数
        for(int i=0;i<width;i++){
		    for(int j=0;j<height;j++){
				//0度
				if(i+2>=0&&i+2<width){
					ori=gray[i][j];
					off=gray[i+2][j];
					glcm[0][ori][off]+=1;
					sum0++;
				}
				//45度
				if(i+2>=0&&i+2<width&&j+2>=0&&j+2<height){
					ori=gray[i][j];
					off=gray[i+2][j+2];
					glcm[1][ori][off]+=1;
					sum45++;
				}
				//90度
				if(j+2>=0&&j+2<height){
					ori=gray[i][j];
					off=gray[i][j+2];
					glcm[2][ori][off]+=1;
					sum90++;
				}
				//135度
				if(i-2>=0&&i-2<width&&j+2>=0&&j+2<height){
					ori=gray[i][j];
					off=gray[i-2][j+2];
					glcm[3][ori][off]+=1;
					sum135++;
				}
			}
		}
		//求出灰度共生矩阵，每个单元为像素对出现的概率
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				glcm[0][i][j]=glcm[0][i][j]/sum0;
				glcm[1][i][j]=glcm[1][i][j]/sum45;
				glcm[2][i][j]=glcm[2][i][j]/sum90;
				glcm[3][i][j]=glcm[3][i][j]/sum135;
			}
		}
		//计算纹理特征
		for(type=0;type<4;type++){
			for(int i=0;i<8;i++){
				for(int j=0;j<8;j++){
					//纹理一致性
					contrast[type]+=Math.pow(glcm[type][i][j], 2);
					//System.out.print(cons[type]+"  ");
					//纹理对比度
					contrast[type]+=Math.pow(i-j,2)*glcm[type][i][j];
					//纹理熵
					if(glcm[type][i][j]!=0){
						entropy[type]-=glcm[type][i][j]*Math.log(glcm[type][i][j]);
					}	
				}
			}
			//System.out.print(cons[type]+"  ");
			//System.out.print(cont[type]+"  ");
			//System.out.print(ent[type]+"  ");
			//System.out.println();
		}
		//纹理相关性特征值计算
        //μ
		for(type=0;type<4;type++){
			for(int i=0;i<8;i++){
				for(int j=0;j<8;j++){
					ux[type]+=i*glcm[type][i][j];
					uy[type]+=j*glcm[type][i][j];
				}
			}
		}
		//σ
		for(type=0;type<4;type++){
		    for(int i=0;i<8;i++){
		        for(int j=0;j<8;j++){
		            delterx[type]+=Math.pow(i-ux[type], 2)*glcm[type][i][j];
		            deltery[type]+=Math.pow(j-uy[type], 2)*glcm[type][i][j];
		            correlation[type]+=i*j*glcm[type][i][j];
		        }
		    }
				//System.out.println(delterx[t]+"\t"+deltery[t]+"\t"+ux[t]+"\t"+uy[t]);
				//纹理相关性
				correlation[type]=(correlation[type]-ux[type]*uy[type])/delterx[type]/deltery[type];
		}
		//期望与标准差
		for(type=0;type<4;type++){
			//期望
			exp[type]=(correlation[type]+contrast[type]+homogeneity[type]+entropy[type])/4;
			//System.out.print(exp[type]+"  ");
			//标准差
			stadv[type]=Math.sqrt(Math.pow(correlation[type]-exp[type], 2)+Math.pow(contrast[type]-exp[type], 2)+Math.pow(homogeneity[type]-exp[type], 2)+Math.pow(entropy[type]-exp[type], 2));
			//System.out.println(stadv[type]);
		}
		in.close();
		JDBC jdbc=new JDBC();
		jdbc.Texture_feature(exp,stadv);
	}

	public static void main(String[] arg) throws Exception{
		FileReader fr=new FileReader("D:\\K\\sc\\19\\Path.txt");
		@SuppressWarnings("resource")
		BufferedReader br=new BufferedReader(fr);
		while((pathin=br.readLine())!=null){
			//pathin=br.readLine();
			TextureOp(pathin);
			//System.out.println(pathin);
		}
		System.exit(0);
	}
}
