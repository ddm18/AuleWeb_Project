function OpenEventDetails(event_id) {
	
	const urlParams = new URLSearchParams(window.location.search);
	
	const groupName = urlParams.get('groupName');
	const course = urlParams.get('course');
	const classroom = urlParams.get('classroom_name');
	const week = urlParams.get('week');
	
	let targetUrl;
	
	if (course) {
        // Redirect for "course"
        targetUrl = `EventDetails?groupName=${groupName}&eventId=${event_id}&course=${course}&week=${week}`;
    } else if (classroom) {
        // Redirect for "classroom"
        targetUrl = `EventDetails?groupName=${groupName}&eventId=${event_id}&classroom=${classroom}&week=${week}`;
    } else {
        // Fallback if neither attribute is present
        targetUrl = `EventDetails?groupName=${groupName}&eventId=${event_id}&week=${week}`;
    }
	
    // Redirect the user
    window.location.href = targetUrl;
    }



