import api from '../../../api/api';

export const DashboardAPI = {
  getWeekly: () => api.get('/dashboard/weekly'),
  getCards: () => api.get('/dashboard/cards'),
};