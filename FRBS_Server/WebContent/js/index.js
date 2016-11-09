	$(document).ready(function() {
		var id = document.getElementById("indexForm");
		
		$('#roundTrip').hide();
		
		$( "#oneWay" ).datepicker({
			 minDate: '+0',
		      dateFormat: 'yy/mm/dd',
		      //defaultDate: "+1w",
		      changeMonth: true,
		      numberOfMonths: 1,
		      onClose: function( selectedDate ) {
		        $( "#roundTrip" ).datepicker( "option", "minDate", selectedDate );
		      }
		     
		    });
		    $( "#roundTrip" ).datepicker({
		      dateFormat: 'yy/mm/dd',
		     // defaultDate: "+1w",
		      changeMonth: true,
		      numberOfMonths: 1,
		      onClose: function( selectedDate ) {
		        $( "#oneWay" ).datepicker( "option", "maxDate", selectedDate );
		      }
		    });
		
		$('#oneWayBtn').click(function() {
			$('#roundTrip').hide();
			id.command.value = "searchAirplaneOneWay";
		});
		
		$('#roundTripBtn').click(function() {
			$('#roundTrip').show();
			id.command.value = "searchAirplaneRoundTrip";
		});
		
		$('#indexForm').submit(function() {
			if($('#departureAp').val() == $('#arrivalAp').val()){
				alert("출발 지점과 도착 지점이 같습니다");
				return false;
			}

			var now = new Date();
			var page =1;
			
			var month = $('#oneWay').datepicker('getDate').getMonth() - now.getMonth();
			var date = $('#oneWay').datepicker('getDate').getDate() - now.getDate();
			
			if(month >0) {
				for(i=0; i<month; i++) {
					if(i == 0) {
						now.setMonth(now.getMonth()+1, now.getDate());	// month가 1 작은 값으로 출력됨
						var tmp = now.getDate();
						now.setDate(0);
						
						page += now.getDate() -tmp;
					}
					else {
						now.setMonth(now.getMonth() +1, 1);
						now.setDate(0);
						
						page += now.getDate();
					}
				}
				
				page += $('#oneWay').datepicker('getDate').getDate();
				
			}	// if month
			
			else {
				page += date;
			}
			id.page.value = page;
		});	// submit
	});