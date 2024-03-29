/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

/*
 * Demo of decal that can be moved around in space
 * 
 * 
 */

package com.badlogic.gdx.tests;

import java.util.HashMap;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;

public class DecalTransform extends InputAdapter implements ApplicationListener {
	private static final int DECAL_COUNT = 5;
	Texture image;
	Decal sprites[] = new Decal[DECAL_COUNT];
	DecalBatch batch;
	Camera cam;
	HashMap<Integer, Action> keyActions = new HashMap<Integer, Action>();
	int key = -1;
	float timePassed = 0;

	@Override
	public void create() {
		image = new Texture(Gdx.files.internal("data/badges/128/badge0.png"));
		image.setFilter(Texture.TextureFilter.Linear,
				Texture.TextureFilter.Linear);
		image.setWrap(Texture.TextureWrap.ClampToEdge,
				Texture.TextureWrap.ClampToEdge);

		float w = 100;// Gdx.graphics.getWidth()/2;
		float h = 80;// Gdx.graphics.getHeight()/2;

		for (int i = 0; i < DECAL_COUNT; i++) {
			sprites[i] = Decal
					.newDecal(20, 20, new TextureRegion(image), false);
			sprites[i].setX(20 * i);
		}

		batch = new DecalBatch();
		Gdx.gl.glClearColor(1, 1, 0, 1);

		float move = 100f;
		float scale = 1;
		float rotate = 50;
		keyActions.put(Input.Keys.W, new TransY(move));
		keyActions.put(Input.Keys.S, new TransY(-move));
		keyActions.put(Input.Keys.A, new TransX(-move));
		keyActions.put(Input.Keys.D, new TransX(move));
		keyActions.put(Input.Keys.Q, new Scale(scale));
		keyActions.put(Input.Keys.E, new Scale(-scale));
		keyActions.put(Input.Keys.J, new RotateZ(rotate));
		keyActions.put(Input.Keys.U, new RotateY(rotate));
		keyActions.put(Input.Keys.K, new RotateX(rotate));
		keyActions.put(Input.Keys.Z, new TransZ(-move));
		keyActions.put(Input.Keys.X, new TransZ(move));
		Gdx.input.setInputProcessor(this);
		Gdx.gl.glDisable(GL10.GL_CULL_FACE);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		Gdx.gl10.glLoadIdentity();
		Gdx.gl10.glScalef(0.02f, 0.02f, 1);

		int n = 0;

		float elapsed = Gdx.graphics.getDeltaTime();
		float translate = timePassed > 0.5 ? (float) Math.random() - timePassed
				/ 2 : (float) Math.random() / 2 + timePassed / 2;
		translate *= (-0.5 + Math.random());

		cam.update();
		cam.apply(Gdx.gl10);

		Action a = keyActions.get(key);

		for (int i = 0; i < DECAL_COUNT; i++) {
			batch.add(sprites[n]);
			if (a != null) {
				a.perform(sprites[n]);
			}
		}

		batch.flush();

		timePassed += elapsed;
		if (timePassed > 1.0f) {
			timePassed = 0;
		}

	}

	@Override
	public boolean keyDown(int keycode) {
		key = keycode;
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		key = -1;
		return true;
	}

	@Override
	public void resize(int width, int height) {
		cam = new PerspectiveCamera(45, width, height);
		// cam = new OrthographicCamera(width, height);
		cam.near = 0.1f;
		cam.far = 1000f;
		cam.position.set(0, 0, 100);
		cam.direction.set(0, 0, -1);
	}

	@Override
	public void resume() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void dispose() {
	}

	private abstract class Action {
		float dir;

		protected Action(float dir) {
			this.dir = dir;
		}

		public abstract void perform(Decal d);
	}

	private class RotateZ extends Action {
		public RotateZ(float dir) {
			super(dir);
		}

		@Override
		public void perform(Decal d) {
			d.rotateZ(Gdx.graphics.getDeltaTime() * dir);
		}
	}

	private class RotateY extends Action {
		public RotateY(float dir) {
			super(dir);
		}

		@Override
		public void perform(Decal d) {
			d.rotateY(Gdx.graphics.getDeltaTime() * dir);
		}
	}

	private class RotateX extends Action {
		public RotateX(float dir) {
			super(dir);
		}

		@Override
		public void perform(Decal d) {
			d.rotateX(Gdx.graphics.getDeltaTime() * dir);
		}
	}

	private class TransX extends Action {
		public TransX(float dir) {
			super(dir);
		}

		@Override
		public void perform(Decal d) {
			d.translateX(Gdx.graphics.getDeltaTime() * dir);
		}
	}

	private class TransY extends Action {
		public TransY(float dir) {
			super(dir);
		}

		@Override
		public void perform(Decal d) {
			d.translateY(Gdx.graphics.getDeltaTime() * dir);
		}
	}

	private class TransZ extends Action {
		public TransZ(float dir) {
			super(dir);
		}

		@Override
		public void perform(Decal d) {
			d.translateZ(Gdx.graphics.getDeltaTime() * dir);
		}
	}

	private class Scale extends Action {
		public Scale(float dir) {
			super(dir);
		}

		@Override
		public void perform(Decal d) {
			d.setScale(Gdx.graphics.getDeltaTime() * dir + d.getScaleX(),
					Gdx.graphics.getDeltaTime() * dir + d.getScaleY());
		}
	}
}