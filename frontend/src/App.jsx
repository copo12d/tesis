import { useState } from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Login } from './components/Login';
import { Register } from './components/Register';
import { Home } from './components/Home';
import { Toaster } from 'react-hot-toast';
import './App.css'

function App() {
  const [token, setToken] = useState(null);

  return (
    <Router>
      <Toaster position="top-right" />
      <Routes>
        <Route path="/" element={
          token ? <Home onLogout={() => setToken(null)} /> : <Navigate to="/login" />
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
  
  )
}

export default App
