/**
 * TwoThreeTree
 * 
 * PURPOSE: A 2-3 Tree that contains words as its data.
 */

public class TwoThreeTree implements GameTree{
    
    // root of the tree
    private Node root;

    //constructor
    public TwoThreeTree() {
        root = null;
    }

    /**
     * addWord
     * 
     * Adds a word to the tree, updating its frequency if it already exists. 
     * Treats all words as case-insensitive.
     * 
     * @param word The word to add.
     */
    @Override
    public void addWord(String word) {
        // check if the word exists already
        if(containsWord(word)){
            //find the node and update its frequency
            Node targetNode = findNode(word);
            // within the node, find the correct data piece to update
            for(int i = 0; i < targetNode.data.length; i++){
                if(targetNode.data[i].word != null){
                    if (targetNode.data[i].word.equalsIgnoreCase(word)){
                        targetNode.data[i].frequency++;
                    }
                }
            }
        } else{
            //insert
            if (root == null){
                // creating a new root and updating its first data value
                Node newNode = new Node();
                newNode.data[0].word = word;
                newNode.data[0].frequency++;
                newNode.numWords++;
                root = newNode;
            } else {
                // tree is not empty
                //find the leaf node where the initial vaue insert happens before the 
                    //potential split
                Node curr = root;
                Node prev = null;

                // iterate until we fall of the tree and find the leaf node
                while (curr != null){
                    prev = curr;
                    if(curr.numWords == 2){
                        // dealing with a node that that two pieces of data
                        curr = twoDataNodeHelp(curr, word);
                    } else{
                        // node only has one piece of data
                        curr = oneDataNodeHelp(curr, word);
                    }
                }

                // leaf node found, and now we add the new data piece
                DataPair newData = new DataPair();
                newData.word = word;
                newData.frequency++;

                // the leaf node we found has two pieces of data already
                if(prev.numWords == 2){
                    if(word.compareToIgnoreCase(prev.data[0].word) < 0){
                        // inserting in front..leftmost
                        // move pos 1 to 2
                        DataPair temp1 = prev.data[1];
                        prev.data[2] = temp1;
                        // move pos 0 to 1
                        DataPair temp2 = prev.data[0];
                        prev.data[1] = temp2;
                        // insert at pos 0
                        prev.data[0] = newData;
                    } else if(word.compareToIgnoreCase(prev.data[1].word) > 0){
                        // inserting at the end..rightmost
                        // insert at pos 2
                        prev.data[2] = newData;
                    } else{
                        // inserting in the middle
                        // move pos 1 to 2
                        DataPair temp3 = prev.data[1];
                        prev.data[2] = temp3;
                        // insert at pos 1
                        prev.data[1] = newData;
                    }
                } else{
                    // the leaf node we found has one piece of data 
                    if(word.compareToIgnoreCase(prev.data[0].word) < 0){
                        // move the word over and insert in the front
                        DataPair temp = prev.data[0];
                        prev.data[1] = temp;
                        prev.data[0] = newData;
                    } else{
                        //insert at spot 1..ie the end
                        prev.data[1] = newData;
                    }
                }
                //increment numwords
                prev.numWords++;
            
                // if the node where we inserted value has 3 pieces of data,
                    //then we split
                if(prev.numWords == 3){
                    //too full...split and rebalance
                    //call split method
                    splitNode(prev);
                }
            }
        }
    } // end of addWord

    /**
     * splitNode
     * 
     * A private method that splits a Node when needed, while preserving the properties 
     * of a 2-3 Tree, by considering every possible scenario. 
     * 
     * @param aNode The node being split
     */
    private void splitNode(Node aNode){
        /*
         * we split the node as long as the node has 3 pieces of data. This strategy helps 
         * make sure that the entire tree is balanced, i.e. we don't just split one node, 
         * rather we continue splitting till every node has a max of two pieces of data.
         * 
         * the parent of the node we split becomes the new node we assess to see if further
         * splitting is required.
         */
        while(aNode.numWords == 3){
            // the node being split has no parent
            if(aNode.parent == null ){ 
                if(isLeaf(aNode)){ 
                    //either a single node in the tree (root but no children)
                    aNode = singleNode(aNode); 
                } else{
                    // or root node (no parent but children)
                    aNode = rootNode(aNode);
                }
            } else{ 
                // leaf or internal - has parents
                if(isLeaf(aNode)){
                    // if a leaf node
                    if(aNode.parent.numWords == 1){
                        // leaf node's parent has 1 data only
                        aNode = parentNodeOneDataLeaf(aNode);
                    } else{
                        // leaf node's parent has 2 data
                        aNode = parentNodeTwoDataLeaf(aNode);
                    }
                } else{
                    // if an internal node
                    if(aNode.parent.numWords == 1){
                        // internal node's parent has 1 data only
                        aNode = parentNodeOneDataInternal(aNode);
                    } else{
                        // internal node's parent has 2 data
                        aNode = parentNodeTwoDataInternal(aNode);
                    }
                }
            }
        }
    } // end of splitNode

