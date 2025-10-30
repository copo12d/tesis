import api from '../../../api/api';

export const DashboardAPI = {
  getWeekly: () => api.get('/dashboard/weekly'),
  getCards: () => api.get('/dashboard/cards'),
  getActiveContainerSummary: () => api.get('/container/public/active-summary'),
  ContainerTypeSummary: () => api.get('/container/public/full-summary'),
};