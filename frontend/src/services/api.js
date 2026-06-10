import axios from 'axios';

const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080',
  headers: { 'Content-Type': 'application/json' },
});

// Attach JWT token to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle 401/403 globally
api.interceptors.response.use(
  (res) => res,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.clear();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth
export const authAPI = {
  register: (data) => api.post('/api/auth/register', data),
  login: (data) => api.post('/api/auth/login', data),
};

// Public
export const classroomAPI = {
  getAll: () => api.get('/api/classrooms'),
};

// Teacher
export const teacherAPI = {
  getStudents: () => api.get('/api/teacher/students'),
  upsertGrade: (data) => api.post('/api/teacher/grades', data),
};

// Student
export const studentAPI = {
  getMyGrades: () => api.get('/api/student/grades'),
};

export default api;
