const API_BASE = '';

const Api = {
  async request(path, options = {}) {
    const res = await fetch(API_BASE + path, {
      credentials: 'include',
      headers: { 'Content-Type': 'application/json', ...options.headers },
      ...options
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({ message: res.statusText }));
      throw new Error(err.message || err.error || 'Request failed');
    }
    if (res.status === 204) return null;
    return res.json();
  },

  getCourses(search, category) {
    const params = new URLSearchParams();
    if (search) params.set('search', search);
    if (category && category !== 'all') params.set('category', category);
    const q = params.toString();
    return this.request('/api/courses' + (q ? '?' + q : ''));
  },

  getFeatured() { return this.request('/api/courses/featured'); },
  getCourse(id) { return this.request('/api/courses/' + id); },

  signup(data) {
    return this.request('/api/auth/signup', { method: 'POST', body: JSON.stringify(data) });
  },

  login(data) {
    return this.request('/api/auth/login', { method: 'POST', body: JSON.stringify(data) });
  },

  logout() { return this.request('/api/auth/logout', { method: 'POST' }); },

  enroll(courseId) {
    return this.request('/api/enrollments', {
      method: 'POST',
      body: JSON.stringify({ courseId: Number(courseId) })
    });
  },

  getMyEnrollments() { return this.request('/api/enrollments/my'); },

  checkEnrollment(courseId) {
    return this.request('/api/enrollments/check/' + courseId);
  },

  getProgress(courseId) { return this.request('/api/progress/' + courseId); },

  updateLesson(courseId, lessonId, completed) {
    return this.request('/api/progress/' + courseId + '/lessons/' + lessonId, {
      method: 'PUT',
      body: JSON.stringify({ completed })
    });
  },

  syncProgress(courseId, completedLessonIds) {
    return this.request('/api/progress/' + courseId + '/sync', {
      method: 'PUT',
      body: JSON.stringify(completedLessonIds)
    });
  },

  createCourse(course) {
    return this.request('/api/courses', { method: 'POST', body: JSON.stringify(course) });
  },

  updateCourse(id, course) {
    return this.request('/api/courses/' + id, { method: 'PUT', body: JSON.stringify(course) });
  },

  deleteCourse(id) {
    return this.request('/api/courses/' + id, { method: 'DELETE' });
  }
};
