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

package edu.eside.flingbox.physics.gravity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import edu.eside.flingbox.math.Vector2D;

/**
 * Gravity Source defines a gravity vector.
 *
 */
public class GravitySource extends Vector2D implements SensorEventListener {

	public static GravitySource getAccelerometerBasedGravity(Context c) {
		// Get Manager from context
		SensorManager sensorManager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
		// Request Accelerometer
		Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		GravitySource gravity = new GravitySource();
		// Set accelerometer event callback
		sensorManager.registerListener(gravity, accelSensor, SensorManager.SENSOR_DELAY_UI);
		
		return gravity;
	}
	
	public static GravitySource getStaticGravity(Vector2D v) {
		return new GravitySource(v);
	}
	
	public static GravitySource getStaticGravity(float i, float j) {
		return new GravitySource(i, j);
	}
	
	private GravitySource() {
		super();
	}
	
	private GravitySource(Vector2D v) {
		super(v);
	}
	
	private GravitySource(float i, float j) {
		super(i, j);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		this.i = -event.values[0] * 10;
		this.j = -event.values[1] * 10;
		
	}
}
