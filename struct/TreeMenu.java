package struct;

import java.util.List;

public class TreeMenu {
	private long id;
	private String text;//text of the node
	private boolean Monitored;
	private String iconCls;

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
	
	public String getIcon(){
		return iconCls;
	}
	
	public void setIcon(String iconCls)
	{
		this.iconCls = iconCls;
	}

}
