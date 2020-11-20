package Selection;

import java.util.ArrayList;

/*
链表工厂，实现addNode方法、changeMethod/changeClass方法（用于变更传递的开始）
 */
public class NodesSet {
    ArrayList<Node> nodes;

    public NodesSet(){
        nodes = new ArrayList<Node>();
    }

    public ArrayList<String> getClassDepGraph(){
        ArrayList<String> strs = new ArrayList<String>();
        for(Node i : nodes){
            for(Node j:i.siteNodes){
                String temp = "\"" + i.Name + "\"" +"->"+ "\""+j.Name+"\"";
                if(!strs.contains(temp)){
                    strs.add(temp);
                }
            }
        }
        return strs;
    }


    public ArrayList<String> getMethodDepGraph(){
        ArrayList<String> strs = new ArrayList<String>();
        for(Node i : nodes){
            for(Node j:i.siteNodes){
                String temp = "\"" + i.Signature + "\"" +"->"+ "\""+j.Signature+"\"";
                if(!strs.contains(temp)){
                    strs.add(temp);
                }
            }
        }
        return strs;
    }

    public void addNode(String name1,String name2){
        Node node1 = null;
        Node node2 = null;

        boolean find = false;
        for(Node i : nodes){
            if(i.Name.equals(name1)){
                node1 = i;
                find = true;
                break;
            }
        }
        if(!find){node1 = new Node(name1);nodes.add(node1);}
        find = false;
        for(Node i : nodes){
            if(i.Name.equals(name2)){
                node2 = i;
                find = true;
                break;
            }
        }
        if(!find){node2 = new Node(name2);nodes.add(node2);}

        for(Node k: node2.siteNodes){
            if(k.Name.equals(node1.Name)){
                return;
            }
        }
        node2.addSiteNode(node1);
        return;
    }

    public void addNode(String n1,String s1,String n2,String s2){
        Node node1 = null;
        Node node2 = null;

        boolean find = false;
        for(Node i : nodes){
            if(i.equals(n1,s1)){
                node1 = i;
                find = true;
                break;
            }
        }
        if(!find){node1 = new Node(n1,s1);nodes.add(node1);}
        find = false;
        for(Node i : nodes){
            if(i.equals(n2,s2)){
                node2 = i;
                find = true;
                break;
            }
        }
        if(!find){node2 = new Node(n2,s2);nodes.add(node2);}

        for(Node k: node2.siteNodes){
            if(k.equals(node1)){
                return;
            }
        }
        node2.addSiteNode(node1);
        return;
    }

    public ArrayList<String> changeMethod(ArrayList<String> changeInfo){
        for(Node i : nodes){
            if(changeInfo.contains(i.Signature)){
                i.isChange = true;
                changeSelf(i);
            }
        }
        ArrayList<String> changed = new ArrayList<String>();
        for(Node i:nodes){
            if(i.isChange){
                changed.add(i.Signature);
            }
        }
        return changed;
    }

    public ArrayList<String> changeClass(ArrayList<String> changeInfo) {
        for(Node i :nodes){
            if(changeInfo.contains(i.Name)){
                i.isChange = true;
                changeSelf2(i);
            }
        }
        ArrayList<String> changed = new ArrayList<String>();
        for(Node i:nodes){
            if(i.isChange){
                changed.add(i.Name);
            }
        }
        return changed;
    }

    private void changeSelf(Node n) {
        if(n.siteNodes.size()==0)return;
        for(Node temp:n.siteNodes){
            if(temp.equals(n)){
                continue;
            }
            temp.isChange = true;
            changeSelf(temp);
        }
    }

    private void changeSelf2(Node n) {
        if(n.siteNodes.size()==0)return;
        for(Node temp:n.siteNodes){
            if(temp.Name.equals(n.Name)||temp.isChange){
                continue;
            }
            temp.isChange = true;
            changeSelf2(temp);
        }
    }
}
