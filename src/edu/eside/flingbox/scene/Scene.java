/*
 *  Flingbox - An OpenSource physics sandbox for Google's Android
 *  Copyright (C) 2009  Jon Ander Peñalba & Endika Gutiérrez
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.eside.flingbox.scene;

import java.io.File;
import java.io.IOException;

import edu.eside.flingbox.input.SceneGestureDetector.OnInputListener;

import android.content.Context;
import android.os.Environment;
import android.view.MotionEvent;


public class Scene extends DrawableScene implements OnInputListener {

	public Scene(Context c) {
		super(c);
	}

	public boolean onFling(MotionEvent onDownEv, MotionEvent ev, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return super.onDown(e);
	}
	
	public boolean onSaveScene() {
		File savedFile = new File(Environment.getExternalStorageDirectory() 
				+ "flingbox/saved.xml");
		try{
			savedFile.createNewFile(); 
		} catch (IOException ex) {
				
		}
		
		return false;
	}
	
}