    /**
     * isLeaf
     * 
     * A private method to check if a node is a leaf node or not
     * 
     * @param aNode The node being checked
     * @return true if leaf, false otherwise
     */
    private boolean isLeaf(Node aNode){
        // a node is a leaf if  no children
        return aNode.children[0] == null;
    } //end of isLeaf

    /**
     * singleNode
     * 
     * A private method that helps the splitNode method by performing all the 
     * tasks needed when splitting a single Node (i.e no parent, no children)
     * 
     * @param aNode The node being split
     * @return The updated parent of the node that we just split
     */
    private Node singleNode(Node aNode){
        // create the new root, which is the middle value of the node being split
        Node newNode = new Node();
        root = newNode;
        newNode.data[0] = aNode.data[1];
        newNode.numWords++;

        // split the node into two children
        //new child 1 - left child
        Node newChild1 = new Node();
        newChild1.data[0] = aNode.data[0];
        newChild1.numWords++;
        newChild1.parent = newNode;
        newNode.children[0] = newChild1;

        // new child 2 - right child
        Node newChild2 = new Node();
        newChild2.data[0] = aNode.data[2];
        newChild2.numWords++;
        newChild2.parent = newNode;
        newNode.children[1] = newChild2;
        
        return newNode;
    } // end of singleNode

    /**
     * rootNode
     * 
     * A private method that helps the splitNode method by performing all the 
     * tasks needed when splitting a root Node (i.e no parent, but has children)
     * 
     * @param aNode The node being split
     * @return The updated parent of the node that we just split
     */
    private Node rootNode(Node aNode){
        // create the new root, which is the middle value of the root node being split
        Node newRoot = new Node();
        root = newRoot;
        newRoot.data[0] = aNode.data[1];
        newRoot.numWords++;

        /*
         * split the node into two children, and further update the children connection 
         * for those new children because when we split the root node, it had 4 children
         */
        //new child 1 - left child
        Node newChild1 = new Node();
        newChild1.data[0] = aNode.data[0];
        newChild1.numWords++;
        newChild1.parent = newRoot;
        newRoot.children[0] = newChild1;
        // making sure to not break the connection with the rest of the tree
        newChild1.children[0] = aNode.children[0];
        newChild1.children[0].parent = newChild1;
        newChild1.children[1] = aNode.children[1];
        newChild1.children[1].parent = newChild1;
        
        // new child 2 - right child
        Node newChild2 = new Node();
        newChild2.data[0] = aNode.data[2];
        newChild2.numWords++;
        newChild2.parent = newRoot;
        newRoot.children[1] = newChild2;
        // making sure to not break the connection with the rest of the tree
        newChild2.children[0] = aNode.children[2];
        newChild2.children[0].parent = newChild2;
        newChild2.children[1] = aNode.children[3];
        newChild2.children[1].parent = newChild2;
        
        return newRoot;
    } // end of rootNode

    /**
     * parentNodeOneDataLeaf
     * 
     * A private method that helps the splitNode method by performing all the 
     * tasks needed when splitting a leaf node where the parent of the leaf node
     * has one piece of data
     * 
     * @param aNode The node being split
     * @return The updated parent of the node that we just split
     */
    private Node parentNodeOneDataLeaf(Node aNode){
        // the node's parent is gonna end up with one extra word
        aNode.parent.numWords++;

        /*
         * If parent node has one piece of data, then we are either splitting the
         * left child or the right child.
         */
        if(aNode.parent.children[0] == aNode){
            // if left child
            DataPair newPair = new DataPair();
            newPair = aNode.parent.data[0];
            //move data from pos 0 value to pos 1 - ordered insert
            aNode.parent.data[1] = newPair; 
            aNode.parent.data[0] = aNode.data[1];

            // creating room to add the new middle child
            // sp move child 1 to 2 (new right child)
            Node newNode = new Node();
            newNode = aNode.parent.children[1];
            aNode.parent.children[2] = newNode; 

            // splitting remianing values into  two children
            //new child 1 - left child
            Node newChild1 = new Node();
            newChild1.data[0] = aNode.data[0];
            newChild1.numWords++;
            newChild1.parent = aNode.parent;
            aNode.parent.children[0] = newChild1;

            // new child 2 - middle child
            Node newChild2 = new Node();
            newChild2.data[0] = aNode.data[2];
            newChild2.numWords++;
            newChild2.parent = aNode.parent;
            aNode.parent.children[1] = newChild2;

        } else{
            // if right child
            // ordered insert at the end i.e. pos 1
            aNode.parent.data[1] = aNode.data[1];
            
            // splitting remianing values into two children
            // left child remains the same

            //new child 1 - middle child
            Node newChild3 = new Node();
            newChild3.data[0] = aNode.data[0];
            newChild3.numWords++;
            newChild3.parent = aNode.parent;
            aNode.parent.children[1] = newChild3;

            // new child 2 - right child
            Node newChild4 = new Node();
            newChild4.data[0] = aNode.data[2];
            newChild4.numWords++;
            newChild4.parent = aNode.parent;
            aNode.parent.children[2] = newChild4;
        }
        return aNode.parent;
    } // end of parentNodeOneDataLeaf

