const STORAGE_KEYS = {
  user: 'elearn_user',
  enrollments: 'elearn_enrollments',
  progress: 'elearn_progress',
  theme: 'elearn_theme'
};

const Storage = {
  getUser() {
    try {
      return JSON.parse(localStorage.getItem(STORAGE_KEYS.user));
    } catch { return null; }
  },

  setUser(user) {
    if (user) localStorage.setItem(STORAGE_KEYS.user, JSON.stringify(user));
    else localStorage.removeItem(STORAGE_KEYS.user);
  },

  getEnrollments() {
    try {
      return JSON.parse(localStorage.getItem(STORAGE_KEYS.enrollments)) || [];
    } catch { return []; }
  },

  setEnrollments(ids) {
    localStorage.setItem(STORAGE_KEYS.enrollments, JSON.stringify([...new Set(ids)]));
  },

  addEnrollment(courseId) {
    const ids = this.getEnrollments();
    if (!ids.includes(Number(courseId))) {
      ids.push(Number(courseId));
      this.setEnrollments(ids);
    }
  },

  isEnrolled(courseId) {
    return this.getEnrollments().includes(Number(courseId));
  },

  getProgress(courseId) {
    try {
      const all = JSON.parse(localStorage.getItem(STORAGE_KEYS.progress)) || {};
      return all[String(courseId)] || [];
    } catch { return []; }
  },

  setProgress(courseId, completedLessonIds) {
    const all = JSON.parse(localStorage.getItem(STORAGE_KEYS.progress) || '{}');
    all[String(courseId)] = completedLessonIds.map(Number);
    localStorage.setItem(STORAGE_KEYS.progress, JSON.stringify(all));
  },

  toggleLesson(courseId, lessonId) {
    const completed = this.getProgress(courseId);
    const id = Number(lessonId);
    const idx = completed.indexOf(id);
    if (idx >= 0) completed.splice(idx, 1);
    else completed.push(id);
    this.setProgress(courseId, completed);
    return completed;
  },

  getPercent(courseId, totalLessons) {
    if (!totalLessons) return 0;
    const done = this.getProgress(courseId).length;
    return Math.round((done / totalLessons) * 100);
  },

  getTheme() {
    return localStorage.getItem(STORAGE_KEYS.theme) || 'light';
  },

  setTheme(theme) {
    localStorage.setItem(STORAGE_KEYS.theme, theme);
    document.documentElement.setAttribute('data-theme', theme);
  }
};

document.documentElement.setAttribute('data-theme', Storage.getTheme());
