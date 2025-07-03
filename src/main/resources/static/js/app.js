// Razorpay Key (replace with your actual test key)
const RAZORPAY_KEY_ID = "rzp_test_OGxjrlvYkCmv4";

// Get references (wait for DOMContentLoaded in case they’re not available yet)
let paymentModal, paymentDetails, messageModal, messageIcon, messageTitle, messageText, loadingOverlay;

document.addEventListener('DOMContentLoaded', () => {
    paymentModal = document.getElementById('paymentModal');
    paymentDetails = document.getElementById('paymentDetails');
    messageModal = document.getElementById('messageModal');
    messageIcon = document.getElementById('messageIcon');
    messageTitle = document.getElementById('messageTitle');
    messageText = document.getElementById('messageText');
    loadingOverlay = document.getElementById('loadingOverlay');

    const logoutButton = document.getElementById('logoutButton');
    if (logoutButton) {
        logoutButton.addEventListener('click', logout);
    }
});

// --- Utility Functions ---

function showLoading(message = 'Processing your request...') {
    if (loadingOverlay) {
        loadingOverlay.querySelector('p').textContent = message;
        loadingOverlay.style.display = 'flex';
    }
}

function hideLoading() {
    if (loadingOverlay) {
        loadingOverlay.style.display = 'none';
    }
}

function showMessageModal(type, title, text) {
    if (!messageModal) return;
    messageIcon.className = 'icon fas';

    if (type === 'success') messageIcon.classList.add('fa-check-circle', 'success');
    else if (type === 'error') messageIcon.classList.add('fa-times-circle', 'error');
    else messageIcon.classList.add('fa-info-circle', 'info');

    messageTitle.textContent = title;
    messageText.textContent = text;
    messageModal.style.display = 'flex';
}

function closeMessageModal() {
    if (messageModal) messageModal.style.display = 'none';
}

function closePaymentModal() {
    if (paymentModal) paymentModal.style.display = 'none';
    if (typeof selectedBooking != 'undefined') {
        selectedBooking = null;
    }
}

// --- Logout Function ---
function logout() {
    showLoading('Logging out...');
    fetch('/api/auth/logout', { method: 'POST' })
        .then(res => {
            hideLoading();
            if (res.ok) {
                showMessageModal('success', 'Logged Out', 'You have been logged out successfully!');
                setTimeout(() => window.location.href = '/login?logout=true', 1500);
            } else {
                showMessageModal('error', 'Logout Failed', 'Session expired or logout failed. Redirecting...');
                setTimeout(() => window.location.href = '/login', 1500);
            }
        })
        .catch(err => {
            console.error('Logout error:', err);
            hideLoading();
            showMessageModal('error', 'Logout Error', 'Error occurred while logging out.');
            setTimeout(() => window.location.href = '/login', 1500);
        });
}

//// --- Close modals on outside click ---
//window.onclick = function(event) {
//    if (event.target === paymentModal) closePaymentModal();
//    if (event.target === messageModal) closeMessageModal();
////	selectedBooking = null;
//};

window.onclick = function(event) {
    if (event.target === paymentModal) {
        closePaymentModal();
    }
    if (event.target === messageModal) {
        closeMessageModal();
    }
    // ✅ Do NOT clear selectedBooking here!
};