    /**
     * parentNodeTwoDataLeaf
     * 
     * A private method that helps the splitNode method by performing all the 
     * tasks needed when splitting a leaf node where the parent of the leaf node
     * has two pieces of data
     * 
     * @param aNode The node being split
     * @return The updated parent of the node that we just split
     */
    private Node parentNodeTwoDataLeaf(Node aNode){
        // pieces of data in parent node increases after split
        aNode.parent.numWords++;

        /*
         * If parent node has two pieces of data, then we are either splitting the
         * left child, the middle child, or the right child.
         */
        if(aNode.parent.children[0] == aNode){
            // if left child
            // updating parent node
            // move pos 1 to 2
            DataPair newPair1 = new DataPair();
            newPair1 = aNode.parent.data[1];
            aNode.parent.data[2] = newPair1;

            // move pos 0 to 1
            DataPair newPair2 = new DataPair();
            newPair2 = aNode.parent.data[0];
            aNode.parent.data[1] = newPair2;

            // insert at pos 0
            aNode.parent.data[0] = aNode.data[1];

            //splitting the remaining values in the node in two new children.
            // making room in the front of the children array

            // move child 2 to 3
            Node newNode1 = new Node();
            newNode1 = aNode.parent.children[2];
            aNode.parent.children[3] = newNode1;

            // move child 1 to 2
            Node newNode2 = new Node();
            newNode2 = aNode.parent.children[1];
            aNode.parent.children[2] = newNode2;

            // inserting new child at pos 0
            //new child 1 - left child
            Node newChild1 = new Node();
            newChild1.data[0] = aNode.data[0];
            newChild1.numWords++;
            newChild1.parent = aNode.parent;
            aNode.parent.children[0] = newChild1;

            // inserting new child at pos 1
            // new child 2 - middle child
            Node newChild2 = new Node();
            newChild2.data[0] = aNode.data[2];
            newChild2.numWords++;
            newChild2.parent = aNode.parent;
            aNode.parent.children[1] = newChild2;
            
        } else if(aNode.parent.children[1] == aNode){
            // if middle child

            // updating parent node
            // move data 1 to 2
            DataPair newPair3 = new DataPair();
            newPair3 = aNode.parent.data[1];
            aNode.parent.data[2] = newPair3;

            // insert at pos 1
            aNode.parent.data[1] = aNode.data[1];

            //splitting the remaining values in the node in two new children.
            // move child from pos 2 to 3
            Node newNode3 = new Node();
            newNode3 = aNode.parent.children[2];
            aNode.parent.children[3] = newNode3;

            //new child 1 insert at pos 1 - middle child
            Node newChild3 = new Node();
            newChild3.data[0] = aNode.data[0];
            newChild3.numWords++;
            newChild3.parent = aNode.parent;
            aNode.parent.children[1] = newChild3;

            // new child 2 insert at pos 2 - right child
            Node newChild4 = new Node();
            newChild4.data[0] = aNode.data[2];
            newChild4.numWords++;
            newChild4.parent = aNode.parent;
            aNode.parent.children[2] = newChild4;

        } else {
            // if right child
            // updating parent node
            // insert value at pos 2..pos should be empty already
            aNode.parent.data[2] = aNode.data[1];

            //splitting the remaining values in the node in two new children.

            //new child 1 insert at pos 2 - right child
            Node newChild5 = new Node();
            newChild5.data[0] = aNode.data[0];
            newChild5.numWords++;
            newChild5.parent = aNode.parent;
            aNode.parent.children[2] = newChild5;

            // new child 2 insert at pos 3 - place holder child
            Node newChild6 = new Node();
            newChild6.data[0] = aNode.data[2];
            newChild6.numWords++;
            newChild6.parent = aNode.parent;
            aNode.parent.children[3] = newChild6;
        }
        return aNode.parent;
    } // end of parentNodeTwoDataLeaf

