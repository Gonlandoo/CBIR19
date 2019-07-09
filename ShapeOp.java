package CBIR;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

import org.opencv.*;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.CvType.*;


public class ShapeOp {
    static String pathin;
    private static BufferedImage img;
    public static void EdgeOp(String pathin) throws Exception{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
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
        M[0]=u20+u02;
        M[1]=t1*t1+4*u11*u11;
        M[2]=t2*t2+t3*t3;
        M[3]=t4*t4+t5*t5;
        M[4]=t2*t4*(t4*t4-3*t5*t5)+t3*t5*(3*t4*t4-t5*t5);
        M[5]=t1*(t4*t4-t5*t5)+4*u11*t4*t5;
        M[6]=t3*t4*(t4*t4-3*t5*t5)-t2*t5*(3*t4*t4-t5*t5);

        in.close();
        JDBC jdbc=new JDBC();
        jdbc.shape_feature(M);

    }

    public static void main(String[] args) throws Exception {
        FileReader fr=new FileReader("D:\\K\\sc\\19\\Path.txt");
        @SuppressWarnings("resource")
        BufferedReader br=new BufferedReader(fr);
        while((pathin=br.readLine())!=null){
            //pathin=br.readLine();
            EdgeOp(pathin);
            //System.out.println(pathin);
        }
        System.exit(0);
    }
}
