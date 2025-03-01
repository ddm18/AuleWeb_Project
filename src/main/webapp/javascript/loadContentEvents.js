function loadCurrentEvents(department) {
    console.log('Department received:', department, 'Type:', typeof department);

    const departmentName = encodeURIComponent(department);
    const url = "currentEvents?groupName=" + departmentName;
    console.log('Requesting URL:', url);

    // Hide the calendar section
    const calendarSection = document.getElementById('calendar-section');
    if (calendarSection) {
        calendarSection.style.display = 'none';
    }

    // Clear the content div before loading new content
    const contentDiv = document.getElementById('content');
    if (contentDiv) {
        contentDiv.innerHTML = ''; // Clears existing content
        contentDiv.style.display = 'block'; // Make it visible
    }

    // Hide the CSV form
    const csvForm = document.getElementById('csvForm');
    if (csvForm) {
        csvForm.classList.remove('visible');
        setTimeout(() => {
            csvForm.style.display = 'none'; // Ensure it's completely hidden
        }, 300); // Match transition duration
    }

    // Make an AJAX request to fetch current events
    fetch(url, {
        method: 'GET',
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok: ' + response.status);
        }
        return response.text();
    })
    .then(html => {
        console.log('Successfully fetched content.');
        if (contentDiv) {
            contentDiv.innerHTML = html;
        }
    })
    .catch(error => {
        console.error('Error fetching current events:', error);
    });
}

function handleFormSubmit(event) {
    event.preventDefault(); // Prevent the form from submitting traditionally

    // Get the selected date from the form input
    const selectedDate = document.getElementById('search-day').value;

    if (selectedDate) {
        console.log('Selected date:', selectedDate);
        // Call loadDayEvent with the selected date
        loadDayEvent(selectedDate);
    }
}

function loadDayEvent(date) {
    console.log('Date received:', date, 'Type:', typeof date);

    // Encode the date and create the request URL
    const encodedDate = encodeURIComponent(date);
    const url = "given_day?date=" + encodedDate;
    console.log('Requesting URL:', url);

    // Hide the calendar section
    const calendarSection = document.getElementById('calendar-section');
    if (calendarSection) {
        calendarSection.style.display = 'none';
    }

    // Clear the content div before loading new content
    const contentDiv = document.getElementById('content');
    if (contentDiv) {
        contentDiv.innerHTML = ''; // Clear existing content
        contentDiv.style.display = 'block'; // Make it visible
    }

    // Hide the CSV form
    const csvForm = document.getElementById('csvForm');
    if (csvForm) {
        csvForm.classList.remove('visible');
        setTimeout(() => {
            csvForm.style.display = 'none'; // Ensure it's completely hidden
        }, 300); // Match transition duration
    }

    // Make an AJAX request to fetch content based on the selected date
    fetch(url, {
        method: 'GET',
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok: ' + response.status);
        }
        return response.text();
    })
    .then(html => {
        console.log('Successfully fetched content.');
        if (contentDiv) {
            contentDiv.innerHTML = html;
        }
    })
    .catch(error => {
        console.error('Error fetching events for the given date:', error);
    });
}