    /**
     * parentNodeOneDataInternal
     * 
     * A private method that helps the splitNode method by performing all the 
     * tasks needed when splitting an internal node where the parent of the internal 
     * node has one piece of data.
     * 
     * @param aNode The node being split
     * @return The updated parent of the node that we just split
     */
    private Node parentNodeOneDataInternal(Node aNode){
        // parent's number of data increases
        aNode.parent.numWords++;

        /*
         * If parent node has one piece of data, then we are either splitting the
         * left child or the right child.
         * 
         * After updating the parent node, we Split the node into two children, and further update 
         * the children connection for those new children because when we split the internal node, we also
         * need to think about the nodes's children. 
         */

        if(aNode.parent.children[0] == aNode){
            //if left child
            DataPair newPair = new DataPair();
            newPair = aNode.parent.data[0];
            //move pos 0 value to pos 1
            aNode.parent.data[1] = newPair; 
            // middle value goes up
            aNode.parent.data[0] = aNode.data[1];

            // move child 1 to 2 making room for new middle child
            Node newNode = new Node();
            newNode = aNode.parent.children[1];
            aNode.parent.children[2] = newNode; 

            //new child 1 insert at pos 0 - left child
            Node newChild1 = new Node();
            newChild1.data[0] = aNode.data[0];
            newChild1.numWords++;
            newChild1.parent = aNode.parent;
            //updating children connection
            newChild1.children[0] = aNode.children[0];
            newChild1.children[0].parent = newChild1;
            newChild1.children[1] = aNode.children[1];
            newChild1.children[1].parent = newChild1;
            aNode.parent.children[0] = newChild1;

            // new child 2 insert at pos 1 - right child
            Node newChild2 = new Node();
            newChild2.data[0] = aNode.data[2];
            newChild2.numWords++;
            newChild2.parent = aNode.parent;
            //updating children connection
            newChild2.children[0] = aNode.children[2];
            newChild2.children[0].parent = newChild2;
            newChild2.children[1] = aNode.children[3];
            newChild2.children[1].parent = newChild2;
            aNode.parent.children[1] = newChild2;

        } else{
            // if right child
            // insert data in empty position
            aNode.parent.data[1] = aNode.data[1];

            //new child 1 insert at pos 0 - left child
            Node newChild3 = new Node();
            newChild3.data[0] = aNode.data[0];
            newChild3.numWords++;
            newChild3.parent = aNode.parent;
            //update children connection
            newChild3.children[0] = aNode.children[0];
            newChild3.children[0].parent = newChild3;
            newChild3.children[1] = aNode.children[1];
            newChild3.children[1].parent = newChild3;
            aNode.parent.children[1] = newChild3;

            // new child 2 insert at pos 1 - middle child
            Node newChild4 = new Node();
            newChild4.data[0] = aNode.data[2];
            newChild4.numWords++;
            newChild4.parent = aNode.parent;
            //update children connection
            newChild4.children[0] = aNode.children[2];
            newChild4.children[0].parent = newChild4;
            newChild4.children[1] = aNode.children[3];
            newChild4.children[1].parent = newChild4;
            aNode.parent.children[2] = newChild4;
          
        }
        return aNode.parent;
    } // end of parentNodeOneDataInternal

