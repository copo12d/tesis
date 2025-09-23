import { BrowserRouter as Router, Routes, Route, Navigate} from 'react-router-dom';
import { Login } from './pages/Login';
import { Register } from './pages/Register';
import { Toaster } from 'react-hot-toast';
import { Dashboard } from './pages/Dashboard';

import PrivateRoute from './routes/PrivateRoute';
import AdminRoute from './routes/AdminRoute';
import PublicRoute from './routes/PublicRoute';

import AuthProvider from './context/AuthProvider';

import './App.css';
function App() {
 
  return (
    <AuthProvider>
      <Router>
        <Toaster position="top-right" />
        <Routes>
          <Route path="/" element={
            <PrivateRoute>
              <Dashboard />
            </PrivateRoute>
          } 
        />
          <Route path="/login" element={
            <PublicRoute>
              <Login />
            </PublicRoute>
          } />
          <Route path="/register" element={
            <PublicRoute>
              <Register />
            </PublicRoute>
          } />
          <Route path="*" element={<Navigate to={'/login'} />} />
        </Routes>
      </Router>
    </AuthProvider>
  )
}

export default App
