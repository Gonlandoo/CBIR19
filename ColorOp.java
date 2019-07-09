package CBIR;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ColorOp {
	static String pathin;
	private static BufferedImage img;
	private static double r;
	private static double g;
	private static double b;
	private static double H;
	private static double S;
	private static double I;

	public static void ColorOp(String pathin) throws Exception{
		//ͨ读取一个图片路径
		File sourceimage=new File(pathin);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(sourceimage.getPath()));
		img=ImageIO.read(in);
		int width=img.getWidth();
		int height=img.getHeight();
		int N=width*height;//图片大小
		double sumh1=0,sums1=0,sumi1=0;
		double sumh2=0,sums2=0,sumi2=0;
		double sumh3=0,sums3=0,sumi3=0;
		
		//计算HSI模型的三个分量
		for(int i=0;i<width;i++){
		    for(int j=0;j<height;j++){
		        //System.out.println(img.getRGB(i, j)&0xffffff);
                int pixel=img.getRGB(i, j);//根据像素的xy坐标获得整数像素
                //归一到[0,1]
				r=(pixel&0xff0000)>>16;//R
				g=(pixel&0xff00)>>8;//G
				b=(pixel&0xff);//B
				//System.out.println(r+g+b);

				double hz;//θ

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
				//I
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
		System.out.println(A1+","+A2+","+A3);
		System.out.println(B1+","+B2+","+B3);
		System.out.println(C1+","+C2+","+C3);
		in.close();
		//将颜色特征写入数据库
		JDBC jdbc=new JDBC();
		jdbc.Color_feature(A1,A2,A3,B1,B2,B3,C1,C2,C3);
	}

	//直方图
    public static  double [][] GetHistogram(BufferedImage img){
	    double [][] histgram=new double[3][256];
	    int width=img.getWidth();
	    int height=img.getHeight();
	    int pix[]=new int[width*height];
	    int r,g,b;
	    pix=img.getRGB(0,0,width,height,pix,0,width);
        for (int i=0;i<width*height;i++){
            r=pix[i]>>16&0xff;
            g=pix[i]>>8&0xff;
            b=pix[i]&0xff;
            histgram[0][r]++;
            histgram[1][g]++;
            histgram[2][b]++;
        }
        double red=0,green=0,blue=0;
        for(int j=0;j<256;j++){
            red+=histgram[0][j];
            green+=histgram[1][j];
            blue+=histgram[2][j];
        }
        for (int j=0;j<256;j++){
            histgram[0][j]/=red;
            histgram[1][j]/=green;
            histgram[2][j]/=blue;
        }
        return histgram;
    }
	public static void main(String[] arg) throws Exception{
		FileReader fr=new FileReader("D:\\K\\sc\\19\\Path.txt");
		@SuppressWarnings("resource")
		BufferedReader br=new BufferedReader(fr);
		while((pathin=br.readLine())!=null){
			//pathin=br.readLine();
			ColorOp(pathin);
			//System.out.println(pathin);
		}
		System.exit(0);
	}
}