    /**
     * parentNodeTwoDataInternal
     * 
     * A private method that helps the splitNode method by performing all the 
     * tasks needed when splitting an internal node where the parent of the internal 
     * node has two pieces of data.
     * 
     * @param aNode The node being split
     * @return The updated parent of the node that we just split
     */
    private Node parentNodeTwoDataInternal(Node aNode){
        // parent's number of data pieces goes up
        aNode.parent.numWords++;

        /*
         * If parent node has two pieces of data, then we are either splitting the
         * left child, the middle child, or the right child.
         * 
         * After updating the parent node, we Split the node into two children, and further update 
         * the children connection for those new children because when we split the internal node, we also
         * need to think about the nodes's children. So we end up using the placeholder child value
         */

        if(aNode.parent.children[0] == aNode){
            // if left child
            // updating parent node
            // move pos 1 to 2
            DataPair newPair1 = new DataPair();
            newPair1 = aNode.parent.data[1];
            aNode.parent.data[2] = newPair1;

            // move pos 0 to 1
            DataPair newPair2 = new DataPair();
            newPair2 = aNode.parent.data[0];
            aNode.parent.data[1] = newPair2;

            // insert at 0
            aNode.parent.data[0] = aNode.data[1];

            //move child at pos 2 to 3
            Node newNode1 = new Node();
            newNode1 = aNode.parent.children[2];
            aNode.parent.children[3] = newNode1;

            // move child at pos 1 to 2
            Node newNode2 = new Node();
            newNode2 = aNode.parent.children[1];
            aNode.parent.children[2] = newNode2;

            //new child 1 insert at pos 0 - left child
            Node newChild1 = new Node();
            newChild1.data[0] = aNode.data[0];
            newChild1.numWords++;
            newChild1.parent = aNode.parent;
            //update children connection
            newChild1.children[0] = aNode.children[0];
            newChild1.children[0].parent = newChild1;
            newChild1.children[1] = aNode.children[1];
            newChild1.children[1].parent = newChild1;
            aNode.parent.children[0] = newChild1;

            // new child 2 insert at pos 1 - middle child
            Node newChild2 = new Node();
            newChild2.data[0] = aNode.data[2];
            newChild2.numWords++;
            newChild2.parent = aNode.parent;
            //update children connection
            newChild2.children[0] = aNode.children[2];
            newChild2.children[0].parent = newChild2;
            newChild2.children[1] = aNode.children[3];
            newChild2.children[1].parent = newChild2;
            aNode.parent.children[1] = newChild2;
            
        } else if(aNode.parent.children[1] == aNode){
            // if middle child

            // updating parent node
            // move pos 1 to 2
            DataPair newPair3 = new DataPair();
            newPair3 = aNode.parent.data[1];
            aNode.parent.data[2] = newPair3;

            // insert at pos 1
            aNode.parent.data[1] = aNode.data[1];

            // move child at pos 2 to 3
            Node newNode3 = new Node();
            newNode3 = aNode.parent.children[2];
            aNode.parent.children[3] = newNode3;

            //new child 1 insert at pos 1 - middle child
            Node newChild3 = new Node();
            newChild3.data[0] = aNode.data[0];
            newChild3.numWords++;
            newChild3.parent = aNode.parent;
            //update children connection
            newChild3.children[0] = aNode.children[0];
            newChild3.children[0].parent = newChild3;
            newChild3.children[1] = aNode.children[1];
            newChild3.children[1].parent = newChild3;
            aNode.parent.children[1] = newChild3;

            // new child 2 insert at pos 2 - right child
            Node newChild4 = new Node();
            newChild4.data[0] = aNode.data[2];
            newChild4.numWords++;
            newChild4.parent = aNode.parent;
            //update children connection
            newChild4.children[0] = aNode.children[2];
            newChild4.children[0].parent = newChild4;
            newChild4.children[1] = aNode.children[3];
            newChild4.children[1].parent = newChild4;
            aNode.parent.children[2] = newChild4;
    
        } else {
            // if right child
            // updating parent node
            // insert at empty pos 2
            aNode.parent.data[2] = aNode.data[1];

            //new child 1 insert at pos 2 - right child
            Node newChild5 = new Node();
            newChild5.data[0] = aNode.data[0];
            newChild5.numWords++;
            newChild5.parent = aNode.parent;
            //update children connection
            newChild5.children[0] = aNode.children[0];
            newChild5.children[0].parent = newChild5;
            newChild5.children[1] = aNode.children[1];
            newChild5.children[1].parent = newChild5;
            aNode.parent.children[2] = newChild5;

            // new child 2insert at pos 3 - placeholder
            Node newChild6 = new Node();
            newChild6.data[0] = aNode.data[2];
            newChild6.numWords++;
            newChild6.parent = aNode.parent;
            //update children connection
            newChild6.children[0] = aNode.children[2];
            newChild6.children[0].parent = newChild6;
            newChild6.children[1] = aNode.children[3];
            newChild6.children[1].parent = newChild6;
            aNode.parent.children[3] = newChild6;   
        }
        return aNode.parent;
    } // end of parentNodeTwoDataInternal

    /**
     * Checks if the tree contains the specified word.
     * @param word The word to check for.
     * @return true if the word is found in the tree, false otherwise.
     */
    @Override
    public boolean containsWord(String word) {
        boolean found = false;
        // proceed if tree is not empty
        if(root != null){
            Node target = findNode(word);
            // if target is null that means we didn't find the word
            if (target != null){
                found = true;
            }
        }
        return found;
    } // end of containsWord

    /**
     * Gets the frequency of a given word in the tree.
     * @param word The word whose frequency is to be retrieved.
     * @return The frequency of the word, or 0 if the word is not found.
     */
    @Override
    public int getFrequency(String word) {
        int wordFreq = 0;
        // proceed only if the tree contains the word
        if(containsWord(word)){

            // find the correct node
            Node targetNode = findNode(word);

            // then find the correct data pair to get the frequency from
            for(int i = 0; i < targetNode.data.length; i++){
                if(targetNode.data[i].word != null){
                    if (targetNode.data[i].word.equalsIgnoreCase(word)){
                        wordFreq = targetNode.data[i].frequency;
                    }
                } 
            }
        }
        return wordFreq;
    } // end of getFrequency

    /**
     * findNode
     * 
     * A private helper method that finds the Node that has the specified word
     * as its data.
     *  
     * @param word the word that the node must have as its data
     * @return returns the Node if found, or null otherwise
     */
    private Node findNode(String word){
        Node target = root;
        boolean found = false;

        // iterate until we find the node, or we reach the end without finding
        while (target != null && !found){
            if(target.data[1].word != null){
                // node has two data values
                if(target.data[0].word.equalsIgnoreCase(word) || target.data[1].word.equalsIgnoreCase(word)){
                    found = true;
                } else{
                    //move curr to left, middle or right
                    target = twoDataNodeHelp(target, word);
                }
            } else{
                // node has one data value
                // normal bst search
                if(target.data[0].word.compareToIgnoreCase(word) == 0){
                    found = true;
                } else {
                      target = oneDataNodeHelp(target, word);
                }           
            }
        }
        return target;
    } // end of findNode

