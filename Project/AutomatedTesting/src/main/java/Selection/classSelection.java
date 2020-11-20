package Selection;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.util.CancelException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class classSelection implements SelectionImpl {

    ArrayList<String> classList = new ArrayList<String>();

    public ArrayList<String> select(AnalysisScope scope, ArrayList<String> changeInfo) throws ClassHierarchyException, CancelException, InvalidClassFileException, IOException {
        ClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(scope);
        Iterable<Entrypoint> eps = new AllApplicationEntrypoints(scope, cha);
        CHACallGraph chaCG = new CHACallGraph(cha);
        chaCG.init(new AllApplicationEntrypoints(scope, cha));

        //必要的初始化
        ArrayList<String> classchange = opInfo(changeInfo);
        ArrayList<String> testchoose = new ArrayList<String>();
        NodesSet nodeClass = new NodesSet();

        //获取需要的method集合和类集合
        ArrayList<ShrikeBTMethod> Methods = new ArrayList<ShrikeBTMethod>();
        for(CGNode node: chaCG) {
            if(node.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                if("Application".equals(method.getDeclaringClass().getClassLoader().toString())) {
                    Methods.add(method);
                    String MclassInnerName = method.getDeclaringClass().getName().toString();
                    if(!classList.contains(MclassInnerName)){
                        classList.add(MclassInnerName);
                    }
                }
            }
        }
        //for(String i : classList){
        //    System.out.println(i);
        //}
        //System.out.println();

        //通过链表实现构建依赖图
        for(ShrikeBTMethod method:Methods){
            String MclassInnerName = method.getDeclaringClass().getName().toString();
            String Msignature = method.getSignature();
            Collection<CallSiteReference> methods = method.getCallSites();
            for(CallSiteReference c:methods){
                String CclassInnerName = c.getDeclaredTarget().getDeclaringClass().getName().toString();
                if(classList.contains(MclassInnerName)&&classList.contains(CclassInnerName)){
                    nodeClass.addNode(MclassInnerName,CclassInnerName);
                }
            }
        }

        //变更信息输入
        classchange = nodeClass.changeClass(classchange);

        //测试用例选择
        for(ShrikeBTMethod method:Methods){
            String MclassInnerName = method.getDeclaringClass().getName().toString();
            String Msignature = method.getSignature();
            boolean isTest = isTest(MclassInnerName,Msignature);
            Collection<CallSiteReference> methods = method.getCallSites();
            for(CallSiteReference c:methods){
                String CclassInnerName = c.getDeclaredTarget().getDeclaringClass().getName().toString();
                if(isTest&&classchange.contains(CclassInnerName)){
                    String temp = MclassInnerName + " " + Msignature;
                    if(!testchoose.contains(temp)){
                        testchoose.add(temp);
                    }
                }
            }
        }
        //int t = 0;
        //for(String i :testchoose){
        //    System.out.println(i);
        //    t++;
        //}
        //System.out.println(t);

        //生成依赖图
        /*ArrayList<String> strs = nodeClass.getClassDepGraph();
        DepGraph graph = new DepGraph();
        graph.draw(strs,"-c");*/

        return testchoose;
    }

    /*
    取修改类
     */
    public ArrayList<String> opInfo(ArrayList<String> changeInfo) {
        ArrayList<String> temp = new ArrayList<String>();
        for(String i : changeInfo){
            String str = i.split(" ")[0];
            if(!temp.contains(str)){
                temp.add(str);
            }
        }
        return temp;
    }
    /*
    <init>存在的Test*类中不属于测试用例
     */
    public boolean isTest(String name, String sign){
        if(name.indexOf("Test")!=-1&&sign.indexOf("<init>")==-1){
            return true;
        }else{
            return false;
        }
    }

}
