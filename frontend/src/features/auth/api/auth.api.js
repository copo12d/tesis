import api from '../../../api/api';

export const AuthAPI = {
  register: (fullName, userName, password, email) =>
    api.post('/users/public/register', { fullName, userName, password, email }),
  login: (userName, password) =>
    api.post('/auth/login', { userName, password }),
  passwordRecoveryRequest: ({ userName, email }) =>
    api.post('/email/public/password-recovery-request', { userName, email }),
  accountRecoveryRequest: (data) =>
    api.post('/email/public/account-recovery-request', data),

  // Cambiado a PUT
  passwordRecoverySetPassword: (id, token, data) =>
    api.put(`/email/public/password-recovery/${id}/${token}`, data),

  // Cambiado a PUT
  accountRecoverySetPassword: (id, token, data) =>
    api.put(`/email/public/account-recovery/${id}/${token}`, data),
  refreshSession: (refreshToken) =>
    api.post('/auth/refresh', { refreshToken }),
};