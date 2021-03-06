package Model;

import java.util.ArrayList;
import java.util.HashMap;

public class ParentNode extends TreeNode {
    private ArrayList<TreeNode> children;
    private String criteria;
    private Float weight;
    private HashMap<String, Float> aggregateScore; // used to record aggregation of all children

    // ========== contructors ==========
    public ParentNode(){
        studentPool = null;
        children = new ArrayList<>();
        criteria = "DEFAULT CRITERIA";
        weight = new Float(0);
        aggregateScore = new HashMap<>();
    }

    public ParentNode(String criteria, float weight){
        studentPool = null;
        children = new ArrayList<>();
        this.criteria = criteria;
        this.weight =  new Float(weight);
        aggregateScore = new HashMap<>();
    }

    // ====== required methods from Model.TreeNode
    public void traverse(int depth){
        String padsym = "  ";
        String padding = "";
        for (int i=0; i<4*depth; i++)
            padding += padsym;

        System.out.println(padding+criteria+",weight="+weight);
        for (TreeNode child : children){
            child.traverse(depth+1);
        }
    }

    public TreeNode copyStructure(){
        // if it's just the last parent before LeafNode,
        // don't copy anything
        if (children.get(0).isLeaf())
            return null;

        ParentNode retnode = new ParentNode();
        retnode.setCriteria(criteria);
        retnode.setWeight(weight);
        for (TreeNode child : children){
            TreeNode childroot = child.copyStructure();
            if (childroot != null)
                retnode.addChild(childroot);
        }
        return retnode;
    }

    public void connectStudentPool(StudentPool pool) {
        if (studentPool==null){
            studentPool = pool;
        }

        for (TreeNode child : children)
            child.connectStudentPool(pool);
    }

    public ArrayList<String> treeValidation(ArrayList<String> errorSofar, boolean checkRootChildrenOnly){
        if (errorSofar == null) {
            errorSofar = new ArrayList<>();
        } else {
            int lastIdx = errorSofar.size()-1;
            if (lastIdx>=0)
                errorSofar.remove(lastIdx);
        }

        // check children weight sum to 100
        Float totalWeight = 0f;
        for (TreeNode child : children){
            if (!checkRootChildrenOnly) {
                errorSofar.add(criteria); // tricky way to pass previous level criteria (to locate leaf position)
                errorSofar = child.treeValidation(errorSofar, checkRootChildrenOnly);
            }
            totalWeight += child.getWeight();
        }
        if (!totalWeight.equals(100f))
            errorSofar.add("sub-categories weight sum in [" + criteria + "] not equal to 100%");

        return errorSofar;
    }

    public HashMap<String, Float> computeFinalScore(){
        // aggregate all weighted scores from children & accumulate them in aggregationScore

        // not first time of calling this function, set all score to zero & re-accumulate
        if (aggregateScore.size() > 0){
            for (HashMap.Entry<String, Float> entry : aggregateScore.entrySet()) {
                aggregateScore.put(entry.getKey(), 0f);
            }
        }

        HashMap<String, Float> childrenScore = null;
        for (TreeNode child : children){
            childrenScore = child.computeFinalScore();

            for (HashMap.Entry<String, Float> entry : childrenScore.entrySet()) {
                if (aggregateScore.containsKey(entry.getKey())){
                    Float newScore = aggregateScore.get(entry.getKey()) + entry.getValue();
                    aggregateScore.replace(entry.getKey(), newScore);
                } else {
                    aggregateScore.put(entry.getKey(), entry.getValue());
                }
            }
        }

        // return a new HashMap obj that multiply each score with weight in this node
        HashMap<String, Float> ret = new HashMap<>();
        for (HashMap.Entry<String, Float> entry : aggregateScore.entrySet()) {
            ret.put(entry.getKey(), new Float(entry.getValue()*weight/100));
        }

        return ret;
    }

