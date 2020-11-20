package Selection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DepGraph {
    public void draw(ArrayList<String> strs,String op) throws IOException {
        //文件名
        String dirlocate = "";
        String type = null;
        if(op.equals("-c")){
            type = "class";
        }else if(op.equals("-m")){
            type = "method";
        }
        String time = ""+ System.currentTimeMillis();
        String loc = dirlocate+"\\"+ type + time.substring(7)+".dot";
        File file = new File(loc);
        //如果文件不存在，创建一个文件
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(file);
            //写入内容
            String content = "digraph graph_"+ type +" {\n";
            for(String i:strs){
                content = content + "\t" + i + "\n";
            }
            content = content + "}";

            bw = new BufferedWriter(fw);
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bw.close();
            fw.close();
        }
    }
}
