import api from '../../../api/api';

export const DashboardAPI = {
  getWeekly: () => api.get('/dashboard/weekly'),
  getCards: () => api.get('/dashboard/cards'),
  getActiveContainerSummary: () => api.get('/container/public/active-summary'),
  ContainerTypeSummary: () => api.get('/container/public/full-summary'),
  ProcessedBatchSummary: () => api.get('/batch/public/processed-summary'),
  getDailyContainerSummary: () => api.get('/batch-reg/public/daily-summary'),
  getWeeklyContainerSummary: () => api.get('/batch-reg/public/weekly-summary'),
};