    public Object[] genFieldRowArray(){
        int numColumn;
        Object[] fieldRow=null;
        if(children.size()>0){
            if (((ParentNode)children.get(0)).getChildNum()>0 && children.get(0).getChild(0).isLeaf()) {
                // assignment view
                numColumn = children.size() + 1;
                fieldRow = new Object[numColumn];
            }
            else {   //!children.get(0).getChild(0).isLeaf()){
                // summarization view
                numColumn = children.size() + 2;
                fieldRow = new Object[numColumn];
                fieldRow[numColumn - 1] = new Dummy("Final Percentage", null);
            }
        } else {
            // this means no children yet in this node
            // ex: new class, before 1st click "add column"  (HW -> (empty)
            numColumn = 1;
            fieldRow = new Object[numColumn];
        }

        assert(fieldRow != null);

        if (studentPool==null)
            fieldRow[0] = new Dummy("Import student", null);
        else
            fieldRow[0] = new Dummy("Student", studentPool);
        int currIdx = 1;
        for (TreeNode child : children){
            fieldRow[currIdx++] = child;
        }

        return fieldRow;
    }

    public Object[][] genScoreTableArray(ArrayList<String> studentOrder){
        // this function can only be called when its grand-children is Model.LeafNode

        if (studentOrder==null){
            // used when no student info available
            Object[][] retTable = new Object[0][0];
            return retTable;
        }

        // assert(children.size()>0 && children.get(0).getChild(0).isLeaf());

        // row # = # student + 3  (extra 3 row : Grading Option / Total Score / Average
        // col # = # children + 1 (extra 1 col : idx0, student information
        int numRow = 3 + studentOrder.size();
        int numCol = 1 + children.size();
        Object[][] retTable = new Object[numRow][numCol];

        // 1st row : grading option (i.e. input type)
        retTable[0][0] = new Dummy("Grading Option", null);
        for (int i=1; i<numCol; i++){
            LeafNode curr = ((LeafNode)children.get(i-1).getChild(0));
            Dummy tmp = new Dummy(curr.getInputType().toString(), curr);
            retTable[0][i] = tmp;
        }

        // 2nd row : total score
        retTable[1][0] = new String("Total Score");
        for (int i=1; i<numCol; i++){
            LeafNode curr = ((LeafNode)children.get(i-1).getChild(0));
            Dummy tmp = new Dummy(curr.getTotalScore().toString(), curr);
            retTable[1][i] = tmp;
        }

        // 3rd row : statistic button
        retTable[2][0] = new String("Statistics");
        for (int i=1; i<numCol; i++){
            Statistics statisticsObj =  new Statistics((LeafNode)children.get(i-1).getChild(0));
            retTable[2][i] = statisticsObj;
        }

        // remaining rows : row by row student score
        int rowStart = 3;
        for (int row = rowStart; row<numRow; row++){
            String studentkey = studentOrder.get(row-rowStart);
            for (int col = 0; col<numCol; col++){
                if (col==0) {
                    // 1st column is student info
                    retTable[row][col] = studentPool.getStudentByKey(studentkey);
                }
                else{
                    // other columns are scores
                    retTable[row][col] = ((LeafNode)(children.get(col-1).getChild(0))).getLeafByKey(studentkey);
                }
            }
        }

        return retTable;
    }

