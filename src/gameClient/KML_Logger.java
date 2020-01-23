package gameClient;


import de.micromata.opengis.kml.v_2_2_0.*;
import utils.Point3D;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;

/**
 * 
 *  
 * 
 */
public class KML_Logger {
	Kml k = new Kml();
	Document b= k.createAndSetDocument().withOpen(true);
	Folder c = b.createAndAddFolder();
	Placemark b1= new Placemark();
	final Kml kml;
	Document doc;
	Folder folder;
	Icon icon ;
	Style style ;
	Placemark placemark ;
	/**
	 * The constructor build the Kml doc and folder that will contain the kml data
	 * @throws FileNotFoundException
	 */
	public KML_Logger() throws FileNotFoundException {
		kml = new Kml();
		doc = kml.createAndSetDocument().withName("KML_game").withOpen(true);
		folder = doc.createAndAddFolder();
		folder.withName("display_game").withOpen(true);
	}
	/**
	 * The method create vertex view for the graph(map) build  
	 * @param longitude x
	 * @param latitude y
	 * @param id id of vertex
	 */
	public void newVertex( double longitude, double latitude, int id) {
		Icon icon = new Icon();
		Style style = doc.createAndAddStyle();
		Placemark placemark = folder.createAndAddPlacemark();	
		icon.withHref("http://maps.google.com/mapfiles/kml/paddle/blu-blank.png");
		style.withId("style_node"+id) .createAndSetIconStyle().withScale(0).withIcon(icon); 
		style.createAndSetLabelStyle().withColor("ff43b3ff").withScale(1);
		placemark.createAndSetPoint().addToCoordinates(longitude, latitude);
		placemark.withName(""+id).withStyleUrl("#style_").createAndSetLookAt().withLongitude(longitude).withLatitude(latitude).withAltitude(0).withRange(10000000);
	}


	/**
	 * this method create the gaerph view
	 * @param g
	 */
	public void createGraph(graph g) {
		Collection<node_data> nodes =g.getV();
		for(node_data n:nodes) {
			newVertex(n.getLocation().x(),n.getLocation().y(), n.getKey());
			Placemark p= doc.createAndAddPlacemark();
			p.setName("route from-"+n.getKey());
			p.createAndAddStyle().createAndSetLineStyle().withColor("ffffff").setWidth(4);
			LineString line =p.createAndSetLineString();
			line.withTessellate(true);
			Collection<edge_data> edges =g.getE(n.getKey());
			for(edge_data e: edges ) {
				line.addToCoordinates(n.getLocation().toString()+ ",0");
				line.addToCoordinates(g.getNode(e.getDest()).getLocation().toString()+ ",0");
			}
		}
	}

	/**
	 * save kml
	 * @param fileName
	 */
	public void save(String fileName)  {
		try {
			kml.marshal(new File("data\\"+fileName+".kml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void saveFruit(Point3D po, int type) {
		// TODO Auto-generated method stub
		icon = new Icon();
		style = doc.createAndAddStyle();
		placemark = folder.createAndAddPlacemark();
		if(type==-1) {
			icon.withHref("http://tancro.e-central.tv/grandmaster/markers/google-icons/mapfiles-kml-paddle/ylw-stars.png");	
		}
		else {// type=-1

			icon.withHref("http://tancro.e-central.tv/grandmaster/markers/google-icons/mapfiles-kml-paddle/red-stars.png");
		}

		style.withId("style_" +type ).createAndSetIconStyle().withScale(1.0).withIcon(icon); 
		placemark.createAndSetPoint().addToCoordinates(po.x(), po.y()); 
		placemark.withStyleUrl("#style_" + type).createAndSetLookAt().withLongitude(po.x()).withLatitude(po.y()).withAltitude(0).withRange(10000000);
		Date d=new Date();
		placemark.createAndSetTimeStamp().withWhen(""+d.toInstant());

	}
	public void saveRobot(Point3D pos, int rid) {
		icon = new Icon();
		style = doc.createAndAddStyle();
		placemark = folder.createAndAddPlacemark();	
		icon.withHref("http://maps.google.com/mapfiles/kml/shapes/cabs.png");
		style.withId("style_robot" + 0).createAndSetIconStyle().withScale(1).withIcon(icon); 
		style.createAndSetLabelStyle().withColor("0033ff").withScale(1); 
		placemark.createAndSetPoint().addToCoordinates(pos.x(), pos.y()); 
		placemark.withName(""+rid).withStyleUrl("#style_robot"+ 0).createAndSetLookAt().withLongitude(pos.x()).withLatitude(pos.y()).withAltitude(0).withRange(10000000);
		Date d=new Date();
		placemark.createAndSetTimeStamp().withWhen(""+d.toInstant());
	}
	public static String getKML(int level) {
        // The file read
        File in = new File("data\\kmlfor"+level+".kml");
        FileReader fr = null;
        String line;
        StringBuilder string = new StringBuilder();
        // Try block: Most stream operations may throw IO exception
        try {
            // Create file reader and file writer objects
            fr = new FileReader(in);

            // Wrap the reader and the writer with buffered streams
            BufferedReader reader = new BufferedReader(fr);
            while ((line = reader.readLine()) != null) {
                // Print the line read and write it to the output file
               string.append(line) ;
            }
            // Close the streams
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
       return string.toString(); 
    }

}

