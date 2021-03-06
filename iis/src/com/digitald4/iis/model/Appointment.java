package com.digitald4.iis.model;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.digitald4.common.component.CalEvent;
import com.digitald4.common.model.GeneralData;
import com.digitald4.common.util.Calculate;
import com.digitald4.common.util.FormatText;
import com.digitald4.iis.dao.AppointmentDAO;
@Entity
@Table(schema="iis",name="appointment")
@NamedQueries({
	@NamedQuery(name = "findByID", query="SELECT o FROM Appointment o WHERE o.ID=?1"),//AUTO-GENERATED
	@NamedQuery(name = "findAll", query="SELECT o FROM Appointment o"),//AUTO-GENERATED
	@NamedQuery(name = "findAllActive", query="SELECT o FROM Appointment o"),//AUTO-GENERATED
	@NamedQuery(name = "findByPatient", query="SELECT o FROM Appointment o WHERE o.PATIENT_ID=?1"),//AUTO-GENERATED
	@NamedQuery(name = "findByNurse", query="SELECT o FROM Appointment o WHERE o.NURSE_ID=?1"),//AUTO-GENERATED
	@NamedQuery(name = "findByPaystub", query="SELECT o FROM Appointment o WHERE o.PAYSTUB_ID=?1"),//AUTO-GENERATED
	@NamedQuery(name = "findByInvoice", query="SELECT o FROM Appointment o WHERE o.INVOICE_ID=?1"),//AUTO-GENERATED
})
@NamedNativeQueries({
	@NamedNativeQuery(name = "refresh", query="SELECT o.* FROM appointment o WHERE o.ID=?"),//AUTO-GENERATED
})
public class Appointment extends AppointmentDAO implements CalEvent {
	
	public Appointment(){
	}
	
	public Appointment(Integer id){
		super(id);
	}
	
	public Appointment(Appointment orig){
		super(orig);
	}
	
	public Object getAssessmentValue(int assessmentId) throws Exception {
		return getAssessmentValue(GeneralData.getInstance(assessmentId));
	}
	
	public Object getAssessmentValue(GeneralData assessment) throws Exception {
		return getAssessmentEntry(assessment).getValue();
	}
	
