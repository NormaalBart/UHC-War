package me.bartvv.uhcwar.interfaces;

import me.bartvv.uhcwar.manager.User;

public interface IPlaceHolder {
	
	public String getPlaceholder();
	
	public String replacePlaceHolder(User user, String message);

}
