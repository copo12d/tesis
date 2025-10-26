import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './context/AuthProvider';
import { Suspense, lazy } from 'react';
import { PrivateAppFallback, PageContentSkeleton } from './components/fallbacks/PrivateFallbacks';

import  SettingsPage  from './features/settings/pages/SettingsPage';
import { Login } from './features/auth/pages/Login';
// IMPORTS DIRECTOS (antes eran lazy)
import { Register } from './features/auth/pages/Register';
import ForgotPassword from './features/auth/pages/ForgotPassword';

import PublicRoute from './routes/PublicRoute';
import PrivateRoute from './routes/PrivateRoute';
import MobileRoute from './routes/MobileRoute';

// Lazy: resto de páginas para dividir el bundle
const AccountLocked = lazy(() => import('./features/auth/pages/AccountLocked'));

const MainLayout = lazy(() => import('./layouts/MainLayout'));

const Dashboard = lazy(() => import('./features/dashboard/pages/Dashboard').then(m => ({ default: m.Dashboard })));
const UserCreate = lazy(() => import('./features/usersManager/pages/UserCreate').then(m => ({ default: m.UserCreate })));
const UsersList = lazy(() => import('./features/usersManager/pages/UsersList').then(m => ({ default: m.UsersList })));
const UserEdit = lazy(() => import('./features/usersManager/pages/UserEdit').then(m => ({ default: m.UserEdit })));
const UserProfileEdit = lazy(() => import('./features/usersManager/pages/UserProfileEdit').then(m => ({ default: m.UserProfileEdit })));
const ReportsPage = lazy(() => import('./features/Reports/pages/ReportsPage').then(m => ({ default: m.ReportsPage })));

const ContainerList = lazy(() => import('./features/container/pages/ContainerList').then(m => ({ default: m.ContainerList })));
const ContainerCreate = lazy(() => import('./features/container/pages/ContainerCreate').then(m => ({ default: m.ContainerCreate })));

const ContainerTypeList = lazy(() => import('./features/containerType/pages/ContainerTypeList').then(m => ({ default: m.ContainerTypeList })));
const ContainerTypeCreate = lazy(() => import('./features/containerType/pages/ContainerTypeCreate').then(m => ({ default: m.ContainerTypeCreate })));
const ContainerTypeEdit = lazy(() => import('./features/containerType/pages/ContainerTypeEdit').then(m => ({ default: m.ContainerTypeEdit })));

const BatchListPage = lazy(() => import('./features/batch/pages/BatchListPage').then(m => ({ default: m.BatchListPage })));
const BatchCreate = lazy(() => import('./features/batch/pages/BatchCreate').then(m => ({ default: m.BatchCreate })));
const BatchDetailPage = lazy(() => import('./features/batch/pages/BatchDetailPage').then(m => ({ default: m.BatchDetailPage })));

const MobileReportPage = lazy(() => import('./features/mobile/pages/MobileReportPage'));
const MobileCollectorLogin = lazy(() => import('./features/mobile/pages/MobileCollectorLogin'));
const MobileCollectorPage = lazy(() => import('./features/mobile/pages/MobileCollectorPage'));
const ThanksPage = lazy(() => import('./features/mobile/pages/ThanksPage'));

function App() {
  return (
    <AuthProvider>
      <Router>
        <Toaster position="top-right" />
        <Routes>
          {/* Públicas */}
          <Route path="/login" element={<PublicRoute><Login /></PublicRoute>} />
          <Route path="/register" element={<PublicRoute><Register /></PublicRoute>} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/account-locked" element={<Suspense fallback={null}><AccountLocked /></Suspense>} />

          {/* Árbol privado con fallback agradable y sidebar skeleton */}
          <Route
            path="/"
            element={
              <PrivateRoute>
                <Suspense fallback={<PrivateAppFallback />}>
                  <MainLayout />
                </Suspense>
              </PrivateRoute>
            }
          >
            {/* Página inicial (Dashboard) con skeleton de contenido */}
            <Route
              index
              element={
                <Suspense fallback={<PageContentSkeleton />}>
                  <Dashboard />
                </Suspense>
              }
            />

            {/* Resto (puedes mantener fallbacks nulos o usar PageContentSkeleton si quieres) */}
            <Route path="users/new" element={<Suspense fallback={<PageContentSkeleton />}><UserCreate /></Suspense>} />
            <Route path="users/all" element={<Suspense fallback={<PageContentSkeleton />}><UsersList /></Suspense>} />
            <Route path="users/edit/:id" element={<Suspense fallback={<PageContentSkeleton />}><UserEdit /></Suspense>} />
            <Route path="container-type/new" element={<Suspense fallback={<PageContentSkeleton />}><ContainerTypeCreate /></Suspense>} />
            <Route path="container-type/edit/:id" element={<Suspense fallback={<PageContentSkeleton />}><ContainerTypeEdit /></Suspense>} />
            <Route path="container-type/list" element={<Suspense fallback={<PageContentSkeleton />}><ContainerTypeList /></Suspense>} />
            <Route path="profile" element={<Suspense fallback={<PageContentSkeleton />}><UserProfileEdit /></Suspense>} />
            <Route path="reports" element={<Suspense fallback={<PageContentSkeleton />}><ReportsPage /></Suspense>} />
            <Route path="container/list" element={<Suspense fallback={<PageContentSkeleton />}><ContainerList /></Suspense>} />
            <Route path="container/new" element={<Suspense fallback={<PageContentSkeleton />}><ContainerCreate /></Suspense>} />
            <Route path="batch/list" element={<Suspense fallback={<PageContentSkeleton />}><BatchListPage /></Suspense>} />
            <Route path="batch/create" element={<Suspense fallback={<PageContentSkeleton />}><BatchCreate /></Suspense>} />
            <Route path="batch/details/:id" element={<Suspense fallback={<PageContentSkeleton />}><BatchDetailPage /></Suspense>} />
            <Route path="/settings" element={<Suspense fallback={<PageContentSkeleton />}><SettingsPage /></Suspense>} />
          </Route>

          {/* Móviles (puedes dejar fallback={null}) */}
          <Route
            path="/mobile/containers/collect/"
            element={
              <PrivateRoute>
                <MobileRoute>
                  <Suspense fallback={null}>
                    <MobileCollectorPage />
                  </Suspense>
                </MobileRoute>
              </PrivateRoute>
            }
          />
          <Route
            path="/mobile/containers/:id"
            element={
              <PublicRoute>
                <MobileRoute>
                  <Suspense fallback={null}>
                    <MobileReportPage />
                  </Suspense>
                </MobileRoute>
              </PublicRoute>
            }
          />
          <Route
            path="/mobile/login"
            element={
              <PublicRoute>
                <MobileRoute>
                  <Suspense fallback={null}>
                    <MobileCollectorLogin />
                  </Suspense>
                </MobileRoute>
              </PublicRoute>
            }
          />
          <Route path="/mobile/thanks" element={<MobileRoute><Suspense fallback={null}><ThanksPage /></Suspense></MobileRoute>} />

          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
