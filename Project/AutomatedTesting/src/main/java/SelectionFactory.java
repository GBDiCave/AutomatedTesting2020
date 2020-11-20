import Selection.SelectionImpl;
import Selection.classSelection;
import Selection.methodSelection;

public class SelectionFactory {
    public SelectionImpl init(String op){
        if(op.equals("-c")){
            return new classSelection();
        }else if(op.equals("-m")){
            return new methodSelection();
        }else{
            System.out.println("parameter wrong!");
            return null;
        }
    }
}
