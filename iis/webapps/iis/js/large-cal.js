
function setMonth(userId, year, month) {
	// Request
	var data = {
		action: "cal",
		id: userId,
		year: year,
		month: month
	};
	var target = document.location.href.match(/^([^#]+)/)[1];
	// Send
	$.ajax({
		url: target,
		dataType: 'json',
		type: 'POST',
		data: data,
		success: function(data, textStatus, XMLHttpRequest) {
			if (data.valid) {
				document.all.cal_sec.innerHTML = data.html;
			} else {
				document.all.cal_sec.innerHTML = 'An unexpected error occured, please try again';
			}
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			document.all.cal_sec.innerHTML = target;//'Error while contacting server, please try again';
		}
	});
}

function addEvent(data) {
	document.all.cal_supp.innerHTML = ' Add Event for ' + data['appointment.start_date'];
	var target = "appointment";
	$.ajax({
		url: target,
		dataType: 'html',
		type: 'GET',
		data: data,
		success: function(data, textStatus, XMLHttpRequest) {
			document.all.cal_supp.innerHTML = data;
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			document.all.cal_supp.innerHTML = 'Error while contacting server for taget ' + target + ', please try again error: ' + errorThrown;
		}
	});
}

function editEvent(id) {
	document.all.cal_supp.innerHTML = ' Edit Event: ' + id;
	var data = {'appointment.id': id};
	var target = "appointment";
	$.ajax({
		url: target,
		dataType: 'html',
		type: 'GET',
		data: data,
		success: function(data, textStatus, XMLHttpRequest) {
			document.all.cal_supp.innerHTML = data;
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			document.all.cal_supp.innerHTML = 'Error while contacting server for taget ' + target + ', please try again error: ' + errorThrown;
		}
	});
}

function submitAppointment(){
	console.log("submit appointment");
	// We'll catch form submission to do it in AJAX, but this works also with JS disabled
	// Stop full page load
	// Check fields
	var appId = $('#appointment_id').val();
	if (!appId) {
		appId = '';
	}
	var patientId = $('#patientId' + appId).val();
	var nurseId = $('#nurseId' + appId).val();
	var startDate = $('#start_date').val();
	var startTime = $('#start_time').val();
	var endTime = $('#end_time').val();
	
	console.log('Patient id: ' + patientId);

	if (!patientId || patientId == 0) {
		$('#cal_sec').removeBlockMessages().blockMessage('Please select a Patient', {type: 'warning'});
	} else if (!startDate || startDate.length == 0) {
		$('#cal_sec').removeBlockMessages().blockMessage('Please enter Start Date', {type: 'warning'});
	} else if (!startTime || startTime.length == 0) {
		$('#cal_sec').removeBlockMessages().blockMessage('Please enter Start Time', {type: 'warning'});
	} else if (!endTime || endTime.length == 0) {
		$('#cal_sec').removeBlockMessages().blockMessage('Please enter End Time', {type: 'warning'});
	} else {
		// Target url
		var target = 'appointment';
		// Request
		var data = {
			'cal_type': document.location.href.match(/^([^#]+)/)[1],
			'appointment.id': $('#appointment_id').val(),
			'appointment.patient_id': patientId,
			'appointment.nurse_id': nurseId,
			'appointment.start_date': startDate,
			'appointment.start_time': startTime,
			'appointment.end_time': endTime,
		};
		sendTimer = new Date().getTime();
		// Send
		$.ajax({
			url: target,
			dataType: 'json',
			type: 'POST',
			data: data,
			success: function(data, textStatus, XMLHttpRequest) {
				if (data.valid) {
					document.all.cal_sec.innerHTML = data.html;
				} else {
					// Message
					$('#cal_sec').removeBlockMessages().blockMessage(data.error || 'An unexpected error occured, please try again', {type: 'error'});
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				// Message
				$('#cal_sec').removeBlockMessages().blockMessage('Error while contacting server, please try again', {type: 'error'});
			}
		});
		// Message
		$('#cal_sec').removeBlockMessages().blockMessage('Please wait, checking login...', {type: 'loading'});
	}
};

cancelEditApp = function() {
	document.all.cal_supp.innerHTML = '';
};

