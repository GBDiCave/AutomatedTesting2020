package Selection;

import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.util.CancelException;

import java.io.IOException;
import java.util.ArrayList;

public interface SelectionImpl {
    /*
    主要方法，完成静态测试与用例选择
     */
    ArrayList<String> select(AnalysisScope scope, ArrayList<String> changeInfo) throws ClassHierarchyException, CancelException, InvalidClassFileException, IOException;

    /*
    在不同粒度下对变更信息进行必要的精简
     */
    ArrayList<String> opInfo(ArrayList<String> changeInfo);

    /*
    辅助方法，确定函数是否为测试用例
     */
    boolean isTest(String name,String sign);
}
