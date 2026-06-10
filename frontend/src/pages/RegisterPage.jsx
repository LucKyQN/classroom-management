import React, { useState, useEffect } from 'react';
import {
  Container, Paper, TextField, Button, Typography, MenuItem,
  Select, InputLabel, FormControl, Box, Alert, CircularProgress
} from '@mui/material';
import { useNavigate, Link } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { authAPI, classroomAPI } from '../services/api';
import { setCredentials } from '../store/authSlice';

export default function RegisterPage() {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [classrooms, setClassrooms] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({
    email: '', password: '', fullName: '', dateOfBirth: '',
    role: 'STUDENT', classroomId: ''
  });

  useEffect(() => {
    classroomAPI.getAll().then(res => setClassrooms(res.data.data || []));
  }, []);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const payload = {
        ...form,
        classroomId: form.classroomId || null,
        dateOfBirth: form.dateOfBirth || null,
      };
      const res = await authAPI.register(payload);
      const { token, ...user } = res.data.data;
      dispatch(setCredentials({ token, user }));
      navigate(user.role === 'TEACHER' ? '/teacher' : '/student');
    } catch (err) {
      setError(err.response?.data?.message || 'Đăng ký thất bại');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="sm" sx={{ mt: 8 }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Typography variant="h5" fontWeight="bold" mb={3} textAlign="center">
          Đăng ký tài khoản
        </Typography>

        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

        <Box component="form" onSubmit={handleSubmit} display="flex" flexDirection="column" gap={2}>
          <TextField label="Email" name="email" type="email" required fullWidth value={form.email} onChange={handleChange} />
          <TextField label="Mật khẩu" name="password" type="password" required fullWidth value={form.password} onChange={handleChange} inputProps={{ minLength: 6 }} />
          <TextField label="Họ và tên" name="fullName" required fullWidth value={form.fullName} onChange={handleChange} />
          <TextField label="Ngày sinh" name="dateOfBirth" type="date" fullWidth value={form.dateOfBirth} onChange={handleChange} InputLabelProps={{ shrink: true }} />

          <FormControl fullWidth required>
            <InputLabel>Vai trò</InputLabel>
            <Select name="role" value={form.role} label="Vai trò" onChange={handleChange}>
              <MenuItem value="TEACHER">Giáo viên</MenuItem>
              <MenuItem value="STUDENT">Học sinh</MenuItem>
            </Select>
          </FormControl>

          <FormControl fullWidth>
            <InputLabel>Chọn lớp</InputLabel>
            <Select name="classroomId" value={form.classroomId} label="Chọn lớp" onChange={handleChange}>
              <MenuItem value=""><em>-- Chọn lớp --</em></MenuItem>
              {classrooms.map((c) => (
                <MenuItem key={c.id} value={c.id}>{c.name}</MenuItem>
              ))}
            </Select>
          </FormControl>

          <Button type="submit" variant="contained" size="large" disabled={loading}>
            {loading ? <CircularProgress size={24} /> : 'Đăng ký'}
          </Button>

          <Typography textAlign="center">
            Đã có tài khoản? <Link to="/login">Đăng nhập</Link>
          </Typography>
        </Box>
      </Paper>
    </Container>
  );
}
