import { useState } from 'react'
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
  const [token, setToken] = useState(true);
 
  return (
    <AuthProvider>
      <Router>
        <Toaster position="top-right" />
        <Routes>
          <Route path="/" element={
            token ? <Dashboard /> : <Navigate to="/login" />
          } />
          <Route path="/login" element={
            token ? <Navigate to="/" /> : <Login onLogin={setToken} />
          } />
          <Route path="/register" element={
            token ? <Navigate to="/" /> : <Register />
          } />
          <Route path="*" element={<Navigate to={token ? '/' : '/login'} />} />
        </Routes>
      </Router>
    </AuthProvider>
  )
}

export default App