    public Object[][] genSummaryTableArray(ArrayList<String> studentOrder){
        // this function can only be called when its children is NOT Model.LeafNode
        // TODO: implicit assumption : called @ root

        if (studentOrder==null){
            // used when no student info available
            // now just construct columns of
            Object[][] retTable = new Object[0][0];

            return retTable;
        }

        //assert(children.size()>0 && !children.get(0).getChild(0).isLeaf());

        int numCol = children.size()+2; // 2 extra col : idx0 : student info / idxlast : final score
        int numRow = studentOrder.size()+2; // 2 extra row (in-order) : weight, statistics
        Object[][] retTable = new Object[numRow][numCol];

        // 1st row: weight of that criteria
        retTable[0][0] = "Weight";
        retTable[0][numCol-1] = "100";
        for (int i=1; i<=children.size(); i++)
             retTable[0][i] = children.get(i-1).getWeight();

        // 2nd row: statistics
        retTable[1][0] = "Statistics";
        for (int i=1; i<=children.size(); i++){
            Statistics statisticsObj =  new Statistics(((ParentNode)children.get(i-1)).getAggregateScore());
            retTable[1][i] = statisticsObj;
        }
        retTable[1][numCol-1] = new Statistics(this.aggregateScore);

        int currRow = 2;
        if (aggregateScore.size()==0){
            // yet click calc final score, fill in every score with buf string
            String buf = "";
            for(int i=currRow; i<numRow; i++){
                Student currStudent = studentPool.getStudentByKey(studentOrder.get(i-currRow));
                for (int j=0; j<numCol; j++){
                    if (j==0)
                        retTable[i][j] = currStudent;
                    else
                        retTable[i][j] = buf;
                }
            }
        } else {
            for(int i=currRow; i<numRow; i++){
                String currStudentPKey =  studentOrder.get(i-currRow);
                for (int j=0; j<numCol; j++){
                    if (j==0) { // first column is student information
                        Student currStudent = studentPool.getStudentByKey(currStudentPKey);
                        retTable[i][j] = currStudent;
                    } else if (j>0 && j<numCol-1){ // other columns are score break down
                        ParentNode currNode = (ParentNode)children.get(j-1);
                        retTable[i][j] = currNode.getAggregateScore().get(currStudentPKey);
                    } else { // last column is overall final score
                        retTable[i][j] = this.aggregateScore.get(currStudentPKey);
                    }
                }
            }
        }

        return retTable;
    }

    public boolean isLeaf(){return false;}
    // ========================================

    // ========== getters =========
    public Float getWeight(){return weight;}
    public TreeNode getChild(int childIndex){ return children.get(childIndex);}
    public int getChildNum(){return children.size();}
    public ArrayList<ParentNode> getAllChildren(){
        // this function is only called when its children not leaf (either no children or parentNode)
        assert(children.size()==0 || !children.get(0).isLeaf());
        ArrayList<ParentNode> ret = new ArrayList<>();
        for (TreeNode child : children){
            ret.add((ParentNode)child);
        }
        return ret;
    }
    public StudentPool getStudentPool(){return studentPool;}
    public HashMap<String, Float> getAggregateScore(){return aggregateScore;}
    public String getCriteria(){return criteria;}
    // ========================================

    // ========== setters ==========
    public void setCriteria(String criteria){this.criteria = criteria;}
    public void setWeight(float weight){this.weight = weight;}
    public void addChild(TreeNode child){
        children.add(child);
    }
    public void removeChild(int childIdx){
        children.remove(childIdx);
    }
    public void updateCurrNode(String classname, ArrayList<String> criteria_name, ArrayList<String> criteria_weight){
        this.criteria = classname;
        this.children.clear();
        assert(criteria_name.size()==criteria_weight.size());
        for (int i=0; i<criteria_name.size(); i++){
            ParentNode tmp = new ParentNode();
            tmp.setCriteria(criteria_name.get(i));
            float currweight = Float.parseFloat(criteria_weight.get(i).trim());
            tmp.setWeight(currweight);
            children.add(tmp);
        }
    }
    // ========================================

    @Override
    public String toString(){
        return criteria;
    }

    // temporay test functions
    public void printAggregateResult(){
        System.out.println("Score aggregate to -> " + criteria);
        for (HashMap.Entry<String, Float> entry : aggregateScore.entrySet()) {
            System.out.println("    " + entry.getKey() + " get " + entry.getValue() + "%");
        }
        System.out.println("\n\n");
    }
}
