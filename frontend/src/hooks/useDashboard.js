import { useState, useEffect } from 'react';
import api from '../api/api'; 

export const useDashboard = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [dashboardData, setDashboardData] = useState({
    weeklyData: [
      { day: "Sat", value: 100 },
      { day: "Sun", value: 10 },
      { day: "Mon", value: 10 },
      { day: "Tue", value: 10 },
      { day: "Wed", value: 10 },
      { day: "Thu", value: 10 },
      { day: "Fri", value: 10 }
    ],
    cardsData: [
      { id: 1, value: 10 },
      { id: 2, value: 10 },
      { id: 3, value: 10 }
    ]
  });

  const fetchDashboardData = async () => {
    setLoading(true);
    setError('');

    try {
      // Ejecutar en paralelo para mayor rendimiento
      const [weeklyRes, cardsRes] = await Promise.all([
        api.get("/dashboard/weekly"),
        api.get("/dashboard/cards")
      ]);

      setDashboardData({
        weeklyData: weeklyRes.data,
        cardsData: cardsRes.data
      });

      return { success: true };
    } catch (err) {
      const errorMessage = err.response?.data?.error || 'Error al cargar datos del dashboard';
      setError(errorMessage);
      return { success: false, error: errorMessage };
    } finally {
      setLoading(false);
    }
  };

   useEffect(() => {
    fetchDashboardData();
  }, []);

  return {
    dashboardData,
    loading,
    error,
    refetch: fetchDashboardData
  };
}