$(document).ready(function () {
    // Toggle dropdown visibility on icon click
    $('.icon-wrapper').on('click', function (event) {
        $('#dropdown').toggleClass('visible'); // Toggle visibility
        event.stopPropagation(); // Prevent click from propagating
    });

    // Hide dropdown when clicking outside
    $(document).on('click', function () {
        $('#dropdown').removeClass('visible'); // Hide dropdown
    });
});