    /**
     * twoDataNodeHelp
     * 
     * This is a private method that helps iterate through the tree in the right
     * order based on the word we are looking for, if we come across a node that
     * has two pieces of data in it and we need to decide if we should move to
     * the left, middle, or right child
     * 
     * @param target The node we have come to that has two pieces of data in it
     * @param word The word that helps dictate which direction we go to
     * @return the node we move to next
     */
    private Node twoDataNodeHelp(Node target, String word){
        Node newCurr = null;

        if(target.data[0].word.compareToIgnoreCase(word) > 0){
            //left
            newCurr = target.children[0];
        } else if(target.data[1].word.compareToIgnoreCase(word) < 0){
            //right
            newCurr = target.children[2];
        } else{
            //middle
            newCurr = target.children[1];
        }
        return newCurr;
    } // end of twoDataNodeHelp

    /**
     * oneDataNodeHelp
     * 
     * This is a private method that helps iterate through the tree in the right
     * order based on the word we are looking for, if we come across a node that
     * has one piece of data in it and we need to decide if we should move to
     * the left or right child.
     * 
     * @param target The node we have come to that has one piece of data in it
     * @param word The word that helps dictate which direction we go to
     * @return the node we move to next
     */
    private Node oneDataNodeHelp(Node target, String word){
        Node newCurr = null;
        if (target.data[0].word.compareToIgnoreCase(word) < 0){
            //right child
            newCurr = target.children[1]; 
        } else{
            //left child
            newCurr = target.children[0];
        }  
        return newCurr;
    } // end of oneDataNodeHelp

    /**
     * Prints the contents of the tree in lexicographic order.
     */
    @Override
    public void print() {
        // tree is not empty
        if (root != null){
            System.out.print("[ ");
            printHelp(root);
            System.out.println("]");
        } else{
            System.out.println("Tree is empty!");
        }
    } // end of print

    /**
     * printHelp
     * 
     * This is a private helper method. This method recursively traverses
     * the entire tree IN-ORDER to print the contents of the tree in 
     * lexicographic order.
     * 
     * @param aNode The node where traversal should begin
     */
    private void printHelp(Node aNode){

        if (aNode == null){
            // empty string and end
            System.out.print("");

        } else if(aNode.numWords == 1){ // node has 1 piece of data
            //left
            printHelp(aNode.children[0]);
            // me
            System.out.print(aNode.data[0].word + "(" + aNode.data[0].frequency + ") ");
            //right
            printHelp(aNode.children[1]);

        } else{
            // node has 2 pieces of data
            //left
            printHelp(aNode.children[0]);
            //data 1
            System.out.print(aNode.data[0].word + "(" + aNode.data[0].frequency + ") "); //data#1
            // middle
            printHelp(aNode.children[1]);
            //data 2
            System.out.print(aNode.data[1].word + "(" + aNode.data[1].frequency + ") "); //data#2
            // right
            printHelp(aNode.children[2]);
        }    
    } // end of printHelp

    /**
     * Calculates the height of the tree as the number of edges on the longest branch.
     * @return The height of the tree.
     */
    @Override
    public int height() {
        int heightVal; 
    
        if (root == null){
            heightVal = 0; //empty tree
        }  else{
            heightVal = heightHelp(root);
        }
        return heightVal;
    } // end of height

    /**
     * heightHelp
     * 
     * This is a private helper method. This method helps calculate the height
     * of a non-empty 2-3 tree recursively.
     * 
     * @param aNode the root of the subtree from where we start measuring height
     * @return the height of the tree
     */
    private int heightHelp(Node aNode){
        if(isLeaf(aNode)){
            return 0;
        } else{ 
            // if it is not a leaf, then we know that there will always be a 
                //left child no matter if 2 pieces of data or one
            // height is the same in all direction so we just go as far as
                //possible in any one direction to get the height
            return heightHelp(aNode.children[0]) + 1;
        }
    } // end of heightHelp

    /**
     * compare
     * 
     * Compares the current tree with another tree, listing unique and common words.
     * If the other tree is not a compatible type, print a message indicating an invalid comparison.
     * 
     * @param otherTree The other tree to compare against.
     */
    @Override
    public void compare(GameTree otherTree) {
        // checking for correct tree type
        if (otherTree instanceof TwoThreeTree){
            // curr tree is not empty
            if (root != null){
                System.out.print("Common Words: [ ");
                commonWords(root, otherTree);
                System.out.println("]");

                System.out.print("Unique Words to Curr Tree: [ ");
                uniqueWords(root, otherTree);
                System.out.println("]");
                
                // get the unique words for the other tree
                otherTreeUniqueWords(otherTree);

            } else{
                System.out.print("Common Words: [  ]");
                System.out.print("Unique Words to Curr Tree: [  ]");
                // get the unique words for the other tree
                otherTreeUniqueWords(otherTree);
            }
        } else{
            System.out.println("The otherTree is not an instance of 2-3 Tree. Incompatible Tree Type!");
        }
    } // end of compare

