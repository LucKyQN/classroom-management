import React, { useState, useEffect } from 'react';
import {
  Container, Typography, Table, TableBody, TableCell, TableHead,
  TableRow, Paper, Button, TextField, Dialog, DialogTitle,
  DialogContent, DialogActions, Alert, Box, Chip, AppBar, Toolbar, IconButton
} from '@mui/material';
import { Logout, School } from '@mui/icons-material';
import { useDispatch, useSelector } from 'react-redux';
import { teacherAPI } from '../services/api';
import { logout } from '../store/authSlice';
import { useNavigate } from 'react-router-dom';

export default function TeacherPage() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { user } = useSelector((s) => s.auth);
  const [students, setStudents] = useState([]);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [dialog, setDialog] = useState({ open: false, student: null });
  const [gradeForm, setGradeForm] = useState({ subject: '', score: '', comment: '' });

  useEffect(() => {
    teacherAPI.getStudents()
      .then(res => setStudents(res.data.data || []))
      .catch(() => setError('Không thể tải danh sách học sinh'));
  }, []);

  const openGradeDialog = (student) => {
    setDialog({ open: true, student });
    setGradeForm({ subject: '', score: '', comment: '' });
    setError('');
  };

  const handleGradeSubmit = async () => {
    try {
      await teacherAPI.upsertGrade({
        studentId: dialog.student.id,
        subject: gradeForm.subject,
        score: parseFloat(gradeForm.score),
        comment: gradeForm.comment,
      });
      setSuccess(`Đã cập nhật điểm cho ${dialog.student.fullName}`);
      setDialog({ open: false, student: null });
    } catch (err) {
      setError(err.response?.data?.message || 'Cập nhật điểm thất bại');
    }
  };

  const handleLogout = () => { dispatch(logout()); navigate('/login'); };

  return (
    <>
      <AppBar position="static">
        <Toolbar>
          <School sx={{ mr: 1 }} />
          <Typography variant="h6" flexGrow={1}>Giáo viên: {user?.fullName}</Typography>
          <IconButton color="inherit" onClick={handleLogout}><Logout /></IconButton>
        </Toolbar>
      </AppBar>

      <Container sx={{ mt: 4 }}>
        <Typography variant="h5" mb={2}>Danh sách học sinh</Typography>

        {success && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>{success}</Alert>}
        {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}

        <Paper>
          <Table>
            <TableHead>
              <TableRow sx={{ bgcolor: 'primary.main' }}>
                <TableCell sx={{ color: 'white' }}>Họ tên</TableCell>
                <TableCell sx={{ color: 'white' }}>Email</TableCell>
                <TableCell sx={{ color: 'white' }}>Ngày sinh</TableCell>
                <TableCell sx={{ color: 'white' }}>Lớp</TableCell>
                <TableCell sx={{ color: 'white' }}>Thao tác</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {students.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={5} align="center">Chưa có học sinh trong lớp</TableCell>
                </TableRow>
              ) : students.map((s) => (
                <TableRow key={s.id} hover>
                  <TableCell>{s.fullName}</TableCell>
                  <TableCell>{s.email}</TableCell>
                  <TableCell>{s.dateOfBirth || '—'}</TableCell>
                  <TableCell><Chip label={s.classroomName || '—'} size="small" /></TableCell>
                  <TableCell>
                    <Button variant="outlined" size="small" onClick={() => openGradeDialog(s)}>
                      Nhập điểm
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Paper>
      </Container>

      <Dialog open={dialog.open} onClose={() => setDialog({ open: false, student: null })} maxWidth="xs" fullWidth>
        <DialogTitle>Nhập điểm - {dialog.student?.fullName}</DialogTitle>
        <DialogContent>
          <Box display="flex" flexDirection="column" gap={2} mt={1}>
            <TextField label="Môn học" required fullWidth value={gradeForm.subject}
              onChange={(e) => setGradeForm({ ...gradeForm, subject: e.target.value })} />
            <TextField label="Điểm (0 - 10)" type="number" required fullWidth
              inputProps={{ min: 0, max: 10, step: 0.1 }}
              value={gradeForm.score}
              onChange={(e) => setGradeForm({ ...gradeForm, score: e.target.value })} />
            <TextField label="Nhận xét" multiline rows={2} fullWidth value={gradeForm.comment}
              onChange={(e) => setGradeForm({ ...gradeForm, comment: e.target.value })} />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialog({ open: false, student: null })}>Hủy</Button>
          <Button variant="contained" onClick={handleGradeSubmit}>Lưu điểm</Button>
        </DialogActions>
      </Dialog>
    </>
  );
}
