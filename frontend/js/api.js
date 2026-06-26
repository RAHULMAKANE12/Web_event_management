/* ============================================================
   API Helper — Centralized fetch wrapper
   ============================================================ */
const API_BASE = 'http://localhost:8080/api';

const api = {
    async request(endpoint, options = {}) {
        const url = `${API_BASE}${endpoint}`;
        const config = {
            headers: { 'Content-Type': 'application/json' },
            ...options,
        };
        // Remove Content-Type for FormData (browser sets it with boundary)
        if (options.body instanceof FormData) {
            delete config.headers['Content-Type'];
        }
        try {
            const response = await fetch(url, config);
            console.log("STATUS:", response.status);
            const data = await response.json();
            if (!response.ok) {
                throw { status: response.status, ...data };
            }
            return data;
        } catch (error) {
            if (error.message === 'Failed to fetch') {
                throw { success: false, message: 'Unable to connect to server. Please ensure the backend is running.' };
            }
            throw error;
        }
    },

    get(endpoint) { return this.request(endpoint); },
    post(endpoint, body) {
        const opts = { method: 'POST' };
        if (body instanceof FormData) { opts.body = body; }
        else { opts.body = JSON.stringify(body); }
        return this.request(endpoint, opts);
    },
    put(endpoint, body) {
        const opts = { method: 'PUT' };
        if (body instanceof FormData) { opts.body = body; }
        else { opts.body = JSON.stringify(body); }
        return this.request(endpoint, opts);
    },
    delete(endpoint) { return this.request(endpoint, { method: 'DELETE' }); },
};

/* ---- Session Management ---- */
const session = {
    set(user) { localStorage.setItem('ems_user', JSON.stringify(user)); },
    get() {
        const d = localStorage.getItem('ems_user');
        return d ? JSON.parse(d) : null;
    },
    clear() { localStorage.removeItem('ems_user'); },
    isLoggedIn() { return !!this.get(); },
    isAdmin() { const u = this.get(); return u && u.role === 'ADMIN'; },
    requireAuth() {
        if (!this.isLoggedIn()) { window.location.href = 'index.html'; return false; }
        return true;
    },
};

/* ---- Toast Notifications ---- */
function showToast(message, type = 'info') {
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    const icons = { success: '✅', error: '❌', warning: '⚠️', info: 'ℹ️' };
    toast.innerHTML = `<span>${icons[type] || ''}</span><span>${message}</span>`;
    container.appendChild(toast);
    setTimeout(() => {
        toast.classList.add('removing');
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

/* ---- Utility Functions ---- */
function formatDate(dateStr) {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
}

function formatTime(timeStr) {
    if (!timeStr) return '';
    const [h, m] = timeStr.split(':');
    const hr = parseInt(h);
    const ampm = hr >= 12 ? 'PM' : 'AM';
    const hr12 = hr % 12 || 12;
    return `${hr12}:${m} ${ampm}`;
}

function truncate(str, len = 100) {
    if (!str) return '';
    return str.length > len ? str.substring(0, len) + '...' : str;
}

function getInitials(name) {
    if (!name) return '?';
    return name.split(' ').map(w => w[0]).join('').substring(0, 2).toUpperCase();
}

function setupAdminUI() {
    if (session.isAdmin()) {
        document.querySelectorAll('.admin-only').forEach(el => el.style.display = 'inline-flex');
    }
}

function setupProfileDropdown() {
    const user = session.get();
    if (!user) return;
    const nameEl = document.getElementById('profileName');
    const roleEl = document.getElementById('profileRole');
    const avatarEl = document.getElementById('profileAvatar');
    if (nameEl) nameEl.textContent = user.name;
    if (roleEl) roleEl.textContent = user.role;
    if (avatarEl) avatarEl.textContent = getInitials(user.name);

    const btn = document.querySelector('.profile-btn');
    const menu = document.querySelector('.dropdown-menu');
    if (btn && menu) {
        btn.addEventListener('click', (e) => {
            e.stopPropagation();
            menu.classList.toggle('show');
        });
        document.addEventListener('click', () => menu.classList.remove('show'));
    }
}

function logout() {
    session.clear();
    window.location.href = 'index.html';
}
