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

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupWindow;

public class ObjectSettingsActivity extends ListActivity {

	/** 
     * Called when the activity is first created. 
     */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	/*
    	View settingsView =(View)findViewById(R.layout.object_settings);
    	
        PopupWindow window = new PopupWindow(settingsView, 10, 10);   
        window.showAsDropDown(settingsView);
    	 */
    	//this.setContentView((View) findViewById(R.layout.object_settings));
    }

}