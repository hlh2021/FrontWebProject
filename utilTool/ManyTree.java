package utilTool;

import struct.ManyTreeNode;
import java.util.ArrayList;

public class ManyTree {
	
	/**
	 * @param A list of String[3]: commentID, author, parentID
	 * @return the root of the manyTree
	 */
	public ManyTreeNode creatTree(ArrayList<String[]> commentList)
	{
		if(commentList==null || commentList.size()<0)
			return null;
		
		ManyTreeNode root = new ManyTreeNode("ROOT", "ROOT", 0);
		
		for(String[] comment : commentList)
		{
			ManyTreeNode currentNode = new ManyTreeNode(comment[0], comment[1], 1);
			//if the parentID is null, add current element to the root's child list
			if(comment[2]==null || comment[2].length()==0)
				root.getChildList().add(currentNode);
			else
			{
				ManyTreeNode parentNode = findParentNode(root, comment[2]);
				if(parentNode != null)
				{
					int parentDepth = parentNode.getDepth();
					currentNode.setDepth(parentDepth+1);
					parentNode.getChildList().add(currentNode);
				}
				 
			}
		}
		
		return root;
	}
	
	//find the parent node in the manyTree according to the parentID
	private ManyTreeNode findParentNode(ManyTreeNode root, String parentID)
	{
		ManyTreeNode parent = null;
		
		for(ManyTreeNode item : root.getChildList())
		{
			if(item.getCommentID().equals(parentID))
			{
				parent = item;
				break;
			}
			else
			{
				if(item.getChildList() != null && item.getChildList().size() > 0) 
				{
					parent = findParentNode(item, parentID);
					if(parent != null)
						break;
				}
					
			}
		}
		
		return parent;
	}
	
	//depth first traversal, param[2]: size, depth
	public int[] iteratorTree(ManyTreeNode root, int[] param) 
	{
		if(root == null)
			return param;
		
		for (ManyTreeNode index : root.getChildList()) 
		{
			//increase the size
			param[0] = param[0] + 1;			
//			System.out.println("depth: "+index.getDepth()+", id: "+index.getCommentID()+", author: "+index.getAuthor());
			//increase the depth
			if(index.getDepth() > param[1])
				param[1] = index.getDepth();
			
			if (index.getChildList() != null && index.getChildList().size() > 0 ) 
				param = iteratorTree(index, param);
		}
		
		return param;		
	}

}