    /**
     * otherTreeUniqueWords
     * 
     * A private helper method that helps keep code clean, organised,
     * and is used to get the words unique to the other tree.
     * 
     * @param otherTree The other tree for which we are getting the 
     *                  unique words for.
     */
    private void otherTreeUniqueWords(GameTree otherTree){
        // get the other tree's rootr
        Node otherTreeRoot = getRoot(otherTree);
        
        if(otherTreeRoot != null){
            // other tree is not empty
            System.out.print("Unique Words to Other Tree: [ ");
            uniqueWords(otherTreeRoot, this);
            System.out.println("]");
        } else{
            System.out.print("Unique Words to Other Tree: [  ]");
        }
    } // end of otherTreeUniqueWords
    
    /**
     * commonWords
     * 
     * A private helper method that helps find all the common words
     * between two 2-3 trees by implementing recursive
     * in-order traversal to check every node of the tree against the 
     * other tree.
     * 
     * @param aNode The node where traversal begins (root of the subtree)
     * @param otherTree the other tree the current tree is being
     *                  compared with.
     */
    private void commonWords(Node aNode, GameTree otherTree){
        if(aNode == null){
            System.out.print("");
        } else if(aNode.numWords == 1){
            //only left and right child
            // left
            commonWords(aNode.children[0], otherTree);
            if(otherTree.containsWord(aNode.data[0].word)){
                System.out.print(aNode.data[0].word + " ");
            }
            //right
            commonWords(aNode.children[1], otherTree);

        } else{
            // left, middle, and right child
            //left
            commonWords(aNode.children[0], otherTree);
            if(otherTree.containsWord(aNode.data[0].word)){
                System.out.print(aNode.data[0].word + " ");
            }
            //middle
            commonWords(aNode.children[1], otherTree);
            if(otherTree.containsWord(aNode.data[1].word)){
                System.out.print(aNode.data[1].word + " ");
            }
            //right
            commonWords(aNode.children[2], otherTree);
        }
    } // end of common words
       
    /**
     * uniqueWords
     * 
     * A private helper method that helps find all the unique words in a
     * tree when compared with another tree. Acheived by implementing recursive
     * in-order traversal to compare every node against the other tree.
     * 
     * @param aNode The node where traversal begins (root of the subtree)
     * @param otherTree the other tree the current tree is being
     *                  compared with.
     */
    private void uniqueWords(Node aNode, GameTree otherTree){
        if(aNode == null){
            System.out.print("");

        } else if(aNode.numWords == 1){
            //only left and right child
            //left
            uniqueWords(aNode.children[0], otherTree);
            if(!otherTree.containsWord(aNode.data[0].word)){
                System.out.print(aNode.data[0].word + " ");
            }
            //right
            uniqueWords(aNode.children[1], otherTree);

        } else{
            // left, middle, and right child
            //left
            uniqueWords(aNode.children[0], otherTree);
            if(!otherTree.containsWord(aNode.data[0].word)){
                System.out.print(aNode.data[0].word + " ");
            }
            //middle
            uniqueWords(aNode.children[1], otherTree);
            if(!otherTree.containsWord(aNode.data[1].word)){
                System.out.print(aNode.data[1].word + " ");
            }
            //right
            uniqueWords(aNode.children[2], otherTree);
        }
    } // end of uniqueWords

    /**
     * getRoot
     * 
     * A private method used to cast a GameTree to a 2-3 tree, and then
     * get its root node. Since used internally only, we know the tree being 
     * passed will be an instance of 2-3.
     * 
     * @param otherTree The tree we want to get the root for
     * @return the root of the other tree
     */
    private Node getRoot(GameTree otherTree){
        TwoThreeTree aTree = (TwoThreeTree)otherTree;
        return aTree.root;
    } // end of getRoot

    /**
     * printTree
     * 
     * Prints a visual representation of the tree structure.
     * Used to display the tree with indentation to show hierarchy.
     */
    @Override
    public void printTree() {
        // tree not empty
        if (root != null){
            // i is a variable that keeps track of the number of times
                //to indent to get the right hierarchy.
            int i = 0;
            printTreeHelp(root,i);
        } else{
            System.out.println("The tree is empty!");
        }
    } // end of printTree

