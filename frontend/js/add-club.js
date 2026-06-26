/* ============================================================
   Add / Edit Club Form Logic
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

    const params = new URLSearchParams(window.location.search);
    const clubId = params.get('id');
    if (clubId) loadClubForEdit(clubId);

    setupLogoUpload();
    setupForm(clubId);
});

function setupLogoUpload() {
    const input = document.getElementById('logoInput');
    const preview = document.getElementById('logoPreview');
    const previewImg = document.getElementById('logoPreviewImg');
    input?.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (ev) => {
                previewImg.src = ev.target.result;
                preview.style.display = 'block';
            };
            reader.readAsDataURL(file);
        }
    });
}

async function loadClubForEdit(id) {
    try {
        const res = await api.get(`/clubs/${id}`);
        const club = res.data;
        document.getElementById('pageTitle').textContent = 'Edit Club';
        document.getElementById('pageSubtitle').textContent = 'Update club information';
        document.getElementById('submitBtn').textContent = 'Update Club';
        document.getElementById('clubName').value = club.clubName;
        document.getElementById('description').value = club.description;
        document.getElementById('mentorName').value = club.mentorName;
        document.getElementById('mentorEmail').value = club.mentorEmail;
        if (club.logoUrl) {
            document.getElementById('logoPreviewImg').src = API_BASE.replace('/api', '') + club.logoUrl;
            document.getElementById('logoPreview').style.display = 'block';
        }
    } catch (err) {
        showToast('Failed to load club data', 'error');
    }
}

function setupForm(clubId) {
    const form = document.getElementById('clubForm');
    form?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const btn = document.getElementById('submitBtn');
        btn.disabled = true;
        btn.innerHTML = '<span class="spinner"></span> Saving...';

        const formData = new FormData();
        formData.append('clubName', document.getElementById('clubName').value.trim());
        formData.append('description', document.getElementById('description').value.trim());
        formData.append('mentorName', document.getElementById('mentorName').value.trim());
        formData.append('mentorEmail', document.getElementById('mentorEmail').value.trim());
        formData.append('createdById', session.get().id);

        const logoFile = document.getElementById('logoInput').files[0];
        if (logoFile) formData.append('logo', logoFile);

        try {
            if (clubId) {
                await api.put(`/clubs/${clubId}`, formData);
                showToast('Club updated successfully!', 'success');
            } else {
                await api.post('/clubs', formData);
                showToast('Club created successfully!', 'success');
            }
            setTimeout(() => window.location.href = 'home.html', 1200);
        } catch (err) {
            showToast(err.message || 'Failed to save club', 'error');
            btn.disabled = false;
            btn.textContent = clubId ? 'Update Club' : 'Create Club';
        }
    });
}
