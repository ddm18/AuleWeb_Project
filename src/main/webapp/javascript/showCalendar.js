function showCalendar() {
    const calendarSection = document.getElementById('calendar-section');
    if (calendarSection) {
        calendarSection.style.display = 'block'; // Show calendar
    }

    const currentSection = document.getElementById('content');
    if (currentSection) {
        currentSection.style.display = 'none'; // Hide content
    }

    // Hide the CSV form
    const csvForm = document.getElementById('csvForm');
    if (csvForm) {
        csvForm.classList.remove('visible');
        setTimeout(() => {
            csvForm.style.display = 'none'; // Ensure it's completely hidden
        }, 300); // Match transition duration
    }
}