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

import java.util.HashMap;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.polymap.rap.widget.openlayers.*;
import org.polymap.rap.widget.openlayers.layers.*;
import org.polymap.rap.widget.openlayers.marker.BoxMarker;
import org.polymap.rap.widget.openlayers.marker.IconMarker;
import org.polymap.rap.widget.openlayers.base.OpenLayersEventListener;
import org.polymap.rap.widget.openlayers.base_types.Bounds;
import org.polymap.rap.widget.openlayers.base_types.Icon;
import org.polymap.rap.widget.openlayers.base_types.LonLat;
import org.polymap.rap.widget.openlayers.base_types.Pixel;
import org.polymap.rap.widget.openlayers.base_types.Size;
import org.polymap.rap.widget.openlayers.controls.*;
import org.polymap.rap.widget.openlayers.features.VectorFeature;


/**
 * Simple Example on how to use the OpenLayers RAP Widget 
 * 
 *  @author Marcus -LiGi- B&uuml;schleb < mail:	ligi (at) polymap (dot) de >
 *
*/

public class Application implements IEntryPoint,OpenLayersEventListener {


	private OpenLayers map;
	private VectorLayer edit_layer;
	private EditingToolbarControl edit_toolbar;
	
	public void process_event(String event_name,HashMap<String,String> payload) {
		if (event_name.equals("changebaselayer"))
			System.out.println("client changed baselayer to '" + payload.get("layername") +"' " + payload.get("property"));
		else if (event_name.equals("changelayer"))
			{
			System.out.println("client changed layer '" + payload.get("layername") +"' " + payload.get("property") +"' " + payload.get("visibility"));
			if (payload.get("property").equals("visibility")) {
					Boolean visible=payload.get("visibility").equals("true");
					if (payload.get("layername").equals(edit_layer.getName())  )  {
						if (visible)	{
							// adding edit control for the vector layer created above
							edit_toolbar=new EditingToolbarControl(edit_layer);
							map.addControl(edit_toolbar);
							}
						else	{ 
							edit_toolbar.deactivate();
							map.removeControl(edit_toolbar);
						}
					}
				}
			
			}
		else 
			System.out.println("unknown event " + event_name);
	}
	
	public int createUI() {

		// prepare the shell
		Display display = new Display();
		Shell shell = new Shell( display, SWT.SHELL_TRIM );
		shell.setLayout( new FillLayout() );
		shell.setText( "OpenLayers Simple Example" );
		
		// create the OpenLayers widget
		map = new OpenLayers( shell, SWT.NONE );

		HashMap <String, String > payload_map=new HashMap<String, String>();
		payload_map.put("layername", "event.layer.name");
		
		
		map.events.register(this,"changebaselayer",payload_map);
		
		payload_map.put("property", "event.property");
		payload_map.put("visibility", "event.layer.visibility");
		
		map.events.register(this,"changelayer",payload_map);
		
		// create and add a WMS layer
		WMSLayer wms_layer=new WMSLayer("polymap WMS", "http://www.polymap.de/geoserver/wms?", "states");
		map.addLayer(wms_layer);
		
		// set Zoom and Center
		map.zoomTo(3);
		map.setCenter(-100.0, 40.0);
		
		// add some controls
		map.addControl(new LayerSwitcherControl());
		map.addControl(new MouseDefaultsControl());
		map.addControl(new KeyboardDefaultsControl());
		map.addControl(new PanZoomBarControl());

		// add vector layer to have a layer the user can edit
		edit_layer=new VectorLayer("edit layer");
		map.addLayer(edit_layer);
		edit_layer.setVisibility(false);
		
		// add vector layer with some boxes to demonstrate the modify feature feature
		VectorLayer vl2=new VectorLayer("selectable boxes");
		map.addLayer(vl2);
	
		VectorFeature vector_feature=new VectorFeature(new Bounds(-100,40,-80,60));
		vl2.addFeatures(vector_feature);
		vector_feature=new VectorFeature(new Bounds(-90,70,-60,80));
		vl2.addFeatures(vector_feature);
		
		vl2.setVisibility(false);
		
		// setting up the Modify Feature Control
		ModifyFeatureControl mfc=new ModifyFeatureControl(vl2);
		
		mfc.addMode(ModifyFeatureControl.DRAG);
		mfc.addMode(ModifyFeatureControl.RESHAPE);
		mfc.addMode(ModifyFeatureControl.ROTATE);
		
		map.addControl(mfc);
		mfc.activate();
		
		// showing box_layer
		BoxMarker bm =new BoxMarker(new Bounds(-120, 23 , -100,42));
		BoxLayer bl =new BoxLayer("box layer");
		bl.addMarker(bm);
		map.addLayer(bl);
		bl.setVisibility(false);
		
		// show IconMarkers
		MarkersLayer ml=new MarkersLayer("icon markers");
		map.addLayer(ml);
		ml.setVisibility(false);
		
		// icon marker with default icon-image
		IconMarker im=new IconMarker(new LonLat(-100,50));
		ml.addMarker(im);
		
		// icon marker with custom image
		Icon ico = new Icon( "http://www.mensus.net/img/icons/google/aqua.png" , new Size(10,17),new Pixel(0,0)  );
		IconMarker im2=new IconMarker(new LonLat(-110,60),ico);
		ml.addMarker(im2);
		
		
		// add a ImageLayer with external URL
		Bounds bounds = new Bounds(-180, -88.759, 180, 88.759);
		Size size = new Size(580, 288);
		ImageLayer image_layer = new ImageLayer("image layer ext","http://earthtrends.wri.org/images/maps/4_m_citylights_lg.gif",bounds,size);
		map.addLayer(image_layer);
		
		// add a ImageLayer with internal URL
		Image image = Graphics.getImage("res/polymap_logo.png", getClass().getClassLoader());
		ImageLayer image_layer_int= new ImageLayer("image layer int",image,bounds);
		map.addLayer(image_layer_int);
		
		shell.setSize( 500, 500 );
		shell.open();

		while( !shell.isDisposed()) {
			if( !display.readAndDispatch() ) 
				display.sleep();
	      	}		
		return 0;
	}
}
