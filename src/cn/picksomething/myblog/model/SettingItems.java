package cn.picksomething.myblog.model;

public class SettingItems {

	private String itemName;
	private String itemURL;

	public SettingItems() {
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
		return "SettingItems[itemName=" + itemName + "itemURL=" + itemURL+ "]";
	}

}
