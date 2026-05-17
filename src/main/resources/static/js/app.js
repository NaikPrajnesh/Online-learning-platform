function renderNavbar() {
  const user = Storage.getUser();
  const navUser = document.getElementById('nav-user');
  const navAuth = document.getElementById('nav-auth');
  if (!navUser || !navAuth) return;

  if (user) {
    navUser.innerHTML = `
      <span class="badge">Hi, ${escapeHtml(user.fullName || user.email)}</span>
      ${user.role === 'ADMIN' ? '<a href="/admin.html">Admin</a>' : ''}
      <button class="btn btn-ghost btn-sm" id="logout-btn">Logout</button>`;
    navAuth.innerHTML = '';
    document.getElementById('logout-btn')?.addEventListener('click', async () => {
      try { await Api.logout(); } catch (_) {}
      Storage.setUser(null);
      window.location.href = '/index.html';
    });
  } else {
    navUser.innerHTML = '';
    navAuth.innerHTML = `
      <a href="/login.html" class="btn btn-ghost btn-sm">Login</a>
      <a href="/signup.html" class="btn btn-primary btn-sm">Sign Up</a>`;
  }
}

function escapeHtml(text) {
  const d = document.createElement('div');
  d.textContent = text || '';
  return d.innerHTML;
}

function renderCourseCard(course, options = {}) {
  const enrolled = Storage.isEnrolled(course.id);
  const percent = Storage.getPercent(course.id, (course.lessons || []).length);
  const continueBtn = options.continueLink
    ? `<a href="/course-detail.html?id=${course.id}" class="btn btn-primary btn-sm" style="margin-top:0.75rem;width:100%">Continue (${percent}%)</a>`
    : '';
  return `
    <article class="course-card" data-id="${course.id}">
      <a href="/course-detail.html?id=${course.id}">
        <img src="${course.imageUrl}" alt="${escapeHtml(course.title)}" loading="lazy">
      </a>
      <div class="course-card-body">
        <span class="badge">${escapeHtml(course.category)}</span>
        <h3><a href="/course-detail.html?id=${course.id}">${escapeHtml(course.title)}</a></h3>
        <p>${escapeHtml(course.shortDescription)}</p>
        <div class="course-meta">
          <span>⏱ ${escapeHtml(course.duration)}</span>
          <span>👤 ${escapeHtml(course.instructor)}</span>
        </div>
        ${enrolled ? `
          <div class="progress-label"><span>Progress</span><span>${percent}%</span></div>
          <div class="progress-bar-wrap"><div class="progress-bar-fill" style="width:${percent}%"></div></div>
        ` : ''}
        <div style="display:flex;gap:0.5rem;margin-top:auto">
          <a href="/course-detail.html?id=${course.id}" class="btn btn-ghost btn-sm" style="flex:1">View</a>
          ${options.showEnroll !== false ? `
            <button class="btn btn-primary btn-sm enroll-btn" data-id="${course.id}" ${enrolled ? 'disabled' : ''}>
              ${enrolled ? 'Enrolled' : 'Enroll'}
            </button>` : ''}
        </div>
        ${continueBtn}
      </div>
    </article>`;
}

function bindEnrollButtons(container) {
  container?.querySelectorAll('.enroll-btn').forEach(btn => {
    btn.addEventListener('click', async (e) => {
      e.preventDefault();
      const courseId = btn.dataset.id;
      if (!Storage.getUser()) {
        window.location.href = '/login.html?redirect=' + encodeURIComponent(window.location.pathname);
        return;
      }
      Storage.addEnrollment(courseId);
      try {
        await Api.enroll(courseId);
        const completed = Storage.getProgress(courseId);
        await Api.syncProgress(courseId, completed).catch(() => {});
      } catch (err) {
        console.warn('Backend enroll:', err.message);
      }
      btn.textContent = 'Enrolled';
      btn.disabled = true;
      if (typeof onEnrollSuccess === 'function') onEnrollSuccess(courseId);
    });
  });
}

function initMobileNav() {
  const toggle = document.getElementById('menu-toggle');
  const links = document.getElementById('nav-links');
  toggle?.addEventListener('click', () => links?.classList.toggle('open'));
}

function initThemeToggle() {
  const btn = document.getElementById('theme-toggle');
  if (!btn) return;
  const updateIcon = () => {
    btn.textContent = Storage.getTheme() === 'dark' ? '☀️' : '🌙';
  };
  updateIcon();
  btn.addEventListener('click', () => {
    const next = Storage.getTheme() === 'dark' ? 'light' : 'dark';
    Storage.setTheme(next);
    updateIcon();
  });
}

function generateCertificate(course, user, percent) {
  const date = new Date().toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' });
  return `
    <div class="certificate-preview" id="certificate">
      <h3>Certificate of Completion</h3>
      <p style="margin:1rem 0;font-size:1.1rem">This certifies that</p>
      <p style="font-size:1.4rem;font-weight:700">${escapeHtml(user?.fullName || 'Student')}</p>
      <p style="margin:1rem 0">has successfully completed</p>
      <p style="font-size:1.2rem;font-weight:600;color:var(--primary)">${escapeHtml(course.title)}</p>
      <p style="margin-top:1rem;color:var(--muted)">${percent}% completed · ${date}</p>
      <p style="margin-top:0.5rem;font-size:0.85rem">EduLearn Platform</p>
    </div>`;
}

function downloadCertificate() {
  const el = document.getElementById('certificate');
  if (!el) return;
  const printWin = window.open('', '_blank');
  printWin.document.write(`
    <html><head><title>Certificate</title>
    <style>body{font-family:Georgia,serif;text-align:center;padding:60px}
    h1{color:#854d0e} .meta{color:#666;margin-top:20px}</style></head>
    <body>${el.innerHTML}</body></html>`);
  printWin.document.close();
  printWin.print();
}

document.addEventListener('DOMContentLoaded', () => {
  renderNavbar();
  initMobileNav();
  initThemeToggle();
});
