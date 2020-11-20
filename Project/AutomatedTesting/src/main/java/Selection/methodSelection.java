package Selection;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.*;
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

public class methodSelection implements SelectionImpl {

    ArrayList<String> methodList = new ArrayList<String>();
    AnalysisScope scope;

    public ArrayList<String> select(AnalysisScope scope, ArrayList<String> changeInfo) throws ClassHierarchyException, IOException, CancelException, InvalidClassFileException {
        ClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(scope);
        Iterable<Entrypoint> eps = new AllApplicationEntrypoints(scope, cha);
        CHACallGraph chaCG = new CHACallGraph(cha);
        chaCG.init(new AllApplicationEntrypoints(scope, cha));

        //必要的初始化
        ArrayList<String> methodchange = opInfo(changeInfo);
        ArrayList<String> testchoose = new ArrayList<String>();
        NodesSet nodeMethod = new NodesSet();

        ArrayList<ShrikeBTMethod> Methods = new ArrayList<ShrikeBTMethod>();

        //简单的列表对应的数据结构，用于记录继承关系
        ArrayList<String> sonClass = new ArrayList<String>();
        ArrayList<String> fatherClass = new ArrayList<String>();

        //遍历chaCG获取wala生成的方法列表与method集合，并记录继承关系
        for(CGNode node: chaCG) {
            if(node.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                if("Application".equals(method.getDeclaringClass().getClassLoader().toString())) {
                    Methods.add(method);
                    String MclassInnerName = method.getDeclaringClass().getName().toString();
                    String Msignature = method.getSignature();
                    String fatherclass = method.getDeclaringClass().getSuperclass().getName().toString();
                    if(!fatherclass.equals("Ljava/lang/Object")){
                        sonClass.add(MclassInnerName);
                        fatherClass.add(fatherclass);
                    }
                    if(!methodList.contains(Msignature)){
                        methodList.add(Msignature);
                    }
                }
            }
        }
        //通过链表实现构建依赖图
        for(ShrikeBTMethod method:Methods){
            String MclassInnerName = method.getDeclaringClass().getName().toString();
            String Msignature = method.getSignature();
            Collection<CallSiteReference> methods = method.getCallSites();
            for(CallSiteReference c:methods){
                String CclassInnerName = c.getDeclaredTarget().getDeclaringClass().getName().toString();
                String Csignature = c.getDeclaredTarget().getSignature();
                //核心依赖构建
                //第一层，若caller和callee同在列表中，直接生成链接关系
                //第二层，若不在，对callee进行方法变换变为父类方法，若在则生成链接关系，若不在则无需生成
                if(methodList.contains(Msignature)&&methodList.contains(Csignature)){
                    nodeMethod.addNode(MclassInnerName,Msignature,CclassInnerName,Csignature);
                }else{
                    if(sonClass.contains(CclassInnerName)){
                        int loc = sonClass.indexOf(CclassInnerName);
                        String FclassInnerName = fatherClass.get(loc);
                        String Fsignature = opFSClass(FclassInnerName,Csignature);
                        if(methodList.contains(Msignature)&&methodList.contains(Fsignature)){
                            nodeMethod.addNode(MclassInnerName,Msignature,CclassInnerName,Csignature);
                            nodeMethod.addNode(CclassInnerName,Csignature,FclassInnerName,Fsignature);
                        }
                    }
                }
            }
        }
        //变更信息输入
        methodchange = nodeMethod.changeMethod(methodchange);
        //测试用例选择
        for(ShrikeBTMethod method:Methods){
            String MclassInnerName = method.getDeclaringClass().getName().toString();
            String Msignature = method.getSignature();
            boolean isTest = isTest(MclassInnerName,Msignature);
            Collection<CallSiteReference> methods = method.getCallSites();
            for(CallSiteReference c:methods){
                String Csignature = c.getDeclaredTarget().getSignature();
                if(isTest&&methodchange.contains(Csignature)){
                    String temp = MclassInnerName + " " + Msignature;
                    if(!testchoose.contains(temp)){
                        testchoose.add(temp);
                    }
                }
            }
        }
        /*System.out.println("testchoose:");
        int t = 0;
        for(String i :testchoose){
            System.out.println(i);
            t++;
        }
        System.out.println(t);*/

        /*ArrayList<String> strs = nodeMethod.getMethodDepGraph();
        DepGraph graph = new DepGraph();
        graph.draw(strs,"-m");*/

        return testchoose;
    }

    /*
    子类方法转化为父类
     */
    private String opFSClass(String farClass, String csignature) {
        if(farClass.equals("Ljava/lang/Object")) return "";
        String words = new StringBuilder(csignature).reverse().toString();
        int num = words.indexOf(".");
        String str1 = words.substring(0,num);
        String str2 = "";
        for(int i=1;i<farClass.length();i++){
            if(farClass.charAt(i)=='/'){
                str2 = str2+".";
            }else{
                str2 = str2 + farClass.substring(i,i+1);
            }
        }
        String ans = str2+"."+new StringBuilder(str1).reverse().toString();
        return ans;
    }

    /*
    取修改类
     */
    public ArrayList<String> opInfo(ArrayList<String> changeInfo) {
        ArrayList<String> temp = new ArrayList<String>();
        for(String i : changeInfo){
            String str = i.split(" ")[1];
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
