/* ============================================================
   Club Detail Page Logic
   ============================================================ */
document.addEventListener('DOMContentLoaded', () => {
    if (!session.requireAuth()) return;
    setupProfileDropdown();
    setupAdminUI();

    const params = new URLSearchParams(window.location.search);
    const clubId = params.get('id');
    if (!clubId) { window.location.href = 'home.html'; return; }

    loadClubDetail(clubId);
    loadClubEvents(clubId, 'UPCOMING');

    document.getElementById('upcomingTab')?.addEventListener('click', () => {
        setActiveTab('upcomingTab');
        loadClubEvents(clubId, 'UPCOMING');
    });
    document.getElementById('pastTab')?.addEventListener('click', () => {
        setActiveTab('pastTab');
        loadClubEvents(clubId, 'COMPLETED');
    });
});

async function loadClubDetail(id) {
    try {
        const res = await api.get(`/clubs/${id}`);
        const club = res.data;
        document.getElementById('clubName').textContent = club.clubName;
        document.getElementById('clubMentor').textContent = `Mentor: ${club.mentorName} • ${club.mentorEmail}`;
        document.getElementById('clubDescription').textContent = club.description;
        document.title = `${club.clubName} — Spoorthy EMS`;

        const logo = document.getElementById('clubLogo');
        if (club.logoUrl) {
            logo.innerHTML = `<img src="${API_BASE.replace('/api', '')}${club.logoUrl}" alt="${club.clubName}">`;
        } else {
            logo.innerHTML = `<span class="fallback">${getInitials(club.clubName)}</span>`;
        }

        // Admin actions
        if (session.isAdmin()) {
            document.getElementById('editClubBtn')?.addEventListener('click', () => {
                window.location.href = `add-club.html?id=${id}`;
            });
            document.getElementById('deleteClubBtn')?.addEventListener('click', async () => {
                if (!confirm(`Are you sure you want to delete "${club.clubName}"? This will also delete all its events.`)) return;
                try {
                    await api.delete(`/clubs/${id}`);
                    showToast('Club deleted successfully', 'success');
                    setTimeout(() => window.location.href = 'home.html', 1000);
                } catch (err) {
                    showToast(err.message || 'Failed to delete club', 'error');
                }
            });
        }
    } catch (err) {
        showToast(err.message || 'Failed to load club', 'error');
    }
}

async function loadClubEvents(clubId, status) {
    const grid = document.getElementById('clubEventsGrid');
    grid.innerHTML = '<div class="loading-overlay"><div class="spinner spinner-lg"></div></div>';
    try {
        const res = await api.get(`/events?clubId=${clubId}&status=${status}`);
        const events = res.data;
        if (!events || events.length === 0) {
            grid.innerHTML = `<div class="empty-state"><h3>No ${status.toLowerCase()} events</h3><p>Check back later for updates.</p></div>`;
            return;
        }
        grid.innerHTML = events.map((ev, i) => `
            <a href="event-detail.html?id=${ev.id}" class="event-card card animate-in" style="text-decoration:none;color:inherit;animation-delay:${i*0.05}s">
                <div class="card-body">
                    <div class="event-card-header">
                        <h3>${ev.eventName}</h3>
                        <span class="badge ${status === 'UPCOMING' ? 'badge-success' : 'badge-warning'}">${status}</span>
                    </div>
                    <div class="event-meta">
                        <span class="event-meta-item">📅 ${formatDate(ev.eventDate)}</span>
                        <span class="event-meta-item">⏰ ${formatTime(ev.eventTime)}</span>
                        <span class="event-meta-item">📍 ${ev.location}</span>
                    </div>
                    <p class="event-desc">${truncate(ev.description, 120)}</p>
                    <div class="reg-count" style="margin-top:.5rem">👥 ${ev.registrationCount || 0} registered</div>
                </div>
            </a>
        `).join('');
    } catch (err) {
        grid.innerHTML = `<div class="empty-state"><h3>Could not load events</h3><p>${err.message}</p></div>`;
    }
}

function setActiveTab(id) {
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    document.getElementById(id)?.classList.add('active');
}
