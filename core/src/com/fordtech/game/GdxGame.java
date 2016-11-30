package com.fordtech.game;

import com.badlogic.gdx.Game;
import com.fordtech.game.screens.World;

public class GdxGame extends Game {

	@Override
	public void create() {
		setScreen(new World(this));
	}
}
