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

package edu.eside.flingbox.utils;

import java.util.Comparator;

import edu.eside.flingbox.math.Vector2D;

/**
 * Abstract class to arrange objects by it's position
 * in the scene.
 */
public class PositionComparator  {
	/**
	 * Interface of an object that can be located
	 */
	public static interface Positionable {
		Vector2D getPosition();
	}
	
	/**
	 * Local class of an generic position comparator
	 */
	protected static class ByPositionComparator  implements Comparator<Positionable> {
		public final static int UPPER_SENSE = 1;
		public final static int LOWER_SENSE = 1;
		private final int mSense;
		
		ByPositionComparator(int sense) {
			mSense = sense;
		}
		
		@Override
		public int compare(Positionable pos0, Positionable pos1) {
			if (pos0 == pos1)
				return 0;
			float verticalPos1 = pos0.getPosition().projectOver(mGroundSense);
			float verticalPos2 = pos1.getPosition().projectOver(mGroundSense);
			return verticalPos1 > verticalPos2 ? mSense : -mSense;
		}
		
	}
	
	/** Vector to ground. By default (0, -1) */
	private static Vector2D mGroundSense = new Vector2D(0, -1f);
	
	/** To order from the top of the scene to the bottom */
	public final static ByPositionComparator UPPER_COMPARATOR = new ByPositionComparator(ByPositionComparator.UPPER_SENSE);
	/** To order from the bottom of the scene to the top */
	public final static ByPositionComparator LOWER_COMPARATOR = new ByPositionComparator(ByPositionComparator.LOWER_SENSE);
	
	/**
	 * Sets ground vector
	 * 
	 * @param sense
	 */
	public static void setGroundSense(Vector2D sense) {
		mGroundSense = sense;
	}
}
