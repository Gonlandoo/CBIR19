package CBIR.PreOp;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class getDirection {

    public static void main(String[] args) throws IOException {
            //取得目标目录
            File file = new File("D:\\K\\sc\\19\\101_ObjectCategories");
            //获取目录下子文件及子文件夹
            File[] files = file.listFiles();
            File writeFile=new File("D:\\K\\sc\\19\\ppath.txt");

            readfile(files,writeFile);

        }

        public static void readfile(File[] files, File wFile) throws IOException {
            if (files == null) {// 如果目录为空，直接退出
                return;
            }
            ArrayList<String>filelist=new ArrayList<>();
            for(File f:files) {
                //如果是文件，直接输出名字
                if(f.isFile()) {
                    //System.out.println(f.getName());
                    String path=f.getAbsolutePath();
                    System.out.println(path);
                    filelist.add(path);
                }
                //如果是文件夹，递归调用
                else if(f.isDirectory()) {
                    readfile(f.listFiles(),wFile);
                }
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter("pppth.txt"));
            for (String s : filelist) {
                //System.out.println(s);
                bw.write(s);
                bw.newLine();
                bw.flush();
            }
            bw.close();
            //System.out.println(filelist.size());
        }
}