/*
 * polymap.org
 * Copyright 2009, Polymap GmbH, and individual contributors as indicated
 * by the @authors tag.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */

package openlayers_rap_simple_example;

import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.polymap.rap.widget.openlayers.*;
import org.polymap.rap.widget.openlayers.layers.*;
import org.polymap.rap.widget.openlayers.base_types.Bounds;
import org.polymap.rap.widget.openlayers.base_types.Size;
import org.polymap.rap.widget.openlayers.controls.*;


/**
 * Simple Example on how to use the OpenLayers RAP Widget 
 * 
 *  @author Marcus -LiGi- B&uuml;schleb < mail:	ligi (at) polymap (dot) de >
 *
*/

public class Application implements IEntryPoint {

	public int createUI() {

		Display display = new Display();
		Shell shell = new Shell( display, SWT.SHELL_TRIM );
		shell.setLayout( new FillLayout() );
		shell.setText( "OpenLayers Simple Example" );
		OpenLayers  map = new OpenLayers( shell, SWT.NONE );

		// create and add a WMS layer
		WMSLayer wms_layer=new WMSLayer(map,"polymap", "http://www.polymap.de/geoserver/wms?", "states");
		map.addLayer(wms_layer);
		
		// set Zoom and Center
		map.zoomTo(3);
		map.setCenter(-100.0, 40.0);
		
		// add some controls
		map.addControl(new LayerSwitcherControl(map));
		map.addControl(new MouseDefaultsControl(map));
		map.addControl(new KeyboardDefaultsControl(map));
		map.addControl(new PanZoomBarControl(map));

		VectorLayer vl=new VectorLayer(map,"edit layer");
		map.addLayer(vl);
	
		map.addControl(new EditingToolbarControl(map,vl));
		
		Bounds bounds = new Bounds(map,-180, -88.759, 180, 88.759);
		Size size = new Size(map,580, 288);
		ImageLayer image_layer = new ImageLayer(map,"image layer","http://earthtrends.wri.org/images/maps/4_m_citylights_lg.gif",bounds,size);
		map.addLayer(image_layer);

		
		shell.setSize( 500, 500 );

		shell.open();
		while( !shell.isDisposed()) {
			if( !display.readAndDispatch() ) 
				display.sleep();
	      	}		
		return 0;
	}
}
