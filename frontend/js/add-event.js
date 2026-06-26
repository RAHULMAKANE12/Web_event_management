/* ============================================================
   Add / Edit Event Form Logic
   ============================================================ */
document.addEventListener('DOMContentLoaded', () => {
    if (!session.requireAuth()) return;
    if (!session.isAdmin()) {
        showToast('Access denied. Admin only.', 'error');
        setTimeout(() => window.location.href = 'home.html', 1500);
        return;
    }
    setupProfileDropdown();
    setupAdminUI();
    loadClubsDropdown();

    const params = new URLSearchParams(window.location.search);
    const eventId = params.get('id');
    if (eventId) loadEventForEdit(eventId);

    setupPrizeRows();
    setupForm(eventId);
});

async function loadClubsDropdown() {
    const select = document.getElementById('clubId');
    try {
        const res = await api.get('/clubs');
        const clubs = res.data || [];
        clubs.forEach(club => {
            const opt = document.createElement('option');
            opt.value = club.id;
            opt.textContent = club.clubName;
            select.appendChild(opt);
        });
    } catch (err) {
        console.error('Failed to load clubs:', err);
    }
}

async function loadEventForEdit(id) {
    try {
        const res = await api.get(`/events/${id}`);
        const ev = res.data;
        document.getElementById('pageTitle').textContent = 'Edit Event';
        document.getElementById('pageSubtitle').textContent = 'Update event information';
        document.getElementById('submitBtn').textContent = 'Update Event';
        document.getElementById('eventName').value = ev.eventName;
        document.getElementById('eventDate').value = ev.eventDate;
        document.getElementById('eventTime').value = ev.eventTime;
        document.getElementById('location').value = ev.location;
        document.getElementById('description').value = ev.description;
        document.getElementById('guestSpeaker').value = ev.guestSpeaker || '';
        document.getElementById('registrationLink').value = ev.registrationLink;

        // Set club dropdown after clubs are loaded
        setTimeout(() => {
            document.getElementById('clubId').value = ev.clubId;
        }, 500);

        // Load prizes
        if (ev.prizeAllocation) {
            try {
                const prizes = JSON.parse(ev.prizeAllocation);
                const container = document.getElementById('prizeRows');
                container.innerHTML = '';
                prizes.forEach(p => addPrizeRow(p.position, p.award));
            } catch {}
        }
    } catch (err) {
        showToast('Failed to load event', 'error');
    }
}

function setupPrizeRows() {
    // Add initial row
    addPrizeRow('1st', '');
    document.getElementById('addPrizeBtn')?.addEventListener('click', () => addPrizeRow('', ''));
}

function addPrizeRow(position = '', award = '') {
    const container = document.getElementById('prizeRows');
    const row = document.createElement('div');
    row.className = 'prize-row';
    row.innerHTML = `
        <input type="text" class="form-control prize-position-input" placeholder="e.g. 1st" value="${position}">
        <input type="text" class="form-control prize-award-input" placeholder="e.g. Trophy + ₹5000" value="${award}">
        <button type="button" class="remove-prize" onclick="this.parentElement.remove()">✕</button>
    `;
    container.appendChild(row);
}

function setupForm(eventId) {
    const form = document.getElementById('eventForm');
    form?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const btn = document.getElementById('submitBtn');
        btn.disabled = true;
        btn.innerHTML = '<span class="spinner"></span> Saving...';

        // Collect prize data
        const prizeRows = document.querySelectorAll('.prize-row');
        const prizes = [];
        prizeRows.forEach(row => {
            const pos = row.querySelector('.prize-position-input').value.trim();
            const award = row.querySelector('.prize-award-input').value.trim();
            if (pos && award) prizes.push({ position: pos, award: award });
        });

        const data = {
            eventName: document.getElementById('eventName').value.trim(),
            clubId: parseInt(document.getElementById('clubId').value),
            eventDate: document.getElementById('eventDate').value,
            eventTime: document.getElementById('eventTime').value,
            location: document.getElementById('location').value.trim(),
            description: document.getElementById('description').value.trim(),
            guestSpeaker: document.getElementById('guestSpeaker').value.trim() || null,
            registrationLink: document.getElementById('registrationLink').value.trim(),
            prizeAllocation: JSON.stringify(prizes),
        };

        try {
            if (eventId) {
                await api.put(`/events/${eventId}`, data);
                showToast('Event updated!', 'success');
            } else {
                await api.post(`/events?createdById=${session.get().id}`, data);
                showToast('Event created!', 'success');
            }
            setTimeout(() => window.location.href = 'home.html', 1200);
        } catch (err) {
            showToast(err.message || 'Failed to save event', 'error');
            btn.disabled = false;
            btn.textContent = eventId ? 'Update Event' : 'Create Event';
        }
    });
}
