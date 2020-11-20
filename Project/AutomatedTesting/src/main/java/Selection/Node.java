package Selection;

import java.util.ArrayList;

/*
通过链表实现，链表记录类和方法
通过ischanged属性完成变更输入与变更传递
 */
public class Node {
    String Name;
    String Signature;
    ArrayList<Node> siteNodes;
    boolean isTest;
    boolean isChange;

    public String getName(){
        return Name+" "+Signature;
    }

    public Node(String name){
        this.Name = name;
        siteNodes = new ArrayList<Node>();
        this.isTest = isTest(name);
        this.isChange = false;
    }

    public Node(String name, String signature){
        this.Name = name;
        this.Signature = signature;
        siteNodes = new ArrayList<Node>();
        this.isTest = isTest(name,signature);
        this.isChange = false;
    }

    public void addSiteNode(Node sitenode){
        siteNodes.add(sitenode);
    }

    public boolean equals(Node node){
        if(this.Name.equals(node.Name)&&this.Signature.equals(node.Signature)){
            return true;
        }else{
            return false;
        }
    }

    public boolean equals(String n,String s){
        if(this.Name.equals(n)&&this.Signature.equals(s)){
            return true;
        }else{
            return false;
        }
    }


    private boolean isTest(String name){
        if(name.indexOf("Test")!=-1){
            return true;
        }else{
            return false;
        }
    }

    private boolean isTest(String name,String sign){
        if(name.indexOf("Test")!=-1&&sign.indexOf("<init>")==-1){
            return true;
        }else{
            return false;
        }
    }
}
