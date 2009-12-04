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
	
	public static final float GRAVITY_SUN = 275.0f;
	
	public static final float GRAVITY_MERCURY = 3.7f;
	public static final float GRAVITY_VENUS = 8.87f;
	public static final float GRAVITY_EARTH = 9.80665f;
	public static final float GRAVITY_MARS = 3.71f;
	public static final float GRAVITY_JUPITER = 23.12f;
	public static final float GRAVITY_SATURN = 8.96f;
	public static final float GRAVITY_NEPTUNE = 11.0f;
	public static final float GRAVITY_PLUTO = 0.6f;
	
	public static final float GRAVITY_MOON = 1.6f;
	public static final float GRAVITY_DEATH_STAR = 3.5303614E-7f;
	

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
		super(v.mul(64f));
	}
	
	private GravitySource(float i, float j) {
		super(i * 64f, j * 64f);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		this.i = -event.values[SensorManager.DATA_X] * 64f;
		this.j = -event.values[SensorManager.DATA_Y] * 64f;
		
	}
}
