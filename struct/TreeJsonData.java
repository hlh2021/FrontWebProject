package struct;

import java.util.List;

public class TreeJsonData {
	private long id;
	private String text;//the text of the node
	private boolean Monitored;
	
	
	private List children;//child array
	private String state = "closed";
	private String iconCls;
	
	public TreeJsonData()
	{
		
	}
	
	public List getChildren() {
		return children;
	}
	public void setChildren(List children) {
		this.children = children;
	}
	public long getId() {
		return id;
	}
	public void setId(long l) {
		this.id = l;
	}
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public boolean getMonitored(){
		return Monitored;
	}
	
	public void setMonitored(boolean Monitored)
	{
		this.Monitored = Monitored;
	}
	
	public String getstate() {
		return state;
	}

	public void setstate(String state) {
		this.state = state;
	}
	
	public String getIcon(){
		return iconCls;
	}
	
	public void setIcon(String iconCls)
	{
		this.iconCls = iconCls;
	}
}
