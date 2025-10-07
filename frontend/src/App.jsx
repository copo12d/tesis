import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';

import { Login } from './features/auth/pages/Login';
import { Register } from './features/auth/pages/Register';
import { Dashboard } from './features/dashboard/pages/Dashboard';
import { UserCreate } from './features/usersManager/pages/UserCreate';
import { UsersList } from './features/usersManager/pages/UsersList';
import { UserEdit } from './features/usersManager/pages/UserEdit';
import ForgotPassword from './features/auth/pages/ForgotPassword';
import AccountLocked from './features/auth/pages/AccountLocked';
import MobileReportPage from './features/mobile/pages/MobileReportPage';
import MobileCollectorLogin from './features/mobile/pages/MobileCollectorLogin';
import { ContainerTypeList } from "./features/containerType/pages/ContainerTypeList";
import { ContainerTypeCreate } from './features/containerType/pages/ContainerTypeCreate';
import { ContainerTypeEdit } from './features/containerType/pages/ContainerTypeEdit';


import MainLayout from './layouts/MainLayout';

import PrivateRoute from './routes/PrivateRoute';
import PublicRoute from './routes/PublicRoute';
import MobileRoute from './routes/MobileRoute';

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
            <Route path="/container-type/new" element={<ContainerTypeCreate />} />
            <Route path="/container-type/edit/:id" element={<ContainerTypeEdit />} />
            <Route path="/container-type/list" element={<ContainerTypeList />} />
            {/* aquí más rutas protegidas futuras */}
          </Route>
          <Route
            path="/mobile"
            element={
              <MobileRoute>
                <MobileReportPage />
              </MobileRoute>
            }
          />
          <Route
            path="/mobile/login"
            element={
              <MobileRoute>
                <MobileCollectorLogin />
              </MobileRoute>
            }
          />

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
            <Route
              path="/forgot-password"
              element={<ForgotPassword />}
            />
            <Route
              path="/account-locked"
              element={<AccountLocked />}
            />

          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
