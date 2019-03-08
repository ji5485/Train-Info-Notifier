/**
 * 
 */
package org.dimigo.project;

import javafx.beans.property.StringProperty;

/**
 * <pre>
 *  org.dimigo.project
 *     |_ TrainInformation
 *
 * 1. 개요 : 
 * 2. 작성일 : 2017. 6. 25.
 * </pre>
 *
 * @author : ji548
 * @version : 1.0
 */
public class TrainInformation {
	private StringProperty trainName;
	private StringProperty depart;
	private StringProperty arrival;
	private StringProperty charge;
	
	public TrainInformation(StringProperty trainName, StringProperty depart, StringProperty arrival, StringProperty charge) {
		this.trainName = trainName;
		this.depart = depart;
		this.arrival = arrival;
		this.charge = charge;
	}
	
	public StringProperty getTrainName() {
		return trainName;
	}
	
	public StringProperty getDepart() {
		return depart;
	}
	
	public StringProperty getArrival() {
		return arrival;
	}
	
	public StringProperty getCharge() {
		return charge;
	}
}
