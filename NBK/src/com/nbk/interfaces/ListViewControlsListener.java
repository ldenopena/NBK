package com.nbk.interfaces;

import com.nbk.models.Item;

public interface ListViewControlsListener {

	public static enum Action{
		DELETE;
	}
	
	public void onAction(Action action, Item item);
	
}
