package CBIR.PreOp;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

import javax.imageio.ImageIO;

public class Prepare {

	static String pathin;
	//static int num=0;
	private static BufferedImage img;
	private final static int WIDTH=120;
	private final static int HEIGHT=150;

	public static void main(String[] arg) throws Exception{

        //读取Path.txt文件
		FileReader fr=new FileReader("D:\\K\\sc\\19\\Path.txt");
		@SuppressWarnings("resource")
		BufferedReader br=new BufferedReader(fr);
		while((pathin=br.readLine())!=null){
            //pathin=br.readLine();
            //通过获得的字符串路径寻找对应文件夹
			File sourceimage=new File(pathin);
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(sourceimage.getPath()));
			img=ImageIO.read(in);
//			获取图片大小
			int width=img.getWidth();
			int height=img.getHeight();
			
			ColorModel cm=ColorModel.getRGBdefault();
			int pixels[]=new int[width*height];
            int[] tpRed=new int[9];
            int[] tpGreen=new int[9];
            int[] tpBlue=new int[9];
            img.getRGB(0, 0, width, height, pixels, 0, width);//pixels数组保存图像的RGB值
//            for(int i=0;i<width*height;i++)
//            	System.out.print(pixels[i]);

            //图像去躁——中值滤波

            for(int i=1;i<height-1;i++){
                for(int j=1;j<width-1;j++){
                    //获得周围的R值
                    tpRed[0]=cm.getRed(pixels[(i-1)*width+j-1]);
                    tpRed[1]=cm.getRed(pixels[(i-1)*width+j]);
                    tpRed[2]=cm.getRed(pixels[(i-1)*width+j+1]);
                    tpRed[3]=cm.getRed(pixels[i*width+j-1]);
                    tpRed[4]=cm.getRed(pixels[i*width+j]);
                    tpRed[5]=cm.getRed(pixels[i*width+j+1]);
                    tpRed[6]=cm.getRed(pixels[(i+1)*width+j-1]);
                    tpRed[7]=cm.getRed(pixels[(i+1)*width+j]);
                    tpRed[8]=cm.getRed(pixels[(i+1)*width+j+1]);
                    //从小到大排序
                    for(int rj=0;rj<8;rj++){
	                    for(int ri=0;ri<8-rj;ri++){
	                        if(tpRed[ri]>tpRed[ri+1]){
	                            int Red_Temp=tpRed[ri];
	                            tpRed[ri]=tpRed[ri+1];
	                            tpRed[ri+1]=Red_Temp;
	                        }
	                    }
                    }
                    int medianRed=tpRed[4];//取得中间值

                    //获得周围的G值
                    tpGreen[0]=cm.getGreen(pixels[(i-1)*width+j-1]);
                    tpGreen[1]=cm.getGreen(pixels[(i-1)*width+j]);
                    tpGreen[2]=cm.getGreen(pixels[(i-1)*width+j+1]);
                    tpGreen[3]=cm.getGreen(pixels[i*width+j-1]);
                    tpGreen[4]=cm.getGreen(pixels[i*width+j]);
                    tpGreen[5]=cm.getGreen(pixels[i*width+j+1]);
                    tpGreen[6]=cm.getGreen(pixels[(i+1)*width+j-1]);
                    tpGreen[7]=cm.getGreen(pixels[(i+1)*width+j]);
                    tpGreen[8]=cm.getGreen(pixels[(i+1)*width+j+1]);
                    for(int rj=0;rj<8;rj++){
                        for(int ri=0;ri<8-rj;ri++){
                            if(tpGreen[ri]>tpGreen[ri+1]){
                                int Green_Temp=tpGreen[ri];
                                tpGreen[ri]=tpGreen[ri+1];
                                tpGreen[ri+1]=Green_Temp;
                            }
                        }
                    }
                    int medianGreen=tpGreen[4];

                    //获得周围的B值
                    tpBlue[0]=cm.getBlue(pixels[(i-1)*width+j-1]);
                    tpBlue[1]=cm.getBlue(pixels[(i-1)*width+j]);
                    tpBlue[2]=cm.getBlue(pixels[(i-1)*width+j+1]);
                    tpBlue[3]=cm.getBlue(pixels[i*width+j-1]);
                    tpBlue[4]=cm.getBlue(pixels[i*width+j]);
                    tpBlue[5]=cm.getBlue(pixels[i*width+j+1]);
                    tpBlue[6]=cm.getBlue(pixels[(i+1)*width+j-1]);
                    tpBlue[7]=cm.getBlue(pixels[(i+1)*width+j]);
                    tpBlue[8]=cm.getBlue(pixels[(i+1)*width+j+1]);
                    for(int rj=0;rj<8;rj++){
                        for(int ri=0;ri<8-rj;ri++){
                            if(tpBlue[ri]>tpBlue[ri+1]){
                                int Blue_Temp=tpBlue[ri];
                                tpBlue[ri]=tpBlue[ri+1];
                                tpBlue[ri+1]=Blue_Temp;
                            }
                        }
                    }
                    int medianBlue=tpBlue[4];
                    
                    int rgb=255<<24|medianRed<<16|medianGreen<<8|medianBlue;
                    img.setRGB(j,i,rgb);
                }
            }
            //改变图片尺寸，并替换原图片（构建图片流）
            BufferedImage tag=new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
			tag.getGraphics().drawImage(img,0,0, WIDTH, HEIGHT, null);
			BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream(sourceimage.getPath()));
			ImageIO.write(tag, "jpg", out);
			//num++;
		}
		//System.out.println(num);
		System.exit(0);
	}
}
