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
 * $Id: $
 *
 * @author 	Marcus -LiGi- Bueschleb
 * 	mail to 		ligi (at) polymap (dot) de
 *                  
 * @version $Revision: $
 */

package openlayers_rap_simple_example;

import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.polymap.rap.widget.openlayers.OpenLayers;

/**
 * This class controls all aspects of the application's execution
 * and is contributed through the plugin.xml.
 */
public class Application implements IEntryPoint {

	public int createUI() {

		Display display = new Display();
		Shell shell = new Shell( display, SWT.SHELL_TRIM );
		shell.setLayout( new FillLayout() );
		shell.setText( "OpenLayers Simple Example" );
		OpenLayers  map = new OpenLayers( shell, SWT.NONE );
		map.addWMS("polymap", "polymap", "http://www.polymap.de/geoserver/wms?", "states");
		shell.setSize( 500, 500 );

		shell.open();
		while( !shell.isDisposed()) {
			if( !display.readAndDispatch() ) 
				display.sleep();
	      	}		
		return 0;
	}
}
