package ru.sidey383.essential.dev.item.frame.map;

public enum FrameClickAction {
	
	RIGHT_CLICK_BLOCK(true), RIGHT_CLICK_AIR(true), RIGHT_CLICK_FRAME(true), LEFT_CLICK_BLOCK(false), LEFT_CLICK_AIR(false), PHYSICAL(false), BLOCK_BREAK(false), FRAME_BREAK(false);
	
	boolean isRight;
	
	FrameClickAction(boolean isRight) {
		this.isRight = isRight;
	}
	
	public boolean isRight() {
		return isRight;
	}
	
	public boolean isLeft() {
		return !isRight;
	}
}