	@Override
	public Object getPropertyValue(String property) {
		if (Character.isDigit(property.charAt(0))) {
			try {
				return getAssessmentValue(Integer.parseInt(property));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else if (property.equalsIgnoreCase("START_DATE")) {
			return getStartDate();
		} else if (property.equalsIgnoreCase("START_TIME")) {
			return getStartTime();
		} else if (property.equalsIgnoreCase("END_DATE")) {
			return getEndDate();
		} else if (property.equalsIgnoreCase("END_TIME")) {
			return getEndTime();
		}
		return super.getPropertyValue(property);
	}
	
	@Override
	public void setPropertyValue(String property, String value) throws Exception {
		property = formatProperty(property);
		if (Character.isDigit(property.charAt(0))) {
			setAssessmentEntry(GeneralData.getInstance(Integer.parseInt(property)), value);
		} else if (property.equalsIgnoreCase("START_DATE")) {
			setStartDate(FormatText.USER_DATE.parse(value));
		} else if (property.equalsIgnoreCase("START_TIME")) {
			setStartTime(new Time(FormatText.USER_TIME.parse(value).getTime()));
		} else if (property.equalsIgnoreCase("END_DATE")) {
			setEndDate(FormatText.USER_DATE.parse(value));
		} else if (property.equalsIgnoreCase("END_TIME")) {
			setEndTime(new Time(FormatText.USER_TIME.parse(value).getTime()));
		} else if (property.equalsIgnoreCase("TIME_IN")) {
			setTimeIn(new DateTime(FormatText.USER_TIME.parse(value)));
		} else if (property.equalsIgnoreCase("TIME_OUT")) {
			setTimeOut(new DateTime(FormatText.USER_TIME.parse(value)));
		} else {
			super.setPropertyValue(property, value);
		}
	}
	
	public AssessmentEntry getAssessmentEntry(GeneralData assessment) throws Exception {
		for (AssessmentEntry ae : getAssessmentEntrys()) {
			if (ae.getAssessment() == assessment) {
				return ae;
			}
		}
		return new AssessmentEntry().setAppointment(this).setAssessment(assessment);
	}

	public Appointment setAssessmentEntry(GeneralData assessment, String value) throws Exception {
		AssessmentEntry ae = getAssessmentEntry(assessment).setValue(value);
		if (ae.isNewInstance()) {
			addAssessmentEntry(ae);
		} else {
			ae.save();
		}
		return this;
	}
	
	public static List<Appointment> getPending() {
		DateTime now = DateTime.now();
		List<Appointment> pot = getCollection(new String[]{"" + PROPERTY.CANCELLED, "" + PROPERTY.ASSESSMENT_COMPLETE}, false, false);
		for (Appointment app : pot) {
			if (app.getStart().isAfter(now)) {
				return pot.subList(0, pot.indexOf(app));
			}
		}
		return pot;
	}
	
	public static List<Appointment> getPending(Vendor vendor) {
		DateTime now = DateTime.now();
		List<Appointment> pending = new ArrayList<Appointment>();
		for (Appointment app : getCollection(new String[]{"" + PROPERTY.CANCELLED, "" + PROPERTY.ASSESSMENT_COMPLETE}, false, false)) {
			if (app.getStart().isAfter(now)) {
				break;
			}
			if (app.getPatient().getVendor() == vendor) {
				pending.add(app);
			}
		}
		return pending;
	}
	
	public static List<Appointment> getReviewables() {
		return getCollection(new String[]{"" + PROPERTY.CANCELLED, "" + PROPERTY.ASSESSMENT_COMPLETE, "" + PROPERTY.ASSESSMENT_APPROVED}, false, true, false);
	}
	
	public static List<Appointment> getPayables() {
		return getCollection(new String[]{"" + PROPERTY.CANCELLED, "" + PROPERTY.ASSESSMENT_APPROVED, "" + PROPERTY.PAYSTUB_ID}, false, true, null);
	}
	
	public static List<Appointment> getBillables() {
		return getCollection(new String[]{"" + PROPERTY.CANCELLED, "" + PROPERTY.ASSESSMENT_APPROVED, "" + PROPERTY.INVOICE_ID}, false, true, null);
	}

	@Override
	public boolean isActiveOnDay(Date date) {
		return isActiveBetween(new DateTime(date), new DateTime(date).plusDays(1));
	}

	@Override
	public String getTitle() {
		return "" + getPatient();
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isActiveBetween(DateTime start, DateTime end) {
		start = start.minusMillis(1);
		DateTime st = getStart();
		DateTime et = getEnd();
		// Did this event start any time between these periods or did these period start any time during this event
		return (start.isBefore(st) && end.isAfter(st) || st.isBefore(start) && et.isAfter(start));
	}
	
	private static int dataPoints = 0;
	public static int getDataPointTotal() throws Exception {
		if (dataPoints == 0) {
			for (GeneralData cat : GenData.ASS_CAT.get().getGeneralDatas()) {
				dataPoints += cat.getGeneralDatas().size();
			}
		}
		return dataPoints;
	}
	
	public int getPercentComplete() throws Exception {
		return getAssessmentEntrys().size() * 100 / getDataPointTotal();
	}
	
	@Override
	public Appointment setStart(DateTime start) throws Exception {
		super.setStart(start);
		checkEndDate();
		return this;
	}
	
	@Override
	public Appointment setEnd(DateTime end) throws Exception {
		super.setEnd(end);
		checkEndDate();
		return this;
	}
	
	public Date getStartDate() {
		DateTime start = getStart();
		if (start != null) {
			return start.toDate();
		}
		return null;
	}
	
	public void setStartDate(Date date) throws Exception {
		DateTime start = getStart();
		if (start == null) {
			setStart(new DateTime(date));
		} else {
			setStart(new DateTime(date).withMillisOfDay(start.getMillisOfDay()));
		}
	}
	
	public Time getStartTime() {
		DateTime start = getStart();
		if (start != null) {
			return new Time(start.getMillis());
		}
		return null;
	}
	
	public void setStartTime(Time time) throws Exception {
		DateTime start = getStart();
		if (start != null) {
			setStart(start.withMillisOfDay(new DateTime(time.getTime()).getMillisOfDay()));
		} else {
			setStart(new DateTime(time.getTime()));
		}
	}
	
	public Date getEndDate() {
		DateTime end = getEnd();
		if (end != null) {
			return end.toDate();
		}
		return null;
	}
	
	private void setEndDate(Date date) throws Exception {
		DateTime end = getEnd();
		if (end == null) {
			setEnd(new DateTime(date));
		} else {
			setEnd(new DateTime(date).withMillisOfDay(end.getMillisOfDay()));
		}
	}
	
	public Time getEndTime() {
		DateTime end = getEnd();
		if (end != null) {
			return new Time(end.getMillis());
		}
		return null;
	}
	
	private void checkEndDate() throws Exception {
		DateTime start = getStart();
		if (start == null) {
			return;
		}
		DateTime end = getEnd();
		if (end == null) {
			setEnd(start.plusHours(3));
		} else if (end.isBefore(start)) {
			setEndDate(start.plusDays(1).toDate());
		} else if ((end.getMillis() - start.getMillis()) > Calculate.ONE_DAY) {
			setEndDate(start.toDate());
		}
	}
	
	public void setEndTime(Time time) throws Exception {
		DateTime end = getEnd();
		if (end == null) {
			setEnd(new DateTime(time.getTime()));
		} else {
			setEnd(end.withMillisOfDay(new DateTime(time.getTime()).getMillisOfDay()));
		}
	}

	@Override
	public int getDuration() {
		DateTime start = getStart();
		DateTime end = getEnd();
		if (start != null && end != null) {
			return (int)(end.getMillis() - start.getMillis()) / 60000;
		}
		return 0;
	}
	
	public Appointment setDuration(int duration) throws Exception {
		setEnd(getStart().plusMinutes(duration));
		return this;
	}
	
	@Override
	public int compareTo(Object o) {
		if (o instanceof Appointment) {
			Appointment app = (Appointment)o;
			int ret = getStart().compareTo(app.getStart());
			if (ret == 0) {
				ret = getEnd().compareTo(app.getEnd());
			}
			if (ret != 0) {
				return ret;
			}
		}
		return super.compareTo(o);
	}

	public boolean isPending() {
		return System.currentTimeMillis() > getStart().getMillis() && !isAssessmentComplete() && !isCancelled();
	}
	
	public boolean isReviewable() {
		return isAssessmentComplete() && !isAssessmentApproved();
	}
	
	public boolean isPayable() {
		return isAssessmentApproved() && !isPaid();
	}
	
	public boolean isBillable() {
		return isAssessmentApproved() && !isBilled();
	}
	
	public boolean isPaid() {
		return getPaystubId() != null;
	}

	public JSONObject toAssessmentJSON() throws Exception {
		JSONObject json = toJSON();
		JSONArray cats = new JSONArray();
		for (GeneralData cat : GenData.ASS_CAT.get().getGeneralDatas()) {
			JSONObject catJson = cat.toJSON();
			JSONArray entries = new JSONArray();
			for (GeneralData value : cat.getGeneralDatas()) {
				entries.put(getAssessmentEntry(value));
			}
			catJson.put("entries", entries);
			cats.put(catJson);
		}
		json.put("categories", cats);
		return json;
	}

	public double getBilledHours() {
		if (getTimeOut() == null || getTimeIn() == null) {
			return 0;
		}
		long diff = (getTimeOut().getMillis() - getTimeIn().getMillis()) / 60000;
		diff = Math.round(diff / 15.0) * 15;
		return diff / 60.0;
	}
	
	@Override
	public double getMileageRate() {
		if (isNewInstance()) {
			return 0;
		}
		if (super.getMileageRate() > 0) {
			return super.getMileageRate();
		}
		return getNurse().getMileageRate();
	}
	
	public boolean isStartOfCare() {
		// TODO Create a way to override this.
		return getPrevAppointment() == null;
	}
	
	public Appointment lockInPayment() throws Exception {
		setPayRate(getPayRate());
		return setMileageRate(getMileageRate());
	}
	
	@Override
	public Appointment setPaystub(Paystub paystub) throws Exception {
		lockInPayment();
		return super.setPaystub(paystub);
	}
	
	@Override
	public double getPayRate() {
		// For new instance return 0 in order to keep automatic payrate.
		if (this.isNewInstance()) {
			return 0;
		}
		if (super.getPayRate() != 0) {
			return super.getPayRate();
		}
		if (getBilledHours() > 2) {
			return getNurse().getPayRate();
		}
		if (isStartOfCare()) {
			return getNurse().getPayRate2HrSoc();
		}
		return getNurse().getPayRate2HrRoc();
	}

	public double getTotalPayment() {
		return Math.round((getBilledHours() * getPayRate() + getMileage() * getMileageRate()) * 100) / 100.0;
	}

	public Appointment getPrevAppointment() {
		List<Appointment> appointments = getPatient().getAppointments();
		int index = appointments.indexOf(this);
		if (index > 0) {
			return appointments.get(index-1);
		}
		return null;
	}
	
	public Appointment lockInBilling() throws Exception {
		setBillingRate(getBillingRate());
		return setVendorMileageRate(getVendorMileageRate());
	}
	
	@Override
	public Appointment setInvoice(Invoice invoice) throws Exception {
		lockInBilling();
		return super.setInvoice(invoice);
	}
	
	public boolean isBilled() {
		return getInvoiceId() != null;
	}

	@Override
	public double getBillingRate() {
		double br;
		if (this.isNewInstance()) {
			br = 0;
		} else if (super.getBillingRate() != 0) {
			br = super.getBillingRate();
		} else if (getBilledHours() > 2) {
			br = getPatient().getBillingRate();
			if (br == 0) {
				br = getPatient().getVendor().getBillingRate();
			}
		} else if (isStartOfCare()) {
				br = getPatient().getBillingRate2HrSoc();
				if (br == 0) {
					br = getPatient().getVendor().getBillingFlat2HrSoc();
				}
		} else {
			br = getPatient().getBillingRate2HrRoc();
			if (br == 0) {
				br = getPatient().getVendor().getBillingFlat2HrRoc();
			}
		}
		return br;
	}

	public double getBillingFlat() {
		double bf = 0;
		if (getBilledHours() <= 2) {
			bf = getPatient().getBillingFlat();
			if (bf == 0) {
				bf = getPatient().getVendor().getBillingFlat();
			}
		}
		return bf;
	}
	
	@Override
	public double getVendorMileageRate() {
		if (this.isNewInstance()) {
			return 0;
		} else if (isBilled() || super.getVendorMileageRate() > 0) {
			return super.getVendorMileageRate();
		}
		double mr = getPatient().getMileageRate();
		if (mr == 0) {
			mr = getPatient().getVendor().getMileageRate();
		}
		return mr;
	}

	public int getVendorMileage() {
		if (getVendorMileageRate() > 0) {
			return getMileage();
		}
		return 0;
	}
	
	public double getVendorMileageTotal() {
		return getVendorMileageRate() * getVendorMileage();
	}
	
	public double getBillingTotal() {
		return Math.round((getBilledHours() * getBillingRate() + getBillingFlat() + getVendorMileageTotal()) * 100) / 100.0;
	}
	
	public double getSelfPaidMileage() {
		return 20;
	}
	
	public double getPayableMileage() {
		double pm = getMileage() - getSelfPaidMileage();
		if (pm > 0) {
			return pm;
		}
		return 0;
	}
	
	@Override
	public JSONObject toJSON() throws JSONException {
		return super.toJSON()
				.put("billingTotal", getBillingTotal())
				.put("totalPayment", getTotalPayment());
	}

	public Vendor getVendor() {
		return getPatient().getVendor();
	}
}
