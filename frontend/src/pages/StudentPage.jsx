import React, { useState, useEffect } from 'react';
import {
  Container, Typography, Table, TableBody, TableCell, TableHead,
  TableRow, Paper, Alert, AppBar, Toolbar, IconButton, Chip, Box
} from '@mui/material';
import { Logout, MenuBook } from '@mui/icons-material';
import { useDispatch, useSelector } from 'react-redux';
import { studentAPI } from '../services/api';
import { logout } from '../store/authSlice';
import { useNavigate } from 'react-router-dom';

const scoreColor = (score) => {
  if (score >= 8) return 'success';
  if (score >= 5) return 'warning';
  return 'error';
};

export default function StudentPage() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { user } = useSelector((s) => s.auth);
  const [grades, setGrades] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    studentAPI.getMyGrades()
      .then(res => setGrades(res.data.data || []))
      .catch(() => setError('Không thể tải bảng điểm'));
  }, []);

  const handleLogout = () => { dispatch(logout()); navigate('/login'); };

  const avg = grades.length
    ? (grades.reduce((s, g) => s + g.score, 0) / grades.length).toFixed(2)
    : null;

  return (
    <>
      <AppBar position="static" color="secondary">
        <Toolbar>
          <MenuBook sx={{ mr: 1 }} />
          <Typography variant="h6" flexGrow={1}>Học sinh: {user?.fullName}</Typography>
          <IconButton color="inherit" onClick={handleLogout}><Logout /></IconButton>
        </Toolbar>
      </AppBar>

      <Container sx={{ mt: 4 }}>
        <Typography variant="h5" mb={2}>Bảng điểm của tôi</Typography>

        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

        {avg && (
          <Box mb={2}>
            <Chip label={`Điểm trung bình: ${avg}`} color={scoreColor(parseFloat(avg))} />
          </Box>
        )}

        <Paper>
          <Table>
            <TableHead>
              <TableRow sx={{ bgcolor: 'secondary.main' }}>
                <TableCell sx={{ color: 'white' }}>Môn học</TableCell>
                <TableCell sx={{ color: 'white' }}>Điểm</TableCell>
                <TableCell sx={{ color: 'white' }}>Nhận xét</TableCell>
                <TableCell sx={{ color: 'white' }}>Giáo viên</TableCell>
                <TableCell sx={{ color: 'white' }}>Cập nhật</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {grades.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={5} align="center">Chưa có điểm nào</TableCell>
                </TableRow>
              ) : grades.map((g) => (
                <TableRow key={g.id} hover>
                  <TableCell>{g.subject}</TableCell>
                  <TableCell>
                    <Chip label={g.score} color={scoreColor(g.score)} size="small" />
                  </TableCell>
                  <TableCell>{g.comment || '—'}</TableCell>
                  <TableCell>{g.teacherName}</TableCell>
                  <TableCell>{new Date(g.updatedAt).toLocaleDateString('vi-VN')}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Paper>
      </Container>
    </>
  );
}
