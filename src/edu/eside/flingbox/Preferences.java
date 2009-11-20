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

public class Preferences {
	public static float[] backgroundColor = new float[] {0.4f, 0.4f, 0.8f, 1.0f };
	
	public static boolean useAcelerometerBasedGravity = true;
	public static float gravity;
	
	public static float defaultDensity = 1.0f;
	public static float defaultRestitutionCoeficient = 0.67f;
	
	public static long hapticFeedbackTime = 50;
	public static boolean doHapticFeedback = true;
	
	/**
	 * Preferences can't be created. It's static
	 */
	private Preferences() { }
	
	public void savePreferences() {
		
	}
	
	public void loadPreferences() {
		
	}
	
}
