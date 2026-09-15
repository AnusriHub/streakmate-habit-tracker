document.addEventListener('DOMContentLoaded', function () {
    autoHideFlashMessages();
});


/*
 * Hide success/error messages after a few seconds.
 * This is only a small convenience so old messages
 * do not stay on the page forever.
 */
function autoHideFlashMessages() {

    const alerts = document.querySelectorAll('.sm-alert');

    alerts.forEach(function (alert) {

        setTimeout(function () {

            alert.style.opacity = '0';
            alert.style.transition = 'opacity 0.3s ease';

            setTimeout(function () {
                alert.remove();
            }, 300);

        }, 5000);

    });
}


/*
 * Copy text to the clipboard.
 * Used by the challenge detail page when
 * the challenge code needs to be copied.
 */
function copyToClipboard(text) {

    if (!navigator.clipboard) {
        return Promise.reject(new Error('Clipboard is not supported.'));
    }

    return navigator.clipboard.writeText(text);
}


/*
 * Format a date for places where JavaScript
 * needs to display a date.
 */
function formatDate(dateStr) {

    const date = new Date(dateStr);

    if (isNaN(date.getTime())) {
        return '';
    }

    return date.toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric'
    });
}
