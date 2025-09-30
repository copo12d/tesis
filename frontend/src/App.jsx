import { BrowserRouter as Router, Routes, Route, Navigate} from 'react-router-dom';
import { Login } from './features/auth/pages/Login';
import { Register } from './features/auth/pages/Register';
import { Toaster } from 'react-hot-toast';
import { Dashboard } from './features/dashboard/pages/Dashboard';
import MainLayout from './layouts/MainLayout';

import PrivateRoute from './routes/PrivateRoute';
import PublicRoute from './routes/PublicRoute';

import AuthProvider from './context/AuthProvider';

function App() {
 
  return (
    <AuthProvider>
      <Router>
        <Toaster position="top-right" />
        <Routes>
          <Route path="/" element={
            <PrivateRoute>
              <MainLayout>
                <Dashboard />
              </MainLayout>
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