    /**
     * printTreeHelp
     * 
     * A private helper method that implements pre-order traversal using recursion.
     * The entire tree is traversed and the contents are printed to get a visual
     * representation of the heirarchy.
     * 
     * @param aNode The node where traversal begins
     * @param i keeps track of the number of times to indent to get the right hierarchy.
     */
    private void printTreeHelp(Node aNode, int i){
        String space = "    "; // used for indentation
        
        if (isLeaf(aNode)){
            // print the lead node info and then we end
            if(aNode.data[1].word == null){
                System.out.println(space.repeat(i) + "[" + aNode.data[0].word + "(" + aNode.data[0].frequency + ")]"); // one piece of data
            } else{
                // two pieces of data
                System.out.println(space.repeat(i) + "[" + aNode.data[0].word + "(" + aNode.data[0].frequency + "), " + aNode.data[1].word + "(" + aNode.data[1].frequency + ")]");
            }
        } else{

            if(aNode.data[1].word == null){
                // one piece of data
                System.out.println(space.repeat(i) + "[" + aNode.data[0].word + "(" + aNode.data[0].frequency + ")]");
            } else{
                // two piece of data
                System.out.println(space.repeat(i) + "[" + aNode.data[0].word + "(" + aNode.data[0].frequency + "), " + aNode.data[1].word + "(" + aNode.data[1].frequency + ")]");
            }

            if(aNode.children[0] != null){
                // left
                printTreeHelp(aNode.children[0], i+1);
            } 

            if(aNode.children[1] != null){
                // middle
                printTreeHelp(aNode.children[1], i+1);
            }

            if(aNode.children[2] != null){
                // right
                // only used in case of node with 3 children
                printTreeHelp(aNode.children[2], i+1);
            }
        }
    } // end of printTreeHelp

    /**
     * doubleFrequency
     * 
     * Doubles the frequency of a word if the word exists in the tree.
     * 
     * @param word The word for which we double the frequency
     */
    @Override
    public void doubleFrequency(String word) {
        // check if the word exists
        if(containsWord(word)){
            // find the node, and then within the node, find the correct data piece
                // and then double the frequency
            Node target = findNode(word);
            for(int i = 0; i < target.data.length; i++){
                if(target.data[i].word != null){
                    if (target.data[i].word.equalsIgnoreCase(word)){
                        target.data[i].frequency = (2*target.data[i].frequency);
                    }
                }
            }
        }
    } // end of doubleFrequency

    /**
     * swapFrequencies
     * 
     * Swaps the frequencies of two words, if both the words exist in the tree.
     * 
     * @param word1 one of the two words for which we swap the frequency
     * @param word2 one of the two words for which we swap the frequency
     */
    @Override
    public void swapFrequencies(String word1, String word2) {
        // check both words exist
        if (containsWord(word1) && containsWord(word2)){
            Node target1 = findNode(word1);
            Node target2 = findNode(word2);
            // needed for swapping purposes
            int temp1 = -1;
            int temp2 = -1;

            // get the frequency of the first word
            for(int i = 0; i < target1.data.length; i++){
                if(target1.data[i].word != null){
                    if (target1.data[i].word.equalsIgnoreCase(word1)){
                        temp1 = target1.data[i].frequency; 
                    }
                }
            }

            // get the frequency of second word, and then set the second word's new 
                //frequency
            for(int j = 0; j < target2.data.length; j++){
                if(target2.data[j].word != null){
                    if (target2.data[j].word.equalsIgnoreCase(word2)){
                        temp2 = target2.data[j].frequency; 
                        target2.data[j].frequency = temp1; 
                    }
                }
            }

            // update the first word's new frequency
            for(int k = 0; k < target1.data.length; k++){
                if(target1.data[k].word != null){
                    if (target1.data[k].word.equalsIgnoreCase(word1)){
                        target1.data[k].frequency = temp2;
                    }
                }
            }
        }
    } // end of swapFrequencies

    // private node class for TwoThreeTree class
    // represents a node in the tree
    private class Node{
        public DataPair[] data; // array of DataPairs..in order to hold more than one data piece
        public Node[] children; // array of children.. the node's children
        public int numWords; // number of data pieces i.e. words present in the node
        public Node parent; // the node's parent

        //constructor
        public Node(){
            // only 2 pieces of data allowed, and third acts as a placeHolder
            this.data = new DataPair[3]; 
            // can only have upto 3 children, 4th acts as a placeholder
            this.children = new Node[4]; 
            
            //initialising both arrays
            for(int i = 0; i < this.data.length; i++){
                this.data[i] = new DataPair();
            }
            for(int i = 0; i < this.children.length; i++){
                this.children[i] = null;
            }
            this.numWords = 0;
            this.parent = null;
        }
    } // end of Node Class

    // A private class that represents a Data pair
    // A node in a 2-3 tree holds upto 2 data pairs i.e. data pieces
    private class DataPair{
        // a data pair has a word, and its frequency
        public String word;
        public int frequency;

        // constructor
        public DataPair(){
            this.word = null;
            this.frequency = 0;
        }
    } // end of DataPair Class

} // end of TwoThreeTree class