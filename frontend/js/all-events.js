/* ============================================================
   All Events Page Logic — With Tab Filtering
   ============================================================ */
document.addEventListener('DOMContentLoaded', () => {
    if (!session.requireAuth()) return;
    setupProfileDropdown();
    setupAdminUI();
    loadTabs();
    loadAllEvents();
});

let allClubs = [];

async function loadTabs() {
    const tabsContainer = document.getElementById('eventTabs');
    try {
        const res = await api.get('/clubs');
        allClubs = res.data || [];
        let tabsHTML = '<button class="tab-btn active" data-club="all" onclick="filterByClub(\'all\')">All</button>';
        allClubs.forEach(club => {
            tabsHTML += `<button class="tab-btn" data-club="${club.id}" onclick="filterByClub(${club.id})">${club.clubName}</button>`;
        });
        tabsContainer.innerHTML = tabsHTML;
    } catch (err) {
        console.error('Failed to load club tabs:', err);
    }
}

async function loadAllEvents(clubId = null) {
    const grid = document.getElementById('allEventsGrid');
    grid.innerHTML = '<div class="loading-overlay"><div class="spinner spinner-lg"></div><p>Loading events...</p></div>';
    try {
        let endpoint = '/events/upcoming';
        if (clubId && clubId !== 'all') {
            endpoint = `/events?clubId=${clubId}&status=UPCOMING`;
        }
        const res = await api.get(endpoint);
        const events = res.data;
        if (!events || events.length === 0) {
            grid.innerHTML = '<div class="empty-state"><h3>No upcoming events</h3><p>Check back later!</p></div>';
            return;
        }
        grid.innerHTML = events.map((ev, i) => `
            <div class="event-card card animate-in" style="animation-delay:${i * 0.04}s">
                <div class="card-body">
                    <div class="event-card-header">
                        <h3>${ev.eventName}</h3>
                        <span class="badge badge-primary">${ev.clubName}</span>
                    </div>
                    <div class="event-meta">
                        <span class="event-meta-item">📅 ${formatDate(ev.eventDate)}</span>
                        <span class="event-meta-item">⏰ ${formatTime(ev.eventTime)}</span>
                        <span class="event-meta-item">📍 ${ev.location}</span>
                    </div>
                    <p class="event-desc">${truncate(ev.description, 120)}</p>
                    <div class="event-card-footer">
                        <span class="reg-count">👥 ${ev.registrationCount || 0} registered</span>
                        <div style="display:flex;gap:.5rem">
                            <a href="event-detail.html?id=${ev.id}" class="btn btn-sm btn-secondary">View</a>
                            <a href="${ev.registrationLink}" target="_blank" class="btn btn-sm btn-primary">Register</a>
                        </div>
                    </div>
                </div>
            </div>
        `).join('');
    } catch (err) {
        grid.innerHTML = `<div class="empty-state"><h3>Error</h3><p>${err.message}</p></div>`;
    }
}

function filterByClub(clubId) {
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    document.querySelector(`.tab-btn[data-club="${clubId}"]`)?.classList.add('active');
    loadAllEvents(clubId);
}
