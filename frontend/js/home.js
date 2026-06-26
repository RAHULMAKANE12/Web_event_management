/* ============================================================
   Home Page Logic
   ============================================================ */
document.addEventListener('DOMContentLoaded', () => {
    if (!session.requireAuth()) return;
    setupProfileDropdown();
    setupAdminUI();
    loadClubs();
    loadUpcomingEvents();
    setupScrollButtons();
});

async function loadClubs() {
    const grid = document.getElementById('clubsGrid');
    grid.innerHTML = '<div class="loading-overlay"><div class="spinner spinner-lg"></div><p>Loading clubs...</p></div>';
    try {
        const res = await api.get('/clubs');
        const clubs = res.data;
        if (!clubs || clubs.length === 0) {
            grid.innerHTML = '<div class="empty-state"><h3>No clubs yet</h3><p>Clubs will appear here once an admin adds them.</p></div>';
            updateStat('clubCount', 0);
            return;
        }
        updateStat('clubCount', clubs.length);
        grid.innerHTML = clubs.map((club, i) => `
            <a href="club-detail.html?id=${club.id}" class="club-card card animate-in" style="animation-delay:${i * 0.05}s">
                <div class="card-body">
                    <div class="club-logo">
                        ${club.logoUrl
                            ? `<img src="${API_BASE.replace('/api', '')}${club.logoUrl}" alt="${club.clubName}">`
                            : `<span class="fallback">${getInitials(club.clubName)}</span>`}
                    </div>
                    <div class="club-info">
                        <h3>${club.clubName}</h3>
                        <p>${truncate(club.description, 90)}</p>
                        <div class="club-mentor">👤 ${club.mentorName}</div>
                    </div>
                </div>
            </a>
        `).join('');
    } catch (err) {
        grid.innerHTML = `<div class="empty-state"><h3>Could not load clubs</h3><p>${err.message}</p></div>`;
    }
}

async function loadUpcomingEvents() {
    const grid = document.getElementById('eventsGrid');
    grid.innerHTML = '<div class="loading-overlay"><div class="spinner spinner-lg"></div><p>Loading events...</p></div>';
    try {
        const res = await api.get('/events/upcoming');
        const events = res.data;
        if (!events || events.length === 0) {
            grid.innerHTML = '<div class="empty-state"><h3>No upcoming events</h3><p>Events will appear here once an admin creates them.</p></div>';
            updateStat('eventCount', 0);
            return;
        }
        updateStat('eventCount', events.length);
        const display = events.slice(0, 6);
        grid.innerHTML = display.map((ev, i) => renderEventCard(ev, i)).join('');

        const viewAll = document.getElementById('viewAllLink');
        if (events.length > 6 && viewAll) viewAll.style.display = 'flex';
    } catch (err) {
        grid.innerHTML = `<div class="empty-state"><h3>Could not load events</h3><p>${err.message}</p></div>`;
    }
}

function renderEventCard(ev, i = 0) {
    return `
        <div class="event-card card animate-in" style="animation-delay:${i * 0.05}s">
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
                    <div style="display:flex;gap:.5rem;align-items:center;">
                        <a href="event-detail.html?id=${ev.id}" class="btn btn-sm btn-secondary">View Details</a>
                        <a href="${ev.registrationLink}" target="_blank" class="btn btn-sm btn-primary">Register Now</a>
                    </div>
                </div>
            </div>
        </div>`;
}

function setupScrollButtons() {
    const clubsBtn = document.getElementById('exploreClubsBtn');
    const eventsBtn = document.getElementById('exploreEventsBtn');
    if (clubsBtn) clubsBtn.addEventListener('click', (e) => {
        e.preventDefault();
        document.getElementById('clubs')?.scrollIntoView({ behavior: 'smooth' });
    });
    if (eventsBtn) eventsBtn.addEventListener('click', (e) => {
        e.preventDefault();
        document.getElementById('events')?.scrollIntoView({ behavior: 'smooth' });
    });
}

function updateStat(id, val) {
    const el = document.getElementById(id);
    if (el) el.textContent = val;
}
