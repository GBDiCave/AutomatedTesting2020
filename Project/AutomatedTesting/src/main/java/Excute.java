import Selection.SelectionImpl;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.io.FileProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Excute {

    AnalysisScope scope;
    ArrayList<String> scopeList;
    ArrayList<String> changeInfo;

    public void run(String[] args) throws IOException, InvalidClassFileException, ClassHierarchyException, CancelException {
        String op = args[0];
        String rootAddress = args[1];
        String change_InfoAddress = args[2];

        scopeList = new ArrayList<String>();
        changeInfo = new ArrayList<String>();

        //初始化scope
        ClassLoader classloader = Excute.class.getClassLoader();
        scope = AnalysisScopeReader.readJavaScope("scope.txt", new File("exclusion.txt"), classloader);
        File rootfile = new FileProvider().getFile(rootAddress);
        fillScope(rootfile);
        //System.out.println(scope);

        //初始化变更信息
        File change_Info = new File(change_InfoAddress);
        fillInfo(change_Info);

        //静态测试与用例选择
        SelectionFactory fac = new SelectionFactory();
        SelectionImpl sec = fac.init(op);
        ArrayList<String> testchoose = sec.select(scope,changeInfo);

        //输出
        Output output = new Output();
        output.cout(testchoose,args[0]);
    }

    public void fillScope(File file) throws IOException, InvalidClassFileException {
        if(file.isDirectory()){
            String[] child = file.list();
            for(String a:child){
                String childAddress = file.getPath()+"\\"+a;
                File childfile = new FileProvider().getFile(childAddress);
                fillScope(childfile);
            }
        }else{
            if(file.getPath().indexOf(".class")!=-1){
                scope.addClassFileToScope(ClassLoaderReference.Application, file);
                scopeList.add(file.getName());
            }
        }
        return;
    }

    public void fillInfo(File file){
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){
                changeInfo.add(s);
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return;
    }
}
