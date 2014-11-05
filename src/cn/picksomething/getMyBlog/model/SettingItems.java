package cn.picksomething.getMyBlog.model;

public class SettingItems {

	private String itemName;
	private String itemURL;

	public SettingItems() {
		// TODO Auto-generated constructor stub
		super();
	}

	public SettingItems(String itemName, String itemURL) {
		super();
		this.itemName = itemName;
		this.itemURL = itemURL;
	}

	public void setSettingItemsName(String itemName) {
		this.itemName = itemName;
	}

	public String getSettingItemsName() {
		return itemName;
	}

	public void setSettingItemsURL(String URL) {
		this.itemURL = URL;
	}

	public String getSettingItemsURL() {
		return itemURL;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "SettingItems[itemName=" + itemName + "itemURL=" + itemURL+ "]";
	}

}
