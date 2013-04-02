<%@ taglib uri="../tld/dd4.tld" prefix="dd4"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="com.digitald4.iis.model.*" %>
<%@ page import="com.digitald4.common.tld.*" %>
<%@ page import="com.digitald4.common.util.FormatText" %>
<%@ page import="com.digitald4.common.component.Column"%>

<%Appointment appointment = (Appointment)request.getAttribute("appointment");%>
<article class="container_12">
	<section class="grid_4">
		<form class="block-content form" name="appointment-form" id="appointment-form" method="post" action="appointment">
			<dd4:input label="Patient" type="<%=InputTag.Type.COMBO%>" object="<%=appointment%>" prop="patient_id"  options="<%=Patient.getPatientsByState(GenData.PATIENT_ACTIVE.get())%>" />
			<dd4:input label="Nurse" type="<%=InputTag.Type.COMBO%>" object="<%=appointment%>" prop="nurse_id"  options="<%=Nurse.getAll()%>" />
			<p><span class="label">Start</span>
				Date:<input type="TEXT"  class="datepicker" name="appointment.start_date" id="appointment.start_date" value="<%=FormatText.formatDate(appointment.getStartDate())%>"/>
				Time:<input type="TEXT" name="appointment.start_time" id="appointment.start_time" value="<%=appointment.getStartTime()%>"/>
			</p>
			<p><span class="label">End</span>
				Date:<input type="TEXT"  class="datepicker" name="appointment.end_date" id="appointment.end_date" value="<%=FormatText.formatDate(appointment.getEndDate())%>"/>
				Time:<input type="TEXT" name="appointment.end_time" id="appointment.end_time" value="<%=appointment.getEndTime()%>"/>
			</p>
			<button type="submit">Save</button>
		</form>
	</section>
</article>
<script>
$(document).ready(function() {
	console.log("document ready");
	// We'll catch form submission to do it in AJAX, but this works also with JS disabled
	$('#appointment-form').submit(function(event) {
		// Stop full page load
		event.preventDefault();
		// Check fields
		var startDate = $('#appointment.start_date').val();
		var startTime = $('#appointment.start_time').val();
		var endDate = $('#appointment.end_date').val();
		var endTime = $('#appointment.end_time').val();
		if (!startDate || startDate.length == 0) {
			$('#login-block').removeBlockMessages().blockMessage('Please enter Start Date', {type: 'warning'});
		} else if (!startTime || startTime.length == 0) {
			$('#login-block').removeBlockMessages().blockMessage('Please enter Start Time', {type: 'warning'});
		} else if (!endDate || endDate.length == 0) {
			$('#login-block').removeBlockMessages().blockMessage('Please enter End Date', {type: 'warning'});
		} else if (!endTime || endTime.length == 0) {
			$('#login-block').removeBlockMessages().blockMessage('Please enter End Time', {type: 'warning'});
		} else {
			var submitBt = $(this).find('button[type=submit]');
			submitBt.disableBt();
			// Target url
			var target = $(this).attr('action');
			if (!target || target == '') {
				// Page url without hash
				target = document.location.href.match(/^([^#]+)/)[1];
			}
			// Request
			var data = {
					'appointment.id': $('#appointment.id').val(),
					'appointment.startDate': startDate,
					'appointment.startTime': startTime,
					'appointment.endDate': endDate,
					'appointment.endTtme': endTime,
			};
			sendTimer = new Date().getTime();
			if (redirect.length > 0) {
				data.redirect = redirect.val();
			}
			// Send
			$.ajax({
				url: target,
				dataType: 'json',
				type: 'POST',
				data: data,
				success: function(data, textStatus, XMLHttpRequest) {
					if (data.valid) {
						// Small timer to allow the 'checking login' message to show when server is too fast
						var receiveTimer = new Date().getTime();
						if (receiveTimer-sendTimer < 500) {
							setTimeout(function() {
								document.all.cal_supp.innerHTML = '';
							}, 500-(receiveTimer-sendTimer));
						} else {
							document.all.cal_supp.innerHTML = '';
						}
					} else {
						// Message
						$('#login-block').removeBlockMessages().blockMessage(data.error || 'An unexpected error occured, please try again', {type: 'error'});
						submitBt.enableBt();
					}
				},
				error: function(XMLHttpRequest, textStatus, errorThrown) {
					// Message
					$('#login-block').removeBlockMessages().blockMessage('Error while contacting server, please try again', {type: 'error'});
					submitBt.enableBt();
				}
			});
			// Message
			$('#login-block').removeBlockMessages().blockMessage('Please wait, checking login...', {type: 'loading'});
		}
	});
});

</script>
