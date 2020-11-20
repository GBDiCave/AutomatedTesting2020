import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.io.FileProvider;

import java.io.File;
import java.io.IOException;

/*
    this file is used to learn how to use wala, which is useless in running
 */
public class ExampleCode {
    public void run() throws IOException, ClassHierarchyException, CancelException, InvalidClassFileException {
        // 获得一个文件
        File file=new FileProvider().getFile(
                "C:\\Users\\86183\\Desktop\\ClassicAutomatedTesting\\ClassicAutomatedTesting\\0-CMD\\target\\classes\\net\\mooctest\\CMD.class");
        ClassLoader classloader = ExampleCode.class.getClassLoader();
        AnalysisScope scope = AnalysisScopeReader.readJavaScope("scope.txt", new File("exclusion.txt"), classloader);
        scope.addClassFileToScope(ClassLoaderReference.Application, file);
        // 1.生成类层次关系对象
        ClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(scope);
        // 2.生成进入点
        Iterable<Entrypoint> eps = new AllApplicationEntrypoints(scope, cha);
        // 3.利用CHA算法构建调用图
        CallGraph cg = new CHACallGraph(cha);
        ((CHACallGraph) cg).init(eps);
        //System.out.println(cg);
        // 4.遍历cg中所有的节点
        for(CGNode node: cg) {
        // node中包含了很多信息，包括类加载器、方法信息等，这里只筛选出需要的信息
            if(node.getMethod() instanceof ShrikeBTMethod) {
                // node.getMethod()返回一个比较泛化的IMethod实例，不能获取到我们想要的信息
                // 一般地，本项目中所有和业务逻辑相关的方法都是ShrikeBTMethod对象
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                // 使用Primordial类加载器加载的类都属于Java原生类，我们一般不关心。
                if("Application".equals(method.getDeclaringClass().getClassLoader().toString())) {
                    // 获取声明该方法的类的内部表示
                    String classInnerName = method.getDeclaringClass().getName().toString();
                    // 获取方法签名
                    String signature = method.getSignature();
                    System.out.println(classInnerName + "  sign: " + signature);
                }
            } else {
                System.out.println(String.format("'%s'不是一个ShrikeBTMethod：%s",
                        node.getMethod(),
                        node.getMethod().getClass()));
            }
        }
    }
}
