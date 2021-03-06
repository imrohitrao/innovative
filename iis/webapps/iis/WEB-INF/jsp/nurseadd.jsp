<%@ taglib uri="../tld/dd4.tld" prefix="dd4" %>
<%@ page import="com.digitald4.common.model.*" %>
<%@ page import="com.digitald4.iis.model.*" %>
<%@ page import="com.digitald4.common.tld.*" %>
<% Nurse nurse = (Nurse)request.getAttribute("nurse");
User user = nurse.getUser();%>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDjNloCm6mOYV0Uk1ilOTAclLbgebGCBQ0&v=3.exp&sensor=false&libraries=places"></script>
<article class="container_12">
	<section class="grid_8">
		<div class="block-border">
			<form class="block-content form" id="simple_form" method="post" action="nurseadd">
				<h1>Nurse Registration</h1>
				<fieldset class="white-bg required">
					<div class="columns">
						<div class="colx2-left">
							<dd4:input type="<%=InputTag.Type.DATE%>" object="<%=nurse%>" prop="reg_date" label="Registration Date" />
						</div>
						<p class="colx2-right">
							<dd4:input type="<%=InputTag.Type.TEXT%>" object="<%=nurse%>" prop="referral_source" label="Referral Source" />
						</p>
					</div>
					<div class="columns">
						<div class="colx2-left">
							<dd4:input type="<%=InputTag.Type.TEXT%>" object="<%=user%>" prop="first_name" label="First Name" />
						</div>
						<div class="colx2-right">
							<dd4:input type="<%=InputTag.Type.TEXT%>" object="<%=user%>" prop="last_name" label="Last Name" />
						</div>
					</div>
					<div class="columns">
						<div class="colx2-left">
							<dd4:input type="<%=InputTag.Type.TEXT%>" object="<%=nurse%>" prop="address" label="Home Address" />
							<input type="hidden" id="latitude" name="nurse.latitude">
							<input type="hidden" id="longitude" name="nurse.longitude">
						</div>
						<p class="colx2-right">
							<dd4:input type="<%=InputTag.Type.TEXT%>" object="<%=user%>" prop="email" label="Email Address" />
						</p>
					</div>
					<div class="columns">
						<p class="colx3-left">
							<dd4:input type="<%=InputTag.Type.TEXT%>" object="<%=nurse%>" prop="pay_rate" label="Pay Rate" />
						</p>
						<p class="colx3-center">
							<span class="label">Pay Rate < 2hr</span>
							SOC <input type="TEXT" name="nurse.pay_rate_2hr_soc" size="5" value="<%=nurse.getPayRate2HrSoc()%>"/> FU <input type="TEXT" name="nurse.pay_rate_2hr_roc" size="5" value="<%=nurse.getPayRate2HrRoc()%>"/>
						</p>
						<p class="colx3-right">
							<dd4:input type="<%=InputTag.Type.TEXT%>" object="<%=nurse%>" prop="mileage_rate" label="Mileage Rate" />
						</p>
					</div>
					<dd4:input type="<%=InputTag.Type.TEXTAREA%>" object="<%=user%>" prop="notes" label="Notes" />
				</fieldset>

				<fieldset class="grey-bg no-margin">
					<button style="float:right" type="submit">Submit</button>
				</fieldset>

			</form>
		</div>
	</section>
	<div class="clear"></div>
	<div class="clear"></div>
</article>
<script>
	google.maps.event.addDomListener(window, 'load', addMapAutoComplete(document.getElementById('address'), function(place) {
		document.getElementById('latitude').value = place.geometry.location.lat();
		document.getElementById('longitude').value = place.geometry.location.lng();
	}));
</script>
