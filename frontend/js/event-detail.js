/* ============================================================
   Event Detail Page Logic
   ============================================================ */
document.addEventListener('DOMContentLoaded', () => {
    if (!session.requireAuth()) return;
    setupProfileDropdown();
    setupAdminUI();

    const params = new URLSearchParams(window.location.search);
    const eventId = params.get('id');
    if (!eventId) { window.location.href = 'home.html'; return; }

    loadEventDetail(eventId);
});

async function loadEventDetail(id) {
    try {
        const res = await api.get(`/events/${id}`);
        const ev = res.data;
        document.getElementById('eventName').textContent = ev.eventName;
        document.getElementById('eventClub').textContent = ev.clubName;
        document.getElementById('eventClub').href = `club-detail.html?id=${ev.clubId}`;
        document.getElementById('eventStatus').textContent = ev.status;
        document.getElementById('eventStatus').className = `badge badge-${ev.status === 'UPCOMING' ? 'success' : 'warning'}`;
        document.getElementById('eventDescription').textContent = ev.description;
        document.getElementById('eventDate').textContent = formatDate(ev.eventDate);
        document.getElementById('eventTime').textContent = formatTime(ev.eventTime);
        document.getElementById('eventLocation').textContent = ev.location;
        document.getElementById('eventSpeaker').textContent = ev.guestSpeaker || 'N/A';
        document.getElementById('regCount').textContent = `${ev.registrationCount || 0} students registered`;
        document.title = `${ev.eventName} — Spoorthy EMS`;

        // Registration link
        const regBtn = document.getElementById('registerBtn');
        if (regBtn) {
            if (ev.status === 'UPCOMING') {
                // Check if already registered
                const user = session.get();
                try {
                    const check = await api.get(`/registrations/check?eventId=${id}&userId=${user.id}`);
                    if (check.data.isRegistered) {
                        regBtn.textContent = '✅ Already Registered';
                        regBtn.disabled = true;
                        regBtn.classList.remove('btn-primary');
                        regBtn.classList.add('btn-secondary');
                    } else {
                        regBtn.addEventListener('click', () => registerForEvent(id, ev.registrationLink));
                    }
                } catch { regBtn.addEventListener('click', () => registerForEvent(id, ev.registrationLink)); }
            } else {
                regBtn.textContent = 'Registration Closed';
                regBtn.disabled = true;
                regBtn.classList.remove('btn-primary');
                regBtn.classList.add('btn-secondary');
            }
        }

        // Prize allocation
        const prizeList = document.getElementById('prizeList');
        if (ev.prizeAllocation) {
            try {
                const prizes = JSON.parse(ev.prizeAllocation);
                if (Array.isArray(prizes) && prizes.length > 0) {
                    const colors = ['gold', 'silver', 'bronze'];
                    prizeList.innerHTML = prizes.map((p, i) => `
                        <li class="prize-item">
                            <span class="prize-position ${colors[i] || 'silver'}">${p.position || (i + 1)}</span>
                            <span class="prize-award">${p.award}</span>
                        </li>
                    `).join('');
                } else { document.getElementById('prizeSection').style.display = 'none'; }
            } catch { document.getElementById('prizeSection').style.display = 'none'; }
        } else { document.getElementById('prizeSection').style.display = 'none'; }

        // Admin: view registered students
        if (session.isAdmin()) {
            document.getElementById('viewStudentsBtn')?.addEventListener('click', () => loadRegisteredStudents(id));
            document.getElementById('editEventBtn')?.addEventListener('click', () => {
                window.location.href = `add-event.html?id=${id}`;
            });
            document.getElementById('deleteEventBtn')?.addEventListener('click', async () => {
                if (!confirm(`Delete "${ev.eventName}"?`)) return;
                try {
                    await api.delete(`/events/${id}`);
                    showToast('Event deleted', 'success');
                    setTimeout(() => window.location.href = 'home.html', 1000);
                } catch (err) { showToast(err.message, 'error'); }
            });
        }
    } catch (err) {
        showToast(err.message || 'Failed to load event', 'error');
    }
}

async function registerForEvent(eventId, regLink) {
    const user = session.get();
    try {
        await api.post('/registrations', { eventId: parseInt(eventId), userId: user.id });
        showToast('Registration successful! Check your email for confirmation.', 'success');
        // Open external registration form
        if (regLink) window.open(regLink, '_blank');
        setTimeout(() => location.reload(), 1500);
    } catch (err) {
        showToast(err.message || 'Registration failed', 'error');
    }
}

async function loadRegisteredStudents(eventId) {
    const overlay = document.getElementById('studentsModal');
    const body = document.getElementById('studentsTableBody');
    overlay.classList.add('show');
    body.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:2rem"><div class="spinner" style="margin:0 auto"></div></td></tr>';
    try {
        const res = await api.get(`/registrations/event/${eventId}`);
        const regs = res.data;
        if (!regs || regs.length === 0) {
            body.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:2rem;color:var(--text-muted)">No registrations yet</td></tr>';
            return;
        }
        body.innerHTML = regs.map((r, i) => `
            <tr>
                <td>${i + 1}</td>
                <td>${r.userName}</td>
                <td>${r.userEmail}</td>
                <td>${r.userRollNumber}</td>
                <td>${r.userMobile}</td>
            </tr>
        `).join('');
    } catch (err) {
        body.innerHTML = `<tr><td colspan="5" style="text-align:center;color:var(--danger)">${err.message}</td></tr>`;
    }
}

function closeModal() {
    document.getElementById('studentsModal')?.classList.remove('show');
}
