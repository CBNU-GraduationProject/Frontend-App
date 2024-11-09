window.onload = function() {
    const token = localStorage.getItem('authToken');
    const userid = localStorage.getItem('userId');
    
    if (!token || !userid) {
        alert('Please log in first.');
        window.location.href = 'login';
        return;
    }
}
