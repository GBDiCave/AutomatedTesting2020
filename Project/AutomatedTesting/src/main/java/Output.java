import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Output {

    public void cout(ArrayList<String> strs,String op) throws IOException {
        //文件名
        String dirlocate = "";
        String name;
        if(op.matches("-c")){
            name = "selection-class.txt";
        }else{
            name = "selection-method.txt";
        }
        String loc = dirlocate + name;
        File file = new File(loc);
        //如果文件不存在，创建一个文件
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(file);

            String content = "";
            for(String i:strs){
                content = content + i + "\n";
            }
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
