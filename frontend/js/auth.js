/* ============================================================
   Auth — Login & Register Logic
   ============================================================ */
document.addEventListener('DOMContentLoaded', () => {
    // If already logged in, go to home
    if (session.isLoggedIn()) {
        window.location.href = 'home.html';
        return;
    }

    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');

    if (loginForm) setupLogin(loginForm);
    if (registerForm) setupRegister(registerForm);

    // Show success message if redirected from registration
    const params = new URLSearchParams(window.location.search);
    if (params.get('registered') === 'true') {
        const alert = document.getElementById('authAlert');
        if (alert) {
            alert.className = 'auth-alert success';
            alert.textContent = 'Registration successful! Please login with your credentials.';
        }
    }
});

function setupLogin(form) {
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const alert = document.getElementById('authAlert');
        const btn = form.querySelector('button[type="submit"]');
        const identifier = document.getElementById('identifier').value.trim();
        const password = document.getElementById('password').value;

        if (!identifier || !password) {
            showAlert(alert, 'Please fill in all fields.', 'error');
            return;
        }

        btn.disabled = true;
        btn.innerHTML = '<span class="spinner"></span> Logging in...';

        try {
            const res = await api.post('/auth/login', { identifier, password });
            session.set(res.data);
            window.location.href = 'home.html';
        } catch (err) {
            showAlert(alert, err.message || 'Invalid credentials. Please try again.', 'error');
            btn.disabled = false;
            btn.textContent = 'Login';
        }
    });
}

function setupRegister(form) {
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const alert = document.getElementById('authAlert');
        const btn = form.querySelector('button[type="submit"]');

        const data = {
            name: document.getElementById('name').value.trim(),
            email: document.getElementById('email').value.trim(),
            mobile: document.getElementById('mobile').value.trim(),
            rollNumber: document.getElementById('rollNumber').value.trim(),
            role: document.getElementById('role').value,
            password: document.getElementById('password').value,
            confirmPassword: document.getElementById('confirmPassword').value,
        };

        // Client-side validation
        if (!data.name || !data.email || !data.mobile || !data.rollNumber || !data.password || !data.confirmPassword) {
            showAlert(alert, 'Please fill in all fields.', 'error');
            return;
        }
        if (data.password !== data.confirmPassword) {
            showAlert(alert, 'Passwords do not match.', 'error');
            return;
        }
        if (data.password.length < 6) {
            showAlert(alert, 'Password must be at least 6 characters.', 'error');
            return;
        }

        btn.disabled = true;
        btn.innerHTML = '<span class="spinner"></span> Creating account...';

        try {
            await api.post('/auth/register', data);
            window.location.href = 'index.html?registered=true';
        } catch (err) {
            const msg = err.message || (err.data ? Object.values(err.data).join(', ') : 'Registration failed.');
            showAlert(alert, msg, 'error');
            btn.disabled = false;
            btn.textContent = 'Create Account';
        }
    });
}

function showAlert(el, msg, type) {
    if (!el) return;
    el.className = `auth-alert ${type}`;
    el.textContent = msg;
    el.style.display = 'block';
}
