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

package edu.eside.flingbox;

import android.os.Bundle;

public class Preferences {
	public static float[] backgroundColor = new float[] {0.563f, 0.707f, 0.778f, 1.0f };

	public static boolean useAcelerometerBasedGravity = true;
	public static float gravity;
	
	private static final String KEY_DENSITY = "PREFERENCES_DENSITY";
	public static float defaultDensity = 1.0f;
	private static final String KEY_RESTIT_COEF = "PREFERENCES_RESTIT_COEF";
	public static float defaultRestitutionCoeficient = 0.5f;
	
	private static final String KEY_DYNAMIC_FRICTION = "PREFERENCES_DYNAMIC_FRICTION";
	public static float defaultDynamicFrictionCoeficient = 0.35f;
	public static float defaultStaticFrictionCoeficient = 0.5f;
	
	/** Haptic feedback will be performed by Preferences */
	public static long hapticFeedbackTime = 50;
	public static boolean doHapticFeedback = true;
	
	/**
	 * Preferences can't be created. It's static
	 */
	private Preferences() { }
	
	public void onSavePreferences() {
		
	}
	
	public void onLoadPreferences(Bundle savedPreferences) {
		defaultDensity = savedPreferences.getFloat(KEY_DENSITY);
		defaultRestitutionCoeficient = savedPreferences.getFloat(KEY_RESTIT_COEF);
		
		defaultDynamicFrictionCoeficient = savedPreferences.getFloat(KEY_DYNAMIC_FRICTION);
	}
	
}
