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

package org.polymap.openlayers.rap.widget.example.simple;

import java.util.HashMap;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.polymap.openlayers.rap.widget.*;
import org.polymap.openlayers.rap.widget.base.OpenLayersEventListener;
import org.polymap.openlayers.rap.widget.base.OpenLayersObject;
import org.polymap.openlayers.rap.widget.base_types.Bounds;
import org.polymap.openlayers.rap.widget.base_types.Icon;
import org.polymap.openlayers.rap.widget.base_types.LonLat;
import org.polymap.openlayers.rap.widget.base_types.OpenLayersMap;
import org.polymap.openlayers.rap.widget.base_types.Pixel;
import org.polymap.openlayers.rap.widget.base_types.Size;
import org.polymap.openlayers.rap.widget.base_types.Style;
import org.polymap.openlayers.rap.widget.controls.*;
import org.polymap.openlayers.rap.widget.features.VectorFeature;
import org.polymap.openlayers.rap.widget.geometry.LineStringGeometry;
import org.polymap.openlayers.rap.widget.geometry.LinearRingGeometry;
import org.polymap.openlayers.rap.widget.geometry.PointGeometry;
import org.polymap.openlayers.rap.widget.geometry.PolygonGeometry;
import org.polymap.openlayers.rap.widget.layers.*;
import org.polymap.openlayers.rap.widget.marker.BoxMarker;
import org.polymap.openlayers.rap.widget.marker.IconMarker;

/**
 * Simple Example on how to use the OpenLayers RAP Widget
 * 
 * @author Marcus -LiGi- B&uuml;schleb < mail: ligi (at) polymap (dot) de >
 * 
 */

public class Application implements IEntryPoint, OpenLayersEventListener {

	private OpenLayersMap map;
	private VectorLayer edit_layer;
	private EditingToolbarControl edit_toolbar;
	private VectorLayer selectable_boxes_layer;

	OverviewMapControl overview = null;

	public void process_event(OpenLayersObject obj, String event_name,
			HashMap<String, String> payload) {
		System.out.println("event from" + obj);
		if (event_name.equals("changebaselayer")) {
			System.out
					.println("client changed baselayer to '"
							+ payload.get("layername") + "' "
							+ payload.get("property"));

			if (overview != null)
				map.removeControl(overview);
			overview = new OverviewMapControl();
			map.addControl(overview);
		} else if (event_name.equals("changelayer")) {
			System.out.println("client changed layer '"
					+ payload.get("layername") + "' " + payload.get("property")
					+ "' " + payload.get("visibility"));
			if (payload.get("property").equals("visibility")) {
				Boolean visible = payload.get("visibility").equals("true");
				if (payload.get("layername").equals(edit_layer.getName())) {
					if (visible) {
						// adding edit control for the vector layer created
						// above
						edit_toolbar = new EditingToolbarControl(edit_layer);
						map.addControl(edit_toolbar);
						VectorLayer[] snapping_layers = { edit_layer,
								selectable_boxes_layer };
						SnappingControl snap_ctrl = new SnappingControl(
								edit_layer, snapping_layers, false);
						snap_ctrl.activate();
						map.addControl(snap_ctrl);

					} else {
						edit_toolbar.deactivate();
						map.removeControl(edit_toolbar);
					}
				}
			}
		} else
			System.out.println("unknown event " + event_name);
	}

