function toggleCsvForm() {
    const csvForm = document.getElementById('csvForm');
    const calendarSection = document.getElementById('calendar-section');
    const contentDiv = document.getElementById('content');

    const isVisible = csvForm.classList.contains('visible');

    if (!isVisible) {
        // Show CSV form, hide calendar-section, and clear content
        csvForm.style.display = 'flex'; // Ensure it takes up space in layout
        setTimeout(() => {
            csvForm.classList.add('visible'); // Apply animations
        }, 10); // Slight delay to trigger transition

        calendarSection.style.display = 'none'; // Hide calendar
        contentDiv.innerHTML = ''; // Clear content
    } else {
        // Hide CSV form and re-show calendar-section
        csvForm.classList.remove('visible');
        setTimeout(() => {
            csvForm.style.display = 'none'; // Hide after transition
        }, 300); // Match CSS transition duration

        calendarSection.style.display = 'block'; // Show calendar
    }
}
