import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';

import { Login } from './features/auth/pages/Login';
import { Register } from './features/auth/pages/Register';
import { Dashboard } from './features/dashboard/pages/Dashboard';
import { UserCreate } from './features/usersManager/pages/UserCreate';
import { UsersList } from './features/usersManager/pages/UsersList';
import { UserEdit } from './features/usersManager/pages/UserEdit';

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
          <Route
            path="/"
            element={
              <PrivateRoute>
                <MainLayout />
              </PrivateRoute>
            }
          >
            <Route index element={<Dashboard />} />
            <Route path="users/new" element={<UserCreate />} />
            <Route path='users/all' element={<UsersList />} />
            <Route path='users/edit/:id' element={<UserEdit />} />
            {/* aquí más rutas protegidas futuras */}
          </Route>

            <Route
              path="/login"
              element={
                <PublicRoute>
                  <Login />
                </PublicRoute>
              }
            />
            <Route
              path="/register"
              element={
                <PublicRoute>
                  <Register />
                </PublicRoute>
              }
            />

          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