	public int createUI() {

		// prepare the shell
		Display display = new Display();
		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setLayout(new FillLayout());
		shell.setText("OpenLayers Simple Example");

		// create the OpenLayers widget
		OpenLayersWidget widget = new OpenLayersWidget(shell, SWT.NONE);
		map = widget.getMap();

		HashMap<String, String> payload_map = new HashMap<String, String>();
		payload_map.put("layername", "event.layer.name");

		map.events.register(this, "changebaselayer", payload_map);

		payload_map.put("property", "event.property");
		payload_map.put("visibility", "event.layer.visibility");

		map.events.register(this, "changelayer", payload_map);

		

		// create and add a WMS layer
		WMSLayer wms_layer = new WMSLayer("polymap WMS",
				"http://www.polymap.de/geoserver/wms?",
				"states,tasmania_state_boundaries,tasmania_roads,tasmania_water_bodies");

		map.addLayer(wms_layer);

		// create and add a WMS layer with opacity
		WMSLayer wms_layer2 = new WMSLayer("OpenLayers WMS",
				"http://labs.metacarta.com/wms/vmap0?", "basic");
		wms_layer2.setIsBaseLayer(false);
		wms_layer2.setOpacity(0.2);
		map.addLayer(wms_layer2);

		// add a ImageLayer with external URL
		Bounds bounds = new Bounds(-180, -88.759, 180, 88.759);
		Size size = new Size(580, 288);
		ImageLayer image_layer = new ImageLayer("image layer ext",
				"http://earthtrends.wri.org/images/maps/4_m_citylights_lg.gif",
				bounds, size);
		map.addLayer(image_layer);

		// add a ImageLayer with internal URL
		Image image = Graphics.getImage("res/polymap_logo.png", getClass()
				.getClassLoader());
		ImageLayer image_layer_int = new ImageLayer("image layer int", image,
				bounds);
		map.addLayer(image_layer_int);

		// set Zoom and Center
		map.zoomTo(3);
		map.setCenter(-100.0, 40.0);

		// add some controls
		LayerSwitcherControl layer_switcher = new LayerSwitcherControl();

		map.addControl(layer_switcher);
		layer_switcher.maximizeControl();

		map.addControl(new MouseDefaultsControl());
		map.addControl(new KeyboardDefaultsControl());
		map.addControl(new PanZoomBarControl());
		map.addControl(new ScaleControl());

		ScaleLineControl scale_line = new ScaleLineControl();
		scale_line.setBottomOutUnits("");
		scale_line.setBottomInUnits("");
		map.addControl(scale_line);

		overview = new OverviewMapControl();
		map.addControl(overview);
		// overview.maximizeControl();

		// add vector layer to have a layer the user can edit
		edit_layer = new VectorLayer("edit layer");
		
		
		edit_layer.events.register(this, "beforefeatureadded", null);

		map.addLayer(edit_layer);
		edit_layer.setVisibility(false);

		// add vector layer with some boxes to demonstrate the modify feature
		// feature
		selectable_boxes_layer = new VectorLayer("selectable boxes");
		
		selectable_boxes_layer.events.register(this, "featureselected", null);
		
		selectable_boxes_layer.events.register(this, "featuremodified", null);
		
		map.addLayer(selectable_boxes_layer);

		//map.events.register(this, "click", new HashMap<String,String>()  {{ put("x",selectable_boxes_layer.getJSObjRef()+".getFeatureFromEvent(event)"); }});

		
		VectorFeature vector_feature = new VectorFeature(new Bounds(-100, 40,
				-80, 60).toGeometry());
		selectable_boxes_layer.addFeatures(vector_feature);
		vector_feature = new VectorFeature(new Bounds(-90, 70, -60, 80)
				.toGeometry());
		selectable_boxes_layer.addFeatures(vector_feature);

		selectable_boxes_layer.setVisibility(false);

		// add vector layer to show how to style a feature
		VectorLayer styled_features_layer = new VectorLayer("styled Features");
		map.addLayer(styled_features_layer);

		Style point_style_red = new Style();
		point_style_red.setAttribute("fillColor", "#FF0000");

		vector_feature = new VectorFeature(new PointGeometry(-85, 50),
				point_style_red);
		styled_features_layer.addFeatures(vector_feature);

		Style poly_style = new Style();
		poly_style.setAttribute("fillColor", "blue");
		poly_style.setAttribute("strokeColor", "black");
		poly_style.setAttribute("strokeDashstyle", "dashdot");

		vector_feature = new VectorFeature(new Bounds(-120, 23, -100, 42)
				.toGeometry(), poly_style);
		styled_features_layer.addFeatures(vector_feature);

		Style point_style_green = new Style();
		point_style_green.setAttribute("fillColor", "#00FF00");
		point_style_green.setAttribute("pointRadius", 20);

		vector_feature = new VectorFeature(new PointGeometry(-95, 65),
				point_style_green);
		styled_features_layer.addFeatures(vector_feature);

		// show some geometry
		VectorLayer geometry_features_layer = new VectorLayer(
				"Geometry Features");
		map.addLayer(geometry_features_layer);
		geometry_features_layer.setVisibility(false);

		PointGeometry[] point_list = { new PointGeometry(-99, 60),
				new PointGeometry(-107, 64), new PointGeometry(-130, 70) };
		VectorFeature linestring_feature = new VectorFeature(
				new LineStringGeometry(point_list));
		geometry_features_layer.addFeatures(linestring_feature);

		PointGeometry[] poly_point_list = { new PointGeometry(-99, 30),
				new PointGeometry(-107, 44), new PointGeometry(-130, 50) };
		VectorFeature poly_feature = new VectorFeature(new PolygonGeometry(
				new LinearRingGeometry(poly_point_list)));
		geometry_features_layer.addFeatures(poly_feature);

		// setting up the Modify Feature Control
		ModifyFeatureControl mfc = new ModifyFeatureControl(
				selectable_boxes_layer);

		mfc.addMode(ModifyFeatureControl.DRAG);
		mfc.addMode(ModifyFeatureControl.RESHAPE);
		mfc.addMode(ModifyFeatureControl.ROTATE);

		map.addControl(mfc);

		mfc.activate();

		// showing box_layer
		BoxMarker bm = new BoxMarker(new Bounds(-120, 23, -100, 42));
		BoxLayer bl = new BoxLayer("box layer");
		bl.addMarker(bm);
		map.addLayer(bl);
		bl.setVisibility(false);

		// show IconMarkers
		MarkersLayer ml = new MarkersLayer("icon markers");
		map.addLayer(ml);
		ml.setVisibility(false);

		// icon marker with default icon-image
		IconMarker im = new IconMarker(new LonLat(-100, 50));
		ml.addMarker(im);

		// icon marker with custom image
		Icon ico = new Icon("http://www.mensus.net/img/icons/google/aqua.png",
				new Size(10, 17), new Pixel(0, 0));
		IconMarker im2 = new IconMarker(new LonLat(-110, 60), ico);
		ml.addMarker(im2);

		shell.setSize(500, 500);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return 0;
	}
}
