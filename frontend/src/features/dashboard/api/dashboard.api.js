import api from '../../../api/api';

export const DashboardAPI = {
  getActiveContainerSummary: () => api.get('/container/public/active-summary'),
  ContainerTypeSummary: () => api.get('/container/public/full-summary'),
  ProcessedBatchSummary: () => api.get('/batch/public/processed-summary'),
  getDailyContainerSummary: () => api.get('/batch-reg/public/daily-summary'),
  getWeeklyContainerSummary: () => api.get('/batch-reg/public/weekly-summary'),
};