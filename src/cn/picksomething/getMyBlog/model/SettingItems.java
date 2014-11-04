package cn.picksomething.getMyBlog.model;

public class SettingItems {
	
	private String itemName;
	
	public SettingItems() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	public SettingItems(String itemName){
		super();
		this.itemName = itemName;
	}
	
	public void setSettingItemsName(String itemName){
		this.itemName = itemName;
	}
	
	public String getSettingItemsName(){
		return itemName;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "SettingItems[itemName=" + itemName + "]";
	}

}
